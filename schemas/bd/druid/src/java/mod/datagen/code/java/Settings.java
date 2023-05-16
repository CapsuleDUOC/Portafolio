//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.java;

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

	private static final String PACKAGE              = "package";
	private static final String EXTENDS              = "extends";
	private static final String NAME_PREFIX          = "namePrefix";
	private static final String NAME_SUFFIX          = "nameSuffix";
	private static final String GEN_CONSTS           = "genConsts";
	private static final String GEN_NAMES            = "genNames";
	private static final String USE_JDO              = "useJdo";

	private static final String ACCESS               = "access";
	private static final String GETTER               = "getter";
	private static final String SETTER               = "setter";
	private static final String DIRTY_CHECK          = "dirtyCheck";
	private static final String FOREIGN_KEY_ARRAY    = "foreignKeyArray";
	private static final String FOREIGN_AS_CLASS_REF = "foreignKeyAsClassRef";
	private static final String SERIALIZABLE         = "serializable";
	private static final String JDO_FILE             = "jdoFile";
	private static final String CAMEL_CASE           = "camelCase";

	//--------------------------------------------------------------------------

	public static final String ACCESS_PUBLIC    = "pub";
	public static final String ACCESS_PROTECTED = "pro";
	public static final String ACCESS_PRIVATE   = "pri";
	public static final String ACCESS_PACKAGE   = "pac";

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

	public String  getPackage()    { return mc.getValue(bm, PACKAGE,     ""); }
	public String  getExtends()    { return mc.getValue(bm, EXTENDS,     ""); }
	public String  getNamePrefix() { return mc.getValue(bm, NAME_PREFIX, ""); }
	public String  getNameSuffix() { return mc.getValue(bm, NAME_SUFFIX, ""); }

	public boolean isGenConsts()   { return mc.getValue(bm, GEN_CONSTS,  true); }
	public boolean isGenNames()    { return mc.getValue(bm, GEN_NAMES,   true); }
	public boolean isUsingJDO()    { return mc.getValue(bm, USE_JDO,    false); }

	public String getAccess() { return mc.getValue(bm, ACCESS, ACCESS_PUBLIC); }

	public boolean isGetter()       { return mc.getValue(bm, GETTER,               false); }
	public boolean isSetter()       { return mc.getValue(bm, SETTER,               false); }
	public boolean isDirtyCheck()   { return mc.getValue(bm, DIRTY_CHECK,          false); }
	public boolean isGenFKArray()   { return mc.getValue(bm, FOREIGN_KEY_ARRAY,    false); }
	public boolean isFKClassRef()   { return mc.getValue(bm, FOREIGN_AS_CLASS_REF, false); }
	public boolean isSerializable() { return mc.getValue(bm, SERIALIZABLE,         false); }
	public boolean isJdoFile()      { return mc.getValue(bm, JDO_FILE,             false); }
	public boolean isCamelCase()    { return mc.getValue(bm, CAMEL_CASE,           false); }

	//--------------------------------------------------------------------------

	public void setPackage   (String value) { mc.setValue(bm, PACKAGE,     value); }
	public void setExtends   (String value) { mc.setValue(bm, EXTENDS,     value); }
	public void setNamePrefix(String value) { mc.setValue(bm, NAME_PREFIX, value); }
	public void setNameSuffix(String value) { mc.setValue(bm, NAME_SUFFIX, value); }

	public void setGenConsts(boolean value) { mc.setValue(bm, GEN_CONSTS, value); }
	public void setGenNames (boolean value) { mc.setValue(bm, GEN_NAMES,  value); }
	public void setUsingJDO (boolean value) { mc.setValue(bm, USE_JDO,    value); }

	public void setAccess(String value) { mc.setValue(bm, ACCESS, value); }

	public void setGetter      (boolean value) { mc.setValue(bm, GETTER,               value); }
	public void setSetter      (boolean value) { mc.setValue(bm, SETTER,               value); }
	public void setDirtyCheck  (boolean value) { mc.setValue(bm, DIRTY_CHECK,          value); }
	public void setGenFKArray  (boolean value) { mc.setValue(bm, FOREIGN_KEY_ARRAY,    value); }
	public void setFKClassRef  (boolean value) { mc.setValue(bm, FOREIGN_AS_CLASS_REF, value); }
	public void setSerializable(boolean value) { mc.setValue(bm, SERIALIZABLE,         value); }
	public void setJdoFile     (boolean value) { mc.setValue(bm, JDO_FILE,             value); }
	public void setCamelCase   (boolean value) { mc.setValue(bm, CAMEL_CASE,           value); }
}

//==============================================================================
