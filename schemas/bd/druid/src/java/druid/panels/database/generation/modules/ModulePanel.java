//==============================================================================
//===
//===   ModulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.modules;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import druid.data.DatabaseNode;
import druid.panels.database.generation.GenerationPanel;

//==============================================================================

public class ModulePanel extends JPanel
{
	private ModuleView   modView  = new ModuleView();
	private ModuleOptPanel modPanel = new ModuleOptPanel();

	//---------------------------------------------------------------------------

	public ModulePanel()
	{
		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modView, modPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		modView.setMinimumSize(d);
		modPanel.setMinimumSize(d);

		setLayout(new BorderLayout());
		add(p, BorderLayout.CENTER);

		modView.setDataModel(modPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode node)
	{
		modView.setDatabaseNode(node);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		modView.saveDataToNode();
	}

	//---------------------------------------------------------------------------

	public void setOptionsPanel(GenerationPanel o)
	{
		modView.setOptionsPanel(o);
	}
}

//==============================================================================
