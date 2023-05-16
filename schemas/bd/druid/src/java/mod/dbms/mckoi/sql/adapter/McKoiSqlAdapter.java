//==============================================================================
//===
//===   McKoiSqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mckoi.sql.adapter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import druid.core.jdbc.DefaultSqlAdapter;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.ViewEntity;

//==============================================================================

public class McKoiSqlAdapter extends DefaultSqlAdapter
{
	public String getId()      { return "mckoiSqlAd";     }
	public String getVersion() { return "1.0";            }
	public String getAuthor()  { return "Andrea Carboni"; }

	public String getDescription() { return "Adapter for the McKoi DBMS"; }

	//---------------------------------------------------------------------------
	//---
	//--- SqlAdapter interface
	//---
	//---------------------------------------------------------------------------

	public String getMatchString() { return "mckoi"; }

	//---------------------------------------------------------------------------
	//---
	//--- Overridedn methods
	//---
	//---------------------------------------------------------------------------

	public void retrieveView(ViewEntity ent)
	{
		JdbcConnection conn = ent.getJdbcConnection();

		String query = "SELECT query FROM SYS_INFO.sUSRView WHERE name='"+ent.sName+"'";

		try
		{
			ResultSet rs = conn.selectUpdate(query);
			rs.next();

			byte binQuery[] = rs.getBytes(1);

			int high = binQuery[0];
			int low  = binQuery[1];

			ent.sqlCode = new String(binQuery, 2, high*256 + low);

			rs.close();
		}
		catch(SQLException e)
		{
			ent.sqlCode = "?? raised exception during retrieval ??\n\n"+e;
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	public void retrieveSequence(SequenceEntity ent)
	{
		JdbcConnection conn = ent.getJdbcConnection();

		try
		{
			String query = "SELECT id FROM SYS_INFO.sUSRSequenceInfo WHERE name='"+ent.sName+"'";

			ResultSet rs = conn.selectUpdate(query);
			rs.next();
			int id = rs.getInt(1);
			rs.close();

			query = 	"SELECT * FROM SYS_INFO.sUSRSequence WHERE seq_id="+id;

			rs = conn.selectUpdate(query);
			rs.next();

			ent.increment = ""+ rs.getInt("increment");
			ent.minValue  = ""+ rs.getInt("minvalue");
			ent.maxValue  = ""+ rs.getInt("maxvalue");
			ent.start     = ""+ rs.getInt("start");
			ent.cache     = ""+ rs.getInt("cache");

			ent.cycle = rs.getBoolean("cycle");
			ent.order = false;

			rs.close();
		}
		catch(SQLException e)
		{
			ent.increment = "?? raised exception during retrieval ??";
			ent.minValue  = e.toString();

			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	protected void dropSequence(SequenceEntity node) throws SQLException, IOException
	{
		String query = "DROP SEQUENCE " + node.sName;

		node.getJdbcConnection().execute(query, null);
	}
}

//==============================================================================
