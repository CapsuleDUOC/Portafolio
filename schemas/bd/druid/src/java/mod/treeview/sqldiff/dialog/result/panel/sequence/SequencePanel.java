//==============================================================================
//===
//===   SequencePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.sequence;

import mod.treeview.sqldiff.dialog.result.panel.AttribPanel;
import mod.treeview.sqldiff.struct.DiffEntity;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class SequencePanel extends TTabbedPane
{
	private AttribPanel panGeneral = new AttribPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SequencePanel()
	{
		addTab("General", panGeneral);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(DiffEntity ent)
	{
		panGeneral.initAttribs();

		panGeneral.setAttrib(ent.get(DiffEntity.SQ_INCREM), "Increment");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_MINVAL), "Min value");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_MAXVAL), "Max value");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_START),  "Start");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_CACHE),  "Cache");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_CYCLE),  "Cycle");
		panGeneral.setAttrib(ent.get(DiffEntity.SQ_ORDER),  "Order");

		panGeneral.updatePanel();
	}
}

//==============================================================================
