//==============================================================================
//===
//===   ErView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.er;

import org.dlib.gui.TFont;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;

//==============================================================================

public class ErView extends AbstractNode
{
	public Legend legend = new Legend();

	//---------------------------------------------------------------------------

	public ErView()
	{
		attrSet.addAttrib("details",      ErEntity.COMPLETE);

		attrSet.addAttrib("nameFamily",   "Default");
		attrSet.addAttrib("nameStyle",    TFont.BOLD_ITALIC);
		attrSet.addAttrib("nameSize",     15);

		attrSet.addAttrib("fieldsFamily", "Default");
		attrSet.addAttrib("fieldsStyle",  TFont.PLAIN);
		attrSet.addAttrib("fieldsSize",   10);

		attrSet.addAttrib("showAttribs",  true);
		attrSet.addAttrib("showBgColor",  true);
		
		setToolTipText("An E/R view of your database");
	}

	//---------------------------------------------------------------------------
	
	public boolean isShowAttribs() { return attrSet.getBool("showAttribs"); }
	public boolean isShowBgColor() { return attrSet.getBool("showBgColor"); }
	
	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ErView(); }

	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		ErView n = (ErView) node;

		n.legend = (Legend) legend.duplicate();

		super.copyTo(node);
	}

	//---------------------------------------------------------------------------

	public void updateData()
	{
		//--- update data if something is changed into the db
		//--- if the user adds/removes a table or a field the entity
		//--- must be updated

		for(int i=0, size = getChildCount(); i<size; i++)
		{
			ErEntity e = (ErEntity) getChild(i);

			e.updateData();
		}
	}
}

//==============================================================================
