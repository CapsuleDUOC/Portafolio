//==============================================================================
//===
//===   ResultSetEditor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;

import org.dlib.gui.flextable.FlexTableColumn;
import org.dlib.gui.flextable.FlexTableModel;
import org.dlib.tools.TVector;

import ddf.lib.SqlMapper;
import ddf.type.MemoryBlob;
import ddf.type.MemoryClob;
import ddf.type.SqlType;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.entities.RecordBasedEntity;
import druid.util.gui.Dialogs;

//==============================================================================

public class ResultSetEditor implements FlexTableModel
{
	public static final int NEWRECORD_START = 0;
	public static final int NEWRECORD_END   = 1;
	public static final int NEWRECORD_ERROR = 2;

	public static final int DEFAULT_WIDTH = 80;

	private ResultSet rSet;
	private boolean   bEditable;
	private boolean   bScrollable;

	private ResultSetMetaData rMeta;

	private int colCount;
	private int rowCount;

	private Vector    vColumns = new Vector();
	private SqlType[] aColTypes;

	/** Table's name */
	private String sTable;
	private String sWhere;
	private String sQuery;

	private RecordBasedEntity eNode;

	private Vector vRecords;

	private JdbcConnection jdbcConn;

	//---------------------------------------------------------------------------
	//--- select/edit mode
	//---
	//--- QUERY: a query is supplied from outside. The resultset is not scrollable
	//---        and not editable
	//---
	//--- TABLE: a JdbcEntityNode is supplied from outside. The resultset is made
	//---        editable and the flexdata is editable

	private static final int QUERY = 0;
	private static final int TABLE = 1;

	private int mode;

	//---------------------------------------------------------------------------
	//--- edit modes when inserting a new row

	private static final int NORMAL = 0;
	private static final int INSERT = 1;

	private int editMode;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ResultSetEditor(RecordBasedEntity node)
	{
		mode     = TABLE;
		eNode    = node;
		sTable   = node.getFullName();
		jdbcConn = node.getJdbcConnection();
		editMode = NORMAL;
	}

	//---------------------------------------------------------------------------

	public ResultSetEditor(JdbcConnection conn)
	{
		mode     = QUERY;
		jdbcConn = conn;
		editMode = NORMAL;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	/** Reload data from table into memory
	  * in TABLE mode query is the WHERE clause
	  * in QUERY mode query is the full query
	  */

	public void refresh(String query) throws SQLException
	{
		editMode = NORMAL;

		if (mode == QUERY)
			sQuery = query;
		else
			sWhere = query;

		loadResultSet();

		colCount = rMeta.getColumnCount();

		bScrollable = (rSet.getType()        != ResultSet.TYPE_FORWARD_ONLY);
		bEditable   = (rSet.getConcurrency() != ResultSet.CONCUR_READ_ONLY);

		bEditable   = bEditable && bScrollable;

		//------------------------------------------------------------------------
		//--- retrieve columns types

		vColumns = new Vector();

		aColTypes = new SqlType[colCount];

		for(int i=0; i<colCount; i++)
		{
			String label    = rMeta.getColumnName(i+1);
			int    type     = rMeta.getColumnType(i+1);
			String dbType   = rMeta.getColumnTypeName(i+1);
			int    decimals = rMeta.getScale(i+1);
			int    prefWidth= rMeta.getColumnDisplaySize(i+1);
			int    width    = DEFAULT_WIDTH;

			if (prefWidth < DEFAULT_WIDTH)
				prefWidth = DEFAULT_WIDTH;

			FlexTableColumn ftc = new FlexTableColumn(label, width);
			ftc.setUserObject(new Integer(prefWidth));
			vColumns.addElement(ftc);

			aColTypes[i] = SqlMapper.map(""+type, dbType, ""+decimals, jdbcConn.getConnection());

			//--- these are all types that we can edit in the flex table

			ftc.setEditable(isSimpleEditable(i));
		}

		//------------------------------------------------------------------------
		//--- retrieve records
		//--- it is better to cache records even if the rset is scrollable because
		//--- the jdbc driver is not very efficient

		Vector vCurrRec;

		vRecords = new Vector();

		for(rowCount=0; rSet.next() && rowCount<jdbcConn.getMaxRows(); rowCount++)
		{
			vCurrRec = new Vector();

			for(int j=0; j<colCount; j++)
				vCurrRec.addElement(getFieldValue(j));

			vRecords.addElement(vCurrRec);
		}
	}

	//---------------------------------------------------------------------------

	public int newRecord()
	{
		if (editMode != NORMAL)
		{
			if (insertRow())
			{
				editMode = NORMAL;
				return NEWRECORD_END;
			}
			else
				return NEWRECORD_ERROR;
		}
		else
		{
			Vector v = new Vector();

			for(int i=0; i<vColumns.size(); i++)
			{
				if (aColTypes[i].isInteger()) v.addElement(new Long(0));

				else if (aColTypes[i].isReal()) v.addElement(new Double(0));

				else v.addElement(null);
			}

			vRecords.addElement(v);

			rowCount++;
			editMode = INSERT;

			return NEWRECORD_START;
		}
	}

	//---------------------------------------------------------------------------

	public void copyRecord(int index)
	{
		vRecords.addElement(((Vector)vRecords.get(index)).clone());

		rowCount++;
		editMode = INSERT;
	}

	//---------------------------------------------------------------------------

	public boolean removeRecord(int index)
	{
		try
		{
			if (editMode != INSERT || index != rowCount-1)
			{
				if (bEditable)
				{
					rSet.absolute(index+1);
					rSet.deleteRow();

					//--- row deleted. Now we must reopen the resultset because some jdbc
					//--- drivers place a placeholder where the deleted record was

					loadResultSet();
				}
				else
				{
					String query = "DELETE FROM " + sTable + " WHERE " + getWhereString(index);

					jdbcConn.execute(query, getWhereParameters(index));
				}
			}
			else
			{
				editMode = NORMAL;
			}

			rowCount--;
			vRecords.removeElementAt(index);

			return true;
		}
		catch(Exception e)   
		{ 
			Dialogs.showException(e);    
		}

		return false;
	}

	//---------------------------------------------------------------------------

	public boolean isIScrollable()     { return bScrollable;          }
	public boolean isInserting()       { return (editMode == INSERT); }
	public boolean isEditable()        { return bEditable;            }
	public String  getCurrentQuery()   { return sQuery;               }
	public SqlType getSqlType(int col) { return aColTypes[col];       }

	//---------------------------------------------------------------------------
	//---
	//---   FlexTableModel methods
	//---
	//---------------------------------------------------------------------------

	public int getColumnCount()
	{
		return colCount;
	}

	//---------------------------------------------------------------------------

	public int getRowCount()
	{
		return rowCount;
	}

	//---------------------------------------------------------------------------

	public FlexTableColumn getColumnAt(int index)
	{
		if ((index < 0) || (index >= colCount)) return null;

		return (FlexTableColumn)vColumns.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public Object getValueAt(int row, int col)
	{
		return ((Vector)vRecords.elementAt(row)).elementAt(col);
	}

	//---------------------------------------------------------------------------

	public void setValueAt(Object o, int row, int col)
	{
		try
		{
			//--- Stage 1 : modify data in DB

			if (editMode != INSERT)
			{
				if (bEditable)
				{
					rSet.absolute(row+1);
					setFieldValue(o, col);
					rSet.updateRow();
				}
				else
					updateNotEditableValue(convertToParam(col, o), row, col);
			}

			//--- Stage 2 : modify data in cached data

			Vector vRow = (Vector) vRecords.elementAt(row);

			if (o == null || o.toString().equals(""))
				vRow.setElementAt(null, col);
			else
				vRow.setElementAt(o, col);
		}
		catch(Exception e)                
		{ 
			Dialogs.showException(e);           
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Methods related to types not editable in simple way (with a table)
	//--- This data is not cached into the ResultSetEditor
	//---
	//---------------------------------------------------------------------------

	public byte[] getBinaryValueAt(int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
			rSet.absolute(row+1);

			return rSet.getBytes(col+1);
		}
		else
		{
			ResultSet rs = getNotEditableResultSet(row, col);

			if (!rs.next()) return null;

			return rs.getBytes(1);
		}
	}

	//---------------------------------------------------------------------------

	public void setBinaryValueAt(byte[] data, int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
			rSet.absolute(row+1);

			if (data != null) rSet.updateBinaryStream(col+1, new ByteArrayInputStream(data), data.length);
				else           rSet.updateNull(col+1);

			rSet.updateRow();
		}
		else
		{
			if (data != null) updateNotEditableValue(new ByteArrayInputStream(data), row, col);
				else				updateNotEditableValue(null, row, col);
		}
	}

	//---------------------------------------------------------------------------
	//--- LONGVARCHAR
	//---------------------------------------------------------------------------

	public String getLongStringAt(int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
			rSet.absolute(row+1);

			return rSet.getString(col+1);
		}
		else
		{
			ResultSet rs = getNotEditableResultSet(row, col);

			if (!rs.next()) return null;

			return rs.getString(1);
		}
	}

	//---------------------------------------------------------------------------

	public void setLongStringAt(String data, int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
//under oracle 8.1.7 this doesn't work, so we must supply a more general solution

//			rSet.absolute(row+1);
//
//			if (data != null)	rSet.updateCharacterStream(col+1, new StringReader(data), data.length());
//				else           rSet.updateNull(col+1);
//
//			rSet.updateRow();

			if (data != null) 	updateNotEditableValue(new StringBuffer(data), row, col);
				else			updateNotEditableValue(null, row, col);

			rSet.absolute(row+1);
			rSet.refreshRow();
		}
		else
		{
			if (data != null) updateNotEditableValue(new StringBuffer(data), row, col);
				else				updateNotEditableValue(null, row, col);
		}
	}

	//---------------------------------------------------------------------------
	//--- BLOB
	//---------------------------------------------------------------------------

	public byte[] getBlobAt(int row, int col) throws SQLException, IOException
	{
		Blob b;

		if (bEditable)
		{
			rSet.absolute(row+1);

			b = rSet.getBlob(col+1);
		}
		else
		{
			ResultSet rs = getNotEditableResultSet(row, col);

			if (!rs.next()) return null;

			b = rs.getBlob(1);
		}

		if(b == null) return null;

		return b.getBytes(1, (int)b.length());
	}

	//---------------------------------------------------------------------------

	public void setBlobAt(byte[] data, int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
			rSet.absolute(row+1);

			if (data != null) rSet.updateBlob(col+1, new MemoryBlob(data));
				else           rSet.updateNull(col+1);

			rSet.updateRow();
		}
		else
		{
			if (data != null)
				updateNotEditableValue(new MemoryBlob(data), row, col);
			else
				updateNotEditableValue(null, row, col);
		}
	}

	//---------------------------------------------------------------------------
	//--- CLOB
	//---------------------------------------------------------------------------

	public String getClobAt(int row, int col) throws SQLException, IOException
	{
		Clob c;

		if (bEditable)
		{
			rSet.absolute(row+1);

			c = rSet.getClob(col+1);
		}
		else
		{
			ResultSet rs = getNotEditableResultSet(row, col);

			if (!rs.next()) return null;

			c = rs.getClob(1);
		}

		if(c == null) return null;

		return c.getSubString(0, (int) c.length());
	}

	//---------------------------------------------------------------------------

	public void setClobAt(String data, int row, int col) throws SQLException, IOException
	{
		if (bEditable)
		{
			rSet.absolute(row+1);

			if (data != null) rSet.updateClob(col+1, new MemoryClob(data));
				else           rSet.updateNull(col+1);

			rSet.updateRow();
		}
		else
		{
			if (data != null)
				updateNotEditableValue(new MemoryClob(data), row, col);
			else
				updateNotEditableValue(null, row, col);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Insert row methods
	//---
	//---------------------------------------------------------------------------

	private boolean insertRow()
	{
		try
		{
			//--- the rowCount > 1 condition solves a bug with oracle 8.1.7
			//--- because if the table has no data the jdbc driver rises an
			//--- exception

			if (bEditable && rowCount>1)	insertEditableRow();
				else								insertNotEditableRow();

			return true;
		}
		catch(Exception e)                
		{ 
			Dialogs.showException(e);           
		}

		//--- in case of exception, we remain in insert mode
		return false;
	}

	//---------------------------------------------------------------------------

	private void insertEditableRow() throws SQLException
	{
		rSet.moveToInsertRow();

		Vector vRow = (Vector) vRecords.elementAt(rowCount-1);

		for(int i=0; i<vColumns.size(); i++)
		{
			if (isSimpleEditable(i))
				setFieldValue(vRow.elementAt(i), i);
		}

		rSet.insertRow();
	}

	//---------------------------------------------------------------------------

	private void insertNotEditableRow() throws SQLException, IOException
	{
		//------------------------------------------------------------------------
		//--- build query

		int row = rowCount -1;

		String query = "INSERT INTO " + sTable + "(" + getInsertNames(row) + ") " +
							"VALUES(" + getInsertString(row) + ")";

		jdbcConn.execute(query, getWhereParameters(row));
	}

	//---------------------------------------------------------------------------

	private String getInsertNames(int row)
	{
		TVector v = new TVector();
		v.setSeparator(", ");

		for(int i=0; i<vColumns.size(); i++)
		{
			FlexTableColumn ftc = (FlexTableColumn) vColumns.elementAt(i);

			Object o = getValueAt(row, i);

			if (isSimpleEditable(i) && o != null)
				v.addElement(ftc.getHeaderValue());
		}

		return v.toString();
	}

	//---------------------------------------------------------------------------

	private String getInsertString(int row)
	{
		TVector v = new TVector();
		v.setSeparator(", ");

		for(int i=0; i<vColumns.size(); i++)
		{
			Object o = getValueAt(row, i);

			if (isSimpleEditable(i) && o != null)
				v.addElement("?");
		}

		return v.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private method
	//---
	//---------------------------------------------------------------------------

	private void loadResultSet() throws SQLException
	{
		if (mode == TABLE)
		{
			String fields = eNode.getSelFields();

			sQuery  = "SELECT " + fields + " FROM " + sTable;

			if (!sWhere.equals(""))
			{
				if (sWhere.toLowerCase().startsWith("order "))
					sQuery += " " + sWhere;
				else
					sQuery += " WHERE " + sWhere;
			}
		}

		//--- To prevent a memory leak, make sure that the ResultSet rSet 
		//--- is closed before using it to hold the results of a new query.
		//---
		//--- Try to close the previous ResultSet, if any.
		//--- Different JDBC drivers leave ResultSet in different states on trying to execute a
		//--- query with invalid syntax.  Try to handle all possibilities.

		if (rSet != null)
			try 
			{ 
				if (rSet.getStatement() != null)
					rSet.getStatement().close();  
				else
					rSet.close();
			}
			catch (SQLException e) 
			{
				// Will end up here with MySQL jdbc driver (5.0.8) on closing query with incorrect syntax.
				// See discussion in http://bugs.mysql.com/bug.php?id=620 
				// as rSet.getStatement() throws an exception.
				// try again to close the ResultSet.
				try 
				{
					rSet.close();
				} 
				catch (SQLException er) 
				{  
					// If rSet didn't close, then setting it to null may not properly release resources
					// for garbage cleanup, but won't force user to restart Druid after issuing a query
					// with incorrect syntax.  See discussion on:
					// http://forum.java.sun.com/thread.jspa?forumID=48&threadID=727535
					rSet = null;
				} 
			}

		rSet  = jdbcConn.selectUpdate(sQuery);
		rMeta = rSet.getMetaData();
	}

	//---------------------------------------------------------------------------

	private boolean isSimpleEditable(int col)
	{
		return 	aColTypes[col].isBoolean() ||
				 	aColTypes[col].isNumber()  ||
					aColTypes[col].isString()  ||
					aColTypes[col].isTemporalType();
	}

	//---------------------------------------------------------------------------

	public ResultSet getNotEditableResultSet(int row, int col) throws SQLException, IOException
	{
		FlexTableColumn ftc = (FlexTableColumn) vColumns.elementAt(col);

		String colName = (String) ftc.getHeaderValue();

		String query = "SELECT " + colName + " FROM "+ sTable +
							" WHERE " + getWhereString(row);

		return jdbcConn.select(query, getWhereParameters(row));
	}

	//---------------------------------------------------------------------------

	private Vector getWhereParameters(int row)
	{
		Vector v = new Vector();

		for(int i=0; i<vColumns.size(); i++)
		{
			Object o = getValueAt(row, i);

			if (isSimpleEditable(i) && o != null)
				v.addElement(convertToParam(i, o));
		}

		return v;
	}

	//---------------------------------------------------------------------------

	private Object convertToParam(int col, Object o)
	{
		if (o == null)
			return null;

		if (aColTypes[col].isDate())
			return java.sql.Date.valueOf((String)o);

		if (aColTypes[col].isTime())
			return Time.valueOf((String)o);

		if (aColTypes[col].isTimeStamp())
			return Timestamp.valueOf((String)o);

		return o;
	}

	//---------------------------------------------------------------------------

	private String getWhereString(int row)
	{
		TVector v = new TVector();
		v.setSeparator(" AND ");

		for(int i=0; i<vColumns.size(); i++)
		{
			Object o = getValueAt(row, i);

			FlexTableColumn ftc = (FlexTableColumn) vColumns.elementAt(i);

			if (isSimpleEditable(i) && o != null)
				v.addElement(ftc.getHeaderValue() + " = ?");
		}

		return v.toString();
	}

	//---------------------------------------------------------------------------

	private void updateNotEditableValue(Object o, int row, int col) throws SQLException, IOException
	{
		FlexTableColumn ftc = (FlexTableColumn) vColumns.elementAt(col);

		String colName = (String) ftc.getHeaderValue();

		String query = "UPDATE " + sTable + " SET " + colName + "= ? WHERE " + getWhereString(row);

		Vector args = getWhereParameters(row);
		args.insertElementAt(o, 0);

		//------------------------------------------------------------------------
		//--- execute query

		jdbcConn.execute(query, args);
	}

	//---------------------------------------------------------------------------

	private void setFieldValue(Object o, int col) throws SQLException
	{
		if (o == null || o.toString().equals(""))
		{
			rSet.updateNull(col+1);
			return;
		}

		//------------------------------------------------------------------------

		if (aColTypes[col].isDate())
			rSet.updateDate(col+1, java.sql.Date.valueOf((String)o));

		//------------------------------------------------------------------------

		else if (aColTypes[col].isTime())
			rSet.updateTime(col+1, Time.valueOf((String)o));

		//------------------------------------------------------------------------

		else if (aColTypes[col].isTimeStamp())
			rSet.updateTimestamp(col+1, Timestamp.valueOf((String)o));

		//------------------------------------------------------------------------

		else
			rSet.updateObject(col+1, o);
	}

	//---------------------------------------------------------------------------

	private Object getFieldValue(int col) throws SQLException
	{
		try
		{
			return getFieldValueI(col);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			return "<EXCEPTION>";
		}
	}

	//---------------------------------------------------------------------------

	private Object getFieldValueI(int col) throws SQLException
	{
		if (aColTypes[col].isDate())
		{
			java.sql.Date d = rSet.getDate(col+1);

			return (d != null) ? d.toString() : null;
		}

		else if (aColTypes[col].isTime())
		{
			Time t = rSet.getTime(col+1);

			return (t != null) ? t.toString() : null;
		}


		else if (aColTypes[col].isTimeStamp())
		{
			Timestamp ts = rSet.getTimestamp(col+1);

			return (ts != null) ? ts.toString() : null;
		}

		//--- bigint, integer, smallint and tinyint

		else if (aColTypes[col].isInteger())
		{
			String sField = rSet.getString(col+1);

			return (sField != null) ? new Long(sField) : null;
		}

		//--- double, float, real, decimal, numeric

		else if (aColTypes[col].isReal())
		{
			String sField = rSet.getString(col+1);

			return (sField != null) ? new Double(sField) : null;
		}

		//--- boolean

		else if (aColTypes[col].isBoolean())
		{
			Object obj = rSet.getObject(col+1);

			return (obj != null ) ? Boolean.valueOf(rSet.getBoolean(col+1)) : null;
		}

		//--- char, varchar, longvarchar

		else if (aColTypes[col].isString())
			return rSet.getString(col+1);

		//--- other unknown stuff

		else
			return "<UNKNOWN>";
	}
}

//==============================================================================
