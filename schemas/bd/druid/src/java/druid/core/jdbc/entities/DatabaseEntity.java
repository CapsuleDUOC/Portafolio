//==============================================================================
//===
//===   DatabaseEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import ddf.lib.SqlMapper;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcLib;
import druid.core.jdbc.RecordList;
import druid.util.gui.ImageFactory;

//==============================================================================

public class DatabaseEntity extends SchemaEntity
{
	public RecordList rlKeywords;
	public RecordList rlMaxValues;
	public RecordList rlSql;
	public RecordList rlTransactions;
	public RecordList rlIdentifiers;
	public RecordList rlOther;
	public RecordList rlDataTypes;
	public RecordList rlCatalogs;
	public RecordList rlTableTypes;
	public RecordList rlResultSets;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DatabaseEntity(JdbcConnection conn)
	{
		super(conn, "Jdbc Database");

		setToolTipText("The database you are currently connected to");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Info retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException
	{
		super.loadInfoI();

		DatabaseMetaData dbmd = getJdbcConnection().getMetaData();

		rlKeywords     = retrieveKeywords(dbmd);
		rlSql          = retrieveSql(dbmd);
		rlMaxValues    = retrieveMaxValues(dbmd);
		rlTransactions = retrieveTrans(dbmd);
		rlIdentifiers  = retrieveIdent(dbmd);
		rlOther        = retrieveOther(dbmd);
		rlDataTypes    = retrieveDataTypes(dbmd);
		rlTableTypes   = getJdbcConnection().retrieveResultSet(dbmd.getTableTypes());
		rlResultSets   = retrieveResultSets(dbmd);

		try
		{
			rlCatalogs = getJdbcConnection().retrieveResultSet(dbmd.getCatalogs());
		}
		catch (SQLException e)
		{
			//--- Ignore. This could fail if the user doesn't have permissions
			//--- At least in mySQL --croy@rcresults.com

			//--- The recordlist cannot be empty. Use a default one
			//--- --acarboni@sourceforge.net

			rlCatalogs = getUnsupportedRL("Catalogs", e.getMessage());
		}
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveKeywords(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Keyword", 50);
		rl.addColumn("Type",    50);

		try
		{
			split(rl, "Sql keyword",      dbmd.getSQLKeywords());
			split(rl, "Numeric function", dbmd.getNumericFunctions());
			split(rl, "String function",  dbmd.getStringFunctions());
			split(rl, "System function",  dbmd.getSystemFunctions());
			split(rl, "Time / Date",      dbmd.getTimeDateFunctions());
		}
		catch(Exception e)
		{
			addEntry(rl, "<not supported>", "???");
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveSql(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Feature", 200);
		rl.addColumn("Supp",     10);

		//------------------------------------------------------------------------

		try
		{
			addEntry(rl, "ALTER TABLE with ADD column",  dbmd.supportsAlterTableWithAddColumn());
			addEntry(rl, "ALTER TABLE with DROP column", dbmd.supportsAlterTableWithDropColumn());
			addEntry(rl, "Column aliasing (AS clause)",  dbmd.supportsColumnAliasing());
			addEntry(rl, "Null + NotNull = Null",        dbmd.nullPlusNonNullIsNull());
			addEntry(rl, "Expressions in ORDER BY",      dbmd.supportsExpressionsInOrderBy());
			addEntry(rl, "ORDER BY unrelated",           dbmd.supportsOrderByUnrelated());
			addEntry(rl, "GROUP BY",                     dbmd.supportsGroupBy());
			addEntry(rl, "GROUP BY unrelated",           dbmd.supportsGroupByUnrelated());
			addEntry(rl, "GROUP BY beyond select",       dbmd.supportsGroupByBeyondSelect());
			addEntry(rl, "LIKE escape clause",           dbmd.supportsLikeEscapeClause());
			addEntry(rl, "Not Null columns",             dbmd.supportsNonNullableColumns());
			addEntry(rl, "ODBC Minimum Sql grammar",     dbmd.supportsMinimumSQLGrammar());
			addEntry(rl, "ODBC Core Sql grammar",        dbmd.supportsCoreSQLGrammar());
			addEntry(rl, "ODBC Extended Sql grammar",    dbmd.supportsExtendedSQLGrammar());
			addEntry(rl, "ANSI-92 Entry level grammar",  dbmd.supportsANSI92EntryLevelSQL());
			addEntry(rl, "ANSI-92 Intermediate grammar", dbmd.supportsANSI92IntermediateSQL());
			addEntry(rl, "ANSI-92 Full Sql grammar",     dbmd.supportsANSI92FullSQL());
			addEntry(rl, "Integrity enhancement facil.", dbmd.supportsIntegrityEnhancementFacility());
			addEntry(rl, "Outer joins",                  dbmd.supportsOuterJoins());
			addEntry(rl, "Full outer joins",             dbmd.supportsFullOuterJoins());
			addEntry(rl, "Limited outer joins",          dbmd.supportsLimitedOuterJoins());
			addEntry(rl, "Schemas in data manipulation", dbmd.supportsSchemasInDataManipulation());
			addEntry(rl, "Schemas in procedure calls",   dbmd.supportsSchemasInProcedureCalls());
			addEntry(rl, "Schemas in table definitions", dbmd.supportsSchemasInTableDefinitions());
			addEntry(rl, "Schemas in index definitions", dbmd.supportsSchemasInIndexDefinitions());
			addEntry(rl, "Schemas in privilege defin.",  dbmd.supportsSchemasInPrivilegeDefinitions());
			addEntry(rl, "Catalogs in data manipulation",dbmd.supportsCatalogsInDataManipulation());
			addEntry(rl, "Catalogs in procedure calls",  dbmd.supportsCatalogsInProcedureCalls());
			addEntry(rl, "Catalogs in table definitions",dbmd.supportsCatalogsInTableDefinitions());
			addEntry(rl, "Catalogs in index definitions",dbmd.supportsCatalogsInIndexDefinitions());
			addEntry(rl, "Catalogs in privilege defin.", dbmd.supportsCatalogsInPrivilegeDefinitions());
		}
		catch(Exception e)
		{
			addUnknownEntry(rl, "Basic metadata info");
		}

		//------------------------------------------------------------------------

		try
		{
			addEntry(rl, "Is catalog before table name", dbmd.isCatalogAtStart());
		}
		catch(SQLException e)
		{
			addUnknownEntry(rl, "Is catalog before table name");
		}

		//------------------------------------------------------------------------

		try
		{
			addEntry(rl, "Positioned DELETE",            dbmd.supportsPositionedDelete());
			addEntry(rl, "Positioned UPDATE",            dbmd.supportsPositionedUpdate());
			addEntry(rl, "SELECT for UPDATE",            dbmd.supportsSelectForUpdate());
			addEntry(rl, "Stored procedures",            dbmd.supportsStoredProcedures());
			addEntry(rl, "Subqueries in comparisons",    dbmd.supportsSubqueriesInComparisons());
			addEntry(rl, "Subqueries in EXISTS",         dbmd.supportsSubqueriesInExists());
			addEntry(rl, "Subqueries in IN",             dbmd.supportsSubqueriesInIns());
			addEntry(rl, "Subqueries in quantified exp.",dbmd.supportsSubqueriesInQuantifieds());
			addEntry(rl, "Correlated subqueries",        dbmd.supportsCorrelatedSubqueries());
			addEntry(rl, "UNION",                        dbmd.supportsUnion());
			addEntry(rl, "UNION ALL",                    dbmd.supportsUnionAll());
			addEntry(rl, "All proced. callable by you",  dbmd.allProceduresAreCallable());
			addEntry(rl, "All tables selectable by you", dbmd.allTablesAreSelectable());
			addEntry(rl, "Nulls are sorted high",        dbmd.nullsAreSortedHigh());
			addEntry(rl, "Nulls are sorted low",         dbmd.nullsAreSortedLow());
			addEntry(rl, "Nulls are sorted at the start",dbmd.nullsAreSortedAtStart());
			addEntry(rl, "Nulls are sorted at the end",  dbmd.nullsAreSortedAtEnd());
			addEntry(rl, "Conversion between types",     dbmd.supportsConvert());
			addEntry(rl, "Table correlation names",      dbmd.supportsTableCorrelationNames());
			addEntry(rl, "Different table corr. names",  dbmd.supportsDifferentTableCorrelationNames());
			addEntry(rl, "Multiple result sets",         dbmd.supportsMultipleResultSets());
			addEntry(rl, "Uses local files",             dbmd.usesLocalFiles());
			addEntry(rl, "Uses one file for each table", dbmd.usesLocalFilePerTable());
		}
		catch(Exception e)
		{
			addUnknownEntry(rl, "Basic metadata info");
		}

		//------------------------------------------------------------------------
		//--- this is a Jdbc 2.1 method and must be trapped because some
		//--- drivers may not implement it

		try
		{
			addEntry(rl, "Batch updates",             dbmd.supportsBatchUpdates());
		}
		catch(Throwable t)
		{
			addUnknownEntry(rl, "Batch updates");
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveMaxValues(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Entity", 150);
		rl.addColumn("Max",    10);

		try
		{
			addEntry(rl, "Binary literal length",    dbmd.getMaxBinaryLiteralLength());
			addEntry(rl, "Char literal length",      dbmd.getMaxCharLiteralLength());
			addEntry(rl, "Column name length",       dbmd.getMaxColumnNameLength());
			addEntry(rl, "Columns in GROUP BY",      dbmd.getMaxColumnsInGroupBy());
			addEntry(rl, "Columns in index",         dbmd.getMaxColumnsInIndex());
			addEntry(rl, "Columns in ORDER BY",      dbmd.getMaxColumnsInOrderBy());
			addEntry(rl, "Columns in SELECT",        dbmd.getMaxColumnsInSelect());
			addEntry(rl, "Columns in table",         dbmd.getMaxColumnsInTable());
			addEntry(rl, "Active connections",       dbmd.getMaxConnections());
			addEntry(rl, "Cursor name length",       dbmd.getMaxCursorNameLength());
			addEntry(rl, "Index length (bytes)",     dbmd.getMaxIndexLength());
			addEntry(rl, "Schema name length",       dbmd.getMaxSchemaNameLength());
			addEntry(rl, "Procedure name length",    dbmd.getMaxProcedureNameLength());
			addEntry(rl, "Catalog name length",      dbmd.getMaxCatalogNameLength());
			addEntry(rl, "Statement length (bytes)", dbmd.getMaxStatementLength());
			addEntry(rl, "Opened statements",        dbmd.getMaxStatements());
			addEntry(rl, "Table name length",        dbmd.getMaxTableNameLength());
			addEntry(rl, "Tables in SELECT",         dbmd.getMaxTablesInSelect());
			addEntry(rl, "User name length",         dbmd.getMaxUserNameLength());

			String s = "Row size";

			if (dbmd.doesMaxRowSizeIncludeBlobs())
				s += " (include blobs)";
			else
				s += " (without blobs)";

			addEntry(rl, s, dbmd.getMaxRowSize());
		}
		catch(Exception e)
		{
			addEntry(rl, "<not supported>",         0);
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveTrans(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Feature", 200);
		rl.addColumn("Supp",     10);

		try
		{
			addEntry(rl, "Transactions",                 dbmd.supportsTransactions());
			addEntry(rl, "Multiple transactions",        dbmd.supportsMultipleTransactions());
			addEntry(rl, "Open cursors across commit",   dbmd.supportsOpenCursorsAcrossCommit());
			addEntry(rl, "Open cursors across rollback", dbmd.supportsOpenCursorsAcrossRollback());
			addEntry(rl, "Open statem. across commit",   dbmd.supportsOpenStatementsAcrossCommit());
			addEntry(rl, "Open statem. across rollback", dbmd.supportsOpenStatementsAcrossRollback());
			addEntry(rl, "Data def and data man. trans.",dbmd.supportsDataDefinitionAndDataManipulationTransactions());
			addEntry(rl, "Data manipulat. trans. only",  dbmd.supportsDataManipulationTransactionsOnly());
			addEntry(rl, "Defin. causes trans. commit",  dbmd.dataDefinitionCausesTransactionCommit());
			addEntry(rl, "Data def. ignored in trans.",  dbmd.dataDefinitionIgnoredInTransactions());
		}
		catch(Exception e)
		{
			addUnknownEntry(rl, "<not supported>");
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveIdent(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Attribute", 200);
		rl.addColumn("Value",     20);

		try
		{
			addEntry(rl, "Supports mixed case",        dbmd.supportsMixedCaseIdentifiers());
			addEntry(rl, "Stores upper case",          dbmd.storesUpperCaseIdentifiers());
			addEntry(rl, "Stores lower case",          dbmd.storesLowerCaseIdentifiers());
			addEntry(rl, "Stores mixed case",          dbmd.storesMixedCaseIdentifiers());
			addEntry(rl, "Supports mixed case quoted", dbmd.supportsMixedCaseQuotedIdentifiers());
			addEntry(rl, "Stores upper case quoted",   dbmd.storesUpperCaseQuotedIdentifiers());
			addEntry(rl, "Stores lower case quoted",   dbmd.storesLowerCaseQuotedIdentifiers());
			addEntry(rl, "Stores mixed case quoted",   dbmd.storesMixedCaseQuotedIdentifiers());
		}
		catch(Exception e)
		{
			addUnknownEntry(rl, "<not supported>");
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveOther(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Attribute", 200);
		rl.addColumn("Value",     40);

		//------------------------------------------------------------------------

		try
		{
			addEntry(rl, "Search string escape",   dbmd.getSearchStringEscape());
			addEntry(rl, "Extra name characters",  dbmd.getExtraNameCharacters());
			addEntry(rl, "Schema term",            dbmd.getSchemaTerm());
			addEntry(rl, "Procedure term",         dbmd.getProcedureTerm());
			addEntry(rl, "Catalog term",           dbmd.getCatalogTerm());
			addEntry(rl, "Identifier quote string",dbmd.getIdentifierQuoteString());
		}
		catch(Exception e)
		{
			addEntry(rl, "<not supported>", "????");
		}

		//------------------------------------------------------------------------

		try
		{
			addEntry(rl, "Catalog separator",  dbmd.getCatalogSeparator());
		}
		catch(SQLException e)
		{
			addEntry(rl, "Catalog separator",  "????");
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveDataTypes(DatabaseMetaData dbmd) throws SQLException
	{
		//--- it is useless to trap datatypes
		//--- they are needed for the database retrieval
		//--- if there is an error, we give up

		RecordList rl = getJdbcConnection().retrieveResultSet(dbmd.getTypeInfo());

		rl.removeColumn(17);
		rl.removeColumn(16);
		rl.removeColumn(15);
		rl.removeColumn(14);
		rl.removeColumn(13);
		rl.removeColumn(12);
		rl.removeColumn(9);
		rl.removeColumn(5);
		rl.removeColumn(4);
		rl.removeColumn(3);

		rl.setColumn(0, "Dbms type",  90);
		rl.setColumn(1, "Maps to",    90);
		rl.setColumn(2, "Precis",     30);
		rl.setColumn(3, "Null",       10);
		rl.setColumn(4, "Case",       10);
		rl.setColumn(5, "Searc",      10);
		rl.setColumn(6, "Fixed",      10);
		rl.setColumn(7, "Incr",       10);

		for(int i=0; i<rl.getRowCount(); i++)
		{
			rl.setValueAt(SqlMapper.mapId(rl.getValueAt(i, 1)).sName, i, 1);
			rl.setValueAt(Boolean.valueOf(JdbcLib.convertNullType(rl.getValueAt(i,3))),    i, 3);
			rl.setValueAt(Boolean.valueOf(JdbcLib.convertBool(rl.getValueAt(i, 4))),   i, 4);
			rl.setValueAt(Boolean.valueOf(JdbcLib.convertSearch(rl.getValueAt(i,5))),  i, 5);
			rl.setValueAt(Boolean.valueOf(JdbcLib.convertBool(rl.getValueAt(i, 6))),   i, 6);
			rl.setValueAt(Boolean.valueOf(JdbcLib.convertBool(rl.getValueAt(i, 7))),   i, 7);
		}

		return rl;
	}

	//---------------------------------------------------------------------------

	private RecordList retrieveResultSets(DatabaseMetaData dbmd) throws SQLException
	{
		RecordList rl = new RecordList();

		rl.addColumn("Feature",  100);
		rl.addColumn("ForwOnly",  30);
		rl.addColumn("ScrInsens", 30);
		rl.addColumn("ScrSens",   30);

		int fo = ResultSet.TYPE_FORWARD_ONLY;
		int si = ResultSet.TYPE_SCROLL_INSENSITIVE;
		int ss = ResultSet.TYPE_SCROLL_SENSITIVE;

		//--- these are Jdbc 2.1 methods and must be trapped because some
		//--- drivers may not implement them

		try
		{
			addEntry(rl, "Result sets type",				dbmd.supportsResultSetType(fo),
																	dbmd.supportsResultSetType(si),
																	dbmd.supportsResultSetType(ss));

			addEntry(rl, "Own inserts are visible",	dbmd.ownInsertsAreVisible(fo),
																	dbmd.ownInsertsAreVisible(si),
																	dbmd.ownInsertsAreVisible(ss));

			addEntry(rl, "Own updates are visible",	dbmd.ownUpdatesAreVisible(fo),
																	dbmd.ownUpdatesAreVisible(si),
																	dbmd.ownUpdatesAreVisible(ss));

			addEntry(rl, "Own deletes are visible",	dbmd.ownDeletesAreVisible(fo),
																	dbmd.ownDeletesAreVisible(si),
																	dbmd.ownDeletesAreVisible(ss));

			addEntry(rl, "Others inserts are visible",dbmd.othersInsertsAreVisible(fo),
																	dbmd.othersInsertsAreVisible(si),
																	dbmd.othersInsertsAreVisible(ss));

			addEntry(rl, "Others updates are visible",dbmd.othersUpdatesAreVisible(fo),
																	dbmd.othersUpdatesAreVisible(si),
																	dbmd.othersUpdatesAreVisible(ss));

			addEntry(rl, "Others deletes are visible",dbmd.othersDeletesAreVisible(fo),
																	dbmd.othersDeletesAreVisible(si),
																	dbmd.othersDeletesAreVisible(ss));

			addEntry(rl, "Inserts are detected",		dbmd.insertsAreDetected(fo),
																	dbmd.insertsAreDetected(si),
																	dbmd.insertsAreDetected(ss));

			addEntry(rl, "Updates are detected",		dbmd.updatesAreDetected(fo),
																	dbmd.updatesAreDetected(si),
																	dbmd.updatesAreDetected(ss));

			addEntry(rl, "Deletes are detected",		dbmd.deletesAreDetected(fo),
																	dbmd.deletesAreDetected(si),
																	dbmd.deletesAreDetected(ss));
		}
		catch(Throwable t)
		{
			addEntry(rl, "<not supported>", false, false, false);
		}

		return rl;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void addUnknownEntry(RecordList rl, String name)
	{
		rl.newRecord();

		rl.addToRecord(name);
		rl.addToRecord(ImageFactory.CHKBOX_UNKNOWN);
	}

	//---------------------------------------------------------------------------

	private void addEntry(RecordList rl, String name, boolean value)
	{
		rl.newRecord();

		rl.addToRecord(name);
		rl.addToRecord(getBoolImage(value));
	}

	//---------------------------------------------------------------------------

	private void addEntry(RecordList rl, String name, int value)
	{
		rl.newRecord();

		rl.addToRecord(name);
		rl.addToRecord(new Integer(value));
	}

	//---------------------------------------------------------------------------

	private void addEntry(RecordList rl, String name, String value)
	{
		rl.newRecord();

		rl.addToRecord(name);
		rl.addToRecord(value);
	}

	//---------------------------------------------------------------------------

	private void addEntry(RecordList rl, String name, boolean v1, boolean v2, boolean v3)
	{
		rl.newRecord();

		rl.addToRecord(name);
		rl.addToRecord(Boolean.valueOf(v1));
		rl.addToRecord(Boolean.valueOf(v2));
		rl.addToRecord(Boolean.valueOf(v3));
	}

	//---------------------------------------------------------------------------

	private ImageIcon getBoolImage(boolean value)
	{
		return (value ? ImageFactory.CHKBOX_SET : ImageFactory.CHKBOX_UNSET);
	}

	//---------------------------------------------------------------------------

	private void split(RecordList rl, String type, String s)
	{
		StringTokenizer st = new StringTokenizer(s, ",");

		while(st.hasMoreTokens())
		{
			rl.newRecord();

			rl.addToRecord(st.nextToken().trim());
			rl.addToRecord(type);
		}
	}

	//---------------------------------------------------------------------------

	private RecordList getUnsupportedRL(String header, String message)
	{
		RecordList rl = new RecordList();

		rl.addColumn(header, 100);

		rl.newRecord();
		rl.addToRecord(message);

		return rl;
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

		if (meta.supportsSchemasInDataManipulation())
		{
			RecordList rl = conn.retrieveResultSet(meta.getSchemas());

			for(int i=0; i<rl.getRowCount(); i++)
			{
				String name = (String) rl.getRecordAt(i).elementAt(0);

				SchemaEntity schema = new SchemaEntity(conn, name);

				add(schema);
			}
		}

		else
			super.loadStructI();
	}
}

//==============================================================================
