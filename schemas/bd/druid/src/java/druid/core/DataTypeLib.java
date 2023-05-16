//==============================================================================
//===
//===   DataTypeLib
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.Vector;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.ConstFolder;
import druid.data.datatypes.DataTypes;
import druid.data.datatypes.Domain;
import druid.data.datatypes.TypeInfo;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.datatypes.VarFolder;

//==============================================================================

public class DataTypeLib
{
	private static final int MAX_DEPTH = 10;

	//---------------------------------------------------------------------------

	/** This method is used to show a field type. It returns a string like
	  * "basic-type" or like "table(field)" in case of fkey
	  */

	public static String getTypeDef(FieldNode f)
	{
		DatabaseNode dbNode = f.getDatabase();

		int typeId  = f.attrSet.getInt("type");
		int tableId = f.attrSet.getInt("refTable");
		int fieldId = f.attrSet.getInt("refField");

		if (typeId != 0)
		{
			AbstractType type = dbNode.dataTypes.getTypeFromId(typeId);

			//--- ? type cancelled ---
			if (type == null)  return "<TYPE DEL>";
				else            return dbNode.dataTypes.getTypeInfo(type).name;
		}
		else if (tableId != 0)
		{
			TableNode table = dbNode.getTableByID(tableId);

			//--- ? table cancelled ---
			if (table == null)  return "<TABLE DEL>";
			else
			{
				FieldNode field = table.getFieldByID(fieldId);

				//--- ? field cancelled ---
				if (field == null)  return "<FIELD DEL>";
				else
				{
					String tName = table.attrSet.getString("name");
					String fName = field.attrSet.getString("name");

					return tName + "(" + fName + ")";
				}
			}
		}
		else
			return "<NOT SET>";
	}

	//---------------------------------------------------------------------------

	/** This method is used to obtain the sql datatype of a field given its
	  * datatype (used during sql and html gneration)
	  */

	public static String getSqlType(FieldNode f)
	{
		return getSqlType(f, 0);
	}

	//---------------------------------------------------------------------------

	private static String getSqlType(FieldNode f, int depth)
	{
		if (depth == MAX_DEPTH) return "<LOOP>";

		DatabaseNode dbNode = f.getDatabase();

		int typeId  = f.attrSet.getInt("type");
		int tableId = f.attrSet.getInt("refTable");
		int fieldId = f.attrSet.getInt("refField");

		if (typeId != 0)
		{
			AbstractType type = dbNode.dataTypes.getTypeFromId(typeId);

			//--- ? type cancelled ---
			if (type == null)
				return "<TYPE DEL>";
			else
			{
				TypeInfo ti = dbNode.dataTypes.getTypeInfo(type);

				return ti.getSqlType();
			}
		}
		else if (tableId != 0)
		{
			TableNode table = dbNode.getTableByID(tableId);

			//--- ? table cancelled ---
			if (table == null)
				return "<TABLE DEL>";
			else
			{
				FieldNode field = table.getFieldByID(fieldId);

				//--- ? field cancelled ---
				if (field == null)  return "<FIELD DEL>";
					else             return getSqlType(field, depth +1);
			}
		}
		else
			return "<NOT SET>";
	}

	//---------------------------------------------------------------------------

	/** This method is used to obtain the datatype of a field, resolving a fkey
	  * if present. The datatype returned is in TypeInfo form
	  */

	public static TypeInfo getTypeInfo(FieldNode f)
	{
		return getTypeInfo(f, 0);
	}

	//---------------------------------------------------------------------------

	private static TypeInfo getTypeInfo(FieldNode f, int depth)
	{
		if (depth == MAX_DEPTH) return null;

		DatabaseNode dbNode = f.getDatabase();

		int typeId  = f.attrSet.getInt("type");
		int tableId = f.attrSet.getInt("refTable");
		int fieldId = f.attrSet.getInt("refField");

		if (typeId != 0)
		{
			AbstractType type = dbNode.dataTypes.getTypeFromId(typeId);

			//--- ? type cancelled ---
			if (type == null)  return null;
				else            return dbNode.dataTypes.getTypeInfo(type);
		}
		else if (tableId != 0)
		{
			TableNode table = dbNode.getTableByID(tableId);

			//--- ? table cancelled ---
			if (table == null)  return null;
			else
			{
				FieldNode field = table.getFieldByID(fieldId);

				//--- ? field cancelled ---
				if (field == null)  return null;
					else             return getTypeInfo(field, depth +1);
			}
		}
		else
			return null;
	}

	//---------------------------------------------------------------------------

	/** Returns the sql check string for a field (like 'field < 100').
	  * @param dbNode the node of the database the field belongs to
	  * @param fieldNode the type of the field MUST be valid (that is != 0)
	  */

	public static String getTypeCheckString(FieldNode fieldNode)
	{
		DatabaseNode dbNode = fieldNode.getDatabase();

		int    typeId = fieldNode.attrSet.getInt("type");
		String fName  = fieldNode.attrSet.getString("name");

		AbstractType aType = dbNode.dataTypes.getTypeFromId(typeId);

		if (aType == null)
			throw new DruidException(DruidException.ILL_ARG,
											 "Field's datatype not found in the db", typeId);

		return getTypeCheckString(fName, aType);
	}

	//---------------------------------------------------------------------------

	/** Returns the sql check string for a field (like 'field < 100').
	  * @param dbNode the node of the database the field belongs to
	  * @param fName is the field name to use to build the string (like 'field')
	  * @param aType abstractType from which to get the type
	  */

	public static String getTypeCheckString(String fName,
														 AbstractType aType)
	{
		AttribSet  as = aType.domain.attrSet;
		AttribList al = aType.domain.valueList;

		String  type   = as.getString("type");
		String  minVal = as.getString("minValue").trim();
		String  maxVal = as.getString("maxValue").trim();
		boolean outR   = as.getBool("outRange");

		//------------------------------------------------------------------------
		//--- none

		if (type.equals(Domain.NONE))
			return null;

		//------------------------------------------------------------------------
		//--- lower

		if (type.equals(Domain.LOWER))
			return fName + " = LOWER(" + fName + ")";

		//------------------------------------------------------------------------
		//--- upper

		if (type.equals(Domain.UPPER))
			return fName + " = UPPER(" + fName + ")";

		//------------------------------------------------------------------------
		//--- range

		if (type.equals(Domain.RANGE))
		{
			//--- only max val
			if (minVal.equals(""))
				return fName + " <= " + maxVal;

			//--- only min val
			if (maxVal.equals(""))
				return fName + " >= " + minVal;

			//--- both min and max vals

			if (!outR)
				return fName + " BETWEEN " + minVal + " AND " + maxVal;
			else
				return fName + " NOT BETWEEN " + minVal + " AND " + maxVal;
		}

		//------------------------------------------------------------------------
		//--- set

		if (type.equals(Domain.SET))
		{
			StringBuffer s = new StringBuffer();

			for (int i = 0; i < al.size(); i++)
			{
				s.append("'").append(al.get(i).getString("value")).append("'");

				if (i != al.size() - 1)
					s.append(", ");
			}

			return fName + " IN (" + s.append(")").toString();
		}

		//------------------------------------------------------------------------
		//--- we must not arrive to this point

		throw new DruidException(DruidException.INC_STR,
										 "Domain type unknown", type);
	}

	//---------------------------------------------------------------------------
	//---
	//--- DataType remapping
	//---
	//---------------------------------------------------------------------------

	/** Given a parent (ConstFolder, ConstDataType, VarFolder, VarDataType, VarAlias)
	  * remaps child. That is returns an abstract-type which is child remapped into
	  * the new parent. If child doesn't need remapping it is returned as is.
	  */

	public static AbstractType remapDataType(TreeViewNode parent, AbstractType child)
	{
		if (parent instanceof ConstFolder)
		{
			if (child instanceof ConstDataType)
				return child;

			AbstractType node = new ConstDataType();

			node.domain = child.domain.duplicate();

			//--- ConstAlias --> ConstDataType

			if (child instanceof ConstAlias)
				node.attrSet = child.attrSet.duplicate();

			//--- VarDataType --> ConstDataType

			else if (child instanceof VarDataType)
				node.attrSet.setString("name", child.attrSet.getString("name"));

			//--- VarAlias --> ConstDataType

			else if (child instanceof VarAlias)
			{
				node.attrSet = child.attrSet.duplicate();
				node.attrSet.removeAttrib("size");
			}

			else
				throw new DruidException(DruidException.INC_STR, "Unknown node", parent);

			node.setText(node.attrSet.getString("name"));

			return node;
		}

		//------------------------------------------------------------------------

		else if (parent instanceof VarFolder)
		{
			if (child instanceof VarDataType)
				return child;

			AbstractType node = new VarDataType();

			node.domain = child.domain.duplicate();
			node.attrSet.setString("name", child.attrSet.getString("name"));
			node.setText(node.attrSet.getString("name"));

			return node;
		}

		//------------------------------------------------------------------------

		else if (parent instanceof ConstDataType)
		{
			if (child instanceof ConstAlias)
				return child;

			AbstractType node = new ConstAlias();

			node.domain = child.domain.duplicate();

			//--- ConstDataType --> ConstAlias

			if (child instanceof ConstDataType)
				node.attrSet = child.attrSet.duplicate();

			//--- VarDataType --> ConstAlias

			else if (child instanceof VarDataType)
				node.attrSet.setString("name", child.attrSet.getString("name"));

			//--- VarAlias --> ConstAlias

			else if (child instanceof VarAlias)
			{
				node.attrSet = child.attrSet.duplicate();
				node.attrSet.removeAttrib("size");
			}
			else
				throw new DruidException(DruidException.INC_STR, "Unknown node", parent);

			node.setText(node.attrSet.getString("name"));

			return node;
		}

		//------------------------------------------------------------------------

		else if (parent instanceof VarDataType)
		{
			if (child instanceof VarAlias)
				return child;

			AbstractType node = new VarAlias();

			node.domain = child.domain.duplicate();

			//--- VarDataType --> VarAlias

			if (child instanceof VarDataType)
				node.attrSet.setString("name", child.attrSet.getString("name"));

			//--- ConstDataType/ConstAlias --> VarAlias

			else if (child instanceof ConstDataType || child instanceof ConstAlias)
			{
				node.attrSet = child.attrSet.duplicate();
				node.attrSet.addAttrib("size", "");
			}

			else
				throw new DruidException(DruidException.INC_STR, "Unknown node", parent);

			node.setText(node.attrSet.getString("name"));

			return node;
		}

		//------------------------------------------------------------------------

		else
			throw new DruidException(DruidException.INC_STR, "Unknown node", parent);
	}

	//---------------------------------------------------------------------------

	public static int mergeDataType(DatabaseNode dbNode, int oldId, int newId)
	{
		Vector vFields = dbNode.getObjects(FieldNode.class);

		int counter = 0;

		for(int i=0; i<vFields.size(); i++)
		{
			FieldNode node = (FieldNode) vFields.elementAt(i);

			if (node.attrSet.getInt("type") == oldId)
			{
				node.attrSet.setInt("type", newId);
				counter++;
			}
		}

		return counter;
	}

	//---------------------------------------------------------------------------

	/** Return the number of fields that use the given datatype
	  */

	public static Vector usage(DatabaseNode dbNode, int typeId)
	{
		Vector vFields = dbNode.getObjects(FieldNode.class);
		Vector vResult = new Vector();

		for(int i=0; i<vFields.size(); i++)
		{
			FieldNode node = (FieldNode) vFields.elementAt(i);

			if (node.attrSet.getInt("type") == typeId)
				vResult.addElement(node);
		}

		return vResult;
	}

	//---------------------------------------------------------------------------

	public static int migrateDataType(AbstractType type, DataTypes desDT)
	{
		String name = type.attrSet.getString("name");

		AbstractType parentType = (AbstractType) type.getParent();
		String       parentName = parentType.attrSet.getString("name");

		//------------------------------------------------------------------------
		//--- constant datatype

		if (type instanceof ConstDataType)
		{
			AbstractType at = getChildByName(name, (AbstractType) desDT.getChild(0));

			if (at != null)
				return at.attrSet.getInt("id");
			else
			{
				//--- create type : the following block is repeated some times
				//--- (instead of putting it at the beginning of the method) to avoid
				//--- a waste of serial numbers

				AbstractType copy = (AbstractType) type.duplicate();
				copy.removeAllChildren();

				desDT.getChild(0).addChild(copy, false);

				return copy.attrSet.remapId();
			}
		}

		//------------------------------------------------------------------------
		//--- constant alias

		else if (type instanceof ConstAlias)
		{
			TreeViewNode constDTs = desDT.getChild(0);

			AbstractType parentCopy = null;

			for(int i=0; i<constDTs.getChildCount(); i++)
			{
				AbstractType currParent = (AbstractType) constDTs.getChild(i);
				String       currName   = currParent.attrSet.getString("name");

				if (parentName.equals(currName))
				{
					parentCopy = currParent;

					AbstractType at = getChildByName(name, currParent);

					if (at != null)
						return at.attrSet.getInt("id");
				}
			}

			//--- create type if not found

			if (parentCopy == null)
			{
				parentCopy = (AbstractType) parentType.duplicate();
				parentCopy.removeAllChildren();
				parentCopy.attrSet.remapId();

				constDTs.addChild(parentCopy, false);
			}

			AbstractType copy = (AbstractType) type.duplicate();

			parentCopy.addChild(copy, false);

			return copy.attrSet.remapId();
		}

		//------------------------------------------------------------------------
		//--- variable alias

		else if (type instanceof VarAlias)
		{
			TreeViewNode varDTs = desDT.getChild(1);

			AbstractType parentCopy = null;

			for(int i=0; i<varDTs.getChildCount(); i++)
			{
				AbstractType currParent = (AbstractType) varDTs.getChild(i);
				String       currName   = currParent.attrSet.getString("name");

				if (parentName.equals(currName))
				{
					parentCopy = currParent;

					AbstractType at = getChildByName(name, currParent);

					if (at != null)
						if (at.attrSet.getString("size").equals(type.attrSet.getString("size")))
							return at.attrSet.getInt("id");
				}
			}

			//--- create type if not found

			if (parentCopy == null)
			{
				parentCopy = (AbstractType) parentType.duplicate();
				parentCopy.removeAllChildren();

				varDTs.addChild(parentCopy, false);
			}

			AbstractType copy = (AbstractType) type.duplicate();

			parentCopy.addChild(copy, false);

			return copy.attrSet.remapId();
		}

		else
			throw new DruidException(DruidException.INC_STR, "Unknown datatype", type);
	}

	//---------------------------------------------------------------------------

	private static AbstractType getChildByName(String name, AbstractType type)
	{
		for(int i=0; i<type.getChildCount(); i++)
		{
			AbstractType at = (AbstractType) type.getChild(i);

			if (at.attrSet.getString("name").equals(name))
				return at;
		}

		return null;
	}
}

//==============================================================================
