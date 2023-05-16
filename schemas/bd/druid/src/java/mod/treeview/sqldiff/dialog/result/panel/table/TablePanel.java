//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.table;

import mod.treeview.sqldiff.dialog.result.panel.DiffUtil;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class TablePanel extends TTabbedPane
{
	private TriggerPanel panTriggers = new TriggerPanel();
	private RulePanel    panRules    = new RulePanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TablePanel()
	{
		addTab("Triggers", panTriggers);
		addTab("Rules",    panRules);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(DiffSummary diffSumm, DiffEntity ent)
	{
		setEnabledAt(0, panTriggers.setCurrentNode(diffSumm, ent));
		setEnabledAt(1, panRules.setCurrentNode(diffSumm, ent));

		DiffUtil.showTab(this);
	}
}

//==============================================================================
