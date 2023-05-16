//==============================================================================
//===
//===   SqlScriptGenerator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.dlib.tools.Util;

import druid.core.DruidException;
import druid.core.config.Config;
import druid.data.DatabaseNode;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;
import druid.util.DruidUtil;
import factory.sql.algorithm.AbstractGenerator;
import factory.sql.algorithm.OptimizedGenerator;
import factory.sql.algorithm.SequentialGenerator;

//==============================================================================

public class SqlScriptGenerator
{
	public static final int ORDER_OPTIMIZED  = 0;
	public static final int ORDER_SEQUENCIAL = 1;

	//---------------------------------------------------------------------------

	private String LF = Config.os.lineSep;

	private DatabaseNode dbNode;
	private SqlGenModule sqlMod;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlScriptGenerator() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode node, SqlGenModule mod, int order, boolean genDrops)
	{
		BasicDatabaseSettings sett = new BasicDatabaseSettings(node.modsConfig, mod);

		dbNode = node;
		sqlMod = mod;

		l.logHeader("script for : " + mod.getFormat());

		//------------------------------------------------------------------------
		//--- generate script file choosing the right generator

		String sqlData;

		AbstractGenerator generator;

		switch(order)
		{
			case ORDER_OPTIMIZED: 	generator = new OptimizedGenerator(l, mod, dbNode);
											break;

			case ORDER_SEQUENCIAL: 	generator = new SequentialGenerator(l, mod, dbNode);
											break;

			default: throw new DruidException(DruidException.INC_STR, "Unknown order", order);
		}

		sqlData = generator.generate();

		//--- if sqlData is null there has been an error

		if (sqlData == null)
			return;

		//------------------------------------------------------------------------
		//--- write sql data to file

		String sOutput = dbNode.modsConfig.getValue(sqlMod, "output");
		Writer w = null;
        BufferedWriter bw = null;
        
		try 
		{
			w  = new FileWriter(DruidUtil.toAbsolutePath(sOutput));
            bw = new BufferedWriter(w);

			//--- write header

			bw.write(getHeader());

			//--- write pre sql

			String dbPreSql = dbNode.attrSet.getString("preSql").trim();

			if (sett.isGenPreSQL() && !dbPreSql.trim().equals(""))
			{
				bw.write(dbPreSql);
				bw.write(getSeparator());
			}

			//--- write generated data

			bw.write(sqlData);

			//--- write post sql

			String dbPostSql = dbNode.attrSet.getString("postSql").trim();

			if (sett.isGenPostSQL() && dbPostSql.trim().length() > 0)
			{
				bw.write(dbPostSql);
				bw.write(getSeparator());
			}
			l.log(Logger.INFO, "Done.");
		}
		catch(IOException e) 
		{
			l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
		} 
		finally 
		{
			try 
			{
				if (bw != null) bw.close();
				if (w != null)  w.close();
			} 
			catch (IOException e) { /* do nothing*/ }
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   Header, Separator, Footer
	//---
	//---------------------------------------------------------------------------

	private String getHeader()
	{
		String dbName = dbNode.attrSet.getString("name");
		int    build  = dbNode.getProjectNode().attrSet.getInt("build");

		StringBuffer sb = new StringBuffer();

		sb.append(sqlMod.comment(Util.replicate("=", 70)) + LF);
		sb.append(sqlMod.comment("===   Sql Script for Database : " + dbName) + LF);
		sb.append(sqlMod.comment("===") + LF);
		sb.append(sqlMod.comment("=== Build : " + build) + LF);
		sb.append(sqlMod.comment(Util.replicate("=", 70)) + LF);
		sb.append(LF);

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String getSeparator()
	{
		return LF + sqlMod.comment(Util.replicate("=", 70)) + LF + LF;
	}
}

//==============================================================================
