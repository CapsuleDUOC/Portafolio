//==============================================================================
//===
//===   SequencePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.sequence;

import org.dlib.gui.TTabbedPane;

import druid.data.SequenceNode;
import druid.panels.sequence.general.GeneralPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class SequencePanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();
	private DocEditor    docEditor= new DocEditor();

	//---------------------------------------------------------------------------

	public SequencePanel()
	{
		addTab("General", genPanel);
		addTab("Docs",    docEditor);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(SequenceNode seqNode)
	{
		docEditor.setDoc(seqNode.xmlDoc);
		genPanel.refresh(seqNode);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(SequenceNode seqNode)
	{
		docEditor.getDoc(seqNode.xmlDoc);
		genPanel.saveDataToNode(seqNode);
	}
}

//==============================================================================
