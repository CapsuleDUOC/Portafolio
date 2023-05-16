//==============================================================================
//===
//===   FunctionEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.SQLException;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public class FunctionEntity extends CodingEntity
{
	public static final String TYPE = "FUNCTION";

	public String sqlCode = UNKNOWN;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FunctionEntity(JdbcConnection conn, String name, String rems)
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

		getJdbcConnection().getSqlAdapter().retrieveFunction(this);
	}
}

//==============================================================================
