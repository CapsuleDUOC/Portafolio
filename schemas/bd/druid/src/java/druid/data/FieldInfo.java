//==============================================================================
//===
//===   FieldInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

//==============================================================================

public class FieldInfo
{
	public String  name;
	public int     type;
	public int     refTable;
	public int     refField;
	public boolean isFkey;
	public String  matchType;
	public String  onUpdate;
	public String  onDelete;
}

//==============================================================================
