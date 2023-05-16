//==============================================================================
//===
//===   JdbcLib
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.lib;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ddf.type.MemoryBlob;
import ddf.type.MemoryClob;
import ddf.type.QueryField;
import ddf.type.SqlType;

//==============================================================================

public class JdbcLib
{
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static List<QueryField> getQueryFields(Connection conn, ResultSet resultSet) throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();

		List<QueryField> result = new ArrayList<QueryField>();

		for(int i=1; i<=metaData.getColumnCount(); i++)
		{
			String name     = metaData.getColumnName(i);
			int    type     = metaData.getColumnType(i);
			String dbType   = metaData.getColumnTypeName(i);
			int    decimals = metaData.getScale(i);

			result.add(new QueryField(name, 
									  SqlMapper.map(Integer.toString(type), dbType, Integer.toString(decimals), conn), 
									  false));
		}

		return result;
	}
	
	//---------------------------------------------------------------------------

	public static Set<String> getPrimaryKeys(DatabaseMetaData dbMeta, String schema, String table) throws SQLException
	{
		ResultSet rs = dbMeta.getPrimaryKeys(null, schema, table);
		
		//--- retrieve primary keys

		Set<String> keys = new HashSet<String>();

		while ( rs.next() )
			keys.add(rs.getObject(4).toString());

		Statement st = rs.getStatement();
		
		rs.close();

		if (st != null)
			st.close();
		
		return keys;
	}

	//---------------------------------------------------------------------------

	public static String getTable(String query)
	{
		String query1 = query.toLowerCase().replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');

		int idx1 = query1.indexOf(" from ");
		int idx2 = query1.indexOf(" where ");
		
		if (idx1 == -1)
			return null;

		//--- extract portion between FROM and WHERE

		if (idx2 == -1)
			idx2 = query.length();

		query = query.substring(idx1 +6, idx2).trim();
		
		//--- if there is a space, cut all from that point

		idx1 = query.indexOf(" ");

		if (idx1 != -1)
			query = query.substring(0, idx1);
		
		//--- comas cannot be accepted

		if (query.indexOf(",") != -1)
			return null;
		
		return query;
	}

	//---------------------------------------------------------------------------
	
	public static List<Object> getRow(ResultSet resultSet, List<QueryField> fields) throws SQLException
	{
		List<Object> row = new ArrayList<Object>();
		
		int i = 1;
		
		Object obj;

		for(QueryField field : fields)
		{
			SqlType type = field.getType();

			if (type.isDate())
				obj = resultSet.getDate(i);

			else if (type.isTime())
				obj = resultSet.getTime(i);

			else if (type.isTimeStamp())
				obj = resultSet.getTimestamp(i);

			//--- main types

			else if (type.isNumber() || type.isString() || type.isLongVarChar())
				obj = resultSet.getString(i);

			else if (type.isBinaryType())
				obj = resultSet.getBytes(i);

			else if (type.isBlob())
			{
				Blob b = resultSet.getBlob(i);

				obj = (b == null) ? null : b.getBytes(1, (int)b.length());
			}

			else if (type.isClob())
			{
				Clob c = resultSet.getClob(i);

				obj = (c == null) ? null : c.getSubString(1, (int) c.length());
			}

			else if (type.isBoolean())
			{
				boolean isTrue = resultSet.getBoolean(i);

				obj = (resultSet.wasNull())	? null : (isTrue) ? "T" : "F";
			}

			//--- other unknown stuff

			else
				obj = "< ???? >";
			
			row.add(obj);
			i++;
		}
		
		return row;
	}

	//---------------------------------------------------------------------------
	
	public static void setRow(PreparedStatement stmt, List<QueryField> fields, List<Object> row) throws SQLException
	{
		for(int i=0; i<fields.size(); i++)
		{
			SqlType type = fields.get(i).getType();
			Object  obj  = row.get(i);
			
			if (obj == null)
				stmt.setNull(i+1, type.iId);

			else if (type.isDate())
				stmt.setDate(i+1, (Date) obj);

			else if (type.isTime())
				stmt.setTime(i+1, (Time) obj);

			else if (type.isTimeStamp())
				stmt.setTimestamp(i+1, (Timestamp) obj);

			else if (type.isInteger())
				stmt.setLong(i+1, (Long) obj);

			else if (type.isReal())
				stmt.setDouble(i+1, (Double) obj);

			else if (type.isBoolean())
				stmt.setBoolean(i+1, (Boolean) obj);

			else if (type.isBinaryType())
			{
				byte data[] = (byte[]) obj;
				stmt.setBinaryStream(i+1, new ByteArrayInputStream(data), data.length);
			}

			else if (type.isBlob())
				stmt.setBlob(i+1, new MemoryBlob((byte[])obj));

			else if (type.isClob())
				stmt.setClob(i+1, new MemoryClob((String) obj));

			//--- we must divide the case of strings and longvarchar because
			//--- some drivers don't implement the setCharacterStream method

			else if (type.isString())
				stmt.setString(i+1, (String) obj);

			else if (type.isLongVarChar())
			{
				String s = (String) obj;
				stmt.setCharacterStream(i+1, new StringReader(s), s.length());
			}

			else
			{
				//--- we arrive here is the sql type is unknown
				stmt.setNull(i+1, type.iId);
			}
		}
	}
}

//==============================================================================
