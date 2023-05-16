//==============================================================================
//===
//===   Html
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.helpers;

import org.dlib.tools.HtmlLib;

//==============================================================================

public class Html
{
	public String getSpaces(int num)    { return HtmlLib.getSpaces(num); }
	public String encode(String text)   { return HtmlLib.encode(text);   }
	public String makeGood(String text) { return HtmlLib.makeGood(text); }
}

//==============================================================================
