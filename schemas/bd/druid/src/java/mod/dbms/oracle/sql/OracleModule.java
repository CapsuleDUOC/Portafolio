//==============================================================================
//===
//===   OracleModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql;

import javax.swing.JComponent;

import mod.dbms.oracle.sql.panels.database.DatabasePanel;
import mod.dbms.oracle.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;
import org.dlib.tools.Util;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.FieldNode;
import druid.data.FieldAttribs;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.interfaces.BasicModule;
import druid.interfaces.ModuleOptions;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.SqlUtil;

//==============================================================================

public class OracleModule extends AbstractSqlGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "oraSql";         }
	public String getVersion()   { return "1.1";            }
	public String getAuthor()    { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script for Oracle. All features derive from "   +
				"the standard sql module. Added limited support for tablespaces and "+
				"indexes options.";
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Oracle Sql"; }
	public boolean isDirectoryBased() { return false;        }
	public boolean hasLargePanel()    { return true;         }

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == BasicModule.DATABASE) return databaseOptions;
		if (env == BasicModule.TABLE)	 return tableOptions;

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
			dbPanel.refresh(new DatabaseSettings(node.modsConfig, OracleModule.this));
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
			tablePanel.refresh(new DatabaseSettings(node.getDatabase().modsConfig, OracleModule.this),
							   new TableSettings  (node.modsConfig,                OracleModule.this),
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

	public String comment(String message)
	{
		return "REM " + message;
	}

	//---------------------------------------------------------------------------

	protected String genTable(TableNode node)
	{
		String s = super.genTable(node);

		return Util.replaceStr(s, LF+LF, LF);
	}

	//---------------------------------------------------------------------------

	protected String genTableAttrib(TableNode node, String sqlName, TVector vFields, int cnt)
	{
		StringBuffer sb = new StringBuffer(super.genTableAttrib(node, sqlName, vFields, cnt));

		if (sqlName.toLowerCase().equals("primary key"))
		{
			DatabaseSettings gs = new DatabaseSettings(node.getDatabase().modsConfig, this);
			TableSettings    ts = new TableSettings   (node.modsConfig,               this);

			int pktspace = ts.getPKTablespace();

			if (pktspace != 0)
			{
				String name = gs.getTablespaceName(pktspace);

				if (name == null)
					name = "<DELETED>";

				sb.append(" USING INDEX TABLESPACE " + name);
			}
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTablePost(TableNode node)
	{
		DatabaseSettings gs = new DatabaseSettings(node.getDatabase().modsConfig, this);
		TableSettings    ts = new TableSettings   (node.modsConfig,               this);

		StringBuffer sb = new StringBuffer();

		int tspace = ts.getTablespace();

		if (tspace != 0)
		{
			String name = gs.getTablespaceName(tspace);

			if (name == null)
				name = "<DELETED>";

			sb.append(LF + "  TABLESPACE " + name);
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		DatabaseSettings ds = new DatabaseSettings(node.getDatabase().modsConfig, this);
		TableSettings    ts = new TableSettings(node.modsConfig, this);

		int    id    = indexAS.getInt("id");
		String templ = indexAS.getString("sqlName");
		String table = node.attrSet.getString("name");

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());
		AttribSet  optAS = idxAL.find(id);

		if (optAS == null)
			throw new DruidException(DruidException.INC_STR, "Index not found in AttribList",
											 "id:"+id+", AL:"+idxAL);

		String  name   = optAS.getString("name");
		int     tabspc = optAS.getInt   ("ts");
		boolean bitmap = optAS.getBool  ("bitmap");
		boolean stats  = optAS.getBool  ("stats");
		boolean noSort = optAS.getBool  ("noSort");

		if (name.equals(""))
			 name = templ;

		//--- build index

		StringBuffer sb = new StringBuffer();

		sb.append("CREATE ");

		if (bitmap)	sb.append("BITMAP ");
			else	sb.append(unique ? "UNIQUE " : "");

		sb.append("INDEX ");

		sb.append(SqlUtil.expandTemplate(name, table, fields, cnt));

		sb.append(" ON " + table + "(");
		sb.append(fields.toString() + ")");
		
		if (indexAS.getString("scope").equals(FieldAttribs.SCOPE_FTINDEX)) {
			sb.append(" INDEXTYPE IS CTXSYS.CTXCAT ");
		}
		if (stats)  sb.append(" COMPUTE STATISTICS");
		if (noSort) sb.append(" NOSORT");

		if (tabspc != 0)
			sb.append(" TABLESPACE " + ds.getTablespaceName(tabspc));

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTrigger(Trigger trigger)
	{
		String result = super.genTrigger(trigger);
		int    index  = result.indexOf("TRIGGER");

		return "CREATE OR REPLACE "+result.substring(index);
	}

	//---------------------------------------------------------------------------
	
	public String generateExtra(AbstractNode node)
	{
		if (node instanceof TableNode)
		{
			TableNode table = (TableNode) node;

			BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);
			TableSettings         ts   = new TableSettings(node.modsConfig, this);

			if (sett.isGenSequences() && ts.isSeqGeneration())
				for (int i=0; i<table.getChildCount(); i++)
				{
					FieldNode field = (FieldNode) table.getChild(i);
					
					if (field.isPkey())
					{
						String temp = ts.getSeqTemplate();
						String name = SqlUtil.expandTemplate(temp, table.getName(), new TVector(), 1);

						return "CREATE SEQUENCE "+ name;
					}
				}
		}
		
		return null;
	}

	//---------------------------------------------------------------------------

	protected String genDropTable(TableNode node)
	{
		return "DROP TABLE "+ node.getQualifiedName() +" CASCADE CONSTRAINTS PURGE";
	}

	//---------------------------------------------------------------------------
	//---
	//--- Code separator when generating the sql script
	//---
	//---------------------------------------------------------------------------

	public String getCodeSeparator()
	{
		return "/"+ LF;
	}
}

//==============================================================================
