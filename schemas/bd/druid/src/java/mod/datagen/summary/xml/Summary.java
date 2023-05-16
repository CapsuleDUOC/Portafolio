//==============================================================================
//===
//===   Summary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.summary.xml;

import java.io.IOException;
import java.util.Vector;

import org.dlib.tools.Util;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlWriter;

import druid.core.AttribSet;
import druid.core.DataTypeLib;
import druid.core.DruidException;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.interfaces.SummaryGenModule;

//==============================================================================

public class Summary implements SummaryGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "xml"; }
	public String getVersion()  { return "1.0";    }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the summary in xml form.\n"+
				 "The file can then be used with an XSL stylesheet";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "XML"; }
	public boolean isDirectoryBased() { return false; }
	public boolean hasLargePanel()    { return false; }

	//---------------------------------------------------------------------------
	//---
	//---   Summary File Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger logger, DatabaseNode dbNode)
	{
		logger.logHeader("Summary File");

		String sOutput  = dbNode.modsConfig.getValue(this, "output");

		try
		{
			new XmlWriter().write(sOutput, build(dbNode));

			logger.log(Logger.INFO, "");
			logger.log(Logger.INFO, "Done.");
		}
		catch(IOException e)
		{
			logger.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
		}
	}

	//---------------------------------------------------------------------------

	private XmlDocument build(DatabaseNode dbNode)
	{
		XmlElement elDB = new XmlElement("database");

		elDB.setAttribute(new XmlAttribute("name",  dbNode.attrSet.getString("name")));
		elDB.setAttribute(new XmlAttribute("build", dbNode.getProjectNode().attrSet.getInt("build")));
		elDB.setAttribute(new XmlAttribute("date",  Util.getCurrentDate()));

		//--- extract all tables from db

		Vector tables = dbNode.getObjects(TableNode.class);

		for(int i=0; i<tables.size(); i++)
			elDB.addChild(genTable((TableNode)tables.elementAt(i)));

		return new XmlDocument(elDB);
	}

	//---------------------------------------------------------------------------

	private XmlElement genTable(TableNode node)
	{
		XmlElement elTable = new XmlElement("table");
		elTable.setAttribute(new XmlAttribute("name", node.attrSet.getString("name")));

		//--- generate table element

		for(int i=0; i<node.getChildCount(); i++)
			elTable.addChild(genField((FieldNode) node.getChild(i)));

		return elTable;
	}

	//---------------------------------------------------------------------------

	private XmlElement genField(FieldNode node)
	{
		XmlElement elField = new XmlElement("field");

		elField.setAttribute(new XmlAttribute("name",    node.attrSet.getString("name")));
		elField.setAttribute(new XmlAttribute("type",    DataTypeLib.getTypeDef(node)));
		elField.setAttribute(new XmlAttribute("sqlType", DataTypeLib.getSqlType(node)));

		//--- write FieldAttribs

		DatabaseNode dbNode = node.getDatabase();

		if (dbNode.attrSet.getBool("summUseDDEquiv"))
			elField.setAttribute(new XmlAttribute("ddEquiv", DataTypeLib.getTypeInfo(node).ddEquiv));

		FieldAttribs fa  = dbNode.fieldAttribs;
		AttribSet    fav = node.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			//--- check if attrib must be included in the data dictionary

			if (as.getBool("useInSumm"))
			{
				Object obj = fav.getData(as.getInt("id") +"");

				XmlElement elAttr = new XmlElement("attrib");
				elAttr.setAttribute(new XmlAttribute("name", as.getString("name")));

				//------------------------------------------------------------------
				//--- String case

				if (obj instanceof String)
				{
					if (!obj.toString().equals(""))
					{
						elAttr.setAttribute(new XmlAttribute("value", (String) obj));
						elField.addChild(elAttr);
					}
				}

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					if (((Boolean)obj).booleanValue())
						elField.addChild(elAttr);
				}

				//------------------------------------------------------------------
				//--- Integer case

				else if (obj instanceof Integer)
				{
					if (!obj.toString().equals(""))
					{
						elAttr.setAttribute(new XmlAttribute("value", obj.toString()));
						elField.addChild(elAttr);
					}
				}

				//------------------------------------------------------------------
				//--- default

				else
					throw new DruidException(DruidException.INC_STR, "Unknown AttribSet type", obj);
			}
		}

		return elField;
	}
}

//==============================================================================
