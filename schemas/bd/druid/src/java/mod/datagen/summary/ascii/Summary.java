//==============================================================================
//===
//===   Summary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.summary.ascii;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.dlib.tools.Util;

import druid.core.AttribSet;
import druid.core.DataTypeLib;
import druid.core.DruidException;
import druid.core.config.Config;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.ProjectNode;
import druid.data.TableNode;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.interfaces.SummaryGenModule;

//==============================================================================

public class Summary implements SummaryGenModule
{
	private DatabaseNode dbaseNode;
	private ProjectNode  projNode;

	private String LF = Config.os.lineSep;

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "ascii"; }
	public String getVersion()  { return "1.0";   }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the summary in simple ascii form";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "Ascii"; }
	public boolean isDirectoryBased() { return false;   }
	public boolean hasLargePanel()    { return false;   }

	//---------------------------------------------------------------------------
	//---
	//---   Summary File Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger logger, DatabaseNode dbNode)
	{
		dbaseNode = dbNode;
		projNode  = (ProjectNode)dbNode.getParent();

		logger.logHeader("Summary File");

		String sOutput  = dbNode.modsConfig.getValue(this, "output");

		try
		{
			Writer w = new FileWriter(sOutput);

			//--- write header ---

			writeHeader(w);

			//--- write tables ---

			Vector tables = dbaseNode.getObjects(TableNode.class);

			for(int i=0; i<tables.size(); i++)
				genSummTable(w, (TableNode)tables.elementAt(i));

			//--- write footer ---

			writeSeparator(w);
			w.close();

			logger.log(Logger.INFO, "");
			logger.log(Logger.INFO, "Done.");
		}
		catch(IOException e)
		{
			logger.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
		}
	}

	//---------------------------------------------------------------------------

	private void genSummTable(Writer w, TableNode tableNode) throws IOException
	{
		String name = tableNode.attrSet.getString("name");

		//--- write base table

		w.write("#== " + name + " ");

		for(int i=0; i<80-5-name.length(); i++) w.write("=");

		w.write(LF);
		w.write(LF);

		for(int i=0; i<tableNode.getChildCount(); i++)
			genLine(w, (FieldNode)tableNode.getChild(i));

		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void genLine(Writer w, FieldNode fieldNode)
										 throws IOException
	{
		//--- write field name

		w.write(Util.pad(fieldNode.attrSet.getString("name"), 15));

		//--- write field type

		String typeDef = DataTypeLib.getTypeDef(fieldNode);
		w.write(" | " + Util.pad(typeDef, 15));

		//--- write field sql type

		String sqlDef = DataTypeLib.getSqlType(fieldNode);
		w.write(" | " + Util.pad(sqlDef, 15));

		//--- write FieldAttribs

		FieldAttribs fa  = dbaseNode.fieldAttribs;
		AttribSet    fav = fieldNode.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			//--- check if attrib must be included in the data dictionary

			if (as.getBool("useInSumm"))
			{
				String id   = "" + as.getInt("id");
				String name = as.getString("name");

				Object obj = fav.getData(id);

				String value = "";

				//------------------------------------------------------------------
				//--- String case

				if (obj instanceof String)
				{
					if (!obj.toString().equals(""))
						value = Util.pad(name + "(" + obj + ")", 15);
					else
						value = Util.pad("", 15);
				}

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					boolean b = ((Boolean)obj).booleanValue();

					if (b)	value = name;
						else 	value = Util.pad("", name.length());
				}

				//------------------------------------------------------------------
				//--- Integer case

				else if (obj instanceof Integer)
				{
					if (!obj.toString().equals(""))
						value = Util.pad(name + "(" + obj + ")", 15);
					else
						value = Util.pad("", 15);
				}

				//------------------------------------------------------------------
				//--- default

				else
					throw new DruidException(DruidException.INC_STR, "Unknown AttribSet type", obj);

				//------------------------------------------------------------------
				//--- ok, write attrib

				w.write(" | " + value);
			}
		}

		//--- write DDEquiv

		if (dbaseNode.attrSet.getBool("summUseDDEquiv"))
		{
			TypeInfo ti = DataTypeLib.getTypeInfo(fieldNode);

			w.write(" | " + Util.pad(ti.ddEquiv, 10));
		}

		w.write(" |" + LF);
	}

	//---------------------------------------------------------------------------

	private void writeHeader(Writer w) throws IOException
	{
		int    build = projNode.attrSet.getInt("build");
		String dbName= dbaseNode.attrSet.getString("name");

		writeSeparator(w);
		w.write("#==   Summary File for Database : " + dbName + " (Build " + build + ")"+LF);
		w.write("#==" + LF);
		w.write("#==   Date of creation: " + Util.getCurrentDate() + LF);
		writeSeparator(w);
		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void writeSeparator(Writer w) throws IOException
	{
		w.write("#=======================================" +
				  "========================================" + LF);
	}
}

//==============================================================================
