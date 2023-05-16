//==============================================================================
//===
//===   TableNodeV
//===
//===   Copyright (C) by Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import druid.core.DataLib;
import druid.data.AbstractNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.util.velocity.sets.AbstractSetV;

//==============================================================================

public class TableNodeV extends AbstractNodeV
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TableNodeV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String getSqlCommands()
	{
		return node.attrSet.getString("sqlCommands");
	}

	//---------------------------------------------------------------------------

	public Collection getFields()
	{
		return convertCollection(node.getObjects(FieldNode.class));
	}

	//---------------------------------------------------------------------------

	public Collection getTriggers()
	{
		return convertCollection(getTableNode().triggers);
	}

	//---------------------------------------------------------------------------

	public Collection getVars()
	{
		return AbstractSetV.convertSet("TableVarV", getTableNode().tableVars);
	}

	//---------------------------------------------------------------------------

	public Collection getRules()
	{
		return convertCollection(getTableNode().rules);
	}

	//---------------------------------------------------------------------------

	public Collection getReferringTables()
	{
		return convertCollection(DataLib.getReferences(getTableNode(), true));
	}

	//---------------------------------------------------------------------------

	public Collection getReferringFields()
	{
		Vector refFields = new Vector();

		int id = as.getInt("id");

		Vector   tables = DataLib.getReferences(getTableNode(), true);
		Iterator iter   = tables.iterator();

		while(iter.hasNext())
		{
			TableNode table = (TableNode)iter.next();

			Enumeration fields = table.children();

			while(fields.hasMoreElements())
			{
				FieldNode field = (FieldNode) fields.nextElement();

				if (field.isFkey() && field.attrSet.getInt("refTable") == id)
					refFields.add(field);
			}
		}

		return convertCollection(refFields);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private TableNode getTableNode()
	{
		return (TableNode) node;
	}
}

//==============================================================================
