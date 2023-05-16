//==============================================================================
//===
//===   Torque
//===
//===   Copyright (C) by Andrea Carboni
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbio.torque;

import org.dlib.xml.XmlElement;
import org.dlib.xml.reader.XmlReader;

import druid.data.DatabaseNode;
import druid.data.ProjectNode;
import druid.interfaces.DatabaseIOModule;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class Torque implements DatabaseIOModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "torqueImp"; }
	public String getVersion()  { return "1.0";       }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Imports databases from the torque xml schema format";
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

	public String getFormat()    { return "Torque XML schema"; }
	public String getExtension() { return "xml";               }

	public boolean canImport() { return true;  }
	public boolean canExport() { return false; }

	//---------------------------------------------------------------------------

	public void doImport(ProjectNode project, String fileName) throws Exception
	{
		XmlElement elRoot = new XmlReader().read(fileName).getRootElement();

		new Importer().doImport(elRoot, project);
	}

	//---------------------------------------------------------------------------

	public void doExport(DatabaseNode db, String fileName) throws Exception {}
}

//==============================================================================
