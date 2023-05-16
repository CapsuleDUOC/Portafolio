//==============================================================================
//===
//===   SequentialGenerator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.algorithm;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;

//==============================================================================

public class SequentialGenerator extends AbstractGenerator
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SequentialGenerator(Logger l, SqlGenModule mod, DatabaseNode node)
	{
		super(l, mod, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String generate()
	{
		//--- collect objects

		Vector objects = new Vector();

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (node instanceof TableNode    || node instanceof ViewNode || node instanceof ProcedureNode ||
				 node instanceof FunctionNode || node instanceof SequenceNode)
				objects.add(node);
		}

		return generate(objects, new Hashtable());
	}
}

//==============================================================================
