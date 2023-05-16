//==============================================================================
//===
//===   LocalPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.choice;

import java.awt.Dimension;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.data.DatabaseNode;
import druid.data.ProjectNode;

//==============================================================================

class LocalPanel extends TPanel
{
	public  static final String NAME = "local";
	private static final String TEXT = "Another database in this project";

	private TLabel   label    = new TLabel(TEXT);
	private TreeView treeView = new TreeView();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public LocalPanel(TreeViewSelListener l)
	{
		super("Local");

		FlexLayout flexL = new FlexLayout(2,1,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup treeview

		treeView.setName(NAME);
		treeView.setEditable(false);
		treeView.setCellRenderer(new ChoiceViewRenderer());
		treeView.addSelectionListener(l);
		treeView.setPreferredSize(new Dimension(200,100));
		treeView.setShowRootHandles(false);

		//--- setup panel

		add("0,0,x,t", label);
		add("1,0,c,x", treeView);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setDatabase(DatabaseNode dbNode)
	{
		ProjectNode projNode = dbNode.getProjectNode();

		TreeViewNode rootNode = new TreeViewNode();

		for(int i=0; i<projNode.getChildCount(); i++)
		{
			DatabaseNode node = (DatabaseNode) projNode.getChild(i);

			if (dbNode != node)
			{
				String dbName = node.attrSet.getString("name");

				rootNode.addChild(new TreeViewNode(dbName, node));
			}
		}

		treeView.setRootNode(rootNode);
	}

	//---------------------------------------------------------------------------

	public void deselectTreeView()
	{
		treeView.clearSelection();
	}

	//---------------------------------------------------------------------------

	public TreeViewNode getSelectedNode()
	{
		return treeView.getSelectedNode();
	}
}

//==============================================================================
