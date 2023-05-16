//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.constraints;

import druid.core.DataModel;
import druid.core.jdbc.entities.ConstraintEntity;
import druid.data.Constraint;
import javax.swing.JPanel;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================
//--- constraints support

public class WorkPanel extends MultiPanel implements DataModel
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank",    new JPanel());
		add("constraint",  genPanel);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		if (node instanceof ConstraintEntity && !node.isRoot())
		{
			Constraint constraint = ((ConstraintEntity) node).constraint;

			genPanel.refresh(constraint);
			show("constraint");
		}

		else
			show("blank");
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node) {}
}

//==============================================================================
