//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mysql.sql;

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
	private static final String ENGINE   = "engine";
	private static final String CHARSET  = "charset";
	private static final String AUTO_INC = "autoInc";

	public static final String TYPE_MYISAM = "MyISAM";
	public static final String TYPE_INNODB = "InnoDB";
	public static final String TYPE_MERGE = "MERGE";
	public static final String TYPE_MEMORY = "MEMORY";
	public static final String TYPE_BDB = "BDB";
	public static final String TYPE_FEDERATED = "FEDERATED";
	public static final String TYPE_ARCHIVE = "ARCHIVE";
	public static final String TYPE_CSV = "CSV";
	public static final String TYPE_BLACKHOLE = "BLACKHOLE";	

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

	public String  getEngine()  { return mc.getValue(bm, ENGINE, TYPE_INNODB); }
	public String  getCharset() { return mc.getValue(bm, CHARSET,         ""); }
	public boolean isAutoInc()  { return mc.getValue(bm, AUTO_INC,     false); }

	public void setEngine (String value) { mc.setValue(bm, ENGINE,  value); }
	public void setCharset(String value) { mc.setValue(bm, CHARSET, value); }
	public void setAutoInc(boolean value){ mc.setValue(bm, AUTO_INC,value); }
}

//==============================================================================
