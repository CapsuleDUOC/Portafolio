//==============================================================================
//===
//===   SequenceNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class SequenceNode extends AbstractNode
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SequenceNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public SequenceNode(String name)
	{
		super(name);

		attrSet.addAttrib("increment", "");
		attrSet.addAttrib("minValue",  "");
		attrSet.addAttrib("maxValue",  "");
		attrSet.addAttrib("start",     "");
		attrSet.addAttrib("cache",     "");
		attrSet.addAttrib("cycle",     false);
		attrSet.addAttrib("order",     false);

		setToolTipText("A sequence that generate numbers");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new SequenceNode(); }
}

//==============================================================================
