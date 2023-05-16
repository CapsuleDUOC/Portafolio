//==============================================================================
//===
//===   ConstraintEntity
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;
import druid.data.Constraint;

//==============================================================================
//---constraints support in JDBC view

public class ConstraintEntity extends AbstractEntity
{
	public Constraint constraint;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ConstraintEntity()
	{
		this(null, null, null);
	}

	//---------------------------------------------------------------------------

	public ConstraintEntity(JdbcConnection conn, String name, Constraint c)
	{
		super(conn, name, "CONSTRAINT", null);

		constraint = c;
	}
}

//==============================================================================
