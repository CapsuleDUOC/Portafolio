//==============================================================================
//===
//===   Druid
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbio.druid;

import druid.core.Serials;
import druid.core.io.ProjectManager;
import druid.data.DatabaseNode;
import druid.data.ProjectNode;
import druid.interfaces.DatabaseIOModule;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class Druid implements DatabaseIOModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "druidIO"; }
	public String getVersion()  { return "1.0";  }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Imports/exports databases from/to other druid projects.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int environment)
	{
		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- DatabaseIOModule interface
	//---
	//---------------------------------------------------------------------------

	public String getFormat()    { return "Druid project file"; }
	public String getExtension() { return "druid";              }

	public boolean canImport() { return true; }
	public boolean canExport() { return true; }

	//---------------------------------------------------------------------------

	public void doImport(ProjectNode project, String fileName) throws Exception
	{
		ProjectManager.loadProject(project, fileName);
	}

	//---------------------------------------------------------------------------

	public void doExport(DatabaseNode db, String fileName) throws Exception
	{
		ProjectNode project = new ProjectNode();
		project.attrSet.setInt("serial", Serials.lastSerial);

		project.addChild(db.duplicate());

		ProjectManager.saveProject(project, fileName);
	}
}

//==============================================================================
