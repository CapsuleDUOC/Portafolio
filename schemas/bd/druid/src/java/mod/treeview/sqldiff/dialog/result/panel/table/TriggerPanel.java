//==============================================================================
//===
//===   TriggerPanel
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

import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;

//==============================================================================

public class TriggerPanel extends MultiPanel implements TreeViewSelListener
{
	private TreeView         treeView  = new TreeView();
	private TriggerInfoPanel infoPanel = new TriggerInfoPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TriggerPanel()
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
		Vector vTriggers = new Vector();

		//--- collect all triggers that belong to this table

		java.util.List list = diffSumm.list(DiffSummary.TRIGGER);

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity entTrig = (DiffEntity) list.get(i);

			if (entTrig.getParent() == ent)
				vTriggers.add(entTrig);
		}

		//--- is there any trigger ?

		if (vTriggers.size() != 0)
		{
			//--- ok, we have to populate the treeview

			TreeViewNode rootNode = new TreeViewNode();

			for(int i=0; i<vTriggers.size(); i++)
			{
				DiffEntity entTrig = (DiffEntity) vTriggers.get(i);

				NodeInfo info = new NodeInfo(DiffSummary.TRIGGER, entTrig);

				rootNode.addChild(new TreeViewNode(entTrig.getName(), info));
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

class TriggerInfoPanel extends MultiPanel
{
	private TTabbedPane tabPane   = new TTabbedPane();
	private AttribPanel panGeneral= new AttribPanel();
	private SqlPanel    panCode   = new SqlPanel();

	private TriggerActivationDecoder trigAct = new TriggerActivationDecoder();
	private TriggerForEachDecoder    trigFor = new TriggerForEachDecoder();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TriggerInfoPanel()
	{
		tabPane.addTab("General", panGeneral);
		tabPane.addTab("Code",    panCode);

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
			panGeneral.setAttrib(ent.get(DiffEntity.TR_ACTIVATION), "Activation", trigAct);
			panGeneral.setAttrib(ent.get(DiffEntity.TR_ONINSERT),   "On insert");
			panGeneral.setAttrib(ent.get(DiffEntity.TR_ONUPDATE),   "On update");
			panGeneral.setAttrib(ent.get(DiffEntity.TR_ONDELETE),   "On delete");
			panGeneral.setAttrib(ent.get(DiffEntity.TR_FOREACH),    "For each",   trigFor);
			panGeneral.setAttrib(ent.get(DiffEntity.TR_WHEN),       "When");
			tabPane.setEnabledAt(0, panGeneral.updatePanel());

			tabPane.setEnabledAt(1, panCode.setText(ent.get(DiffEntity.TR_CODE)));

			show("info");

			DiffUtil.showTab(tabPane);
		}
	}
}

//==============================================================================
