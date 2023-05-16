//==============================================================================
//===
//===   DDFReader
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.imp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.dlib.tools.FullTokenizer;

import ddf.imp.DDFReaderListener.OperationType;
import ddf.lib.Codec;
import ddf.lib.SqlMapper;
import ddf.type.Core;
import ddf.type.Core.Section;
import ddf.type.DDFInfo;
import ddf.type.QueryField;
import ddf.type.SqlType;

//==============================================================================

public class DDFReader
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------
	
	public DDFReader(String fileName) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(fileName));
		
		parser = new SimpleDateFormat(Core.TIMESTAMP_PATTERN);
		parser.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void read(DDFReaderListener listener) throws IOException, SQLException, ParseException
	{
		String line;
	    
		Section section       = null;
		boolean fieldsFired   = false;
		long    addedRecords  = 0;
		long    updatedRecords= 0;
		long    removedRecords= 0;
		
		info = new DDFInfo();

		List<QueryField> fields = new ArrayList<QueryField>();
		
		while ((line = br.readLine()) != null) 
		{
			//--- skip comments or blank lines
			if (line.length() == 0 || line.startsWith("#")) 
				continue;

			if (line.equals(Section.INFO.toString()))
				section = Section.INFO;

			else if (line.equals(Section.FIELDS.toString()))
				section = Section.FIELDS;

			//--- [DATA] is an hack for backward compatibility
			else if (line.equals(Section.INSERT.toString()) || line.equals("[DATA]"))
				section = Section.INSERT;

			else if (line.equals(Section.UPDATE.toString())) 
				section = Section.UPDATE;

			else if (line.equals(Section.DELETE.toString())) 
				section = Section.DELETE;

			else 
			{
				//--- fires query fields if not fired yet

				if ((section == Section.INSERT || section == Section.UPDATE || section == Section.DELETE) && !fieldsFired)
				{
					listener.handleQueryFields(fields);
					fieldsFired = true;
				}

				if (section == Section.INFO)
					handleInfo(info, line);

				else if (section == Section.FIELDS)
					fields.add(decodeField(line));

				else if (section == Section.INSERT)
				{
					if (!listener.handleInsertRow(decodeRow(fields, line), line, ++addedRecords))
						break;
					
					listener.handlePostRow(OperationType.INSERT, line, addedRecords);
				}
				
				else if (section == Section.UPDATE)
				{
					if (!listener.handleUpdateRow(decodeRow(fields, line), line, ++updatedRecords))
						break;
					
					listener.handlePostRow(OperationType.UPDATE, line, updatedRecords);
				}

				else if (section == Section.DELETE)
				{
					if (!listener.handleDeleteRow(decodeRow(fields, line), line, ++removedRecords))
						break;
					
					listener.handlePostRow(OperationType.DELETE, line, removedRecords);
				}
				else
					throw new RuntimeException("Data not allowed in this section");
			}
		}

		if (section != Section.INSERT && section != Section.UPDATE && section != Section.DELETE)
			throw new RuntimeException("Unexpected EOF encountered");
	}

	//---------------------------------------------------------------------------

	public void close() throws IOException
	{
		br.close();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------
	
	private QueryField decodeField(String line) throws RuntimeException
	{
		String tokens[] = line.split(",");
		
		if (tokens.length < 2)
			throw new RuntimeException("Bad field format");

		String  name    = tokens[0].trim();
		String  type    = tokens[1].trim();
		String  pkey    = (tokens.length >= 3) ? tokens[2].trim() : "N";
		SqlType sqlType = SqlMapper.mapName(type);

		if (!type.equals(sqlType.sName))
			throw new RuntimeException("Column types differ ["+ type +"/"+ sqlType.sName +"]");

		return new QueryField(name, sqlType, pkey.equals("K"));
	}

	//---------------------------------------------------------------------------
	
	private List<Object> decodeRow(List<QueryField> fields, String line) throws ParseException
	{
		FullTokenizer ft = new FullTokenizer(line, "\t");

		if (ft.countTokens() != fields.size())
			throw new RuntimeException("Fields count differs from token count : \n"+ line);

		List<Object> row = new ArrayList<Object>();
		
		for(int i=0; i<ft.countTokens(); i++)
		{
			SqlType sqlType = fields.get(i).getType();
			String  token   = ft.nextToken();

			//--- null is the default for "" or unknown types
			Object obj = null;

			if (token.length() == 0)
				obj = null;
			
			else if (sqlType.isDate())
				obj = Date.valueOf(token);

			else if (sqlType.isTime())
				obj = Time.valueOf(token);

			else if (sqlType.isTimeStamp())
			{
				if (info.getVersion().equals("2") || info.getVersion().equals("1"))
					obj = Timestamp.valueOf(token);
				else
					obj = new Timestamp(parser.parse(token).getTime());
			}

			else if (sqlType.isInteger())
				obj = Long.parseLong(token);

			else if (sqlType.isReal())
				obj = Double.parseDouble(token);

			else if (sqlType.isBoolean())
				obj = token.equals("T");

			else if (sqlType.isBinaryType() || sqlType.isBlob() || sqlType.isClob())
				obj = Codec.decodeBytes(token);

			else if (sqlType.isString() || sqlType.isLongVarChar())
				obj = Codec.decodeString(token);
			
			row.add(obj);
		}

		return row;
	}

	//---------------------------------------------------------------------------
	
	private void handleInfo(DDFInfo info, String line)
	{
		StringTokenizer st = new StringTokenizer(line, "=");
		
		String name = st.nextToken();
		String value= st.nextToken();
		
		if (name.equals("version"))
			info.setVersion(value);
		
		else if (name.equals("table"))
			info.setTable(value);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private BufferedReader   br;
	private DDFInfo          info;
	private SimpleDateFormat parser;
}

//==============================================================================
