//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.standard.sql;

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
		defIndexOpt.addAttrib("id",      0);
		defIndexOpt.addAttrib("index",  "");
		defIndexOpt.addAttrib("name",   "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String INDEXOPT = "idx";
	private static final String POSTSQL  = "postSql";

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

	public String getPostSql() { return mc.getValue(bm, POSTSQL, ""); }

	public void setPostSql(String value) { mc.setValue(bm, POSTSQL, value); }

	//--------------------------------------------------------------------------

	public AttribList getIndexOpt(DatabaseNode dbNode)
	{
		AttribList al = mc.getAttribList(bm, INDEXOPT, defIndexOpt);

		SqlUtil.syncIndexes(al, dbNode.fieldAttribs);

		return al;
	}
}

//==============================================================================
