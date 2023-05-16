//==============================================================================
//===
//===   OptionsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.field.options;

import javax.swing.JTabbedPane;

import druid.data.FieldNode;
import druid.interfaces.BasicModule;
import druid.panels.field.options.general.GeneralPanel;
import druid.util.gui.ModulePanel;

//==============================================================================

public class OptionsPanel extends JTabbedPane
{
	private GeneralPanel panGeneral = new GeneralPanel();
	private ModulePanel  panModule  = new ModulePanel(BasicModule.FIELD);

	//---------------------------------------------------------------------------

	public OptionsPanel()
	{
		addTab("General", panGeneral);
		addTab("Modules", panModule);
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldNode node)
	{
		panGeneral.refresh(node);
		panModule.refresh(node);
	}
}

//==============================================================================
