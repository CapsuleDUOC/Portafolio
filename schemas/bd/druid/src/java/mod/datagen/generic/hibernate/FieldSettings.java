//==============================================================================
//===
//===   FieldSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class FieldSettings extends AbstractSettings
{
	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String PROP_NAME = "propName";
	private static final String TYPE      = "type";
	private static final String ACCESS    = "acc";
	private static final String INSERT    = "ins";
	private static final String UPDATE    = "upd";
	private static final String FORMULA   = "form";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public FieldSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public String getPropertyName() { return mc.getValue(bm, PROP_NAME, ""); }
	public String getType()         { return mc.getValue(bm, TYPE,      ""); }
	public String getAccess()       { return mc.getValue(bm, ACCESS,    ""); }
	public String getFormula()      { return mc.getValue(bm, FORMULA,   ""); }
	public String getInsert()       { return mc.getValue(bm, INSERT,    Consts.DEFAULT); }
	public String getUpdate()       { return mc.getValue(bm, UPDATE,    Consts.DEFAULT); }

	//--- setters

	public void setPropertyName(String s) { mc.setValue(bm, PROP_NAME, s); }
	public void setType        (String s) { mc.setValue(bm, TYPE,      s); }
	public void setAccess      (String s) { mc.setValue(bm, ACCESS,    s); }
	public void setFormula     (String s) { mc.setValue(bm, FORMULA,   s); }
	public void setInsert      (String s) { mc.setValue(bm, INSERT,    s); }
	public void setUpdate      (String s) { mc.setValue(bm, UPDATE,    s); }
}

//==============================================================================
