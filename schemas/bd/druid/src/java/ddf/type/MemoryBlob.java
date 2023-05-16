//==============================================================================
//===
//===   MemoryBlob
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

//==============================================================================

public class MemoryBlob implements Blob
{
	private byte[] aData;
	private int    iPos;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MemoryBlob(byte[] data)
	{
		aData = data;
		iPos  = 0;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Blob methods
	//---
	//---------------------------------------------------------------------------

	public long length() throws SQLException
	{
		return aData.length;
	}

	//---------------------------------------------------------------------------

	public byte[] getBytes(long pos, int length) throws SQLException
	{
		pos--;

		if (pos == 0 && length == aData.length) return aData;

		if (pos+length > aData.length)
			length = (int) (aData.length - pos);

		byte[] data = new byte[length];

		for(int i=0; i<length; i++)
			data[i] = aData[(int)pos + i];

		return data;
	}

	//---------------------------------------------------------------------------

	public InputStream getBinaryStream () throws SQLException
	{
		return new ByteArrayInputStream(aData);
	}

	//---------------------------------------------------------------------------

	public long position(byte pattern[], long start) throws SQLException
	{
		return 0;
	}

	//---------------------------------------------------------------------------

	public long position(Blob pattern, long start) throws SQLException
	{
		return 0;
	}

	//---------------------------------------------------------------------------
	//--- Java 1.4 Blob methods
	//---------------------------------------------------------------------------

	public int setBytes(long pos, byte[] bytes) throws SQLException
	{
		System.out.println("Called : setBytes 1");
		return 0;
	}

	//---------------------------------------------------------------------------

	public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException
	{
		System.out.println("Called : setBytes 2");
		return 0;
	}

	//---------------------------------------------------------------------------

	public OutputStream setBinaryStream(long pos) throws SQLException
	{
		System.out.println("Called : setBinaryStream");
		return null;
	}

	//---------------------------------------------------------------------------

	public void truncate(long len) throws SQLException
	{
		System.out.println("Called : truncate");
	}

	//---------------------------------------------------------------------------
	//--- Java 1.6 Blob methods
	//---------------------------------------------------------------------------

	public void free() throws SQLException 
	{
	}

	//---------------------------------------------------------------------------

	public InputStream getBinaryStream(long pos, long length) throws SQLException 
	{
		return null;
	}
}

//==============================================================================
