//==============================================================================
//===
//===   ErView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.erview;

import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;

//==============================================================================

public class ErView
{
	public static final String TAGNAME = "erView";

	public int snapSize   = 4;
	public int scrollSize = 32;

	public Print print = new Print();

	//---------------------------------------------------------------------------

	private static final String SNAPSIZE   = "snapSize";
	private static final String SCROLLSIZE = "scrollSize";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ErView() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setupConfig(XmlElement el)
	{
		if (el == null) return;

		snapSize   = Util.getIntValue(el.getChildValue(SNAPSIZE),    4);
		scrollSize = Util.getIntValue(el.getChildValue(SCROLLSIZE), 32);

		print.setupConfig(el.getChild(Print.TAGNAME));
	}

	//---------------------------------------------------------------------------

	public XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		elRoot	.addChild(new XmlElement(SNAPSIZE,   snapSize   +""))
					.addChild(new XmlElement(SCROLLSIZE, scrollSize +""))
					.addChild(print.getConfig());

		return elRoot;
	}
}

//==============================================================================
