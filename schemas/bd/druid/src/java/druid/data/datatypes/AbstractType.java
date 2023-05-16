//==============================================================================
//===
//===   AbstractType
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;

//==============================================================================

public class AbstractType extends AbstractNode
{
	public Domain domain = new Domain();

	//---------------------------------------------------------------------------

	public AbstractType()                  {}
	public AbstractType(DatabaseNode node) { super(node); }
	public AbstractType(String name)       { super(name); }

	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		AbstractType n = (AbstractType) node;

		n.domain = domain.duplicate();

		super.copyTo(node);
	}
}

//==============================================================================
