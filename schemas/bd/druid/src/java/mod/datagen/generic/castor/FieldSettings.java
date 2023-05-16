//==============================================================================
//===
//===   FieldSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class FieldSettings extends AbstractSettings
{
	public static final String TRUE  = "true";
	public static final String FALSE = "false";
	public static final String OMIT  = "-";

	public static final String COLL_NONE    = "-";			//--- default
	public static final String COLL_ARRAY   = "array";
	public static final String COLL_ARRLIST = "arraylist";
	public static final String COLL_VECTOR  = "vector";
	public static final String COLL_HASHTBL = "hashtable";
	public static final String COLL_COLLECT = "collection";
	public static final String COLL_SET     = "set";
	public static final String COLL_MAP     = "map";
	public static final String COLL_ENUM    = "enumerate";

	public static final String DIRTY_CHECK  = "check";		//--- default
	public static final String DIRTY_IGNORE = "ignore";

	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	private static final String DIRECT        = "dir";
	private static final String LAZY          = "laz";
	private static final String TRANSIENT     = "tra";
	private static final String GET_METHOD    = "get";
	private static final String SET_METHOD    = "set";
	private static final String CREATE_METHOD = "cre";
	private static final String COLLECTION    = "col";
	private static final String HANDLER       = "han";
	private static final String HAS_METHOD    = "has";
	private static final String CONTAINER     = "con";

	private static final String SQL_READONLY  = "sqr";
	private static final String SQL_DIRTY     = "sqd";
	private static final String SQL_TRANSIENT = "sqt";

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

	public String getGetMethod()    { return mc.getValue(bm, GET_METHOD,    ""); }
	public String getSetMethod()    { return mc.getValue(bm, SET_METHOD,    ""); }
	public String getCreateMethod() { return mc.getValue(bm, CREATE_METHOD, ""); }
	public String getCollection()   { return mc.getValue(bm, COLLECTION,    COLL_NONE); }
	public String getHandler()      { return mc.getValue(bm, HANDLER,       ""); }
	public String getHasMethod()    { return mc.getValue(bm, HAS_METHOD,    ""); }
	public String getContainer()    { return mc.getValue(bm, CONTAINER,     OMIT); }
	public String getSqlDirty()     { return mc.getValue(bm, SQL_DIRTY,     DIRTY_CHECK); }

	public boolean isDirect()       { return mc.getValue(bm, DIRECT,        false); }
	public boolean isLazy()         { return mc.getValue(bm, LAZY,          false); }
	public boolean isTransient()    { return mc.getValue(bm, TRANSIENT,     false); }
	public boolean isSqlReadOnly()  { return mc.getValue(bm, SQL_READONLY,  false); }
	public boolean isSqlTransient() { return mc.getValue(bm, SQL_TRANSIENT, false); }

	//--- setters

	public void setGetMethod   (String s) { mc.setValue(bm, GET_METHOD,    s); }
	public void setSetMethod   (String s) { mc.setValue(bm, SET_METHOD,    s); }
	public void setCreateMethod(String s) { mc.setValue(bm, CREATE_METHOD, s); }
	public void setCollection  (String s) { mc.setValue(bm, COLLECTION,    s); }
	public void setHandler     (String s) { mc.setValue(bm, HANDLER,       s); }
	public void setHasMethod   (String s) { mc.setValue(bm, HAS_METHOD,    s); }
	public void setContainer   (String s) { mc.setValue(bm, CONTAINER,     s); }
	public void setSqlDirty    (String s) { mc.setValue(bm, SQL_DIRTY,     s); }

	public void setDirect      (boolean yesno) { mc.setValue(bm, DIRECT,        yesno); }
	public void setLazy        (boolean yesno) { mc.setValue(bm, LAZY,          yesno); }
	public void setTransient   (boolean yesno) { mc.setValue(bm, TRANSIENT,     yesno); }
	public void setSqlReadOnly (boolean yesno) { mc.setValue(bm, SQL_READONLY,  yesno); }
	public void setSqlTransient(boolean yesno) { mc.setValue(bm, SQL_TRANSIENT, yesno); }
}

//==============================================================================
