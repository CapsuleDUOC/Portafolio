//==============================================================================
//===
//===   ModuleOptPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.modules;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.DruidException;
import druid.data.DatabaseNode;
import druid.interfaces.BasicModule;
import druid.interfaces.DataGenModule;
import druid.panels.database.generation.modules.general.GeneralPanel;

//==============================================================================

class ModuleOptPanel extends MultiPanel implements DataModel
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------

	public ModuleOptPanel()
	{
		//--- setup this multi panel

		add(new JPanel(), "blank");
		add(genPanel,     "info");
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		DataTracker.beginDisabledSection();

		if (node == null || node.getUserData() instanceof String)
		{
			show("blank");
		}
		else
		{
			BasicModule mod = (BasicModule) node.getUserData();

			//--- the root node contains the database node
			//--- this is a bit ugly but keeps the architecture clean

			DatabaseNode dbNode = (DatabaseNode) ((TreeViewNode) node.getRoot()).getUserData();

			if (mod instanceof DataGenModule)
			{
				show("info");

				DataGenModule dgMod = (DataGenModule) mod;

				genPanel.setCurrentModule(dgMod,  dbNode);
			}
			else
			{
				throw new DruidException(DruidException.INC_STR, "Unknown module instance", mod);
			}
		}

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node == null || node.getUserData() instanceof String)
			return;

		BasicModule mod = (BasicModule) node.getUserData();

		if (mod instanceof DataGenModule)
		{
			DataGenModule dgMod = (DataGenModule) mod;

			//--- the root node contains the database node
			//--- this is a bit ugly but keeps the architecture clean

			DatabaseNode dbNode = (DatabaseNode) ((TreeViewNode) node.getRoot()).getUserData();

			genPanel.saveDataToModule(dgMod,  dbNode.modsConfig);
		}
		else
		{
			throw new DruidException(DruidException.INC_STR, "Unknown module instance", mod);
		}
	}
}

//==============================================================================
