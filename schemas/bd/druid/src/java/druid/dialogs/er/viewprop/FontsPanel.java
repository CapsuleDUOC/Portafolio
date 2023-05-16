//==============================================================================
//===
//===   FontsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.data.er.ErView;
import druid.util.gui.FontPanel;

//==============================================================================

public class FontsPanel extends JPanel
{
	private FontPanel nameFontPan   = new FontPanel("Entity name");
	private FontPanel fieldsFontPan = new FontPanel("Fields");

	//---------------------------------------------------------------------------

	public FontsPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 2, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", nameFontPan);
		add("0,1,x,x", fieldsFontPan);
	}

	//---------------------------------------------------------------------------

	public void refresh(ErView erView)
	{
		nameFontPan.refresh("name",     erView.attrSet);
		fieldsFontPan.refresh("fields", erView.attrSet);
	}

	//---------------------------------------------------------------------------

	public void store(ErView erView)
	{
		nameFontPan.store("name",     erView.attrSet);
		fieldsFontPan.store("fields", erView.attrSet);
	}
}

//==============================================================================
