//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.html;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class Settings extends AbstractSettings
{
	private static final String LANGUAGE   = "language";
	private static final String THUMBNAILS = "thumbnails";
	private static final String SKIN       = "skin";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public Settings(ModulesConfig mc, BasicModule bm)
	{
		super(mc, bm);
	}

	//--------------------------------------------------------------------------
	//---
	//--- General settings API
	//---
	//--------------------------------------------------------------------------

	public String getLanguage()   { return mc.getValue(bm, LANGUAGE,          "en"); }
	public String getSkin()       { return mc.getValue(bm, SKIN,         "Classic"); }
	public int    getThumbnails() { return mc.getValue(bm, THUMBNAILS,         128); }

	public void setLanguage(String value)  { mc.setValue(bm, LANGUAGE,   value); }
	public void setSkin(String value)      { mc.setValue(bm, SKIN,       value); }
	public void setThumbnails(int value)   { mc.setValue(bm, THUMBNAILS, value); }
}

//==============================================================================
