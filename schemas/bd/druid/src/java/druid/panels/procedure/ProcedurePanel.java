//==============================================================================
//===
//===   ProcedurePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.procedure;

import org.dlib.gui.TTabbedPane;

import druid.data.ProcedureNode;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.ExtraSqlPanel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class ProcedurePanel extends TTabbedPane
{
	private DocEditor     docEditor = new DocEditor();
	private ExtraSqlPanel sqlPanel  = new ExtraSqlPanel("sqlCode", "Definition", false);

	//---------------------------------------------------------------------------

	public ProcedurePanel()
	{
		addTab("Sql",  sqlPanel);
		addTab("Docs", docEditor);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(ProcedureNode node)
	{
		docEditor.setDoc(node.xmlDoc);
		sqlPanel.refresh(node.attrSet);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(ProcedureNode node)
	{
		docEditor.getDoc(node.xmlDoc);
		sqlPanel.saveDataToNode(node.attrSet);
	}
}

//==============================================================================
