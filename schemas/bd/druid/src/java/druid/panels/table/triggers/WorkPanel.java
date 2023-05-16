//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.triggers;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.Trigger;
import druid.panels.table.triggers.general.GeneralPanel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class WorkPanel extends MultiPanel implements DataModel
{
	private GeneralPanel genPanel  = new GeneralPanel();
	private DocEditor    docEditor = new DocEditor();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		TTabbedPane tp = new TTabbedPane();

		tp.addTab("General", genPanel);
		tp.addTab("Docs",    docEditor);

		add("blank",    new JPanel());
		add("trigger",  tp);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		DataTracker.beginDisabledSection();

		if (node instanceof Trigger && !node.isRoot())
		{
			Trigger trigger = (Trigger) node;

			genPanel.refresh(trigger);
			docEditor.setDoc(trigger.xmlDoc);

			show("trigger");
		}

		else
			show("blank");

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node instanceof Trigger)
		{
			Trigger trigger = (Trigger) node;

			genPanel.saveDataToNode(trigger);
			docEditor.getDoc(trigger.xmlDoc);
		}
	}
}

//==============================================================================
