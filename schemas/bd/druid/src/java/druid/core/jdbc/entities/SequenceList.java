//==============================================================================
//===
//===   SequenceList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class SequenceList extends AbstractEntity
{
	public SequenceList(JdbcConnection conn)
	{
		super(conn, "Sequences", null, null);

		setToolTipText("Sequences in this schema/db");
	}
}

//==============================================================================
