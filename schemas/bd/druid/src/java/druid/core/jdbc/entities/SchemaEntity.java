//==============================================================================
//===
//===   SchemaEntity
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

public class SchemaEntity extends ContainerEntity
{
	private RecordList rlProcFunc;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SchemaEntity(JdbcConnection conn, String name)
	{
		super(conn, name, "SCHEMA", null);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public RecordList getProcFunc() throws SQLException
	{
		if (rlProcFunc == null)
		{
			JdbcConnection   conn = getJdbcConnection();
			DatabaseMetaData meta = conn.getMetaData();

			rlProcFunc = conn.retrieveResultSet(meta.getProcedures(null, getSchema(), "%"));
		}

		return rlProcFunc;
	}

	//---------------------------------------------------------------------------

	public void reset()
	{
		rlProcFunc = null;

		super.reset();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadStructI() throws SQLException
	{
		JdbcConnection   conn = getJdbcConnection();
		DatabaseMetaData meta = conn.getMetaData();

		//--- create children subfolders

		TableList     tables    = new TableList(conn);
		ViewList      views     = new ViewList(conn);
		SequenceList  sequences = new SequenceList(conn);
		SystemList    system    = new SystemList(conn);

		//--- retrieve all database objects
		//--- read meta data for TABLE, VIEW SYNONYM and SEQUENCE explicitly
		//--- (otherwise sequences are not properly read by ORACLE)

		RecordList rl = conn.retrieveResultSet(meta.getTables(null, getSchema(), "%",
										new String[]{"TABLE", "VIEW", "SYNONYM", "SEQUENCE" }));

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector v = rl.getRecordAt(i);

			String name = (String) v.elementAt(2);
			String type = (String) v.elementAt(3);
			String rems = (String) v.elementAt(4);

			if (type == null) type = "????";

			type = type.toUpperCase();

			if (type.equals(TableEntity.TYPE))
				tables.add(new TableEntity(conn, name, rems));

			else if (type.equals(ViewEntity.TYPE))
				views.add(new ViewEntity(conn, name, rems));

			else if (type.equals(SynonymEntity.TYPE))
				system.add(new SynonymEntity(conn, name, rems));

			else if (type.equals(SequenceEntity.TYPE))
				sequences.add(new SequenceEntity(conn, name, rems));

			else if (type.indexOf("SYSTEM") != -1)
			{
				if (type.indexOf(TableEntity.TYPE) != -1)
					system.add(new TableEntity(conn, name, rems));

				else if (type.indexOf(ViewEntity.TYPE) != -1)
					system.add(new ViewEntity(conn, name, rems));

				else if (type.indexOf(SynonymEntity.TYPE) != -1)
					system.add(new SynonymEntity(conn, name, rems));

				else if (type.indexOf(SequenceEntity.TYPE) != -1)
					system.add(new SequenceEntity(conn, name, rems));

				else
					system.add(new OtherEntity(conn, name, type, rems));
			}

			else
				system.add(new OtherEntity(conn, name, type, rems));
		}

		//--- retrieve UDTs

		try
		{
			rl = conn.retrieveResultSet(meta.getUDTs(null, getSchema(), "%", null));

			for(int i=0; i<rl.getRowCount(); i++)
			{
				Vector v = rl.getRecordAt(i);

				String name = (String) v.elementAt(2);
				String rems = (String) v.elementAt(5);

				system.add(new UDTEntity(conn, name, rems));
			}
		}
		catch(SQLException e)
		{
			//--- Ok, maybe UDTs are not supported
		}

		//--- add all retrieved information to the schema

		addIfCase(tables);
		addIfCase(views);

		add(new ProcedureList(conn));
		add(new FunctionList (conn));

		addIfCase(sequences);
		addIfCase(system);
	}

	//---------------------------------------------------------------------------

	private void addIfCase(AbstractEntity child)
	{
		if (child.getChildCount() != 0)
		{
			child.setName(child.sName + " (" + child.getChildCount() + ")");
			add(child);
		}
	}
}

//==============================================================================
