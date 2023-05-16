//==============================================================================
//===
//===   TabDelimited
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.jdbc.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import ddf.lib.SqlMapper;
import ddf.type.SqlType;
import druid.core.config.Config;
import druid.core.jdbc.JdbcConnection;
import druid.interfaces.ModuleOptions;
import druid.interfaces.RecordIOModule;

//==============================================================================

public class TabDelimited implements RecordIOModule
{
	public String getId()        { return "tabDel"; }
	public String getAuthor()    { return "Andrea Carboni"; }
	public String getVersion()   { return "1.0"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Export a table data/query result to an ascii file in tab delimited form";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public String  getFormat()    { return "Tab delimited"; }
	public String  getExtension() { return "csv"; }
	public boolean canImport()    { return false; }
	public boolean canExport()    { return true;  }

	//---------------------------------------------------------------------------
	//---
	//--- Vars
	//---
	//---------------------------------------------------------------------------

	private ResultSet rSet;

	private ResultSetMetaData rMeta;

	private SqlType[] aColTypes;

	//---------------------------------------------------------------------------
	//---
	//--- Methods
	//---
	//---------------------------------------------------------------------------

	public TabDelimited() {}

	//---------------------------------------------------------------------------

	public void doImport(JdbcConnection jdbcConn, String table, String fileName, ImportListener l) {}

	//---------------------------------------------------------------------------

	public void doExport(JdbcConnection jdbcConn, String query, String fileName, 
						 ExportListener l) throws IOException, SQLException 
	{
		//--- perform query

		rSet  = jdbcConn.select(query, null);
		rMeta = rSet.getMetaData();

		int colCount = rMeta.getColumnCount();

		//------------------------------------------------------------------------
		//--- retrieve columns types

		aColTypes = new SqlType[colCount];

		StringBuffer header = new StringBuffer();

		for (int i = 0; i < colCount; i++) 
		{
			String name     = rMeta.getColumnName(i+1);
			int    type     = rMeta.getColumnType(i+1);
			String dbType   = rMeta.getColumnTypeName(i+1);
			int    decimals = rMeta.getScale(i+1);

			header.append(name + "\t");
			aColTypes[i] = SqlMapper.map(""+type, dbType, ""+decimals, jdbcConn.getConnection());
		}

		//------------------------------------------------------------------------
		//--- open file, scan records and save data

		Object o = null;
        BufferedWriter bw = null;

        try 
        {
            bw = new BufferedWriter(new FileWriter(fileName));
            
            bw.write(header.toString().trim() + Config.os.lineSep);
            
    		while(rSet.next()) 
    		{
    			for(int j = 0; j < colCount; j++) 
    			{
    				if (aColTypes[j].isDate())
    					o = rSet.getDate(j+1);
    
    				else if (aColTypes[j].isTime())
    					o = rSet.getTime(j+1);
    
    				else if (aColTypes[j].isTimeStamp())
    					o = rSet.getTimestamp(j+1);
    
    				//--- main types
    
    				else if (aColTypes[j].isNumber() || aColTypes[j].isString())
    					o = rSet.getString(j+1);
    
    				//--- other unknown stuff
    
    				else
    					o = "< ???? >";
    
    				bw.write((o != null) ? o.toString() : "");
    
    				if (j != colCount-1) 
    					bw.write("\t");
    			}
    
    			bw.write(Config.os.lineSep);
    		}
    
    		rSet.getStatement().close();
        } 
        catch (IOException e) 
        {
            throw new IOException(e.getMessage());
        } 
        finally 
        {
            try 
            {
                if (bw != null) 
                	bw.close();
            } 
            catch (IOException e) { /* do nothing*/ }
        }
	}
}

//==============================================================================
