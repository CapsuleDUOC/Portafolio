//==============================================================================
//===
//===   DDFImporter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.dlib.tools.TVector;

import ddf.lib.JdbcLib;
import ddf.type.DDFInfo;
import ddf.type.QueryField;

//==============================================================================

public class DDFImporter
{
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void doImport(Connection conn, String table, String fileName, 
						 DDFImportListener listener) throws SQLException, IOException, ParseException
	{
		this.conn     = conn;
		this.table    = table;
		this.listener = listener;
		
		stmtInsert  = null;
		stmtUpdate  = null;
		stmtDelete  = null;
		skipAll     = false;
		missingPKeys= false;

		DDFReader rdr = new DDFReader(fileName);

		try 
		{
			rdr.read(readerListener);
		} 
		finally 
		{
			try 
			{
				rdr.close();
			} 
			catch (Exception e) { /* do nothing*/ }
            
			try 
			{
				if (stmtInsert != null) 
					stmtInsert.close();

				if (stmtUpdate != null) 
					stmtUpdate.close();

				if (stmtDelete != null) 
					stmtDelete.close();
			} 
			catch (Exception e) { /* do nothing*/ }
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods : Insert part
	//---
	//---------------------------------------------------------------------------

	private PreparedStatement createInsertStatement() throws SQLException
	{
		TVector vFields = new TVector();
		TVector vQMarks = new TVector();

		for (QueryField field : fields)
		{
			vFields.add(field.getName());
			vQMarks.add("?");
		}

		String query = "INSERT INTO " + table + "(" + vFields + ") " + "VALUES (" + vQMarks + ")";
		
		return conn.prepareStatement(query);
	}

	//---------------------------------------------------------------------------
	
	private boolean insertRow(List<Object> row, String line, long recordNum) throws SQLException
	{
		if (stmtInsert == null)
			stmtInsert = createInsertStatement();
		
		JdbcLib.setRow(stmtInsert, fields, row);
		
		if (listener != null)
			listener.insertingRow(fields, row, recordNum);

		while (true)
			try
			{
				stmtInsert.executeUpdate();
				return true;
			}
			catch(SQLException e)
			{
				if (listener == null)
					throw e;
				
				if (skipAll)
					return true;
				
				DDFImportListener.ActionType action = listener.onInsertError(e, recordNum, line);
				
				if (action == DDFImportListener.ActionType.SKIP)
					return true;

				if (action == DDFImportListener.ActionType.SKIP_ALL)
				{
					skipAll = true;
					return true;
				}

				if (action == DDFImportListener.ActionType.ABORT)
					return false;
			}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods : Update part
	//---
	//---------------------------------------------------------------------------

	private PreparedStatement createUpdateStatement() throws SQLException
	{
		TVector where  = new TVector();
		TVector values = new TVector();
		where.setSeparator(" AND ");

		for (QueryField field : fields)
			if (field.isPKey())	where .add(field.getName() + " = ?");
				else			values.add(field.getName() + " = ?");

		if (where.isEmpty())
			throw new RuntimeException("Missing primary keys for update operation");

		if (values.isEmpty())
			throw new RuntimeException("Missing values for update operation");

		String query = "UPDATE " + table + " SET " + values + " WHERE " + where;

		return conn.prepareStatement(query);
	}

	//---------------------------------------------------------------------------

	private boolean updateRow(List<Object> row, String line, long recordNum) throws SQLException
	{
		if (stmtUpdate == null)
			stmtUpdate = createUpdateStatement();
		
		//--- fields and values must be reorganized in order to be properly set

		List<QueryField> valueFields = new ArrayList<QueryField>();
		List<QueryField> whereFields = new ArrayList<QueryField>();
		List<Object>     valueRow    = new ArrayList<Object>();
		List<Object>     whereRow    = new ArrayList<Object>();

		for (int i=0; i<fields.size(); i++)
		{
			QueryField field = fields.get(i);
			Object     value = row   .get(i);

			if (field.isPKey()) 
			{
				whereFields.add(field);
				whereRow   .add(value);
			}
			else
			{
				valueFields.add(field);
				valueRow   .add(value);
			}
		}

		List<QueryField> fieldsUpdate = new ArrayList<QueryField>();
		fieldsUpdate.addAll(valueFields);
		fieldsUpdate.addAll(whereFields);

		List<Object> rowUpdate = new ArrayList<Object>();
		rowUpdate.addAll(valueRow);
		rowUpdate.addAll(whereRow);
		
		//--- ok, now we can set the fields

		JdbcLib.setRow(stmtUpdate, fieldsUpdate, rowUpdate);
		
		if (listener != null)
			listener.updatingRow(fields, row, recordNum);

		while (true)
			try
			{
				stmtUpdate.executeUpdate();
				return true;
			}
			catch(SQLException e)
			{
				if (listener == null)
					throw e;
				
				if (skipAll)
					return true;
				
				DDFImportListener.ActionType action = listener.onUpdateError(e, recordNum, line);
				
				if (action == DDFImportListener.ActionType.SKIP)
					return true;

				if (action == DDFImportListener.ActionType.SKIP_ALL)
				{
					skipAll = true;
					return true;
				}

				if (action == DDFImportListener.ActionType.ABORT)
					return false;
			}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods : Delete part
	//---
	//---------------------------------------------------------------------------

	private PreparedStatement createDeleteStatement() throws SQLException
	{
		TVector where = new TVector();
		where.setSeparator(" AND ");

		for (QueryField field : fields)
			if (field.isPKey())	
				where.add(field.getName() + " = ?");

		if (where.isEmpty())
		{
			//--- no primary keys, try a delete on all row

			missingPKeys = true;

			for (QueryField field : fields)
				where.add(field.getName() + " = ?");
		}

		String query = "DELETE FROM " + table + " WHERE " + where;

		return conn.prepareStatement(query);
	}

	//---------------------------------------------------------------------------

	private boolean deleteRow(List<Object> row, String line, long recordNum) throws SQLException
	{
		if (stmtDelete == null)
			stmtDelete = createDeleteStatement();
		
		if (missingPKeys)
			JdbcLib.setRow(stmtDelete, fields, row);
		else
		{
			//--- fields and values must be reorganized in order to be properly set

			List<QueryField> pkeyFields = new ArrayList<QueryField>();
			List<Object>     pkeyRow    = new ArrayList<Object>();

			for (int i=0; i<fields.size(); i++)
			{
				QueryField field = fields.get(i);
				Object     value = row   .get(i);

				if (field.isPKey()) 
				{
					pkeyFields.add(field);
					pkeyRow   .add(value);
				}
			}
			
			//--- ok, now we can set the fields
			
			JdbcLib.setRow(stmtDelete, pkeyFields, pkeyRow);
		}

		if (listener != null)
			listener.deletingRow(fields, row, recordNum);

		while (true)
			try
			{
				stmtDelete.executeUpdate();
				return true;
			}
			catch(SQLException e)
			{
				if (listener == null)
					throw e;
				
				if (skipAll)
					return true;
				
				DDFImportListener.ActionType action = listener.onDeleteError(e, recordNum, line);
				
				if (action == DDFImportListener.ActionType.SKIP)
					return true;

				if (action == DDFImportListener.ActionType.SKIP_ALL)
				{
					skipAll = true;
					return true;
				}

				if (action == DDFImportListener.ActionType.ABORT)
					return false;
			}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listener
	//---
	//---------------------------------------------------------------------------

	private DDFReaderListener readerListener = new DDFReaderListener() 
	{
		@Override
		public void handleInfo(DDFInfo i)
		{
			info = i;
			
			if (table == null)
				table = info.getTable();
		}

		@Override
		public void handleQueryFields(List<QueryField> f) throws SQLException 
		{
			fields = f;
		}

		@Override
		public boolean handleInsertRow(List<Object> row, String line, long recordNum) throws SQLException 
		{
			return insertRow(row, line, recordNum);
		}
		
		@Override
		public boolean handleUpdateRow(List<Object> row, String line, long recordNum) throws SQLException 
		{
			return updateRow(row, line, recordNum);
		}
		
		@Override
		public boolean handleDeleteRow(List<Object> row, String line, long recordNum) throws SQLException 
		{
			return deleteRow(row, line, recordNum);
		}

		@Override
		public void handlePostRow(OperationType type, String line, long recordNum) throws SQLException 
		{
			if (listener != null)
				listener.handlePostRow(type, recordNum, line);
		}
	};

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private Connection        conn;
	private String            table;
	private DDFImportListener listener;
	private PreparedStatement stmtInsert;
	private PreparedStatement stmtUpdate;
	private PreparedStatement stmtDelete;
	private DDFInfo           info;
	private List<QueryField>  fields;

	private boolean skipAll;
	private boolean missingPKeys;
}

//==============================================================================
