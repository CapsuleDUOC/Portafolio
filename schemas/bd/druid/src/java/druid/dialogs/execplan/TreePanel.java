//==============================================================================
//===
//===   TreePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.execplan;

import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.treetable.TreeTable;
import org.dlib.gui.treeview.TreeViewNode;

import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class TreePanel extends JPanel
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TreePanel()
	{
		FlexLayout fl = new FlexLayout(1, 1);
		fl.setColProp(0, FlexLayout.EXPAND);
		fl.setRowProp(0, FlexLayout.EXPAND);
		setLayout(fl);

		add("0,0,x,x", new JScrollPane(treeTable));

//		treeTable.setEditable(false);
		treeTable.setRootVisible(false);
		treeTable.setTreeCellRenderer(new TreeViewRenderer());
	}

	//---------------------------------------------------------------------------
	//---
	//---   API methods
	//---
	//---------------------------------------------------------------------------

	public void setupTable(TreeViewNode root, List<String> header)
	{
		int delta = 200;
		
		for (String name : header)
		{
			treeTable.addHeader(name, 100 + delta);
			delta = 0;
		}
		
		treeTable.setRootNode(root);
		root.expand(true, 20);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private TreeTable treeTable = new TreeTable();
}

//==============================================================================
