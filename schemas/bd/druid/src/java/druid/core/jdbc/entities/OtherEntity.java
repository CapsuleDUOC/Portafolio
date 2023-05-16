//==============================================================================
//===
//===   OtherEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class OtherEntity extends AbstractEntity
{
	public OtherEntity(JdbcConnection conn, String name, String type, String rems)
	{
		super(conn, name, type, rems);
	}
}

//==============================================================================
