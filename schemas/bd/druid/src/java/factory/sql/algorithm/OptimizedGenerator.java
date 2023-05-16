//==============================================================================
//===
//===   OptimizedGenerator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.algorithm;

import java.util.Vector;
import java.util.Hashtable;

import druid.core.DataLib;
import druid.data.DatabaseNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;

//==============================================================================

public class OptimizedGenerator extends AbstractGenerator
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptimizedGenerator(Logger l, SqlGenModule mod, DatabaseNode node)
	{
		super(l,mod,node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String generate()
	{
		//------------------------------------------------------------------------
		//--- order tables depending on their dependances

		Vector srcTables = dbNode.getObjects(TableNode.class);
		Vector desTables = DataLib.getOrderedTables(srcTables);

		Hashtable unresolved = new Hashtable();

		if (srcTables.size() != 0)
		{
			for(int i=0; i<srcTables.size(); i++)
			{
				TableNode t = (TableNode)srcTables.elementAt(i);
				String name = t.attrSet.getString("name");

				unresolved.put(name, t);

				logger.log(Logger.INFO, "Postponing fkeys for table : " + name);
			}

			desTables.addAll(srcTables);
		}

		//---------------------------------------------------------------------
		//--- collect all data

		Vector sequen = dbNode.getObjects(SequenceNode.class);
		Vector func   = dbNode.getObjects(FunctionNode.class);
		Vector proc   = dbNode.getObjects(ProcedureNode.class);
		Vector views  = dbNode.getObjects(ViewNode.class);

		//--- the following block decide the generation order

		Vector objects = new Vector();

		objects.addAll(sequen);
		objects.addAll(desTables);
		objects.addAll(views);
		objects.addAll(func);
		objects.addAll(proc);

		return generate(objects, unresolved);
	}
}

//==============================================================================
