//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.java.panels;

import mod.datagen.code.java.Settings;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class OptionPanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();
	private JdoPanel     jdoPanel = new JdoPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		addTab("General",      genPanel);
		addTab("JDO Settings", jdoPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		genPanel.refresh(s);
		jdoPanel.refresh(s);
	}
}

//==============================================================================
