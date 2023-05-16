//==============================================================================
//===
//===   NoteNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class NotesNode extends AbstractNode
{
	public static final String INFO    = "i";
	public static final String ALERT   = "?";
	public static final String DANGER  = "!";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public NotesNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public NotesNode(String name)
	{
		super(name);

		attrSet.addAttrib("type", INFO);

		setToolTipText("Notes that don't fit into folders, tables, etc...");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean isInfo()   { return attrSet.getString("type").equals(INFO);   }
	public boolean isAlert()  { return attrSet.getString("type").equals(ALERT);  }
	public boolean isDanger() { return attrSet.getString("type").equals(DANGER); }

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new NotesNode(); }
}

//==============================================================================
