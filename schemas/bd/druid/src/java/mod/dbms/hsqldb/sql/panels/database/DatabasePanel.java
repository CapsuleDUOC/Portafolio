//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.hsqldb.sql.panels.database;

import javax.swing.JTabbedPane;

import factory.sql.BasicDatabaseSettings;
import factory.sql.panels.database.AltDropPanel;
import factory.sql.panels.database.ContainerPanel;

//==============================================================================

public class DatabasePanel extends JTabbedPane
{
	private ContainerPanel panGeneral = new ContainerPanel();
	private AltDropPanel   panAltDrop = new AltDropPanel();

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("General",         panGeneral);
		addTab("Alternate Drops", panAltDrop);
	}

	//---------------------------------------------------------------------------

	public void refresh(BasicDatabaseSettings s)
	{
		panGeneral.refresh(s);
		panAltDrop.refresh(s);
	}
}

//==============================================================================
