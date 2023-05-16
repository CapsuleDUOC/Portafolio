//==============================================================================
//===
//===   NodeInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result;

import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.IconLine;

import druid.util.gui.ImageFactory;

//==============================================================================

public class NodeInfo
{
	public int        iCatalog;
	public DiffEntity deEntity;
	public IconLine   icon = new IconLine(1);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public NodeInfo(int catalog, DiffEntity diffEnt)
	{
		iCatalog = catalog;
		deEntity = diffEnt;

		//--- handle the case of diff only on tablespaces
		//--- in this case there is no DATABASE diff-entity

		if (diffEnt == null || catalog == DiffSummary.DATABASE)
		{
			icon.addImage(ImageFactory.DATABASE.getImage());

			return;
		}

		//--- first image

		if (diffEnt.isNone())
			icon.addImage(ImageFactory.NULL.getImage());

		else if (diffEnt.isAdded())
			icon.addImage(ImageFactory.NEW.getImage());

		else if (diffEnt.isRemoved())
			icon.addImage(ImageFactory.DELETE.getImage());

		else
			icon.addImage(ImageFactory.LENS.getImage());

		//--- second image

		if (catalog == DiffSummary.TABLE)
			icon.addImage(ImageFactory.TABLE.getImage());

		else if (catalog == DiffSummary.FIELD)
			icon.addImage(ImageFactory.FIELD.getImage());

		else if (catalog == DiffSummary.VIEW)
			icon.addImage(ImageFactory.VIEW.getImage());

		else if (catalog == DiffSummary.PROCEDURE)
			icon.addImage(ImageFactory.PROCEDURE.getImage());

		else if (catalog == DiffSummary.FUNCTION)
			icon.addImage(ImageFactory.FUNCTION.getImage());

		else if (catalog == DiffSummary.TRIGGER)
			icon.addImage(ImageFactory.TRIGGER.getImage());

		else if (catalog == DiffSummary.SEQUENCE)
			icon.addImage(ImageFactory.SEQUENCE.getImage());

		else if (catalog == DiffSummary.TABLERULE)
			icon.addImage(ImageFactory.RULE.getImage());
	}
}

//==============================================================================
