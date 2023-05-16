//==============================================================================
//===
//===   ErFontData
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

//==============================================================================

public class ErFontData
{
	public FontMetrics fontMetr;

	public int   iHeight;
	public int   iAscent;
	public Color colForegr;
	public Color colBackgr;
	public Font  font;
	
	//---------------------------------------------------------------------------

	public ErFontData(FontMetrics fm, int height, int ascen, Font f)
	{
		this(fm, height, ascen, f, null, null);
	}
	
	//---------------------------------------------------------------------------

	public ErFontData(FontMetrics fm, int height, int ascen, Font f, Color fg, Color bg)
	{
		fontMetr  = fm;
		iHeight   = height;
		iAscent   = ascen;
		font      = f;
		
		setColors(fg, bg);
	}
	
	//---------------------------------------------------------------------------

	public ErFontData(ErFontData erfd)
	{
		fontMetr  = erfd.fontMetr;
		iHeight   = erfd.iHeight;
		iAscent   = erfd.iAscent;
		colForegr = erfd.colForegr;
		colBackgr = erfd.colBackgr;
		font      = erfd.font;
	}
	
	//---------------------------------------------------------------------------
	
	public void setColors(Color fg, Color bg)
	{
		colForegr = fg;
		colBackgr = bg;
	}
	
	//---------------------------------------------------------------------------

	public Color getForegr(boolean invert)
	{
		if (!invert) 	return colForegr;
			else 			return colBackgr;
	}
	
	//---------------------------------------------------------------------------
	
	public Color getBackgr(boolean invert)
	{
		if (!invert) 	return colBackgr;
			else 			return colForegr;
	}
}

//==============================================================================
