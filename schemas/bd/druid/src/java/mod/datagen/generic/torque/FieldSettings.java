//==============================================================================
//===
//===   FieldSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class FieldSettings extends AbstractSettings
{
	public static final String JAVATYPE_PRIMITIVE = "primitive";	//--- default
	public static final String JAVATYPE_OBJECT    = "object";

	public static final String JAVANAM_NOCHANGE   = "nochange";		//--- default
	public static final String JAVANAM_UNDERSCORE = "underscore";
	public static final String JAVANAM_JAVANAME   = "javaname";
	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String JAVA_NAME       = "javaName";
	private static final String JAVA_TYPE       = "javaType";
	private static final String JAVA_NAMING     = "javaNam";
	private static final String AUTO_INCREMENT  = "autoIncrement";
	private static final String INPUT_VALIDATOR = "inputValidator";

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

	public String getJavaName()   { return mc.getValue(bm, JAVA_NAME,       ""); }
	public String getInputValid() { return mc.getValue(bm, INPUT_VALIDATOR, ""); }
	public String getJavaType()   { return mc.getValue(bm, JAVA_TYPE,       JAVATYPE_PRIMITIVE); }
	public String getJavaNaming() { return mc.getValue(bm, JAVA_NAMING,     JAVANAM_NOCHANGE); }

	public boolean isAutoIncrement() { return mc.getValue(bm, AUTO_INCREMENT, false); }

	//--- setters

	public void setJavaName(String s)   { mc.setValue(bm, JAVA_NAME,       s); }
	public void setInputValid(String s) { mc.setValue(bm, INPUT_VALIDATOR, s); }
	public void setJavaType(String s)   { mc.setValue(bm, JAVA_TYPE,       s); }
	public void setJavaNaming(String s) { mc.setValue(bm, JAVA_NAMING,     s); }

	public void setAutoIncrement(boolean yesno) { mc.setValue(bm, AUTO_INCREMENT, yesno); }
}

//==============================================================================
