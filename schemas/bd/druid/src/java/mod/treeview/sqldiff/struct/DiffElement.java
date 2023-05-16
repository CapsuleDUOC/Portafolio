//==============================================================================
//===
//===   DiffElement
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.struct;

//==============================================================================

public class DiffElement
{
	public int iSubEntity;

	public Object  objOldValue;
	public Object  objNewValue;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DiffElement(int subEntity, Object oldValue, Object newValue)
	{
		iSubEntity   = subEntity;

		objOldValue  = oldValue;
		objNewValue  = newValue;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean isAdded()   { return objOldValue == null && objNewValue != null; }
	public boolean isRemoved() { return objOldValue != null && objNewValue == null; }
	public boolean isChanged() { return objOldValue != null && objNewValue != null; }

	//---------------------------------------------------------------------------

	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("[DEL: ");
		sb.append("subEnt="+iSubEntity +", ");
		sb.append("oldVal="+objOldValue+", ");
		sb.append("newVal="+objNewValue);
		sb.append("]");

		return sb.toString();
	}
}

//==============================================================================
