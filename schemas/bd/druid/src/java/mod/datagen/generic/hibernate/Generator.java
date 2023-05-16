//==============================================================================
//===
//===   Generator
//===
//===   Copyright (C) by Andrea Carboni, Damien Boucquey
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import java.util.Vector;

import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlCodec;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.BasicModule;
import druid.interfaces.Logger;
import factory.sql.FKeyEntry;

//==============================================================================

class Generator
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

	public XmlElement generate(BasicModule mod, DatabaseNode db, TableNode node)
	{
		DatabaseSettings sett = new DatabaseSettings(db.modsConfig, mod);

		XmlElement elRoot = new XmlElement("hibernate-mapping");

		if (!sett.getSchema().equals(""))
			elRoot.setAttribute(new XmlAttribute("schema", sett.getSchema()));

		if (!sett.getDefaultCascade().equals(DatabaseSettings.DEFCASCADE_NONE))
			elRoot.setAttribute(new XmlAttribute("default-cascade", sett.getDefaultCascade()));

		if (!sett.isAutoImport())
			elRoot.setAttribute(new XmlAttribute("auto-import", sett.isAutoImport()));

		String pack = sett.getPackage();

		//--- generate class element

		elRoot.addChild(genClass(mod, sett, node, pack));

		//------------------------------------------------------------------------
		//--- generate sql queries

//		for(int i=0; i<db.sqlQueries.getChildCount(); i++)
//		{
//			SqlQuery query = (SqlQuery) db.sqlQueries.getChild(i);
//
//			elRoot.addChild(genSqlQuery(query));
//		}

		return elRoot;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Class generation
	//---
	//---------------------------------------------------------------------------

	private XmlElement genClass(BasicModule mod, DatabaseSettings dbSett, TableNode node, String pack)
	{
		TableSettings sett = new TableSettings(node.modsConfig, mod);

		XmlElement elTable = new XmlElement("class");

		String tableName = node.attrSet.getString("name");
		String className = pack +"."+ tableName;

		String suffix    =resolve(sett.getClassName(),      dbSett.getClassName());
		String discrim   =resolve(sett.getDiscriminValue(), dbSett.getDiscriminValue());
		String schema    =resolve(sett.getSchema(),         dbSett.getSchema());
		String proxy     =resolve(sett.getProxy(),          dbSett.getProxy());
		String where     =resolve(sett.getWhere(),          dbSett.getWhere());
		String persister =resolve(sett.getPersister(),      dbSett.getPersister());
		String batchSize =resolve(sett.getBatchSize(),      dbSett.getBatchSize());

		if (!suffix.equals(""))
			className = sett.getClassName();

		elTable.setAttribute(new XmlAttribute("name",  className));
		elTable.setAttribute(new XmlAttribute("table", tableName));

		//------------------------------------------------------------------------
		//--- add tables's comment (if any)

		String comment = node.attrSet.getString("comment").trim();

		if (!comment.equals(""))
		{
			XmlElement metaField = new XmlElement("meta");

			metaField.setAttribute(new XmlAttribute("attribute","class-description"));
			metaField.setValue(XmlCodec.encode(comment));

			elTable.addChild(metaField);
		}

		//------------------------------------------------------------------------
		//--- add other properties

		boolean isMutable   = resolve(sett.getMutable(),         dbSett.isMutable());
		boolean isDynInsert = resolve(sett.getDynamicInsert(),   dbSett.isDynamicInsert());
		boolean isDynUpdate = resolve(sett.getDynamicUpdate(),   dbSett.isDynamicUpdate());
		boolean isSelBefUpd = resolve(sett.getSelectBeforeUpd(), dbSett.isSelectBeforeUpd());
		boolean isLazy      = resolve(sett.getLazy(),            dbSett.isLazy());

		if (!schema.equals(""))
			elTable.setAttribute(new XmlAttribute("schema", schema));

		if (!proxy.equals(""))
			elTable.setAttribute(new XmlAttribute("proxy", proxy));

		if (!discrim.equals(""))
			elTable.setAttribute(new XmlAttribute("discriminator-value", discrim));

		if (isMutable)
			elTable.setAttribute(new XmlAttribute("mutable", isMutable));

		//---

		String polymorph = sett.getPolymorphism();

		if (polymorph.equals(TableSettings.POLYMORPH_DEFAULT))
			polymorph = dbSett.getPolymorphism();

		if (!polymorph.equals(TableSettings.POLYMORPH_IMPLICIT))
			elTable.setAttribute(new XmlAttribute("polymorphism", polymorph));

		//---

		if (!where.equals(""))
			elTable.setAttribute(new XmlAttribute("where", where));

		if (!persister.equals(""))
			elTable.setAttribute(new XmlAttribute("persister", persister));

		if (isDynInsert)
			elTable.setAttribute(new XmlAttribute("dynamic-insert", isDynInsert));

		if (isDynUpdate)
			elTable.setAttribute(new XmlAttribute("dynamic-update", isDynUpdate));

		if (!batchSize.equals("1"))
			elTable.setAttribute(new XmlAttribute("batch-size", batchSize));

		if (isSelBefUpd)
			elTable.setAttribute(new XmlAttribute("select-before-update", isSelBefUpd));

		//---

		String optLock = sett.getOptimisticLock();

		if (optLock.equals(TableSettings.OPTLOCK_DEFAULT))
			optLock = dbSett.getOptimisticLock();

		if (!optLock.equals(TableSettings.OPTLOCK_VERSION))
			elTable.setAttribute(new XmlAttribute("optimistic-lock", optLock));

		//---

		if (isLazy)
			elTable.setAttribute(new XmlAttribute("lazy", isLazy));

		//------------------------------------------------------------------------
		//--- get foreign key vector

		Vector vFKeys = genForeignKeys(node);

		//--- generate class key

		elTable.addChild(genPrimaryKey(mod, dbSett, node, vFKeys, pack));

		//------------------------------------------------------------------------
		//--- generate properties

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			if (!DataLib.isPrimaryKey(field))
				elTable.addChild(genProperty(mod, dbSett, field, "property"));
		}

		return elTable;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Properties generation
	//---
	//---------------------------------------------------------------------------

	private XmlElement genProperty(BasicModule mod, DatabaseSettings dbSett, FieldNode node, String elemName)
	{
		FieldSettings sett = new FieldSettings(node.modsConfig, mod);

		XmlElement elField = new XmlElement(elemName);

		String propName = resolve(sett.getPropertyName(), dbSett.getPropertyName());
		String fieldName= node.attrSet.getString("name");

		if (propName.equals(""))
			propName = fieldName;

		elField.setAttribute(new XmlAttribute("name", propName));

		//------------------------------------------------------------------------
		//--- add field's type

		TypeInfo ti      = DataTypeLib.getTypeInfo(node);
		String   sqlType = ti.getSqlType();
		String   type    = ti.ddEquiv;

		elField.setAttribute(new XmlAttribute("type", type));

		if (type.equals(""))
			logger.log(Logger.ALERT, "Warning : DDEquiv is empty for type --> " + ti.name);

		elField.setAttribute(new XmlAttribute("column", fieldName));

		//------------------------------------------------------------------------
		//--- add field's comment (if any)

		String comment = node.attrSet.getString("comment").trim();

		if (!comment.equals(""))
		{
			XmlElement metaField = new XmlElement("meta");

			metaField.setAttribute(new XmlAttribute("attribute", "field-description"));
			metaField.setValue(XmlCodec.encode(comment));

			elField.addChild(metaField);
		}

		//------------------------------------------------------------------------
		//--- add field's sqltype

		XmlElement columnField = new XmlElement("column");

		columnField.setAttribute(new XmlAttribute("name",     fieldName));
		columnField.setAttribute(new XmlAttribute("sql-type", sqlType));

		elField.addChild(columnField);

		//------------------------------------------------------------------------
		//--- add general attributes

		boolean isInsert = resolve(sett.getInsert(), dbSett.isInsert());
		boolean isUpdate = resolve(sett.getUpdate(), dbSett.isUpdate());

		String typeAttr    = resolve(sett.getType(),    dbSett.getType());
		String accessAttr  = resolve(sett.getAccess(),  dbSett.getAccess());
		String formulaAttr = resolve(sett.getFormula(), dbSett.getFormula());

		if (!typeAttr.equals(""))
			elField.setAttribute(new XmlAttribute("type", typeAttr));

		if (!isInsert)
			elField.setAttribute(new XmlAttribute("insert", isInsert));

		if (!isUpdate)
			elField.setAttribute(new XmlAttribute("update", isUpdate));

		if (!formulaAttr.equals(""))
			elField.setAttribute(new XmlAttribute("formula", formulaAttr));

		if (!accessAttr.equals(""))
			elField.setAttribute(new XmlAttribute("access", accessAttr));

		return elField;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Foreign keys generation
	//---
	//---------------------------------------------------------------------------

	private Vector genForeignKeys(TableNode node)
	{
		DatabaseNode dbNode = node.getDatabase();

		//------------------------------------------------------------------------
		//--- get all table fkeys

		Vector v = new Vector();

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode f = (FieldNode) node.getChild(i);

			int type = f.attrSet.getInt("type");
			int refT = f.attrSet.getInt("refTable");

			if (type == 0 && refT != 0)
			{
				AttribSet as = f.attrSet;

				int refTable = as.getInt("refTable");
				int refField = as.getInt("refField");

				String fName = as.getString("name");
				String match = as.getString("matchType");
				String onUpd = as.getString("onUpdate");
				String onDel = as.getString("onDelete");

				TableNode fkeyTNode = dbNode.getTableByID(refTable);
				FieldNode fkeyFNode = fkeyTNode.getFieldByID(refField);

				String fkTable = fkeyTNode.attrSet.getString("name");
				String fkField = fkeyFNode.attrSet.getString("name");

				v.add(new FKeyEntry(fName, fkTable, fkField, match, onUpd, onDel));
			}
		}

		//------------------------------------------------------------------------
		//--- merge fkeys (for multiple fkeys)

		for(int i=0; i<v.size()-1; i++)
		{
			FKeyEntry fke1 = (FKeyEntry) v.get(i);
			FKeyEntry fke2 = (FKeyEntry) v.get(i+1);

			if (fke1.merge(fke2))
			{
				v.remove(i+1);
				i--;
			}
		}

		return v;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ID generation
	//---
	//---------------------------------------------------------------------------

	private XmlElement genPrimaryKey(BasicModule mod, DatabaseSettings dbSett,
												TableNode node, Vector vFKeys, String pack)
	{
		TableSettings sett = new TableSettings(node.modsConfig, mod);

		boolean isFKeys = resolve(sett.getForeignKeys(), dbSett.isForeignKeys());

		XmlElement elClassId = new XmlElement("id");

		int keyNodeCount = 0;

		for(int i=0; i<node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);

			if (DataLib.isPrimaryKey(field))
				keyNodeCount++;
		}

		if (keyNodeCount == 1)
		{
			for(int i=0; i<node.getChildCount(); i++)
			{
				FieldNode field = (FieldNode) node.getChild(i);

				if (DataLib.isPrimaryKey(field))
				{
					if (isFKeys && field.isFkey())
					{
						int j = getKeyVectorIndex(field, vFKeys);

						if (j>=0)
						{
							XmlElement elKey = genForeignKey((FKeyEntry) vFKeys.get(j), node, pack);

							vFKeys.remove(j);
							elKey.setName("one-to-one");
							elClassId.addChild(elKey);
						}
					}
					else
						elClassId = genId(mod, dbSett, field, sett);

					break;
				}
			}
		}

		else if (keyNodeCount > 1)
		{
			elClassId.setName("composite-id");

			for(int i=0; i<node.getChildCount(); i++)
			{
				FieldNode field = (FieldNode) node.getChild(i);

				if (DataLib.isPrimaryKey(field))
				{
					if (isFKeys && field.isFkey())
					{
						int j = getKeyVectorIndex(field, vFKeys);

						if (j>=0)
						{
							XmlElement elKey = genForeignKey((FKeyEntry) vFKeys.get(j), node, pack);

							if (keyNodeCount == ((FKeyEntry) vFKeys.get(j)).vFields.size())
								elKey.setName("key-one-to-one");
							else
								elKey.setName("key-many-to-one");

							vFKeys.remove(j);
							elClassId.addChild(elKey);
						}
					}
					else
					{
						elClassId.addChild(genProperty(mod, dbSett, field, "key-property"));
					}
				}
			}
		}

		return elClassId;
	}

	//---------------------------------------------------------------------------

	private int getKeyVectorIndex(FieldNode field, Vector vFKeys)
	{
		String name = field.attrSet.getString("name");

		boolean found = false;
		int j;

		for (j=0; j<vFKeys.size() && !found; j++)
		{
			FKeyEntry fkey = (FKeyEntry) vFKeys.get(j);

			for (int k=0; k<fkey.vFields.size() && !found; k++)
				if (fkey.vFields.get(k).equals(name))
					found = true;
		}

		return (found)	? (j-1) : (-1);
	}

	//---------------------------------------------------------------------------

	private XmlElement genId(BasicModule mod, DatabaseSettings dbSett, FieldNode node, TableSettings tableSett)
	{
		FieldSettings sett = new FieldSettings(node.modsConfig, mod);

		TypeInfo ti = DataTypeLib.getTypeInfo(node);

		String sqlType = ti.getSqlType();
		String type    = ti.ddEquiv;

		XmlElement idField = new XmlElement("id");

		String propName = resolve(sett.getPropertyName(), dbSett.getPropertyName());
		String fieldName= node.attrSet.getString("name");

		if (propName.equals(""))
			propName = fieldName;

		idField.setAttribute(new XmlAttribute("name", propName));
		idField.setAttribute(new XmlAttribute("type", type));

		if (type.equals(""))
			logger.log(Logger.ALERT, "Warning : DDEquiv is empty for type --> " + ti.name);

		XmlElement columnField = new XmlElement("column");

		columnField.setAttribute(new XmlAttribute("name",     fieldName));
		columnField.setAttribute(new XmlAttribute("sql-type", sqlType));

		idField.addChild(columnField);

		XmlElement generatorField = new XmlElement("generator");

		String generatorClass = (resolve(tableSett.getGenerateIds(), dbSett.isGenerateIds()))
										? "native"
										: "assigned";

		generatorField.setAttribute(new XmlAttribute("class", generatorClass));

		idField.addChild(generatorField);

		return idField;
	}

	//---------------------------------------------------------------------------

	private XmlElement genForeignKey(FKeyEntry key, TableNode node, String pack)
	{
		XmlElement elFK = new XmlElement("many-to-one");

		elFK.setAttribute(new XmlAttribute("name",  key.fkTable));
		elFK.setAttribute(new XmlAttribute("class", pack + "." + key.fkTable));

		for(int j=0; j<key.vFields.size(); j++)
			elFK.addChild(genColumn(node.getFieldByName((String) key.vFields.get(j))));

		return elFK;
	}

	//---------------------------------------------------------------------------

	private XmlElement genColumn(FieldNode node)
	{
		XmlElement columnField = new XmlElement("column");

		TypeInfo ti = DataTypeLib.getTypeInfo(node);

		String fieldName= node.attrSet.getString("name");

		columnField.setAttribute(new XmlAttribute("name",     fieldName));
		columnField.setAttribute(new XmlAttribute("sql-type", ti.getSqlType()));

		return columnField;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Query generation
	//---
	//---------------------------------------------------------------------------

	/*private XmlElement genSqlQuery(SqlQuery query)
	{
		XmlElement elQuery = new XmlElement("sql-query");

		elQuery.setAttribute(new XmlAttribute("name", query.attrSet.getString("name")));
		elQuery.setValue("*** TO FINISH ***");

		return elQuery;
	}*/

	//---------------------------------------------------------------------------
	//---
	//--- Options resolution methods
	//---
	//---------------------------------------------------------------------------

	private boolean resolve(String value, boolean def)
	{
		if (value.equals(Consts.DEFAULT))
			return def;

		return value.equals(Consts.YES);
	}

	//---------------------------------------------------------------------------

	private String resolve(String value, String def)
	{
		if (value.equals(""))
			return def;

		return value;
	}
}

//==============================================================================
