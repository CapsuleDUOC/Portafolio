//==============================================================================
//===
//===   Generator
//===
//===   Copyright (C) by Andrea Carboni
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque;

import java.util.Enumeration;

import org.dlib.tools.TVector;
import org.dlib.tools.Util;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlElement;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.core.DocManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldInfo;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.BasicModule;
import druid.interfaces.Logger;
import factory.sql.SqlUtil;

//==============================================================================

public class Generator
{
	private Logger logger;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Generator(Logger l)
	{
		logger = l;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public XmlElement generate(BasicModule mod, DatabaseNode db)
	{
		DatabaseSettings sett = new DatabaseSettings(db.modsConfig, mod);

		XmlElement elRoot = new XmlElement("database");

		elRoot.setAttribute(new XmlAttribute("name", db.attrSet.getString("name")));

		if (!sett.getDefaultIdMethod().equals(DatabaseSettings.IDMETHOD_NONE))
			elRoot.setAttribute(new XmlAttribute("defaultIdMethod", sett.getDefaultIdMethod()));

		if (!sett.getDefaultJavaType().equals(DatabaseSettings.JAVATYPE_PRIMITIVE))
			elRoot.setAttribute(new XmlAttribute("defaultJavaType", sett.getDefaultJavaType()));

		if (!sett.getPackage().equals(""))
			elRoot.setAttribute(new XmlAttribute("package", sett.getPackage()));

		if (!sett.getBaseClass().equals(""))
			elRoot.setAttribute(new XmlAttribute("baseClass", sett.getBaseClass()));

		if (!sett.getBasePeer().equals(""))
			elRoot.setAttribute(new XmlAttribute("basePeer", sett.getBasePeer()));

		if (!sett.getDefaultJavaNaming().equals(DatabaseSettings.JAVANAM_UNDERSCORE))
			elRoot.setAttribute(new XmlAttribute("defaultJavaNamingMethod", sett.getDefaultJavaNaming()));

		if (sett.isHeavyIndexing())
			elRoot.setAttribute(new XmlAttribute("heavyIndexing", sett.isHeavyIndexing()));

		for(Enumeration e=db.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (node instanceof TableNode)
				elRoot.addChild(genTable(mod, (TableNode) node));
		}

		return elRoot;
	}

	//---------------------------------------------------------------------------

	private XmlElement genTable(BasicModule mod, TableNode node)
	{
		TableSettings sett = new TableSettings(node.modsConfig, mod);

		XmlElement elTable = new XmlElement("table");

		elTable.setAttribute(new XmlAttribute("name", node.attrSet.getString("name")));

		if (!sett.getJavaName().equals(""))
			elTable.setAttribute(new XmlAttribute("javaName", sett.getJavaName()));

		if (!sett.getIdMethod().equals(TableSettings.IDMETHOD_NULL))
			elTable.setAttribute(new XmlAttribute("idMethod", sett.getIdMethod()));

		if (sett.isSkippingSql())
			elTable.setAttribute(new XmlAttribute("skipSql", sett.isSkippingSql()));

		if (sett.isAbstract())
			elTable.setAttribute(new XmlAttribute("abstract", sett.isAbstract()));

		if (!sett.getBaseClass().equals(""))
			elTable.setAttribute(new XmlAttribute("baseClass", sett.getBaseClass()));

		if (!sett.getBasePeer().equals(""))
			elTable.setAttribute(new XmlAttribute("basePeer", sett.getBasePeer()));

		if (!sett.getAlias().equals(""))
			elTable.setAttribute(new XmlAttribute("alias", sett.getAlias()));

		if (!sett.getInterface().equals(""))
			elTable.setAttribute(new XmlAttribute("interface", sett.getInterface()));

		if (!sett.getJavaNaming().equals(TableSettings.JAVANAM_NOCHANGE))
			elTable.setAttribute(new XmlAttribute("javaNamingMethod", sett.getJavaNaming()));

		if (sett.isHeavyIndexing())
			elTable.setAttribute(new XmlAttribute("heavyIndexing", sett.isHeavyIndexing()));

		addDocs(elTable, node);

		//--- generate fields

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			elTable.addChild(genField(mod, field));
		}

		genForeignKeys(elTable, node);
		genIndexes    (elTable, node, "index",  "index-column",  FieldAttribs.SCOPE_INDEX,  false);
		genIndexes    (elTable, node, "unique", "unique-column", FieldAttribs.SCOPE_UINDEX, true);
		genUnique     (elTable, node);

		return elTable;
	}

	//---------------------------------------------------------------------------

	private XmlElement genField(BasicModule mod, FieldNode node)
	{
		FieldSettings sett = new FieldSettings(node.modsConfig, mod);

		XmlElement elField = new XmlElement("column");

		elField.setAttribute(new XmlAttribute("name", node.attrSet.getString("name")));

		if (!sett.getJavaName().equals(""))
			elField.setAttribute(new XmlAttribute("javaName", sett.getJavaName()));

		if (DataLib.isPrimaryKey(node))
			elField.setAttribute(new XmlAttribute("primaryKey", "true"));

		if (DataLib.isNotNull(node))
			elField.setAttribute(new XmlAttribute("required", "true"));

		TypeInfo info = DataTypeLib.getTypeInfo(node);

		elField.setAttribute(new XmlAttribute("type", info.basicType));

		if (!sett.getJavaType().equals(FieldSettings.JAVATYPE_PRIMITIVE))
			elField.setAttribute(new XmlAttribute("javaType", sett.getJavaType()));

		if (info.size != null)
			elField.setAttribute(new XmlAttribute("size", info.size));

		String def = DataLib.getDefaultValue(node);

		if (def != null)
			elField.setAttribute(new XmlAttribute("default", def));

		if (sett.isAutoIncrement())
			elField.setAttribute(new XmlAttribute("autoIncrement", sett.isAutoIncrement()));

		if (!sett.getInputValid().equals(""))
			elField.setAttribute(new XmlAttribute("inputValidator", sett.getInputValid()));

		if (!sett.getJavaNaming().equals(FieldSettings.JAVANAM_NOCHANGE))
			elField.setAttribute(new XmlAttribute("javaNamingMethod", sett.getJavaNaming()));

		addDocs(elField, node);

		return elField;
	}

	//---------------------------------------------------------------------------

	private void genForeignKeys(XmlElement elTable, TableNode node)
	{
		int cnt = 1;

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			if (field.isFkey())
			{
				FieldInfo info = field.getInfo();

				TableNode fkTable = node.getDatabase().getTableByID(info.refTable);
				FieldNode fkField = fkTable.getFieldByID(info.refField);

				//---

				XmlElement elFK = new XmlElement("foreign-key");

				elFK.setAttribute(new XmlAttribute("foreignTable", fkTable.attrSet.getString("name")));

				//--- generate foreign key name if the template is given

				String templ = node.attrSet.getString("tempFK");
				String table = node.attrSet.getString("name");

				TVector fields = new TVector();
				fields.add(field);

				String name = SqlUtil.expandTemplate(templ, table, fields, cnt);

				if (!name.equals(""))
				{
					elFK.setAttribute(new XmlAttribute("name", name));
					cnt++;
				}

				elFK.setAttribute(new XmlAttribute("onUpdate", mapOnClause(info.onUpdate)));
				elFK.setAttribute(new XmlAttribute("onDelete", mapOnClause(info.onDelete)));

				//---

				XmlElement elRef = new XmlElement("reference");

				elRef.setAttribute(new XmlAttribute("local",   field.attrSet.getString("name")));
				elRef.setAttribute(new XmlAttribute("foreign", fkField.attrSet.getString("name")));

				//---

				elFK.addChild(elRef);
				elTable.addChild(elFK);
			}
		}
	}

	//---------------------------------------------------------------------------

	private String mapOnClause(String action)
	{
		if (action.equals(FieldNode.CASCADE)) return "cascade";
		if (action.equals(FieldNode.SETNULL)) return "setnull";

		return "none";
	}

	//---------------------------------------------------------------------------

	private void genIndexes(XmlElement elTable, TableNode node, String parent, String child,
									String index, boolean genName)
	{
		FieldAttribs fa = node.getDatabase().fieldAttribs;

		int cnt = 1;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as    = fa.get(i);
			String    type  = as.getString("type");
			String    scope = as.getString("scope");

			if (type.equals(FieldAttribs.TYPE_BOOL) && scope.equals(index))
			{
				int id = as.getInt("id");

				XmlElement elIdx  = new XmlElement(parent);
				TVector    fields = new TVector();

				//---

				for(int j=0; j<node.getChildCount(); j++)
				{
					FieldNode field = (FieldNode) node.getChild(j);
					String    name  = field.attrSet.getString("name");

					if (field.fieldAttribs.getBool(id +""))
					{
						fields.add(field);

						XmlElement elIdxCol = new XmlElement(child);

						elIdxCol.setAttribute(new XmlAttribute("name", name));

						elIdx.addChild(elIdxCol);
					}
				}

				//---

				if (genName)
				{
					String templ = node.attrSet.getString("tempOther");
					String table = node.attrSet.getString("name");
					String name  = SqlUtil.expandTemplate(templ, table, fields, cnt);

					if (!name.equals(""))
					{
						elIdx.setAttribute(new XmlAttribute("name", name));
						cnt++;
					}
				}

				//---

				if (!elIdx.isLeaf())
					elTable.addChild(elIdx);
			}
		}
	}

	//---------------------------------------------------------------------------

	private void genUnique(XmlElement elTable, TableNode node)
	{
		int cnt = 10;

		FieldAttribs fa = node.getDatabase().fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			String id    = as.getInt("id") +"";
			String sql   = as.getString("sqlName").toLowerCase();
			String type  = as.getString("type");
			String scope = as.getString("scope");

			if (sql.equals("unique") && type.equals(FieldAttribs.TYPE_BOOL))
			{
				//------------------------------------------------------------------
				//--- unique at field scope

				if (scope.equals(FieldAttribs.SCOPE_FIELD))
					for(int j=0; j<node.getChildCount(); j++)
					{
						FieldNode field = (FieldNode) node.getChild(j);

						if (field.fieldAttribs.getBool(id))
						{
							XmlElement elUnCol = new XmlElement("unique-column");
							elUnCol.setAttribute(new XmlAttribute("name", field.attrSet.getString("name")));

							XmlElement elUnique = new XmlElement("unique");
							elUnique.addChild(elUnCol);

							//--- generate constraint name

							TVector fields = new TVector();
							fields.add(field);

							String templ = node.attrSet.getString("tempOther");
							String table = node.attrSet.getString("name");
							String name  = SqlUtil.expandTemplate(templ, table, fields, cnt);

							if (!name.equals(""))
							{
								elUnique.setAttribute(new XmlAttribute("name", name));
								cnt++;
							}

							if (!elUnique.isLeaf())
								elTable.addChild(elUnique);
						}
					}

				//------------------------------------------------------------------
				//--- unique at table scope

				else if (scope.equals(FieldAttribs.SCOPE_TABLE))
				{
					XmlElement elUnique = new XmlElement("unique");

					TVector fields = new TVector();

					//---

					for(int j=0; j<node.getChildCount(); j++)
					{
						FieldNode field = (FieldNode) node.getChild(j);

						if (field.fieldAttribs.getBool(id))
						{
							fields.add(field);

							XmlElement elUnCol = new XmlElement("unique-column");
							elUnCol.setAttribute(new XmlAttribute("name", field.attrSet.getString("name")));

							elUnique.addChild(elUnCol);
						}
					}

					//--- generate constraint name

					String templ = node.attrSet.getString("tempOther");
					String table = node.attrSet.getString("name");
					String name  = SqlUtil.expandTemplate(templ, table, fields, cnt);

					if (!name.equals(""))
					{
						elUnique.setAttribute(new XmlAttribute("name", name));
						cnt++;
					}

					if (!elUnique.isLeaf())
						elTable.addChild(elUnique);
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private void addDocs(XmlElement el, AbstractNode node)
	{
		String descr = DocManager.toText(node.xmlDoc).trim();

		descr = Util.replaceStr(descr, "\n", ". ");

		if (!descr.equals(""))
			el.setAttribute(new XmlAttribute("description", descr));
	}
}

//==============================================================================
