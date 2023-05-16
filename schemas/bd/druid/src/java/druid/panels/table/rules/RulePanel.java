//==============================================================================
//===
//===   RulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.rules;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TSplitPane;

import druid.data.TableNode;

//==============================================================================

public class RulePanel extends JPanel
{
	private RuleView  ruleView  = new RuleView();
	private WorkPanel workPanel = new WorkPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public RulePanel()
	{
		FlexLayout flexL = new FlexLayout(1, 1, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		ruleView.setDataModel(workPanel);

		add("0,0,x,x", new TSplitPane(ruleView, workPanel));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableNode tableNode)
	{
		ruleView.setRuleNode(tableNode.rules);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		ruleView.saveDataToNode();
	}
}

//==============================================================================
