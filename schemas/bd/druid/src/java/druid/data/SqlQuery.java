//==============================================================================
//===
//===   SqlQuery
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class SqlQuery extends AbstractNode
{
	//---------------------------------------------------------------------------

	public SqlQuery()
	{
		attrSet.addAttrib("sqlCode", "SELECT * FROM ...");

		setToolTipText("A query you can execute via JDBC");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new SqlQuery(); }
}

//==============================================================================
