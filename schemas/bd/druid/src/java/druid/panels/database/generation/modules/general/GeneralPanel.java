//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.modules.general;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;
import druid.data.ModulesConfig;
import druid.interfaces.DataGenModule;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private OutputPanel   outPanel  = new OutputPanel();
	private SpecificPanel specPanel = new SpecificPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x",   outPanel);
		add("0,1,x,x", specPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentModule(DataGenModule mod, DatabaseNode node)
	{
		ModulesConfig mc = node.modsConfig;

		outPanel.setOutput(mc.getValue(mod, "output"));
		outPanel.setDirectoryBased(mod.isDirectoryBased());

		specPanel.refresh(mod, node);
	}

	//---------------------------------------------------------------------------

	public void saveDataToModule(DataGenModule mod, ModulesConfig mc)
	{
		mc.setValue(mod, "output", outPanel.getOutput());
	}
}

//==============================================================================
