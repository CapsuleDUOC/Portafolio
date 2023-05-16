//==============================================================================
//===
//===   ModulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTabbedPane;

import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.interfaces.BasicModule;
import druid.interfaces.DataGenModule;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class ModulePanel extends JTabbedPane
{
	private int type;

	private Vector vModules = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ModulePanel(int type)
	{
		this.type = type;

		for(Enumeration e=ModuleManager.getAllModules(); e.hasMoreElements();)
		{
			BasicModule mod = (BasicModule) e.nextElement();

			ModuleOptions modOpt = mod.getModuleOptions(type);

			if (modOpt != null)
				vModules.add(mod);
		}
	}

	//---------------------------------------------------------------------------

	public void refresh(AbstractNode node)
	{
		int currTab = getSelectedIndex();

		removeAll();

		DatabaseNode dbNode = node.getDatabase();

		for(int i=0; i<vModules.size(); i++)
		{
			BasicModule   mod    = (BasicModule)   vModules.get(i);
			ModuleOptions modOpt = mod.getModuleOptions(type);

			if (mod instanceof DataGenModule)
			{
				DataGenModule dgMod = (DataGenModule) mod;

				if (dbNode.modsUsage.contains(mod))
				{
					addTab(dgMod.getFormat(), modOpt.getPanel());
					modOpt.refresh(node);
				}
			}
			else
			{
				addTab(mod.getId(), modOpt.getPanel());
				modOpt.refresh(node);
			}
		}

		if (currTab != -1 && currTab < getTabCount())
			setSelectedIndex(currTab);
	}
}

//==============================================================================
