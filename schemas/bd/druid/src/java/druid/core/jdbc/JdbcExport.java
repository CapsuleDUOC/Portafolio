//==============================================================================
//===
//===   JdbcExport
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dlib.gui.ProgressDialog;

import druid.core.DataLib;
import druid.core.IntegrityChecker;
import druid.data.AbstractNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.interfaces.SqlGenModule;

//==============================================================================

public class JdbcExport
{
	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	/** This method is intended to be executed into a separate thread */

	public static void rebuildDatabase(ProgressDialog progrDial, SqlGenModule mod, AbstractNode node)
												throws Exception
	{
		JdbcConnection jdbcConn = node.getDatabase().getJdbcConnection();

		//------------------------------------------------------------------------
		//--- check database integrity

		IntegrityChecker ic = new IntegrityChecker();

		if (ic.check(node.getDatabase()) != IntegrityChecker.OK)
			throw new Exception("Integrity check failed. Open the data\n" +
						"generation dialog for more information.");

		//------------------------------------------------------------------------
		//--- retrieve objects and check for circular references

		Vector vSrcTables = node.getObjects(TableNode.class);
		Vector vTables    = DataLib.getOrderedTables(vSrcTables);

		if (vSrcTables.size() != 0)
		{
			StringBuffer res = 	new StringBuffer("Circular references found.\n" +
								"The following tables could not be resolved:\n");

			for(int i=0; i<vSrcTables.size(); i++)
			{
				TableNode tnode = (TableNode) vSrcTables.elementAt(i);

				res.append(" - ").append(tnode.attrSet.getString("name")).append("\n");
			}

			throw new Exception(res.toString());
		}

		Vector vViews  = node.getObjects(ViewNode.class);
		Vector vProcs  = node.getObjects(ProcedureNode.class);
		Vector vFuncs  = node.getObjects(FunctionNode.class);
		Vector vSequen = node.getObjects(SequenceNode.class);

		int numElem = 2*(vTables.size() + vViews.size() + vProcs.size() + vFuncs.size() + vSequen.size());

		progrDial.reset(numElem);

		//------------------------------------------------------------------------
		//--- first, remove views to avoid integrity constraints errors

		dropEntityList(jdbcConn, vViews,  progrDial);

		//------------------------------------------------------------------------
		//--- then tables (must be dropped in reverse order)

		for(int i=vTables.size()-1; i>=0; i--)
		{
			AbstractNode tnode = (AbstractNode)vTables.elementAt(i);

			progrDial.advance("Dropping : " + tnode.attrSet.getString("name"));

			dropEntity(jdbcConn, tnode);
		}

		//------------------------------------------------------------------------
		//--- to end with procs, funcs, and sequences

		dropEntityList(jdbcConn, vProcs,  progrDial);
		dropEntityList(jdbcConn, vFuncs,  progrDial);

		//--- sequences should be altered to maintain their value. For now, don't care

		dropEntityList(jdbcConn, vSequen, progrDial);

		//------------------------------------------------------------------------
		//--- now recreate entities

		createEntityList(jdbcConn, mod, vSequen, progrDial);
		createEntityList(jdbcConn, mod, vFuncs,  progrDial);
		createEntityList(jdbcConn, mod, vProcs,  progrDial);

		//------------------------------------------------------------------------
		//--- then recreate tables

		for(int i=0; i<vTables.size(); i++)
		{
			TableNode tnode = (TableNode) vTables.elementAt(i);

			progrDial.advance("Creating : " + tnode.getText());

			createEntity(jdbcConn, mod, tnode);
		}

		//------------------------------------------------------------------------
		//--- and then views

		createEntityList(jdbcConn, mod, vViews,  progrDial);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Rebuild a sql entity (table, view, procedure, function, sequence)
	//---
	//---------------------------------------------------------------------------

	public static void rebuildEntity(JdbcConnection jdbcConn, SqlGenModule mod, AbstractNode node) throws Exception
	{
		dropEntity(jdbcConn, node);
		createEntity(jdbcConn, mod, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static void createEntity(JdbcConnection jdbcConn, SqlGenModule mod, AbstractNode node) throws Exception
	{
		try
		{
			if (node instanceof TableNode)
				createTable(jdbcConn, (TableNode) node, mod);

			else if (node instanceof Trigger || node instanceof ProcedureNode || node instanceof FunctionNode)
			{
				String query = mod.generate(node);

				jdbcConn.execute(query +";", null);
			}
			else
			{
				String query = mod.generate(node);

				jdbcConn.execute(query, null);
			}
		}
		catch(Exception e)
		{
			String message = 	"Raised exception during entity rebuild.\n\n"+
									"- entity : " +node.attrSet.getString("name") +"\n" +
									"- exception : " +e.getMessage();

			throw new Exception(message);
		}
	}

	//---------------------------------------------------------------------------

	private static void createTable(JdbcConnection jdbcConn, TableNode node, SqlGenModule mod)
												throws SQLException, IOException
	{
		//------------------------------------------------------------------------
		//--- step 1 : create table

		String createQuery = mod.generate(node);

		jdbcConn.execute(createQuery, null);

		//------------------------------------------------------------------------
		//--- step 2 : create indexes

		List indexes = mod.generateIndexes(node);

		for(int i=0; i<indexes.size(); i++)
		{
			String query = (String) indexes.get(i);

			jdbcConn.execute(query, null);
		}

		//------------------------------------------------------------------------
		//--- step 3 : create triggers

		for(int i=0; i<node.triggers.getChildCount(); i++)
		{
			Trigger trigger = (Trigger) node.triggers.getChild(i);

			jdbcConn.execute(mod.generate(trigger) +";", null);
		}

		//------------------------------------------------------------------------
		//--- step 4 : generate comments too

		List list = mod.generateComments(node);

		for(int i=0; i<list.size(); i++)
			jdbcConn.execute(list.get(i).toString(), null);

		//------------------------------------------------------------------------
		//--- step 5 : generate sqlCommands

		String cmds = node.attrSet.getString("sqlCommands");

		StringTokenizer st = new StringTokenizer(cmds, "\n");

		StringBuffer query = new StringBuffer(cmds.length());

		while (st.hasMoreTokens())
		{
			String line = st.nextToken();

			if (line.length() > 0)
			{
				if (query.length() > 0)	query.append("\n");
                query.append(line);

				if (line.trim().endsWith(";"))
				{
					//--- we must skip the last ";" because some dbms don't like it
					//--- (like oracle)

					jdbcConn.execute(query.substring(0, query.length()-1), null);
					query.setLength(0);
				}
			}
		}

		if (query.length() > 0)
			jdbcConn.execute(query.toString(), null);
	}

	//---------------------------------------------------------------------------

	private static void createEntityList(JdbcConnection jdbcConn, SqlGenModule mod, Vector vEntities, ProgressDialog progrDial)
											throws Exception
	{
		for(int i=0; i<vEntities.size(); i++)
		{
			AbstractNode node = (AbstractNode)vEntities.elementAt(i);

			progrDial.advance("Creating : " + node.attrSet.getString("name"));

			createEntity(jdbcConn, mod, node);
		}
	}

	//---------------------------------------------------------------------------

	private static void dropEntityList(JdbcConnection jdbcConn, Vector vEntities, ProgressDialog progrDial)
	{
		for(int i=0; i<vEntities.size(); i++)
		{
			AbstractNode node = (AbstractNode)vEntities.elementAt(i);

			progrDial.advance("Dropping : " + node.attrSet.getString("name"));

			dropEntity(jdbcConn, node);
		}
	}

	//---------------------------------------------------------------------------

	private static void dropEntity(JdbcConnection jdbcConn, AbstractNode node)
	{
		try
		{
			jdbcConn.getSqlAdapter().dropEntity(node);
		}
		catch(Exception e)
		{
			//--- maybe the entity is not created yet or it is referenced
		}
	}
}

//==============================================================================
