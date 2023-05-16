//==============================================================================
//===
//===   TriggerView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.triggers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
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
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcExport;
import druid.core.jdbc.JdbcLib;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.Trigger;
import druid.dialogs.preview.SqlPreviewDialog;
import druid.interfaces.SqlGenModule;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.SimpleTreeViewRenderer;

//==============================================================================

public class TriggerView extends JPanel implements PopupGenerator, ActionListener,
																	TreeViewSelListener
{
	private DataModel    dataModel;
	private TreeViewNode currNode;
	private Trigger      trigNode;

	protected TreeView tree = new TreeView();

	private DndRuleManager dndManager = new DndRuleManager(ImageFactory.NODROP);

	private SqlPreviewDialog  sqlDlg;

	private static final String REBUILD_TRIG = "pop_trigRebuild";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TriggerView()
	{
		FlexLayout flexL = new FlexLayout(1,1,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new SimpleTreeViewRenderer(ImageFactory.TRIGGER));
		tree.setRootNode(new Trigger());
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

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		//------------------------------------------------------------------------
		//--- nothing selected

		if (node == null)
		{
			popup.add(MenuFactory.createItem("pop_add", "Add trigger", this));
		}
		else
		{
			boolean connected = ((AbstractNode) node).getDatabase().getJdbcConnection().isConnected();

			popup.add(MenuFactory.createItem("pop_delete",     "Delete trigger", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_sqlPreview", "Sql preview...", this));

			popup.addSeparator();

			JMenu menu = MenuFactory.createMenu("Rebuild in jdbc DB");

			menu.setEnabled(connected);
			popup.add(menu);
			JdbcLib.fillRebuildMenu(menu, (AbstractNode) node, REBUILD_TRIG, this);
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.TRIGGER);

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
		if (cmd.equals("pop_sqlPreview"))  pop_sqlPreview();

		if (cmd.startsWith(REBUILD_TRIG))
		{
			String      id   = cmd.substring(REBUILD_TRIG.length());
			SqlGenModule mod = (SqlGenModule) ModuleManager.getModule(SqlGenModule.class, id);

			pop_trigRebuild(mod);
		}

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
		parent.addChild(new Trigger());

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void pop_delete()
	{
		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(tree.getSelectedNode(), true);
	}

	//---------------------------------------------------------------------------

	private void pop_sqlPreview()
	{
		AbstractNode node = (AbstractNode) tree.getSelectedNode();

		if (sqlDlg == null)
			sqlDlg = new SqlPreviewDialog(GuiUtil.getFrame(this));

		sqlDlg.run(node);
	}

	//---------------------------------------------------------------------------

	private void pop_trigRebuild(SqlGenModule mod)
	{
		AbstractNode   node     = (AbstractNode) tree.getSelectedNode();
		JdbcConnection jdbcConn = node.getDatabase().getJdbcConnection();

		GuiUtil.setWaitCursor(this, true);

		try
		{
			JdbcExport.rebuildEntity(jdbcConn, mod, node);
		}
		catch(Exception e)
		{
			Dialogs.showOperationAborted(this, e.getMessage());
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setTriggerNode(Trigger node)
	{
		if (trigNode == node) return;

		currNode = null;
		trigNode = node;
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
		dndManager.addRule(Trigger.class, Trigger.class, TreeViewDndHandler.ACCEPT_BEFORE);
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
