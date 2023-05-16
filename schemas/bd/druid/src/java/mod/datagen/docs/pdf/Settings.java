//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf;

import com.lowagie.text.FontFactory;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;

//==============================================================================

public class Settings
{
	
	// Paper size
	public static final int PAGESIZE_A4 = 0;
	public static final int PAGESIZE_LETTER = 1;
	public static final int PAGESIZE_LEGAL = 2;

	//--------------------------------------------------------------------------
	//--- margins

	private static final String TOP    = "top";
	private static final String BOTTOM = "bottom";
	private static final String LEFT   = "left";
	private static final String RIGHT  = "right";

	//--- mapping

	private static final String SMALLER = "smaller";
	private static final String SMALL   = "small";
	private static final String BIG     = "big";
	private static final String BIGGER  = "bigger";
	private static final String BIGGEST = "biggest";
	private static final String HUGE    = "huge";

	//--- fonts

	private static final String COVER      = "cover";
	private static final String CHAPTER    = "chapter";
	private static final String SECTION    = "section";
	private static final String SUBSECTION = "subsection";
	private static final String NORMAL     = "normal";
	private static final String CODE       = "code";
	private static final String TITLE      = "title";
	private static final String HEADER     = "header";
	private static final String CELL       = "cell";

	//--- general

	private static final String LANGUAGE      = "language";
	private static final String THUMBNAILS    = "thumbnails";
	private static final String PAGESIZE      = "pageSize";
	private static final String ENTITYSCALING = "entityScaling";
	private static final String CELLPADDING   = "cellPadding";

	//--------------------------------------------------------------------------

	private ModulesConfig mc;
	private BasicModule   bm;

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public Settings(ModulesConfig mc, BasicModule bm)
	{
		this.mc = mc;
		this.bm = bm;
	}

	//--------------------------------------------------------------------------
	//---
	//--- General settings API
	//---
	//--------------------------------------------------------------------------

	public String getLanguage()      { return mc.getValue(bm, LANGUAGE,            "en"); }
	public int    getThumbnails()    { return mc.getValue(bm, THUMBNAILS,           192); }
	public int    getPageSize()      { return mc.getValue(bm, PAGESIZE,     PAGESIZE_A4); }
	public int    getEntityScaling() { return mc.getValue(bm, ENTITYSCALING,         80); }
	public int    getCellPadding()   { return mc.getValue(bm, CELLPADDING,            3); }

	public void setLanguage(String value)   { mc.setValue(bm, LANGUAGE,      value); }
	public void setThumbnails(int value)    { mc.setValue(bm, THUMBNAILS,    value); }
	public void setPageSize(int value)      { mc.setValue(bm, PAGESIZE,      value); }
	public void setEntityScaling(int value) { mc.setValue(bm, ENTITYSCALING, value); }
	public void setCellPadding(int value)   { mc.setValue(bm, CELLPADDING,   value); }

	//--------------------------------------------------------------------------
	//---
	//--- Margins API
	//---
	//--------------------------------------------------------------------------

	public int getTopMargin()    { return mc.getValue(bm, TOP,    57); }
	public int getBottomMargin() { return mc.getValue(bm, BOTTOM, 57); }
	public int getLeftMargin()   { return mc.getValue(bm, LEFT,   28); }
	public int getRightMargin()  { return mc.getValue(bm, RIGHT,  28); }

	public void setTopMargin(int value)    { mc.setValue(bm, TOP,    value); }
	public void setBottomMargin(int value) { mc.setValue(bm, BOTTOM, value); }
	public void setLeftMargin(int value)   { mc.setValue(bm, LEFT,   value); }
	public void setRightMargin(int value)  { mc.setValue(bm, RIGHT,  value); }

	//--------------------------------------------------------------------------
	//---
	//--- Mapping API : the font size mapping refers to the default font size
	//---               thus SMALLER = -4 means default-size -4
	//---
	//--------------------------------------------------------------------------

	public int getSmaller() { return mc.getValue(bm, SMALLER, -4); }
	public int getSmall()   { return mc.getValue(bm, SMALL,   -2); }
	public int getBig()     { return mc.getValue(bm, BIG,      2); }
	public int getBigger()  { return mc.getValue(bm, BIGGER,   4); }
	public int getBiggest() { return mc.getValue(bm, BIGGEST,  8); }
	public int getHuge()    { return mc.getValue(bm, HUGE,    12); }

	public void setSmaller(int value) { mc.setValue(bm, SMALLER, value); }
	public void setSmall(int value)   { mc.setValue(bm, SMALL,   value); }
	public void setBig(int value)     { mc.setValue(bm, BIG,     value); }
	public void setBigger(int value)  { mc.setValue(bm, BIGGER,  value); }
	public void setBiggest(int value) { mc.setValue(bm, BIGGEST, value); }
	public void setHuge(int value)    { mc.setValue(bm, HUGE,    value); }

	//--------------------------------------------------------------------------
	//---
	//--- Fonts API
	//---
	//--------------------------------------------------------------------------

	public FontInfo getCoverFont()
	{
		String font = mc.getValue(bm, COVER, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 40, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getChapterFont()
	{
		String font = mc.getValue(bm, CHAPTER, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 24, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getSectionFont()
	{
		String font = mc.getValue(bm, SECTION, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 20, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getSubSectionFont()
	{
		String font = mc.getValue(bm, SUBSECTION, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 18, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getNormalFont()
	{
		String font = mc.getValue(bm, NORMAL, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 10, false, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getCodeFont()
	{
		String font = mc.getValue(bm, CODE, null);

		if (font == null) return new FontInfo(FontFactory.COURIER, 10, false, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getTitleFont()
	{
		String font = mc.getValue(bm, TITLE, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 18, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getHeaderFont()
	{
		String font = mc.getValue(bm, HEADER, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 10, true, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public FontInfo getCellFont()
	{
		String font = mc.getValue(bm, CELL, null);

		if (font == null) return new FontInfo(FontFactory.HELVETICA, 10, false, false);
			else 				return new FontInfo(font);
	}

	//--------------------------------------------------------------------------

	public void setCoverFont(FontInfo fi)      { mc.setValue(bm, COVER, fi.toString());      }
	public void setChapterFont(FontInfo fi)    { mc.setValue(bm, CHAPTER, fi.toString());    }
	public void setSectionFont(FontInfo fi)    { mc.setValue(bm, SECTION, fi.toString());    }
	public void setSubSectionFont(FontInfo fi) { mc.setValue(bm, SUBSECTION, fi.toString()); }
	public void setNormalFont(FontInfo fi)     { mc.setValue(bm, NORMAL, fi.toString());     }
	public void setCodeFont(FontInfo fi)       { mc.setValue(bm, CODE, fi.toString());       }
	public void setTitleFont(FontInfo fi)      { mc.setValue(bm, TITLE, fi.toString());      }
	public void setHeaderFont(FontInfo fi)     { mc.setValue(bm, HEADER, fi.toString());     }
	public void setCellFont(FontInfo fi)       { mc.setValue(bm, CELL, fi.toString());       }
}

//==============================================================================
