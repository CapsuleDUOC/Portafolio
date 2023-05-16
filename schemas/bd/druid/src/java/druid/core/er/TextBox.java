//==============================================================================
//===
//===   TextBox
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

//==============================================================================

public class TextBox
{
	private static final int MARGIN_X = 4;
	private static final int MARGIN_Y = 1;

	public  Rectangle  rcBounds;
	private String     sText;
	private ErFontData erFontData;
	private boolean    centered;
	private boolean    bold;
	private boolean    italic;
	
	//---------------------------------------------------------------------------
	//--- x and y are relative to the top/left of the entity image
	//---------------------------------------------------------------------------

	public TextBox(String s, ErFontData erfd)
	{
		this(s, erfd, false);
	}

	//---------------------------------------------------------------------------
	
	public TextBox(String s, ErFontData erfd, boolean centered)
	{
		erFontData = erfd;
		rcBounds   = new Rectangle(0, 0, 0, erfd.iHeight + 2*MARGIN_Y);
		
		this.centered = centered;
		
		setText(s);
	}

	//---------------------------------------------------------------------------

	public void setText(String s)
	{
		rcBounds.width = erFontData.fontMetr.stringWidth(s) + 2*MARGIN_X;
		sText = s;
	}

	//---------------------------------------------------------------------------
	//--- x and y are relative to the entity origin

	public boolean contains(int x, int y)
	{
		return rcBounds.contains(x, y);
	}

	//---------------------------------------------------------------------------
	
	public void setBold  (boolean bold  ) { this.bold   = bold;   }
	public void setItalic(boolean italic) { this.italic = italic; }
	
	//---------------------------------------------------------------------------
	//--- x and y are component absolute coords
	//--- font must be set from caller

	public void draw(Graphics g, int dx, int dy, boolean invert, boolean drawBgCol)
	{
		if (drawBgCol)
		{
			g.setColor(erFontData.getBackgr(invert));
			g.fillRect(dx + rcBounds.x, dy + rcBounds.y, rcBounds.width, rcBounds.height);
		}
		
		g.setColor(erFontData.getForegr(invert));

		Font f = erFontData.font;

		int style = Font.PLAIN;
		
		if (bold)
			style |= Font.BOLD;
		
		if (italic)
			style |= Font.ITALIC;
		
		g.setFont(new Font(f.getFontName(), style, f.getSize()));
		
		int delta = 0;
		
		if (centered)
		{
			int width = erFontData.fontMetr.stringWidth(sText);
			delta = (rcBounds.width - width) /2;
		}
		
		g.drawString(sText, dx + rcBounds.x + MARGIN_X + delta, dy + rcBounds.y + MARGIN_Y + erFontData.iAscent);

	}
	
	//---------------------------------------------------------------------------
	
	public String toString() { return sText; }
}

//==============================================================================
