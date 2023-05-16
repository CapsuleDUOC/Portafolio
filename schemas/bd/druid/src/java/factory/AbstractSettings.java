//==============================================================================
//===
//===   AbstractSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;

//==============================================================================

public class AbstractSettings
{
	//--------------------------------------------------------------------------

	protected ModulesConfig mc;
	protected BasicModule   bm;

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public AbstractSettings(ModulesConfig mc, BasicModule bm)
	{
		this.mc = mc;
		this.bm = bm;
	}
}

//==============================================================================
