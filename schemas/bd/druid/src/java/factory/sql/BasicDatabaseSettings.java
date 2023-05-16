//==============================================================================
//===
//===   BasicDatabaseSettings
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql;

import org.dlib.tools.TextFileLoader;

import druid.core.AttribList;
import druid.core.config.Config;
import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;
import factory.AbstractSettings;

//==============================================================================

public class BasicDatabaseSettings extends AbstractSettings
{
	//--------------------------------------------------------------------------
	//--- General boolean options (default = true)

	private static final String GEN_TABLES      = "genTables";
	private static final String GEN_INLINE_FKS  = "genInlineFKs";
	private static final String GEN_VIEWS       = "genViews";
	private static final String GEN_PROCEDURES  = "genProcedures";
	private static final String GEN_FUNCTIONS   = "genFunctions";
	private static final String GEN_SEQUENCES   = "genSequences";
	private static final String GEN_TRIGGERS    = "genTriggers";
	private static final String GEN_RULES       = "genRules";
	private static final String GEN_COMMENTS    = "genComments";
	private static final String GEN_INDEXES     = "genIndexes";
	private static final String GEN_DROPSTATEM  = "genDropStmt";
	private static final String GEN_PRESQL      = "genPreSQL";
	private static final String GEN_POSTSQL     = "genPostSQL";

	//--- ordering (default = OPTIMIZED)

	private static final String ORDER = "order";

	public static final String ORDER_OPTIMIZED  = "o";
	public static final String ORDER_SEQUENCIAL = "s";

	private static final String SQL_MAPPING = "sqlMapping";

	//--- alternate drop table/sequence statement
	
	private static final String ALT_DROP_TABLE_FLAG = "altDropTable";
	private static final String ALT_DROP_TABLE_STMT = "altDropTableStmt";

	private static final String ALT_DROP_SEQUENCE_FLAG = "altDropSeq";
	private static final String ALT_DROP_SEQUENCE_STMT = "altDropSeqStmt";

	//---------------------------------------------------------------------------
	//---
	//--- Defaults
	//---
	//---------------------------------------------------------------------------

	private static AttribList defMapping = new AttribList();

	//---------------------------------------------------------------------------

	static
	{
		defMapping.addAttrib("sqlType",    "");
		defMapping.addAttrib("mappedType", "");
	}

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public BasicDatabaseSettings(ModulesConfig mc, BasicModule bm) { super(mc, bm); }

	//--------------------------------------------------------------------------
	//---
	//--- Generation API
	//---
	//--------------------------------------------------------------------------

	public boolean isGenTables()     { return mc.getValue(bm, GEN_TABLES,      true); }
	public boolean isGenInlineFKs()  { return mc.getValue(bm, GEN_INLINE_FKS,  true); }
	public boolean isGenViews()      { return mc.getValue(bm, GEN_VIEWS,       true); }
	public boolean isGenProcedures() { return mc.getValue(bm, GEN_PROCEDURES,  true); }
	public boolean isGenFunctions()  { return mc.getValue(bm, GEN_FUNCTIONS,   true); }
	public boolean isGenSequences()  { return mc.getValue(bm, GEN_SEQUENCES,   true); }
	public boolean isGenTriggers()   { return mc.getValue(bm, GEN_TRIGGERS,    true); }
	public boolean isGenRules()      { return mc.getValue(bm, GEN_RULES,       true); }
	public boolean isGenComments()   { return mc.getValue(bm, GEN_COMMENTS,    true); }
	public boolean isGenIndexes()    { return mc.getValue(bm, GEN_INDEXES,     true); }
	public boolean isGenDropStmts()  { return mc.getValue(bm, GEN_DROPSTATEM, false); }
	public boolean isGenPreSQL()     { return mc.getValue(bm, GEN_PRESQL,     false); }
	public boolean isGenPostSQL()    { return mc.getValue(bm, GEN_POSTSQL,    false); }

	public void setGenTables    (boolean value) { mc.setValue(bm, GEN_TABLES,     value); }
	public void setGenInlineFKs (boolean value) { mc.setValue(bm, GEN_INLINE_FKS, value); }
	public void setGenViews     (boolean value) { mc.setValue(bm, GEN_VIEWS,      value); }
	public void setGenProcedures(boolean value) { mc.setValue(bm, GEN_PROCEDURES, value); }
	public void setGenFunctions (boolean value) { mc.setValue(bm, GEN_FUNCTIONS,  value); }
	public void setGenSequences (boolean value) { mc.setValue(bm, GEN_SEQUENCES,  value); }
	public void setGenTriggers  (boolean value) { mc.setValue(bm, GEN_TRIGGERS,   value); }
	public void setGenRules     (boolean value) { mc.setValue(bm, GEN_RULES,      value); }
	public void setGenComments  (boolean value) { mc.setValue(bm, GEN_COMMENTS,   value); }
	public void setGenIndexes   (boolean value) { mc.setValue(bm, GEN_INDEXES,    value); }
	public void setGenDropStmts (boolean value) { mc.setValue(bm, GEN_DROPSTATEM, value); }
	public void setGenPreSQL    (boolean value) { mc.setValue(bm, GEN_PRESQL,     value); }
	public void setGenPostSQL   (boolean value) { mc.setValue(bm, GEN_POSTSQL,    value); }

	//--------------------------------------------------------------------------
	//---
	//--- Ordering API
	//---
	//--------------------------------------------------------------------------

	public String getOrder() { return mc.getValue(bm, ORDER, ORDER_OPTIMIZED); }

	public void setOrder(String value) { mc.setValue(bm, ORDER, value); }

	//--------------------------------------------------------------------------
	//---
	//--- Alternate drop table APIs
	//---
	//--------------------------------------------------------------------------

	public boolean isAltDropTable() { return mc.getValue(bm, ALT_DROP_TABLE_FLAG, false); }

	public void setAltDropTable(boolean value) 
	{ 
		mc.setValue(bm, ALT_DROP_TABLE_FLAG, value); 
	}

	//--------------------------------------------------------------------------

	public String getAltDropTableStmt() { return mc.getValue(bm, ALT_DROP_TABLE_STMT, DROP_TABLE_STMT); }

	public void setAltDropTableStmt(String value) 
	{ 
		mc.setValue(bm, ALT_DROP_TABLE_STMT, value); 
	}

	//--------------------------------------------------------------------------
	//---
	//--- Alternate drop sequence APIs
	//---
	//--------------------------------------------------------------------------

	public boolean isAltDropSequence() { return mc.getValue(bm, ALT_DROP_SEQUENCE_FLAG, false); }

	public void setAltDropSequence(boolean value) 
	{ 
		mc.setValue(bm, ALT_DROP_SEQUENCE_FLAG, value); 
	}

	//--------------------------------------------------------------------------

	public String getAltDropSequenceStmt() { return mc.getValue(bm, ALT_DROP_SEQUENCE_STMT, DROP_SEQUENCE_STMT); }

	public void setAltDropSequenceStmt(String value) 
	{ 
		mc.setValue(bm, ALT_DROP_SEQUENCE_STMT, value); 
	}

	//--------------------------------------------------------------------------
	//---
	//--- Other APIs
	//---
	//--------------------------------------------------------------------------

	public AttribList getSqlMapping()
	{
		return mc.getAttribList(bm, SQL_MAPPING, defMapping);
	}

	//--------------------------------------------------------------------------
	
	private static String DROP_TABLE_STMT;
	private static String DROP_SEQUENCE_STMT;
	
	static
	{
		TextFileLoader loader = new TextFileLoader(Config.dir.data + Config.os.fileSep + "drop-table.template");
		DROP_TABLE_STMT = loader.getString();

		loader = new TextFileLoader(Config.dir.data + Config.os.fileSep + "drop-sequence.template");
		DROP_SEQUENCE_STMT = loader.getString();
	}
}

//==============================================================================
