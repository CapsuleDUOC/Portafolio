//==============================================================================
//===
//===   DbIOInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.io;

import druid.interfaces.DatabaseIOModule;

//==============================================================================

public class DbIOInfo
{
	public String fileName;
	public DatabaseIOModule dbMod;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DbIOInfo(String file, DatabaseIOModule mod)
	{
		fileName = file;
		dbMod    = mod;
	}
}

//==============================================================================
