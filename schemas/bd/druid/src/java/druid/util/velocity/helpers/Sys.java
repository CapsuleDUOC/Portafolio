//==============================================================================
//===
//===   Sys
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.helpers;

//==============================================================================

public class Sys
{
	public String date;
	public String day;
	public String time;

	//---------------------------------------------------------------------------

	public Sys()
	{
		day  = org.dlib.tools.Util.getCurrentDay();
		time = org.dlib.tools.Util.getCurrentTime();
		date = day +" "+ time;
	}

	//---------------------------------------------------------------------------

	public String getDate() { return date; }
	public String getDay()  { return day;  }
	public String getTime() { return time; }
}

//==============================================================================
