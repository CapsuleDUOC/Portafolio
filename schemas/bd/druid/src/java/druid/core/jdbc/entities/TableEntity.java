//==============================================================================
//===
//===   TableEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.dlib.gui.flextable.FlexTableColumn;

import druid.core.DataTracker;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcLib;
import druid.core.jdbc.RecordList;

//==============================================================================

public class TableEntity extends RecordBasedEntity
{
	public static final String TYPE = "TABLE";

	public RecordList rlIndexesInt;

	public int iIndexes;
	public int iUnqIndexes;

	public TriggerEntity triggers = new TriggerEntity();

	//--- constraints support
	public ConstraintEntity constraints = new ConstraintEntity();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TableEntity(JdbcConnection conn, String name, String rems)
	{
		super(conn, name, TYPE, rems);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException
	{
		super.loadInfoI();

		loadTableIndexes();

		try
		{
			triggers    = new TriggerEntity();
			constraints = new ConstraintEntity();

			DataTracker.beginDisabledSection();
			getJdbcConnection().getSqlAdapter().retrieveTriggers(this);
			getJdbcConnection().getSqlAdapter().retrieveConstraints(this);
		}
		finally
		{
			DataTracker.endDisabledSection();
		}
	}

	//---------------------------------------------------------------------------
	//--- synonyms and views don't have indexes.
	//--- if the getIndexInfo is called, the jdbc driver issues a sql-exception

	private void loadTableIndexes() throws SQLException
	{
		JdbcConnection jdbcConn = getJdbcConnection();

		rlIndexesInt = new RecordList();

		ResultSet rs = null;

		try
		{
			rs = jdbcConn.getMetaData().getIndexInfo(null, getSchema(), sName, false, true);
		}
		catch (NullPointerException npe)
		{
			// The postgres driver has a bug. --croy@rcresults.com
		}

		RecordList rl = (rs==null) ? new RecordList() : jdbcConn.retrieveResultSet(rs);

		rlIndexesInt.addColumn("index-name", 100);
		rlIndexesInt.addColumn("unique",     100);
		rlIndexesInt.addColumn("order",      100);
		rlIndexesInt.addColumn("fields",     100);

		//------------------------------------------------------------------------

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector row = rl.getRecordAt(i);

			Boolean uniq = Boolean.valueOf(JdbcLib.convertNotBool(row.elementAt(3)));
			String  name = JdbcLib.convertString(row.elementAt(5));
			String  pos  = JdbcLib.convertString(row.elementAt(7));
			String  field= JdbcLib.convertString(row.elementAt(8));
			String  order= JdbcLib.convertString(row.elementAt(9));

			if (order == null) order = "A";

			if ((name == null) || (field == null)) continue;

			//--- we must exclude system indexes

			if ((pos != null) && (!pos.equals("0")))
			{
				//-- we must skip indexes relative to primary keys

				boolean found = false;

				for(int j=0; j<rlBasicInfo.getRowCount(); j++)
				{
					Vector  tiRow   = rlBasicInfo.getRecordAt(j);
					String  tiField = (String)  tiRow.elementAt(0);
					Boolean tiPK    = (Boolean) tiRow.elementAt(3);

					if (field.equals(tiField) && tiPK.booleanValue())
					{
						found = true;
						break;
					}
				}

				if (found) continue;

				//--- check if index is already in recordlist

				int fpos = -1;

				for(int j=0; j<rlIndexesInt.getRowCount(); j++)
				{
					String currName = (String) rlIndexesInt.getValueAt(j, 0);

					if (name.equals(currName))
					{
						fpos = j;
						break;
					}
				}

				//--- add or update index

				if (fpos == -1)
				{
					rlIndexesInt.newRecord();
					rlIndexesInt.addToRecord(name);
					rlIndexesInt.addToRecord(uniq);
					rlIndexesInt.addToRecord(order);

					Vector f = new Vector();
					f.addElement(field);

					rlIndexesInt.addToRecord(f);
				}
				else
				{
					Vector f = (Vector) rlIndexesInt.getValueAt(fpos, 3);
					f.addElement(field);
				}
			}
		}

		//------------------------------------------------------------------------
		//--- indexes retrieved. Now build rlTableInfo

		iIndexes    = 0;
		iUnqIndexes = 0;

		for(int i=0; i<rlIndexesInt.getRowCount(); i++)
		{
			Vector index = rlIndexesInt.getRecordAt(i);

			boolean unique = ((Boolean)index.elementAt(1)).booleanValue();
			Vector  fields =  (Vector) index.elementAt(3);

			if (unique)
			{
				iUnqIndexes++;
				rlBasicInfo.addColumn("U-Idx" + iUnqIndexes, 100);
			}
			else
			{
				iIndexes++;
				rlBasicInfo.addColumn("Idx" + iIndexes, 100);
			}

			for(int j=0; j<rlBasicInfo.getRowCount(); j++)
			{
				Vector row = rlBasicInfo.getRecordAt(j);

				String field = (String) row.elementAt(0);

				boolean found = false;

				for(int k=0; k<fields.size(); k++)
				{
					String ifield = (String) fields.elementAt(k);

					if (field.equals(ifield))
					{
						found = true;
						break;
					}
				}

				row.addElement(Boolean.valueOf(found));
			}

			//--- some drivers have indexes with internal fields
			//--- in this case the index column is empty and must
			//--- be removed

			boolean isPresent = false;

			for(int j=0; j<rlBasicInfo.getRowCount(); j++)
			{
				Vector  row   = rlBasicInfo.getRecordAt(j);
				boolean isSet = ((Boolean) row.elementAt(row.size()-1)).booleanValue();

				if (isSet)
				{
					isPresent = true;
					break;
				}
			}

			if (!isPresent)
			{
				int colCount = rlBasicInfo.getColumnCount();

				FlexTableColumn ftc = rlBasicInfo.getColumnAt(colCount -1);

				if (ftc.getHeaderValue().toString().toLowerCase().startsWith("u"))
					iUnqIndexes--;
				else
					iIndexes--;

				rlBasicInfo.removeColumn(colCount -1);
			}
		}
	}
}

//==============================================================================
