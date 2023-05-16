//==============================================================================
//===
//===   DatabaseSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor;

import druid.data.ModulesConfig;
import druid.core.AttribList;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class DatabaseSettings extends AbstractSettings
{
	//---------------------------------------------------------------------------
	//---
	//--- Defaults
	//---
	//---------------------------------------------------------------------------

	private static AttribList defInclude = new AttribList();

	//---------------------------------------------------------------------------

	static
	{
		defInclude.addAttrib("include", "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String PACKAGE          = "package";
	private static final String CLASS_SUFFIX     = "classSuffix";
	private static final String INCLUDES         = "includes";
	private static final String USE_DDEQUIV      = "useDDEquiv";

	private static final String KG_HL_TABLE      = "hl_table";
	private static final String KG_HL_KEYFIELD   = "hl_keyField";
	private static final String KG_HL_VALFIELD   = "hl_valField";
	private static final String KG_HL_GRABSIZE   = "hl_grabSize";
	private static final String KG_HL_SAMECONN   = "hl_sameConn";
	private static final String KG_HL_GLOBAL     = "hl_global";

	private static final String KG_SEQ_SEQUENCE  = "se_sequence";
	private static final String KG_SEQ_RETURNING = "se_returning";
	private static final String KG_SEQ_INCREMENT = "se_increment";
	private static final String KG_SEQ_TRIGGER   = "se_trigger";

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

	public String getPackage()      { return mc.getValue(bm, PACKAGE,          ""); }
	public String getClassSuffix()  { return mc.getValue(bm, CLASS_SUFFIX,     ""); }
	public String getHLTable()      { return mc.getValue(bm, KG_HL_TABLE,      ""); }
	public String getHLKeyField()   { return mc.getValue(bm, KG_HL_KEYFIELD,   ""); }
	public String getHLValueField() { return mc.getValue(bm, KG_HL_VALFIELD,   ""); }
	public String getHLGrabSize()   { return mc.getValue(bm, KG_HL_GRABSIZE,   ""); }
	public String getSEQSequence()  { return mc.getValue(bm, KG_SEQ_SEQUENCE,  ""); }
	public String getSEQIncrement() { return mc.getValue(bm, KG_SEQ_INCREMENT, ""); }

	public boolean isHLSameConn()   { return mc.getValue(bm, KG_HL_SAMECONN,   false); }
	public boolean isHLGlobal()     { return mc.getValue(bm, KG_HL_GLOBAL,     false); }
	public boolean isSEQReturning() { return mc.getValue(bm, KG_SEQ_RETURNING, false); }
	public boolean isSEQTrigger()   { return mc.getValue(bm, KG_SEQ_TRIGGER,   false); }
	public boolean isUsingDDEquiv() { return mc.getValue(bm, USE_DDEQUIV,      false); }

	public AttribList getIncludes() { return mc.getAttribList(bm, INCLUDES, defInclude); }

	//--------------------------------------------------------------------------
	//--- setters

	public void setPackage     (String s) { mc.setValue(bm, PACKAGE,          s); }
	public void setClassSuffix (String s) { mc.setValue(bm, CLASS_SUFFIX,     s); }
	public void setHLTable     (String s) { mc.setValue(bm, KG_HL_TABLE,      s); }
	public void setHLKeyField  (String s) { mc.setValue(bm, KG_HL_KEYFIELD,   s); }
	public void setHLValueField(String s) { mc.setValue(bm, KG_HL_VALFIELD,   s); }
	public void setHLGrabSize  (String s) { mc.setValue(bm, KG_HL_GRABSIZE,   s); }
	public void setSEQSequence (String s) { mc.setValue(bm, KG_SEQ_SEQUENCE,  s); }
	public void setSEQIncrement(String s) { mc.setValue(bm, KG_SEQ_INCREMENT, s); }

	public void setHLSameConn  (boolean s) { mc.setValue(bm, KG_HL_SAMECONN,   s); }
	public void setHLGlobal    (boolean s) { mc.setValue(bm, KG_HL_GLOBAL,     s); }
	public void setSEQReturning(boolean s) { mc.setValue(bm, KG_SEQ_RETURNING, s); }
	public void setSEQTrigger  (boolean s) { mc.setValue(bm, KG_SEQ_TRIGGER,   s); }
	public void setUsingDDEquiv(boolean s) { mc.setValue(bm, USE_DDEQUIV,      s); }
}

//==============================================================================
