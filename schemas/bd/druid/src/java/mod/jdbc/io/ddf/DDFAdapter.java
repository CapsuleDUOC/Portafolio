//==============================================================================
//===
//===   DDFAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.jdbc.io.ddf;

import java.sql.SQLException;
import java.util.List;

import ddf.exp.DDFExportListener;
import ddf.imp.DDFImportListener;
import ddf.imp.DDFReaderListener.OperationType;
import ddf.type.QueryField;
import druid.interfaces.RecordIOModule.ExportListener;
import druid.interfaces.RecordIOModule.ImportListener;

//==============================================================================

public class DDFAdapter implements DDFImportListener, DDFExportListener
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------
	
	public DDFAdapter(ImportListener l)
	{
		importListener = l;
	}

	//---------------------------------------------------------------------------
	
	public DDFAdapter(ExportListener l)
	{
		exportListener = l;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Methods
	//---
	//---------------------------------------------------------------------------

	@Override
	public void insertingRow(List<QueryField> fields, List<Object> row, long recordNum)
	{
		importListener.insertingRow(fields, row, recordNum);
	}
	
	//---------------------------------------------------------------------------

	@Override
	public void updatingRow(List<QueryField> fields, List<Object> row, long recordNum) 
	{
		importListener.updatingRow(fields, row, recordNum);
	}

	//---------------------------------------------------------------------------

	@Override
	public void deletingRow(List<QueryField> fields, List<Object> row, long recordNum) 
	{
		importListener.deletingRow(fields, row, recordNum);
	}

	//---------------------------------------------------------------------------

	@Override
	public ActionType onInsertError(SQLException e, long recordNum, String line)
	{
		ImportListener.ActionType at = importListener.onInsertError(e, recordNum, line);
		
		if (at == ImportListener.ActionType.ABORT)
			return ActionType.ABORT;
		
		if (at == ImportListener.ActionType.RETRY)
			return ActionType.RETRY;

		if (at == ImportListener.ActionType.SKIP)
			return ActionType.SKIP;
		
		return ActionType.SKIP_ALL;
	}

	//---------------------------------------------------------------------------
	
	@Override
	public ActionType onUpdateError(SQLException e, long recordNum, String line) 
	{
		ImportListener.ActionType at = importListener.onUpdateError(e, recordNum, line);
		
		if (at == ImportListener.ActionType.ABORT)
			return ActionType.ABORT;
		
		if (at == ImportListener.ActionType.RETRY)
			return ActionType.RETRY;

		if (at == ImportListener.ActionType.SKIP)
			return ActionType.SKIP;
		
		return ActionType.SKIP_ALL;
	}

	//---------------------------------------------------------------------------

	@Override
	public ActionType onDeleteError(SQLException e, long recordNum, String line) 
	{
		ImportListener.ActionType at = importListener.onDeleteError(e, recordNum, line);
		
		if (at == ImportListener.ActionType.ABORT)
			return ActionType.ABORT;
		
		if (at == ImportListener.ActionType.RETRY)
			return ActionType.RETRY;

		if (at == ImportListener.ActionType.SKIP)
			return ActionType.SKIP;
		
		return ActionType.SKIP_ALL;
	}

	//---------------------------------------------------------------------------

	@Override
	public void exportedRow(List<Object> row, long recordNum)
	{
		exportListener.exportedRow(row, recordNum);
	}

	//---------------------------------------------------------------------------

	@Override
	public void handlePostRow(OperationType type, long recordNum, String line) throws SQLException {}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------
	
	private ImportListener importListener;
	private ExportListener exportListener;
}

//==============================================================================
