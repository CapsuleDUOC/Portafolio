//==============================================================================
//===
//===   JdbcTreeRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.renderers;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.jdbc.entities.DatabaseEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.OtherEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.SynonymEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.UDTEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.util.gui.ImageFactory;

//==============================================================================

public class JdbcTreeRenderer extends DefaultTreeCellRenderer
{
	public JdbcTreeRenderer() {}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		if (node == null) return this;

		if (node instanceof DatabaseEntity)       setIcon(ImageFactory.DATABASE);

		else if (node instanceof TableEntity)     setIcon(ImageFactory.TABLE);
		else if (node instanceof ViewEntity)      setIcon(ImageFactory.VIEW);
		else if (node instanceof SynonymEntity)   setIcon(ImageFactory.SYNONYM);
		else if (node instanceof ProcedureEntity) setIcon(ImageFactory.PROCEDURE);
		else if (node instanceof FunctionEntity)  setIcon(ImageFactory.FUNCTION);
		else if (node instanceof SequenceEntity)  setIcon(ImageFactory.SEQUENCE);
		else if (node instanceof UDTEntity)       setIcon(ImageFactory.UDT);
		else if (node instanceof OtherEntity)     setIcon(ImageFactory.OTHER);

		else
		{
			if (node.isExpanded())	setIcon(ImageFactory.OFOLDER);
				else 						setIcon(ImageFactory.CFOLDER);
		}

		return this;
	}
}

//==============================================================================
