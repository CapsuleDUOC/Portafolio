//==============================================================================
//===
//===   DataDictionary
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.datadict.xml;

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
import druid.data.datatypes.TypeInfo;
import druid.interfaces.DatadictGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class DataDictionary implements DatadictGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "xml"; }
	public String getVersion()  { return "1.0";   }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the data dictionary in xml";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	public String  getFormat()        { return "XML"; }
	public boolean isDirectoryBased() { return false; }
	public boolean hasLargePanel()    { return false; }

	//---------------------------------------------------------------------------
	//---
	//---   Data Dictionary File Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger logger, DatabaseNode dbNode)
	{
		logger.logHeader("Data Dictionary File");

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
			elDB.addChild(genTable((TableNode) tables.elementAt(i)));

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

	private XmlElement genField(FieldNode fieldNode)
	{
		XmlElement elField = new XmlElement("field");
		elField.setAttribute(new XmlAttribute("name", fieldNode.attrSet.getString("name")));

		//--- write DDEquiv

		DatabaseNode dbNode = fieldNode.getDatabase();

		if (dbNode.attrSet.getBool("ddUseDDEquiv"))
		{
			TypeInfo ti = DataTypeLib.getTypeInfo(fieldNode);

			elField.addChild(new XmlElement("ddEquiv", ti.ddEquiv));
		}

		//--- write FieldAttribs

		FieldAttribs  fa  = dbNode.fieldAttribs;
		AttribSet     fav = fieldNode.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			//--- check if attrib must be included in the data dictionary

			if (as.getBool("useInDD"))
			{
				XmlElement elAttr = new XmlElement("attrib");

				elAttr.setAttribute(new XmlAttribute("name", as.getString("name")));

				elField.addChild(elAttr);

				//---

				Object obj = fav.getData(as.getInt("id") +"");

				//------------------------------------------------------------------
				//--- String case

				if (obj instanceof String)
					elAttr.setAttribute(new XmlAttribute("value", (String) obj));

				//------------------------------------------------------------------
				//--- Boolean case

				else if (obj instanceof Boolean)
				{
					boolean b = ((Boolean)obj).booleanValue();

					elAttr.setAttribute(new XmlAttribute("value", b));
				}

				//------------------------------------------------------------------
				//--- Integer case

				else if (obj instanceof Integer)
					elAttr.setAttribute(new XmlAttribute("value", obj +""));

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
