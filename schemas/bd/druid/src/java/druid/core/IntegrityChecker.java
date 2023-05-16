//==============================================================================
//===
//===   IntegrityChecker
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.Enumeration;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.Domain;
import druid.data.datatypes.VarAlias;

//==============================================================================

public class IntegrityChecker
{
	public static final int OK          = 0;
	public static final int ERROR_TABLE = 1;
	public static final int ERROR_FIELD = 2;
	public static final int ERROR_TYPE  = 3;

	//---------------------------------------------------------------------------

	private static final int MAX_DEPTH = 10;

	//---------------------------------------------------------------------------

	private DatabaseNode dbNode;
	private String       errMsg;
	private TableNode    errTable;
	private FieldNode    errField;
	private AbstractType errType;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public IntegrityChecker() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public int check(DatabaseNode dbNode)
	{
		this.dbNode = dbNode;

		errMsg   = null;
		errTable = null;
		errField = null;
		errType  = null;

		for(Enumeration e = dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode)e.nextElement();

			//------------------------------------------------------
			//--- table check

			if (node instanceof TableNode)
			{
				int res = checkTable((TableNode) node);

				if (res != OK)
					return res;
			}

			//------------------------------------------------------
			//--- field check

			else if (node instanceof FieldNode)
			{
				int res = checkField((FieldNode) node, 0);

				if (res != OK)
					return res;
			}
		}

		return OK;
	}

	//---------------------------------------------------------------------------

	public String       getError() { return errMsg;   }
	public TableNode    getTable() { return errTable; }
	public FieldNode    getField() { return errField; }
	public AbstractType getType()  { return errType;  }

	//---------------------------------------------------------------------------
	//---
	//--- Private checkIntegrity methods
	//---
	//---------------------------------------------------------------------------

	/** Integrity check for a table:
	  *  - the tablespace (if any) must exist
	  */

	private int checkTable(TableNode node)
	{
		return OK;
	}

	//---------------------------------------------------------------------------
	/** Integrity check for a field:
	  *  - the datatype (if any) must exist
	  *  - the reference table / field (if any) must exist
	  *  - the datatype of the referenced field (if any) must exist
	  */

	private int checkField(FieldNode node, int depth)
	{
		int typeId  = node.attrSet.getInt("type");
		int tableId = node.attrSet.getInt("refTable");
		int fieldId = node.attrSet.getInt("refField");

		//------------------------------------------------------------------------
		//--- check datatype

		if (typeId != 0)
		{
			AbstractType type = dbNode.dataTypes.getTypeFromId(typeId);

			//--- ? type cancelled

			if (type == null)
			{
				errMsg   = "Field's datatype has been deleted.";
				errField = node;

				return ERROR_FIELD;
			}

			if (!checkType(type))
			{
				errField = node;

				return ERROR_TYPE;
			}
		}

		//------------------------------------------------------------------------
		//--- check foreign key

		else if (tableId != 0)
		{
			TableNode table = dbNode.getTableByID(tableId);

			//--- ? table cancelled

			if (table == null)
			{
				errMsg   = "Reference table has been deleted.";
				errField = node;

				return ERROR_FIELD;
			}
			else
			{
				FieldNode field = table.getFieldByID(fieldId);

				//--- ? field cancelled

				if (field == null)
				{
					errMsg   = "Reference field has been deleted.";
					errField = node;

					return ERROR_FIELD;
				}
				else
				{
					int fType = field.attrSet.getInt("type");

					if (fType != 0)
					{
						AbstractType type = dbNode.dataTypes.getTypeFromId(fType);

						//--- ? type cancelled

						if (type == null)
						{
							errMsg   = "Type of referenced field has been deleted.";
							errField = node;

							return ERROR_FIELD;
						}

						if (!checkType(type))
						{
							errField = node;

							return ERROR_TYPE;
						}
					}
					else
					{
						//--- we must resolve 'field'

						if (depth == MAX_DEPTH)
						{
							errMsg   = "Circular reference found for field.";
							errField = node;

							return ERROR_FIELD;
						}
						else
							return checkField(field, depth +1);
					}
				}
			}
		}

		//------------------------------------------------------------------------
		//--- type not set

		else
		{
			errMsg   = "Field's datatype is not set.";
			errField = node;

			return ERROR_FIELD;
		}

		return OK;
	}

	//---------------------------------------------------------------------------

	private boolean checkType(AbstractType type)
	{
		boolean constType  = type instanceof ConstDataType;
		boolean constAlias = type instanceof ConstAlias;
		boolean varAlias   = type instanceof VarAlias;

		//------------------------------------------------------------------------

		if (varAlias)
		{
			if (type.attrSet.getString("size").trim().equals(""))
			{
				errMsg  = "The field's datatype has an empty size";
				errType = type;

				return false;
			}
		}

		//------------------------------------------------------------------------

		if (constType || constAlias || varAlias)
		{
			Domain domain = type.domain;

			if (domain.attrSet.getString("type").equals(Domain.RANGE))
			{
				String min = domain.attrSet.getString("minValue");
				String max = domain.attrSet.getString("maxValue");

				if (!min.equals("") && !testValue(min))
				{
					errMsg  = "The field's datatype has a domain with an invalid min value";
					errType = type;

					return false;
				}

				if (!max.equals("") && !testValue(max))
				{
					errMsg  = "The field's datatype has a domain with an invalid max value";
					errType = type;

					return false;
				}
			}

			else if (domain.attrSet.getString("type").equals(Domain.SET))
			{
				if (domain.valueList.size() == 0)
				{
					errMsg  = "The field's datatype has a domain with an empty set";
					errType = type;

					return false;
				}
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------

	private boolean testValue(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
}

//==============================================================================
