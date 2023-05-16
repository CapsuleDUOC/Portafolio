//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.rules;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.TableRule;
import druid.panels.table.rules.general.GeneralPanel;
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

		add("blank", new JPanel());
		add("rule",  tp);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		DataTracker.beginDisabledSection();

		if (node instanceof TableRule && !node.isRoot())
		{
			TableRule rule = (TableRule) node;

			genPanel.refresh(rule);
			docEditor.setDoc(rule.xmlDoc);

			show("rule");
		}

		else
			show("blank");

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node instanceof TableRule)
		{
			TableRule rule = (TableRule) node;

			genPanel.saveDataToNode(rule);
			docEditor.getDoc(rule.xmlDoc);
		}
	}
}

//==============================================================================
