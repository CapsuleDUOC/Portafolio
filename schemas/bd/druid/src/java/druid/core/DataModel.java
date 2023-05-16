//==============================================================================
//===
//===   DataModel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public interface DataModel
{
	public void setCurrentNode(TreeViewNode node);
	public void saveDataToNode(TreeViewNode node);
}

//==============================================================================
