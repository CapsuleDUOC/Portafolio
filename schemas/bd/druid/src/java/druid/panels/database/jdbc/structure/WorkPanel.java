//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure;


import javax.swing.JPanel;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataModel;
import druid.core.jdbc.entities.DatabaseEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.SynonymEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.UDTEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.panels.database.jdbc.structure.db.DbPanel;
import druid.panels.database.jdbc.structure.function.FunctionPanel;
import druid.panels.database.jdbc.structure.procedure.ProcedurePanel;
import druid.panels.database.jdbc.structure.sequence.SequencePanel;
import druid.panels.database.jdbc.structure.synonym.SynonymPanel;
import druid.panels.database.jdbc.structure.table.TablePanel;
import druid.panels.database.jdbc.structure.udt.UDTPanel;
import druid.panels.database.jdbc.structure.view.ViewPanel;

//==============================================================================

public class WorkPanel extends MultiPanel implements DataModel
{
	private DbPanel        dbPanel    = new DbPanel();
	private TablePanel     tablePanel = new TablePanel();
	private ViewPanel      viewPanel  = new ViewPanel();
	private ProcedurePanel procPanel  = new ProcedurePanel();
	private FunctionPanel  funcPanel  = new FunctionPanel();
	private SynonymPanel   synonPanel = new SynonymPanel();
	private SequencePanel  sequenPanel= new SequencePanel();
	private UDTPanel       udtPanel   = new UDTPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank",     new JPanel());
		add("database",  dbPanel);
		add("table",     tablePanel);
		add("view",      viewPanel);
		add("procedure", procPanel);
		add("function",  funcPanel);
		add("synonym",   synonPanel);
		add("sequence",  sequenPanel);
		add("udt",       udtPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//---   DataModel
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		if (node instanceof DatabaseEntity)
		{
			dbPanel.refresh((DatabaseEntity)node);
			show("database");
		}

		//------------------------------------------------------------------------

		else if (node instanceof TableEntity)
		{
			tablePanel.refresh((TableEntity)node);
			show("table");
		}

		//------------------------------------------------------------------------

		else if (node instanceof ViewEntity)
		{
			viewPanel.refresh((ViewEntity)node);
			show("view");
		}

		//------------------------------------------------------------------------

		else if (node instanceof ProcedureEntity)
		{
			procPanel.refresh((ProcedureEntity)node);
			show("procedure");
		}

		//------------------------------------------------------------------------

		else if (node instanceof FunctionEntity)
		{
			funcPanel.refresh((FunctionEntity)node);
			show("function");
		}

		//------------------------------------------------------------------------

		else if (node instanceof SynonymEntity)
		{
			synonPanel.refresh((SynonymEntity)node);
			show("synonym");
		}

		//------------------------------------------------------------------------

		else if (node instanceof SequenceEntity)
		{
			sequenPanel.refresh((SequenceEntity)node);
			show("sequence");
		}

		//------------------------------------------------------------------------

		else if (node instanceof UDTEntity)
		{
			udtPanel.refresh((UDTEntity)node);
			show("udt");
		}

		//------------------------------------------------------------------------

		else
			show("blank");
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node) {}
}

//==============================================================================
