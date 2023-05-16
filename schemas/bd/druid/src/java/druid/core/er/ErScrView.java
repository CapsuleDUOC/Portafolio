//==============================================================================
//===
//===   ErScrView
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.TFont;
import org.dlib.gui.print.GraphicPrinterSource;
import org.dlib.gui.print.PrintRow;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DruidException;
import druid.core.config.Config;
import druid.data.FieldNode;
import druid.data.er.ErEntity;
import druid.data.er.ErView;

//==============================================================================

public class ErScrView implements GraphicPrinterSource
{
	private ErView erView;

	private Point ptCurrOffset;

	//--- rebuildable objects

	private Font       fontName;
	private ErFontData erfdName;

	private Font       fontField;
	private ErFontData erfdField;

	private ErScrLegend scrLegend;

	private Vector vObjects = new Vector();
	private Vector vLinks   = new Vector();

	private boolean createToPrint;
	private boolean blackWhite;

	//---------------------------------------------------------------------------
	//--- value returned by calcRefs
	//--- indicates if entity B has a reference to entity A

	private static final int REFS_NONE = 0;
	private static final int REFS_PK   = 1;
	private static final int REFS_FKNN = 2;
	private static final int REFS_FK   = 3;

	//---------------------------------------------------------------------------
	//---
	//--- Constructors
	//---
	//---------------------------------------------------------------------------

	public ErScrView(ErView erv, Graphics g)
	{
		this(erv, g, false, false);
	}

	//---------------------------------------------------------------------------

	public ErScrView(ErView erv, Graphics g, boolean createToPrint, boolean blackWhite)
	{
		this.createToPrint = createToPrint;
		this.blackWhite    = createToPrint ? blackWhite : false;

		erView = erv;
		erView.updateData();

		rebuild(g);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public ErView getErView() { return erView; }

	//---------------------------------------------------------------------------

	public Point getCurrOffset() { return ptCurrOffset; }

	//---------------------------------------------------------------------------

	public void setCurrOffset(Point p)
	{
		ptCurrOffset = p;
	}

	//---------------------------------------------------------------------------

	public ErScrEntity addObject(Object obj)
	{
		if (obj instanceof ErEntity)
		{
			ErScrEntity ese = new ErScrEntity((ErEntity)obj, erfdName, erfdField, blackWhite);
			vObjects.add(ese);
			
			return ese;
		}

		throw new DruidException(DruidException.INC_STR, "Unknown object type", obj);
	}

	//---------------------------------------------------------------------------

	public void removeObject(Object obj)
	{
		vObjects.remove(obj);
	}

	//---------------------------------------------------------------------------

	public Object getObjectFromPoint(int x, int y)
	{
		ErScrEntity erScrEnt;
		Object obj;

		for(int i=0; i<vObjects.size(); i++)
		{
			obj = vObjects.elementAt(i);

			if (obj instanceof ErScrEntity)
			{
				erScrEnt = (ErScrEntity) obj;

				if (erScrEnt.isMouseInside(x,y))
				{
					//--- shift selected element to first position so it is drawn
					//--- on top of others

					vObjects.removeElementAt(i);
					vObjects.insertElementAt(erScrEnt ,0);

					//--- return data
					return erScrEnt;
				}
			}
			else
				throw new DruidException(DruidException.INC_STR, "Unknown object type", obj);
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public ErScrEntity getObjectFromEntity(ErEntity e)
	{
		for(int i=0; i<vObjects.size(); i++)
		{
			ErScrEntity erScrEnt = (ErScrEntity) vObjects.elementAt(i);

			if (erScrEnt.getErEntity() == e) return erScrEnt;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public void translate(int dx, int dy)
	{
		if (vObjects.size() == 0) return;

		int minX = 100000;
		int minY = 100000;

		//------------------------------------------------------------------------
		//--- stage 1: calc minX and minY

		for(int i=0; i<vObjects.size(); i++)
		{
			Object obj = vObjects.elementAt(i);

			if (obj instanceof ErScrEntity)
			{
				ErScrEntity e = (ErScrEntity) obj;

				Rectangle r = e.getBounds();

				minX = Math.min(minX, r.x);
				minY = Math.min(minY, r.y);
			}
		}

		//------------------------------------------------------------------------
		//--- stage 2: check translation validity

		if (minX + dx < 0) return;
		if (minY + dy < 0) return;

		//------------------------------------------------------------------------
		//--- stage 3: adjust locations

		for(int i=0; i<vObjects.size(); i++)
		{
			Object obj = vObjects.elementAt(i);

			if (obj instanceof ErScrEntity)
			{
				ErScrEntity e = (ErScrEntity) obj;

				Rectangle r = e.getBounds();

				r.x += dx;
				r.y += dy;

				AttribSet as = e.getErEntity().attrSet;

				as.setInt("locX", r.x);
				as.setInt("locY", r.y);
			}
		}
	}

	//---------------------------------------------------------------------------

	public ErLink getLinkFromPoint(int x, int y, int maxDistance)
	{
		//--- links ends inside entities so we must skip them

		if (getObjectFromPoint(x, y) != null)
			return null;

		//--- ok, look for a link

		for(int i=0; i<vLinks.size(); i++)
		{
			ErLink link = (ErLink) vLinks.get(i);

			int distance = link.getDistanceFromPoint(x, y);

			if (distance != -1)
				if (distance <= maxDistance)
					return link;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public Dimension getErSize()
	{
		int maxX = 1;
		int maxY = 1;

		for(int i=0; i<vObjects.size(); i++)
		{
			Object obj = vObjects.elementAt(i);

			if (obj instanceof ErScrEntity)
			{
				ErScrEntity e = (ErScrEntity) obj;

				Rectangle r = e.getBounds();

				maxX = Math.max(maxX, r.x + r.width);
				maxY = Math.max(maxY, r.y + r.height);
			}
		}

		return new Dimension(maxX, maxY);
	}

	//---------------------------------------------------------------------------

	public BufferedImage createErImage()
	{
		Dimension dim = getErSize();

		BufferedImage bimg = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_3BYTE_BGR);

		Graphics g = bimg.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0,0, dim.width, dim.height);

		drawView(g);

		g.dispose();

		return bimg;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Drawing capability
	//---
	//---------------------------------------------------------------------------

	public void drawView(Graphics g)
	{
		//--- antialiasing is used only for links and when printing because
		//--- the entities use bitmaps

		GuiUtil.setAntiAliasing(g, Config.general.guiAAliasing);

		drawLinks(g);
		drawObjects(g);

		scrLegend.draw(getErSize(), g, !createToPrint);
	}

	//---------------------------------------------------------------------------

	private void drawObjects(Graphics g)
	{
		for(int i=vObjects.size()-1; i>=0; i--)
		{
			Object obj = vObjects.elementAt(i);

			if (obj instanceof ErScrEntity)
				((ErScrEntity) obj).drawEntity(g, !createToPrint);
			else
				throw new DruidException(DruidException.INC_STR, "Unknown object type", obj);
		}
	}

	//---------------------------------------------------------------------------

	private void drawLinks(Graphics g)
	{
		for(int i=0; i<vLinks.size(); i++)
			((ErLink)vLinks.elementAt(i)).draw(g);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Rebuilding methods
	//---
	//---------------------------------------------------------------------------

	/** Rebuilds all GUI structs from the given ErView
	  */

	private void rebuild(Graphics g)
	{
		rebuildFonts(g);
		rebuildObjects();
		rebuildLinks();
		rebuildLegend(g);
	}

	//---------------------------------------------------------------------------

	private void rebuildFonts(Graphics g)
	{
		fontName  = rebuildFont(erView.attrSet, "name");

		FontMetrics fm1 = g.getFontMetrics(fontName);
		erfdName = new ErFontData(fm1, fm1.getHeight(), fm1.getMaxAscent(), fontName);

		fontField = rebuildFont(erView.attrSet, "fields");

		FontMetrics fm2 = g.getFontMetrics(fontField);
		erfdField = new ErFontData(fm2, fm2.getHeight(), fm2.getMaxAscent(), fontField);
	}

	//---------------------------------------------------------------------------

	private Font rebuildFont(AttribSet as, String prefix)
	{
		String name  = as.getString(prefix + "Family");
		String style = as.getString(prefix + "Style");
		int    size  = as.getInt(   prefix + "Size");

		return new Font(name, TFont.convertToNative(style), size);
	}

	//---------------------------------------------------------------------------

	private void rebuildObjects()
	{
		vObjects.removeAllElements();

		for(int i=0; i<erView.getChildCount(); i++)
			addObject(erView.getChild(i));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Links methods
	//---
	//---------------------------------------------------------------------------

	public void rebuildLinks()
	{
		vLinks.removeAllElements();

		ErScrEntity erEnt1;
		ErScrEntity erEnt2;
		ErLink      erLink;

		for(int i=0; i<vObjects.size()-1; i++)
		{
			erEnt1 = (ErScrEntity) vObjects.elementAt(i);

			for(int j=i+1; j<vObjects.size(); j++)
			{
				erEnt2 = (ErScrEntity) vObjects.elementAt(j);

				erLink = buildLink(erEnt1, erEnt2);

				if (erLink != null)
					vLinks.add(erLink);
			}
		}
	}

	//---------------------------------------------------------------------------

	private ErLink buildLink(ErScrEntity erEnt1, ErScrEntity erEnt2)
	{
		int BtoA = calcRefs(erEnt1, erEnt2);
		int AtoB = calcRefs(erEnt2, erEnt1);

		//------------------------------------------------------------------------
		//--- perform mapping (16 possibilities) to figure out what link must be
		//--- created

		if (AtoB == REFS_NONE && BtoA == REFS_NONE) return null;

		int startSh = calcShape(AtoB, BtoA);
		int endSh   = calcShape(BtoA, AtoB);

		int style   = ((AtoB == REFS_PK) || (BtoA == REFS_PK))
							? ErLink.STYLE_NORMAL
							: ErLink.STYLE_DASHED;

		return new ErLink(erEnt1, erEnt2, startSh, endSh, style);
	}

	//---------------------------------------------------------------------------

	private int calcRefs(ErScrEntity erScrEnt1, ErScrEntity erScrEnt2)
	{
		ErEntity erEnt1 = erScrEnt1.getErEntity();

		//--- scan ent2 primary keys

		for(int i=0; i<erScrEnt2.getPkNum(); i++)
			if (erEnt1.existsTable(erScrEnt2.getPkRefTable(i))) return REFS_PK;

		//--- scan ent2 normal fields

		for(int i=0; i<erScrEnt2.getFkNum(); i++)
		{
			if (erEnt1.existsTable(erScrEnt2.getFkRefTable(i)))
			{
				FieldNode f = erScrEnt2.getFkAt(i);

				return (DataLib.isNotNull(f) ? REFS_FKNN : REFS_FK);
			}
		}

		return REFS_NONE;
	}

	//---------------------------------------------------------------------------

	private int calcShape(int ref1, int ref2)
	{
		if (ref1 == REFS_NONE)
		{
			if (ref2 == REFS_FK)	return ErLink.SHAPE_ROAR_EMPTY;
				else 				return ErLink.SHAPE_NONE;
		}

		if (ref2 == REFS_FK)		
			return ErLink.SHAPE_ROAR_FILLED;

		if (ref2 == REFS_PK)
			if ((ref1 == REFS_FKNN) || (ref1 == REFS_FK))
				return ErLink.SHAPE_BALL_EMPTY;

		return ErLink.SHAPE_BALL_FILLED;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Legend methods
	//---
	//---------------------------------------------------------------------------

	private void rebuildLegend(Graphics g)
	{
		scrLegend = new ErScrLegend(erView.legend, g);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ImagePrinterSource interface
	//---
	//---------------------------------------------------------------------------

	public int      getWidth()  { return getErSize().width;  }
	public int      getHeight() { return getErSize().height; }
	public PrintRow getHeader() { return null; }
	public PrintRow getFooter() { return null; }

	public void print(Graphics g)
	{
		drawView(g);
	}
}

//==============================================================================
