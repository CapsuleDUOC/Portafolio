//==============================================================================
//===
//===   FieldNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.AttribSet;
import druid.core.Serials;

//==============================================================================

public class FieldNode extends AbstractNode
{
	public AttribSet fieldAttribs = new AttribSet();

	//--- field type (simple or fkey)
	//--- type != 0 --> basic type
	//--- type == 0 --> fkey (in this case use refTable and refField to obtain the id)
	//---
	//--- if both type and refTable are == 0 the type is not set

	//---------------------------------------------------------------------------

	public static final String NOACTION   = "n";
	public static final String CASCADE    = "c";
	public static final String SETNULL    = "s";
	public static final String SETDEFAULT = "d";
	public static final String RESTRICT   = "r";

	public static final String FULL    = "f";
	public static final String PARTIAL = "p";
	public static final String SIMPLE  = "s";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FieldNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public FieldNode(String name)
	{
		super(name);

		attrSet.addAttrib("id",        Serials.get());
		attrSet.addAttrib("comment",   "");

		attrSet.addAttrib("type",      0);
		attrSet.addAttrib("refTable",  0);
		attrSet.addAttrib("refField",  0);
		attrSet.addAttrib("matchType", SIMPLE);
		attrSet.addAttrib("onUpdate",  NOACTION);
		attrSet.addAttrib("onDelete",  NOACTION);

		setToolTipText("A table's field");
	}

	//---------------------------------------------------------------------------
	protected TreeViewNode getNewInstance() { return new FieldNode(); }
	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		FieldNode n = (FieldNode) node;

		n.fieldAttribs = fieldAttribs.duplicate();

		super.copyTo(node);
	}

	//---------------------------------------------------------------------------
	
	public boolean isPkey()
	{
		AttribSet as = getDatabase().fieldAttribs.findPrimaryKey();
		
		if (as == null)
			return false;

		String id = Integer.toString(as.getInt("id"));
		
		return fieldAttribs.getBool(id);
	}

	//---------------------------------------------------------------------------

	public boolean isFkey()
	{
		int type     = attrSet.getInt("type");
		int refTable = attrSet.getInt("refTable");

		return (type == 0) && (refTable != 0);
	}

	//---------------------------------------------------------------------------
	
	public TableNode getReferencedTable()
	{
		int refTable = attrSet.getInt("refTable");
		
		return getDatabase().getTableByID(refTable);
	}
	
	//---------------------------------------------------------------------------

	public FieldInfo getInfo()
	{
		FieldInfo fi = new FieldInfo();

		fi.name     = attrSet.getString("name");
		fi.type     = attrSet.getInt("type");
		fi.refTable = attrSet.getInt("refTable");
		fi.refField = attrSet.getInt("refField");
		fi.isFkey   = (fi.type == 0) && (fi.refTable != 0);
		fi.matchType= attrSet.getString("matchType");
		fi.onUpdate = attrSet.getString("onUpdate");
		fi.onDelete = attrSet.getString("onDelete");

		return fi;
	}
}

//==============================================================================
