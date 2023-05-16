//==============================================================================
//===
//===   FunctionNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class FunctionNode extends AbstractNode
{
	//---------------------------------------------------------------------------

	public FunctionNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public FunctionNode(String name)
	{
		super(name);

		attrSet.addAttrib("sqlCode", "CREATE FUNCTION...");

		setToolTipText("A Function: Use the 'sql' tab to write the code");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new FunctionNode(); }
}

//==============================================================================
