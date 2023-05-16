//==============================================================================
//===
//===   FieldAttribV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

//==============================================================================

public class FieldAttribV
{
	private Object value;

	//---------------------------------------------------------------------------

	public FieldAttribV(Object obj)
	{
		value = obj;
	}

	//---------------------------------------------------------------------------

	public boolean getIsBool()   { return value instanceof Boolean; }
	public boolean getIsString() { return value instanceof String;  }
	public boolean getIsInt()    { return value instanceof Integer; }

	public String  getValue()    { return value.toString(); }
}

//==============================================================================
