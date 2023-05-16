//==============================================================================
//===
//===   ErPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.er;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;

//==============================================================================

public class ErDesigner extends JPanel
{
	private ErWorkPanel workPanel    = new ErWorkPanel();
	private ErTreeView  treeView   = new ErTreeView();

	//---------------------------------------------------------------------------

	public ErDesigner()
	{
		//------------------------------------------------------------------------
		//--- horizontal split

		JSplitPane hs = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, workPanel);
		hs.setOneTouchExpandable(true);
		hs.setDividerLocation(230);
		hs.setContinuousLayout(true);

		//------------------------------------------------------------------------
		//--- set min size

		Dimension d = new Dimension(100,50);

		workPanel.setMinimumSize(d);
		treeView.setMinimumSize(d);

		//------------------------------------------------------------------------
		//--- setp panel

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", hs);

		treeView.setDataModel(workPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode node)
	{
		treeView.setDatabaseNode(node);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		treeView.saveDataToNode();
	}
}

//==============================================================================
