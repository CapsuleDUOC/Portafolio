//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql.panels.table;

import javax.swing.JPanel;

import mod.dbms.oracle.sql.DatabaseSettings;
import mod.dbms.oracle.sql.TableSettings;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;

//==============================================================================

public class TablePanel extends JPanel
{
	private IndexPanel    panIndex    = new IndexPanel();
	private StoragePanel  panStorage  = new StoragePanel();

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(1, 2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panIndex);
		add("0,1,x",   panStorage);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings gs, TableSettings ts, DatabaseNode dbNode)
	{
		panIndex.refresh(gs, ts, dbNode);
		panStorage.refresh(gs, ts);
	}
}

//==============================================================================
