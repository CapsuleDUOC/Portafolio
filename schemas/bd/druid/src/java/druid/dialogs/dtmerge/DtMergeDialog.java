//==============================================================================
//===
//===   DtMergeDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.dtmerge;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.TDialog;
import org.dlib.gui.TLabel;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.VarAlias;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class DtMergeDialog extends TDialog implements ActionListener, PopupGenerator
{
	private TreeView typeView   = new TreeView();
	private boolean  cancelFlag = false;

	private int typeId;

	//---------------------------------------------------------------------------

	public DtMergeDialog(Frame frame)
	{
		super(frame, "DataType Merge", true);

		Container cp = getContentPane();

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		FlexLayout flexL = new FlexLayout(1,2,8,8);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		//--- setup treeview ---

		typeView.setPopupGen(this);
		typeView.setCellRenderer(new TreeViewRenderer());

		//--- add data ---

		p.add("0,0,x",   new TLabel(" Select the type to merge with "));
		p.add("0,1,x,x", typeView);

		cp.add(p, BorderLayout.CENTER);
		typeView.setPreferredSize(new Dimension(350, 530));
	}

	//---------------------------------------------------------------------------
	//---
	//---   Popup Generation
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		if (node instanceof ConstDataType || node instanceof ConstAlias || node instanceof VarAlias)
			popup.add(MenuFactory.createItem("pop_type", "Merge with this type", this));

		return popup;
	}

	//---------------------------------------------------------------------------
	//---
	//---   Event Handling
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("pop_type"))
		{
			AbstractType node = (AbstractType) typeView.getSelectedNode();

			typeId = node.attrSet.getInt("id");

			cancelFlag = false;
			hide();   //--- exit from dialog ---
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   API
	//---
	//---------------------------------------------------------------------------

	public boolean run(DatabaseNode dbNode)
	{
		//--- build tree ---

		TreeViewNode rootNode = new TreeViewNode();

		TreeViewNode dt = dbNode.dataTypes.duplicate();

		rootNode.add(dt);

		typeView.setRootNode(rootNode);

		dt.expand(true);
		dt.getChild(0).expand(true);
		dt.getChild(1).expand(true);

		//--- show ---

		cancelFlag = true;

		showDialog();
		return !cancelFlag;
	}

	//---------------------------------------------------------------------------

	public int getId() { return typeId; }
}

//==============================================================================
