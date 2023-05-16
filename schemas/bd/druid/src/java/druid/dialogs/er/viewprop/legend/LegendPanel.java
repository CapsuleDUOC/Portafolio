//==============================================================================
//===
//===   LegendPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop.legend;

import org.dlib.gui.TTabbedPane;

import druid.data.er.Legend;
import druid.dialogs.er.viewprop.legend.colors.ColorsPanel;
import druid.util.gui.FontPanel;

//==============================================================================

public class LegendPanel extends TTabbedPane
{
	private GeneralPanel genPanel  = new GeneralPanel();
	private FontPanel    fontPanel = new FontPanel("Legend font");
	private ColorsPanel  colPanel  = new ColorsPanel();

	//---------------------------------------------------------------------------

	public LegendPanel()
	{
		addTab("General", genPanel);
		addTab("Colors",  colPanel);
		addTab("Fonts",   fontPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(Legend l)
	{
		genPanel.refresh(l);
		colPanel.refresh(l);
		fontPanel.refresh("font", l.attrSet);
	}

	//---------------------------------------------------------------------------

	public void store(Legend l)
	{
		genPanel.store(l);
		colPanel.store(l);
		fontPanel.store("font", l.attrSet);
	}
}

//==============================================================================
