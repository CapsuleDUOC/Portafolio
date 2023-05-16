//==============================================================================
//===
//===   DatatypesPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.datatypes;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import druid.data.DatabaseNode;

//==============================================================================

public class DatatypesPanel extends JPanel
{
	DatatypesView dataView  = new DatatypesView();
	WorkPanel     workPanel = new WorkPanel();

	//---------------------------------------------------------------------------

	public DatatypesPanel()
	{
		dataView.setDataModel(workPanel);

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataView, workPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		dataView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		setLayout(new BorderLayout());
		add(p, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Refresh / Store methods
	//---
	//---------------------------------------------------------------------------

	//--- we must reset all because if a new db is selected, datatypes
	//--- refer to the previous one

	public void refresh(DatabaseNode dbNode)
	{
		dataView.setDatabaseNode(dbNode);

		dbNode.dataTypes.expand(true, 1);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataView.saveDataToNode();
	}
}

//==============================================================================
