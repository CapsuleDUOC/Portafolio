//==============================================================================
//===
//===   Logger
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

//==============================================================================

/** This interface provide a method used to display messages on an output device
  */

public interface Logger
{
	//--- log types
	
	public static final int INFO  = 0; //--- used to display the generation process
	public static final int ALERT = 1; //--- used to display anomalies / errors
	
	public void log(int type, String message);
	public void logHeader(String message);
}

//==============================================================================
