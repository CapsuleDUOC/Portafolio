//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql;

import druid.core.AttribList;
import druid.data.DatabaseNode;
import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;
import factory.sql.SqlUtil;

//==============================================================================

public class TableSettings extends AbstractSettings
{
	//---------------------------------------------------------------------------
	//---
	//--- Defaults
	//---
	//---------------------------------------------------------------------------

	private static AttribList defIndexOpt = new AttribList();

	//---------------------------------------------------------------------------

	static
	{
		defIndexOpt.addAttrib("id",         0);
		defIndexOpt.addAttrib("index",     "");
		defIndexOpt.addAttrib("name",      "");
		defIndexOpt.addAttrib("ts",         0);
		defIndexOpt.addAttrib("bitmap", false);
		defIndexOpt.addAttrib("stats",  false);
		defIndexOpt.addAttrib("noSort", false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String INDEXOPT      = "idx";
	private static final String TABLESPACE    = "ts";
	private static final String PK_TABLESPACE = "pkts";
	private static final String SEQ_GENERATION= "seqGener";
	private static final String SEQ_TEMPLATE  = "seqTempl";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public TableSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public int     getTablespace()   { return mc.getValue(bm, TABLESPACE,         0); }
	public int     getPKTablespace() { return mc.getValue(bm, PK_TABLESPACE,      0); }
	public String  getSeqTemplate()  { return mc.getValue(bm, SEQ_TEMPLATE,   "SEQ_{table}"); }
	public boolean isSeqGeneration() { return mc.getValue(bm, SEQ_GENERATION, false); }

	public void setTablespace   (int     value) { mc.setValue(bm, TABLESPACE,    value); }
	public void setPKTablespace (int     value) { mc.setValue(bm, PK_TABLESPACE, value); }
	public void setSeqTemplate  (String  value) { mc.setValue(bm, SEQ_TEMPLATE,  value); }
	public void setSeqGeneration(boolean value) { mc.setValue(bm, SEQ_GENERATION,value); }

	//--------------------------------------------------------------------------

	public AttribList getIndexOpt(DatabaseNode dbNode)
	{
		AttribList al = mc.getAttribList(bm, INDEXOPT, defIndexOpt);

		SqlUtil.syncIndexes(al, dbNode.fieldAttribs);

		return al;
	}
}

//==============================================================================
