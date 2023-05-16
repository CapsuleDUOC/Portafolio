//==============================================================================
//===
//===   DefaultLogger
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util;

//==============================================================================

public class DefaultLogger
{
	private StringBuffer sbLog = new StringBuffer();

	//---------------------------------------------------------------------------

	public DefaultLogger() {}

	//---------------------------------------------------------------------------

	public void log(String message)
	{
		sbLog.append(message + "\n");
	}

	//---------------------------------------------------------------------------

	public String getLog() { return sbLog.toString(); }
}

//==============================================================================
