//==============================================================================
//===
//===   VarDataType
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class VarDataType extends AbstractType
{
	//---------------------------------------------------------------------------

	public VarDataType()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public VarDataType(String name)
	{
		super(name);

		setToolTipText("A variable datatype of your DBMS");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new VarDataType(); }
}

//==============================================================================
