//==============================================================================
//===
//===   Saver
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.io;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlElement;

import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.ProjectNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.DataTypes;
import druid.data.datatypes.Domain;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.data.er.LegendColor;

//==============================================================================

class Saver implements ElementNames
{
	//---------------------------------------------------------------------------
	//---
	//--- Saver
	//---
	//---------------------------------------------------------------------------

	static XmlElement save(ProjectNode projNode)
	{
		XmlElement elRoot = new XmlElement(PROJECT);

		elRoot.setAttribute(new XmlAttribute(ATTR_VERSION, ProjectManager.VERSION));

		elRoot.addChild(IOLib.attribsToXml(projNode.attrSet));

		for(int i=0; i<projNode.getChildCount(); i++)
			elRoot.addChild(databaseToXml((DatabaseNode) projNode.getChild(i)));

		return elRoot;
	}

	//---------------------------------------------------------------------------

	private static XmlElement databaseToXml(DatabaseNode dbNode)
	{
		XmlElement elDb = new XmlElement(DATABASE);

		AttribSet as = dbNode.attrSet.duplicate();

		elDb.addChild(IOLib.attribsToXml(as));

		elDb.addChild(IOLib.attribsToXml   (REVISIONS,    dbNode.revisions));
		elDb.addChild(IOLib.attribsToXml   (FIELDATTRIBS, dbNode.fieldAttribs));
		elDb.addChild(IOLib.attribsToXml   (DBVARS,       dbNode.dbVars));
		elDb.addChild(IOLib.modsConfigToXml(MODSCONFIG,   dbNode.modsConfig));

		elDb.addChild(modsUsageToXml(dbNode.modsUsage.htModules));
		elDb.addChild(datatypesToXml(dbNode.dataTypes));
		elDb.addChild(IOLib.treeToXml(dbNode.sqlQueries, SQLQUERIES, SQLQUERY));
		elDb.addChild(erViewsToXml(dbNode.erViews));
		elDb.addChild(objectsToXml(dbNode));

		IOLib.docsToXml(dbNode, elDb);

		return elDb;
	}

	//---------------------------------------------------------------------------

	private static XmlElement modsUsageToXml(Hashtable ht)
	{
		XmlElement el = new XmlElement(MODSUSAGE);

		for(Enumeration e=ht.keys(); e.hasMoreElements();)
		{
			String key   = (String) e.nextElement();

			XmlElement elMod = new XmlElement(MODULE);

			elMod.setAttribute(new XmlAttribute("name",  key));

			el.addChild(elMod);
		}

		return el;
	}

	//---------------------------------------------------------------------------
	//--- DataTypes
	//---------------------------------------------------------------------------

	private static XmlElement datatypesToXml(DataTypes node)
	{
		XmlElement elRoot = new XmlElement(DATATYPES);

		//------------------------------------------------------------------------
		//--- const datatypes

		XmlElement elConFolder = new XmlElement(CONSTFOLDER);

		TreeViewNode conFolder = node.getChild(0);

		for(int i=0; i<conFolder.getChildCount(); i++)
		{
			AbstractType type = (AbstractType) conFolder.getChild(i);

			elConFolder.addChild(typeToXml(type, true));
		}

		//------------------------------------------------------------------------
		//--- var datatypes

		XmlElement elVarFolder = new XmlElement(VARFOLDER);

		TreeViewNode varFolder = node.getChild(1);

		for(int i=0; i<varFolder.getChildCount(); i++)
		{
			AbstractType type = (AbstractType) varFolder.getChild(i);

			elVarFolder.addChild(typeToXml(type, false));
		}

		//------------------------------------------------------------------------
		//--- exit

		elRoot.addChild(elConFolder);
		elRoot.addChild(elVarFolder);

		return elRoot;
	}

	//---------------------------------------------------------------------------

	private static XmlElement typeToXml(AbstractType type, boolean putDomain)
	{
		XmlElement elType = new XmlElement(TYPE);

		elType.addChild(IOLib.attribsToXml(type.attrSet));

		IOLib.docsToXml(type, elType);

		if (putDomain)
			elType.addChild(domainToXml(type.domain));

		//--- add aliases

		XmlElement elAliases = new XmlElement(ALIASES);

		for(int i=0; i<type.getChildCount(); i++)
		{
			AbstractType alias = (AbstractType) type.getChild(i);

			XmlElement elAlias = new XmlElement(ALIAS);

			elAlias.addChild(IOLib.attribsToXml(alias.attrSet));
			elAlias.addChild(domainToXml(alias.domain));

			IOLib.docsToXml(alias, elAlias);

			elAliases.addChild(elAlias);
		}

		elType.addChild(elAliases);

		return elType;
	}

	//---------------------------------------------------------------------------

	private static XmlElement domainToXml(Domain domain)
	{
		XmlElement elDomain = new XmlElement(DOMAIN);

		elDomain.addChild(IOLib.attribsToXml(domain.attrSet));
		elDomain.addChild(IOLib.attribsToXml(domain.valueList));

		return elDomain;
	}

	//---------------------------------------------------------------------------
	//--- ErViews
	//---------------------------------------------------------------------------

	private static XmlElement erViewsToXml(ErView node)
	{
		XmlElement elRoot = new XmlElement(ERVIEWS);

		for(int i=0; i<node.getChildCount(); i++)
		{
			ErView erView = (ErView) node.getChild(i);

			XmlElement elView = new XmlElement(ERVIEW);

			elView.addChild(IOLib.attribsToXml(erView.attrSet));
			elView.addChild(IOLib.modsConfigToXml(MODSCONFIG, erView.modsConfig));
			IOLib.docsToXml(erView, elView);

			elRoot.addChild(elView);

			//---------------------------------------------------------------------
			//--- add legend

			XmlElement elLegend = new XmlElement(LEGEND);

			elLegend.addChild(IOLib.attribsToXml(erView.legend.attrSet));

			for(int j=0; j<erView.legend.getChildCount(); j++)
			{
				//--- add colors

				LegendColor legCol = (LegendColor) erView.legend.getChild(j);

				XmlElement elColor = new XmlElement(COLOR);

				elColor.addChild(IOLib.attribsToXml(legCol.attrSet));

				XmlElement elValues = new XmlElement(VALUES);

				elValues.addChild(getColor(COL_NAME,   legCol.colName));
				elValues.addChild(getColor(COL_NAMEBG, legCol.colNameBg));
				elValues.addChild(getColor(COL_TEXT,   legCol.colText));
				elValues.addChild(getColor(COL_TEXTBG, legCol.colTextBg));
				elValues.addChild(getColor(COL_BG,     legCol.colBg));
				elValues.addChild(getColor(COL_BORDER, legCol.colBorder));

				elColor.addChild(elValues);

				elLegend.addChild(elColor);
			}

			elView.addChild(elLegend);

			//---------------------------------------------------------------------
			//--- add entities

			for(int j=0; j<erView.getChildCount(); j++)
			{
				ErEntity erEntity = (ErEntity) erView.getChild(j);

				XmlElement elEnt = new XmlElement(ERENTITY);

				elEnt.addChild(IOLib.attribsToXml(erEntity.attrSet));
				elEnt.addChild(IOLib.modsConfigToXml(MODSCONFIG, erEntity.modsConfig));
				IOLib.docsToXml(erEntity, elEnt);

				XmlElement elTables = new XmlElement(TABLES);

				for(int l=0; l<erEntity.getTableNum(); l++)
				{
					XmlElement elTable = new XmlElement(ERENT_TABLE);

					int id = erEntity.getTableAt(l);
					elTable.setAttribute(new XmlAttribute(ATTR_ID, id));

					elTables.addChild(elTable);
				}

				elEnt.addChild(elTables);
				elView.addChild(elEnt);
			}
		}

		return elRoot;
	}

	//---------------------------------------------------------------------------

	private static XmlElement getColor(String name, Color c)
	{
		XmlElement elCol = new XmlElement(name);

		elCol.setAttribute(new XmlAttribute(ATTR_RED,   c.getRed()));
		elCol.setAttribute(new XmlAttribute(ATTR_GREEN, c.getGreen()));
		elCol.setAttribute(new XmlAttribute(ATTR_BLUE,  c.getBlue()));

		return elCol;
	}

	//---------------------------------------------------------------------------
	//--- Objects (tables, fields, folders etc...)
	//---------------------------------------------------------------------------

	private static XmlElement objectsToXml(AbstractNode node)
	{
		XmlElement elNode = null;

		if (node instanceof DatabaseNode)
		{
			elNode = new XmlElement(OBJECTS);
		}
		else
		{
			if (node instanceof FolderNode)
				elNode = new XmlElement(FOLDER);

			else if (node instanceof TableNode)
			{
				elNode = new XmlElement(TABLE);
				elNode.addChild(IOLib.attribsToXml(TABLEVARS, ((TableNode)node).tableVars));
				elNode.addChild(IOLib.treeToXml(((TableNode)node).triggers, TRIGGERS, TRIGGER));
				elNode.addChild(IOLib.treeToXml(((TableNode)node).rules,    RULES,    RULE));
			}

			else if (node instanceof FieldNode)
			{
				elNode = new XmlElement(FIELD);
				elNode.addChild(IOLib.attribsToXml(FATTRIBS, ((FieldNode)node).fieldAttribs));
			}

			else if (node instanceof ViewNode)
				elNode = new XmlElement(VIEW);

			else if (node instanceof ProcedureNode)
				elNode = new XmlElement(PROCEDURE);

			else if (node instanceof FunctionNode)
				elNode = new XmlElement(FUNCTION);

			else if (node instanceof SequenceNode)
				elNode = new XmlElement(SEQUENCE);

			else if (node instanceof NotesNode)
				elNode = new XmlElement(NOTES);

			else
				throw new DruidException(DruidException.INC_STR, "Unknown node type", node);

			elNode.addChild(IOLib.attribsToXml(node.attrSet));
			elNode.addChild(IOLib.modsConfigToXml(MODSCONFIG, node.modsConfig));
			IOLib.docsToXml(node, elNode);
		}

		for(int i=0; i<node.getChildCount(); i++)
			elNode.addChild(objectsToXml((AbstractNode) node.getChild(i)));

		return elNode;
	}
}

//==============================================================================
