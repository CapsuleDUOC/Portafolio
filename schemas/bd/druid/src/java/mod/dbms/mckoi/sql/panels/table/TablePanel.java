//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mckoi.sql.panels.table;

import javax.swing.JPanel;

import mod.dbms.mckoi.sql.TableSettings;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;
import factory.sql.panels.table.IndexPanel;

//==============================================================================

public class TablePanel extends JPanel
{
	private IndexPanel panIndex = new IndexPanel();

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panIndex);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts, DatabaseNode dbNode)
	{
		panIndex.refresh(ts.getIndexOpt(dbNode));
	}
}

//==============================================================================
