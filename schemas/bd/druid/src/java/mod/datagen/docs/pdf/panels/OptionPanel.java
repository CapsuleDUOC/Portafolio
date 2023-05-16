//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.panels;

import mod.datagen.docs.pdf.Settings;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class OptionPanel extends TTabbedPane
{
	private GeneralPanel     genPanel    = new GeneralPanel();
	private FontPanel        fontPanel   = new FontPanel();
	private FontMappingPanel fontMapping = new FontMappingPanel();
	private MarginsPanel     margPanel   = new MarginsPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		addTab("General", genPanel);
		addTab("Fonts",   fontPanel);
		addTab("Mapping", fontMapping);
		addTab("Margins", margPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		genPanel   .refresh(s);
		fontPanel  .refresh(s);
		fontMapping.refresh(s);
		margPanel  .refresh(s);
	}
}

//==============================================================================
