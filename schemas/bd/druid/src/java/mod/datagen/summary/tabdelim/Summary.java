//==============================================================================
//===
//===   Summary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.summary.tabdelim;

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

	public String getId()       { return "tabDel"; }
	public String getVersion()  { return "1.0";    }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the summary in ascii tab delimited form\n"+
				 "The file can then be imported into a spreadsheet program";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "Tab delimited"; }
	public boolean isDirectoryBased() { return false;           }
	public boolean hasLargePanel()    { return false;           }

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
		//--- write base table

		w.write(tableNode.attrSet.getString("name") + LF + LF);

		for(int i=0; i<tableNode.getChildCount(); i++)
			genLine(w, (FieldNode)tableNode.getChild(i));

		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void genLine(Writer w, FieldNode fieldNode)
										 throws IOException
	{
		//--- write field name

		w.write(fieldNode.attrSet.getString("name"));

		w.write("\t" + DataTypeLib.getTypeDef(fieldNode));
		w.write("\t" + DataTypeLib.getSqlType(fieldNode));

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
						value = name + "(" + obj + ")";
				}

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					if (((Boolean)obj).booleanValue())
						value = name;
				}

				//------------------------------------------------------------------
				//--- Integer case

				else if (obj instanceof Integer)
				{
					if (!obj.toString().equals(""))
						value = name + "(" + obj + ")";
				}

				//------------------------------------------------------------------
				//--- default

				else
					throw new DruidException(DruidException.INC_STR, "Unknown AttribSet type", obj);

				//------------------------------------------------------------------
				//--- ok, write attrib

				w.write("\t" + value);
			}
		}

		//--- write DDEquiv

		if (dbaseNode.attrSet.getBool("summUseDDEquiv"))
			w.write("\t" + DataTypeLib.getTypeInfo(fieldNode).ddEquiv);

		w.write(LF);
	}

	//---------------------------------------------------------------------------

	private void writeHeader(Writer w) throws IOException
	{
		int    build = projNode.attrSet.getInt("build");
		String dbName= dbaseNode.attrSet.getString("name");

		w.write("Summary File for Database : " + dbName + " (Build " + build + ")"+LF);
		w.write(LF);
		w.write("Date of creation: " + Util.getCurrentDate() + LF);
		w.write(LF);
	}
}

//==============================================================================
