//==============================================================================
//===
//===   SynonymEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

//--- data editor support for synonyms

public class SynonymEntity extends RecordBasedEntity
{
	public static final String TYPE = "SYNONYM";

	//---------------------------------------------------------------------------

	public SynonymEntity(JdbcConnection conn, String name, String rems)
	{
		super(conn, name, TYPE, rems);
	}
}

//==============================================================================
