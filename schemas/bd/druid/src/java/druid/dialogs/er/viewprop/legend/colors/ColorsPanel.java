//==============================================================================
//===
//===   ColorsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop.legend.colors;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.dlib.gui.FlexLayout;

import druid.data.er.Legend;

//==============================================================================

public class ColorsPanel extends JPanel
{
	private ColorView colorView = new ColorView();
	private WorkPanel workPanel = new WorkPanel();

	//---------------------------------------------------------------------------

	public ColorsPanel()
	{
		colorView.setDataModel(workPanel);

		//------------------------------------------------------------------------
		//--- horizontal split

		JSplitPane hs = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorView, workPanel);
		hs.setOneTouchExpandable(true);
		hs.setDividerLocation(280);
		hs.setContinuousLayout(true);

		//------------------------------------------------------------------------
		//--- set min size

		Dimension d = new Dimension(100,50);

		colorView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		//------------------------------------------------------------------------
		//--- setp panel

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", hs);
	}

	//---------------------------------------------------------------------------

	public void refresh(Legend l)
	{
		colorView.setLegend(l);
	}

	//---------------------------------------------------------------------------

	public void store(Legend l)
	{
		workPanel.saveDataToNode(l);
	}
}

//==============================================================================
