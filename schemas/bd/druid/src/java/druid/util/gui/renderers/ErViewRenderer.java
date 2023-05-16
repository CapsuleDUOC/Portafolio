//==============================================================================
//===
//===   ErViewRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.renderers;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.util.gui.ImageFactory;

//==============================================================================

public class ErViewRenderer extends DefaultTreeCellRenderer
{
	public ErViewRenderer() {}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		if (node instanceof ErView)
			setIcon(ImageFactory.ER_VIEW);

		else if (node instanceof ErEntity)
			setIcon(ImageFactory.ER_ENTITY);

		return this;
	}
}

//==============================================================================
