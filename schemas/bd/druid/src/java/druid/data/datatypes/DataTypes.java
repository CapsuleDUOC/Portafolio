//==============================================================================
//===
//===   DataTypes
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.datatypes;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.AttribSet;

//==============================================================================

public class DataTypes extends AbstractType
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DataTypes()
	{
		setText("Datatypes");

		add(new ConstFolder());
		add(new VarFolder());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	/** Given a datatype's id, returns its node. The method scans all datatypes
	  * searching for the datatype which id is equal to the given param.
	  * @param id id of the datatype to look for
	  * @return the datatype's node if it is found, otherwise null
	  */

	public AbstractType getTypeFromId(int id)
	{
		for(Enumeration e = preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractType node = (AbstractType)e.nextElement();
			AttribSet as = node.attrSet;

			if (as.contains("id"))
				if (as.getInt("id") == id)
					return node;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public TypeInfo getTypeInfo(AbstractType absType)
	{
		TypeInfo  tinfo = new TypeInfo();
		AttribSet as    = absType.attrSet;

		tinfo.name = as.getString("name");

		if (absType instanceof ConstAlias)
		{
			tinfo.id         = as.getInt("id");
			tinfo.basicType  = ((AbstractType)absType.getParent()).attrSet.getString("name");
			tinfo.ddEquiv    = as.getString("ddEquiv");
		}
		else
		if (absType instanceof VarAlias)
		{
			tinfo.id        = as.getInt("id");
			tinfo.basicType = ((AbstractType)absType.getParent()).attrSet.getString("name");
			tinfo.ddEquiv   = as.getString("ddEquiv");
			tinfo.size      = as.getString("size");
		}
		else
		if (absType instanceof ConstDataType)
		{
			tinfo.id        = as.getInt("id");
			tinfo.basicType = as.getString("name");
			tinfo.ddEquiv   = as.getString("ddEquiv");
		}

		return tinfo;
	}

	//---------------------------------------------------------------------------

	public List getConcreteTypes()
	{
		Vector v = new Vector();

		for(Enumeration e = preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractType node = (AbstractType) e.nextElement();

			if (node instanceof ConstDataType || node instanceof ConstAlias || node instanceof VarAlias)
				v.addElement(node);
		}

		return v;
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance()
	{
		TreeViewNode node = new DataTypes();

		node.remove(0);
		node.remove(0);

		return node;
	}
}

//==============================================================================
