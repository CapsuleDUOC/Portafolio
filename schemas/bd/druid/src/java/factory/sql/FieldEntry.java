//==============================================================================
//===
//===   FieldEntry
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql;

//==============================================================================

class FieldEntry
{
	public String name;
	public String type;
	public String attribs = "";

	//---------------------------------------------------------------------------

	public FieldEntry() {}

	//---------------------------------------------------------------------------

	public void addAttrib(String attrib)
	{
		if (attrib != null && !attrib.equals(""))
			attribs = attribs +" "+ attrib;
	}
}

//==============================================================================
