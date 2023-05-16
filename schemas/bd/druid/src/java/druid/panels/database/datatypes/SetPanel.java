//==============================================================================
//===
//===   SetPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.datatypes;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.core.AttribList;
import druid.util.gui.DataEntryPanel;

//==============================================================================

public class SetPanel extends TPanel
{
	private DataEntryPanel dataPanel = new DataEntryPanel();

	//---------------------------------------------------------------------------

	public SetPanel()
	{
		super("Set");

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x",dataPanel);

		dataPanel.addAttrib("value", "Values", 100);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribList al)
	{
		dataPanel.setAttribList(al);
	}
}

//==============================================================================
