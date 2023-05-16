//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class TableSettings extends AbstractSettings
{
	public static final String IDMETHOD_NULL     = "null";			//--- default
	public static final String IDMETHOD_NONE     = "none";
	public static final String IDMETHOD_IDBROKER = "idbroker";
	public static final String IDMETHOD_NATIVE   = "native";

	public static final String JAVANAM_NOCHANGE   = "nochange";		//--- default
	public static final String JAVANAM_UNDERSCORE = "underscore";
	public static final String JAVANAM_JAVANAME   = "javaname";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String JAVA_NAME      = "javaName";
	private static final String ID_METHOD      = "idMethod";
	private static final String BASE_CLASS     = "baseClass";
	private static final String BASE_PEER      = "basePeer";
	private static final String ALIAS          = "alias";
	private static final String INTERFACE      = "interf";
	private static final String JAVA_NAMING    = "javaNam";
	private static final String SKIP_SQL       = "skipSql";
	private static final String ABSTRACT       = "abstract";
	private static final String HEAVY_INDEXING = "heavyIndex";

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

	public String getJavaName()   { return mc.getValue(bm, JAVA_NAME,   ""); }
	public String getIdMethod()   { return mc.getValue(bm, ID_METHOD,   IDMETHOD_NULL); }
	public String getBaseClass()  { return mc.getValue(bm, BASE_CLASS,  ""); }
	public String getBasePeer()   { return mc.getValue(bm, BASE_PEER,   ""); }
	public String getAlias()      { return mc.getValue(bm, ALIAS,       ""); }
	public String getInterface()  { return mc.getValue(bm, INTERFACE,   ""); }
	public String getJavaNaming() { return mc.getValue(bm, JAVA_NAMING, JAVANAM_NOCHANGE); }

	public boolean isSkippingSql()   { return mc.getValue(bm, SKIP_SQL,       false); }
	public boolean isAbstract()      { return mc.getValue(bm, ABSTRACT,       false); }
	public boolean isHeavyIndexing() { return mc.getValue(bm, HEAVY_INDEXING, false); }

	//--- setters

	public void setJavaName(String s)   { mc.setValue(bm, JAVA_NAME,   s); }
	public void setIdMethod(String s)   { mc.setValue(bm, ID_METHOD,   s); }
	public void setBaseClass(String s)  { mc.setValue(bm, BASE_CLASS,  s); }
	public void setBasePeer(String s)   { mc.setValue(bm, BASE_PEER,   s); }
	public void setAlias(String s)      { mc.setValue(bm, ALIAS,       s); }
	public void setInterface(String s)  { mc.setValue(bm, INTERFACE,   s); }
	public void setJavaNaming(String s) { mc.setValue(bm, JAVA_NAMING, s); }

	public void setSkippingSql(boolean yesno)   { mc.setValue(bm, SKIP_SQL,       yesno); }
	public void setAbstract(boolean yesno)      { mc.setValue(bm, ABSTRACT,       yesno); }
	public void setHeavyIndexing(boolean yesno) { mc.setValue(bm, HEAVY_INDEXING, yesno); }
}

//==============================================================================
