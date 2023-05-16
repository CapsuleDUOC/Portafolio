//==============================================================================
//===
//===   ColorView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop.legend.colors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dlib.gui.ColorIcon;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.TToolBar;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.DataLib;
import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.er.ErThemeManager;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.er.Legend;
import druid.data.er.LegendColor;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;

//==============================================================================

public class ColorView extends JPanel implements PopupGenerator, ActionListener,
																 TreeViewSelListener
{
	private DataModel    dataModel;
	private TreeViewNode clipNode;

	private TreeView tree = new TreeView();

	private AbstractButton btnNew;
	private AbstractButton btnCut;
	private AbstractButton btnCopy;
	private AbstractButton btnPaste;
	private AbstractButton btnUp;
	private AbstractButton btnDown;

	private TreeViewNode currNode;

	//---------------------------------------------------------------------------

	public ColorView()
	{
		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		//------------------------------------------------------------------------
		//--- toolbar

		TToolBar toolBar = new TToolBar();

		btnNew = toolBar.add(ImageFactory.NEW,  this, "pop_new",  "Add a new color");

		toolBar.addSeparator();

		btnCut   = toolBar.add(ImageFactory.CUT,  this, "pop_cut",  "Cut color");
		btnCopy  = toolBar.add(ImageFactory.COPY, this, "pop_copy", "Copy color");
		btnPaste = toolBar.add(ImageFactory.PASTE,this, "pop_paste","Paste color");

		toolBar.addSeparator();

		btnUp   = toolBar.add(ImageFactory.UP,   this, "pop_moveup",   "Move color up");
		btnDown = toolBar.add(ImageFactory.DOWN, this, "pop_movedown", "Move color down");

		//------------------------------------------------------------------------
		//--- setup tree

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new ColorViewRenderer());
		tree.setRootNode(new TreeViewNode());

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0,x",   toolBar);
		add("0,1,x,x", tree);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void setLegend(Legend l)
	{
		currNode = null;
		clipNode = null;
		tree.setRootNode(l);
		updateButtons(null);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		boolean paste = (clipNode != null);

		if (node == null)
		{
			popup.add(MenuFactory.createItem("pop_new",   "Add color", this));
			
			//--- add themes
			JMenu themes = MenuFactory.createMenu("Add colors from theme");
			
			for (String theme : ErThemeManager.getThemeNames())
				themes.add(MenuFactory.createItem("theme."+ theme, theme, this));
			
			popup.add(themes);
			
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_paste", "Paste",   this, paste));
		}
		else
		{
			popup.add(MenuFactory.createItem("pop_del",      "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",      "Cut",    this));
			popup.add(MenuFactory.createItem("pop_copy",     "Copy",   this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_moveup",   "Move up",   this));
			popup.add(MenuFactory.createItem("pop_movedown", "Move down", this));
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.ER_LEGEND);

		return popup;
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		dataModel.saveDataToNode(currNode);

		currNode = e.getSelectedNode();

		dataModel.setCurrentNode(currNode);
		updateButtons(currNode);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		//--- we need to store GUI's data into internal structs
		//--- because several pop_xxx methods require fresh data

		dataModel.saveDataToNode(currNode);

		//--- handle popup menus ---

		if (cmd.equals("pop_new"))       pop_new();
		if (cmd.equals("pop_del"))       pop_del(false);
		if (cmd.equals("pop_cut"))       pop_del(true);
		if (cmd.equals("pop_copy"))      pop_copy();
		if (cmd.equals("pop_paste"))     pop_paste();

		if (cmd.equals("pop_moveup"))    pop_moveup();
		if (cmd.equals("pop_movedown"))  pop_movedown();

		//------------------------------------------------------------------------
		//--- handle themes
		
		if (cmd.startsWith("theme."))
			pop_addFromTheme(cmd.substring(6));
		
		//------------------------------------------------------------------------
		//--- dispatch module events

		TreeViewNode node = tree.getSelectedNode();
		updateButtons(node);

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.dispatchTreeNodeEvent(GuiUtil.getFrame(this), cmd, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup Methods
	//---
	//---------------------------------------------------------------------------

	private void pop_new()
	{
		TreeViewNode parent = tree.getRootNode();
		TreeViewNode node   = new LegendColor();

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		node.setToolTipText("A color used to represent an entity");

		DataTracker.setDataChanged();

		updateButtons(node);
	}

	//---------------------------------------------------------------------------

	private void pop_addFromTheme(String theme)
	{
		TreeViewNode parent = tree.getRootNode();
		
		for (LegendColor node : ErThemeManager.getThemeColors(theme))
		{
			node = (LegendColor) node.duplicate();
			DataLib.remapIds(node, false);
			
			//--- adds child, selecting it and making it visible
			parent.addChild(node);

			node.setToolTipText("A color used to represent an entity");			
			updateButtons(node);
		}
		
		DataTracker.setDataChanged();
	}
	
	//---------------------------------------------------------------------------

	private void pop_del(boolean cut)
	{
		TreeViewNode node = tree.getSelectedNode();

		if (cut)
			clipNode = node;
		else
		{
			if (!Dialogs.confirm(this, "Delete color confirmation", "Are you sure you want to delete this color?"))
				return;
			
		}
		
		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(node, true);
		
		updateButtons(node);
	}

	//---------------------------------------------------------------------------

	private void pop_copy()
	{
		clipNode = tree.getSelectedNode().duplicate();
		DataLib.remapIds((AbstractNode) clipNode, false);

		updateButtons(tree.getSelectedNode());
	}

	//---------------------------------------------------------------------------

	private void pop_paste()
	{
		TreeViewNode node  = tree.getRootNode();
		LegendColor  child = (LegendColor) clipNode;

		clipNode = clipNode.duplicate();
		DataLib.remapIds((AbstractNode) clipNode, false);
		
		//--- add child, selecting it and making it visible
		node.addChild(child);

		DataTracker.setDataChanged();

		updateButtons(child);
	}

	//---------------------------------------------------------------------------

	private void pop_moveup()
	{
		TreeViewNode node = tree.getSelectedNode();

		DruidUtil.moveTreeNode(node, true);

		updateButtons(node);
	}

	//---------------------------------------------------------------------------

	private void pop_movedown()
	{
		TreeViewNode node = tree.getSelectedNode();

		DruidUtil.moveTreeNode(node, false);

		updateButtons(node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void updateButtons(TreeViewNode node)
	{
		if (node == tree.getRootNode()) 
			node = null;

		btnPaste.setEnabled(clipNode != null);

		btnCut.setEnabled((node != null) && (node.getParent().getChildCount() != 1));
		btnCopy.setEnabled(node != null);

		if (node == null)
		{
			btnUp.setEnabled(false);
			btnDown.setEnabled(false);
		}
		else
		{
			TreeViewNode parent = (TreeViewNode) node.getParent();

			int ndx = parent.getIndex(node);

			if (parent.getChildCount() == 1)
			{
				btnUp.setEnabled(false);
				btnDown.setEnabled(false);
			}
			else
			{
				btnUp.setEnabled(ndx != 0);
				btnDown.setEnabled(ndx != parent.getChildCount()-1);
			}
		}
	}
}

//==============================================================================
//===
//===   ColorViewRenderer
//===
//==============================================================================

class ColorViewRenderer extends DefaultTreeCellRenderer
{
	private ColorIcon colorIcon = new ColorIcon();

	//---------------------------------------------------------------------------

	public ColorViewRenderer() {}

	//---------------------------------------------------------------------------

	public Component getTreeCellRendererComponent(JTree tree, Object value,
								boolean sel, boolean exp, boolean leaf,
								int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

		TreeViewNode node = (TreeViewNode) value;

		setIcon(colorIcon);
		setToolTipText(node.getToolTipText());

		if (node instanceof LegendColor)
		{
			LegendColor lc = (LegendColor) node;
			colorIcon.setColor(lc.colBg);
			colorIcon.setBorderColor(lc.colBorder);
		}

		return this;
	}
}

//==============================================================================
