//==============================================================================
//===
//===   Legend
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.er;

import org.dlib.gui.TFont;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;

//==============================================================================

public class Legend extends AbstractNode
{
	//---------------------------------------------------------------------------
	//--- legend location on E/R view

	public static final String NONE = "-";
	public static final String NW   = "nw";
	public static final String NE   = "ne";
	public static final String SW   = "sw";
	public static final String SE   = "se";

	//---------------------------------------------------------------------------

	public Legend()
	{
		attrSet.addAttrib("location",   SE);

		attrSet.addAttrib("fontFamily", "Default");
		attrSet.addAttrib("fontStyle",  TFont.PLAIN);
		attrSet.addAttrib("fontSize",   10);

		//--- there must be at least one color for each view
		addChild(new LegendColor());
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new Legend(); }

	//---------------------------------------------------------------------------

	public LegendColor getColor(int id)
	{
		for(int i=0; i<getChildCount(); i++)
		{
			LegendColor lc = (LegendColor) getChild(i);

			if (lc.attrSet.getInt("id") == id) return lc;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public int getFirstColorId()
	{
		return ((LegendColor)getChild(0)).attrSet.getInt("id");
	}
}

//==============================================================================
