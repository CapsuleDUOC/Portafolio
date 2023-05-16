//==============================================================================
//===
//===   McKoiModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mckoi.sql;

import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import mod.dbms.mckoi.sql.panels.database.DatabasePanel;
import mod.dbms.mckoi.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.data.AbstractNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.interfaces.BasicModule;
import druid.interfaces.ModuleOptions;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.SqlUtil;

//==============================================================================

public class McKoiModule extends AbstractSqlGenModule
{

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "mckoiSql";       }
	public String getVersion()   { return "1.0";            }
	public String getAuthor()    { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script for McKoi. All features derive from "   +
					"the standard sql module.";
	}

	//---------------------------------------------------------------------------

	public String     getFormat()                { return "McKoi Sql"; }
	public boolean    isDirectoryBased()         { return false;       }
	public boolean    hasLargePanel()            { return true;        }

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == BasicModule.DATABASE) return databaseOptions;
		if (env == BasicModule.TABLE)		return tableOptions;

		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ModuleOptions handlers
	//---
	//---------------------------------------------------------------------------

	//---------------------------------------------------------------------------
	//--- Env : DATABASE
	//---------------------------------------------------------------------------

	private ModuleOptions databaseOptions = new ModuleOptions()
	{
		private DatabasePanel dbPanel = new DatabasePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, McKoiModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return dbPanel; }
	};

	//---------------------------------------------------------------------------
	//--- Env : TABLE
	//---------------------------------------------------------------------------

	private ModuleOptions tableOptions = new ModuleOptions()
	{
		private TablePanel tablePanel = new TablePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			tablePanel.refresh(new TableSettings(node.modsConfig, McKoiModule.this),
									 node.getDatabase());
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return tablePanel; }
	};

	//---------------------------------------------------------------------------
	//---
	//--- Overridden methods
	//---
	//---------------------------------------------------------------------------

	public List generateComments(TableNode node)
	{
		//--- mckoi doesn't have the 'COMMENT' keyword so we must avoid comments
		//--- generation

		return new Vector();
	}

	//---------------------------------------------------------------------------

	protected String genSequence(SequenceNode node)
	{
		AttribSet as = node.attrSet;

		String  name   = as.getString("name");
		String  incr   = as.getString("increment").trim();
		String  minVal = as.getString("minValue").trim();
		String  maxVal = as.getString("maxValue").trim();
		String  start  = as.getString("start").trim();
		String  cache  = as.getString("cache").trim();

		String s = "CREATE SEQUENCE " + name;

		String w = "";

		if (!incr.equals(""))    w += " INCREMENT " + incr;
		if (!minVal.equals(""))  w += " MINVALUE "  + minVal;
		if (!maxVal.equals(""))  w += " MAXVALUE "  + maxVal;
		if (!start.equals(""))   w += " START "     + start;
		if (!cache.equals(""))   w += " CACHE "     + cache;
		if (as.getBool("cycle")) w += " CYCLE";
//		if (as.getBool("order")) w += " ORDER";

		if (!w.equals(""))
			s += LF + "   " + w;

		return s;
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());

		return SqlUtil.generateIndex(node, indexAS, fields, unique, cnt, idxAL,false);
	}
}

//==============================================================================
