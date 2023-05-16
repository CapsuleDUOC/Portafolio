//==============================================================================
//===
//===   ProjectNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class ProjectNode extends AbstractNode
{
	//---------------------------------------------------------------------------

	public ProjectNode()
	{
		//--- the serial attrib is updated after a save operation
		//--- here, the value '0' is only used to add the attrib
		//--- to get the serial use Serials.lastSerial

		attrSet.addAttrib("serial", 0);
		attrSet.addAttrib("build",  1);

		attrSet.removeAttrib("name");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new ProjectNode(); }
}

//==============================================================================
