//==============================================================================
//===
//===   ModuleView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.ModulesConfig;
import druid.data.ModulesUsage;
import druid.dialogs.generation.GenerateDialog;
import druid.interfaces.BasicModule;
import druid.interfaces.CodeGenModule;
import druid.interfaces.DataGenModule;
import druid.interfaces.DatadictGenModule;
import druid.interfaces.DocsGenModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.SqlGenModule;
import druid.interfaces.SummaryGenModule;
import druid.interfaces.TemplateGenModule;
import druid.panels.database.generation.GenerationPanel;
import druid.util.DruidUtil;
import druid.util.gui.renderers.ModuleViewRenderer;

//==============================================================================

public class ModuleView extends JPanel implements  PopupGenerator, ActionListener,
																	TreeViewSelListener
{
	private DataModel    dataModel;
	private TreeViewNode currNode;
	private DatabaseNode dbNode;

	private TreeView  tree = new TreeView();

	private GenerationPanel optPanel;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ModuleView()
	{
		FlexLayout flexL = new FlexLayout(1,1,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setEditable(false);
		tree.setCellRenderer(new ModuleViewRenderer());
		tree.setRootNode(new TreeViewNode());

		//--- setup panel

		add("0,0,x,x", tree);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setDatabaseNode(DatabaseNode node)
	{
		if (dbNode == node) return;

		currNode = null;
		dbNode   = node;

		TreeViewNode root = getModuleTree(node.modsUsage);
		root.setUserData(node);

		tree.setRootNode(root);
		root.expand(true, 2);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataModel.saveDataToNode(currNode);
	}

	//---------------------------------------------------------------------------

	public void setOptionsPanel(GenerationPanel o)
	{
		optPanel = o;
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
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		//------------------------------------------------------------------------
		//--- nothing selected

		if (node == null) return null;

		JPopupMenu popup = new JPopupMenu();

		//------------------------------------------------------------------------

		Object o = node.getUserData();

		if (o instanceof String)
		{
			String s = (String) o;

			boolean enabled = (node.getChildCount() != 0);

			popup.add(MenuFactory.createItem("pop_generate", "Generate",  this, enabled));

			if (s.equals(ModuleViewRenderer.DTGEN_DATADICT))
				addPopupModules(popup, DatadictGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_DOCS))
				addPopupModules(popup, DocsGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_SUMMARY))
				addPopupModules(popup, SummaryGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_CODE))
				addPopupModules(popup, CodeGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_SQL))
				addPopupModules(popup, SqlGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_TEMPLATE))
				addPopupModules(popup, TemplateGenModule.class);

			else if (s.equals(ModuleViewRenderer.DTGEN_GENERIC))
				addPopupModules(popup, GenericGenModule.class);
		}

		else if (o instanceof BasicModule)
		{
			popup.add(MenuFactory.createItem("pop_generate", "Generate", this));
			popup.add(MenuFactory.createItem("pop_remove",   "Remove",   this));
		}

		return popup;
	}

	//---------------------------------------------------------------------------

	private void addPopupModules(JPopupMenu popup, Class c)
	{
		JMenu menu = MenuFactory.createMenu("Add module");
		popup.add(menu);

		for (Enumeration e=ModuleManager.getModules(c); e.hasMoreElements();)
		{
			DataGenModule mod = (DataGenModule) e.nextElement();

			String modId = ModuleManager.getAbsoluteID(mod);

			if (!dbNode.modsUsage.contains(mod))
				menu.add(MenuFactory.createItem(modId, mod.getFormat(), this));
		}

		menu.setEnabled(menu.getMenuComponentCount() != 0);
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

		if (cmd.equals("pop_generate"))
			pop_generate();

		else if (cmd.equals("pop_remove"))
				pop_remove();

		//--- maybe it is an add of a module

		else
		{
			for (Enumeration en=ModuleManager.getAllModules(); en.hasMoreElements();)
			{
				BasicModule mod = (BasicModule) en.nextElement();

				String modId = ModuleManager.getAbsoluteID(mod);

				if (cmd.equals(modId))
					pop_add(mod);
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup Methods
	//---
	//---------------------------------------------------------------------------

	private void pop_generate()
	{
		optPanel.saveDataToNode(dbNode);

		GenerateDialog dlg = new GenerateDialog(GuiUtil.getFrame(this));

		dlg.run(dbNode, tree.getSelectedNode());
	}

	//---------------------------------------------------------------------------

	private void pop_add(BasicModule mod)
	{
		TreeViewNode parent = tree.getSelectedNode();

		//--- add child, selecting it and making it visible
		parent.addChild(new TreeViewNode(getModLabel(mod), mod));

		//--- add module to config

		dbNode.modsUsage.addModule(mod);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_remove()
	{
		TreeViewNode node = tree.getSelectedNode();

		//--- we must set currNode to null because the removeNode method calls
		//--- the select method. A nodeSelected event is raised for the removed node.
		//--- when the saveDataToNode is called for the removed node, the modules
		//--- config is not available anymore because the removed node is detached
		//--- from its root

		currNode = null;

		//--- remove from tree

		DruidUtil.removeNode(node, true);

		//--- remove from config

		BasicModule mod = (BasicModule) node.getUserData();
		dbNode.modsUsage.removeModule(mod);

		//--- remove module settings from all nodes

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			ModulesConfig cfg = ((AbstractNode) e.nextElement()).modsConfig;
			cfg.remove(mod);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Setup tree struct
	//---
	//---------------------------------------------------------------------------

	private TreeViewNode getModuleTree(ModulesUsage modsUsage)
	{
		TreeViewNode gen  = new TreeViewNode("<html><b>DATA GENERATION</b>", ModuleViewRenderer.DATAGEN);

		addDataGenModules(gen, modsUsage);

		//--- setup and return root node

		TreeViewNode root = new TreeViewNode();

		root.addChild(gen);

		return root;
	}

	//---------------------------------------------------------------------------

	private void addDataGenModules(TreeViewNode parent, ModulesUsage modsUsage)
	{
		TreeViewNode dd   = new TreeViewNode("Data Dictionary", ModuleViewRenderer.DTGEN_DATADICT);
		TreeViewNode docs = new TreeViewNode("Docs",            ModuleViewRenderer.DTGEN_DOCS);
		TreeViewNode summ = new TreeViewNode("Summary",         ModuleViewRenderer.DTGEN_SUMMARY);
		TreeViewNode code = new TreeViewNode("Code",            ModuleViewRenderer.DTGEN_CODE);
		TreeViewNode sql  = new TreeViewNode("Sql script",      ModuleViewRenderer.DTGEN_SQL);
		TreeViewNode temp = new TreeViewNode("From Templates",  ModuleViewRenderer.DTGEN_TEMPLATE);
		TreeViewNode gen  = new TreeViewNode("Persistence",     ModuleViewRenderer.DTGEN_GENERIC);

		//--- add modules

		addModules(dd,   modsUsage, DatadictGenModule.class);
		addModules(docs, modsUsage, DocsGenModule.class);
		addModules(summ, modsUsage, SummaryGenModule.class);
		addModules(code, modsUsage, CodeGenModule.class);
		addModules(sql,  modsUsage, SqlGenModule.class);
		addModules(temp, modsUsage, TemplateGenModule.class);
		addModules(gen,  modsUsage, GenericGenModule.class);

		//--- setup sub-tree

		parent.addChild(dd);
		parent.addChild(docs);
		parent.addChild(summ);
		parent.addChild(code);
		parent.addChild(sql);
		parent.addChild(temp);
		parent.addChild(gen);
	}

	//---------------------------------------------------------------------------

	private void addModules(TreeViewNode parent, ModulesUsage modsUsage, Class c)
	{
		for(Enumeration e=ModuleManager.getModules(c); e.hasMoreElements();)
		{
			BasicModule mod = (BasicModule) e.nextElement();

			if (modsUsage.contains(mod))
				parent.addChild(new TreeViewNode(getModLabel(mod), mod));
		}
	}

	//---------------------------------------------------------------------------

	private String getModLabel(BasicModule mod)
	{
		if (mod instanceof DataGenModule)
			return ((DataGenModule) mod).getFormat();

		return mod.getId();
	}
}

//==============================================================================
