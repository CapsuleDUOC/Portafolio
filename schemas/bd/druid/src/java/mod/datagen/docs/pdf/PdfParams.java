//==============================================================================
//===
//===   PdfParams
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

import druid.data.DatabaseNode;
import druid.interfaces.Logger;

//==============================================================================

class PdfParams
{
	public int thumbSize;

	public int marginLeft;
	public int marginRight;
	public int marginTop;
	public int marginBottom;

	public Rectangle pageSize;

	public float entScalingPerc;

	//---------------------------------------------------------------------------

	public String defFontName;
	public int    defFontStyle;
	public float  defFontSize;

	public Font fontCover;
	public Font fontChapters;
	public Font fontSections;
	public Font fontSubSects;
	public Font fontCode;
	public Font fontDefault;

	public int sizeMapping[] = { 0, 0, 0, 0, 0, 0, 0 };

	//---------------------------------------------------------------------------
	//--- table settings

	public Font fontTableTitle;
	public Font fontTableHeader;
	public Font fontTableCell;
	public Font fontTableCellBold;

	public Color colTableTitle    = new Color(204, 204, 255);
	public Color colTableHeader   = new Color(232, 255, 255);
	public Color colTableCellCode = new Color(240, 240, 240);

	public int tableCellPadding;

	//---------------------------------------------------------------------------
	//--- general use variables

	public DatabaseNode dbNode;

	public Logger logger;

	public int currChapter = 1;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public PdfParams(Logger l, DatabaseNode node, Settings s)
	{
		logger  = l;
		dbNode  = node;

		thumbSize      = s.getThumbnails();
		entScalingPerc = s.getEntityScaling();

		marginLeft   = s.getLeftMargin();
		marginRight  = s.getRightMargin();
		marginTop    = s.getTopMargin();
		marginBottom = s.getBottomMargin();

		//--- page size mapping
		switch (s.getPageSize())
		{
			case Settings.PAGESIZE_LETTER:
				pageSize = PageSize.LETTER;
				break;
			case Settings.PAGESIZE_LEGAL:
				pageSize = PageSize.LEGAL;
				break;
			// case Settings.PAGESIZE_A4: /* This is the default papel size */
			default:
				pageSize = PageSize.A4;
		}

		//--- font handling

		fontCover    = mapFont(s.getCoverFont());
		fontChapters = mapFont(s.getChapterFont());
		fontSections = mapFont(s.getSectionFont());
		fontSubSects = mapFont(s.getSubSectionFont());
		fontCode     = mapFont(s.getCodeFont());
		fontDefault  = mapFont(s.getNormalFont());

		//--- setup normal font

		FontInfo fi = s.getNormalFont();

		defFontName = fi.name;
		defFontSize = fi.size;
		defFontStyle= Font.NORMAL;

		if (fi.bold)   defFontStyle |= Font.BOLD;
		if (fi.italic) defFontStyle |= Font.ITALIC;

		//--- setup font mapping

		sizeMapping[0] = (int) defFontSize + s.getSmaller();
		sizeMapping[1] = (int) defFontSize + s.getSmall();
		sizeMapping[2] = (int) defFontSize;
		sizeMapping[3] = (int) defFontSize + s.getBig();
		sizeMapping[4] = (int) defFontSize + s.getBigger();
		sizeMapping[5] = (int) defFontSize + s.getBiggest();
		sizeMapping[6] = (int) defFontSize + s.getHuge();

		//--- table settings

		fontTableTitle  = mapFont(s.getTitleFont());
		fontTableHeader = mapFont(s.getHeaderFont());

		FontInfo tfi = s.getCellFont();

		fontTableCell = mapFont(tfi);

		tfi.bold = true;

		fontTableCellBold = mapFont(tfi);

		tableCellPadding = s.getCellPadding();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Document createDocument()
	{
		return new Document(pageSize, marginLeft, marginRight, marginTop, marginBottom);
	}

	//---------------------------------------------------------------------------

	public Font createDefaultFont(int style)
	{
		return FontFactory.getFont(defFontName, defFontSize, style, Color.BLACK);
	}

	//---------------------------------------------------------------------------

	public float getSpaceWidth()  { return pageSize.width()  - marginLeft - marginRight;  }
	public float getSpaceHeight() { return pageSize.height() - marginTop  - marginBottom; }
	public float getSpaceTop()    { return 100; }

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private Font mapFont(FontInfo fi)
	{
		int style = 0;

		if (fi.bold)   style |= Font.BOLD;
		if (fi.italic) style |= Font.ITALIC;

		return FontFactory.getFont(fi.name, fi.size, style, Color.BLACK);
	}
}

//==============================================================================
