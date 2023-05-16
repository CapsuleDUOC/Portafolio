//==============================================================================
//===
//===   DataGenModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import druid.data.DatabaseNode;

//==============================================================================

public interface DataGenModule extends BasicModule
{
	/** Returns the format handled by the module. Typical strings are:
	  * "Ascii", "Browsable HTML", "XML" etc...
	  */

	public String getFormat();

	/** Called when the user presses the corresponding button in the Data
	  * Generation Dialog.
	  */

	public void generate(Logger l, DatabaseNode dbNode);

	/** Indicates if the module uses the output as a directory to create the
	  * generated files.
	  */

	public boolean isDirectoryBased();

	/** Indicates if the GUI manager has to expand the panel vertically.
	  */

	public boolean hasLargePanel();
}

//==============================================================================
