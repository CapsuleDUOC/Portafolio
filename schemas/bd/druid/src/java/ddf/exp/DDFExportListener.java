//==============================================================================
//===
//===   DDFExportListener
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.exp;

import java.util.List;

//==============================================================================

public interface DDFExportListener
{
	public void exportedRow(List<Object> row, long recordNum);	
}

//==============================================================================
