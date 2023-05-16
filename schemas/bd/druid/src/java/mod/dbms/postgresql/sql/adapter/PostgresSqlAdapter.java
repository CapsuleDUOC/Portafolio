//==============================================================================
//===
//===   PostgresSqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql.adapter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.dlib.tools.TVector;

import druid.core.DataLib;
import druid.core.jdbc.DefaultSqlAdapter;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.RecordList;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.data.FieldNode;
import druid.data.FunctionNode;
import druid.data.TableNode;
import factory.sql.SqlUtil;

//==============================================================================

public class PostgresSqlAdapter extends DefaultSqlAdapter
{
	public String getId()      { return "postgresSqlAd";  }
	public String getVersion() { return "1.0";            }
	public String getAuthor()  { return "Andrea Carboni"; }

	public String getDescription() { return "Adapter for the PostgreSQL DBMS"; }

	//---------------------------------------------------------------------------
	//---
	//--- SqlAdapter interface
	//---
	//---------------------------------------------------------------------------

	public String getMatchString() { return "postgresql"; }

	//---------------------------------------------------------------------------
	//---
	//--- Retrieve methods
	//---
	//---------------------------------------------------------------------------

	public void retrieveView(ViewEntity ent)
	{
		JdbcConnection conn = ent.getJdbcConnection();

		String query = "SELECT view_definition FROM information_schema.views "+
							"WHERE table_schema='public' AND table_name='"+ent.sName+"'";

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

	//---------------------------------------------------------------------------
	//---
	//--- Drop methods
	//---
	//---------------------------------------------------------------------------

	protected void dropTable(TableNode node) throws SQLException, IOException
	{
		String name = node.attrSet.getString("name");

		JdbcConnection jdbcConn = node.getDatabase().getJdbcConnection();

		jdbcConn.execute("DROP TABLE " + name, null);

		//--- Hack for postgres <= 7.3 : we will try to remove all related sequences

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			if (DataLib.isPrimaryKey(field))
				try
				{
					String fieldName = field.attrSet.getString("name");

					jdbcConn.execute("DROP SEQUENCE " +name+"_"+fieldName+"_seq", null);
				}
				catch(Exception e) {}
		}
	}

	//---------------------------------------------------------------------------

	protected void dropFunction(FunctionNode node) throws SQLException, IOException
	{
		String code = node.attrSet.getString("sqlCode");
		String name = SqlUtil.getNameFromCode(code);

		int start = code.indexOf("(");
		int end   = code.indexOf(")");

		if (start != -1 && end != -1 && start < end)
			name += code.substring(start, end +1);

		node.getDatabase().getJdbcConnection().execute("DROP FUNCTION " + name, null);
	}

	//---------------------------------------------------------------------------

	protected void dropFunction(FunctionEntity node) throws SQLException, IOException
	{
		String query = "DROP FUNCTION " + node.getFullName();

		//--- collect parameters of function to drop

		RecordList rl = node.rlParameters;
		TVector v = new TVector();

		int idxColType  = rl.getColumnIndex("COLUMN_TYPE");
		int idxDataType = rl.getColumnIndex("DATA_TYPE");

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector row = rl.getRecordAt(i);

			String colType  = (String) row.get(idxColType);
			String dataType = (String) row.get(idxDataType);

			if (colType == null)
				colType = "?";

			if (colType.toLowerCase().equals("in"))
				v.add(dataType);
		}

		if (v.size() != 0)
			query += "(" +v+ ")";

		node.getJdbcConnection().execute(query, null);
	}
}

//==============================================================================
