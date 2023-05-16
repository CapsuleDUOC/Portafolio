//==============================================================================
//===
//===   GenerationPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation;

import org.dlib.gui.TTabbedPane;

import druid.data.DatabaseNode;
import druid.panels.database.generation.modules.ModulePanel;
import druid.panels.database.generation.options.OptionsPanel;

//==============================================================================

public class GenerationPanel extends TTabbedPane
{
	private OptionsPanel optPanel = new OptionsPanel();
	private ModulePanel  modPanel = new ModulePanel();

	//---------------------------------------------------------------------------

	public GenerationPanel()
	{
		add("Options", optPanel);
		add("Modules", modPanel);

		modPanel.setOptionsPanel(this);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode dbNode)
	{
		optPanel.refresh(dbNode.attrSet);
		modPanel.refresh(dbNode);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(DatabaseNode dbNode)
	{
		optPanel.saveDataToNode(dbNode.attrSet);
		modPanel.saveDataToNode();
	}
}

//==============================================================================
