//==============================================================================
//===
//===   ModuleView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.modules;

import java.util.Enumeration;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.modules.ModuleManager;
import druid.interfaces.BasicModule;
import druid.interfaces.CodeGenModule;
import druid.interfaces.DatabaseIOModule;
import druid.interfaces.DatadictGenModule;
import druid.interfaces.DocsGenModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.JdbcPanelModule;
import druid.interfaces.RecordEditorModule;
import druid.interfaces.RecordIOModule;
import druid.interfaces.SqlAdapter;
import druid.interfaces.SqlGenModule;
import druid.interfaces.SummaryGenModule;
import druid.interfaces.TemplateGenModule;
import druid.interfaces.TreeNodeModule;
import druid.util.gui.renderers.ModuleViewRenderer;

//==============================================================================

public class ModuleView extends JPanel
{
	private TreeView tree = new TreeView();

	//---------------------------------------------------------------------------

	public ModuleView(TreeViewSelListener sl)
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(sl);
		tree.setEditable(false);
		tree.setCellRenderer(new ModuleViewRenderer());
		tree.setRootNode(getModuleTree());
		tree.getRootNode().expand(true, 1);

		//--- setup panel

		add("0,0,x,x", tree);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Setup tree struct
	//---
	//---------------------------------------------------------------------------

	private TreeViewNode getModuleTree()
	{
		TreeViewNode gen  = new TreeViewNode("<html><b>DATA GENERATION</b>", ModuleViewRenderer.DATAGEN);
		TreeViewNode db   = new TreeViewNode("<html><b>DATABASE</b>",        ModuleViewRenderer.DATABASE);
		TreeViewNode jdbc = new TreeViewNode("<html><b>JDBC ACCESS</b>",     ModuleViewRenderer.JDBC);
		TreeViewNode tn   = new TreeViewNode("<html><b>POPUP MENU</b>",      ModuleViewRenderer.TREENODE);

		addDataGenModules(gen);
		addDbModules(db);
		addJdbcModules(jdbc);
		addTreeNodeModules(tn);

		//--- setup and return root node

		TreeViewNode root = new TreeViewNode();

		root.addChild(gen);
		root.addChild(db);
		root.addChild(jdbc);
		root.addChild(tn);

		return root;
	}

	//---------------------------------------------------------------------------

	private void addDataGenModules(TreeViewNode parent)
	{
		TreeViewNode dd   = new TreeViewNode("Data Dictionary");
		TreeViewNode docs = new TreeViewNode("Docs");
		TreeViewNode summ = new TreeViewNode("Summary");
		TreeViewNode code = new TreeViewNode("Code");
		TreeViewNode sql  = new TreeViewNode("Sql script");
		TreeViewNode temp = new TreeViewNode("From Templates");
		TreeViewNode gen  = new TreeViewNode("Generic");

		//--- add modules

		addModules(dd,   DatadictGenModule.class);
		addModules(docs, DocsGenModule.class);
		addModules(summ, SummaryGenModule.class);
		addModules(code, CodeGenModule.class);
		addModules(sql,  SqlGenModule.class);
		addModules(temp, TemplateGenModule.class);
		addModules(gen,  GenericGenModule.class);

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

	private void addDbModules(TreeViewNode parent)
	{
		TreeViewNode io = new TreeViewNode("Import / Export");

		//--- add modules

		addModules(io, DatabaseIOModule.class);

		//--- setup sub-tree

		parent.addChild(io);
	}

	//---------------------------------------------------------------------------

	private void addJdbcModules(TreeViewNode parent)
	{
		TreeViewNode io  = new TreeViewNode("Data Import / Export");
		TreeViewNode rec = new TreeViewNode("Record Editor");
		TreeViewNode pan = new TreeViewNode("Db Panels");
		TreeViewNode sql = new TreeViewNode("Sql Adapters");

		//--- add modules

		addModules(io,  RecordIOModule.class);
		addModules(rec, RecordEditorModule.class);
		addModules(pan, JdbcPanelModule.class);
		addModules(sql, SqlAdapter.class);

		//--- setup sub-tree

		parent.addChild(io);
		parent.addChild(rec);
		parent.addChild(pan);
		parent.addChild(sql);
	}

	//---------------------------------------------------------------------------

	private void addTreeNodeModules(TreeViewNode parent)
	{
		addModules(parent, TreeNodeModule.class);
	}

	//---------------------------------------------------------------------------

	private void addModules(TreeViewNode parent, Class c)
	{
		for(Enumeration e=ModuleManager.getModules(c); e.hasMoreElements();)
		{
			BasicModule mod = (BasicModule) e.nextElement();

			parent.addChild(new TreeViewNode(mod.getId(), mod));
		}
	}
}

//==============================================================================
