//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate.panels.database;

import mod.datagen.generic.hibernate.DatabaseSettings;
import javax.swing.JTabbedPane;

//==============================================================================

public class DatabasePanel extends JTabbedPane
{
	private DatabaseOptions panDB    = new DatabaseOptions();
	private TableOptions    panTable = new TableOptions();
	private FieldOptions    panField = new FieldOptions();

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("General", panDB);
		addTab("Table",   panTable);
		addTab("Field",   panField);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panDB   .refresh(s);
		panTable.refresh(s);
		panField.refresh(s);
	}
}

//==============================================================================
