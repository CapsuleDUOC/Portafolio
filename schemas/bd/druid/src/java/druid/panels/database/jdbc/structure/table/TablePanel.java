//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table;

import druid.core.jdbc.entities.TableEntity;
import druid.panels.database.jdbc.structure.RecordBasedPanel;
import druid.panels.database.jdbc.structure.table.constraints.ConstraintPanel;
import druid.panels.database.jdbc.structure.table.triggers.TriggerPanel;

//==============================================================================

public class TablePanel extends RecordBasedPanel
{
	private TriggerPanel    trigPanel       = new TriggerPanel();
	private ConstraintPanel constraintPanel = new ConstraintPanel();

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		addTab("Triggers",    trigPanel);
		addTab("Constraints", constraintPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableEntity node)
	{
		super.refresh(node);

		trigPanel      .refresh(node);
		constraintPanel.refresh(node);
	}
}

//==============================================================================
