//==============================================================================
//===
//===   SqlWorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.sqlnavigator;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.SqlQuery;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class SqlWorkPanel extends MultiPanel implements DataModel
{
	private SqlTextArea txaSql = new SqlTextArea();

	//---------------------------------------------------------------------------

	public SqlWorkPanel()
	{
		add("blank", new JPanel());
		add("sql",   txaSql);

		txaSql.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public SqlTextArea getQuerySource()
	{
		return txaSql;
	}

	//---------------------------------------------------------------------------
	//---
	//---   DataModel
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		DataTracker.beginDisabledSection();

		if (node == null || node.isRoot())
			show("blank");
		else
		{
			SqlQuery n = (SqlQuery) node;
			txaSql.setText(n.attrSet.getString("sqlCode"));
			show("sql");
		}

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node == null || node.isRoot()) return;

		SqlQuery sqlNode = (SqlQuery) node;
		sqlNode.attrSet.setString("sqlCode", txaSql.getText());
	}
}

//==============================================================================
