//==============================================================================
//===
//===   Constraint
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================
//--- constraints support

public class Constraint extends AbstractNode
{
	//---------------------------------------------------------------------------

	public Constraint()
	{
		attrSet.addAttrib("code",   "");
		attrSet.addAttrib("status", "");

		setToolTipText("A Constraint of your table");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new Constraint(); }
}

//==============================================================================
