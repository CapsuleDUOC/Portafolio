//==============================================================================
//===
//===   ProcedureNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class ProcedureNode extends AbstractNode
{
	//---------------------------------------------------------------------------

	public ProcedureNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public ProcedureNode(String name)
	{
		super(name);

		attrSet.addAttrib("sqlCode", "CREATE PROCEDURE...");

		setToolTipText("A procedure: Use the 'sql' tab to write the code");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ProcedureNode(); }
}

//==============================================================================
