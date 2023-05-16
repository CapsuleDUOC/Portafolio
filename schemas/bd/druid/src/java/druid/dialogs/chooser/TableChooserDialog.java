//==============================================================================
//===
//===   TableChooserDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.chooser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TButton;
import org.dlib.gui.TDialog;
import org.dlib.gui.TLabel;
import org.dlib.gui.TSeparator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FolderNode;
import druid.data.TableNode;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class TableChooserDialog extends TDialog implements TreeViewSelListener, ActionListener
{
	private TreeView treeView = new TreeView();
	private TButton  btnNone  = new TButton("None", "none", this);

	private int id;

	private boolean firstRun = true;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TableChooserDialog(Frame frame)
	{
		super(frame, "Table Selector", true);

		Container cp = getContentPane();

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		FlexLayout flexL = new FlexLayout(1,5);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		flexL.setNullGaps(4,4);
		p.setLayout(flexL);

		//--- setup treeview ---

		treeView.setEditable(false);
		treeView.addSelectionListener(this);
		treeView.setCellRenderer(new TreeViewRenderer());

		//--- add data ---

		p.add("0,0,x",   new TLabel(" Select a table [double left click] "));
		p.add("0,1,x,x", treeView);
		p.add("0,2,x",   new TSeparator(TSeparator.HORIZONTAL));
		p.add("0,3,c",   btnNone);

		cp.add(p, BorderLayout.CENTER);
		treeView.setPreferredSize(new Dimension(350, 530));
	}

	//---------------------------------------------------------------------------
	//---
	//---   API
	//---
	//---------------------------------------------------------------------------

	public boolean run(DatabaseNode dbNode)
	{
		//--- build tree ---

		TreeViewNode rootNode = dbNode.duplicate();

		//------------------------------------------------------------------------
		//--- step 1 : collect all tables (and folders)

		Enumeration e=rootNode.preorderEnumeration();

		//--- skip database node (root)

		e.nextElement();

		Vector vOther = new Vector();

		while (e.hasMoreElements())
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (!(node instanceof TableNode || node instanceof FolderNode))
				vOther.add(node);
		}

		//--- ok, now remove other objects from tree

		for(int i=0; i<vOther.size(); i++)
			((AbstractNode) vOther.get(i)).removeFromParent();

		//------------------------------------------------------------------------
		//--- step 2 : show result

		treeView.setRootNode(rootNode);

		rootNode.expand(true, 1);

		//--- show ---

		if (firstRun)
		{
			firstRun = false;
			showDialog();
		}
		else
		{
			clearCancelled();
			setVisible(true);
		}

		return !isCancelled();
	}

	//---------------------------------------------------------------------------

	public int getID() { return id; }

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		if (e.isDoubleClicked())
		{
			AbstractNode selNode = (AbstractNode) e.getSelectedNode();

			if (selNode instanceof TableNode)
			{
				id = selNode.attrSet.getInt("id");

				hide();
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		id = 0;
		hide();
	}
}

//==============================================================================
