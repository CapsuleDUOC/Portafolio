//==============================================================================
//===
//===   DDFExporter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.exp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import ddf.lib.JdbcLib;
import ddf.type.QueryField;

//==============================================================================

public class DDFExporter
{
	//---------------------------------------------------------------------------

	public void doExport(Connection conn, String query, String fileName, 
						 DDFExportListener listener) throws SQLException, IOException
	{
		PreparedStatement stmt = conn.prepareStatement( query );

		ResultSet resultSet = null;
		DDFWriter writer    = null;

		try 
		{
			trySetMaxFieldSize( stmt, 0 );
			stmt.setMaxRows( 0 );

			//--- perform query & retrieve query fields

			resultSet  = stmt.executeQuery();

			List<QueryField> fields = JdbcLib.getQueryFields(conn, resultSet);

			writer = new DDFWriter(fileName);
			
			String schema = null;
			String table  = JdbcLib.getTable(query);

			if (table != null)
			{
				int idx = table.indexOf(".");
				
				if (idx == -1)
					writer.setTable(table);
				else
				{
					schema = table.substring(0, idx);
					table  = table.substring(idx +1);
					
					writer.setSchema(schema);
					writer.setTable(table);
				}
				
				//--- in case of single table, we can extract pk info
				
				Set<String> keys = JdbcLib.getPrimaryKeys(conn.getMetaData(), schema, table);
				
				for (QueryField field : fields)
					if (keys.contains(field.getName()))
						field.setPKey(true);
			}

			writer.setQuery(query);
			writer.setFields(fields);
			writer.beginInsertSection();

			//--- scan records and save data

			int recordNum = 1;
			
			while(resultSet.next())
			{
				List<Object> row = JdbcLib.getRow(resultSet, fields);
				
				writer.writeRow(row);
				
				if (listener != null)
					listener.exportedRow(row, recordNum++);
			}

			resultSet.close();
			stmt.close();
		} 
		finally 
		{
			try 
			{
				if (writer != null)
					writer.end();
			} 
			catch (IOException e) { /* do nothing*/ }

			try 
			{
				if (resultSet != null)
					resultSet.close();
			} 
			catch (SQLException e) { /* do nothing*/ }

			try 
			{
				stmt.close();
			} 
			catch (SQLException e) { /* do nothing*/ }
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void trySetMaxFieldSize( Statement stmt, int value )
	{
		try
		{
			stmt.setMaxFieldSize( value );
		}
		catch ( SQLException e ) {}
	}
}

//==============================================================================
