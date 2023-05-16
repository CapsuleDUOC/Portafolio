//==============================================================================
//===
//===   VarAlias
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.Serials;

//==============================================================================

public class VarAlias extends AbstractType
{
	//---------------------------------------------------------------------------

	public VarAlias()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public VarAlias(String name)
	{
		super(name);

		attrSet.addAttrib("id",      Serials.get());
		attrSet.addAttrib("size",    "");
		attrSet.addAttrib("ddEquiv", "");

		setToolTipText("A variable alias (needs size)");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new VarAlias(); }
}

//==============================================================================
