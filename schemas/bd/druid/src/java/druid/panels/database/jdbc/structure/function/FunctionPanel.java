//==============================================================================
//===
//===   FunctionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.function;

import org.dlib.gui.TTabbedPane;

import druid.core.jdbc.entities.FunctionEntity;
import druid.util.gui.SqlTextArea;
import druid.util.jdbc.RecordListPanel;

//==============================================================================

public class FunctionPanel extends TTabbedPane
{
	private RecordListPanel paramPanel = new RecordListPanel();
	private SqlTextArea     txaCode    = new SqlTextArea();

	//---------------------------------------------------------------------------

	public FunctionPanel()
	{
		addTab("Parameters", paramPanel);
		addTab("Sql code",   txaCode);

		txaCode.setEditable(false);
	}

	//---------------------------------------------------------------------------

	public void refresh(FunctionEntity node)
	{
		paramPanel.refresh(node.rlParameters);
		txaCode.setText(node.sqlCode);
	}
}

//==============================================================================
