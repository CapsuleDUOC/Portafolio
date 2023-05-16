//==============================================================================
//===
//===   TypeInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

//==============================================================================

public class TypeInfo
{
	public int id = 0;

	public String name;
	public String size;
	public String basicType;
	public String ddEquiv;

	//---------------------------------------------------------------------------

	public TypeInfo()
	{
	}

	//---------------------------------------------------------------------------

	public String getSqlType()
	{
		if (size == null) return basicType;
			else           return basicType + "(" + size + ")";
	}
}

//==============================================================================
