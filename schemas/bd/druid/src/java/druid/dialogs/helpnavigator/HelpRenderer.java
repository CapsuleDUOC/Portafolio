//==============================================================================
//===
//===   HelpRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.helpnavigator;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.util.gui.ImageFactory;

//==============================================================================

public class HelpRenderer extends DefaultTreeCellRenderer
{
	public HelpRenderer() {}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		if (node.isLeaf() && node.getLevel() != 1)
			setIcon(ImageFactory.PAGE);
		else
		{
			if (node.isExpanded())	setIcon(ImageFactory.BOOK_OPEN);
				else 						setIcon(ImageFactory.BOOK_CLOSED);
		}

		return this;
	}
}

//==============================================================================
