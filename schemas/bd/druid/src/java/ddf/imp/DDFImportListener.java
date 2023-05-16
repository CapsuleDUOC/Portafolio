//==============================================================================
//===
//===   DDFImportListener
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.imp;

import java.sql.SQLException;
import java.util.List;

import ddf.imp.DDFReaderListener.OperationType;
import ddf.type.QueryField;

//==============================================================================

public interface DDFImportListener
{
	public enum ActionType { RETRY, SKIP, SKIP_ALL, ABORT };

	public void insertingRow(List<QueryField> fields, List<Object> row, long recordNum);
	public void updatingRow (List<QueryField> fields, List<Object> row, long recordNum);
	public void deletingRow (List<QueryField> fields, List<Object> row, long recordNum);
	
	public ActionType onInsertError(SQLException e, long recordNum, String line) throws SQLException;
	public ActionType onUpdateError(SQLException e, long recordNum, String line) throws SQLException;
	public ActionType onDeleteError(SQLException e, long recordNum, String line) throws SQLException;

	public void handlePostRow(OperationType type, long recordNum, String line) throws SQLException;
}

//==============================================================================
