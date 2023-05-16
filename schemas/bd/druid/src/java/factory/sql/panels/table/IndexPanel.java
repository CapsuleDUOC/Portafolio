//==============================================================================
//===
//===   IndexPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.table;

import java.awt.Dimension;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.core.AttribList;
import factory.sql.panels.AttribListEditor;

//==============================================================================

public class IndexPanel extends TPanel
{
	private AttribListEditor indexEditor = new AttribListEditor();

	//---------------------------------------------------------------------------

	public IndexPanel()
	{
		super("Indexes");

		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x",  indexEditor);

		//--- setup grid attribs

		indexEditor.addAttrib("index",  "Index",   30, false);
		indexEditor.addAttrib("name",   "Name",   130);

		indexEditor.setPreferredSize(new Dimension(100, 120));
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribList indexes)
	{
		indexEditor.refresh(indexes);
	}
}

//==============================================================================
