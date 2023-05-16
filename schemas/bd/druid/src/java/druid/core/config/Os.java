//==============================================================================
//===
//===   Os
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config;

//==============================================================================

public class Os
{
	//--- File separator. Can be "/" (for linux) or "\" (for windows)
	public String fileSep;

	//--- Line separator. Can be "/n" (for linux) or "/r/n" (for windows)
	public String lineSep;

	//--- Operating system Can be "linux" etc,,,
	public String name;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Os()
	{
		fileSep  = System.getProperty("file.separator", "/");
		lineSep  = System.getProperty("line.separator", "/n");
		name     = System.getProperty("os.name", "unknown").toLowerCase();
	}
}

//==============================================================================
