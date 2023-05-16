//==============================================================================
//===
//===   HSqlDbSqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.hsqldb.sql.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;

import druid.core.jdbc.DefaultSqlAdapter;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.entities.ViewEntity;

//==============================================================================

public class HSqlDbSqlAdapter extends DefaultSqlAdapter
{
	public String getId()      { return "hsqlSqlAd";      }
	public String getVersion() { return "1.0";            }
	public String getAuthor()  { return "Andrea Carboni"; }

	public String getDescription() { return "Adapter for the HSQLDB DBMS"; }

	//---------------------------------------------------------------------------
	//---
	//--- SqlAdapter interface
	//---
	//---------------------------------------------------------------------------

	public String getMatchString() { return "hsqldb"; }

	//---------------------------------------------------------------------------
	//---
	//--- Retrieve methods
	//---
	//---------------------------------------------------------------------------

	public void retrieveView(ViewEntity ent)
	{
		JdbcConnection conn = ent.getJdbcConnection();

		String query = "SELECT view_definition FROM SYSTEM_VIEWS "+
							"WHERE  table_name='"+ent.sName+"'";

		try
		{
			ResultSet rs = conn.selectUpdate(query);
			rs.next();
			ent.sqlCode = "CREATE VIEW "+ent.sName+" AS\n"+rs.getString(1);
			rs.close();
		}
		catch(SQLException e)
		{
			ent.sqlCode = "?? raised exception during retrieval ??\n\n"+e;
			e.printStackTrace();
		}
	}
}

//==============================================================================
