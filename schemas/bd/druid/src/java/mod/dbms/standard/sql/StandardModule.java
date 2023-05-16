//==============================================================================
//===
//===   StandardModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.standard.sql;

import javax.swing.JComponent;

import mod.dbms.standard.sql.panels.database.DatabasePanel;
import mod.dbms.standard.sql.panels.table.TablePanel;

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

public class StandardModule extends AbstractSqlGenModule
{

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "stdSql";         }
	public String getVersion()   { return "2.0";            }
	public String getAuthor()    { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script that creates the database. "   +
					"Supports multiple foreign keys, sequences, triggers and table rules.";
	}

	//---------------------------------------------------------------------------

	public String     getFormat()                { return "Standard Sql"; }
	public boolean    isDirectoryBased()         { return false;          }
	public boolean    hasLargePanel()            { return true;           }

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
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, StandardModule.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, StandardModule.this),
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

	protected String genTablePost(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		String postSql = ts.getPostSql().trim();

		return postSql.equals("") ? "" : LF + postSql;
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());

		// Note: there are no provisions for Indexes in the SQL standard.
		return SqlUtil.generateIndex(node, indexAS, fields, unique, cnt, idxAL,true);
	}
}

//==============================================================================
