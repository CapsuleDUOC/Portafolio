//==============================================================================
//===
//===   ResultDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JSplitPane;

import mod.treeview.sqldiff.dialog.result.panel.ResultPanel;
import mod.treeview.sqldiff.report.script.MigrationScript;
import mod.treeview.sqldiff.report.text.TextReport;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;
import mod.treeview.sqldiff.struct.Differ;

import org.dlib.gui.TDialog;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.TTextArea;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.data.DatabaseNode;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class ResultDialog extends TDialog implements TreeViewSelListener
{
	private DatabaseNode oldDB;
	private DatabaseNode newDB;

	private DiffSummary diffSumm;

	private TreeView    treeView      = new TreeView();
	private ResultPanel resultPanel   = new ResultPanel();
	private TTextArea   txaTextDiff   = new TTextArea(18, 54);
	private SqlTextArea txaMigrScript = new SqlTextArea();

	private TTabbedPane tabPane       = new TTabbedPane();

	private static Font font = new Font("monospaced", Font.PLAIN, 12);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ResultDialog(Frame frame)
	{
		super(frame, "Diff results", true);

		treeView.setEditable(false);
		treeView.setCellRenderer(new TreeViewRenderer());
		treeView.addSelectionListener(this);

		txaTextDiff.setEditable(false);
		txaTextDiff.setFont(font);
		txaMigrScript.setEditable(false);

		//------------------------------------------------------------------------

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, resultPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		sp.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		treeView.setMinimumSize(d);
		resultPanel.setMinimumSize(d);

		//------------------------------------------------------------------------

		tabPane.addTab("Result",           sp);
		tabPane.addTab("Text diff",        txaTextDiff);
		tabPane.addTab("Migration script", txaMigrScript);

		tabPane.setPreferredSize(new Dimension(750, 550));

		getContentPane().add(tabPane, BorderLayout.CENTER);

		MenuHandler menu = new MenuHandler(this);
		setJMenuBar(menu.menuBar);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setDatabases(DatabaseNode oldDB, DatabaseNode newDB)
	{
		this.oldDB = oldDB;
		this.newDB = newDB;
	}

	//---------------------------------------------------------------------------

	public boolean diff(boolean invert)
	{
		if (!invert)
			diffSumm = Differ.diff(oldDB, newDB);
		else
			diffSumm = Differ.diff(newDB, oldDB);

		treeView.setRootNode(buildTree(diffSumm));
		treeView.getRootNode().expand(true, 2);

		resultPanel.setCurrentNode(diffSumm, null);
		tabPane.setSelectedIndex(0);

		txaTextDiff.setText(new TextReport().build(diffSumm));
		txaMigrScript.setText(new MigrationScript().build(diffSumm));

		return diffSumm.isEmpty();
	}

	//---------------------------------------------------------------------------

	public String getTextDiff()   { return txaTextDiff.getText();   }
	public String getMigrScript() { return txaMigrScript.getText(); }

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		TreeViewNode selNode = e.getSelectedNode();

		if (selNode == null)
			resultPanel.setCurrentNode(diffSumm, null);
		else
			resultPanel.setCurrentNode(diffSumm, (NodeInfo) selNode.getUserData());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private TreeViewNode buildTree(DiffSummary diffSumm)
	{
		TreeViewNode rootNode = new TreeViewNode();

		buildDatabase(diffSumm, rootNode);
		buildTables(diffSumm, rootNode);
		buildViews(diffSumm, rootNode);
		buildProcedures(diffSumm, rootNode);
		buildFunctions(diffSumm, rootNode);
		buildSequences(diffSumm, rootNode);

		return rootNode;
	}

	//---------------------------------------------------------------------------

	private void buildDatabase(DiffSummary diffSumm, TreeViewNode parent)
	{
		java.util.List list = diffSumm.list(DiffSummary.DATABASE);

		if (list.size() != 0)
		{
			DiffEntity   ent  = (DiffEntity) list.get(0);
			NodeInfo     info = new NodeInfo(DiffSummary.DATABASE, ent);
			TreeViewNode node = new TreeViewNode("Database", info);

			parent.addChild(node);
		}
	}

	//---------------------------------------------------------------------------

	private void buildTables(DiffSummary diffSumm, TreeViewNode parent)
	{
		TreeViewNode node = new TreeViewNode("Tables");

		java.util.List tableList = diffSumm.list(DiffSummary.TABLE);

		for(int i=0; i<tableList.size(); i++)
		{
			DiffEntity tableEnt = (DiffEntity) tableList.get(i);

			NodeInfo     info  = new NodeInfo(DiffSummary.TABLE, tableEnt);
			TreeViewNode table = new TreeViewNode(tableEnt.getName(), info);

			node.addChild(table);

			//--- handle fields

			java.util.List fieldList = diffSumm.list(DiffSummary.FIELD);

			for(int j=0; j<fieldList.size(); j++)
			{
				DiffEntity fieldEnt = (DiffEntity) fieldList.get(j);

				if (fieldEnt.getParent() == tableEnt)
				{
					             info  = new NodeInfo(DiffSummary.FIELD, fieldEnt);
					TreeViewNode field = new TreeViewNode(fieldEnt.getName(), info);

					table.addChild(field);
				}
			}
		}

		node.sortChildren();

		if (node.getChildCount() != 0)
			parent.addChild(node);
	}

	//---------------------------------------------------------------------------

	private void buildViews(DiffSummary diffSumm, TreeViewNode parent)
	{
		TreeViewNode node = new TreeViewNode("Views");

		buildEntity(node, diffSumm, DiffSummary.VIEW);

		if (node.getChildCount() != 0)
			parent.addChild(node);
	}

	//---------------------------------------------------------------------------

	private void buildProcedures(DiffSummary diffSumm, TreeViewNode parent)
	{
		TreeViewNode node = new TreeViewNode("Procedures");

		buildEntity(node, diffSumm, DiffSummary.PROCEDURE);

		if (node.getChildCount() != 0)
			parent.addChild(node);
	}

	//---------------------------------------------------------------------------

	private void buildFunctions(DiffSummary diffSumm, TreeViewNode parent)
	{
		TreeViewNode node = new TreeViewNode("Functions");

		buildEntity(node, diffSumm, DiffSummary.FUNCTION);

		if (node.getChildCount() != 0)
			parent.addChild(node);
	}

	//---------------------------------------------------------------------------

	private void buildSequences(DiffSummary diffSumm, TreeViewNode parent)
	{
		TreeViewNode node = new TreeViewNode("Sequences");

		buildEntity(node, diffSumm, DiffSummary.SEQUENCE);

		if (node.getChildCount() != 0)
			parent.addChild(node);
	}

	//---------------------------------------------------------------------------

	private void buildEntity(TreeViewNode parent, DiffSummary diffSumm, int catalog)
	{
		java.util.List list = diffSumm.list(catalog);

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity ent = (DiffEntity) list.get(i);

			NodeInfo     info = new NodeInfo(catalog, ent);
			TreeViewNode node = new TreeViewNode(ent.getName(), info);

			parent.addChild(node);
		}

		parent.sortChildren();
	}
}

//==============================================================================
