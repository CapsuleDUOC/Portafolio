//==============================================================================
//===
//===   SqlView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.sqlnavigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.treeview.DndRuleManager;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewDndHandler;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.modules.ModuleManager;
import druid.data.SqlQuery;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.SimpleTreeViewRenderer;

//==============================================================================

public class SqlView extends JPanel implements PopupGenerator, ActionListener,
															  TreeViewSelListener
{
	protected TreeView tree = new TreeView();

	private TreeViewNode   prevNode;
	private TreeViewNode   currNode;
	protected TreeViewNode   clipNode;
	private DataModel      dataModel;
	private SqlResultPanel resultPanel;

	private DndRuleManager dndManager = new DndRuleManager(ImageFactory.NODROP);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlView()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new SimpleTreeViewRenderer(ImageFactory.GEAR));
		tree.setRootNode(new SqlQuery());
		tree.setDndHandler(dndManager);

		add("0,0,x,x", tree);

		setupDndRules();

		//--- setup key bindings

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Del());

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), new Cut());
		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), new Copy());
		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), new Paste());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void setRootNode(SqlQuery node)
	{
		if (node == prevNode) return;

		prevNode = node;
		currNode = null;
		resultPanel.clear();
		tree.setRootNode(node);
	}

	//---------------------------------------------------------------------------

	public boolean isANodeSelected()
	{
		return tree.getSelectedNode() != null;
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------

	public void setResultPanel(SqlResultPanel rp)
	{
		resultPanel = rp;
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataModel.saveDataToNode(currNode);
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

		resultPanel.enableExecute(currNode != null);
	}

	//---------------------------------------------------------------------------
	//---
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		boolean bPaste = (clipNode != null);
		boolean bRemove= (tree.getRootNode().getChildCount() != 0);

		//------------------------------------------------------------------------
		//--- nothing selected

		if (node == null)
		{
			popup.add(MenuFactory.createItem("pop_addquery",  "Add Query",  this));
			popup.add(MenuFactory.createItem("pop_paste",     "Paste",      this, bPaste));
			popup.add(MenuFactory.createItem("pop_removeall", "Remove all", this, bRemove));
		}
		else
		{
			popup.add(MenuFactory.createItem("pop_del",       "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",       "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy",      "Copy",  this));
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.JDBC_SQLNAVIG);

		return popup;
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

		if (cmd.equals("pop_addquery"))  pop_addQuery();

		if (cmd.equals("pop_del"))       pop_del(false);
		if (cmd.equals("pop_copy"))      pop_copy();
		if (cmd.equals("pop_cut"))       pop_del(true);
		if (cmd.equals("pop_paste"))     pop_paste();

		if (cmd.equals("pop_removeall")) pop_removeall();

		//------------------------------------------------------------------------
		//--- dispatch module events

		TreeViewNode node = tree.getSelectedNode();

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.dispatchTreeNodeEvent(GuiUtil.getFrame(this), cmd, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup Methods
	//---
	//---------------------------------------------------------------------------

	private void pop_addQuery()
	{
		TreeViewNode parent = tree.getRootNode();
		TreeViewNode node   = new SqlQuery();

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_del(boolean cut)
	{
		TreeViewNode node = tree.getSelectedNode();

		if (cut)
			clipNode = node;

		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(node, true);
	}

	//---------------------------------------------------------------------------

	protected void pop_copy()
	{
		clipNode = tree.getSelectedNode();

		clipNode = clipNode.duplicate();
	}

	//---------------------------------------------------------------------------

	protected void pop_paste()
	{
		TreeViewNode node  = tree.getRootNode();
		TreeViewNode child = clipNode;

		clipNode = clipNode.duplicate();

		//--- adds child, selecting it and making it visible
		node.addChild(child);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_removeall()
	{
		TreeViewNode node = tree.getRootNode();

		node.removeAllChildren();
		node.recalcChildren();

		DataTracker.setDataChanged();

		//--- the work panel needs refresh

		dataModel.setCurrentNode(null);
		resultPanel.clear();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupDndRules()
	{
		dndManager.addRule(SqlQuery.class, SqlQuery.class, TreeViewDndHandler.ACCEPT_BEFORE);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Key bindings classes
	//---
	//---------------------------------------------------------------------------

	private class Del extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node != null)
				pop_del(false);
		}
	}

	//---------------------------------------------------------------------------

	private class Cut extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node != null)
				pop_del(true);
		}
	}

	//---------------------------------------------------------------------------

	private class Copy extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node != null)
				pop_copy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (clipNode != null)
				pop_paste();
		}
	}
}

//==============================================================================
