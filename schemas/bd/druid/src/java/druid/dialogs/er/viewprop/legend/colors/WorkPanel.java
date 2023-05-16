//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop.legend.colors;


import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.er.LegendColor;

//==============================================================================

public class WorkPanel extends MultiPanel implements DataModel
{
	private RightPanel rightPanel = new RightPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank", new JPanel());
		add("color", rightPanel);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		DataTracker.beginDisabledSection();

		if (node instanceof LegendColor)
		{
			rightPanel.refresh((LegendColor)node);
			show("color");
		}

		else show("blank");

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node instanceof LegendColor)
			rightPanel.saveDataToNode((LegendColor)node);
	}
}

//==============================================================================
