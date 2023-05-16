//==============================================================================
//===
//===   Info
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.type;

//==============================================================================

public class DDFInfo
{
	//---------------------------------------------------------------------------
	//---
	//--- DDF INFO section information
	//---
	//---------------------------------------------------------------------------
	
	public String getVersion() { return version; }
	
	public void setVersion(String version)
	{
		this.version = version;
	}

	//---------------------------------------------------------------------------
	
	public String getTable() { return table; }
	
	public void setTable(String table)
	{
		this.table = table;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------
	
	private String version;
	private String table;
}

//==============================================================================
