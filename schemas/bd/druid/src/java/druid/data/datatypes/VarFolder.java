//==============================================================================
//===
//===   VarFolder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class VarFolder extends AbstractType
{
	//---------------------------------------------------------------------------

	public VarFolder()
	{
		setEditable(false);
		setText("Variable Size");
		setToolTipText("This folder contains variable datatypes and aliases");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new VarFolder(); }
}

//==============================================================================
