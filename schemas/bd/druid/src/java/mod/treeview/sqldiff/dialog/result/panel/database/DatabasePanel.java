//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.database;

import mod.treeview.sqldiff.dialog.result.panel.DiffUtil;
import mod.treeview.sqldiff.dialog.result.panel.SqlPanel;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class DatabasePanel extends TTabbedPane
{
	private SqlPanel panPreSql  = new SqlPanel();
	private SqlPanel panPostSql = new SqlPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("Pre Sql",     panPreSql);
		addTab("Post Sql",    panPostSql);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(DiffSummary diffSumm, DiffEntity ent)
	{
		if (ent != null)
		{
			setEnabledAt(0, panPreSql .setText(ent.get(DiffEntity.DB_PRESQL)));
			setEnabledAt(1, panPostSql.setText(ent.get(DiffEntity.DB_POSTSQL)));
		}
		else
		{
			setEnabledAt(0, false);
			setEnabledAt(1, false);
		}

		DiffUtil.showTab(this);
	}
}

//==============================================================================
