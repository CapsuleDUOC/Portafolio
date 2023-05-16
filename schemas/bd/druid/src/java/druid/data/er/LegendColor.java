//==============================================================================
//===
//===   LegendColor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.er;

import java.awt.Color;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.Serials;
import druid.data.AbstractNode;

//==============================================================================

public class LegendColor extends AbstractNode
{
	public Color colName   = new Color(  0,   0,   0);
	public Color colNameBg = new Color(180, 230, 255);
	public Color colText   = new Color(  0,   0,   0);
	public Color colTextBg = new Color(230, 230, 180);
	public Color colBg     = new Color(255, 255, 200);
	public Color colBorder = new Color(  0,   0,   0);

	//---------------------------------------------------------------------------

	public LegendColor()
	{
		attrSet.addAttrib("id",        Serials.get());

		setToolTipText("A color of the legend");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new LegendColor(); }

	//---------------------------------------------------------------------------

	public int getId() { return attrSet.getInt("id"); }
	
	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		super.copyTo(node);

		LegendColor n = (LegendColor) node;

		n.colName   = GuiUtil.cloneColor(colName);
		n.colNameBg = GuiUtil.cloneColor(colNameBg);
		n.colText   = GuiUtil.cloneColor(colText);
		n.colTextBg = GuiUtil.cloneColor(colTextBg);
		n.colBg     = GuiUtil.cloneColor(colBg);
		n.colBorder = GuiUtil.cloneColor(colBorder);
	}
}

//==============================================================================
