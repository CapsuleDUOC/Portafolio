//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.php;

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

    public static final String TYPE_DB_MYSQL = "MySQL";
    public static final String TYPE_DB_MYSQLI = "MySQL (mysqli)";    
    public static final String TYPE_DB_ORACLE = "ORACLE (OCI8)";	
    public static final String TYPE_DB_POSTGRES = "Posgresql (8.1+)";
    public static final String TYPE_DB_PDO = "PDO (PHP Data Objects)";
    
	private static final String EXTENDS     = "extends";
	private static final String NAME_PREFIX = "namePrefix";
	private static final String NAME_SUFFIX = "nameSuffix";
	private static final String NAME_DBTYPE  = "nameDbType";
	
	private static final String GEN_CONSTS  = "genConsts";
	private static final String GEN_NAMES   = "genNames";
	private static final String GEN_VARIA   = "genVaria";
	private static final String GEN_PERSI     = "genPersi";
	private static final String GEN_MVC     = "genMVC";
	private static final String GEN_DOJO     = "genDojo";
	private static final String PATHTODOJO = "PathToDojo";
	private static final String GEN_TESTS     = "genTests";
	private static final String GEN_FRAME     = "genFrame";
    private static final String GEN_EXAMPLE     = "genExample";
    private static final String DETECT_AUTOPK   = "detectAutoPK";


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

	public String  getExtends()    { return mc.getValue(bm, EXTENDS,     ""); }
	public String  getNamePrefix() { return mc.getValue(bm, NAME_PREFIX, ""); }
	public String  getNameSuffix() { return mc.getValue(bm, NAME_SUFFIX, ""); }
	public String  getPathToDojo() { return mc.getValue(bm, PATHTODOJO, "http://o.aolcdn.com/dojo/1.1.1/dojo/dojo.xd.js"); } 
    public String  getNameDbType() { return mc.getValue(bm, NAME_DBTYPE, TYPE_DB_MYSQL); }
	
	public boolean isGenConsts()   { return mc.getValue(bm, GEN_CONSTS,  true); }
	public boolean isGenNames()    { return mc.getValue(bm, GEN_NAMES,   true); }
	public boolean isGenVaria()    { return mc.getValue(bm, GEN_VARIA,   true); }
	public boolean isGenPersi()      { return mc.getValue(bm, GEN_PERSI,     true); }
	public boolean isGenMVC()      { return mc.getValue(bm, GEN_MVC,     true); }
	public boolean isGenDojo()      { return mc.getValue(bm, GEN_DOJO,     true); }	
	public boolean isGenTests()      { return mc.getValue(bm, GEN_TESTS,     true); }	
	public boolean isGenFrame()      { return mc.getValue(bm, GEN_FRAME,     true); }
	public boolean isGenExample()      { return mc.getValue(bm, GEN_EXAMPLE,     true); }
	public boolean isDetectAutoPK()    { return mc.getValue(bm, DETECT_AUTOPK, true); } 	
	//--------------------------------------------------------------------------

	public void setExtends   (String value) { mc.setValue(bm, EXTENDS,     value); }
	public void setNamePrefix(String value) { mc.setValue(bm, NAME_PREFIX, value); }
	public void setNameSuffix(String value) { mc.setValue(bm, NAME_SUFFIX, value); }
	public void setPathToDojo(String value) { mc.setValue(bm, PATHTODOJO, value); } 
    public void setNameDbType(String value) { mc.setValue(bm, NAME_DBTYPE, value); }
    
	public void setGenConsts(boolean value) { mc.setValue(bm, GEN_CONSTS, value); }
	public void setGenNames (boolean value) { mc.setValue(bm, GEN_NAMES,  value); }
	public void setGenVaria (boolean value) { mc.setValue(bm, GEN_VARIA,  value); }
	public void setGenPersi (boolean value)   { mc.setValue(bm, GEN_PERSI,    value); }		
	public void setGenMVC (boolean value)   { mc.setValue(bm, GEN_MVC,    value); }
	public void setGenDojo (boolean value)   { mc.setValue(bm, GEN_DOJO,    value); }
	public void setGenTests (boolean value)   { mc.setValue(bm, GEN_TESTS,    value); }		
	public void setGenFrame (boolean value)   { mc.setValue(bm, GEN_FRAME,    value); }		
	public void setGenExample (boolean value)   { mc.setValue(bm, GEN_EXAMPLE,    value); }
	public void setDetectAutoPK (boolean value) { mc.setValue(bm, DETECT_AUTOPK, value); } 
}

//==============================================================================
