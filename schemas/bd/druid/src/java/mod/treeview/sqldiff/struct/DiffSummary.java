//==============================================================================
//===
//===   DiffSummary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.struct;

import java.util.List;
import java.util.Vector;

//==============================================================================

public class DiffSummary
{
	public static final int DATABASE   =  0;
	public static final int PROCEDURE  =  1;
	public static final int FUNCTION   =  2;
	public static final int VIEW       =  3;
	public static final int TABLE      =  4;
	public static final int TRIGGER    =  5;
	public static final int FIELD      =  6;
	public static final int FIELDATTRIB=  7;
	public static final int TABLERULE  =  8;
	public static final int SEQUENCE   =  9;

	private static final int SIZE = SEQUENCE +1;

	//---------------------------------------------------------------------------

	private Vector vCatalogs = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DiffSummary()
	{
		for(int i=0; i<SIZE; i++)
			vCatalogs.add(new Vector());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void add(int catalog, DiffEntity e)
	{
		Vector vEntities = (Vector) vCatalogs.get(catalog);

		vEntities.add(e);
	}

	//---------------------------------------------------------------------------

	public DiffEntity get(int catalog, String name)
	{
		Vector vEntities = (Vector) vCatalogs.get(catalog);

		for(int i=0; i<vEntities.size(); i++)
		{
			DiffEntity ent = (DiffEntity) vEntities.get(i);

			if (ent.getName().equals(name))
				return ent;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public List list(int catalog)
	{
		Vector vEntities = (Vector) vCatalogs.get(catalog);

		Vector vNames = new Vector();

		for(int i=0; i<vEntities.size(); i++)
			vNames.add(vEntities.get(i));

		return vNames;
	}

	//---------------------------------------------------------------------------

	public boolean isEmpty()
	{
		for(int i=0; i<vCatalogs.size(); i++)
		{
			Vector v = (Vector) vCatalogs.get(i);

			if (!v.isEmpty())
				return false;
		}

		return true;
	}
}

//==============================================================================
