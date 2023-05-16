//==============================================================================
//===
//===   RulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.table;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import mod.treeview.sqldiff.dialog.result.NodeInfo;
import mod.treeview.sqldiff.dialog.result.TreeViewRenderer;
import mod.treeview.sqldiff.dialog.result.panel.AttribPanel;
import mod.treeview.sqldiff.dialog.result.panel.DiffUtil;
import mod.treeview.sqldiff.dialog.result.panel.SqlPanel;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

//==============================================================================

public class RulePanel extends MultiPanel implements TreeViewSelListener
{
	private TreeView      treeView  = new TreeView();
	private RuleInfoPanel infoPanel = new RuleInfoPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public RulePanel()
	{
		treeView.setEditable(false);
		treeView.setCellRenderer(new TreeViewRenderer());
		treeView.addSelectionListener(this);

		//------------------------------------------------------------------------

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, infoPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(150);
		sp.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		treeView.setMinimumSize(d);
		infoPanel.setMinimumSize(d);

		add("blank", new JPanel());
		add("info",  sp);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean setCurrentNode(DiffSummary diffSumm, DiffEntity ent)
	{
		Vector vRules = new Vector();

		//--- collect all rules that belong to this table

		java.util.List list = diffSumm.list(DiffSummary.TABLERULE);

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity entRule = (DiffEntity) list.get(i);

			if (entRule.getParent() == ent)
				vRules.add(entRule);
		}

		//--- is there any rule ?

		if (vRules.size() != 0)
		{
			//--- ok, we have to populate the treeview

			TreeViewNode rootNode = new TreeViewNode();

			for(int i=0; i<vRules.size(); i++)
			{
				DiffEntity entRule = (DiffEntity) vRules.get(i);

				NodeInfo info = new NodeInfo(DiffSummary.TABLERULE, entRule);

				rootNode.addChild(new TreeViewNode(entRule.getName(), info));
			}

			treeView.setRootNode(rootNode);
			infoPanel.setCurrentNode(null);

			show("info");
			return true;
		}
		else
		{
			show("blank");
			return false;
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		TreeViewNode selNode = e.getSelectedNode();

		if (selNode == null)
			infoPanel.setCurrentNode(null);
		else
			infoPanel.setCurrentNode((NodeInfo) selNode.getUserData());
	}
}

//==============================================================================

class RuleInfoPanel extends MultiPanel
{
	private TTabbedPane tabPane   = new TTabbedPane();
	private AttribPanel panGeneral= new AttribPanel();
	private SqlPanel    panCode   = new SqlPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public RuleInfoPanel()
	{
		tabPane.addTab("General", panGeneral);
		tabPane.addTab("Rule",    panCode);

		add("blank", new JPanel());
		add("info",  tabPane);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(NodeInfo info)
	{
		if (info == null)
			show("blank");
		else
		{
			DiffEntity ent = info.deEntity;

			panGeneral.initAttribs();
			panGeneral.setAttrib(ent.get(DiffEntity.RU_USE), "In use");
			tabPane.setEnabledAt(0, panGeneral.updatePanel());

			tabPane.setEnabledAt(1, panCode.setText(ent.get(DiffEntity.RU_RULE)));

			show("info");

			DiffUtil.showTab(tabPane);
		}
	}
}

//==============================================================================
