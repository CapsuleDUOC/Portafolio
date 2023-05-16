//==============================================================================
//===
//===   IOModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

//==============================================================================

public interface IOModule extends BasicModule
{
	/** Returns the format handled by the module. Typical strings are:
	  * "Ascii", "Browsable HTML", "XML" etc...
	  * This string will be displayed into the popup
	  */

	public String getFormat();

	/** Returns the file extension handled by this module (without the starting '.')
	  * The extension is automatically added to the file name if not present
	  */

	public String getExtension();

	/** Is the module able to import data in druid using this format ?
	  */

	public boolean canImport();

	/** Is the module able to export data from druid using this format ?
	  */

	public boolean canExport();
}

//==============================================================================
