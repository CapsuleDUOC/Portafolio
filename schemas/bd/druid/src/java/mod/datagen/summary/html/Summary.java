//==============================================================================
//===
//===   Summary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.summary.html;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.dlib.tools.HtmlLib;
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

	public String getId()       { return "html"; }
	public String getVersion()  { return "1.1";  }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the summary in html";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "HTML"; }
	public boolean isDirectoryBased() { return false;  }
	public boolean hasLargePanel()    { return false;  }

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

			writeFooter(w);
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

		w.write("<HR/>");
		w.write("<H2>"+ encode(tableNode.attrSet.getString("name")) + "</H2>" + LF);

		w.write("<TABLE BORDER=\"1\" CELLPADDING=\"2\" CELLSPACING=\"0\" WIDTH=\"100%\">");
		writeTableHeader(w);

		for(int i=0; i<tableNode.getChildCount(); i++)
			genLine(w, (FieldNode)tableNode.getChild(i));

		w.write("</TABLE>" +LF);
	}

	//---------------------------------------------------------------------------

	private void genLine(Writer w, FieldNode fieldNode)
										 throws IOException
	{
		w.write("<TR>");

		//--- write field name

		w.write("<TD BGCOLOR=\"#E0E0FF\">" +encode(fieldNode.attrSet.getString("name"))+ "</TD>");

		w.write("<TD>" +encode(DataTypeLib.getTypeDef(fieldNode))+ "</TD>");
		w.write("<TD>" +encode(DataTypeLib.getSqlType(fieldNode))+ "</TD>");

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
//				String name = as.getString("name");

				Object obj = fav.getData(id);

				String value = "&nbsp;";

				//------------------------------------------------------------------
				//--- String / Integer case

				if (obj instanceof String || obj instanceof Integer)
				{
					if (!obj.toString().equals(""))
						value = encode(obj.toString());
				}

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					if (((Boolean)obj).booleanValue())	value = "X";
						else 										value = "-";
				}

				//------------------------------------------------------------------
				//--- default

				else
					throw new DruidException(DruidException.INC_STR, "Unknown AttribSet type", obj);

				//------------------------------------------------------------------
				//--- ok, write attrib

				w.write("<TD ALIGN=\"CENTER\">" +value+ "</TD>");
			}
		}

		//--- write DDEquiv

		if (dbaseNode.attrSet.getBool("summUseDDEquiv"))
			w.write("<TD>" + encode(DataTypeLib.getTypeInfo(fieldNode).ddEquiv)+ "</TD>");

		w.write("</TR>" +LF);
	}

	//---------------------------------------------------------------------------

	private void writeHeader(Writer w) throws IOException
	{
		int    build = projNode.attrSet.getInt("build");
		String dbName= dbaseNode.attrSet.getString("name");

		w.write("<HTML><HEAD></HEAD><BODY>" +LF);
		w.write("<H1>Summary File for Database : " + encode(dbName) + " (Build " + build + ") </H1>"+LF);
		w.write("Date of creation: " + Util.getCurrentDate() + "<BR>" +LF);
	}

	//---------------------------------------------------------------------------

	private void writeTableHeader(Writer w) throws IOException
	{
		w.write("<TR BGCOLOR=\"#CCCCFF\">");
		w.write("<TH>Field</TH>");
		w.write("<TH>Type</TH>");
		w.write("<TH>Sql type</TH>");

		FieldAttribs fa = dbaseNode.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			//--- check if attrib must be included in the data dictionary

			if (as.getBool("useInSumm"))
				w.write("<TH>" + encode(as.getString("name")) + "</TH>");
		}

		//--- write DDEquiv

		if (dbaseNode.attrSet.getBool("summUseDDEquiv"))
			w.write("<TH>DD Equiv</TH>");

		w.write("</TR>");
	}

	//---------------------------------------------------------------------------

	private void writeFooter(Writer w) throws IOException
	{
		w.write("</BODY></HTML>");
	}

	//---------------------------------------------------------------------------

	private String encode(String s)
	{
		return HtmlLib.makeGood(HtmlLib.encode(s));
	}
}

//==============================================================================
