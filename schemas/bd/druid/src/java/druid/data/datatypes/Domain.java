//==============================================================================
//===
//===   Domain
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import druid.core.AttribList;
import druid.core.AttribSet;

//==============================================================================

public class Domain
{
	public static final String NONE  = "none";
	public static final String LOWER = "lower";
	public static final String UPPER = "upper";
	public static final String RANGE = "range";
	public static final String SET   = "set";

	public AttribSet  attrSet   = new AttribSet();
	public AttribList valueList = new AttribList();

	//---------------------------------------------------------------------------

	public Domain()
	{
		attrSet.addAttrib("type",     NONE);
		attrSet.addAttrib("minValue", "");
		attrSet.addAttrib("maxValue", "");
		attrSet.addAttrib("outRange", false);

		valueList.addAttrib("value",  "");
	}

	//---------------------------------------------------------------------------

	public Domain duplicate()
	{
		Domain d = new Domain();

		d.attrSet   = attrSet.duplicate();
		d.valueList = valueList.duplicate();

		return d;
	}
}

//==============================================================================
