//==============================================================================
//===
//===   ModulesUsage
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import java.util.Enumeration;
import java.util.Hashtable;

import druid.core.modules.ModuleManager;
import druid.interfaces.BasicModule;

//==============================================================================

public class ModulesUsage
{
	//--- made public only for conversion from old format
	public Hashtable htModules = new Hashtable(30, 0.75f);

	//---------------------------------------------------------------------------

	public ModulesUsage() {}

	//---------------------------------------------------------------------------

	public boolean contains(BasicModule mod)
	{
		String fullName = ModuleManager.getAbsoluteID(mod);

		return htModules.containsKey(fullName);
	}

	//---------------------------------------------------------------------------

	public void addModule(BasicModule mod)
	{
		String fullName = ModuleManager.getAbsoluteID(mod);

		htModules.put(fullName, "???");
	}

	//---------------------------------------------------------------------------

	public void removeModule(BasicModule mod)
	{
		String fullName = ModuleManager.getAbsoluteID(mod);

		htModules.remove(fullName);
	}

	//---------------------------------------------------------------------------

	public ModulesUsage duplicate()
	{
		ModulesUsage mu = new ModulesUsage();

		for(Enumeration e = htModules.keys(); e.hasMoreElements();)
		{
			String name  = (String) e.nextElement();
			String value = (String) htModules.get(name);

			mu.htModules.put(name, value);
		}

		return mu;
	}
}

//==============================================================================
