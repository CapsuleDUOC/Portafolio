//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.database;

import javax.swing.JTabbedPane;

import mod.datagen.generic.castor.DatabaseSettings;

//==============================================================================

public class DatabasePanel extends JTabbedPane
{
	private GeneralPanel panGeneral  = new GeneralPanel();
	private IncludePanel panIncludes = new IncludePanel();
	private KeyGenPanel  panKeyGen   = new KeyGenPanel();

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("General",  panGeneral);
		addTab("Includes", panIncludes);
		addTab("Key gens", panKeyGen);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panGeneral .refresh(s);
		panIncludes.refresh(s);
		panKeyGen  .refresh(s);
	}
}

//==============================================================================
