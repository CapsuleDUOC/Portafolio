//==============================================================================
//===
//===   UDTEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Vector;

import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.RecordList;

//==============================================================================

public class UDTEntity extends AbstractEntity
{
	public static final String TYPE = "UDT";

	public String sClass;
	public String sDataType;
	public String sBaseType;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public UDTEntity(JdbcConnection conn, String name, String rems)
	{
		super(conn, name, TYPE, rems);
	}

	//---------------------------------------------------------------------------
	//---
	//---  Info retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException
	{
		JdbcConnection   conn = getJdbcConnection();
		DatabaseMetaData meta = conn.getMetaData();

		//--- here an exception must be thrown because this method get called
		//--- only after a node refresh and therefore the UDT is supported
		//--- (in any case the UDT could exist no more, after a delete)

		RecordList rl = conn.retrieveResultSet(meta.getUDTs(null, getSchema(), "%", null));

		//--- extract data

		Vector v = rl.getRecordAt(0);

		sClass    = (String) v.elementAt(3);
		sDataType = (String) v.elementAt(4);

		if (v.size() > 6)
			sBaseType = (String) v.elementAt(6);
		else
			sBaseType = "<NOT SUPPORTED>";
	}
}

//==============================================================================
