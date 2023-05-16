//==============================================================================
//===
//===   ViewNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class ViewNode extends AbstractNode
{
	//---------------------------------------------------------------------------

	public ViewNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public ViewNode(String name)
	{
		super(name);

		attrSet.addAttrib("sqlCode", "CREATE VIEW <name>(<field>,<field>...) AS\n   SELECT...");

		setToolTipText("A Sql view: Use the 'sql' tab to write the code");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ViewNode(); }
}

//==============================================================================
