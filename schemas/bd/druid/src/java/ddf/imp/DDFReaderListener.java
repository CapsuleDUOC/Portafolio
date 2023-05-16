//==============================================================================
//===
//===   DDFReaderListener
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.imp;

import java.sql.SQLException;
import java.util.List;

import ddf.type.DDFInfo;
import ddf.type.QueryField;

//==============================================================================

public interface DDFReaderListener
{
	public enum OperationType { INSERT, UPDATE, DELETE };

	public void handleInfo(DDFInfo info);

	public void handleQueryFields(List<QueryField> fields) throws SQLException;

	public boolean handleInsertRow(List<Object> row, String line, long recordNum) throws SQLException;
	public boolean handleUpdateRow(List<Object> row, String line, long recordNum) throws SQLException;
	public boolean handleDeleteRow(List<Object> row, String line, long recordNum) throws SQLException;
	
	public void handlePostRow(OperationType type, String line, long recordNum) throws SQLException;
}

//==============================================================================
