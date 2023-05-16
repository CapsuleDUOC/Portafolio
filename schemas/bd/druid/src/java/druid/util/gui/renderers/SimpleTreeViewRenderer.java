//==============================================================================
//===
//===   SimpleTreeViewRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.renderers;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.util.gui.ImageFactory;

//==============================================================================

public class SimpleTreeViewRenderer extends DefaultTreeCellRenderer
{
	private Icon icon;

	//---------------------------------------------------------------------------

	public SimpleTreeViewRenderer(Icon icon)
	{
		this.icon = icon;
	}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		if (node != null)
		{
			if (node.isLeaf())
				setIcon(icon);
			else
			{
				if (node.isExpanded())	setIcon(ImageFactory.OFOLDER);
					else 				setIcon(ImageFactory.CFOLDER);
			}
		}

		return this;
	}
}

//==============================================================================
