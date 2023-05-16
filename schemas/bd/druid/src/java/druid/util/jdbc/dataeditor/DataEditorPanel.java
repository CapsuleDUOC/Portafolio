//==============================================================================
//===
//===   DataEditorPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;

import druid.core.jdbc.entities.RecordBasedEntity;
import druid.util.jdbc.ResultSetEditor;
import druid.util.jdbc.dataeditor.record.RecordEditorPanel;
import druid.util.jdbc.dataeditor.table.TableEditorPanel;

//==============================================================================

public class DataEditorPanel extends MultiPanel
{
	private TableEditorPanel  tablePanel  = new TableEditorPanel(this);
	private RecordEditorPanel recordPanel = new RecordEditorPanel(this);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DataEditorPanel()
	{
		add("blank",  new JPanel());
		add("table",  tablePanel);
		add("record", recordPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API method
	//---
	//---------------------------------------------------------------------------

	public void clearNode()
	{
		show("blank");
	}

	//---------------------------------------------------------------------------

	public void setNode(RecordBasedEntity node)
	{
		tablePanel.setNode(node);
		show("table");
	}

	//---------------------------------------------------------------------------

	public void showTableEditor()
	{
		show("table");
	}

	//---------------------------------------------------------------------------

	public void showRecordEditor(ResultSetEditor rse, int row)
	{
		recordPanel.setRecord(rse, row);
		show("record");
	}
}

//==============================================================================
