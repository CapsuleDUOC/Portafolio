//==============================================================================
//===
//===   ChoiceViewRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.choice;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import druid.util.gui.ImageFactory;

//==============================================================================

class ChoiceViewRenderer extends DefaultTreeCellRenderer
{
	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		setIcon(ImageFactory.DATABASE);

		return this;
	}
}

//==============================================================================
