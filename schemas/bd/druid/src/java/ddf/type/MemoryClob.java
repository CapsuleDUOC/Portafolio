//==============================================================================
//===
//===   MemoryClob
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

//==============================================================================

public class MemoryClob implements Clob
{
	private String sData;
	private int    iPos;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MemoryClob(String data)
	{
		sData = data;
		iPos  = 0;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Clob methods
	//---
	//---------------------------------------------------------------------------

	public long length() throws SQLException
	{
		return sData.length();
	}

	//---------------------------------------------------------------------------

	public String getSubString(long pos, int length) throws SQLException
	{
		return sData.substring((int) pos -1, length);
	}

	//---------------------------------------------------------------------------

	public Reader getCharacterStream () throws SQLException
	{
		return new StringReader(sData);
	}

	//---------------------------------------------------------------------------

	public InputStream getAsciiStream () throws SQLException
	{
		return new ByteArrayInputStream(sData.getBytes());
	}

	//---------------------------------------------------------------------------

	public long position(String searchStr, long start) throws SQLException
	{
		return sData.indexOf(searchStr, (int) start);
	}

	//---------------------------------------------------------------------------

	public long position(Clob searchStr, long start) throws SQLException
	{
		return 0;
	}

	//---------------------------------------------------------------------------
	//--- Java 1.4 Clob methods
	//---------------------------------------------------------------------------

	public int setString(long pos, String str) throws SQLException
	{
		System.out.println("Called : setString 1");
		return 0;
	}

	//---------------------------------------------------------------------------

	public int setString(long pos, String str, int offset, int len) throws SQLException
	{
		System.out.println("Called : setString 2");
		return 0;
	}

	//---------------------------------------------------------------------------

	public OutputStream setAsciiStream(long pos) throws SQLException
	{
		System.out.println("Called : setAsciiStream");
		return null;
	}

	//---------------------------------------------------------------------------

	public Writer setCharacterStream(long pos) throws SQLException
	{
		System.out.println("Called : setCharacterStream");
		return null;
	}

	//---------------------------------------------------------------------------

	public void truncate(long len) throws SQLException
	{
		System.out.println("Called : truncate");
	}

	//---------------------------------------------------------------------------
	//--- Java 1.6 Clob methods
	//---------------------------------------------------------------------------

	public void free() throws SQLException 
	{
	}

	//---------------------------------------------------------------------------

	public Reader getCharacterStream(long pos, long length) throws SQLException 
	{
		return null;
	}
}

//==============================================================================
