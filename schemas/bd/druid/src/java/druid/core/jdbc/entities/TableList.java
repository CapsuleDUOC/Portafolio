//==============================================================================
//===
//===   TableList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class TableList extends AbstractEntity
{
	public TableList(JdbcConnection conn)
	{
		super(conn, "Tables", null, null);

		setToolTipText("Tables in this schema/db");
	}
}

//==============================================================================
