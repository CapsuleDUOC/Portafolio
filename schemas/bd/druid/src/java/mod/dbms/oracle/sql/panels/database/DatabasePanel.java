//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql.panels.database;

import javax.swing.JTabbedPane;

import mod.dbms.oracle.sql.DatabaseSettings;
import factory.sql.panels.database.AltDropPanel;
import factory.sql.panels.database.ContainerPanel;

//==============================================================================

public class DatabasePanel extends JTabbedPane
{
	private ContainerPanel  panGeneral     = new ContainerPanel();
	private TablespacePanel panTablespaces = new TablespacePanel();
	private AltDropPanel    panAltDrop     = new AltDropPanel();

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("General",         panGeneral);
		addTab("Tablespaces",     panTablespaces);
		addTab("Alternate Drops", panAltDrop);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panGeneral    .refresh(s);
		panTablespaces.refresh(s);
		panAltDrop    .refresh(s);
	}
}

//==============================================================================
