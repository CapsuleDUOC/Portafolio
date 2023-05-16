//==============================================================================
//===
//===   IndexPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql.panels.table;

import java.awt.Dimension;

import mod.dbms.oracle.sql.DatabaseSettings;
import mod.dbms.oracle.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TPanel;

import druid.core.AttribList;
import druid.data.DatabaseNode;
import factory.sql.panels.AttribListEditor;

//==============================================================================

public class IndexPanel extends TPanel
{
	private AttribListEditor indexEditor = new AttribListEditor();
	private TComboBox        tcbTSpace   = new TComboBox();

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

		indexEditor.addAttrib("index",  "Index",       30, false);
		indexEditor.addAttrib("name",   "Name",       130);
		indexEditor.addAttrib("ts",     "Tablespace", 130, tcbTSpace);
		indexEditor.addAttrib("bitmap", "Bitmap",      30);
		indexEditor.addAttrib("stats",  "Stats",       30);
		indexEditor.addAttrib("noSort", "No Sort",     30);

		indexEditor.setPreferredSize(new Dimension(100, 120));
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings gs, TableSettings ts, DatabaseNode dbNode)
	{
		tcbTSpace.removeAllItems();
		tcbTSpace.addItem(0, "-none-");

		//------------------------------------------------------------------------
		//--- add tablespaces to combobox

		AttribList tabs = gs.getTablespaces();

		for(int i=0; i<tabs.size(); i++)
		{
			int    id   = tabs.get(i).getInt("id");
			String name = tabs.get(i).getString("name");

			tcbTSpace.addItem(id, name);
		}

		indexEditor.refresh(ts.getIndexOpt(dbNode));
	}
}

//==============================================================================
