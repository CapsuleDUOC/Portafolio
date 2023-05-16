//==============================================================================
//===
//===   DbPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.db;

import org.dlib.gui.TTabbedPane;

import druid.core.jdbc.entities.DatabaseEntity;
import druid.util.jdbc.RecordListPanel;

//==============================================================================

public class DbPanel extends TTabbedPane
{
	private RecordListPanel keyPanel = new RecordListPanel();
	private RecordListPanel sqlPanel = new RecordListPanel();
	private RecordListPanel maxPanel = new RecordListPanel();
	private RecordListPanel traPanel = new RecordListPanel();
	private RecordListPanel idsPanel = new RecordListPanel();
	private RecordListPanel othPanel = new RecordListPanel();
	private RecordListPanel catPanel = new RecordListPanel();
	private RecordListPanel tatPanel = new RecordListPanel();
	private RecordListPanel datPanel = new RecordListPanel();
	private RecordListPanel rssPanel = new RecordListPanel();

	//---------------------------------------------------------------------------

	public DbPanel()
	{
		addTab("Keywords",   keyPanel);
		addTab("Sql",        sqlPanel);
		addTab("Max val",    maxPanel);
		addTab("Transact",   traPanel);
		addTab("Identif",    idsPanel);
		addTab("Other",      othPanel);
		addTab("Catalogs",   catPanel);
		addTab("TableTypes", tatPanel);
		addTab("DataTypes",  datPanel);
		addTab("ResultSets", rssPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseEntity node)
	{
		keyPanel.refresh(node.rlKeywords);
		sqlPanel.refresh(node.rlSql);
		maxPanel.refresh(node.rlMaxValues);
		traPanel.refresh(node.rlTransactions);
		idsPanel.refresh(node.rlIdentifiers);
		othPanel.refresh(node.rlOther);
		catPanel.refresh(node.rlCatalogs);
		tatPanel.refresh(node.rlTableTypes);
		datPanel.refresh(node.rlDataTypes);
		rssPanel.refresh(node.rlResultSets);
	}
}

//==============================================================================
