//==============================================================================
//===
//===   IncludePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.util.gui.DataEntryPanel;
import mod.datagen.generic.castor.DatabaseSettings;

//==============================================================================

public class IncludePanel extends JPanel
{
	private DataEntryPanel panIncludes = new DataEntryPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public IncludePanel()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panIncludes);

		panIncludes.addAttrib("include", "Include", 250);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panIncludes.setAttribList(s.getIncludes());
	}
}

//==============================================================================
