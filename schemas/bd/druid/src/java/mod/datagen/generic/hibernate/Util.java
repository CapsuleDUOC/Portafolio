//==============================================================================
//===
//===   Util
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

public class Util
{
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static TComboBoxGuardian createYesNoDefGuardian(String method)
	{
		TComboBoxGuardian g = new TComboBoxGuardian(method);

		g.addItem(Consts.YES,     "Yes");
		g.addItem(Consts.NO,      "No");
		g.addItem(Consts.DEFAULT, "Global setting");

		return g;
	}
}

//==============================================================================
