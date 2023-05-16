//==============================================================================
//===
//===   DaffodilModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.daffodil.sql;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.interfaces.ModuleOptions;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.SqlUtil;

import javax.swing.JComponent;

import mod.dbms.daffodil.sql.panels.database.DatabasePanel;
import mod.dbms.daffodil.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;

//==============================================================================

public class DaffodilModule extends AbstractSqlGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "daffSql";        }
	public String getVersion()   { return "1.0";            }
	public String getAuthor()    { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script for Daffodil DB. All features derive from "   +
					"the standard sql module. Supports fulltext indexes.";
	}

	//---------------------------------------------------------------------------

	public String     getFormat()                { return "Daffodil DB"; }
	public boolean    isDirectoryBased()         { return false;         }
	public boolean    hasLargePanel()            { return true;          }

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE)
			return databaseOptions;

		if (env == TABLE)
			return tableOptions;

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
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, DaffodilModule.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, DaffodilModule.this),
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

	protected String genSequence(SequenceNode node)
	{
		AttribSet as = node.attrSet;

		String  name   = as.getString("name");
		String  incr   = as.getString("increment").trim();
		String  minVal = as.getString("minValue").trim();
		String  maxVal = as.getString("maxValue").trim();
		String  start  = as.getString("start").trim();
//		String  cache  = as.getString("cache").trim();

		String s = "CREATE SEQUENCE " + name;

		String w = "";

		if (!incr.equals(""))    w += " INCREMENT " + incr;
		if (!minVal.equals(""))  w += " MINVALUE "  + minVal;
		if (!maxVal.equals(""))  w += " MAXVALUE "  + maxVal;
		if (!start.equals(""))   w += " START "     + start;
//		if (!cache.equals(""))   w += " CACHE "     + cache;
		if (as.getBool("cycle")) w += " CYCLE";
//		if (as.getBool("order")) w += " ORDER";

		if (!w.equals(""))
			s += LF + "   " + w;

		return s;
	}

	//---------------------------------------------------------------------------

	protected String genTablePost(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		StringBuffer sb = new StringBuffer();

		String country = ts.getCountry() .trim();
		String language= ts.getLanguage().trim();

		if (!country.equals(""))
			sb.append(LF + "  COUNTRY " + country);

		if (!language.equals(""))
			sb.append(LF + "  LANGUAGE " + language);

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		int    id    = indexAS.getInt("id");
		String templ = indexAS.getString("sqlName");
		String table = node.attrSet.getString("name");

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());
		AttribSet  optAS = idxAL.find(id);

		if (optAS == null)
			throw new DruidException(DruidException.INC_STR, "Index not found in AttribList",
											 "id:"+id+", AL:"+idxAL);

		String  name     = optAS.getString("name");
		boolean fullText = optAS.getBool("fullText");

		if (name.equals(""))
			 name = templ;

		//--- build index

		StringBuffer sb = new StringBuffer();

		sb.append("CREATE ");
		sb.append(fullText ? "FULLTEXT " : "");
		sb.append("INDEX ");

		sb.append(SqlUtil.expandTemplate(name, table, fields, cnt));

		sb.append(" ON " + table);

		sb.append("("+ fields.toString() +")");

		return sb.toString();
	}
}

//==============================================================================
