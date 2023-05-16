//==============================================================================
//===
//===   RecordBasedEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.dlib.tools.TVector;

import ddf.lib.SqlMapper;
import ddf.type.SqlType;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcLib;
import druid.core.jdbc.RecordList;

//==============================================================================

public abstract class RecordBasedEntity extends AbstractEntity
{
	public RecordList rlBasicInfo;
	public RecordList rlFKeys;
	public RecordList rlExpKeys;
	public RecordList rlPriv;
	public RecordList rlColPriv;

	//--- type (SqlType), size (string)
	public RecordList rlFieldsInt;
	public RecordList rlFKeysInt;

	//--- selection (GUI related)

	public RecordList rlSelectedFields;

	public String where;
	
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public RecordBasedEntity(JdbcConnection conn, String name, String type, String rems)
	{
		super(conn, name, type, rems);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Selection methods
	//---      selection refers to selected columns to show on screen
	//---
	//---------------------------------------------------------------------------

	public int getSelFieldsCount()
	{
		return rlSelectedFields.getRowCount();
	}

	//---------------------------------------------------------------------------

	public String getSelFields()
	{
		RecordList rl = rlSelectedFields;

		TVector res = new TVector();

		for(int i=0; i<rl.getRowCount(); i++)
		{
			String  field = (String)  rl.getValueAt(i,0);
			Boolean show  = (Boolean) rl.getValueAt(i,1);

			if (show.booleanValue())
				res.addElement(field);
		}

		if (res.size() == 0) return "*";

		return res.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException
	{
		loadBasicInfo();
		loadFKeys();
		loadExpKeys();
		loadPrimaryKey();
		loadPriv();
		loadColPriv();
	}

	//---------------------------------------------------------------------------

	private void loadBasicInfo() throws SQLException
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		ResultSet  rs = jdbcConn.getMetaData().getColumns(null, getSchema(), sName, "%");
		RecordList rl = jdbcConn.retrieveResultSet(rs);

		rl.removeColumn(17);
		rl.removeColumn(16);
		rl.removeColumn(15);
		rl.removeColumn(14);
		rl.removeColumn(13);
		rl.removeColumn(9);
		rl.removeColumn(7);
		rl.removeColumn(2);
		rl.removeColumn(1);
		rl.removeColumn(0);

		//--- now we have:
		//---   fieldname (str)
		//---   datatype (int)
		//---   datatype (DBMS string)
		//---   fieldsize (int)
		//---   decimal digits (int)
		//---   isnullable (int)
		//---   default (str)
		//---   remarks (str)

		rlBasicInfo = new RecordList();

		rlBasicInfo.addColumn("Field",    250);
		rlBasicInfo.addColumn("DataType", 250);
		rlBasicInfo.addColumn("DbmsType", 250);
		rlBasicInfo.addColumn("NotNull",  100);
		rlBasicInfo.addColumn("Default",  100);
		rlBasicInfo.addColumn("Remarks",  250);

		rlFieldsInt = new RecordList();

		rlFieldsInt.addColumn("sqltype", 100);
		rlFieldsInt.addColumn("size",    100);

		rlSelectedFields = new RecordList();

		rlSelectedFields.addColumn("Field", 130);
		rlSelectedFields.addColumn("Show",   30);

		rlSelectedFields.getColumnAt(1).setEditable(true);

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector row = rl.getRecordAt(i);

			String  name  = JdbcLib.convertString(row.elementAt(0));
			Object  type  = row.elementAt(1);
			String  dbtype= JdbcLib.convertString(row.elementAt(2));
			Object  size  = row.elementAt(3);
			Object  decim = row.elementAt(4);
			Boolean bNotN = Boolean.valueOf(JdbcLib.convertNullField(row.elementAt(5)));
			String  sRmks = JdbcLib.convertString(row.elementAt(6));
			String  sDef  = JdbcLib.convertString(row.elementAt(7));

			SqlType sqlType = SqlMapper.map(type, dbtype, decim, jdbcConn.getConnection());
			String  sSize   = JdbcLib.convertSize(size, decim);

			//--- build first field list

			rlBasicInfo.newRecord();
			rlBasicInfo.addToRecord(name);

			StringBuffer sType = new StringBuffer(sqlType.sName);

			if ((sSize != null) && (sqlType.iSize != SqlType.CONST))
				sType.append("(").append(sSize).append(")");

			rlBasicInfo.addToRecord(sType.toString());
			rlBasicInfo.addToRecord(dbtype);
			rlBasicInfo.addToRecord(bNotN);
			rlBasicInfo.addToRecord(sDef);
			rlBasicInfo.addToRecord(sRmks);

			//--- build second field list

			rlFieldsInt.newRecord();
			rlFieldsInt.addToRecord(sqlType);
			rlFieldsInt.addToRecord(sSize);

			//--- build output fields

			rlSelectedFields.newRecord();
			rlSelectedFields.addToRecord(name);
			rlSelectedFields.addToRecord(Boolean.TRUE);
		}
	}

	//---------------------------------------------------------------------------

	private void loadFKeys() throws SQLException
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		ResultSet rs = null;

		try
		{
			 rs = getJdbcConnection().getMetaData().getImportedKeys(null, getSchema(), sName);
		}
		catch (SQLException e)
		{
			// Ignore. Older versions of postgres will throw and exception here
			// If the development version of the postgres jdbc driver is
			// used on a < 7.1 database it will throw this exception because
			// one of the system tables is missing a required column.
			// --croy@rcresults.com
		}
		RecordList rl = (rs == null) ? new RecordList() : jdbcConn.retrieveResultSet(rs);

		//------------------------------------------------------------------------

		rlFKeys = new RecordList();

		rlFKeys.addColumn("Field",      140);
		rlFKeys.addColumn("References", 180);
		rlFKeys.addColumn("On Update",  90);
		rlFKeys.addColumn("On Delete",  90);

		//------------------------------------------------------------------------

		rlFKeysInt = new RecordList();

		rlFKeysInt.addColumn("field-id", 100);
		rlFKeysInt.addColumn("schema",   100);
		rlFKeysInt.addColumn("table",    100);
		rlFKeysInt.addColumn("field",    100);
		rlFKeysInt.addColumn("update",   100);
		rlFKeysInt.addColumn("delete",   100);

		//------------------------------------------------------------------------

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector row = rl.getRecordAt(i);

			String pkTable  = JdbcLib.convertString(row.elementAt(2));
			String pkField  = JdbcLib.convertString(row.elementAt(3));

			String field    = JdbcLib.convertString(row.elementAt(7));
			String onUpdate = JdbcLib.convertString(row.elementAt(9));
			String onDelete = JdbcLib.convertString(row.elementAt(10));

			rlFKeys.newRecord();
			rlFKeys.addToRecord(field);
			rlFKeys.addToRecord(pkTable + "(" + pkField + ")");
			rlFKeys.addToRecord(JdbcLib.convertOnRule(onUpdate));
			rlFKeys.addToRecord(JdbcLib.convertOnRule(onUpdate));

			rlFKeysInt.newRecord();
			rlFKeysInt.addToRecord(field);
			rlFKeysInt.addToRecord(pkTable);
			rlFKeysInt.addToRecord(pkField);
			rlFKeysInt.addToRecord(JdbcLib.convertOnRuleInt(onUpdate));
			rlFKeysInt.addToRecord(JdbcLib.convertOnRuleInt(onDelete));
		}
	}

	//---------------------------------------------------------------------------

	private void loadExpKeys() throws SQLException
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		ResultSet  rs = jdbcConn.getMetaData().getExportedKeys(null, getSchema(), sName);
		RecordList rl = (rs==null) ? new RecordList() : jdbcConn.retrieveResultSet(rs);

		//------------------------------------------------------------------------

		rlExpKeys = new RecordList();

		rlExpKeys.addColumn("Table",  100);
		rlExpKeys.addColumn("Field",  100);

		//------------------------------------------------------------------------

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector row = rl.getRecordAt(i);

			String fTable  = JdbcLib.convertString(row.elementAt(6));
			String fField  = JdbcLib.convertString(row.elementAt(7));

			rlExpKeys.newRecord();
			rlExpKeys.addToRecord(fTable);
			rlExpKeys.addToRecord(fField);
		}
	}

	//---------------------------------------------------------------------------

	private void loadPrimaryKey() throws SQLException
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		ResultSet  rs = jdbcConn.getMetaData().getPrimaryKeys(null, getSchema(), sName);
		RecordList rl = jdbcConn.retrieveResultSet(rs);

		rlBasicInfo.insertColumn(3, "PrKey", 100);

		for(int i=0; i<rlBasicInfo.getRowCount(); i++)
		{
			String field = JdbcLib.convertString(rlBasicInfo.getValueAt(i, 0));

			boolean found = false;

			for(int j=0; j<rl.getRowCount(); j++)
			{
				String pkField = JdbcLib.convertString(rl.getValueAt(j, 3));

				if ((field != null) && (pkField != null))
					if (field.equals(pkField))
					{
						found = true;
						break;
					}
			}

			rlBasicInfo.setValueAt(Boolean.valueOf(found), i, 3);

			//--- we must unset the 'not null' attrib for primary keys
			if (found)
				rlBasicInfo.setValueAt(Boolean.FALSE, i, 4);
		}
	}

	//---------------------------------------------------------------------------

	private void loadPriv()
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		RecordList rl = null;

		try
		{
			ResultSet rs = jdbcConn.getMetaData().getTablePrivileges(null, getSchema(), sName);

			if (rs == null)
			{
				rl = new RecordList();
				rl.addColumn("???????", 100);

				rl.newRecord();
				rl.addToRecord("Not supported by JDBC Driver");
			}
			else
			{
				rl = jdbcConn.retrieveResultSet(rs);

				rl.removeColumn(2);
				rl.removeColumn(1);
				rl.removeColumn(0);
			}
		}
		catch(SQLException e)
		{
			rl = new RecordList();
			rl.addColumn("Exception", 100);

			rl.newRecord();
			rl.addToRecord(e.getMessage());
		}

		rlPriv = rl;
	}

	//---------------------------------------------------------------------------

	private void loadColPriv()
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		RecordList rl = null;

		try
		{
			ResultSet rs = jdbcConn.getMetaData().getColumnPrivileges(null, getSchema(), sName, "%");

			if (rs == null)
			{
				rl = new RecordList();
				rl.addColumn("???????", 100);
				rl.newRecord();
				rl.addToRecord("Not supported by JDBC Driver");
			}
			else
			{
				rl = jdbcConn.retrieveResultSet(rs);

				rl.removeColumn(2);
				rl.removeColumn(1);
				rl.removeColumn(0);
			}
		}
		catch(SQLException e)
		{
			rl = new RecordList();
			rl.addColumn("Exception", 100);

			rl.newRecord();
			rl.addToRecord(e.getMessage());
		}

		rlColPriv = rl;
	}
}

//==============================================================================
