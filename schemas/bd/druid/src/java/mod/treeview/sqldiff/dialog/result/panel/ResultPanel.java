//==============================================================================
//===
//===   ResultPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel;

import javax.swing.JPanel;

import mod.treeview.sqldiff.dialog.result.NodeInfo;
import mod.treeview.sqldiff.dialog.result.panel.database.DatabasePanel;
import mod.treeview.sqldiff.dialog.result.panel.field.FieldPanel;
import mod.treeview.sqldiff.dialog.result.panel.sequence.SequencePanel;
import mod.treeview.sqldiff.dialog.result.panel.table.TablePanel;
import mod.treeview.sqldiff.struct.DiffElement;
import mod.treeview.sqldiff.struct.DiffEntity;
import mod.treeview.sqldiff.struct.DiffSummary;

import org.dlib.gui.MultiPanel;

//==============================================================================

public class ResultPanel extends MultiPanel
{
	private DatabasePanel panDb    = new DatabasePanel();
	private TablePanel    panTable = new TablePanel();
	private FieldPanel    panField = new FieldPanel();
	private SqlPanel      panSql   = new SqlPanel();
	private SequencePanel panSequen= new SequencePanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ResultPanel()
	{
		add("blank",  new JPanel());
		add("db",     panDb);
		add("table",  panTable);
		add("field",  panField);
		add("sql",    panSql);
		add("sequen", panSequen);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(DiffSummary diffSumm, NodeInfo info)
	{
		if (info == null)
			show("blank");

		else if (info.iCatalog == DiffSummary.DATABASE)
		{
			panDb.setCurrentNode(diffSumm, info.deEntity);
			show("db");
		}

		else if (info.iCatalog == DiffSummary.TABLE)
		{
			panTable.setCurrentNode(diffSumm, info.deEntity);
			show("table");
		}

		else if (info.iCatalog == DiffSummary.FIELD)
		{
			panField.setCurrentNode(diffSumm, info.deEntity);
			show("field");
		}

		else if (info.iCatalog == DiffSummary.SEQUENCE)
		{
			panSequen.setCurrentNode(info.deEntity);
			show("sequen");
		}

		else
		{
			DiffEntity  ent  = info.deEntity;
			DiffElement diff = ent.get(DiffEntity.PFV_SQLCODE);

			panSql.setText(diff);
			show("sql");
		}
	}
}

//==============================================================================
