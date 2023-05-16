//==============================================================================
//===
//===   TableSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class TableSettings extends AbstractSettings
{
	public static final String POLYMORPH_IMPLICIT = "implicit";
	public static final String POLYMORPH_EXPLICIT = "explicit";
	public static final String POLYMORPH_DEFAULT  = "-";

	public static final String OPTLOCK_NONE    = "none";
	public static final String OPTLOCK_VERSION = "version";
	public static final String OPTLOCK_DIRTY   = "dirty";
	public static final String OPTLOCK_ALL     = "all";
	public static final String OPTLOCK_DEFAULT = "-";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String CLASS_NAME     = "className";
	private static final String DISCRIMIN_VAL  = "discrVal";
	private static final String MUTABLE        = "mutable";
	private static final String SCHEMA         = "schema";
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

	public String getClassName()       { return mc.getValue(bm, CLASS_NAME,     ""); }
	public String getDiscriminValue()  { return mc.getValue(bm, DISCRIMIN_VAL,  ""); }
	public String getSchema()          { return mc.getValue(bm, SCHEMA,         ""); }
	public String getProxy()           { return mc.getValue(bm, PROXY,          ""); }
	public String getWhere()           { return mc.getValue(bm, WHERE,          ""); }
	public String getPersister()       { return mc.getValue(bm, PERSISTER,      ""); }
	public String getBatchSize()       { return mc.getValue(bm, BATCH_SIZE,     ""); }
	public String getPolymorphism()    { return mc.getValue(bm, POLYMORPHISM,   POLYMORPH_DEFAULT); }
	public String getOptimisticLock()  { return mc.getValue(bm, OPTIM_LOCK,     OPTLOCK_DEFAULT);   }
	public String getMutable()         { return mc.getValue(bm, MUTABLE,        Consts.DEFAULT); }
	public String getDynamicInsert()   { return mc.getValue(bm, DYNAMIC_INSERT, Consts.DEFAULT); }
	public String getDynamicUpdate()   { return mc.getValue(bm, DYNAMIC_UPDATE, Consts.DEFAULT); }
	public String getSelectBeforeUpd() { return mc.getValue(bm, SELECT_BEF_UPD, Consts.DEFAULT); }
	public String getLazy()            { return mc.getValue(bm, LAZY,           Consts.DEFAULT); }
	public String getGenerateIds()     { return mc.getValue(bm, GENIDS,         Consts.DEFAULT); }
	public String getForeignKeys()     { return mc.getValue(bm, FORKEY,         Consts.DEFAULT); }

	//--- setters

	public void setClassName      (String s) { mc.setValue(bm, CLASS_NAME,     s); }
	public void setDiscriminValue (String s) { mc.setValue(bm, DISCRIMIN_VAL,  s); }
	public void setSchema         (String s) { mc.setValue(bm, SCHEMA,         s); }
	public void setProxy          (String s) { mc.setValue(bm, PROXY,          s); }
	public void setWhere          (String s) { mc.setValue(bm, WHERE,          s); }
	public void setPersister      (String s) { mc.setValue(bm, PERSISTER,      s); }
	public void setBatchSize      (String s) { mc.setValue(bm, BATCH_SIZE,     s); }
	public void setPolymorphism   (String s) { mc.setValue(bm, POLYMORPHISM,   s); }
	public void setOptimisticLock (String s) { mc.setValue(bm, OPTIM_LOCK,     s); }
	public void setMutable        (String s) { mc.setValue(bm, MUTABLE,        s); }
	public void setDynamicInsert  (String s) { mc.setValue(bm, DYNAMIC_INSERT, s); }
	public void setDynamicUpdate  (String s) { mc.setValue(bm, DYNAMIC_UPDATE, s); }
	public void setSelectBeforeUpd(String s) { mc.setValue(bm, SELECT_BEF_UPD, s); }
	public void setLazy           (String s) { mc.setValue(bm, LAZY,           s); }
	public void setGenerateIds    (String s) { mc.setValue(bm, GENIDS,         s); }
	public void setForeignKeys    (String s) { mc.setValue(bm, FORKEY,         s); }
}

//==============================================================================
