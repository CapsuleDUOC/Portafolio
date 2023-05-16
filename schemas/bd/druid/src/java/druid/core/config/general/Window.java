//==============================================================================
//===
//===   Window
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.general;

import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;

//==============================================================================

public class Window
{
	public static final String TAGNAME = "window";

	public int width  = 750;
	public int height = 550;

	//---------------------------------------------------------------------------

	private static final String WIDTH   = "width";
	private static final String HEIGHT  = "height";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Window() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	void setupConfig(XmlElement el)
	{
		if (el == null) return;

		width  = Util.getIntValue(el.getChildValue(WIDTH),  750);
		height = Util.getIntValue(el.getChildValue(HEIGHT), 550);
	}

	//---------------------------------------------------------------------------

	XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		elRoot	.addChild(new XmlElement(WIDTH,  width  +""))
					.addChild(new XmlElement(HEIGHT, height +""));

		return elRoot;
	}
}

//==============================================================================
