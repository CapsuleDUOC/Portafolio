//==============================================================================
//===
//===   ErScrEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.dlib.gui.GuiUtil;

import druid.core.DataLib;
import druid.core.config.Config;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.er.ErEntity;
import druid.data.er.LegendColor;

//==============================================================================

public class ErScrEntity
{
	public static final int MARGIN    =  8;
	public static final int VERTSPACE =  6;
	public static final int CORNER    = 12;

	private ErEntity erEntity;

	//--- rebuildable values

	private Rectangle rcBounds = new Rectangle(0,0,0,0);

	private TextBox tbName;

	private List<ErScrEntityField> listPks    = new ArrayList<ErScrEntityField>();
	private List<ErScrEntityField> listFks    = new ArrayList<ErScrEntityField>();
	private List<ErScrEntityField> listFields = new ArrayList<ErScrEntityField>();

	private LegendColor legCol;
	private ErFontData  erfdName;
	private ErFontData  erfdField;

	private boolean bBlackWhite;

	private BufferedImage bimgEntity;

	private static final int PRIMARY   = 0; //--- all PKs are not FK
	private static final int SECONDARY = 1; //--- some PKs are FK but refer to the same table
	private static final int GENERIC   = 2; //--- some PKs are FK

	private int iEntityType;

	//--- these two arrays are needed to draw SECONDARY entities

	private int aX[] = new int[] { CORNER, 1, 2, 2, 1, CORNER, 0, 0, CORNER };
	private int aY[] = new int[] { 0, 0, CORNER, 1, 2, 2, 1, CORNER, 0 };

	//---------------------------------------------------------------------------
	//---
	//--- Constructors
	//---
	//---------------------------------------------------------------------------

	public ErScrEntity(ErEntity ent, ErFontData fdName, ErFontData fdField, boolean bw)
	{
		bBlackWhite = bw;

		erEntity  = ent;
		erfdName  = new ErFontData(fdName);
		erfdField = new ErFontData(fdField);

		rcBounds.x = ent.attrSet.getInt("locX");
		rcBounds.y = ent.attrSet.getInt("locY");

		rebuild();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public Rectangle     getBounds()   { return rcBounds;       }
	public BufferedImage getImage()    { return bimgEntity;     }
	public ErEntity      getErEntity() { return erEntity;       }
	public int           getPkNum()    { return listPks.size(); }
	public int           getFkNum()    { return listFks.size(); }

	//---------------------------------------------------------------------------

	public void drawEntity(Graphics g, boolean useBitmap)
	{
		if (useBitmap)
			g.drawImage(bimgEntity, rcBounds.x, rcBounds.y, null);
		else
		{
			g.translate(rcBounds.x, rcBounds.y);
			drawImage(g);
			g.translate(-rcBounds.x, -rcBounds.y);
		}
	}

	//---------------------------------------------------------------------------

	public boolean isMouseOnName(int x, int y)
	{
		return tbName.contains(x, y);
	}

	//---------------------------------------------------------------------------

	public boolean isMouseInside(int x, int y)
	{
		return rcBounds.contains(x, y);
	}

	//---------------------------------------------------------------------------

	public int getPkRefTable(int pos)
	{
		return listPks.get(pos).getRefTable();
	}

	//---------------------------------------------------------------------------

	public int getFkRefTable(int pos)
	{
		return listFks.get(pos).getRefTable();
	}

	//---------------------------------------------------------------------------

	public FieldNode getFkAt(int pos)
	{
		return listFks.get(pos).getFieldNode();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Image rebuilding
	//---
	//---------------------------------------------------------------------------

	public void rebuild()
	{
		legCol = erEntity.getLegendColor();

		//--- if the color was deleted we must use the first color in the legend

		if (legCol == null)
		{
			int colId = erEntity.getErView().legend.getFirstColorId();

			erEntity.attrSet.setInt("colorId", colId);
			legCol = erEntity.getLegendColor();
		}

		if (!bBlackWhite)
		{
			erfdName.setColors( legCol.colName, legCol.colNameBg);
			erfdField.setColors(legCol.colText, legCol.colTextBg);
		}
		else
		{
			erfdName.setColors( Color.black, Color.white);
			erfdField.setColors(Color.black, Color.white);
		}

		rebuildName();
		rebuildFields();
		recalcEntityType();
		alignFields();
		rebuildImage();
	}

	//---------------------------------------------------------------------------

	private void rebuildName()
	{
		String name = erEntity.attrSet.getString("name");

		tbName = new TextBox(name, erfdName, true);

		tbName.rcBounds.x = MARGIN;
		tbName.rcBounds.y = MARGIN;
	}

	//---------------------------------------------------------------------------

	private void rebuildFields()
	{
		listPks   .clear();
		listFks   .clear();
		listFields.clear();

		DatabaseNode dbNode = null;

		for(int i=0; i<erEntity.getTableNum(); i++)
		{
			TableNode tabNode = erEntity.getTableNodeAt(i);

			if (dbNode == null)
				dbNode = tabNode.getDatabase();

			for(int j=0; j<tabNode.getChildCount(); j++)
			{
				FieldNode fiNode = (FieldNode) tabNode.getChild(j);

				ErScrEntityField enField = new ErScrEntityField(fiNode, erfdField);

				if (DataLib.isPrimaryKey(fiNode))
					listPks.add(enField);
				
				else if (fiNode.isFkey())
					listFks.add(enField);
				
				else
					listFields.add(enField);
			}
		}
	}

	//---------------------------------------------------------------------------

	private void recalcEntityType()
	{
		iEntityType = PRIMARY;

		int tabId = 0;

		for(ErScrEntityField enField : listPks)
		{
			FieldNode f = enField.getFieldNode();

			if (f.isFkey())
			{
				iEntityType = SECONDARY;

				if (tabId == 0)
					tabId = f.attrSet.getInt("refTable");

				else if (tabId != f.attrSet.getInt("refTable"))
				{
					iEntityType = GENERIC;
					return;
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private void alignFields()
	{
		//------------------------------------------------------------------------
		//--- stage 1: calc widths for all fields

		FieldWidth fieldWidths = new FieldWidth();
		
		fieldWidths.calcWidths(listPks);
		fieldWidths.calcWidths(listFks);
		fieldWidths.calcWidths(listFields);

		//------------------------------------------------------------------------
		//--- stage 2: choose max width between fields and entity name

		String details = erEntity.getDetails();

		boolean useTypes  = (details.equals(ErEntity.COMPLETE));
		boolean useAttribs= erEntity.getErView().isShowAttribs();
		
		int newWidth = fieldWidths.nameWidth;

		if (useAttribs)
			newWidth += fieldWidths.attrWidth +1;
		
		if (useTypes)
			newWidth += fieldWidths.typeWidth +1;

		if (tbName.rcBounds.width < newWidth)
			tbName.rcBounds.width = newWidth;
		else
			fieldWidths.nameWidth += tbName.rcBounds.width - newWidth;

		//------------------------------------------------------------------------
		//--- stage 3: set new widths for pks and simple fields

		for(ErScrEntityField enField : listPks)
			enField.setWidths(MARGIN, fieldWidths, useAttribs);

		//--- set new widths for foreign keys

		for(ErScrEntityField enField : listFks)
			enField.setWidths(MARGIN, fieldWidths, useAttribs);

		//--- set new widths for simple fields

		for(ErScrEntityField enField : listFields)
			enField.setWidths(MARGIN, fieldWidths, useAttribs);
		
		//------------------------------------------------------------------------
		//--- stage 4: set y coords

		int currY = tbName.rcBounds.y + tbName.rcBounds.height + VERTSPACE*2 +1;

		//--- primary keys

		for(ErScrEntityField enField : listPks)
		{
			enField.setY(currY);
			enField.recalcBounds(useTypes, useAttribs);

			currY += enField.getHeight() +1;
		}

		//--- foreign keys

		currY += VERTSPACE*2;

		for(ErScrEntityField enField : listFks)
		{
			enField.setY(currY);
			enField.recalcBounds(useTypes, useAttribs);

			currY += enField.getHeight() +1;
		}
		
		//--- other fields
		
		currY += VERTSPACE*2;

		for(ErScrEntityField enField : listFields)
		{
			enField.setY(currY);
			enField.recalcBounds(useTypes, useAttribs);

			currY += enField.getHeight() +1;
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   Rebuild the entity image
	//---
	//---------------------------------------------------------------------------

	private void rebuildImage()
	{
		rcBounds.width  = tbName.rcBounds.width + MARGIN*2;
		rcBounds.height = calcImageHeight(erEntity.getDetails());

		int width  = rcBounds.width;
		int height = rcBounds.height;

		//------------------------------------------------------------------------
		//--- rebuild image

		//--- if we use transparent images we have two drawbacks:
		//---  - the texts are drawn very badly
		//---  - rendering is about 2-3 times slower
//		bimgEntity = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		bimgEntity = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D big = bimgEntity.createGraphics();

		drawImage(big);
	}

	//---------------------------------------------------------------------------

	/** drawBorder is true the cells are drawn with an outline border
	  *            (used when printing in black & white)
	  */

	private void drawImage(Graphics big)
	{
		GuiUtil.setTextAntiAliasing(big, Config.general.isTextAAliased());

		drawBackground(big, rcBounds.width, rcBounds.height);

		String details = erEntity.getDetails();

		boolean showBgCol = erEntity.getErView().isShowBgColor();
		
		//--- draw title

		big.setFont(erfdName.font);
		tbName.draw(big, 0, 0, false, showBgCol);

		if (showBgCol)
		{
			big.setColor(legCol.colBorder);
			Rectangle r = tbName.rcBounds;
			big.drawRect(r.x -1, r.y -1, r.width +1, r.height +1);
		}
		
		big.setFont(erfdField.font);

		//--- draw pks

		boolean useTypes  = details.equals(ErEntity.COMPLETE);
		boolean useAttribs= erEntity.getErView().isShowAttribs();
		
		//--- draw primary keys

		for(ErScrEntityField enField : listPks)
			if (enField.getYLocation() < rcBounds.height)
				enField.draw(big, 0, 0, -1, useTypes, useAttribs, showBgCol);

		//--- draw foreign keys

		for(ErScrEntityField enField : listFks)
			if (enField.getYLocation() < rcBounds.height)
				enField.draw(big, 0, 0, -1, useTypes, useAttribs, showBgCol);

		//--- draw fields

		for(ErScrEntityField enField : listFields)
			if (enField.getYLocation() < rcBounds.height)
				enField.draw(big, 0, 0, -1, useTypes, useAttribs, showBgCol);
	}

	//---------------------------------------------------------------------------

	private int calcImageHeight(String details)
	{
		ErScrEntityField enField;

		int height = tbName.rcBounds.height + MARGIN*2;

		if (details.equals(ErEntity.ONLY_NAME))
			return height;

		//--- primary keys
		
		if (listPks.size() == 0)
			height += MARGIN;
		else
		{
			enField = listPks.get(listPks.size()-1);
			height  = enField.getYLocation() + enField.getHeight() + MARGIN;
		}

		if (details.equals(ErEntity.NAME_AND_PKS))
			return height;
		
		//--- foreign keys
		
		if (listFks.size() == 0)
			height += MARGIN;			
		else
		{
			enField = listFks.get(listFks.size()-1);
			height  = enField.getYLocation() + enField.getHeight() + MARGIN;
		}
		
		if (details.equals(ErEntity.NAME_PKS_FKS))
			return height;
		
		//--- normal fields

		if (listFields.size() == 0)
			height += MARGIN;			
		else
		{
			enField = listFields.get(listFields.size()-1);
			height  = enField.getYLocation() + enField.getHeight() + MARGIN;
		}

		return height;
	}

	//---------------------------------------------------------------------------

	private void drawBackground(Graphics big, int width, int height)
	{
		ErScrEntityField enField;

		//--- clear all with white color

		big.setColor(Color.white);
		big.fillRect(0,0, width, height);

		//------------------------------------------------------------------------
		//--- paint background

		Color colBg     = legCol.colBg;
		Color colBorder = legCol.colBorder;

		if (bBlackWhite)
		{
			colBg     = Color.white;
			colBorder = Color.black;
		}

		big.setColor(colBg);

		switch(iEntityType)
		{
			case PRIMARY:	big.fillRect(0, 0, width, height);
							big.setColor(colBorder);
							big.drawRect(0, 0, width-1, height-1);
							break;

			case SECONDARY:	aX[1] = width-CORNER-1;
							aX[2] = width-1;
							aX[3] = width-1;
							aX[4] = width-CORNER-1;

							aY[3] = height-CORNER-1;
							aY[4] = height-1;
							aY[5] = height-1;
							aY[6] = height-CORNER-1;

							big.fillPolygon(aX, aY, 9);
							big.setColor(colBorder);
							big.drawPolygon(aX, aY, 9);
							break;

			case GENERIC:	big.fillRoundRect(0, 0, width-1, height-1, CORNER*2, CORNER*2);
							big.setColor(colBorder);
							big.drawRoundRect(0, 0, width-1, height-1, CORNER*2, CORNER*2);
							break;
		}

		//------------------------------------------------------------------------
		//--- paint separators

		String details = erEntity.getDetails();

		if (details.equals(ErEntity.ONLY_NAME))
			return;

		//--- separator between name and pks

		big.setColor(colBorder);

		int borderY = tbName.rcBounds.y + tbName.rcBounds.height + VERTSPACE;
		big.drawLine(0, borderY, width -1, borderY);

		if (details.equals(ErEntity.NAME_AND_PKS))
			return;
		
		//--- separator between pks and fkeys

		if (listPks.size() == 0)
			borderY += MARGIN;
		else
		{
			enField = listPks.get(listPks.size() -1);
			borderY = enField.getYLocation() + enField.getHeight() + VERTSPACE;
		}
		
		drawDashLine(big, width-1, borderY);
		
		if (details.equals(ErEntity.NAME_PKS_FKS))
			return;
		
		//--- separator between fkeys and fields
		
		if (listFks.size() == 0)
			borderY += MARGIN;
		else
		{
			enField = listFks.get(listFks.size() -1);
			borderY = enField.getYLocation() + enField.getHeight() + VERTSPACE;
		}
		
		drawDashLine(big, width-1, borderY);
	}

	//------------------------------------------------------------------------
	
	private void drawDashLine(Graphics big, int width, int y)
	{
		for (int i=1; i<width/4; i++)
			big.drawLine(i*4, y, i*4 +2 -1, y);
	}
	
	//------------------------------------------------------------------------

	public String toString()
	{
		return erEntity.attrSet.getString("name");
	}
}

//==============================================================================

class FieldWidth
{
	public int attrWidth = 0;
	public int nameWidth = 0;
	public int typeWidth = 0;
	
	//------------------------------------------------------------------------

	public void calcWidths(List<ErScrEntityField> list)
	{
		int currWidth;
	
		for(ErScrEntityField enField : list)
		{
			currWidth = enField.getAttrWidth();
			
			if (attrWidth < currWidth) 
				attrWidth = currWidth;
	
			currWidth = enField.getNameWidth();
			
			if (nameWidth < currWidth) 
				nameWidth = currWidth;
	
			currWidth = enField.getTypeWidth();
			
			if (typeWidth < currWidth) 
				typeWidth = currWidth;
		}
	}
}

//==============================================================================
