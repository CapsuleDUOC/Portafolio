//==============================================================================
//===
//===   Dir
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config;

//==============================================================================

public class Dir
{
	//--- environment vars for directories

	private static final String DIR_IMAGES  = "dir.images";
	private static final String DIR_MODULES = "dir.modules";
	private static final String DIR_DATA    = "dir.data";
	private static final String DIR_DOCS    = "dir.docs";

	//--- variables

	public String images;
	public String modules;
	public String data;
	public String docs;

	//--- path to the druid directory
	public String druid;

	//--- full path where the druid files are located (ending with "/" )
	public String config;

	//--- Dir from which the druid script was launched
	public String current;

	//--- Temporary dir. Under windows it is "C:\WINDOWS\TEMP"
	//--- Under linux it is "/tmp"
	public String temp;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Dir(String druidDir, User user, Os os)
	{
		druid   = druidDir;
		images  = System.getProperty(DIR_IMAGES,  druidDir + "/images");
		modules = System.getProperty(DIR_MODULES, druidDir + "/modules");
		data    = System.getProperty(DIR_DATA,    druidDir + "/data");
		docs    = System.getProperty(DIR_DOCS,    druidDir + "/docs");
		current = System.getProperty("user.dir",       ".");
		temp    = System.getProperty("java.io.tmpdir", "/tmp");

		if (os.name.equals("linux"))
			config = user.home + os.fileSep + ".druid" + os.fileSep;
		else
			config = user.home + os.fileSep + "Druid" + os.fileSep;
	}
}

//==============================================================================
