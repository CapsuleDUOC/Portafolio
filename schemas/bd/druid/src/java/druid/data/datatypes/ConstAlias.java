//==============================================================================
//===
//===   ConstAlias
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.Serials;

//==============================================================================

public class ConstAlias extends AbstractType
{
	//---------------------------------------------------------------------------

	public ConstAlias()
	{
		attrSet.addAttrib("id",          Serials.get());
		attrSet.addAttrib("ddEquiv",     "");

		setToolTipText("A constant alias (no size needed)");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ConstAlias(); }
}

//==============================================================================
