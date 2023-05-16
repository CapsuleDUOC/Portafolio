//==============================================================================
//===
//===   ConstFolder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class ConstFolder extends AbstractType
{
	//---------------------------------------------------------------------------

	public ConstFolder()
	{
		setEditable(false);
		setText("Constant Size");
		setToolTipText("This folder contains constant datatypes and aliases");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ConstFolder(); }
}

//==============================================================================
