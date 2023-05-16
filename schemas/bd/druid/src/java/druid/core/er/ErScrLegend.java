//==============================================================================
//===
//===   ErScrLegend
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.TFont;

import druid.core.AttribSet;
import druid.core.config.Config;
import druid.data.er.Legend;
import druid.data.er.LegendColor;

//==============================================================================

public class ErScrLegend
{
	private static final int MARGIN   =  4;
	private static final int COLWIDTH = 32;

	private Legend        legend;
	private BufferedImage bimgLegend;

	//---------------------------------------------------------------------------

	public ErScrLegend(Legend l, Graphics g)
	{
		legend = l;

		rebuild(g);
	}

	//---------------------------------------------------------------------------

	public void draw(Dimension dim, Graphics g, boolean useBitmap)
	{
		Point p = getLegendPos(dim);

		if (p == null)
			return;

		if (useBitmap)
			g.drawImage(bimgLegend, p.x, p.y, null);
		else
		{
			g.translate(p.x, p.y);

			Font        font = getLegendFont();
			FontMetrics fm   = g.getFontMetrics(font);

			drawImage(g, font, fm.getMaxAscent(), fm.getHeight());

			g.translate(-p.x, -p.y);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Rebuilding methods
	//---
	//---------------------------------------------------------------------------

	private void rebuild(Graphics g)
	{
		Font font = getLegendFont();

		FontMetrics fm = g.getFontMetrics(font);

		createImage(fm);

		Graphics2D big = bimgLegend.createGraphics();

		drawImage(big, font, fm.getMaxAscent(), fm.getHeight());
	}

	//---------------------------------------------------------------------------

	private Font getLegendFont()
	{
		AttribSet as = legend.attrSet;

		String name  = as.getString("fontFamily");
		String style = as.getString("fontStyle");
		int    size  = as.getInt(   "fontSize");

		return new Font(name, TFont.convertToNative(style), size);
	}

	//---------------------------------------------------------------------------

	private void createImage(FontMetrics fm)
	{
		int width = 0;

		for(int i=0; i<legend.getChildCount(); i++)
		{
			LegendColor legCol = (LegendColor) legend.getChild(i);

			width = Math.max(width, fm.stringWidth(legCol.attrSet.getString("name")));
		}

		width += COLWIDTH +2 +2 +2;
		int height = legend.getChildCount() * (fm.getHeight() +1) +3;

		bimgLegend = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	}

	//---------------------------------------------------------------------------

	private void drawImage(Graphics big, Font font, int fontAscent, int fontHeight)
	{
		GuiUtil.setTextAntiAliasing(big, Config.general.isTextAAliased());

		int w = bimgLegend.getWidth();
		int h = bimgLegend.getHeight();

		//--- paint background

		big.setColor(Color.white);
		big.fillRect(0,0, w,h);

		//--- paint border

		big.setColor(Color.black);
		big.drawRect(0,0, w-1,h-1);

		//--- build image

		big.setFont(font);

		for(int i=0; i<legend.getChildCount(); i++)
		{
			LegendColor legCol = (LegendColor) legend.getChild(i);

			int currH = 2+ i*(fontHeight +1);

			big.setColor(legCol.colBg);
			big.fillRect(2, currH, COLWIDTH, fontHeight);

			big.setColor(Color.black);
			big.drawRect(2, currH, COLWIDTH-1, fontHeight-1);

			big.drawString(legCol.attrSet.getString("name"), 2+ COLWIDTH +2, currH +fontAscent);
		}
	}

	//---------------------------------------------------------------------------

	private Point getLegendPos(Dimension dim)
	{
		Point p = new Point(MARGIN, MARGIN);

		String loc = legend.attrSet.getString("location");

		if (loc.equals(Legend.NONE))
			return null;

		if (loc.equals(Legend.NE) || loc.equals(Legend.SE))
			p.x = dim.width - bimgLegend.getWidth() - MARGIN;

		if (loc.equals(Legend.SW) || loc.equals(Legend.SE))
			p.y = dim.height - bimgLegend.getHeight() - MARGIN;

		return p;
	}
}

//==============================================================================
