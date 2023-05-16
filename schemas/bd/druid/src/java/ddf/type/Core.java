//==============================================================================
//===
//===   Core
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.type;

//==============================================================================

public class Core
{
	//---------------------------------------------------------------------------
	//---
	//--- DDF Section type
	//---
	//---------------------------------------------------------------------------
	
	public static final String DDF_VERSION       = "3";
	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	public enum Section
	{
		INFO("[INFO]"), FIELDS("[FIELDS]"), INSERT("[INSERT]"), UPDATE("[UPDATE]"), DELETE("[DELETE]");

		//-----------------------------------------------------------------------

		private Section(String section) { this.section = section; }
	
		//-----------------------------------------------------------------------

		public String toString() { return section; }

		//-----------------------------------------------------------------------
	
		private String section;
	}
}

//==============================================================================
