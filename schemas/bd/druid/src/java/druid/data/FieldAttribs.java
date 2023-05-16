//==============================================================================
//===
//===   FieldAttribs
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.Serials;

//==============================================================================

public class FieldAttribs extends AttribList
{
	protected AttribList getNewInstance() { return new FieldAttribs(); }

	//---------------------------------------------------------------------------
	//--- type consts ---

	public static final String TYPE_BOOL    = "b";
	public static final String TYPE_STRING  = "s";
	public static final String TYPE_INT     = "i";

	//--- scope consts ---

	public static final String SCOPE_FIELD  = "f";
	public static final String SCOPE_TABLE  = "t";
	public static final String SCOPE_INDEX  = "i";
	public static final String SCOPE_UINDEX = "u";
	public static final String SCOPE_FTINDEX = "x";
	public static final String SCOPE_CUSTOM = "c";

	//---------------------------------------------------------------------------

	public FieldAttribs()
	{
		addAttrib("id",        Serials.get());
		addAttrib("name",      "-UnNamed-");
		addAttrib("sqlName",   "");
		addAttrib("type",      TYPE_BOOL);     //--- must be the same as for AttribSet
		addAttrib("scope",     SCOPE_FIELD);
		addAttrib("useInDD",   false);
		addAttrib("useInSumm", false);
		addAttrib("width",     60);
		addAttrib("descr",     "");
	}

	//---------------------------------------------------------------------------

	public AttribSet findPrimaryKey()
	{
		for(int i=0; i<size(); i++)
		{
			AttribSet as = get(i);

			String sqlName = as.getString("sqlName");
			String type    = as.getString("type");
			
			if (sqlName.equalsIgnoreCase("primary key") && type.equals(TYPE_BOOL))
				return as;
		}

		return null;
	}
}

//==============================================================================
