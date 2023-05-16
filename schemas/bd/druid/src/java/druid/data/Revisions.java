//==============================================================================
//===
//===   Revisions
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.tools.Util;

import druid.core.AttribList;
import druid.core.AttribSet;

//==============================================================================

public class Revisions extends AttribList
{
	//---------------------------------------------------------------------------

	public Revisions()
	{
		addAttrib("version",  "x.x");
		addAttrib("date",     "??-??-????");
		addAttrib("descr",    "...");
	}

	//---------------------------------------------------------------------------

	protected AttribList getNewInstance() { return new Revisions(); }

	//---------------------------------------------------------------------------

	public AttribSet append()
	{
		AttribSet as = super.append();
		as.setString("date", Util.getCurrentDate());
		return as;
	}
}

//==============================================================================
