//==============================================================================
//===
//===   DatabaseSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class DatabaseSettings extends AbstractSettings
{
	//--- database constants

	public static final String DEFCASCADE_NONE    = "none";			//--- default
	public static final String DEFCASCADE_SAVEUPD = "save-update";
	public static final String DEFCASCADE_ALL     = "all";

	//--- table constants

	public static final String POLYMORPH_IMPLICIT = "implicit";	//--- default
	public static final String POLYMORPH_EXPLICIT = "explicit";

	public static final String OPTLOCK_NONE    = "none";
	public static final String OPTLOCK_VERSION = "version";	//--- default
	public static final String OPTLOCK_DIRTY   = "dirty";
	public static final String OPTLOCK_ALL     = "all";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	//--- database constants

	private static final String PACKAGE         = "package";
	private static final String DEFAULT_CASCADE = "defCascade";
	private static final String AUTO_IMPORT     = "autoImp";

	//--- table constants

	private static final String CLASS_NAME     = "className";
	private static final String DISCRIMIN_VAL  = "discrVal";
	private static final String SCHEMA         = "schema";
	private static final String MUTABLE        = "mutable";
	private static final String PROXY          = "proxy";
	private static final String DYNAMIC_INSERT = "dynInsert";
	private static final String DYNAMIC_UPDATE = "dynUpdate";
	private static final String SELECT_BEF_UPD = "selBefUpd";
	private static final String POLYMORPHISM   = "polymorph";
	private static final String WHERE          = "where";
	private static final String PERSISTER      = "persister";
	private static final String BATCH_SIZE     = "batchSize";
	private static final String OPTIM_LOCK     = "optLock";
	private static final String LAZY           = "lazy";

	private static final String GENIDS         = "GenerateIds";
	private static final String FORKEY         = "ForeignKeys";

	//--- fields constants

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

	public DatabaseSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods (database)
	//---
	//--------------------------------------------------------------------------

	public String getPackage()        { return mc.getValue(bm, PACKAGE,         ""); }
	public String getDefaultCascade() { return mc.getValue(bm, DEFAULT_CASCADE, DEFCASCADE_NONE); }

	public boolean isAutoImport() { return mc.getValue(bm, AUTO_IMPORT, true); }

	//--- setters

	public void setPackage(String s)        { mc.setValue(bm, PACKAGE,         s); }
	public void setDefaultCascade(String s) { mc.setValue(bm, DEFAULT_CASCADE, s); }

	public void setAutoImport(boolean yesno) { mc.setValue(bm, AUTO_IMPORT, yesno); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods (Tables)
	//---
	//--------------------------------------------------------------------------

	public String getClassName()      { return mc.getValue(bm, CLASS_NAME,    ""); }
	public String getDiscriminValue() { return mc.getValue(bm, DISCRIMIN_VAL, ""); }
	public String getSchema()         { return mc.getValue(bm, SCHEMA,        ""); }
	public String getProxy()          { return mc.getValue(bm, PROXY,         ""); }
	public String getWhere()          { return mc.getValue(bm, WHERE,         ""); }
	public String getPersister()      { return mc.getValue(bm, PERSISTER,     ""); }
	public String getBatchSize()      { return mc.getValue(bm, BATCH_SIZE,    "1"); }
	public String getPolymorphism()   { return mc.getValue(bm, POLYMORPHISM,  POLYMORPH_IMPLICIT); }
	public String getOptimisticLock() { return mc.getValue(bm, OPTIM_LOCK,    OPTLOCK_VERSION); }

	public boolean isMutable()         { return mc.getValue(bm, MUTABLE,         true); }
	public boolean isDynamicInsert()   { return mc.getValue(bm, DYNAMIC_INSERT, false); }
	public boolean isDynamicUpdate()   { return mc.getValue(bm, DYNAMIC_UPDATE, false); }
	public boolean isSelectBeforeUpd() { return mc.getValue(bm, SELECT_BEF_UPD, false); }
	public boolean isLazy()            { return mc.getValue(bm, LAZY,           false); }

	public boolean isGenerateIds()     { return mc.getValue(bm, GENIDS,          true); }
	public boolean isForeignKeys()     { return mc.getValue(bm, FORKEY,          true); }

	//--- setters

	public void setClassName     (String s) { mc.setValue(bm, CLASS_NAME,    s); }
	public void setDiscriminValue(String s) { mc.setValue(bm, DISCRIMIN_VAL, s); }
	public void setSchema        (String s) { mc.setValue(bm, SCHEMA,        s); }
	public void setProxy         (String s) { mc.setValue(bm, PROXY,         s); }
	public void setWhere         (String s) { mc.setValue(bm, WHERE,         s); }
	public void setPersister     (String s) { mc.setValue(bm, PERSISTER,     s); }
	public void setBatchSize     (String s) { mc.setValue(bm, BATCH_SIZE,    s); }
	public void setPolymorphism  (String s) { mc.setValue(bm, POLYMORPHISM,  s); }
	public void setOptimisticLock(String s) { mc.setValue(bm, OPTIM_LOCK,    s); }

	public void setMutable        (boolean yesno) { mc.setValue(bm, MUTABLE,        yesno); }
	public void setDynamicInsert  (boolean yesno) { mc.setValue(bm, DYNAMIC_INSERT, yesno); }
	public void setDynamicUpdate  (boolean yesno) { mc.setValue(bm, DYNAMIC_UPDATE, yesno); }
	public void setSelectBeforeUpd(boolean yesno) { mc.setValue(bm, SELECT_BEF_UPD, yesno); }
	public void setLazy           (boolean yesno) { mc.setValue(bm, LAZY,           yesno); }

	public void setGenerateIds    (boolean yesno) { mc.setValue(bm, GENIDS,         yesno); }
	public void setForeignKeys    (boolean yesno) { mc.setValue(bm, FORKEY,         yesno); }

	//--------------------------------------------------------------------------
	//---
	//--- API methods (Fields)
	//---
	//--------------------------------------------------------------------------

	public String getPropertyName() { return mc.getValue(bm, PROP_NAME, ""); }
	public String getType()         { return mc.getValue(bm, TYPE,      ""); }
	public String getAccess()       { return mc.getValue(bm, ACCESS,    ""); }
	public String getFormula()      { return mc.getValue(bm, FORMULA,   ""); }

	public boolean isInsert() { return mc.getValue(bm, UPDATE, true); }
	public boolean isUpdate() { return mc.getValue(bm, INSERT, true); }

	//--- setters

	public void setPropertyName(String s) { mc.setValue(bm, PROP_NAME, s); }
	public void setType        (String s) { mc.setValue(bm, TYPE,      s); }
	public void setAccess      (String s) { mc.setValue(bm, ACCESS,    s); }
	public void setFormula     (String s) { mc.setValue(bm, FORMULA,   s); }

	public void setInsert(boolean yesno) { mc.setValue(bm, INSERT, yesno); }
	public void setUpdate(boolean yesno) { mc.setValue(bm, UPDATE, yesno); }
}

//==============================================================================
