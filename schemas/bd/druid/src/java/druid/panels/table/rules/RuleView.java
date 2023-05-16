//==============================================================================
//===
//===   RuleView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.rules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import druid.data.TableRule;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.SimpleTreeViewRenderer;

//==============================================================================

public class RuleView extends JPanel implements PopupGenerator, ActionListener,
																	TreeViewSelListener
{
	private DataModel    dataModel;
	private TreeViewNode currNode;
	private TableRule    ruleNode;

	protected TreeView tree = new TreeView();

	private DndRuleManager dndManager = new DndRuleManager(ImageFactory.NODROP);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public RuleView()
	{
		FlexLayout flexL = new FlexLayout(1,1,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new SimpleTreeViewRenderer(ImageFactory.RULE));
		tree.setRootNode(new TableRule());
		tree.setDndHandler(dndManager);

		//--- setup panel

		add("0,0,x,x", tree);

		setupDndRules();

		//--- setup key bindings

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Delete());
	}

	//---------------------------------------------------------------------------
	//---
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode treeNode)
	{
		JPopupMenu popup = new JPopupMenu();

		//------------------------------------------------------------------------
		//--- nothing selected

		if (treeNode == null)
		{
			popup.add(MenuFactory.createItem("pop_add", "Add rule", this));
		}
		else
		{
			popup.add(MenuFactory.createItem("pop_delete", "Delete rule", this));
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (treeNode == null)
			treeNode = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, treeNode, this, TreeNodeModule.TABLERULE);

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
	}

	//---------------------------------------------------------------------------
	//---
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		//--- we need to store GUI's data into internal structs
		//--- because several pop_xxx methods require fresh data

		dataModel.saveDataToNode(currNode);

		//--- handle popup menus ---

		if (cmd.equals("pop_add"))         pop_add();
		if (cmd.equals("pop_delete"))      pop_delete();

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

	private void pop_add()
	{
		TreeViewNode parent = tree.getRootNode();

		//--- adds child, selecting it and making it visible
		parent.addChild(new TableRule());

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void pop_delete()
	{
		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(tree.getSelectedNode(), true);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setRuleNode(TableRule node)
	{
		if (ruleNode == node) return;

		currNode = null;
		ruleNode = node;
		tree.setRootNode(node);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataModel.saveDataToNode(currNode);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupDndRules()
	{
		dndManager.addRule(TableRule.class, TableRule.class, TreeViewDndHandler.ACCEPT_BEFORE);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Key bindings classes
	//---
	//---------------------------------------------------------------------------

	private class Delete extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (tree.getSelectedNode() != null)
				pop_delete();
		}
	}
}

//==============================================================================
