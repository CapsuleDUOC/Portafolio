//==============================================================================
//===
//===   DatabaseSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.Serials;
import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.sql.BasicDatabaseSettings;

//==============================================================================

public class DatabaseSettings extends BasicDatabaseSettings
{
	//---------------------------------------------------------------------------
	//---
	//--- Defaults
	//---
	//---------------------------------------------------------------------------

	private static AttribList defTablespaces = new AttribList();

	//---------------------------------------------------------------------------

	static
	{
		defTablespaces.addAttrib("id",   Serials.get());
		defTablespaces.addAttrib("name", "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String TABLESPACES = "tablespaces";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public DatabaseSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public AttribList getTablespaces() { return mc.getAttribList(bm, TABLESPACES, defTablespaces); }

	//--------------------------------------------------------------------------

	public String getTablespaceName(int id)
	{
		AttribList tabs = getTablespaces();

		for(int i=0; i<tabs.size(); i++)
		{
			AttribSet as = tabs.get(i);

			if (as.getInt("id") == id)
				return as.getString("name");
		}

		return null;
	}
}

//==============================================================================
