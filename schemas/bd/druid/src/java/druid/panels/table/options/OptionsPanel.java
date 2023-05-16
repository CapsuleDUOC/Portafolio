//==============================================================================
//===
//===   OptionsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.options;

import javax.swing.JTabbedPane;

import druid.data.TableNode;
import druid.interfaces.BasicModule;
import druid.panels.table.options.general.GeneralPanel;
import druid.util.gui.ModulePanel;

//==============================================================================

public class OptionsPanel extends JTabbedPane
{
	private GeneralPanel panGeneral = new GeneralPanel();
	private ModulePanel  panModule  = new ModulePanel(BasicModule.TABLE);

	//---------------------------------------------------------------------------

	public OptionsPanel()
	{
		addTab("General", panGeneral);
		addTab("Modules", panModule);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableNode tableNode)
	{
		panGeneral.refresh(tableNode);
		panModule.refresh(tableNode);
	}
	
	//---------------------------------------------------------------------------

	public void saveDataToNode(TableNode node)
	{
		panGeneral.saveDataToNode(node);
	}
}

//==============================================================================
