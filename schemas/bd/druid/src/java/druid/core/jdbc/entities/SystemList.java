//==============================================================================
//===
//===   SystemList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class SystemList extends AbstractEntity
{
	public SystemList(JdbcConnection conn)
	{
		super(conn, "System objects", null, null);

		setToolTipText("Objects for internal use");
	}
}

//==============================================================================
