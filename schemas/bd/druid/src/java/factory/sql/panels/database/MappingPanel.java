//==============================================================================
//===
//===   MappingPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.database;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.core.AttribList;
import druid.util.gui.DataEntryPanel;

//==============================================================================

public class MappingPanel extends TPanel
{
	private DataEntryPanel mappingPanel = new DataEntryPanel();

	//---------------------------------------------------------------------------

	public MappingPanel()
	{
		super("SqlTypes remapping");

		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x",  mappingPanel);

		//--- setup grid attribs

		mappingPanel.addAttrib("sqlType",    "Sql type",    50);
		mappingPanel.addAttrib("mappedType", "Mapped type", 50);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribList mapping)
	{
		mappingPanel.setAttribList(mapping);
	}
}

//==============================================================================
