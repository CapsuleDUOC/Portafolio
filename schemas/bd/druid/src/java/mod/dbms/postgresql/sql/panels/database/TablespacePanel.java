//==============================================================================
//===
//===   TablespacePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql.panels.database;

import druid.util.gui.DataEntryPanel;
import javax.swing.JPanel;
import mod.dbms.postgresql.sql.DatabaseSettings;
import org.dlib.gui.FlexLayout;

//==============================================================================

public class TablespacePanel extends JPanel
{
	private DataEntryPanel panTablespaces = new DataEntryPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TablespacePanel()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panTablespaces);

		panTablespaces.addAttrib("name", "Name", 250);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panTablespaces.setAttribList(s.getTablespaces());
	}
}

//==============================================================================
