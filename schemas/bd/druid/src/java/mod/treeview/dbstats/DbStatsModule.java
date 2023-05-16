//==============================================================================
//===
//===   DbStatsModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.dbstats;

import java.awt.Frame;
import java.util.Enumeration;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.er.ErView;
import druid.dialogs.tabledialog.TableDialog;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TreeNodeModule;

//==============================================================================

public class DbStatsModule implements TreeNodeModule
{
	public String getId()       { return "dbStats";        }
	public String getAuthor()   { return "Andrea Carboni"; }
	public String getVersion()  { return "1.2";            }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Performs some stats on the db and shows results in a dialog\n"+
				 "Stats include number of tables, fields, views, etc...";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	//---------------------------------------------------------------------------

	public String getPopupText()
	{
		return "Statistics...";
	}

	//---------------------------------------------------------------------------

	public boolean isNodeAccepted(TreeViewNode node)
	{
		return (node instanceof DatabaseNode) || (node instanceof FolderNode);
	}

	//---------------------------------------------------------------------------

	public boolean isNodeEnabled(TreeViewNode node) { return true; }

	//---------------------------------------------------------------------------

	public void nodeSelected(Frame f, TreeViewNode node)
	{
		DbStats dbs = getDbStats((AbstractNode) node);

		TableDialog td = new TableDialog(f, "Database Statistics");

		td.addColumn("Stat performed", 200);
		td.addColumn("Result",          40);

		//------------------------------------------------------------------------
		//--- database stuff

		if (node instanceof DatabaseNode)
		{
			td.append("Revisions",           dbs.revs);
			td.append("Field attributes",    dbs.fattribs);
			td.append("Er views",            dbs.erViews);
			td.append("Er entities",         dbs.erEntities);
			td.append("",                    "");
			td.append("DataTypes (total)",   dbs.datatypes);
			td.append("Constant datatypes",  dbs.constDt);
			td.append("Constant aliases",    dbs.constAlias);
			td.append("Variable datatypes",  dbs.varDt);
			td.append("Variable aliases",    dbs.varAlias);
			td.append("",                    "");
		}

		//------------------------------------------------------------------------

		td.append("Folders",             dbs.folders);
		td.append("Tables",              dbs.tables);
		td.append("Fields",              dbs.fields);
		td.append("Views",               dbs.views);
		td.append("Procedures",          dbs.procedures);
		td.append("Functions",           dbs.functions);
		td.append("Sequences",           dbs.sequences);
		td.append("Notes",               dbs.notes);
		td.append("",                    "");
		td.append("Table vars",          dbs.tableVars);
		td.append("Triggers",            dbs.triggers);
		td.append("Table rules",         dbs.rules);
		td.append("",                    "");

		//------------------------------------------------------------------------
		//--- some calculated stats

		if (dbs.tables != 0)
		{
			float avg = (dbs.fields * 100f / dbs.tables) /100f;

			td.append("Fields in a table (avg)", avg);
		}
		else
			td.append("Fields in a table (avg)",    0);

		td.showDialog();
	}

	//---------------------------------------------------------------------------

	public int getEnvironment()
	{
		return PROJECT;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private DbStats getDbStats(AbstractNode statNode)
	{
		DatabaseNode dbNode = statNode.getDatabase();

		DbStats dbs = new DbStats();

		//------------------------------------------------------------------------
		//--- basic info

		dbs.revs     = dbNode.revisions.size();
		dbs.fattribs = dbNode.fieldAttribs.size();

		//------------------------------------------------------------------------
		//--- loop on database/folder elements

		Enumeration e = statNode.preorderEnumeration();

		//--- skip first element

		e.nextElement();

		while(e.hasMoreElements())
		{
			AbstractNode node = (AbstractNode)e.nextElement();

			if (node instanceof FolderNode)    dbs.folders++;
			if (node instanceof FieldNode)     dbs.fields++;
			if (node instanceof ViewNode)      dbs.views++;
			if (node instanceof ProcedureNode) dbs.procedures++;
			if (node instanceof FunctionNode)  dbs.functions++;
			if (node instanceof SequenceNode)  dbs.sequences++;
			if (node instanceof NotesNode)     dbs.notes++;

			if (node instanceof TableNode)
			{
				dbs.tables++;

				TableNode tNode = (TableNode) node;

				dbs.tableVars += tNode.tableVars.size();
				dbs.triggers  += tNode.triggers.getChildCount();
				dbs.rules     += tNode.rules.getChildCount();
			}
		}

		//------------------------------------------------------------------------
		//--- loop on datatypes

		for(e = dbNode.dataTypes.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractType node = (AbstractType)e.nextElement();

			dbs.datatypes++;

			if (node instanceof ConstDataType) dbs.constDt++;
			if (node instanceof ConstAlias)    dbs.constAlias++;
			if (node instanceof VarDataType)   dbs.varDt++;
			if (node instanceof VarAlias)      dbs.varAlias++;
		}

		//--- for datatypes we must subtract 3 which is :
		//--- dt node + const folder node + var folder node

		dbs.datatypes = dbs.datatypes -3;

		//------------------------------------------------------------------------
		//--- loop on er-views

		for(int i=0; i<dbNode.erViews.getChildCount(); i++)
		{
			ErView erView = (ErView) dbNode.erViews.getChild(i);

			dbs.erViews++;
			dbs.erEntities += erView.getChildCount();
		}

		return dbs;
	}
}

//==============================================================================
