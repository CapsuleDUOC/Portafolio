//==============================================================================
//===
//===   ViewPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.view;

import druid.core.jdbc.entities.ViewEntity;
import druid.panels.database.jdbc.structure.RecordBasedPanel;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class ViewPanel extends RecordBasedPanel
{
	private SqlTextArea txaCode = new SqlTextArea();

	//---------------------------------------------------------------------------

	public ViewPanel()
	{
		addTab("Sql code", txaCode);

		txaCode.setEditable(false);
	}

	//---------------------------------------------------------------------------

	public void refresh(ViewEntity node)
	{
		super.refresh(node);

		txaCode.setText(node.sqlCode);
	}
}

//==============================================================================
