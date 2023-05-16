//==============================================================================
//===
//===   FolderPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.folder;

import org.dlib.gui.TTabbedPane;

import druid.data.FolderNode;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class FolderPanel extends TTabbedPane
{
	private DocEditor docEditor = new DocEditor();

	//---------------------------------------------------------------------------

	public FolderPanel()
	{
		addTab("Docs", docEditor);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(FolderNode folderNode)
	{
		docEditor.setDoc(folderNode.xmlDoc);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(FolderNode folderNode)
	{
		docEditor.getDoc(folderNode.xmlDoc);
	}
}

//==============================================================================
