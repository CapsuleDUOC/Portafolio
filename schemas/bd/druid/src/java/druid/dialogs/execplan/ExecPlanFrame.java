//==============================================================================
//===
//===   ExecPlanFrame
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.execplan;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import druid.core.jdbc.ExecutionPlan;

//==============================================================================

public class ExecPlanFrame extends JFrame
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ExecPlanFrame()
	{
		setTitle("Execution Plan");

		JTabbedPane pane = new JTabbedPane();
		pane.addTab("Query",     tabQuery);
		pane.addTab("Tree plan", tabTreePlan);
		pane.addTab("Text plan", tabTextPlan);
		
		getContentPane().add(pane, BorderLayout.CENTER);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(900,700);
		setLocationRelativeTo(getParent());
	}

	//---------------------------------------------------------------------------
	//---
	//---   API methods
	//---
	//---------------------------------------------------------------------------

	public void showPlan(String query, ExecutionPlan plan)
	{
		tabQuery   .setText(query);
		tabTreePlan.setupTable(plan.getTreePlan(), plan.getTreeHeader());
		tabTextPlan.setText(plan.getTextPlan());

		setVisible(true);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private TextPanel tabQuery    = new TextPanel();
	private TreePanel tabTreePlan = new TreePanel();
	private TextPanel tabTextPlan = new TextPanel();
}

//==============================================================================
