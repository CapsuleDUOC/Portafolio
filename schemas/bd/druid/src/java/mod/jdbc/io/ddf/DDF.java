//==============================================================================
//===
//===   DDF
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.jdbc.io.ddf;

import ddf.exp.DDFExporter;
import ddf.imp.DDFImporter;
import druid.core.jdbc.JdbcConnection;
import druid.interfaces.ModuleOptions;
import druid.interfaces.RecordIOModule;

//==============================================================================

public class DDF implements RecordIOModule
{
	public String getId()        { return "ddf"; }
	public String getAuthor()    { return "Andrea Carboni"; }
	public String getVersion()   { return "1.1"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "The druid data format is a special data format that allows users to " +
			    "export data to an ascii file an reimport the data back to a table. " +
			    "Almost all data types are supported. Supports UNICODE strings. "+
			    "Starting with Druid 3.12 the format has been EXTENDED.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public String  getFormat()    { return "Druid data format (EXTENDED)"; }
	public String  getExtension() { return "ddf"; }
	public boolean canImport()    { return true;  }
	public boolean canExport()    { return true;  }

	//---------------------------------------------------------------------------
	//---
	//--- Methods
	//---
	//---------------------------------------------------------------------------

	public DDF() {}

	//---------------------------------------------------------------------------

	public void doImport(JdbcConnection jdbcConn, String table, String fileName, ImportListener l) throws Exception
	{
		new DDFImporter().doImport(jdbcConn.getConnection(), table, fileName, new DDFAdapter(l));
	}

	//---------------------------------------------------------------------------

	public void doExport(JdbcConnection jdbcConn, String query, String fileName, ExportListener l) throws Exception
	{
		new DDFExporter().doExport(jdbcConn.getConnection(), query, fileName, new DDFAdapter(l));
	}
}

//==============================================================================
