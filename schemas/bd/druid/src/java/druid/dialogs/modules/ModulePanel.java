//==============================================================================
//===
//===   ModulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.modules;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.MultiPanel;

import druid.interfaces.BasicModule;

//==============================================================================

public class ModulePanel extends MultiPanel
{
	private GeneralPanel  genPanel   = new GeneralPanel();
	private SpecificPanel specPanel  = new SpecificPanel();

	//---------------------------------------------------------------------------

	public ModulePanel()
	{
		JPanel p = new JPanel();

		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x",   genPanel);
		p.add("0,1,x,x", specPanel);

		//--- setup this multi panel

		add(new JPanel(), "blank");
		add(p,            "info");
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(Object o)
	{
		if (o == null || o instanceof String)
		{
			show("blank");
		}
		else
		{
			show("info");

			BasicModule mod = (BasicModule) o;

			genPanel.setCurrentModule(mod);
			specPanel.setCurrentModule(mod);
		}
	}
}

//==============================================================================
