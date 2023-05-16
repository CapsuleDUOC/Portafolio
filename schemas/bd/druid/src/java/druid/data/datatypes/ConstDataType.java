//==============================================================================
//===
//===   ConstDataType
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.Serials;

//==============================================================================

public class ConstDataType extends AbstractType
{
	//---------------------------------------------------------------------------

	public ConstDataType()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public ConstDataType(String name)
	{
		super(name);

		attrSet.addAttrib("id",      Serials.get());
		attrSet.addAttrib("ddEquiv", "");

		setToolTipText("A constant datatype of your DBMS");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ConstDataType(); }
}

//==============================================================================
