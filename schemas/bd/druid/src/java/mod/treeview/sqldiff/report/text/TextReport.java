//==============================================================================
//===
//===   TextReport
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.report.text;

import java.util.List;
import java.util.Vector;

import mod.treeview.sqldiff.struct.DiffElement;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.tools.Util;

import druid.core.config.Config;
import druid.data.FieldAttribs;
import druid.util.DruidUtil;
import druid.util.decoder.Decoder;
import druid.util.decoder.OnClauseDecoder;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;

//==============================================================================

public class TextReport
{
	private static final String LF = Config.os.lineSep;

	private StringBuffer sbTextDiff = new StringBuffer();

	private OnClauseDecoder          onClauseDec = new OnClauseDecoder();
	private TriggerActivationDecoder trigActDec  = new TriggerActivationDecoder();
	private TriggerForEachDecoder    trigForDec  = new TriggerForEachDecoder();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TextReport() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String build(DiffSummary diffSumm)
	{
		buildDatabase(diffSumm);
		buildTables(diffSumm);
		buildViews(diffSumm);
		buildProcedures(diffSumm);
		buildFunctions(diffSumm);
		buildSequences(diffSumm);

		return sbTextDiff.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Generation private methods
	//---
	//---------------------------------------------------------------------------

	private void buildDatabase(DiffSummary diffSumm)
	{
		List list = diffSumm.list(DiffSummary.DATABASE);

		if (list.size() == 0)
			return;

		writeBigHeader("Database");

		DiffEntity ent = (DiffEntity) list.get(0);

		DiffElement dePreSql  = ent.get(DiffEntity.DB_PRESQL);
		DiffElement dePostSql = ent.get(DiffEntity.DB_POSTSQL);

		writeSql("Pre SQL",  dePreSql);
		writeSql("Post SQL", dePostSql);
	}

	//---------------------------------------------------------------------------

	private void buildTables(DiffSummary diffSumm)
	{
		List list = diffSumm.list(DiffSummary.TABLE);

		if (list.size() == 0)
			return;

		writeBigHeader("Tables");

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			writeSmallHeader(getPrefix(ent) +" "+ ent.getName());

			List fields   = collectSubEntities(diffSumm.list(DiffSummary.FIELD),     ent);
			List triggers = collectSubEntities(diffSumm.list(DiffSummary.TRIGGER),   ent);
			List rules    = collectSubEntities(diffSumm.list(DiffSummary.TABLERULE), ent);

			buildFields(diffSumm, fields);
			buildTriggers(triggers);
			buildTableRules(rules);
		}
	}

	//---------------------------------------------------------------------------

	private void buildFields(DiffSummary diffSumm, List list)
	{
		if (list.size() == 0)
			return;

		writeSeparator("Fields");

		//--- first loop : attribs with "FIELD" scope

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			List attribs = collectSubEntities(diffSumm.list(DiffSummary.FIELDATTRIB), ent);

			String head = getPrefix(ent) +" "+ Util.pad(ent.getName(), 15) + " : ";
			String body = buildFieldChanged(ent, attribs);

			if (!body.equals(""))
				write(head + body);
		}

		write("");

		//--- second loop : attribs with "TABLE" scope

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			List attribs = collectSubEntities(diffSumm.list(DiffSummary.FIELDATTRIB), ent);

			String body = buildTableAttribs(ent, attribs);

			if (!body.equals(""))
				write(body);
		}

		//--- third loop : fields that are foreign keys

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			String body = buildForeignKeys(ent);

			if (!body.equals(""))
				write(body);
		}

		write("");
	}

	//---------------------------------------------------------------------------

	private String buildFieldChanged(DiffEntity ent, List attribs)
	{
		StringBuffer sb = new StringBuffer();

		//--- handle type

		DiffElement deFieldType = ent.get(DiffEntity.FI_TYPE);

		if (deFieldType != null)
		{
			if (deFieldType.isAdded())
				sb.append("+"+ Util.pad(deFieldType.objNewValue +" ", 15));

			else if (deFieldType.isRemoved())
				sb.append("-"+ Util.pad(deFieldType.objOldValue +" ", 15));

			else
			{
				sb.append("~("+ deFieldType.objOldValue);
				sb.append(" --> ");
				sb.append(deFieldType.objNewValue +") ");
			}
		}

		//--- handle attribs

		for(int i=0; i<attribs.size(); i++)
		{
			DiffEntity attr = (DiffEntity) attribs.get(i);

			DiffElement deSql   = attr.get(DiffEntity.FA_SQLNAME);
			DiffElement deType  = attr.get(DiffEntity.FA_TYPE);
			DiffElement deScope = attr.get(DiffEntity.FA_SCOPE);
			DiffElement deValue = attr.get(DiffEntity.FA_VALUE);

			if (attr.isAdded())
			{
				if (deScope.objNewValue.equals(FieldAttribs.SCOPE_FIELD))
				{
					sb.append("+"+ deSql.objNewValue);

					if (deType.objNewValue.equals(FieldAttribs.TYPE_INT))
						sb.append("("+ deValue.objNewValue +")");

					else if (deType.objNewValue.equals(FieldAttribs.TYPE_STRING))
						sb.append("('"+ deValue.objNewValue +"')");

					sb.append(" ");
				}
			}

			else if (attr.isRemoved())
			{
				if (deScope.objOldValue.equals(FieldAttribs.SCOPE_FIELD))
				{
					sb.append("-"+ deSql.objOldValue);

					if (deType.objOldValue.equals(FieldAttribs.TYPE_INT))
						sb.append("("+ deValue.objOldValue +")");

					else if (deType.objOldValue.equals(FieldAttribs.TYPE_STRING))
						sb.append("('"+ deValue.objOldValue +"')");

					sb.append(" ");
				}
			}

			else //--- changed
			{
				if (deScope.objOldValue.equals(FieldAttribs.SCOPE_FIELD))
				{
					sb.append("~"+ deSql.objOldValue +"[");

					//--- the bool attrib is only added/removed, cannot be changed

					if (deType.objOldValue.equals(FieldAttribs.TYPE_INT))
					{
						sb.append("("+ deValue.objOldValue +")");
						sb.append(" --> ");
						sb.append("("+ deValue.objNewValue +")");
					}
					else
					{
						sb.append("('"+ deValue.objOldValue +"')");
						sb.append(" --> ");
						sb.append("('"+ deValue.objNewValue +"')");
					}

					sb.append("] ");
				}
			}
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String buildTableAttribs(DiffEntity ent, List attribs)
	{
		StringBuffer sb = new StringBuffer();

		for(int i=0; i<attribs.size(); i++)
		{
			DiffEntity attr = (DiffEntity) attribs.get(i);

			DiffElement deSql   = attr.get(DiffEntity.FA_SQLNAME);
			DiffElement deScope = attr.get(DiffEntity.FA_SCOPE);

			if (attr.isAdded())
			{
				if (deScope.objNewValue.equals(FieldAttribs.SCOPE_TABLE))
					sb.append("+"+ deSql.objNewValue +"("+ ent.getName() +") ");

				else if (deScope.objNewValue.equals(FieldAttribs.SCOPE_INDEX))
					sb.append("+INDEX("+ ent.getName() +") ");

				else if (deScope.objNewValue.equals(FieldAttribs.SCOPE_UINDEX))
					sb.append("+UNIQUE INDEX("+ ent.getName() +") ");
			}

			else if (attr.isRemoved())
			{
				if (deScope.objOldValue.equals(FieldAttribs.SCOPE_TABLE))
					sb.append("-"+ deSql.objOldValue +"("+ ent.getName() +") ");

				else if (deScope.objOldValue.equals(FieldAttribs.SCOPE_INDEX))
					sb.append("+INDEX("+ ent.getName() +") ");

				else if (deScope.objOldValue.equals(FieldAttribs.SCOPE_UINDEX))
					sb.append("+UNIQUE INDEX("+ ent.getName() +") ");
			}

			else //--- changed
			{
				//--- we are not interested these attribs (like 'default')
				//--- attribs of scope TABLE/[U]INDEX are boolean and then
				//--- can be only added/removed
			}
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String buildForeignKeys(DiffEntity ent)
	{
		StringBuffer sb = new StringBuffer();

		DiffElement deFKey = ent.get(DiffEntity.FI_FKEY);
		DiffElement deUpd  = ent.get(DiffEntity.FI_ONUPDATE);
		DiffElement deDel  = ent.get(DiffEntity.FI_ONDELETE);

		if (ent.isAdded())
		{
			if (deFKey == null) return "";

			sb.append("+foreign key : ");
			sb.append(deFKey.objNewValue +" ");
			sb.append(getOnClause("UPDATE", deUpd, false));
			sb.append(getOnClause("DELETE", deDel, false));
		}
		else if (ent.isRemoved())
		{
			if (deFKey == null) return "";

			sb.append("-foreign key : ");
			sb.append(deFKey.objOldValue +" ");
			sb.append(getOnClause("UPDATE", deUpd, true));
			sb.append(getOnClause("DELETE", deDel, true));
		}

		else //--- changed
		{
			if (deFKey == null)
			{
				if (deUpd == null && deDel == null) return "";

				sb.append("~foreign key for field '"+ent.getName()+"' : ");
				sb.append(getOnClauseChange("UPDATE", deUpd));
				sb.append(getOnClauseChange("DELETE", deDel));
			}

			else
			{
				sb.append("~foreign key : ");

				if (deFKey.isAdded())
					sb.append("+("+ deFKey.objNewValue +") ");

				else if (deFKey.isRemoved())
					sb.append("-("+ deFKey.objOldValue +") ");

				else
					sb.append("~("+deFKey.objOldValue+" --> "+deFKey.objNewValue +") ");

				sb.append(getOnClauseChange("UPDATE", deUpd));
				sb.append(getOnClauseChange("DELETE", deDel));
			}
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String getOnClauseChange(String name, DiffElement de)
	{
		if (de == null)
			return "";

		//--- add case

		if (de.isAdded())
			return "+(" + getOnClause(name, de, false) +") ";

		//--- remove case

		if (de.isRemoved())
			return "-(" + getOnClause(name, de, true) +") ";

		//--- change case

		StringBuffer sb = new StringBuffer();

		sb.append("~(ON "+ name +" ");
		sb.append(onClauseDec.decode(de.objOldValue.toString()));
		sb.append(" --> ");
		sb.append(onClauseDec.decode(de.objNewValue.toString()));
		sb.append(") ");

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String getOnClause(String name, DiffElement de, boolean old)
	{
		StringBuffer sb = new StringBuffer();

		if (de != null)
		{
			sb.append("ON "+ name +" ");

			String clause = old ? de.objOldValue.toString() : de.objNewValue.toString();

			sb.append(onClauseDec.decode(clause));
			sb.append(" ");
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private void buildTriggers(List list)
	{
		if (list.size() == 0)
			return;

		writeSeparator("Triggers");

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			write(getPrefix(ent) +" "+ ent.getName());

			writeAttrib("      ", "Activation", ent.get(DiffEntity.TR_ACTIVATION), trigActDec);
			writeAttrib("      ", "On Insert",  ent.get(DiffEntity.TR_ONINSERT));
			writeAttrib("      ", "On Update",  ent.get(DiffEntity.TR_ONUPDATE));
			writeAttrib("      ", "On Delete",  ent.get(DiffEntity.TR_ONDELETE));
			writeAttrib("      ", "For Each",   ent.get(DiffEntity.TR_FOREACH),    trigForDec);
			writeAttrib("      ", "When",       ent.get(DiffEntity.TR_WHEN));
			writeAttrib("      ", "Code",       ent.get(DiffEntity.TR_CODE), true);
			write("");
		}
	}

	//---------------------------------------------------------------------------

	private void buildTableRules(List list)
	{
		if (list.size() == 0)
			return;

		writeSeparator("Rules");

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			write(getPrefix(ent) +" "+ ent.getName());

			writeAttrib("      ", "In use", ent.get(DiffEntity.RU_USE));
			writeAttrib("      ", "Rule",   ent.get(DiffEntity.RU_RULE));
			write("");
		}
	}

	//---------------------------------------------------------------------------

	private void buildViews(DiffSummary diffSumm)
	{
		buildPFV("Views", diffSumm.list(DiffSummary.VIEW));
	}

	//---------------------------------------------------------------------------

	private void buildProcedures(DiffSummary diffSumm)
	{
		buildPFV("Procedures", diffSumm.list(DiffSummary.PROCEDURE));
	}

	//---------------------------------------------------------------------------

	private void buildFunctions(DiffSummary diffSumm)
	{
		buildPFV("Functions", diffSumm.list(DiffSummary.FUNCTION));
	}

	//---------------------------------------------------------------------------

	private void buildSequences(DiffSummary diffSumm)
	{
		List list = diffSumm.list(DiffSummary.SEQUENCE);

		if (list.size() == 0)
			return;

		writeBigHeader("Sequences");

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			writeSmallHeader(getPrefix(ent) +" "+ ent.getName());

			writeAttrib("", "Increment", ent.get(DiffEntity.SQ_INCREM));
			writeAttrib("", "Min value", ent.get(DiffEntity.SQ_MINVAL));
			writeAttrib("", "Max value", ent.get(DiffEntity.SQ_MAXVAL));
			writeAttrib("", "Start",     ent.get(DiffEntity.SQ_START));
			writeAttrib("", "Cache",     ent.get(DiffEntity.SQ_CACHE));
			writeAttrib("", "Cycle",     ent.get(DiffEntity.SQ_CYCLE));
			writeAttrib("", "Order",     ent.get(DiffEntity.SQ_ORDER));
			write("");
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- General private methods
	//---
	//---------------------------------------------------------------------------

	private List collectSubEntities(List list, DiffEntity ent)
	{
		Vector vResult = new Vector();

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity subEnt = (DiffEntity) list.get(i);

			if (subEnt.getParent() == ent)
				vResult.add(subEnt);
		}

		return vResult;
	}

	//---------------------------------------------------------------------------

	private void buildPFV(String name, java.util.List list)
	{
		if (list.size() == 0)
			return;

		writeBigHeader(name);

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			writeSql(ent.getName(), ent.get(DiffEntity.PFV_SQLCODE));
		}
	}

	//---------------------------------------------------------------------------

	private void writeSql(String name, DiffElement elem)
	{
		if (elem == null)
			return;

		String oldText = (elem.objOldValue != null) ? elem.objOldValue.toString() : null;
		String newText = (elem.objNewValue != null) ? elem.objNewValue.toString() : null;

		writeSmallHeader(getPrefix(oldText, newText) +" "+ name);

		if (oldText != null && newText != null)
		{
			write(oldText.trim());
			write("");
			writeSeparator("NEW");
			write(newText.trim());
		}

		else
		{
			if (oldText != null) write(oldText.trim());
			if (newText != null) write(newText.trim());
		}

		write("");
	}

	//---------------------------------------------------------------------------

/*	private void writeAttrib(String name, DiffElement elem)
	{
		writeAttrib("", name, elem);
	}*/

	//---------------------------------------------------------------------------

	private void writeAttrib(String prefix, String name, DiffElement elem)
	{
		writeAttrib(prefix, name, elem, false, null);
	}

	//---------------------------------------------------------------------------

	private void writeAttrib(String prefix, String name, DiffElement elem, boolean hide)
	{
		writeAttrib(prefix, name, elem, hide, null);
	}

	//---------------------------------------------------------------------------

	private void writeAttrib(String prefix, String name, DiffElement elem, Decoder dec)
	{
		writeAttrib(prefix, name, elem, false, dec);
	}

	//---------------------------------------------------------------------------

	private void writeAttrib(String prefix, String name, DiffElement elem, boolean hide, Decoder dec)
	{
		if (elem == null)
			return;

		StringBuffer sb = new StringBuffer(prefix + getPrefix(elem) + " ");

		sb.append(Util.pad(name, 15) +" : ");

		if (hide)
		{
			sb.append("(see project file)");
		}

		else
		{
			if (elem.isAdded())
				sb.append(DruidUtil.applyDecoder(dec, elem.objNewValue.toString()));

			else if (elem.isRemoved())
				sb.append(DruidUtil.applyDecoder(dec, elem.objOldValue.toString()));

			else
			{
				sb.append(Util.pad(DruidUtil.applyDecoder(dec, elem.objOldValue.toString()), 12));
				sb.append(" --> ");
				sb.append(DruidUtil.applyDecoder(dec, elem.objNewValue.toString()));
			}
		}

		write(sb.toString());
	}

	//---------------------------------------------------------------------------

	private String getPrefix(DiffEntity ent)
	{
		if (ent.isAdded())   return "(+)";
		if (ent.isRemoved()) return "(-)";

		return "(~)";
	}

	//---------------------------------------------------------------------------

	private String getPrefix(DiffElement elem)
	{
		return getPrefix(elem.objOldValue, elem.objNewValue);
	}

	//---------------------------------------------------------------------------

	private String getPrefix(Object oldValue, Object newValue)
	{
		if (oldValue == null) return "(+)";
		if (newValue == null) return "(-)";

		return "(~)";
	}

	//---------------------------------------------------------------------------

	private void writeSeparator(String text)
	{
		int len = 70 - text.length() -5;

		write("--- " +text+ " " +Util.replicate("-", len));
		write("");
	}

	//---------------------------------------------------------------------------

	private void writeBigHeader(String name)
	{
		write(Util.replicate("=", 70));
		write("=== ");
		write("=== " + name);
		write("=== ");
		write(Util.replicate("=", 70));
		write("");
	}

	//---------------------------------------------------------------------------

	private void writeSmallHeader(String name)
	{
		write(Util.replicate("-", 70));
		write(name);
		write(Util.replicate("-", 70));
		write("");
	}

	//---------------------------------------------------------------------------

	private void write(String text)
	{
		sbTextDiff.append(text + LF);
	}
}

//==============================================================================
