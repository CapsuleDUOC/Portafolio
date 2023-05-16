//==============================================================================
//===
//===   AbstractSqlGenModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DataTypeLib;
import druid.core.DruidException;
import druid.core.config.Config;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.TableRule;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;
import druid.util.decoder.MatchTypeDecoder;
import druid.util.decoder.OnClauseDecoder;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;
import java.util.List;
import java.util.Vector;
import org.dlib.tools.TVector;
import org.dlib.tools.Util;

//==============================================================================

public abstract class AbstractSqlGenModule implements SqlGenModule
{
	protected String LF = Config.os.lineSep;

	//---------------------------------------------------------------------------
	//---
	//--- SqlGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String generate(AbstractNode node)
	{
		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);

		if (node instanceof TableNode)
			return sett.isGenTables() ? genTable((TableNode) node) : "";

		if (node instanceof ViewNode)
			return sett.isGenViews() ? genView((ViewNode) node) : "";

		if (node instanceof ProcedureNode)
			return sett.isGenProcedures() ? genProcedure((ProcedureNode) node) : "";

		if (node instanceof FunctionNode)
			return sett.isGenFunctions() ? genFunction((FunctionNode) node) : "";

		if (node instanceof SequenceNode)
			return sett.isGenSequences() ? genSequence((SequenceNode) node) : "";

		if (node instanceof Trigger)
			return sett.isGenTriggers() ? genTrigger((Trigger) node) : "";

		throw new DruidException(DruidException.INC_STR, "Unknown node instance", node);
	}

	//---------------------------------------------------------------------------

	public String generateDrop(AbstractNode node)
	{
		if (node instanceof TableNode)
			return genDropTableAlt((TableNode) node);

		if (node instanceof ViewNode)
			return genDropView((ViewNode) node);

		if (node instanceof ProcedureNode)
			return genDropProcedure((ProcedureNode) node);

		if (node instanceof FunctionNode)
			return genDropFunction((FunctionNode) node);

		if (node instanceof SequenceNode)
			return genDropSequenceAlt((SequenceNode) node);

		throw new DruidException(DruidException.INC_STR, "Unknown node instance", node);
	}

	//---------------------------------------------------------------------------
	
	public String generateExtra(AbstractNode node)
	{
		return null;
	}
	
	//---------------------------------------------------------------------------
	//--- Indexes generation
	//---------------------------------------------------------------------------

	public List generateIndexes(TableNode node)
	{
		Vector vList = new Vector();

		//--- must we generate indexes ?

		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);

		if (!sett.isGenIndexes())
			return vList;

		//--- ok, go on with generation

		FieldAttribs fAttribs = node.getDatabase().fieldAttribs;

		int cnt = 1;

		//------------------------------------------------------------------------

		for(int j=0; j<fAttribs.size(); j++)
		{
			AttribSet as = fAttribs.get(j);

			String faId    = "" + as.getInt("id");
			String faScope = as.getString("scope");

			//--- check scope

			boolean idx = faScope.equals(FieldAttribs.SCOPE_INDEX);
			boolean udx = faScope.equals(FieldAttribs.SCOPE_UINDEX);
			boolean ftx = faScope.equals(FieldAttribs.SCOPE_FTINDEX);

			if (idx || udx || ftx)
			{
				TVector fields = new TVector();

				for(int i=0; i<node.getChildCount(); i++)
				{
					FieldNode f = (FieldNode) node.getChild(i);

					Object obj = f.fieldAttribs.getData(faId);

					if (obj == null)
						throw new DruidException(DruidException.INC_STR, "Obj is null !!!");

					if (obj instanceof Boolean)
					{
						boolean b = ((Boolean)obj).booleanValue();

						if (b)
							fields.addElement(f.attrSet.getString("name"));
					}
				}

				//--- ? is there any field for attrib ?
				//--- if the answer is yes then build the attrib line

				if (fields.size() != 0)
				{
					vList.add(generateIndex(node, as, fields, udx, cnt));
					cnt++;
				}
			}
		}

		return vList;
	}

	//---------------------------------------------------------------------------

	protected abstract String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt);

	//---------------------------------------------------------------------------
	//--- References generation
	//---------------------------------------------------------------------------

	public String generateReferences(TableNode node)
	{
		return genReferences(node);
	}

	//---------------------------------------------------------------------------
	//--- Comments generation
	//---------------------------------------------------------------------------

	public List generateComments(TableNode node)
	{
		Vector v = new Vector();

		//--- must we generate comments ?

		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);

		if (!sett.isGenComments())
			return v;

		//--- ok, go on with generation

		String tableName = node.attrSet.getString("name");
		String tableComm = node.attrSet.getString("comment").trim();

		if (tableComm.length() > 0)
			v.add(genCommForTable(tableName, tableComm));

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode f = (FieldNode) node.getChild(i);

			AttribSet as = f.attrSet;

			String fieldName = as.getString("name");
			String fieldComm = as.getString("comment");

			if ( fieldComm.length() > 0 )
				v.add(genCommForField(tableName, fieldName, fieldComm));
		}

		return v;
	}

	//---------------------------------------------------------------------------

	protected String genCommForTable(String name, String comment)
	{
		return 	"COMMENT ON TABLE " + name +
					" is '" + Util.replaceStr(comment, "'", "''") + "'";
	}

	//---------------------------------------------------------------------------

	protected String genCommForField(String table, String name, String comment)
	{
		return 	"COMMENT ON COLUMN " + table + "." + name +
					" is '" + Util.replaceStr(comment, "'", "''") + "'";
	}

	//---------------------------------------------------------------------------
	//--- Other methods
	//---------------------------------------------------------------------------

	public String comment(String message)
	{
		return "-- " + message;
	}

	//---------------------------------------------------------------------------

	public String check(AbstractNode node)
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public String getCodeSeparator()
	{
		return "";
	}

	//---------------------------------------------------------------------------
	//---
	//--- DataGenModule interface
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode node)
	{
		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.modsConfig, this);

		String sOrder = sett.getOrder();

		int order = -1;

		if (sOrder.equals(BasicDatabaseSettings.ORDER_OPTIMIZED))  order = SqlScriptGenerator.ORDER_OPTIMIZED;
		if (sOrder.equals(BasicDatabaseSettings.ORDER_SEQUENCIAL)) order = SqlScriptGenerator.ORDER_SEQUENCIAL;

		new SqlScriptGenerator().generate(l, node, this, order, sett.isGenDropStmts());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table generation
	//---
	//---------------------------------------------------------------------------

	protected String genTable(TableNode node)
	{
		if (node.isGhost())
			return "";
		
		DatabaseNode dbNode = node.getDatabase();

		BasicDatabaseSettings sett = new BasicDatabaseSettings(dbNode.modsConfig, this);

		AttribList alSqlMapping = sett.getSqlMapping();

		FieldAttribs fAttribs = dbNode.fieldAttribs;

		TVector vChecks = new TVector();
		vChecks.setSeparator("," + LF);

		//------------------------------------------------------------------------
		//--- collect fields

		Vector vFieldEntries = new Vector();

		int maxFieldLen = 0;
		int maxTypeLen  = 0;

		for(int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			AttribSet fAs = field.attrSet;

			FieldEntry fe = new FieldEntry();

			fe.name = fAs.getString("name");
			fe.type = genFieldType(field, vChecks);
			fe.type = remapSqlType(fe.type, alSqlMapping);

			vFieldEntries.add(fe);

			maxFieldLen = Math.max(maxFieldLen, fe.name.length());
			maxTypeLen  = Math.max(maxTypeLen,  fe.type.length());

			//--- write field body ---

			for(int j = 0; j < fAttribs.size(); j++)
			{
				AttribSet as = fAttribs.get(j);

				String faId    = as.getInt("id") +"";
				String faScope = as.getString("scope");

				Object obj = field.fieldAttribs.getData(faId);

				if (obj == null)
					throw new DruidException(DruidException.INC_STR, "Obj is null !!!");

				//--- check scope

				if (faScope.equals(FieldAttribs.SCOPE_FIELD))
					fe.addAttrib(genFieldAttrib(field, as, obj));
			}
			
			fe.addAttrib(getExtraAttribs(field));
		}

		//------------------------------------------------------------------------
		//--- CREATE statement

		StringBuffer sb = new StringBuffer();

		sb.append(genTablePre(node) + LF);
		sb.append("  (" + LF);

		//------------------------------------------------------------------------
		//--- generate fields

		for (int i = 0; i < vFieldEntries.size(); i++)
		{
			FieldEntry fe = (FieldEntry) vFieldEntries.get(i);

			sb.append("    ");
			sb.append(Util.pad(fe.name, maxFieldLen));
			sb.append("  ");

			if (fe.attribs.equals(""))
				sb.append(fe.type);
			else
			{
				sb.append(Util.pad(fe.type, maxTypeLen));
				sb.append("  ");
				sb.append(fe.attribs);
			}

			//--- end of line ---

			if (i != vFieldEntries.size() - 1)
				sb.append("," + LF);
		}

		//------------------------------------------------------------------------
		//--- table general data

		String tabAttr = genTableAttribs(node);

		if (!tabAttr.equals(""))
			sb.append("," + LF + LF + tabAttr);

		//--- generate fks with table defs

		if (sett.isGenInlineFKs())
		{
			String refer = genReferences(node);

			if (!refer.equals(""))
				sb.append("," + LF + LF + refer);
		}

		//--- add rules to checks

		TableRule rules = node.rules;

		for (int i = 0; i < rules.getChildCount(); i++)
		{
			TableRule rule = (TableRule) rules.getChild(i);

			AttribSet asRule = rule.attrSet;

			if (asRule.getBool("use"))
			{
				String textRule = genTableRule(rule);

				if (!textRule.equals(""))
					vChecks.add(textRule);
			}
		}

		String checks = vChecks.toString();

		if (!checks.equals(""))
			sb.append("," + LF + LF + checks);

		//------------------------------------------------------------------------

		sb.append(LF + "  )");

		sb.append(genTablePost(node));

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTablePre(TableNode node)
	{
		return "CREATE TABLE " + getQualifiedName(node);
	}

	//---------------------------------------------------------------------------

	protected String getQualifiedName(TableNode node)
	{
		AttribSet as = node.attrSet;

		String schema = as.getString("schema").trim();

		if (schema.equals("")) 	return as.getString("name");
			else						return schema +"."+ as.getString("name");
	}

	//---------------------------------------------------------------------------

	protected String genTablePost(TableNode node)
	{
		return "";
	}

	//---------------------------------------------------------------------------

	protected String genFieldType(FieldNode node, Vector vChecks)
	{
		//--- if the field is not a fkey, check its domain

		if (node.attrSet.getInt("type") != 0)
		{
			String check = DataTypeLib.getTypeCheckString(node);

			//--- if the domain is defined, add it to the check list

			if (check != null)
				vChecks.add("    CHECK(" + check + ")");
		}

		//--- resolve and return type

		return DataTypeLib.getSqlType(node);
	}

	//---------------------------------------------------------------------------

	protected String remapSqlType(String type, AttribList alSqlMapping)
	{
		for(int i=0; i<alSqlMapping.size(); i++)
		{
			AttribSet as = alSqlMapping.get(i);

			String sqlType    = as.getString("sqlType");
			String mappedType = as.getString("mappedType");

			try
			{
				if (type.matches(sqlType))
					return type.replaceFirst(sqlType, mappedType);
			}
			catch(Exception e)
			{
				System.out.println("Manformed regular expression : " +sqlType);
				System.out.println("Exception is : " +e.getMessage());
			}
		}

		return type;
	}

	//---------------------------------------------------------------------------

	protected String genFieldAttrib(FieldNode node, AttribSet asAttrib, Object obj)
	{
		StringBuffer sb = new StringBuffer();

		String faSqlName = asAttrib.getString("sqlName");

		if (obj instanceof Boolean)
		{
			boolean b = ((Boolean)obj).booleanValue();

			if (b)
				sb.append(faSqlName);
		}

		else if (obj instanceof String || obj instanceof Integer)
		{
			String value = obj.toString().trim();

			if (!value.equals(""))
				sb.append(faSqlName + " " + value);
		}

		else
			throw new DruidException(DruidException.INC_STR, "Unknown type of object", obj);

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTableRule(TableRule rule)
	{
		String  name = rule.attrSet.getString("name").trim();
		String  text = rule.attrSet.getString("rule").trim();

		if (!name.equals(""))
			name = "CONSTRAINT " + name + " ";

		return "    " + name + "CHECK(" + text + ")";
	}

	//---------------------------------------------------------------------------
	
	protected String getExtraAttribs(FieldNode field) 
	{
		return null;
	}
	
	//---------------------------------------------------------------------------
	//--- Table Attribs Generation
	//---------------------------------------------------------------------------

	protected String genTableAttribs(TableNode tableNode)
	{
		FieldAttribs fAttribs = tableNode.getDatabase().fieldAttribs;

		Vector vTableAttribs = new Vector();

		//------------------------------------------------------------------------

		for(int j=0; j<fAttribs.size(); j++)
		{
			String faId      = "" + fAttribs.get(j).getInt("id");
			String faSqlName = fAttribs.get(j).getString("sqlName");
			String faScope   = fAttribs.get(j).getString("scope");

			//--- check scope

			if (faScope.equals(FieldAttribs.SCOPE_TABLE))
			{
				TVector fields = new TVector();

				for(int i=0; i<tableNode.getChildCount(); i++)
				{
					FieldNode f = (FieldNode) tableNode.getChild(i);

					Object obj = f.fieldAttribs.getData(faId);

					if (obj == null)
						throw new DruidException(DruidException.INC_STR, "Obj is null !!!");

					if (obj instanceof Boolean)
					{
						boolean b = ((Boolean)obj).booleanValue();

						if (b)
							fields.addElement(f.attrSet.getString("name"));
					}
				}

				//--- ? is there any field for attrib ?
				//--- if the answer is yes then build the attrib line

				if (fields.size() != 0)
				{
					fields.insertElementAt(faSqlName, 0);
					vTableAttribs.addElement(fields);
				}
			}
		}

		//------------------------------------------------------------------------

		StringBuffer sb = new StringBuffer();

		for(int i=0; i<vTableAttribs.size(); i++)
		{
			TVector fields = (TVector) vTableAttribs.get(i);

			String sqlName = (String) fields.get(0);

			fields.removeElementAt(0);

			sb.append("    " + genTableAttrib(tableNode, sqlName, fields, i+1));

			if (i != vTableAttribs.size()-1)
				sb.append("," + LF);
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genTableAttrib(TableNode node, String sqlName, TVector vFields, int cnt)
	{
		//--- add constraint name to PKEY

		StringBuffer sb = new StringBuffer();

		boolean pkey   = sqlName.toLowerCase().equals("primary key");

		AttribSet as = node.attrSet;

		String name = pkey ? as.getString("tempPK") : as.getString("tempOther");

		if (!name.equals(""))
		{
			sb.append("CONSTRAINT ");
			sb.append(SqlUtil.expandTemplate(name, as.getString("name"), vFields, cnt));
			sb.append(" ");
		}

		sb.append(sqlName);
		sb.append("(");
		sb.append(vFields.toString());
		sb.append(")");

		return sb.toString();
	}

	//---------------------------------------------------------------------------
	//---   Foreign Key Generation
	//---------------------------------------------------------------------------

	protected String genReferences(TableNode tableNode)
	{
		BasicDatabaseSettings sett = new BasicDatabaseSettings(tableNode.getDatabase().modsConfig, this);

		//------------------------------------------------------------------------
        //--- get all table fkeys

        Vector v = SqlUtil.getForeignKeys(tableNode);

		//------------------------------------------------------------------------
		//--- generate fkey references

		StringBuffer sb = new StringBuffer();

		if (sett.isGenInlineFKs())
		{
			for(int i=0; i<v.size(); i++)
			{
				sb.append("    " + genForeignKeyInline(tableNode, (FKeyEntry) v.get(i), i+1));

				if (i < v.size()-1)
					sb.append("," + LF);
			}
		}
		else
		{
			sb.append("ALTER TABLE ");
			sb.append(tableNode.attrSet.getString("name"));
			sb.append(" ADD CONSTRAINT ").append(LF);

			for(int i=0; i<v.size(); i++)
			{
				sb.append("    " + genForeignKey(tableNode, (FKeyEntry) v.get(i), i+1));

				if (i < v.size()-1)
				{
					sb.append(LF).append("/");
					sb.append(LF).append("ALTER TABLE ").append(tableNode.attrSet.getString("name"));
					sb.append(" ADD CONSTRAINT ").append(LF);//sb.append("," + LF);
				}
			}
		}

		return (v.size() > 0) ? sb.toString() : "";
	}

	//---------------------------------------------------------------------------

	protected String genForeignKey(TableNode node, FKeyEntry fk, int cnt)
	{
		StringBuffer sb = new StringBuffer(node.attrSet.getString("name"));
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

	protected String genForeignKeyInline(TableNode node, FKeyEntry fk, int cnt)
	{
		String s =  "foreign key(" + fk.vFields.toString() + ") references " + fk.fkTable +
						"(" + fk.vFkFields.toString() + ")";

		MatchTypeDecoder matchDec    = new MatchTypeDecoder();
		OnClauseDecoder  onClauseDec = new OnClauseDecoder();

		//--- match type ---

		if (!fk.matchType.equals(FieldNode.SIMPLE))
			s += " match " + matchDec.decode(fk.matchType);

		//--- on update ---

		if (!fk.onUpd.equals(FieldNode.NOACTION))
			s += " on update " + onClauseDec.decode(fk.onUpd);

		//--- on delete ---

		if (!fk.onDel.equals(FieldNode.NOACTION))
			s += " on delete " + onClauseDec.decode(fk.onDel);

		//--- add constraint name to FKEY

		StringBuffer sb = new StringBuffer();

		String name = node.attrSet.getString("tempFK");

		if (!name.equals(""))
		{
			sb.append("CONSTRAINT ");
			sb.append(SqlUtil.expandTemplate(name, node.attrSet.getString("name"), fk.vFields, cnt));
			sb.append(" ");
		}

		sb.append(s);

		return sb.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Views, procedures and functions generation
	//---
	//---------------------------------------------------------------------------

	protected String genView(ViewNode node)
	{
		return cutEndingColon(node.attrSet.getString("sqlCode"));
	}

	//---------------------------------------------------------------------------

	protected String genProcedure(ProcedureNode node)
	{
		return cutEndingColon(node.attrSet.getString("sqlCode"));
	}

	//---------------------------------------------------------------------------

	protected String genFunction(FunctionNode node)
	{
		return cutEndingColon(node.attrSet.getString("sqlCode"));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Sequence generation
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

		if (!minVal.equals(""))  w += " MINVALUE "     + minVal;
		if (!maxVal.equals(""))  w += " MAXVALUE "     + maxVal;
		if (!start.equals(""))   w += " START WITH "   + start;
		if (!incr.equals(""))    w += " INCREMENT BY " + incr;
		if (!cache.equals(""))   w += " CACHE "        + cache;
		if (as.getBool("cycle")) w += " CYCLE";
		if (as.getBool("order")) w += " ORDER";

		if (!w.equals(""))
			s += LF + "   " + w;

		return s;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Triggers Generation
	//---
	//---------------------------------------------------------------------------

	protected String genTrigger(Trigger trigger)
	{
		String tName = trigger.getParentTable().attrSet.getString("name");

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

	protected String getOperations(boolean insert, boolean update, boolean delete)
	{
		TVector v = new TVector();
		v.setSeparator(" OR ");

		if (insert) v.addElement("INSERT");
		if (update) v.addElement("UPDATE");
		if (delete) v.addElement("DELETE");

		return v.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- DROP statements generation
	//---
	//---------------------------------------------------------------------------

	protected String genDropTableAlt(TableNode node)
	{
		String fullName = node.getQualifiedName();

		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);

		if (!sett.isAltDropTable())
			return genDropTable(node);

		String s = sett.getAltDropTableStmt();

		s = Util.replaceStr(s, "{table}",  node.getName());
		s = Util.replaceStr(s, "{fqtable}",fullName);
		s = Util.replaceStr(s, "{schema}", node.getSchema());

		return s;
	}
	
	//---------------------------------------------------------------------------

	protected String genDropTable(TableNode node)
	{
		return "DROP TABLE "+node.getQualifiedName();
	}
	
	//---------------------------------------------------------------------------

	protected String genDropView(ViewNode node)
	{
		String code = node.attrSet.getString("sqlCode");

		return "DROP "+ SqlUtil.getTypeFromCode(code) +" "+ SqlUtil.getNameFromCode(code);
	}

	//---------------------------------------------------------------------------

	protected String genDropProcedure(ProcedureNode node)
	{
		String code = node.attrSet.getString("sqlCode");

		return "DROP "+ SqlUtil.getTypeFromCode(code) +" "+ SqlUtil.getNameFromCode(code);
	}

	//---------------------------------------------------------------------------

	protected String genDropFunction(FunctionNode node)
	{
		return "DROP FUNCTION "+SqlUtil.getNameFromCode(node.attrSet.getString("sqlCode"));
	}

	//---------------------------------------------------------------------------

	protected String genDropSequenceAlt(SequenceNode node)
	{
		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.getDatabase().modsConfig, this);

		if (!sett.isAltDropSequence())
			return genDropSequence(node);

		String s = sett.getAltDropSequenceStmt();

		s = Util.replaceStr(s, "{sequence}", node.attrSet.getString("name"));
//		s = Util.replaceStr(s, "{fqtable}",fullName);
//		s = Util.replaceStr(s, "{schema}", node.getSchema());

		return s;
	}

	//---------------------------------------------------------------------------

	protected String genDropSequence(SequenceNode node)
	{
		return "DROP SEQUENCE "+ node.attrSet.getString("name");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Other methods
	//---
	//---------------------------------------------------------------------------

	protected String cutEndingColon(String s)
	{
		s = s.trim();

		while (s.endsWith(";"))
			s = s.substring(0, s.length()-1).trim();

		return s;
	}
}

//==============================================================================
