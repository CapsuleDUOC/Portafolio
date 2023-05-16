//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table;

import org.dlib.gui.TComboBox;
import org.dlib.gui.TTabbedPane;

import druid.data.TableNode;
import druid.data.TableVars;
import druid.panels.table.extra.ExtraPanel;
import druid.panels.table.fields.FieldsPanel;
import druid.panels.table.options.OptionsPanel;
import druid.panels.table.rules.RulePanel;
import druid.panels.table.triggers.TriggerPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.DataEntryPanel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class TablePanel extends TTabbedPane
{
	private DocEditor      docEditor   = new DocEditor();
	private DataEntryPanel varsPanel   = new DataEntryPanel();
	private OptionsPanel   optPanel    = new OptionsPanel();
	private ExtraPanel     extraPanel  = new ExtraPanel();
	private FieldsPanel    fieldsPanel = new FieldsPanel();
	private TriggerPanel   trigPanel   = new TriggerPanel();
	private RulePanel      rulesPanel  = new RulePanel();

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		addTab("Fields",   fieldsPanel);
		addTab("Vars",     varsPanel);
		addTab("Triggers", trigPanel);
		addTab("Rules",    rulesPanel);
		addTab("Docs",     docEditor);
		addTab("Options",  optPanel);
		addTab("Extra",    extraPanel);

		//------------------------------------------------------------------------

		TComboBox tcbType = new TComboBox();

		tcbType.addItem(TableVars.BOOL,   "Bool");
		tcbType.addItem(TableVars.STRING, "String");
		tcbType.addItem(TableVars.INT,    "Int");
		tcbType.addItem(TableVars.LONG,   "Long");
		tcbType.addItem(TableVars.FLOAT,  "Float");
		tcbType.addItem(TableVars.DOUBLE, "Double");
		tcbType.addItem(TableVars.CHAR,   "Char");

		varsPanel.addAttrib("name",  "Name",  220);
		varsPanel.addAttrib("type",  "Type",  130, tcbType);
		varsPanel.addAttrib("value", "Value", 200);
		varsPanel.addAttrib("descr", "Descr", 400);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------

	public void refresh(TableNode tableNode)
	{
		docEditor.setDoc(tableNode.xmlDoc);
		varsPanel.setAttribList(tableNode.tableVars);
		optPanel.refresh(tableNode);
		fieldsPanel.refresh(tableNode);
		extraPanel.refresh(tableNode);
		trigPanel.refresh(tableNode);
		rulesPanel.refresh(tableNode);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TableNode tableNode)
	{
		docEditor .getDoc(tableNode.xmlDoc);
		trigPanel .saveDataToNode();
		rulesPanel.saveDataToNode();
		extraPanel.saveDataToNode(tableNode);
		optPanel  .saveDataToNode(tableNode);
	}
}

//==============================================================================
