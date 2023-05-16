//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels;

import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.panels.database.DatabasePanel;
import druid.panels.field.FieldPanel;
import druid.panels.folder.FolderPanel;
import druid.panels.function.FunctionPanel;
import druid.panels.notes.NotesPanel;
import druid.panels.procedure.ProcedurePanel;
import druid.panels.sequence.SequencePanel;
import druid.panels.table.TablePanel;
import druid.panels.view.ViewPanel;

//==============================================================================

public class WorkPanel extends MultiPanel implements DataModel
{
	private DatabasePanel  databaseP = new DatabasePanel();
	private FolderPanel    folderP   = new FolderPanel();
	private TablePanel     tableP    = new TablePanel();
	private ViewPanel      viewP     = new ViewPanel();
	private ProcedurePanel procP     = new ProcedurePanel();
	private FunctionPanel  funcP     = new FunctionPanel();
	private FieldPanel     fieldP    = new FieldPanel();
	private SequencePanel  sequenceP = new SequencePanel();
	private NotesPanel     notesP    = new NotesPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank",    new JPanel());
		add("database", databaseP);
		add("folder",   folderP);
		add("table",    tableP);
		add("view",     viewP);
		add("procedure",procP);
		add("function", funcP);
		add("field",    fieldP);
		add("sequence", sequenceP);
		add("notes",    notesP);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode treeNode)
	{
		DataTracker.beginDisabledSection();

		if (treeNode instanceof DatabaseNode)
		{
			databaseP.refresh((DatabaseNode)treeNode);
			show("database");
		}

		else if (treeNode instanceof FolderNode)
		{
			folderP.refresh((FolderNode)treeNode);
			show("folder");
		}

		else if (treeNode instanceof TableNode)
		{
			tableP.refresh((TableNode)treeNode);
			show("table");
		}

		else if (treeNode instanceof ViewNode)
		{
			viewP.refresh((ViewNode)treeNode);
			show("view");
		}

		else if (treeNode instanceof ProcedureNode)
		{
			procP.refresh((ProcedureNode)treeNode);
			show("procedure");
		}

		else if (treeNode instanceof FunctionNode)
		{
			funcP.refresh((FunctionNode)treeNode);
			show("function");
		}

		else if (treeNode instanceof FieldNode)
		{
			fieldP.refresh((FieldNode)treeNode);
			show("field");
		}

		else if (treeNode instanceof SequenceNode)
		{
			sequenceP.refresh((SequenceNode)treeNode);
			show("sequence");
		}

		else if (treeNode instanceof NotesNode)
		{
			notesP.refresh((NotesNode)treeNode);
			show("notes");
		}

		else show("blank");

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node instanceof DatabaseNode)
			databaseP.saveDataToNode((DatabaseNode)node);

		if (node instanceof FolderNode)
			folderP.saveDataToNode((FolderNode)node);

		if (node instanceof TableNode)
			tableP.saveDataToNode((TableNode)node);

		if (node instanceof ViewNode)
			viewP.saveDataToNode((ViewNode)node);

		if (node instanceof ProcedureNode)
			procP.saveDataToNode((ProcedureNode)node);

		if (node instanceof FunctionNode)
			funcP.saveDataToNode((FunctionNode)node);

		if (node instanceof FieldNode)
			fieldP.saveDataToNode((FieldNode)node);

		if (node instanceof SequenceNode)
			sequenceP.saveDataToNode((SequenceNode)node);

		if (node instanceof NotesNode)
			notesP.saveDataToNode((NotesNode)node);
	}
}

//==============================================================================
