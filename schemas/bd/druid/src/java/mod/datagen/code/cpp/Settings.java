//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.cpp;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class Settings extends AbstractSettings
{
	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String PRELINE = "preLine";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public Settings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public String getPreLine() { return mc.getValue(bm, PRELINE, ""); }

	public void setPreLine(String value) { mc.setValue(bm, PRELINE, value); }
}

//==============================================================================
