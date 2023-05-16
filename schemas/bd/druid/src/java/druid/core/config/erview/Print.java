//==============================================================================
//===
//===   Print
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.erview;

import org.dlib.gui.print.GraphicPrinter;
import org.dlib.gui.print.PageMargins;
import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;

//==============================================================================

public class Print
{
	public static final String TAGNAME = "print";

	public PageMargins margins = new PageMargins(10, 10, 10, 10);

	public double  scale      = 0.5;
	public boolean blackWhite = false;
	public String  pageSize   = GraphicPrinter.PAGE_A4;

	//---------------------------------------------------------------------------

	private static final String LEFT_MARGIN   = "leftMargin";
	private static final String TOP_MARGIN    = "topMargin";
	private static final String RIGHT_MARGIN  = "rightMargin";
	private static final String BOTTOM_MARGIN = "bottomMargin";
	private static final String SCALE         = "scale";
	private static final String BLACK_WHITE   = "blackWhite";
	private static final String PAGE_SIZE     = "pageSize";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Print() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	void setupConfig(XmlElement el)
	{
		if (el == null) return;

		scale      = Util.getFloatValue  (el.getChildValue(SCALE),       50);
		blackWhite = Util.getBooleanValue(el.getChildValue(BLACK_WHITE), false);
		pageSize   = Util.getStringValue (el.getChildValue(PAGE_SIZE),   GraphicPrinter.PAGE_A4);

		float left   = Util.getFloatValue(el.getChildValue(LEFT_MARGIN),   10);
		float top    = Util.getFloatValue(el.getChildValue(TOP_MARGIN),    10);
		float right  = Util.getFloatValue(el.getChildValue(RIGHT_MARGIN),  10);
		float bottom = Util.getFloatValue(el.getChildValue(BOTTOM_MARGIN), 10);

		margins = new PageMargins(left, top, right, bottom);
	}

	//---------------------------------------------------------------------------

	XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		elRoot	.addChild(new XmlElement(SCALE,         scale          +""))
					.addChild(new XmlElement(BLACK_WHITE,   blackWhite     +""))
					.addChild(new XmlElement(PAGE_SIZE,     pageSize       +""))
					.addChild(new XmlElement(LEFT_MARGIN,   margins.left   +""))
					.addChild(new XmlElement(TOP_MARGIN,    margins.top    +""))
					.addChild(new XmlElement(RIGHT_MARGIN,  margins.right  +""))
					.addChild(new XmlElement(BOTTOM_MARGIN, margins.bottom +""));

		return elRoot;
	}
}

//==============================================================================
