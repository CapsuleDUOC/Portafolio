//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mysql.sql.panels.table;

import javax.swing.JPanel;

import mod.dbms.mysql.sql.TableSettings;

import org.dlib.gui.FlexLayout;

import druid.data.DatabaseNode;
import factory.sql.panels.table.IndexPanel;

//==============================================================================

public class TablePanel extends JPanel
{
	private IndexPanel   panIndex   = new IndexPanel();
	private PostSqlPanel panPostSql = new PostSqlPanel();
	private GeneralPanel panGeneral = new GeneralPanel();
        
	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(1, 3);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panIndex);
		add("0,1,x,x", panPostSql);
		add("0,2,x,x", panGeneral);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts, DatabaseNode dbNode)
	{
		panIndex.refresh(ts.getIndexOpt(dbNode));
		panPostSql.refresh(ts);
		panGeneral.refresh(ts);
	}
}

//==============================================================================
