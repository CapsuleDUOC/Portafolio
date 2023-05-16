//==============================================================================
//===
//===   TreeViewRenderer
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.renderers;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.ConstFolder;
import druid.data.datatypes.DataTypes;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.datatypes.VarFolder;
import druid.util.gui.ImageFactory;

//==============================================================================

public class TreeViewRenderer extends DefaultTreeCellRenderer
{
	public TreeViewRenderer() {}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		if (node instanceof DatabaseNode) setIcon(ImageFactory.DATABASE);
		else
		if (node instanceof FolderNode || node instanceof ConstFolder ||
			 node instanceof VarFolder  || node instanceof DataTypes)
		{
			if (node.isExpanded())	setIcon(ImageFactory.OFOLDER);
				else 						setIcon(ImageFactory.CFOLDER);
		}
		else if (node instanceof TableNode)
		{
			TableNode t = (TableNode) node;
			
			setIcon(t.isGhost() ? ImageFactory.GHOST_TABLE : ImageFactory.TABLE);
		}
		else if (node instanceof ViewNode)      setIcon(ImageFactory.VIEW);
		else if (node instanceof ProcedureNode) setIcon(ImageFactory.PROCEDURE);
		else if (node instanceof FunctionNode)  setIcon(ImageFactory.FUNCTION);
		else if (node instanceof FieldNode)     setIcon(ImageFactory.FIELD);
		else if (node instanceof SequenceNode)  setIcon(ImageFactory.SEQUENCE);

		else if (node instanceof NotesNode)
		{
			NotesNode notes = (NotesNode) node;

			if (notes.isInfo())   setIcon(ImageFactory.INFO);
			if (notes.isAlert())  setIcon(ImageFactory.ALERT);
			if (notes.isDanger()) setIcon(ImageFactory.DANGER);
		}

		else if (node instanceof ConstDataType || node instanceof VarDataType)
				setIcon(ImageFactory.BASICDT);

		else if (node instanceof ConstAlias || node instanceof VarAlias)
				setIcon(ImageFactory.ALIASDT);

		return this;
	}
}

//==============================================================================
