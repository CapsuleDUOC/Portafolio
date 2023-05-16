//==============================================================================
//===
//===   ViewList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class ViewList extends AbstractEntity
{
	public ViewList(JdbcConnection conn)
	{
		super(conn, "Views", null, null);

		setToolTipText("Views in this schema/db");
	}
}

//==============================================================================
