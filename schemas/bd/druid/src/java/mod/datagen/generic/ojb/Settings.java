//==============================================================================
//===
//===   Settings
//===
//===   Copyright (C) by Antonio Gallardo & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================
package mod.datagen.generic.ojb;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
/**
 *  OJB Settings
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: Settings.java,v 1.9 2003/11/14 16:30:41 antoniog Exp $
*/
public class Settings {

    // XML attributes for - DB Connection
    public static final String JCDALIAS = "jcd-alias";
    public static final String DEFAULTCONNECTION = "default-connection";
    public static final String DBPLAT = "platform";
    public static final String JDBCLEVEL = "jdbc-level";
	public static final String SEQUENCEMANAGER = "sequence-manager";
    public static final String DBHOST = "host";
    public static final String DBPORT = "port";
    public static final String DBUSER = "username";
    public static final String DBPASSWORD = "password";

    // OJB
    public static final String OJB_PACKAGE = "Package";
    public static final String OJB_CLASS_SUFFIX = "ClassSuffix";

    // Database Plataform
	public static final String DBPLAT_AXION = "Axion";
	public static final String DBPLAT_DB2 = "Db2";
	public static final String DBPLAT_FIREBIRD = "Firebird";
	public static final String DBPLAT_HSQLDB = "Hsqldb";
	public static final String DBPLAT_INFORMIX = "Informix";
	public static final String DBPLAT_MSACCESS = "MsAccess";
	public static final String DBPLAT_MSSQLSERVER = "MsSQLServer";
	public static final String DBPLAT_MYSQL = "MySQL";
	public static final String DBPLAT_NONSTOPSQL = "NonstopSql";
	public static final String DBPLAT_ORACLE = "Oracle";
	public static final String DBPLAT_ORACLE9 = "Oracle9i";
	public static final String DBPLAT_POSTGRES = "PostgreSQL";
	public static final String DBPLAT_SYBASE = "Sybase";
	public static final String DBPLAT_SYBASEASE = "SybaseASE";
	public static final String DBPLAT_SYBASEASA = "SybaseASA";
	public static final String DBPLAT_SAPDB = "Sapdb";

    // JDBC Level
    public static final String JDBC_LEVEL1 = "1.0";
    public static final String JDBC_LEVEL2 = "2.0";
    public static final String JDBC_LEVEL3 = "3.0";

	// Generated filenames
	public static final String REPOSITORY_XML = "repository.xml";
	
	// Sequence Manager
	public static final String SEQUENCE_HIGHLOW = "HighLow";
	public static final String SEQUENCE_INMEMORY = "InMemory";
	public static final String SEQUENCE_NEXTVAL = "NextVal";
	public static final String SEQUENCE_SEQHILO = "SeqHiLo";
	public static final String SEQUENCE_STOREDPROCEDURE = "StoredProcedure";
	public static final String SEQUENCE_MSSQLGUID = "MSSQLGuid";
	public static final String SEQUENCE_MYSQL = "MySQL";
	
	// Sequence Manager - Names to be presented to the users.
	public static final String SEQUENCE_HIGHLOW_N = "OJB High/Low";
	public static final String SEQUENCE_INMEMORY_N = "OJB In-Memory";
	public static final String SEQUENCE_NEXTVAL_N = "Database based NextVal";
	public static final String SEQUENCE_SEQHILO_N = "Database based High/Low";
	public static final String SEQUENCE_STOREDPROCEDURE_N = "Oracle-Style (Stored Procedures)";
	public static final String SEQUENCE_MSSQLGUID_N = "MS SQL Server 7.0+ GUID";
	public static final String SEQUENCE_MYSQL_N = "MySQL";
	
	// Other fields
	public static final String DEFAULTCONNECTION_NAME = "defaultconnection";
	public static final String COLLECTIONDESCRIPTOR_NAME = "collectionDescriptor";
	public static final String REFERENCEDESCRIPTOR_NAME = "referenceDescriptor";
	public static final String USEDATECONVERTOR_NAME = "useDateConvertor";

	private ModulesConfig mc;
	private BasicModule   bm;

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public Settings(ModulesConfig mc, BasicModule bm)
	{
		this.mc = mc;
		this.bm = bm;
	}
	
	//	--------------------------------------------------------------------------
	//---
	//--- JDBC settings API
	//---
	//--------------------------------------------------------------------------
	public String getJcdAlias() { return mc.getValue(bm, JCDALIAS,            "default"); }
	public boolean isDefaultConnection() { return mc.getBoolValue(bm, DEFAULTCONNECTION_NAME); }
	public String getDbPlat() { return mc.getValue(bm, DBPLAT, DBPLAT_POSTGRES); }
	public String getJdbcLevel() { return mc.getValue(bm, JDBCLEVEL, JDBC_LEVEL3); }
	public String getSequenceManager() { return mc.getValue(bm, SEQUENCEMANAGER, SEQUENCE_HIGHLOW); }
	public String getDbHost() { return mc.getValue(bm, DBHOST, "localhost"); }
	public String getDbPort() { return mc.getValue(bm, DBPORT, "5432"); }
	public String getDbUser() { return mc.getValue(bm, DBUSER); }
	public String getDbPassword() { return mc.getValue(bm, DBPASSWORD); }
	public String getOjbPackage() { return mc.getValue(bm, OJB_PACKAGE); }
	public String getOjbClassSuffix() { return mc.getValue(bm, OJB_CLASS_SUFFIX); }
	public boolean isCollectionDescriptor() { return mc.getBoolValue(bm, COLLECTIONDESCRIPTOR_NAME); }
	public boolean isReferenceDescriptor() { return mc.getBoolValue(bm, REFERENCEDESCRIPTOR_NAME); }
	public boolean isDateConvertor() { return mc.getBoolValue(bm, USEDATECONVERTOR_NAME); }
	
	public void setJcdAlias(String value)   { mc.setValue(bm, JCDALIAS,      value); }
	public void setDefaultConnection(boolean value) {mc.setValue(bm, DEFAULTCONNECTION_NAME, value); }
	public void setDbPlat(String value) {mc.setValue(bm, DBPLAT, value); }
	public void setJdbcLevel(String value) {mc.setValue(bm, JDBCLEVEL, value); }	
	public void setSequenceManager(String value) {mc.setValue(bm, SEQUENCEMANAGER, value); }
	public void setDbHost(String value) {mc.setValue(bm, DBHOST, value); }
	public void setDbPort(String value) {mc.setValue(bm, DBPORT, value); }
	public void setDbUser(String value) {mc.setValue(bm, DBUSER, value); }
	public void setDbPassword(String value) {mc.setValue(bm, DBPASSWORD, value); }
	public void setOjbPackage(String value) {mc.setValue(bm, OJB_PACKAGE, value); }
	public void setOjbClassSuffix(String value) {mc.setValue(bm, OJB_CLASS_SUFFIX, value); }
	public void setCollectionDescriptor(boolean value) {mc.setValue(bm, COLLECTIONDESCRIPTOR_NAME, value); }
	public void setReferenceDescriptor(boolean value) {mc.setValue(bm, REFERENCEDESCRIPTOR_NAME, value); }
	public void setDateConvertor(boolean value) {mc.setValue(bm, USEDATECONVERTOR_NAME, value); }
}
