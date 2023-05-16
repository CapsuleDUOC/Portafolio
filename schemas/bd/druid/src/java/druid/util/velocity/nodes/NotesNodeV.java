//==============================================================================
//===
//===   NotesNodeV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import druid.data.AbstractNode;
import druid.data.NotesNode;

//==============================================================================

public class NotesNodeV extends AbstractNodeV
{
	public NotesNodeV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public boolean getIsInfo()   { return getNotes().isInfo();   }
	public boolean getIsAlert()  { return getNotes().isAlert();  }
	public boolean getIsDanger() { return getNotes().isDanger(); }

	//---------------------------------------------------------------------------

	private NotesNode getNotes() { return (NotesNode) node; }
}

//==============================================================================
