//==============================================================================
//===
//===   SequenceEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.SQLException;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class SequenceEntity extends AbstractEntity
{
	public static final String TYPE = "SEQUENCE";

	public String increment = UNKNOWN;
	public String minValue  = UNKNOWN;
	public String maxValue  = UNKNOWN;
	public String start     = UNKNOWN;
	public String cache     = UNKNOWN;

	public boolean cycle;
	public boolean order;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SequenceEntity(JdbcConnection conn, String name, String rems)
	{
		super(conn, name, TYPE, rems);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException
	{
		super.loadInfoI();

		getJdbcConnection().getSqlAdapter().retrieveSequence(this);
	}
}

//==============================================================================
