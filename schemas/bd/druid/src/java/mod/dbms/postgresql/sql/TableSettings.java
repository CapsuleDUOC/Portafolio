//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.data.DatabaseNode;
import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;
import factory.sql.SqlUtil;

//==============================================================================

public class TableSettings extends AbstractSettings
{
	public static final String ACCESS_DEFAULT = "default";
	public static final String ACCESS_BTREE   = "BTREE";
	public static final String ACCESS_RTREE   = "RTREE";
	public static final String ACCESS_HASH    = "HASH";
	public static final String ACCESS_GIST    = "GIST";

	//---------------------------------------------------------------------------
	//---
	//--- Defaults
	//---
	//---------------------------------------------------------------------------

	private static AttribList defIndexOpt = new AttribList();

	//---------------------------------------------------------------------------

	static
	{
		defIndexOpt.addAttrib("id",       0);
		defIndexOpt.addAttrib("index",   "");
		defIndexOpt.addAttrib("name",    "");
		defIndexOpt.addAttrib("access",  ACCESS_DEFAULT);
		defIndexOpt.addAttrib("where",   "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String INDEXOPT   = "idx";
	private static final String TABLESPACE = "ts";
	private static final String INHERITS   = "inherits";
	private static final String INHER_OR   = "inherOR";

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

	public int getTablespace()   { return mc.getValue(bm, TABLESPACE, 0); }
	public int getInheritsFrom() { return mc.getValue(bm, INHERITS,   0); }

	//--------------------------------------------------------------------------

	public String getInheritsOverride() { return mc.getValue(bm, INHER_OR, ""); }

	//--------------------------------------------------------------------------

	public void setTablespace  (int value) { mc.setValue(bm, TABLESPACE, value); }
	public void setInheritsFrom(int value) { mc.setValue(bm, INHERITS,   value); }

	//--------------------------------------------------------------------------

	public void setInheritsOverride(String value) { mc.setValue(bm, INHER_OR, value); }

	//--------------------------------------------------------------------------

	public AttribList getIndexOpt(DatabaseNode dbNode)
	{
		AttribList al = mc.getAttribList(bm, INDEXOPT, defIndexOpt);

		SqlUtil.syncIndexes(al, dbNode.fieldAttribs);

		for(int i=0; i<al.size(); i++)
		{
			AttribSet as = al.get(i);

			if (as.getString("access").equals(""))
				as.setString("access", ACCESS_DEFAULT);
		}

		return al;
	}
}

//==============================================================================
