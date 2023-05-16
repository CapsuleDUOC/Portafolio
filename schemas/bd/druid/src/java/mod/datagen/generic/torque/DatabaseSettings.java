//==============================================================================
//===
//===   DatabaseSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class DatabaseSettings extends AbstractSettings
{
	public static final String IDMETHOD_NONE     = "none";			//--- default
	public static final String IDMETHOD_IDBROKER = "idbroker";
	public static final String IDMETHOD_NATIVE   = "native";

	public static final String JAVATYPE_PRIMITIVE = "primitive";	//--- default
	public static final String JAVATYPE_OBJECT    = "object";

	public static final String JAVANAM_NOCHANGE   = "nochange";
	public static final String JAVANAM_UNDERSCORE = "underscore";	//--- default
	public static final String JAVANAM_JAVANAME   = "javaname";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String DEFAULT_ID_METHOD   = "defIdMeth";
	private static final String DEFAULT_JAVA_TYPE   = "defJavaType";
	private static final String PACKAGE             = "package";
	private static final String BASE_CLASS          = "baseClass";
	private static final String BASE_PEER           = "basePeer";
	private static final String DEFAULT_JAVA_NAMING = "defJavaNam";
	private static final String HEAVY_INDEXING      = "heavyIndex";

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

	public String getDefaultIdMethod()   { return mc.getValue(bm, DEFAULT_ID_METHOD,   IDMETHOD_NONE);      }
	public String getDefaultJavaType()   { return mc.getValue(bm, DEFAULT_JAVA_TYPE,   JAVATYPE_PRIMITIVE); }
	public String getDefaultJavaNaming() { return mc.getValue(bm, DEFAULT_JAVA_NAMING, JAVANAM_UNDERSCORE); }
	public String getPackage()           { return mc.getValue(bm, PACKAGE,             ""); }
	public String getBaseClass()         { return mc.getValue(bm, BASE_CLASS,          ""); }
	public String getBasePeer()          { return mc.getValue(bm, BASE_PEER,           ""); }

	public boolean isHeavyIndexing()     { return mc.getValue(bm, HEAVY_INDEXING, false); }

	//--- setters

	public void setDefaultIdMethod(String s)   { mc.setValue(bm, DEFAULT_ID_METHOD,   s); }
	public void setDefaultJavaType(String s)   { mc.setValue(bm, DEFAULT_JAVA_TYPE,   s); }
	public void setDefaultJavaNaming(String s) { mc.setValue(bm, DEFAULT_JAVA_NAMING, s); }
	public void setPackage(String s)           { mc.setValue(bm, PACKAGE,             s); }
	public void setBaseClass(String s)         { mc.setValue(bm, BASE_CLASS,          s); }
	public void setBasePeer(String s)          { mc.setValue(bm, BASE_PEER,           s); }

	public void setHeavyIndexing(boolean yesno){ mc.setValue(bm, HEAVY_INDEXING, yesno); }
}

//==============================================================================
