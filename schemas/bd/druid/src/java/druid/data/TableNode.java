//==============================================================================
//===
//===   TableNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.Serials;

//==============================================================================

public class TableNode extends AbstractNode
{
	public TableVars  tableVars = new TableVars();
	public Trigger    triggers  = new Trigger();
	public TableRule  rules     = new TableRule();

	//---------------------------------------------------------------------------

	public TableNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public TableNode(String name)
	{
		super(name);

		attrSet.addAttrib("id",          Serials.get());
		attrSet.addAttrib("comment",     "");
		attrSet.addAttrib("sqlCommands", "");
		attrSet.addAttrib("tempPK",      "");
		attrSet.addAttrib("tempFK",      "");
		attrSet.addAttrib("tempOther",   "");
		attrSet.addAttrib("schema",      "");
		attrSet.addAttrib("ghost",       false);

		triggers.setParentTable(this);
		rules.setParentTable(this);

		setToolTipText("A database's table");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new TableNode(); }

	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		TableNode n = (TableNode) node;

		n.tableVars = (TableVars) tableVars.duplicate();
		n.triggers  = (Trigger)   triggers.duplicate();
		n.rules     = (TableRule) rules.duplicate();

		n.triggers.setParentTable(n);
		n.rules.setParentTable(n);

		super.copyTo(node);
	}

	//---------------------------------------------------------------------------

	public FieldNode getFieldByID(int id)
	{
		for(int i=0; i<getChildCount(); i++)
			if (((FieldNode)getChild(i)).attrSet.getInt("id") == id)
				return (FieldNode)getChild(i);

		return null;
	}

	//---------------------------------------------------------------------------

	public FieldNode getFieldByName(String name)
	{
		for(int i=0; i<getChildCount(); i++)
			if (((FieldNode)getChild(i)).attrSet.getString("name").equals(name))
				return (FieldNode) getChild(i);

		return null;
	}

	//---------------------------------------------------------------------------

	public String getSchema() 
	{ 
		return attrSet.getString("schema").trim(); 
	}

	//---------------------------------------------------------------------------

	public String getQualifiedName()
	{
		String schema = getSchema();
		String name   = attrSet.getString("name");

		return (schema.length() == 0) ? name : schema +"."+ name;
	}
	
	//---------------------------------------------------------------------------
	
	public boolean isGhost() { return attrSet.getBool("ghost"); }
}

//==============================================================================
