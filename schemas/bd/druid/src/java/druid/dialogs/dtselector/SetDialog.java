//==============================================================================
//===
//===   SetDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.dtselector;

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

import druid.core.DataLib;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.VarAlias;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class SetDialog extends TDialog implements ActionListener, PopupGenerator
{
	private TreeView typeView   = new TreeView();
	private boolean  cancelFlag = false;

	private FieldNode pkNode;

	//--- if idType==false   id1 is the datatype id ---
	//--- if idType==true    id1 is the ref table id & id2 is the ref field id ---

	private boolean idType = false;

	private int id1 = 0;
	private int id2 = 0;

	//---------------------------------------------------------------------------

	public SetDialog(Frame frame)
	{
		super(frame, "DataType Selector", true);

		Container cp = getContentPane();

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		FlexLayout flexL = new FlexLayout(1,2,8,8);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		//--- setup treeview ---

		typeView.setEditable(false);
		typeView.setPopupGen(this);
		typeView.setCellRenderer(new TreeViewRenderer());

		//--- add data ---

		p.add("0,0,x",   new TLabel(" Select a Datatype or a table/field for an fkey  [right click] "));
		p.add("0,1,x,x", typeView);

		cp.add(p, BorderLayout.CENTER);
		typeView.setPreferredSize(new Dimension(350, 530));

		pack();
		clearCancelled();
		setLocationRelativeTo(getParent());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Popup Generation
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		if (node instanceof FieldNode)
			popup.add(MenuFactory.createItem("pop_chField", "Choose Field as FKey", this));

		if (node instanceof ConstAlias || node instanceof ConstDataType || node instanceof VarAlias)
			popup.add(MenuFactory.createItem("pop_chType", "Choose this Datatype", this));

		if (node instanceof TableNode)
		{
			for(int i=0; i< node.getChildCount(); i++)
			{
				FieldNode fNode = (FieldNode) node.getChild(i);

				if (DataLib.isPrimaryKey(fNode))
				{
					popup.add(MenuFactory.createItem("pop_chTable",
								"Choose the Primary key of this Table", this));

					pkNode = fNode;
					break;
				}
			}
		}

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

		TreeViewNode selNode = typeView.getSelectedNode();

		if (cmd.equals("pop_chField"))
		{
			FieldNode fieldNode = (FieldNode) selNode;
			id1 = ((TableNode)fieldNode.getParent()).attrSet.getInt("id");
			id2 = fieldNode.attrSet.getInt("id");
			setId(true, id1, id2);
		}
		else if (cmd.equals("pop_chType"))
			setId(false, ((AbstractType)selNode).attrSet.getInt("id"), 0);

		else if (cmd.equals("pop_chTable"))
		{
			id1 = ((TableNode)pkNode.getParent()).attrSet.getInt("id");
			id2 = pkNode.attrSet.getInt("id");
			setId(true, id1, id2);
		}
	}

	//---------------------------------------------------------------------------

	private void setId(boolean type, int id1Val, int id2Val)
	{
		idType = type;
		id1    = id1Val;
		id2    = id2Val;

		cancelFlag = false;
		hide();   //--- exit from dialog ---
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
		TreeViewNode tb = dbNode.duplicate();

		rootNode.add(dt);
		rootNode.add(tb);

		typeView.setRootNode(rootNode);

		dt.expand(true);
		dt.getChild(0).expand(true);
		dt.getChild(1).expand(true);

		tb.expand(true);

		//--- show ---

		cancelFlag = true;
		setVisible(true);

		return !cancelFlag;
	}

	//---------------------------------------------------------------------------

	public boolean isFkeySelected() { return idType; }
	public int     getId1()         { return id1;    }
	public int     getId2()         { return id2;    }
}

//==============================================================================
