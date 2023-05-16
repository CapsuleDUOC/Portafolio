//==============================================================================
//===
//===   FolderNodeV
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import druid.core.DataLib;
import druid.data.AbstractNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;

//==============================================================================

public class FolderNodeV extends AbstractNodeV
{
	public FolderNodeV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public Collection getAllChildren()   { return convertCollection(node.getObjects(AbstractNode.class));  }
	public Collection getAllFolders()    { return convertCollection(node.getObjects(FolderNode.class));    }
	public Collection getAllTables()     { return convertCollection(node.getObjects(TableNode.class));     }
	public Collection getAllFields()     { return convertCollection(node.getObjects(FieldNode.class));     }
	public Collection getAllViews()      { return convertCollection(node.getObjects(ViewNode.class));      }
	public Collection getAllProcedures() { return convertCollection(node.getObjects(ProcedureNode.class)); }
	public Collection getAllFunctions()  { return convertCollection(node.getObjects(FunctionNode.class));  }
	public Collection getAllSequences()  { return convertCollection(node.getObjects(SequenceNode.class));  }
	public Collection getAllNotes()      { return convertCollection(node.getObjects(NotesNode.class));     }

	//---------------------------------------------------------------------------

	public Collection getAllSortedChildren()
   {
		Vector v = new Vector();

		Enumeration e=node.preorderEnumeration();

		//--- skip itself
		e.nextElement();

		while(e.hasMoreElements())
			v.add(e.nextElement());

		DataLib.sortObjects(v);

		return convertCollection(v);
	}
}

//==============================================================================
