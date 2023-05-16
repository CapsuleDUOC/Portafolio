//==============================================================================
//===
//===   NotesPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.notes;

import org.dlib.gui.TTabbedPane;

import druid.data.NotesNode;
import druid.panels.notes.general.GeneralPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class NotesPanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();
	private DocEditor    docEditor= new DocEditor();

	//---------------------------------------------------------------------------

	public NotesPanel()
	{
		addTab("General", genPanel);
		addTab("Docs",    docEditor);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(NotesNode node)
	{
		genPanel.refresh(node);
		docEditor.setDoc(node.xmlDoc);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(NotesNode node)
	{
		genPanel.saveDataToNode(node);
		docEditor.getDoc(node.xmlDoc);
	}
}

//==============================================================================
