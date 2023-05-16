//==============================================================================
//===
//===   User
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config;

//==============================================================================

public class User
{
	//--- Under windows it is "unknown"
	public String name;

	//--- User home directory. Under windows it is "C:\WINDOWS".
	//--- Under linux it is "/home/<user>" or "/root"
	public String home;

	//--- User language. For Italy is 'it'
	public String lang;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	User()
	{
		name = System.getProperty("user.name",      "unknown");
		home = System.getProperty("user.home",      ".");
		lang = System.getProperty("user.country",   "it");
	}
}

//==============================================================================
