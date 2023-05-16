//==============================================================================
//===
//===   BasicModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

//==============================================================================

/** General information about a module
  */

public interface BasicModule
{
	/** Returns an unique id that represent the module. The unique constraint must
	  * be respected only for modules of the same category. Typical values are:
	  * html, htmlGen, xml, ascii, htmlDocs
	  */

	public String getId();

	public String getAuthor();
	public String getVersion();
	public String getDescription();

	//--- environment types

	public static final int DATABASE  = 0;
	public static final int TABLE     = 1;
	public static final int VIEW      = 2;
	public static final int PROCEDURE = 3;
	public static final int FUNCTION  = 4;
	public static final int SEQUENCE  = 5;
	public static final int FIELD     = 6;

	public ModuleOptions getModuleOptions(int environment);
}

//==============================================================================
