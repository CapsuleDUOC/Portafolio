//==============================================================================
//===
//===   Importer
//===
//===   Copyright (C) by Andrea Carboni
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbio.torque;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dlib.xml.XmlElement;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DocManager;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.ProjectNode;
import druid.data.TableNode;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.DataTypes;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.datatypes.VarFolder;

//==============================================================================

class Importer
{
	private int pkID;
	private int defID;
	private int nnID;

	private Hashtable htConstTypes;
	private Hashtable htVarTypes;
	private Vector    vIndexes;
	private Vector    vUniques;
	private Vector    vFKeys;

	private DatabaseNode dbNode;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Importer() {}

	//---------------------------------------------------------------------------
	//---
	//--- API method
	//---
	//---------------------------------------------------------------------------

	public void doImport(XmlElement root, ProjectNode project)
	{
		htConstTypes = new Hashtable();
		htVarTypes   = new Hashtable();
		vIndexes     = new Vector();
		vUniques     = new Vector();
		vFKeys       = new Vector();

		dbNode = new DatabaseNode();

		String name = root.getAttributeValue("name");

		if (name != null)
			dbNode.setName(name);

		pkID  = DataLib.getPrimaryKey(dbNode.fieldAttribs, true);
		defID = DataLib.getDefault(dbNode.fieldAttribs, true);
		nnID  = DataLib.getNotNull(dbNode.fieldAttribs, true);

		//--- scan all tables

		List tables = root.getChildren("table");

		for(int i=0; i<tables.size(); i++)
		{
			XmlElement elTable = (XmlElement) tables.get(i);

			String tabName  = elTable.getAttributeValue("name");
			String tabDescr = elTable.getAttributeValue("description");

			TableNode table = new TableNode();

			if (tabName != null)
				table.setName(tabName);

			if (tabDescr != null)
				DocManager.convert(table.xmlDoc, tabDescr);

			dbNode.addChild(table);

			//--- handle fields

			importFields (table, elTable);
			importFKeys  (table, elTable);
			importIndexes(table, elTable);
			importUnique (table, elTable);
		}

		adjustFKeys();

		//--- add database to project at the end of the process to avoid
		//--- useless calls to the tree

		project.add(dbNode);
	}

	//---------------------------------------------------------------------------

	private void importFields(TableNode table, XmlElement elTable)
	{
		DataTypes dt = dbNode.dataTypes;

		List fields = elTable.getChildren("column");

		for(int i=0; i<fields.size(); i++)
		{
			XmlElement elField = (XmlElement) fields.get(i);

			String name    = elField.getAttributeValue("name");
			String descr   = elField.getAttributeValue("description");
			String prKey   = elField.getAttributeValue("primaryKey");
			String notnull = elField.getAttributeValue("required");
			String type    = elField.getAttributeValue("type");
			String size    = elField.getAttributeValue("size");
			String def     = elField.getAttributeValue("default");

			FieldNode field = new FieldNode();

			DataLib.syncField(dbNode, field);

			if (name != null)
				field.setName(name);

			if (descr != null)
				DocManager.convert(field.xmlDoc, descr);

			if ("true".equals(prKey))
				field.fieldAttribs.setBool(pkID +"", true);

			if ("true".equals(notnull))
				field.fieldAttribs.setBool(nnID +"", true);

			if (def != null)
				field.fieldAttribs.setString(defID +"", def);

			//--- handle datatype

			if (type != null)
			{
				if (size == null)
				{
					//--- handle const type

					Integer intType = (Integer) htConstTypes.get(type);

					//--- type not found -> add it

					if (intType == null)
					{
						ConstDataType ct = new ConstDataType(type);
						dt.getChild(0).addChild(ct);

						intType = new Integer(ct.attrSet.getInt("id"));
						htConstTypes.put(type, intType);
					}

					field.attrSet.setInt("type", intType.intValue());
				}
				else
				{
					//--- handle var type

					Hashtable htSizes = (Hashtable) htVarTypes.get(type);

					if (htSizes == null)
					{
						htSizes = new Hashtable();

						htVarTypes.put(type, htSizes);

						VarDataType vt = new VarDataType(type);
						dt.getChild(1).addChild(vt);
					}

					Integer intType = (Integer) htSizes.get(size);

					//--- type not found -> add it

					if (intType == null)
					{
						VarAlias va = new VarAlias(type+size);
						va.attrSet.setString("size", size);

						VarFolder vf = (VarFolder) dt.getChild(1);

						for(int j=0; j<vf.getChildCount(); j++)
						{
							VarDataType vt = (VarDataType) vf.getChild(j);

							if (vt.attrSet.getString("name").equals(type))
							{
								vt.addChild(va);
								break;
							}
						}

						intType = new Integer(va.attrSet.getInt("id"));
						htSizes.put(size, intType);
					}

					field.attrSet.setInt("type", intType.intValue());
				}
			}

			table.addChild(field);
		}
	}

	//---------------------------------------------------------------------------

	private void importFKeys(TableNode table, XmlElement elTable)
	{
		List fkeys = elTable.getChildren("foreign-key");

		for(int i=0; i<fkeys.size(); i++)
		{
			XmlElement elFKey = (XmlElement) fkeys.get(i);

			String refTable = elFKey.getAttributeValue("foreignTable");
			String onUpdate = elFKey.getAttributeValue("onUpdate");
			String onDelete = elFKey.getAttributeValue("onDelete");

			if (refTable != null)
			{
				List fkEntries = elFKey.getChildren("reference");

				for(int j=0; j<fkEntries.size(); j++)
				{
					XmlElement fkEntry = (XmlElement) fkEntries.get(j);

					String locField = fkEntry.getAttributeValue("local");
					String refField = fkEntry.getAttributeValue("foreign");

					if (locField != null && refField != null)
						vFKeys.add(new FKey(table, locField, refTable, refField, onUpdate, onDelete));
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private void adjustFKeys()
	{
		for(int i=0; i<vFKeys.size(); i++)
		{
			FKey fkey = (FKey) vFKeys.get(i);

			FieldNode locField = fkey.table.getFieldByName(fkey.locField);
			TableNode refTable = dbNode.getTableByName(fkey.refTable);

			if (locField != null && refTable != null)
			{
				FieldNode refField = refTable.getFieldByName(fkey.refField);

				if (refField != null)
				{
					//--- ok, set fkey

					AttribSet as = locField.attrSet;

					as.setInt("type", 0);
					as.setInt("refTable", refTable.attrSet.getInt("id"));
					as.setInt("refField", refField.attrSet.getInt("id"));
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private void importIndexes(TableNode table, XmlElement elTable)
	{
		FieldAttribs fa = dbNode.fieldAttribs;

		List indexes = elTable.getChildren("index");

		//--- add index attribs if needed

		for(int i=vIndexes.size(); i<indexes.size(); i++)
		{
			AttribSet as = fa.append();

			as.setString("name",  "Idx"+(i+1));
			as.setString("scope", FieldAttribs.SCOPE_INDEX);

			vIndexes.add(as);

			//--- this is heavy and maybe there are better ways to do this.
			//--- This way is simple and feasible because there is no much data.

			DataLib.syncAllFields(dbNode);
		}

		//--- add imported indexes

		for(int i=0; i<indexes.size(); i++)
		{
			XmlElement elIndex = (XmlElement) indexes.get(i);
			AttribSet  idxAS   = (AttribSet) vIndexes.get(i);

			List indexCols = elIndex.getChildren("index-column");

			for(int j=0; j<indexCols.size(); j++)
			{
				XmlElement elIndexCol = (XmlElement) indexCols.get(j);

				String name = elIndexCol.getAttributeValue("name");

				FieldNode field = table.getFieldByName(name);

				if (field != null)
					field.fieldAttribs.setBool(idxAS.getInt("id")+"", true);
			}
		}
	}

	//---------------------------------------------------------------------------

	private void importUnique(TableNode table, XmlElement elTable)
	{
		FieldAttribs fa = dbNode.fieldAttribs;

		List uniques = elTable.getChildren("unique");

		//--- add index attribs if needed

		for(int i=vUniques.size(); i<uniques.size(); i++)
		{
			AttribSet as = fa.append();

			as.setString("name",  "MUnq"+(i+1));
			as.setString("scope", FieldAttribs.SCOPE_TABLE);

			vUniques.add(as);

			//--- this is heavy and maybe there are better ways to do this.
			//--- This way is simple and feasible because there is no much data.

			DataLib.syncAllFields(dbNode);
		}

		//--- add imported indexes

		for(int i=0; i<uniques.size(); i++)
		{
			XmlElement elIndex = (XmlElement) uniques.get(i);
			AttribSet  unqAS   = (AttribSet) vUniques.get(i);

			List uniqueCols = elIndex.getChildren("unique-column");

			for(int j=0; j<uniqueCols.size(); j++)
			{
				XmlElement elUniqueCol = (XmlElement) uniqueCols.get(j);

				String name = elUniqueCol.getAttributeValue("name");

				FieldNode field = table.getFieldByName(name);

				if (field != null)
					field.fieldAttribs.setBool(unqAS.getInt("id")+"", true);
			}
		}
	}
}

//==============================================================================

class FKey
{
	public TableNode table;
	public String    locField;
	public String    refTable;
	public String    refField;
	public String    onUpdate = FieldNode.NOACTION;
	public String    onDelete = FieldNode.NOACTION;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FKey(TableNode table, String locField, String refTable, String refField, String onUpd, String onDel)
	{
		this.table    = table;
		this.locField = locField;
		this.refTable = refTable;
		this.refField = refField;

		if ("cascade".equals(onUpd)) onUpd = FieldNode.CASCADE;
		if ("setnull".equals(onUpd)) onUpd = FieldNode.SETNULL;
		if ("cascade".equals(onDel)) onDel = FieldNode.CASCADE;
		if ("setnull".equals(onDel)) onDel = FieldNode.SETNULL;
	}
}

//==============================================================================
