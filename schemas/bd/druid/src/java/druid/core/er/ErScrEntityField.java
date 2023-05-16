//==============================================================================
//===
//===   ErScrEntityField
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.Graphics;
import java.awt.Rectangle;

import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.FieldNode;

//==============================================================================

public class ErScrEntityField
{
	private FieldNode field;

	private Rectangle rcBounds;

	private TextBox tbAttr;
	private TextBox tbName;
	private TextBox tbType;

	//---------------------------------------------------------------------------

	public ErScrEntityField(FieldNode f, ErFontData erfd)
	{
		field = f;

		String name = f.attrSet.getString("name");
		String type = DataTypeLib.getTypeDef(f);

		//--- the following TextBoxes overlap
		//--- the ErScrEntity should align them

		tbAttr = new TextBox(getAttributes(), erfd);
		tbName = new TextBox(name, erfd);
		tbType = new TextBox(type, erfd);
		
		boolean notn = DataLib.isNotNull(f);
		boolean uniq = DataLib.isUnique(f);
		
		tbAttr.setBold(uniq);
		tbName.setBold(uniq);
		tbType.setBold(uniq);
		
		tbAttr.setItalic(notn);
		tbName.setItalic(notn);
		tbType.setItalic(notn);
	}

	//---------------------------------------------------------------------------

	public int getAttrWidth() { return tbAttr.rcBounds.width; }
	public int getNameWidth() { return tbName.rcBounds.width; }
	public int getTypeWidth() { return tbType.rcBounds.width; }
	public int getYLocation() { return rcBounds.y;            }
	public int getHeight()    { return rcBounds.height;       }

	public FieldNode getFieldNode() { return field; }

	//---------------------------------------------------------------------------

	public int getRefTable()
	{
		if (!field.isFkey()) return 0;

		return field.attrSet.getInt("refTable");
	}

	//---------------------------------------------------------------------------

	public void setWidths(int x, FieldWidth fw, boolean useAttribs)
	{
		tbAttr.rcBounds.x     = x;
		tbAttr.rcBounds.width = fw.attrWidth;

		tbName.rcBounds.x     = tbAttr.rcBounds.x + (useAttribs ? fw.attrWidth +1 : 0);
		tbName.rcBounds.width = fw.nameWidth;

		tbType.rcBounds.x     = tbName.rcBounds.x + fw.nameWidth +1;
		tbType.rcBounds.width = fw.typeWidth;
	}

	//---------------------------------------------------------------------------

	public void setY(int y)
	{
		tbAttr.rcBounds.y = y;
		tbName.rcBounds.y = y;
		tbType.rcBounds.y = y;
	}

	//---------------------------------------------------------------------------
	//--- call only after setWidths(...) and setY(...)

	public void recalcBounds(boolean incType, boolean incAttrib)
	{
		//--- recalc field bundaries

		rcBounds = new Rectangle(tbName.rcBounds);

		if (incAttrib)
			rcBounds.width += tbAttr.rcBounds.width +1;

		if (incType) 
			rcBounds.width += tbType.rcBounds.width +1;
	}

	//---------------------------------------------------------------------------

	public void draw(Graphics g, int dx, int dy, int invIdx, boolean useTypes,  
					 boolean useAttribs, boolean drawBgCol)
	{
		if (useAttribs)
			tbAttr.draw(g, dx, dy, invIdx == 0, drawBgCol);
		
		tbName.draw(g, dx, dy, invIdx == 1, drawBgCol);

		if (useTypes)
			tbType.draw(g, dx, dy, invIdx == 2, drawBgCol);
	}

	//---------------------------------------------------------------------------

	private String getAttributes()
	{
		if (DataLib.isPrimaryKey(field)) return " PK ";
		if (field.isFkey())              return " FK ";

		boolean bNotNull = DataLib.isNotNull(field);
		boolean bUnique  = DataLib.isUnique(field);

		if (bNotNull && bUnique) return " K ";

		if (bNotNull) return " N ";
		if (bUnique)  return " U ";

		return " A ";
	}
}

//==============================================================================
