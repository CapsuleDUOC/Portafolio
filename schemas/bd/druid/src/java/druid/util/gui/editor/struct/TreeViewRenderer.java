//==============================================================================
//===
//===   TreeViewRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.editor.struct;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.text.html.HTML;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.util.gui.ImageFactory;

//==============================================================================

public class TreeViewRenderer extends DefaultTreeCellRenderer
{

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		ElementInfo info = (ElementInfo) node.getUserData();

		if (info != null)
		{
			if (info.tag == HTML.Tag.P || info.tag == HTML.Tag.IMPLIED)
				setIcon(ImageFactory.PARAGRAPH);

			else if (info.tag == HTML.Tag.CONTENT)	setIcon(ImageFactory.FONT_FAMIL);
			else if (info.tag == HTML.Tag.IMG)		setIcon(ImageFactory.IMAGE);
			else if (info.tag == HTML.Tag.OL)		setIcon(ImageFactory.LIST_ORDERED);
			else if (info.tag == HTML.Tag.UL)		setIcon(ImageFactory.LIST_UNORDERED);
			else if (info.tag == HTML.Tag.LI)		setIcon(ImageFactory.LIST_ITEM);
		}

		return this;
	}
}

//==============================================================================
