//==============================================================================
//===
//===   TriggerPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.triggers;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TSplitPane;

import druid.data.TableNode;

//==============================================================================

public class TriggerPanel extends JPanel
{
	private TriggerView triggerView = new TriggerView();
	private WorkPanel   workPanel   = new WorkPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TriggerPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 1, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		triggerView.setDataModel(workPanel);

		add("0,0,x,x", new TSplitPane(triggerView, workPanel));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableNode tableNode)
	{
		triggerView.setTriggerNode(tableNode.triggers);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		triggerView.saveDataToNode();
	}
}

//==============================================================================
