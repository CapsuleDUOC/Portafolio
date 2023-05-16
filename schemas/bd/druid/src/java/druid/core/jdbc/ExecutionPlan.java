//==============================================================================
//===
//===   ExecutionPlan
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.util.List;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class ExecutionPlan
{
	private TreeViewNode treePlan;
	private List<String> treeHeader;
	private String       textPlan;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ExecutionPlan(TreeViewNode treePlan, List<String> treeHeader, String textPlan)
	{
		this.treePlan   = treePlan;
		this.treeHeader = treeHeader;
		this.textPlan   = textPlan;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public TreeViewNode getTreePlan()   { return treePlan;   }
	public List<String> getTreeHeader() { return treeHeader; }
	public String       getTextPlan()   { return textPlan;   }
}

//==============================================================================
