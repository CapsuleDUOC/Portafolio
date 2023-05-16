//==============================================================================
//===
//===   RecordEditorPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import druid.util.jdbc.ResultSetEditor;
import druid.util.jdbc.dataeditor.DataEditorPanel;

//==============================================================================

public class RecordEditorPanel extends JPanel
{
	private RecordView recordView = new RecordView();
	private WorkPanel  workPanel  = new WorkPanel();

	//---------------------------------------------------------------------------

	public RecordEditorPanel(DataEditorPanel dep)
	{
		recordView.setDataModel(workPanel);
		workPanel.setDataEditor(dep);

		//------------------------------------------------------------------------
		//--- split pane

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, recordView, workPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		sp.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		recordView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setRecord(ResultSetEditor rse, int editRow)
	{
		recordView.setRootNode(rse);
		workPanel.setData(rse, editRow);
		workPanel.setCurrentNode(null);
	}
}

//==============================================================================
