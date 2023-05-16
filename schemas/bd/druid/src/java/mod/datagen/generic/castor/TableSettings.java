//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class TableSettings extends AbstractSettings
{
	public static final String ACCESS_READONLY  = "read-only";
	public static final String ACCESS_SHARED    = "shared";		//--- default
	public static final String ACCESS_EXCLUSIVE = "exclusive";
	public static final String ACCESS_DBLOCKED  = "db-locked";

	public static final String KEYGEN_NONE      = "-";				//--- default
	public static final String KEYGEN_MAX       = "MAX";
	public static final String KEYGEN_HIGHLOW   = "HIGH-LOW";
	public static final String KEYGEN_UUID      = "UUID";
	public static final String KEYGEN_IDENTITY  = "IDENTITY";
	public static final String KEYGEN_SEQUENCE  = "SEQUENCE";

	public static final String CACHETYPE_NONE      = "none";
	public static final String CACHETYPE_COUNTLIM  = "count-limited";	//---default
	public static final String CACHETYPE_TIMELIM   = "time-limited";
	public static final String CACHETYPE_UNLIMITED = "unlimited";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String EXTENDS       = "ext";
	private static final String DEPENDS       = "dep";
	private static final String AUTO_COMPLETE = "aco";
	private static final String ACCESS        = "acc";
	private static final String KEY_GENERATOR = "kge";
	private static final String VERIFY_CONSTR = "vec";
	private static final String CACHE_TYPE    = "cat";
	private static final String CACHE_CAPACITY= "cac";

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public TableSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public int    getExtends()   { return mc.getValue(bm, EXTENDS,        0); }
	public int    getDepends()   { return mc.getValue(bm, DEPENDS,        0); }
	public String getAccess()    { return mc.getValue(bm, ACCESS,         ACCESS_SHARED); }
	public String getKeyGen()    { return mc.getValue(bm, KEY_GENERATOR,  KEYGEN_NONE); }
	public String getCacheType() { return mc.getValue(bm, CACHE_TYPE,     CACHETYPE_COUNTLIM); }
	public String getCacheCap()  { return mc.getValue(bm, CACHE_CAPACITY, ""); }

	public boolean isAutoComplete()   { return mc.getValue(bm, AUTO_COMPLETE, false); }
	public boolean isVerifyConstr()   { return mc.getValue(bm, VERIFY_CONSTR,  true); }

	//--- setters

	public void setExtends  (int s)    { mc.setValue(bm, EXTENDS,        s); }
	public void setDepends  (int s)    { mc.setValue(bm, DEPENDS,        s); }
	public void setAccess   (String s) { mc.setValue(bm, ACCESS,         s); }
	public void setKeyGen   (String s) { mc.setValue(bm, KEY_GENERATOR,  s); }
	public void setCacheType(String s) { mc.setValue(bm, CACHE_TYPE,     s); }
	public void setCacheCap (String s) { mc.setValue(bm, CACHE_CAPACITY, s); }

	public void setAutoComplete(boolean yesno) { mc.setValue(bm, AUTO_COMPLETE, yesno); }
	public void setVerifyConstr(boolean yesno) { mc.setValue(bm, VERIFY_CONSTR, yesno); }
}

//==============================================================================
