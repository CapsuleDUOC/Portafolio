//==============================================================================
//===
//===   HelpNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.helpnavigator;

import java.util.Enumeration;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class HelpNode extends TreeViewNode
{
	public String name;
	public String base;
	public String file;

	//---------------------------------------------------------------------------

	public HelpNode() {}

	//---------------------------------------------------------------------------
	//---
	//---   API Methods
	//---
	//---------------------------------------------------------------------------

	public void setup()
	{
		for(Enumeration e=preorderEnumeration(); e.hasMoreElements();)
		{
			HelpNode node = (HelpNode) e.nextElement();

			if (node.name != null)
				node.setText(node.name);
		}
	}

	//---------------------------------------------------------------------------

	public String getFile(HelpNode node)
	{
		//--- we must handle the root's node special case
		if (node.file == null)
			return null;

		if (node.file.startsWith("#"))
			return getFile((HelpNode) node.getParent()) + node.file;
		else
		{
			String path = "";

			while (!(node instanceof HelpNode))
			{
				if (node.base != null)
					path = node.base + "/" + path;

				node = (HelpNode) node.getParent();
			}

			return path + node.file;
		}
	}

	//---------------------------------------------------------------------------

	public HelpNode getNodeFromFile(String file)
	{
		for(Enumeration e = preorderEnumeration(); e.hasMoreElements();)
		{
			HelpNode node = (HelpNode) e.nextElement();

			if (file.equals(getFile(node)))
				return node;
		}

		return null;
	}
}

//==============================================================================
