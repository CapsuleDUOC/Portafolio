//==============================================================================
//===
//===   FolderNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class FolderNode extends AbstractNode
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FolderNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public FolderNode(String name)
	{
		super(name);

		setToolTipText("A folder: Use it to organize your DB objects");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new FolderNode(); }
}

//==============================================================================
