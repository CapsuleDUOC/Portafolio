//==============================================================================
//===
//===   UDTPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.udt;

import org.dlib.gui.TTabbedPane;

import druid.core.jdbc.entities.UDTEntity;

//==============================================================================

public class UDTPanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public UDTPanel()
	{
		addTab("General", genPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(UDTEntity node)
	{
		genPanel.refresh(node);
	}
}

//==============================================================================
