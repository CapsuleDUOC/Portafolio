//==============================================================================
//===
//===   DataDictionary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.datadict.ascii;

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
import druid.interfaces.DatadictGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class DataDictionary implements DatadictGenModule
{
	private DatabaseNode dbaseNode;
	private ProjectNode  projNode;
	private Logger       logger;

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
		return "Generates the data dictionary in ascii form";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "Ascii"; }
	public boolean isDirectoryBased() { return false;   }
	public boolean hasLargePanel()    { return false;   }

	//---------------------------------------------------------------------------
	//---
	//---   Data Dictionary File Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		dbaseNode = dbNode;
		projNode  = (ProjectNode)dbNode.getParent();
		logger    = l;

		logger.logHeader("Data Dictionary File");

		String sOutput  = dbNode.modsConfig.getValue(this, "output");

		try
		{
			Writer w = new FileWriter(sOutput);

			//--- write header ---

			writeHeader(w);

			//--- write tables ---

			Vector tables = dbaseNode.getObjects(TableNode.class);

			for(int i=0; i<tables.size(); i++)
				genDDTable(w, (TableNode)tables.elementAt(i));

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

	private void genDDTable(Writer w, TableNode tableNode) throws IOException
	{
		String name = tableNode.attrSet.getString("name");

		//--- write base table

		w.write("#== " + name + " ");

		for(int i=0; i<80-5-name.length(); i++) w.write("=");

		w.write(LF);
		w.write(LF);

		for(int i=0; i<tableNode.getChildCount(); i++)
			genLine(w, name, (FieldNode)tableNode.getChild(i));

		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void genLine(Writer w, String tableName, FieldNode fieldNode)
										 throws IOException
	{
		//--- write table & field names

		w.write(Util.pad("'" + tableName + "', ", 15));
		w.write(Util.pad("'" + fieldNode.attrSet.getString("name") + "'", 15));

		//--- write DDEquiv

		if (dbaseNode.attrSet.getBool("ddUseDDEquiv"))
		{
			TypeInfo ti = DataTypeLib.getTypeInfo(fieldNode);

			w.write(Util.pad(", '" + ti.ddEquiv + "'", 15));
		}

		//--- write FieldAttribs

		FieldAttribs fa  = dbaseNode.fieldAttribs;
		AttribSet    fav = fieldNode.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			//--- check if attrib must be included in the data dictionary

			if (as.getBool("useInDD"))
			{
				String id = "" + as.getInt("id");

				Object obj = fav.getData(id);

				String value = "";

				//------------------------------------------------------------------
				//--- String case

				if (obj instanceof String)
				{
					value = (String) obj;

					if (value.equals(""))       value = "''";
					if (!value.startsWith("'")) value = "'" + value;
					if (!value.endsWith("'"))   value = value + "'";
				}

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					boolean b = ((Boolean)obj).booleanValue();

					value = "'" + (b ? "y" : "n") + "'";
				}

				//------------------------------------------------------------------
				//--- Integer case

				else if (obj instanceof Integer)
					value = "" + obj;

				//------------------------------------------------------------------
				//--- default

				else
					throw new DruidException(DruidException.INC_STR, "Unknown AttribSet type", obj);

				//------------------------------------------------------------------
				//--- ok, write attrib

				w.write(Util.pad(", " + value, 10));
			}
		}

		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void writeHeader(Writer w) throws IOException
	{
		int    build = projNode.attrSet.getInt("build");
		String dbName= dbaseNode.attrSet.getString("name");

		writeSeparator(w);
		w.write("#==   Data Dictionary File for Database : " + dbName + " (Build " + build + ")"+LF);
		w.write("#=="+LF);
		w.write("#== Format: table, field" + getDDFields() + "" + LF);
		writeSeparator(w);
		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void writeSeparator(Writer w) throws IOException
	{
		w.write("#=======================================" +
				  "========================================" + LF);
	}

	//---------------------------------------------------------------------------

	private String getDDFields()
	{
		StringBuffer res = new StringBuffer();

		if (dbaseNode.attrSet.getBool("ddUseDDEquiv"))
			res.append(", ddEquiv");

		//--- write FieldAttribs

		FieldAttribs fa = dbaseNode.fieldAttribs;

		for (int i = 0; i < fa.size(); i++) {
			AttribSet as = fa.get(i);

			if (as.getBool("useInDD"))
				res.append(", ").append(as.getString("name"));
		}
		return res.toString();
	}
}

//==============================================================================
