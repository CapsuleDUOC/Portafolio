//==============================================================================
//===
//===   FieldPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.field;

import mod.treeview.sqldiff.dialog.result.panel.AttribPanel;
import mod.treeview.sqldiff.dialog.result.panel.DiffUtil;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.TTabbedPane;

import druid.util.decoder.OnClauseDecoder;

//==============================================================================

public class FieldPanel extends TTabbedPane
{
	private AttribPanel      panGeneral = new AttribPanel();
	private FieldAttribPanel panAttribs = new FieldAttribPanel();

	private OnClauseDecoder onDec = new OnClauseDecoder();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FieldPanel()
	{
		addTab("General",  panGeneral);
		addTab("Attribs",  panAttribs);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(DiffSummary diffSumm, DiffEntity ent)
	{
		//--- setup general attribs

		panGeneral.initAttribs();
		panGeneral.setAttrib(ent.get(DiffEntity.FI_TYPE),     "Type");
		panGeneral.setAttrib(ent.get(DiffEntity.FI_FKEY),     "Foreign key");
		panGeneral.setAttrib(ent.get(DiffEntity.FI_ONUPDATE), "On update", onDec);
		panGeneral.setAttrib(ent.get(DiffEntity.FI_ONDELETE), "On delete", onDec);
		setEnabledAt(0, panGeneral.updatePanel());

		panAttribs.initAttribs();

		//--- setup field attribs

		java.util.List list = diffSumm.list(DiffSummary.FIELDATTRIB);

		for(int i=0; i<list.size(); i++)
		{
			DiffEntity entAttr = (DiffEntity) list.get(i);

			if (entAttr.getParent() == ent)
				panAttribs.setAttrib(entAttr);
		}

		setEnabledAt(1, panAttribs .updatePanel());

		DiffUtil.showTab(this);
	}
}

//==============================================================================
