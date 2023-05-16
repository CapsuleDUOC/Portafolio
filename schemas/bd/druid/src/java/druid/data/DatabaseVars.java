//==============================================================================
//===
//===   DatabaseVars
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import druid.core.AttribList;

//==============================================================================

public class DatabaseVars extends AttribList
{
	//---------------------------------------------------------------------------
	//--- type consts ---

	public static final String BOOL   = "b";
	public static final String STRING = "s";
	public static final String INT    = "i";
	public static final String LONG   = "l";
	public static final String CHAR   = "c";
	public static final String FLOAT  = "f";
	public static final String DOUBLE = "d";

	//---------------------------------------------------------------------------

	public DatabaseVars()
	{
		addAttrib("name",  "-UnNamed-");
		addAttrib("type",  BOOL);
		addAttrib("value", "");
		addAttrib("descr", "");
	}

	//---------------------------------------------------------------------------

	protected AttribList getNewInstance() { return new DatabaseVars(); }
}

//==============================================================================
