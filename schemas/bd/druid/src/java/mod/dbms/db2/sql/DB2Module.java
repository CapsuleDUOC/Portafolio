//==============================================================================
//===
//===   StandardModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.db2.sql;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.data.AbstractNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.interfaces.BasicModule;
import druid.interfaces.ModuleOptions;
import druid.util.decoder.TriggerDB2ActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.SqlUtil;
import javax.swing.JComponent;
import mod.dbms.db2.sql.panels.database.DatabasePanel;
import mod.dbms.db2.sql.panels.table.TablePanel;
import org.dlib.tools.TVector;

//==============================================================================

public class DB2Module extends AbstractSqlGenModule
{

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "IBM_DB2";         }
	public String getVersion()   { return "1.0";            }
	public String getAuthor()    { return "Jeff Bendixsen"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script DB2 UDB. Tested with UDB v7.2 FP13 on Windows. " +
		         "Should work with 8.x as well."   +
  					"Based off standard generated with changes to TRIGGER and SEQUENCE generation";
	}

	//---------------------------------------------------------------------------

	public String     getFormat()                { return "IBM_DB2"; }
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

	public String comment(String message)
	{
		return "-- " + message;
	}
	//---------------------------------------------------------------------------
	//--- Env : DATABASE
	//---------------------------------------------------------------------------

	private ModuleOptions databaseOptions = new ModuleOptions()
	{
		private DatabasePanel dbPanel = new DatabasePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, DB2Module.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, DB2Module.this),
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

	// Table

	protected String genTablePost(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		String postSql = ts.getPostSql().trim();

		return postSql.equals("") ? "" : LF + postSql;
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

		if (!start.equals(""))   w += " START WITH "   + start;  else w += " START WITH 1 ";
		if (!incr.equals(""))    w += " INCREMENT BY " + incr;   else w += " INCREMENT BY 1";
		if (!minVal.equals(""))  w += " MINVALUE "     + minVal; else w += " NO MINVALUE";
		if (!maxVal.equals(""))  w += " MAXVALUE "     + maxVal; else w += " NO MAXVALUE" ;
		if (as.getBool("cycle")) w += " CYCLE";                  else w += " NO CYCLE";
		if (!cache.equals(""))   w += " CACHE "        + cache;  else w += " NO CACHE";
		if (as.getBool("order")) w += " ORDER";                  else w += " NO ORDER";

		if (!w.equals(""))
			s += w;
//			s += LF + "   " + w;

		return s;
	}

	//---------------------------------------------------------------------------
	// Trigger

	protected String genTrigger(Trigger trigger)
	{
		String tName = trigger.getParentTable().attrSet.getString("name");

		AttribSet as = trigger.attrSet;

		String name    = as.getString("name");
		String activat = as.getString("activation");
		String forEach = as.getString("forEach");
		String when    = as.getString("when").trim();
		String of      = as.getString("of").trim();
		String code    = as.getString("code").trim();

		code = cutEndingColon(code);

		boolean onInsert = as.getBool("onInsert");
		boolean onUpdate = as.getBool("onUpdate");
		boolean onDelete = as.getBool("onDelete");

		String s = "CREATE TRIGGER " +name+ " "+ LF +
						new TriggerDB2ActivationDecoder().decode(activat) +" "+
						getOperations(onInsert, onUpdate, onDelete) +
						getOf(of) + " ON " +tName+ LF+
						getReferences(onInsert, onUpdate, onDelete) +
						"FOR EACH " + new TriggerForEachDecoder().decode(forEach) + LF +
						"MODE DB2SQL ";

		if (!when.equals(""))
			s += "WHEN (" +when+ ") ";

		return s + LF + code;
	}

	//---------------------------------------------------------------------------

	protected String getOf(String Of)
	{
	  String OfClause = "";

		if (!Of.equals(""))
			OfClause = " OF " + Of;
		else
		  OfClause = "";

		return OfClause;

	}

	//---------------------------------------------------------------------------

	protected String getReferences(boolean insert, boolean update, boolean delete)
	{
		String references = "";

		if (insert) references = "REFERENCING NEW AS N " + LF;
		if (update) references = "REFERENCING NEW AS N OLD AS O " + LF;
		if (delete) references = "REFERENCING OLD AS O " + LF;

		return references;

	}

	//---------------------------------------------------------------------------
	//---
	//--- DROP statements generation
	//---
	//---------------------------------------------------------------------------

	protected String genDropTrigger(Trigger node)
	{
		return "DROP TRIGGER "+node.attrSet.getString("name");
	}

	//---------------------------------------------------------------------------

	protected String genDropSequence(SequenceNode node)
	{
		return "DROP SEQUENCE "+node.attrSet.getString("name")+" RESTRICT";
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());
		// TODO: DB2 has a full text index CREATE INDEX idx FOR TEXT ON fields
		return SqlUtil.generateIndex(node, indexAS, fields, unique, cnt, idxAL,false);
	}
}

//==============================================================================
