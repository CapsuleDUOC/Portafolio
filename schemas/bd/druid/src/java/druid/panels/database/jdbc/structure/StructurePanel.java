//==============================================================================
//===
//===   StructurePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;

//==============================================================================

public class StructurePanel extends JPanel
{
	private StructureView structView = new StructureView();
	private WorkPanel     workPanel  = new WorkPanel();

	//---------------------------------------------------------------------------

	public StructurePanel()
	{
		structView.setDataModel(workPanel);

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, structView, workPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(180);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		structView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		add("0,0,x,x", p);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode dbNode, boolean force)
	{
		structView.refresh(dbNode, force);
	}
}

//==============================================================================
