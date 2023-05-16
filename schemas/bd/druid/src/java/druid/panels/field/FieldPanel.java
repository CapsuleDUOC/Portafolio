//==============================================================================
//===
//===   FieldPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.field;

import org.dlib.gui.TTabbedPane;

import druid.data.FieldNode;
import druid.panels.field.options.OptionsPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class FieldPanel extends TTabbedPane
{
	private DocEditor    docEditor= new DocEditor();
	private OptionsPanel optPanel = new OptionsPanel();

	//---------------------------------------------------------------------------

	public FieldPanel()
	{
		addTab("Docs",    docEditor);
		addTab("Options", optPanel);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldNode fieldNode)
	{
		docEditor.setDoc(fieldNode.xmlDoc);
		optPanel.refresh(fieldNode);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(FieldNode fieldNode)
	{
		docEditor.getDoc(fieldNode.xmlDoc);
	}
}

//==============================================================================
