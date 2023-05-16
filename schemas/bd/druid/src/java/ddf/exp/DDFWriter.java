//==============================================================================
//===
//===   DDFWriter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.exp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.dlib.tools.Util;

import ddf.lib.Codec;
import ddf.type.Core;
import ddf.type.Core.Section;
import ddf.type.QueryField;

//==============================================================================

public class DDFWriter
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------
	
	public DDFWriter(String fileName) throws IOException
	{
		bw = new BufferedWriter(new FileWriter(fileName));

		formatter = new SimpleDateFormat(Core.TIMESTAMP_PATTERN);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API mnethods
	//---
	//---------------------------------------------------------------------------

	public void setQuery(String query)
	{
		this.query = query;
	}

	//---------------------------------------------------------------------------

	public void setSchema(String schema)
	{
		this.schema = schema;
	}

	//---------------------------------------------------------------------------

	public void setTable(String table)
	{
		this.table = table;
	}

	//---------------------------------------------------------------------------

	public void setFields(List<QueryField> fields)
	{
		this.fields = fields;
	}

	//---------------------------------------------------------------------------
	
	public void beginInsertSection() throws IOException
	{
		if (!isHeaderWritten)
		{
			writeHeader();
			isHeaderWritten = true;
		}

        bw.write("\n");
		bw.write(Section.INSERT +"\n");
	}

	//---------------------------------------------------------------------------
	
	public void beginUpdateSection() throws IOException
	{
		if (!isHeaderWritten)
		{
			writeHeader();
			isHeaderWritten = true;
		}

        bw.write("\n");
		bw.write(Section.UPDATE +"\n");
	}

	//---------------------------------------------------------------------------
	
	public void beginDeleteSection() throws IOException
	{
		if (!isHeaderWritten)
		{
			writeHeader();
			isHeaderWritten = true;
		}

        bw.write("\n");
		bw.write(Section.DELETE +"\n");
	}

	//---------------------------------------------------------------------------
	
	public void writeRow(List<Object> row) throws IOException
	{
		int i=0;

		for(Object obj : row)
		{
			if (obj != null)
			{
				if (obj instanceof byte[])
					bw.write(Codec.encodeBytes((byte[]) obj));
				else if (obj instanceof Timestamp)
					bw.write(formatter.format((Timestamp) obj));
				else
					bw.write(Codec.encodeString(obj.toString()));
			}

			if (i != row.size()-1) 
				bw.write("\t");
			
			i++;
		}
		
		bw.write("\n");
	}

	//---------------------------------------------------------------------------

	public void end() throws IOException
	{
		bw.close();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void writeHeader() throws IOException
	{
		bw.write("#---------------------------------------------------------\n");
		bw.write("#--- File exported in DDF format on " + Util.getCurrentDate() + "\n");
		bw.write("#---\n");

		if (query != null)
			bw.write("#--- " + query.replace('\n',' ').replace('\r',' ') + "\n");

		bw.write("#---------------------------------------------------------\n");
		bw.write("\n");
		bw.write(Section.INFO +"\n");
		bw.write("version="+ Core.DDF_VERSION +"\n");
		
		if (table != null)
		{
			bw.write("table="+ table +"\n");
			
			if (schema != null)
				bw.write("schema="+ schema +"\n");
		}

		//--- write fields

		bw.write("\n");
		bw.write(Section.FIELDS +"\n");
		
		for(QueryField field : fields)
			bw.write(encodeField(field) +"\n");
	}

	//---------------------------------------------------------------------------
	
	private String encodeField(QueryField qf)
	{
		return Util.pad(qf.getName() +",", 15) + 
			   Util.pad(qf.getType().sName +",", 12) + 
			   (qf.isPKey() ? "K" : "N");
	}
	
	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------
	
	private BufferedWriter   bw;
	private String           query;
	private List<QueryField> fields;
	private boolean          isHeaderWritten;
	private SimpleDateFormat formatter;

	private String schema;
	private String table;
}

//==============================================================================
