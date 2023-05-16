//==============================================================================
//===
//===   HSqlDBModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.hsqldb.sql;

import javax.swing.JComponent;

import mod.dbms.hsqldb.sql.panels.database.DatabasePanel;
import mod.dbms.hsqldb.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.data.AbstractNode;
import druid.data.TableNode;
import druid.interfaces.BasicModule;
import druid.interfaces.ModuleOptions;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.SqlUtil;

//==============================================================================

public class HSqlDBModule extends AbstractSqlGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "hsqldbSql";      }
	public String getVersion()   { return "1.0";            }
	public String getAuthor()    { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script for HSqlDB. All features derive from "   +
					"the standard sql module. Added type to tables (memory, cached...).";
	}

	//---------------------------------------------------------------------------

	public String     getFormat()                { return "HSqlDB Sql"; }
	public boolean    isDirectoryBased()         { return false;        }
	public boolean    hasLargePanel()            { return true;         }

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
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, HSqlDBModule.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, HSqlDBModule.this),
									 node.getDatabase());
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return tablePanel; }
	};

	//---------------------------------------------------------------------------
	//---
	//--- Overwritten methods
	//---
	//---------------------------------------------------------------------------

	protected String genTablePre(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		String type = ts.getType();

		if (type.equals(TableSettings.TYPE_DEFAULT))
			type = "";
		else
			type = type +" ";

		return "CREATE " +type+ "TABLE " + node.attrSet.getString("name");
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
