//==============================================================================
//===
//===   ProcedureList
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

public class ProcedureList extends ContainerEntity
{
	//---------------------------------------------------------------------------

	public ProcedureList(JdbcConnection conn)
	{
		super(conn, "Procedures", null, null);

		setToolTipText("Procedures in this schema/db");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadStructI() throws SQLException
	{
		RecordList rl = ((SchemaEntity) getParent()).getProcFunc();

		JdbcConnection conn = getJdbcConnection();

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector v = rl.getRecordAt(i);

			String name = (String) v.elementAt(2);
			String rems = (String) v.elementAt(6);
			String type = DatabaseMetaData.procedureResultUnknown +"";

			if (v.elementAt(7) != null)
				type = v.elementAt(7).toString();

			if (type.equals(DatabaseMetaData.procedureNoResult +""))
				add(new ProcedureEntity(conn, name, rems));
		}
	}
}

//==============================================================================
