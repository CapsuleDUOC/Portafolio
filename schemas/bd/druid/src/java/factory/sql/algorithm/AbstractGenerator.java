//==============================================================================
//===
//===   AbstractGenerator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.algorithm;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dlib.tools.FullTokenizer;
import org.dlib.tools.Util;

import druid.core.config.Config;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;
import factory.sql.BasicDatabaseSettings;

//==============================================================================

public class AbstractGenerator
{
	protected String LF   = Config.os.lineSep;
	protected String TERM = Config.general.sqlTerminator;

	protected Logger       logger;
	protected SqlGenModule sqlMod;
	protected DatabaseNode dbNode;

	protected BasicDatabaseSettings sett;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AbstractGenerator(Logger l, SqlGenModule mod, DatabaseNode node)
	{
		logger = l;
		sqlMod = mod;
		dbNode = node;

		sett = new BasicDatabaseSettings(dbNode.modsConfig, sqlMod);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String generate() { return null; }

	//---------------------------------------------------------------------------
	//--- used by SqlPreview dialog

	public String generate(AbstractNode node)
	{
		StringBuffer sb = new StringBuffer();

		String tmp = sqlMod.generate(node);
		sb.append(tmp);

		int len = tmp.trim().length();

		if (len > 0 && tmp.lastIndexOf(';') != (len-1) && tmp.lastIndexOf('/') != (len-1))
			sb.append(TERM);

		sb.append(LF);

		//--- build node's extra stuff
		
		String extra = sqlMod.generateExtra(node);
		
		if (extra != null)
			sb.append(LF + extra + TERM + LF);

		//--- in case of a table we need to generate triggers and indexes

		if (node instanceof TableNode && sett.isGenInlineFKs())
			buildTableExtra(sb, (TableNode) node);

		else if (node instanceof ProcedureNode || node instanceof FunctionNode)
		{
			len = sb.toString().trim().length();

			if (len > 0 && sb.lastIndexOf(sqlMod.getCodeSeparator()) != len-1)
				sb.append(sqlMod.getCodeSeparator());
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Service methods
	//---
	//---------------------------------------------------------------------------

	protected String generate(List objects, Hashtable unresolved)
	{
		StringBuffer sb = new StringBuffer();

		//---------------------------------------------------------------------
		//--- DROP statements generation (in reverse order)

		if (sett.isGenDropStmts())
		{
			for(int i=objects.size()-1; i>=0; i--)
			{
				AbstractNode node = (AbstractNode) objects.get(i);

				boolean condition = (node instanceof TableNode     && sett.isGenTables())     ||
									(node instanceof ViewNode      && sett.isGenViews())      ||
									(node instanceof ProcedureNode && sett.isGenProcedures()) ||
									(node instanceof FunctionNode  && sett.isGenFunctions())  ||
									(node instanceof SequenceNode  && sett.isGenSequences());
				
				if (condition)
					sb.append(sqlMod.generateDrop(node) + TERM + LF);
			}

			sb.append(LF + getSeparator());
		}

		//---------------------------------------------------------------------
		//--- generate data. we must resolve circular references
		//--- the following code is pretty ugly but at this point
		//--- we have to focus on druid 4...

		if (sett.isGenInlineFKs())
		{
			Vector fkAlters = new Vector();

			for(int i=0; i<objects.size(); i++)
			{
				AbstractNode node = (AbstractNode) objects.get(i);
				String       sql  = generate(node);

				if (unresolved.containsValue(node))
				{
					Vector v = new Vector();

					FullTokenizer ft = new FullTokenizer(sql, LF);

					while(ft.hasMoreTokens())
					{
						String line = ft.nextToken();

						if (line.indexOf("foreign key") == -1)
							v.add(line);
						else
						{
							StringTokenizer st = new StringTokenizer(line.trim(), " ");

							String constrName = null;

							if (st.nextToken().equalsIgnoreCase("constraint"))
							{
								constrName = st.nextToken();

								//--- skip "foreign" word
								st.nextToken();
							}

							//--- locField is in the form "(field)"
							String locField = st.nextToken().substring(3);

							//--- skip "references" word
							st.nextToken();

							//--- this is like "table(field)"
							String reference = st.nextToken();
							String table     = reference.substring(0, reference.indexOf("("));

							if (!unresolved.containsKey(table))
								v.add(line);
							else
							{
								StringBuffer alter = new StringBuffer();

								alter.append("ALTER TABLE ");
								alter.append(Util.pad(node.attrSet.getString("name"), 15));
								alter.append(" ADD ");

								if (constrName != null)
								{
									alter.append("CONSTRAINT ");
									alter.append(constrName);
									alter.append(" ");
								}

								alter.append("FOREIGN KEY");
								alter.append(locField);
								alter.append(" REFERENCES ");
								alter.append(reference);

								while(st.hasMoreTokens())
									alter.append(" "+ st.nextToken());

								String stmt = alter.toString();

								if (stmt.endsWith(","))
									stmt = stmt.substring(0, stmt.length() -1);

								fkAlters.add(stmt);
							}
						}
					}

					//--- now, the rebuilt table's sql statement can contain wrong ","
					//--- we have to remove them

					StringBuffer ss = new StringBuffer();

					int j=v.size()-1;

					while(!v.get(j--).toString().trim().startsWith(")"))
						;

					if (v.get(j).toString().trim().equals(""))
					{
						v.remove(j);
						j--;
					}

					String lastLine = (String) v.get(j);

					if (lastLine.endsWith(","))
						v.set(j, lastLine.substring(0, lastLine.length() -1));

					for(j=0; j<v.size(); j++)
						ss.append(v.get(j) + LF);

					sql = ss.toString();
				}

				if (sql.trim().length() > 0)
				{
					sb.append(sql);
					sb.append(LF + getSeparator());
				}
			}

			//---------------------------------------------------------------------
			//--- add ALTER statements (if the case)

			for(int i=0; i<fkAlters.size(); i++)
				sb.append(fkAlters.get(i) + ";" + LF);
		}
		else
		{
			for(int i=0; i<objects.size(); i++)
			{
				AbstractNode node = (AbstractNode) objects.get(i);
				String tmp = generate(node);

				if (tmp.trim().length() > 0)
				{
					sb.append(tmp);
					sb.append(LF + getSeparator());
				}
			}

			if (sett.isGenTables())
			{
				// Generate references
				for (int i=0; i < objects.size(); i++)
				{
					AbstractNode node = (AbstractNode) objects.get(i);

					if (node instanceof TableNode)
					{
						String tmp = sqlMod.generateReferences((TableNode)node);

						if (tmp.trim().length() > 0)
							sb.append(tmp).append(LF).append("/").append(LF).append(LF);
					}
				}

				if (sett.isGenTriggers())
				{
					// Generate Triggers
					for (int i=0; i < objects.size(); i++)
					{
						AbstractNode node = (AbstractNode) objects.get(i);

						if (node instanceof TableNode)
							buildTableExtra(sb, (TableNode) node);
					}
				}
			}
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected void buildTableExtra(StringBuffer sb, TableNode table)
	{
		//------------------------------------------------------------------------
		//--- indexes generation

		List indexes = sqlMod.generateIndexes(table);

		if (indexes.size() != 0)
		{
			sb.append(LF);

			for(int j=0; j<indexes.size(); j++)
				sb.append(indexes.get(j) + TERM + LF);
		}

		//------------------------------------------------------------------------
		//--- triggers generation

		for(int j=0; j<table.triggers.getChildCount(); j++)
		{
			Trigger trigger = (Trigger) table.triggers.getChild(j);

			String tmp = sqlMod.generate(trigger);

			if (tmp.trim().length() > 0)
				sb.append(LF + getSmallSeparator() + tmp + TERM + LF + sqlMod.getCodeSeparator());
		}

		//------------------------------------------------------------------------
		//--- field comments generation

		List fieldComments = sqlMod.generateComments(table);

		if (fieldComments.size() != 0)
		{
			sb.append(LF + getSmallSeparator());

			for(int j=0; j<fieldComments.size(); j++)
				sb.append(fieldComments.get(j) +TERM + LF);
		}

		//------------------------------------------------------------------------
		//--- sql commands generation

		String sqlCmd = table.attrSet.getString("sqlCommands").trim();

		if (!sqlCmd.equals(""))
		{
			sb.append(LF + getSmallSeparator());
			sb.append(sqlCmd + LF);
		}
	}

	//---------------------------------------------------------------------------

	protected String getSeparator()
	{
		return sqlMod.comment(Util.replicate("=", 70)) + LF + LF;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String getSmallSeparator()
	{
		return sqlMod.comment(Util.replicate("-", 70)) + LF + LF;
	}
}

//==============================================================================
