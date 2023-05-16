//==============================================================================
//===
//===   ErEntityV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes.er;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

import org.dlib.gui.TFont;

import druid.core.AttribSet;
import druid.core.er.ErFontData;
import druid.core.er.ErScrEntity;
import druid.data.AbstractNode;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.util.velocity.nodes.AbstractNodeV;

//==============================================================================

public class ErEntityV extends AbstractNodeV
{
	private static BufferedImage bimg = new BufferedImage(2,2, BufferedImage.TYPE_4BYTE_ABGR);

	private Rectangle rcBounds;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ErEntityV(AbstractNode node)
	{
		super(node);

		setupCoords();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Collection getTables()
	{
		ErEntity ent = (ErEntity) node;

		Vector v = new Vector();

		for(int i=0; i<ent.getTableNum(); i++)
			v.add(convertNode(ent.getTableNodeAt(i)));

		return v;
	}

	//---------------------------------------------------------------------------

	public int getX()      { return rcBounds.x;      }
	public int getY()      { return rcBounds.y;      }
	public int getWidth()  { return rcBounds.width;  }
	public int getHeight() { return rcBounds.height; }

	//---------------------------------------------------------------------------
	//---
	//--- Private methods to calc the entity bounds
	//---
	//---------------------------------------------------------------------------

	private void setupCoords()
	{
		Graphics g = bimg.createGraphics();

		ErView erView = (ErView) node.getParent();

		Font fontName  = rebuildFont(erView.attrSet, "name");

		FontMetrics fm1 = g.getFontMetrics(fontName);
		ErFontData erfdName = new ErFontData(fm1, fm1.getHeight(), fm1.getMaxAscent(), fontName);

		Font fontField = rebuildFont(erView.attrSet, "fields");

		FontMetrics fm2 = g.getFontMetrics(fontField);
		ErFontData erfdField = new ErFontData(fm2, fm2.getHeight(), fm2.getMaxAscent(), fontField);

		ErScrEntity erScrEnt = new ErScrEntity((ErEntity)node, erfdName, erfdField, false);

		rcBounds = erScrEnt.getBounds();

		g.dispose();
	}

	//---------------------------------------------------------------------------

	private Font rebuildFont(AttribSet as, String prefix)
	{
		String name  = as.getString(prefix + "Family");
		String style = as.getString(prefix + "Style");
		int    size  = as.getInt(   prefix + "Size");

		return new Font(name, TFont.convertToNative(style), size);
	}
}

//==============================================================================
