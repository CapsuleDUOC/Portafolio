//==============================================================================
//===
//===   IndexPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql.panels.table;

import java.awt.Dimension;

import mod.dbms.postgresql.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TPanel;

import druid.data.DatabaseNode;
import factory.sql.panels.AttribListEditor;

//==============================================================================

public class IndexPanel extends TPanel
{
	private AttribListEditor indexEditor = new AttribListEditor();
	private TComboBox        tcbAccess   = new TComboBox();

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

		indexEditor.addAttrib("index",  "Index",     20, false);
		indexEditor.addAttrib("name",   "Name",     100);
		indexEditor.addAttrib("access", "Access",   100, tcbAccess);
		indexEditor.addAttrib("where",  "Where",    120);

		indexEditor.setPreferredSize(new Dimension(100, 120));

		tcbAccess.addItem(TableSettings.ACCESS_DEFAULT, "Default");
		tcbAccess.addItem(TableSettings.ACCESS_BTREE,   "BTREE");
		tcbAccess.addItem(TableSettings.ACCESS_RTREE,   "RTREE");
		tcbAccess.addItem(TableSettings.ACCESS_HASH,    "HASH");
		tcbAccess.addItem(TableSettings.ACCESS_GIST,    "GIST");
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts, DatabaseNode dbNode)
	{
		indexEditor.refresh(ts.getIndexOpt(dbNode));
	}
}

//==============================================================================
