//==============================================================================
//===
//===   TableRule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class TableRule extends AbstractNode
{
	//---------------------------------------------------------------------------

	public TableRule()
	{
		attrSet.addAttrib("use",    true);
		attrSet.addAttrib("rule",   "");

		setToolTipText("A rule to check contraints");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new TableRule(); }
}

//==============================================================================
