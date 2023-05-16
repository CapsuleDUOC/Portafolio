//==============================================================================
//===
//===   PostgresModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql;

import java.util.Vector;

import javax.swing.JComponent;

import mod.dbms.postgresql.sql.panels.database.DatabasePanel;
import mod.dbms.postgresql.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.FieldNode;
import druid.data.FunctionNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.interfaces.ModuleOptions;
import druid.util.decoder.MatchTypeDecoder;
import druid.util.decoder.OnClauseDecoder;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;
import factory.sql.AbstractSqlGenModule;
import factory.sql.FKeyEntry;
import factory.sql.SqlUtil;

//==============================================================================

public class PostgresModule extends AbstractSqlGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "pgSql"; }
	public String getVersion()   { return "1.2";   }
	public String getAuthor()    { return "Andrea Carboni, Ruslan A. Bondar "; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script for PostgreSQL. All features derive from "   +
					"the standard sql module. Added inheritance, serials and full indexes support.";
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "PostgreSql"; }
	public boolean isDirectoryBased() { return false;        }
	public boolean hasLargePanel()    { return true;         }

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
			dbPanel.refresh(new DatabaseSettings(node.modsConfig, PostgresModule.this));
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
			tablePanel.refresh(new DatabaseSettings(node.getDatabase().modsConfig, PostgresModule.this),
									 new TableSettings   (node.modsConfig,               PostgresModule.this),
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

	protected String genTablePost(TableNode node)
	{
		DatabaseSettings gs = new DatabaseSettings(node.getDatabase().modsConfig, this);
		TableSettings    ts = new TableSettings(node.modsConfig, this);

		StringBuffer sb = new StringBuffer();

		//--- inheritance management

		String inherOverride = ts.getInheritsOverride().trim();

		if (inherOverride.length() != 0)
		{
			sb.append(LF + "  INHERITS(" + inherOverride +")");
		}
		else
		{
			int id = ts.getInheritsFrom();

			if (id != 0)
			{
				TableNode inherit = node.getDatabase().getTableByID(id);

				String name = "<DELETED>";

				if (inherit != null)
					name = inherit.attrSet.getString("name");

				sb.append(LF + "  INHERITS(" + name +")");
			}
		}

		//--- tablespaces management

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

	protected String genFieldType(FieldNode node, Vector vChecks)
	{
		String result = super.genFieldType(node, vChecks);

		//--- if the field is a fkey --> convert 'serial' to 'int'

		if (node.attrSet.getInt("type") == 0 && result.equals("serial"))
			result = "int";

		if (node.attrSet.getInt("type") == 0 && result.equals("serial8"))
			result = "int8";

		if (node.attrSet.getInt("type") == 0 && result.equals("bigserial"))
			result = "bigint";

		return result;
	}

	//---------------------------------------------------------------------------

	protected String genForeignKey(TableNode node, FKeyEntry fk, int cnt)
	{
		StringBuffer sb = new StringBuffer(node.getQualifiedName());
		sb.append("_").append(fk.vFields.toString());

		while (sb.length() > 27)
		{
			int us = sb.lastIndexOf("_");

			if (us == -1)
				break;

			sb.deleteCharAt(us);
		}

		if (sb.length() > 27)
			sb.setLength(sb.length()-(sb.length()-27)); // 30 char identifier limit

		sb .append("_FK ")
			.append("foreign key(")
			.append(fk.vFields.toString())
			.append(") references ")
			.append(fk.fkTable)
			.append("(")
			.append(fk.vFkFields.toString())
			.append(")");

		MatchTypeDecoder matchDec    = new MatchTypeDecoder();
		OnClauseDecoder  onClauseDec = new OnClauseDecoder();

		//--- match type ---

		if (!fk.matchType.equals(FieldNode.SIMPLE))
			sb.append(" match ").append(matchDec.decode(fk.matchType));

		//--- on update ---

		if (!fk.onUpd.equals(FieldNode.NOACTION))
			sb.append(" on update ").append(onClauseDec.decode(fk.onUpd));

		//--- on delete ---

		if (!fk.onDel.equals(FieldNode.NOACTION))
			sb.append(" on delete ").append(onClauseDec.decode(fk.onDel));

		//--- add constraint name to FKEY

		StringBuffer sc = new StringBuffer();

		String name = node.attrSet.getString("tempFK");

		if (!name.equals(""))
		{
			sc.append("CONSTRAINT ");
			sc.append(SqlUtil.expandTemplate(name, node.attrSet.getString("name"), fk.vFields, cnt));
			sc.append(" ");
		}

		sc.append(sb);

		return sc.toString();
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

		String  name   = optAS.getString("name");
		String  access = optAS.getString("access");
		String  where  = optAS.getString("where");

		if (name.equals(""))
			 name = templ;

		//--- build index

		StringBuffer sb = new StringBuffer();

		sb.append("CREATE ");
		sb.append(unique ? "UNIQUE " : "");
		sb.append("INDEX ");

		sb.append(SqlUtil.expandTemplate(name, table, fields, cnt));

		sb.append(" ON " + table);

		if (!access.equals(TableSettings.ACCESS_DEFAULT))
			sb.append(" USING " + access + " ");

		sb.append("(");
		sb.append(fields.toString() + ")");

		if (!where.equals(""))
			sb.append(" WHERE " + where);

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTrigger(Trigger trigger)
	{
		String tName = trigger.getParentTable().getQualifiedName();

		AttribSet as = trigger.attrSet;

		String name    = as.getString("name");
		String activat = as.getString("activation");
		String forEach = as.getString("forEach");
		String when    = as.getString("when").trim();
		String code    = as.getString("code").trim();

		code = cutEndingColon(code);

		boolean onInsert = as.getBool("onInsert");
		boolean onUpdate = as.getBool("onUpdate");
		boolean onDelete = as.getBool("onDelete");

		String s = "CREATE TRIGGER " +name+ " "+
						new TriggerActivationDecoder().decode(activat) +" "+
						getOperations(onInsert, onUpdate, onDelete) +
						" ON " +tName;

		TriggerForEachDecoder triggerForEachDecoder = new TriggerForEachDecoder();

		if ("ROW".equals(triggerForEachDecoder.decode(forEach)))
		{
			s += LF;
			s += "  FOR EACH " +triggerForEachDecoder.decode(forEach) +" ";

			if (!when.equals(""))
				s += "WHEN (" +when+ ") ";
		}

		return s + LF + code;
	}

	//---------------------------------------------------------------------------

	protected String genDropFunction(FunctionNode node)
	{
		String code = node.attrSet.getString("sqlCode");
		String name = SqlUtil.getNameFromCode(code);

		int start = code.indexOf("(");
		int end   = code.indexOf(")");

		if (start != -1 && end != -1 && start < end)
			name += code.substring(start, end +1);

		return "DROP FUNCTION " + name;
	}
}

//==============================================================================
