//==============================================================================
//===
//===   Loader
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.io;

import java.awt.Color;
import java.util.List;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.xml.XmlElement;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.ModulesUsage;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.ProjectNode;
import druid.data.SequenceNode;
import druid.data.SqlQuery;
import druid.data.TableNode;
import druid.data.TableRule;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.Domain;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.data.er.LegendColor;

//==============================================================================

class Loader implements ElementNames
{
	//---------------------------------------------------------------------------
	//---
	//--- Loader
	//---
	//---------------------------------------------------------------------------

	public static void load(XmlElement elProj, ProjectNode projNode)
	{
		IOLib.xmlToAttribSet(elProj.getChild(ATTRIBS), projNode.attrSet);

		List listDb = elProj.getChildren(DATABASE);

		for(int i=0; i<listDb.size(); i++)
		{
			XmlElement   elDb   = (XmlElement)listDb.get(i);
			DatabaseNode dbNode = new DatabaseNode();

			IOLib.xmlToAttribSet (elDb.getChild(ATTRIBS),    dbNode.attrSet);
			IOLib.xmlToDocs      (elDb.getChild(DOCS),       dbNode);
			IOLib.xmlToModsConfig(elDb.getChild(MODSCONFIG), dbNode.modsConfig);

			IOLib.xmlToAttribList(elDb.getChild(REVISIONS)   .getChild(LIST), dbNode.revisions);
			IOLib.xmlToAttribList(elDb.getChild(FIELDATTRIBS).getChild(LIST), dbNode.fieldAttribs);

			XmlElement elDbVars = elDb.getChild(DBVARS);

			if (elDbVars != null)
				IOLib.xmlToAttribList(elDbVars.getChild(LIST), dbNode.dbVars);

			xmlToModsUsage(elDb.getChild(MODSUSAGE),   dbNode.modsUsage);
			xmlToDataTypes(elDb.getChild(DATATYPES),   dbNode);
			xmlToErViews(elDb.getChild(ERVIEWS),       dbNode);

			//--- START HACK ------------------------------------------------------------------
			//--- This hack loads old tablespaces into the oracle module

			XmlElement elTS = elDb.getChild("tablespaces");

			if (elTS != null)
			{
				druid.interfaces.SqlGenModule sqlMod = (druid.interfaces.SqlGenModule)
																	druid.core.modules.ModuleManager.getModule(
																	druid.interfaces.SqlGenModule.class, "oraSql");

				AttribList al =new AttribList();
				al.addAttrib("id",    0);
				al.addAttrib("name", "");

				al = dbNode.modsConfig.getAttribList(sqlMod, "tablespaces", al);

				IOLib.xmlToAttribList(elTS.getChild(LIST),  al);
				dbNode.modsUsage.addModule(sqlMod);
			}

			//--- END HACK   ------------------------------------------------------------------

			IOLib.xmlToTree(elDb.getChild(SQLQUERIES), dbNode.sqlQueries, SQLQUERY, SqlQuery.class);

			List listObj = elDb.getChild(OBJECTS).getChildren();

			for(int j=0; j<listObj.size(); j++)
				dbNode.addChild(xmlToObjects((XmlElement) listObj.get(j), dbNode.fieldAttribs));

			dbNode.setText(dbNode.attrSet.getString("name"));
			projNode.addChild(dbNode);
		}
	}

	//---------------------------------------------------------------------------

	private static void xmlToDataTypes(XmlElement elDataTypes, DatabaseNode dbNode)
	{
		XmlElement elConFolder = elDataTypes.getChild(CONSTFOLDER);
		XmlElement elVarFolder = elDataTypes.getChild(VARFOLDER);

		if (elConFolder != null)
			xmlToType(elConFolder, dbNode.dataTypes.getChild(0), false);

		if (elVarFolder != null)
			xmlToType(elVarFolder, dbNode.dataTypes.getChild(1),  true);
	}

	//---------------------------------------------------------------------------

	private static void xmlToType(XmlElement elFolder, TreeViewNode node, boolean variable)
	{
		List list = elFolder.getChildren(TYPE);

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elType = (XmlElement) list.get(i);

			AbstractType type;

			if (!variable) type = new ConstDataType();
				else 			type = new VarDataType();

			XmlElement elAttribs = elType.getChild(ATTRIBS);
			XmlElement elDomain  = elType.getChild(DOMAIN);

			IOLib.xmlToAttribSet(elAttribs, type.attrSet);

			if (elDomain != null)
				xmlToDomain(elDomain, type.domain);

			IOLib.xmlToDocs(elType.getChild(DOCS), type);

			//--- handle aliases

			XmlElement elAliases = elType.getChild(ALIASES);

			if (elAliases != null)
			{
				List listAliases = elAliases.getChildren(ALIAS);

				for(int j=0; j<listAliases.size(); j++)
				{
					XmlElement elAlias  = (XmlElement) listAliases.get(j);
					XmlElement elAlAttr = elAlias.getChild(ATTRIBS);
					XmlElement elAlDom  = elAlias.getChild(DOMAIN);

					AbstractType alias;

					if (!variable) alias = new ConstAlias();
						else 			alias = new VarAlias();

					IOLib.xmlToAttribSet(elAlAttr, alias.attrSet);

					if (elAlDom != null)
						xmlToDomain(elAlDom, alias.domain);

					IOLib.xmlToDocs(elAlias.getChild(DOCS), alias);

					alias.setText(alias.attrSet.getString("name"));
					type.addChild(alias);
				}
			}

			type.setText(type.attrSet.getString("name"));
			node.addChild(type);
		}
	}

	//---------------------------------------------------------------------------

	private static void xmlToDomain(XmlElement elDomain, Domain domain)
	{
		XmlElement elAttribs = elDomain.getChild(ATTRIBS);
		XmlElement elList    = elDomain.getChild(LIST);

		IOLib.xmlToAttribSet (elAttribs, domain.attrSet);
		IOLib.xmlToAttribList(elList,    domain.valueList);
	}

	//---------------------------------------------------------------------------

	private static void xmlToErViews(XmlElement elErViews, DatabaseNode dbNode)
	{
		List list = elErViews.getChildren(ERVIEW);

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elErView  = (XmlElement) list.get(i);
			XmlElement elAttribs = elErView.getChild(ATTRIBS);
			XmlElement elLegend  = elErView.getChild(LEGEND);

			ErView node = new ErView();

			IOLib.xmlToDocs      (elErView.getChild(DOCS), node);
			IOLib.xmlToModsConfig(elErView.getChild(MODSCONFIG), node.modsConfig);
			IOLib.xmlToAttribSet (elAttribs, node.attrSet);

			//---------------------------------------------------------------------
			//--- handle legend

			if (elLegend != null)
			{
				//--- handle attribs

				XmlElement elLegAttr = elLegend.getChild(ATTRIBS);

				IOLib.xmlToAttribSet(elLegAttr, node.legend.attrSet);

				//--- handle colors

				List listCol = elLegend.getChildren(COLOR);

				if (listCol.size() != 0)
					node.legend.remove(0);

				for(int j=0; j<listCol.size(); j++)
				{
					XmlElement  elLegCol = (XmlElement) listCol.get(j);
					LegendColor legCol   = new LegendColor();

					XmlElement elColAttribs = elLegCol.getChild(ATTRIBS);
					XmlElement elColValues  = elLegCol.getChild(VALUES);

					IOLib.xmlToAttribSet(elColAttribs, legCol.attrSet);

					if (elColValues != null)
					{
						legCol.colName   = getColor(COL_NAME,   elColValues);
						legCol.colNameBg = getColor(COL_NAMEBG, elColValues);
						legCol.colText   = getColor(COL_TEXT,   elColValues);
						legCol.colTextBg = getColor(COL_TEXTBG, elColValues);
						legCol.colBg     = getColor(COL_BG,     elColValues);
						legCol.colBorder = getColor(COL_BORDER, elColValues);
					}

					legCol.setText(legCol.attrSet.getString("name"));
					node.legend.addChild(legCol);
				}
			}

			//---------------------------------------------------------------------
			//--- handle entities

			List listEnt = elErView.getChildren(ERENTITY);

			for(int j=0; j<listEnt.size(); j++)
			{
				XmlElement elEntity = (XmlElement) listEnt.get(j);
				ErEntity   erEntity = new ErEntity();

				XmlElement elEntAttr = elEntity.getChild(ATTRIBS);
				XmlElement elTables  = elEntity.getChild(TABLES);

				IOLib.xmlToDocs(elEntity.getChild(DOCS), erEntity);
				IOLib.xmlToModsConfig(elEntity.getChild(MODSCONFIG), erEntity.modsConfig);

				IOLib.xmlToAttribSet(elEntAttr, erEntity.attrSet);

				if (elTables != null)
				{
					List listTables = elTables.getChildren(ERENT_TABLE);

					for(int k=0; k<listTables.size(); k++)
					{
						XmlElement elTable = (XmlElement) listTables.get(k);

						String id = elTable.getAttributeValue(ATTR_ID);

						erEntity.addTable(Integer.parseInt(id));
					}
				}

				erEntity.setText(erEntity.attrSet.getString("name"));
				node.addChild(erEntity);
			}

			node.setText(node.attrSet.getString("name"));
			dbNode.erViews.addChild(node);
		}
	}

	//---------------------------------------------------------------------------

	private static Color getColor(String name, XmlElement el)
	{
		XmlElement elColor = el.getChild(name);

		if (elColor == null)
			return Color.black;

		String red   = elColor.getAttributeValue(ATTR_RED);
		String green = elColor.getAttributeValue(ATTR_GREEN);
		String blue  = elColor.getAttributeValue(ATTR_BLUE);

		return new Color(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
	}

	//---------------------------------------------------------------------------

	private static AbstractNode xmlToObjects(XmlElement elObjects, FieldAttribs fa)
	{
		AbstractNode node = null;

		String name = elObjects.getName();

		if (name.equals(FOLDER))
			node = new FolderNode();

		else if (name.equals(TABLE))
		{
			TableNode table = new TableNode();

			IOLib.xmlToAttribList(elObjects.getChild(TABLEVARS).getChild(LIST), table.tableVars);
			IOLib.xmlToTree(elObjects.getChild(TRIGGERS), table.triggers, TRIGGER, Trigger.class);
			IOLib.xmlToTree(elObjects.getChild(RULES),    table.rules,    RULE,    TableRule.class);

			node = table;

			//--- START HACK ----------------------------------------------------------------------
			//--- This hack rescues the old postSql attrib and puts it into the standard sql module
			//--- Furthermore, rescues the tablespace and puts it into the oracle module

			XmlElement elAttr = elObjects.getChild(ATTRIBS);

			if (elAttr != null)
			{
				List l = elAttr.getChildren();

				for(int i=0; i<l.size(); i++)
				{
					XmlElement elA = (XmlElement) l.get(i);

					String attrName  = elA.getAttributeValue("name");
					String attrValue = elA.getAttributeValue("value");

					if (attrName.equals("postSql"))
					{
						druid.interfaces.SqlGenModule sqlMod = (druid.interfaces.SqlGenModule)
																			druid.core.modules.ModuleManager.getModule(
																							druid.interfaces.SqlGenModule.class,
																							"stdSql");
						table.modsConfig.setValue(sqlMod, "postSql", attrValue);
					}
					else if (attrName.equals("tableSpace"))
					{
						druid.interfaces.SqlGenModule sqlMod = (druid.interfaces.SqlGenModule)
																			druid.core.modules.ModuleManager.getModule(
																							druid.interfaces.SqlGenModule.class,
																							"oraSql");
						table.modsConfig.setValue(sqlMod, "ts", attrValue);
					}
				}
			}

			//--- END HACK ------------------------------------------------------------------------
		}

		else if (name.equals(FIELD))
		{
			FieldNode field = new FieldNode();

			xmlToFieldAttribs(elObjects.getChild(FATTRIBS).getChild(ATTRIBS), field.fieldAttribs, fa);

			node = field;
		}

		else if (name.equals(VIEW))
			node = new ViewNode();

		else if (name.equals(PROCEDURE))
			node = new ProcedureNode();

		else if (name.equals(FUNCTION))
			node = new FunctionNode();

		else if (name.equals(SEQUENCE))
			node = new SequenceNode();

		else if (name.equals(NOTES))
			node = new NotesNode();

		else
			return null;

		//--- every node has an attrib-set

		IOLib.xmlToAttribSet (elObjects.getChild(ATTRIBS),    node.attrSet);
		IOLib.xmlToDocs      (elObjects.getChild(DOCS),       node);
		IOLib.xmlToModsConfig(elObjects.getChild(MODSCONFIG), node.modsConfig);

		node.setText(node.attrSet.getString("name"));

		//--- handle children

		List listChildren = elObjects.getChildren();

		for(int i=0; i<listChildren.size(); i++)
		{
			AbstractNode child = xmlToObjects((XmlElement) listChildren.get(i), fa);

			if (child != null)
				node.addChild(child);
		}

		//--- return node

		return node;
	}

	//---------------------------------------------------------------------------

	private static void xmlToFieldAttribs(XmlElement elAttribs, AttribSet as, FieldAttribs fa)
	{
		if (elAttribs == null) return;

		List list = elAttribs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elAttr = (XmlElement) list.get(i);

			String name  = elAttr.getAttributeValue(ATTR_NAME);
			String value = elAttr.getAttributeValue(ATTR_VALUE);

			if (value == null)
				value = elAttr.getValue();

			String type  = null;

			for(int j=0; j<fa.size(); j++)
			{
				AttribSet fas = fa.get(j);
				String    id  = Integer.toString(fas.getInt("id"));

				if (name.equals(id))
				{
					type = fas.getString("type");
					break;
				}
			}

			if (type == null)
				throw new DruidException(DruidException.INC_STR, "Attrib id not found for field", name);

			if (type.equals(FieldAttribs.TYPE_STRING))
				as.addAttrib(name, value);

			else if (type.equals(FieldAttribs.TYPE_INT))
				as.addAttrib(name, Integer.parseInt(value));

			else if (type.equals(FieldAttribs.TYPE_BOOL))
				as.addAttrib(name, Boolean.valueOf(value).booleanValue());

			else
				throw new DruidException(DruidException.INC_STR, "Unknown type of attrib", type);
		}
	}

	//---------------------------------------------------------------------------

	private static void xmlToModsUsage(XmlElement elAttribs, ModulesUsage mods)
	{
		if (elAttribs == null) return;

		List list = elAttribs.getChildren(MODULE);

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elAttr = (XmlElement) list.get(i);

			String name  = elAttr.getAttributeValue(ATTR_NAME);

			mods.htModules.put(name, "???");
		}
	}
}

//==============================================================================
