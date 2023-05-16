//==============================================================================
//===
//===   IndexPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.daffodil.sql.panels.table;

import java.awt.Dimension;

import druid.data.DatabaseNode;
import factory.sql.panels.AttribListEditor;
import mod.dbms.daffodil.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

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

		indexEditor.addAttrib("index",    "Index",      20, false);
		indexEditor.addAttrib("name",     "Name",      100);
		indexEditor.addAttrib("fullText", "Full text",  50);

		indexEditor.setPreferredSize(new Dimension(100, 120));
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts, DatabaseNode dbNode)
	{
		indexEditor.refresh(ts.getIndexOpt(dbNode));
	}
}

//==============================================================================
