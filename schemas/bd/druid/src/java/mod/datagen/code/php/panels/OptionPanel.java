//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.php.panels;

import mod.datagen.code.php.Settings;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class OptionPanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		addTab("General", genPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		genPanel.refresh(s);
	}
}

//==============================================================================
