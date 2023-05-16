//==============================================================================
//===
//===   TriggerEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import druid.core.jdbc.JdbcConnection;
import druid.data.Trigger;

//==============================================================================

public class TriggerEntity extends AbstractEntity
{
	public Trigger trigger;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TriggerEntity()
	{
		this(null, null, null);
	}

	//---------------------------------------------------------------------------

	public TriggerEntity(JdbcConnection conn, String name, Trigger t)
	{
		super(conn, name, "TRIGGER", null);

		trigger = t;
	}
}

//==============================================================================
