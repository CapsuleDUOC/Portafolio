//==============================================================================
//===
//===   DatabaseIOModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import druid.data.DatabaseNode;
import druid.data.ProjectNode;

//==============================================================================

public interface DatabaseIOModule extends IOModule
{
	/** Performs the import
	  */

	public void doImport(ProjectNode project, String fileName) throws Exception;

	/** Performs the export
	  */

	public void doExport(DatabaseNode db, String fileName) throws Exception;
}

//==============================================================================
