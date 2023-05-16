//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.triggers;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.jdbc.entities.TriggerEntity;
import druid.data.Trigger;

//==============================================================================

public class WorkPanel extends MultiPanel implements DataModel
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank",    new JPanel());
		add("trigger",  genPanel);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		if (node instanceof TriggerEntity && !node.isRoot())
		{
			Trigger trigger = ((TriggerEntity) node).trigger;

			genPanel.refresh(trigger);

			show("trigger");
		}

		else
			show("blank");
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node) {}
}

//==============================================================================
