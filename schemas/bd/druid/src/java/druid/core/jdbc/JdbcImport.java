//==============================================================================
//===
//===   JdbcImport
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.dlib.gui.ProgressDialog;
import org.dlib.gui.treeview.TreeViewNode;

import ddf.type.SqlType;
import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTracker;
import druid.core.DocManager;
import druid.core.DruidException;
import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.TriggerEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.dialogs.jdbc.entityselector.EntitySelector;

//==============================================================================

public class JdbcImport
{
	//---------------------------------------------------------------------------
	//---
	//--- Objects import
	//---
	//---------------------------------------------------------------------------

	/** This method is intended to be executed into a separate thread */

	public static void importObjects(ProgressDialog progrDial, DatabaseNode dbNode,
												Vector vImpEnt) throws Exception
	{
		if (progrDial != null)
			progrDial.reset(vImpEnt.size() +3);

		FolderNode folder = new FolderNode("Imported data");

		int pkId  = DataLib.getPrimaryKey(dbNode.fieldAttribs, true);
		int defId = DataLib.getDefault(dbNode.fieldAttribs, true);
		int nnId  = DataLib.getNotNull(dbNode.fieldAttribs, true);

		//------------------------------------------------------------------
		//--- Step 1 : retrieve data and build tree

		for(int i=0; i<vImpEnt.size(); i++)
		{
			AbstractEntity node = (AbstractEntity) vImpEnt.elementAt(i);

			if (progrDial != null)
				progrDial.advance(node.getFullName());

			//---------------------------------------------------------------
			//--- retrieve entity structure (if not yet retrieved)

			try
			{
				node.loadInfo();
			}
			catch(SQLException e)
			{
				String message = 	"Raised exception when importing.\n\n"+
										"- entity : " + node.getFullName() +"\n"+
										"- exception : "+ e.getMessage();

				throw new Exception(message);
			}

			//---------------------------------------------------------------
			//--- build proper AbstractNode

			if (node instanceof TableEntity)
				importTable(dbNode, folder, (TableEntity) node, ""+pkId, ""+nnId, ""+defId);

			else if (node instanceof ViewEntity)
				importView(folder, (ViewEntity) node);

			else if (node instanceof ProcedureEntity)
				importProcedure(folder, (ProcedureEntity) node);

			else if (node instanceof FunctionEntity)
				importFunction(folder, (FunctionEntity) node);

			else if (node instanceof SequenceEntity)
				importSequence(folder, (SequenceEntity) node);
		}

		//------------------------------------------------------------------
		//--- Step 2 : adjust foreign keys

		if (progrDial != null)
			progrDial.advance("Adjusting fkeys...");

		adjustFKeys(folder, vImpEnt);

		//------------------------------------------------------------------
		//--- Step 3 : add indexes

		if (progrDial != null)
			progrDial.advance("Adding indexes...");

		buildIndexes(dbNode.fieldAttribs, vImpEnt, folder);

		//------------------------------------------------------------------
		//--- Step 4 : sync database fields with other attribs

		if (progrDial != null)
			progrDial.advance("Refreshing database...");

		dbNode.addChild(folder);
		folder.expand(true);

		DataLib.syncAllFields(dbNode);
		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table import methods
	//---
	//---------------------------------------------------------------------------

	private static void importTable(DatabaseNode dbNode, FolderNode folder, TableEntity node,
											  String pkId, String nnId, String defId)
	{
		TableNode n = new TableNode(node.sName);

		n.attrSet.setString("comment", node.sRemarks);
		DocManager.convert(n.xmlDoc, node.sRemarks);

		folder.add(n);

		RecordList rl = node.rlBasicInfo;

		for(int i=0; i<rl.getRowCount(); i++)
		{
			Vector vField = rl.getRecordAt(i);

			String  sName =  (String) vField.elementAt(0);
			boolean bPk   = ((Boolean)vField.elementAt(3)).booleanValue();
			boolean bNN   = ((Boolean)vField.elementAt(4)).booleanValue();
			String  sDef  =  (String) vField.elementAt(5);
			String  sRmks =  (String) vField.elementAt(6);

			if (sDef  == null) sDef  = "";
			if (sRmks == null) sRmks = "";

			FieldNode field = new FieldNode(sName);

			field.attrSet.setString("comment", sRmks);

			DocManager.convert(field.xmlDoc,sRmks);

			n.add(field);

			AttribSet as = field.fieldAttribs;

			//--- a primary key should have "not-null" not set

			if (bPk) bNN = false;

			//--- set basic attribs

			as.addAttrib(defId, sDef);
			as.addAttrib(pkId,  bPk);
			as.addAttrib(nnId,  bNN);

			//--- set datatype (if the case)

			if (!isFieldFKey(sName, node.rlFKeysInt))
			{
				Vector v = node.rlFieldsInt.getRecordAt(i);

				SqlType sqlType = (SqlType) v.elementAt(0);
				String  size    = (String)  v.elementAt(1);

				field.attrSet.setInt("type", retrieveDataType(dbNode, sqlType, size));
			}
		}

		//--- retrieve triggers

		for(int i=0; i<node.triggers.getChildCount(); i++)
		{
			TriggerEntity trigEnt = (TriggerEntity) node.triggers.getChild(i);

			n.triggers.addChild( (Trigger) trigEnt.trigger.duplicate());
		}
	}

	//---------------------------------------------------------------------------

	private static boolean isFieldFKey(String field, RecordList rl)
	{
		for(int i=0; i<rl.getRowCount(); i++)
		{
			String f = (String) rl.getRecordAt(i).elementAt(0);

			if (field.equals(f)) return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	private static int retrieveDataType(DatabaseNode dbNode, SqlType sqlType, String size)
	{
		if ((sqlType.iSize == SqlType.VAR) && (size == null)) size = "???";

		if (sqlType.iSize == SqlType.CONST) size = null;

		String name = sqlType.sName;

		if (size == null)
		{
			//---------------------------------------------------------------------
			//--- retrieve constant type

			AbstractType at = (AbstractType) dbNode.dataTypes.getChild(0);

			for(int i=0; i<at.getChildCount(); i++)
			{
				ConstDataType cdt = (ConstDataType) at.getChild(i);

				String cdtName = cdt.attrSet.getString("name");

				if (name.equalsIgnoreCase(cdtName))
					return cdt.attrSet.getInt("id");
			}

			//--- type not found. we must add it

			ConstDataType cdt = new ConstDataType(sqlType.sName);

			at.addChild(cdt, false);

			return cdt.attrSet.getInt("id");
		}

		else
		{
			//---------------------------------------------------------------------
			//--- retrieve variable type

			AbstractType at = (AbstractType) dbNode.dataTypes.getChild(1);

			for(int i=0; i<at.getChildCount(); i++)
			{
				VarDataType vdt = (VarDataType) at.getChild(i);

				String vdtName = vdt.attrSet.getString("name");

				if (name.equalsIgnoreCase(vdtName))
				{
					for(int j=0; j<vdt.getChildCount(); j++)
					{
						VarAlias va = (VarAlias) vdt.getChild(j);

						if (size.equals(va.attrSet.getString("size")))
							return va.attrSet.getInt("id");

					}

					//--- alias not found. we must add it

					VarAlias va = new VarAlias(name + size.replace(',','_'));
					va.attrSet.setString("size", size);

					vdt.addChild(va, false);

					return va.attrSet.getInt("id");
				}
			}

			//--- type not found. we must add it

			VarDataType vdt = new VarDataType(name);

			at.addChild(vdt, false);

			VarAlias va = new VarAlias(name + size.replace(',','_'));
			va.attrSet.setString("size", size);

			vdt.addChild(va, false);

			return va.attrSet.getInt("id");
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Adjust table foreign keys after all objects have been imported
	//---
	//---------------------------------------------------------------------------

	private static void adjustFKeys(FolderNode folder, Vector vImpEnt)
	{
		for(int i=0; i<folder.getChildCount(); i++)
		{
			AbstractNode node = (AbstractNode) folder.getChild(i);

			if (node instanceof TableNode)
			{
				String tableName = node.attrSet.getString("name");

				for(int j=0; j<node.getChildCount(); j++)
				{
					FieldNode field = (FieldNode) node.getChild(j);

					if (field.attrSet.getInt("type") == 0)
					{
						//--- ok, field needs remapping. Search its table
						//--- in vImpEnt

						for(int k=0; k<vImpEnt.size(); k++)
						{
							AbstractEntity enode = (AbstractEntity) vImpEnt.elementAt(k);

							if (enode instanceof TableEntity && enode.sName.equals(tableName))
							{
								adjustFKey(field, (TableEntity)enode, folder);
								break;
							}
						}
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private static void adjustFKey(FieldNode field, TableEntity node, FolderNode folder)
	{
		for(int i=0; i<node.rlFKeysInt.getRowCount(); i++)
		{
			Vector row = node.rlFKeysInt.getRecordAt(i);

			String fname   = (String) row.elementAt(0);
			String fkTable = (String) row.elementAt(1);
			String fkField = (String) row.elementAt(2);
			String onUpd   = (String) row.elementAt(3);
			String onDel   = (String) row.elementAt(4);

			if (field.attrSet.getString("name").equals(fname))
			{
				//--- field found. now look for fktable

				for(int j=0; j<folder.getChildCount(); j++)
				{
					AbstractNode fkTableNode = (AbstractNode) folder.getChild(j);

					if (fkTableNode instanceof TableNode)
						if (fkTableNode.attrSet.getString("name").equals(fkTable))
						{
							//--- table found. now look for fkfield

							for(int k=0; k<fkTableNode.getChildCount(); k++)
							{
								AbstractNode fkFieldNode = (AbstractNode) fkTableNode.getChild(k);

								if (fkFieldNode.attrSet.getString("name").equals(fkField))
								{
									AttribSet as = field.attrSet;

									as.setInt("refTable", fkTableNode.attrSet.getInt("id"));
									as.setInt("refField", fkFieldNode.attrSet.getInt("id"));

									as.setString("onUpdate", onUpd);
									as.setString("onDelete", onDel);
								}
							}
						}
				}

				return;
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Adjust tables indexes keys after all objects have been imported
	//---
	//---------------------------------------------------------------------------

	private static void buildIndexes(FieldAttribs fa, Vector vImpEnt, FolderNode folder)
	{
		int normIdx = 0;
		int uniqIdx = 0;
		int fulltIdx = 0;

		//------------------------------------------------------------------------
		//--- retrieve maximum normal and unique indexes num

		for(int i=0; i<vImpEnt.size(); i++)
		{
			AbstractEntity node = (AbstractEntity) vImpEnt.elementAt(i);

			if (node instanceof TableEntity)
			{
				RecordList rl = ((TableEntity)node).rlBasicInfo;

				int locNormIdx = 0;
				int locUniqIdx = 0;
				int locFulltIdx = 0;

				for(int j=7; j<rl.getColumnCount(); j++)
				{
					String colName = ((String) rl.getColumnAt(j).getHeaderValue()).toLowerCase();

					if (colName.startsWith("u"))	locUniqIdx++;
						else 								locNormIdx++;
				}

				if (locNormIdx > normIdx) normIdx = locNormIdx;
				if (locUniqIdx > uniqIdx) uniqIdx = locUniqIdx;
			}
		}

		//------------------------------------------------------------------------
		//--- scan all attribs to look for indexes (normal and unique)
		//--- indexes are added (if case)

		Vector vNormIndexes = new Vector();
		Vector vUniqIndexes = new Vector();
		Vector vFullTextIndexes = new Vector();

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			int id = as.getInt("id");

			String sqlName = as.getString("sqlName").trim();
			String type    = as.getString("type");
			String scope   = as.getString("scope");

			if (sqlName.equals("") && type.equals(FieldAttribs.TYPE_BOOL))
			{
				if (scope.equals(FieldAttribs.SCOPE_INDEX))
					vNormIndexes.addElement(new Integer(id));

				else if (scope.equals(FieldAttribs.SCOPE_UINDEX))
					vUniqIndexes.addElement(new Integer(id));
				else if (scope.equals(FieldAttribs.SCOPE_FTINDEX))
                    vFullTextIndexes.addElement(new Integer(id));
			}
		}

		//--- add unique indexes

		while (vUniqIndexes.size() < uniqIdx)
		{
			AttribSet as = fa.append();

			as.setString("name",  "Udx"+(vUniqIndexes.size()+1));
			as.setString("scope", FieldAttribs.SCOPE_UINDEX);

			vUniqIndexes.addElement(new Integer(as.getInt("id")));
		}

		//--- add normal indexes

		while (vNormIndexes.size() < normIdx)
		{
			AttribSet as = fa.append();

			as.setString("name",  "Idx"+(vNormIndexes.size()+1));
			as.setString("scope", FieldAttribs.SCOPE_INDEX);

			vNormIndexes.addElement(new Integer(as.getInt("id")));
		}
        //--- add fulltext indexes
 
        while (vFullTextIndexes.size() < fulltIdx)
        {
            AttribSet as = fa.append();
 
            as.setString("name",  "Idx"+(vNormIndexes.size()+1));
            as.setString("scope", FieldAttribs.SCOPE_FTINDEX);
 
            vFullTextIndexes.addElement(new Integer(as.getInt("id")));
        }
		
		//------------------------------------------------------------------------
		//--- setup indexes for fields

		for(int i=0; i<folder.getChildCount(); i++)
		{
			AbstractNode node = (AbstractNode) folder.getChild(i);

			if (node instanceof TableNode)
			{
				TableEntity enode = retrieveTable((TableNode)node, vImpEnt);

				RecordList rl = enode.rlBasicInfo;

				for(int j=0; j<node.getChildCount(); j++)
				{
					FieldNode field = (FieldNode) node.getChild(j);

					int locNormIdx = 0;
					int locUniqIdx = 0;

					for(int k=7; k<rl.getColumnCount(); k++)
					{
						String  colName = ((String) rl.getColumnAt(k).getHeaderValue()).toLowerCase();
						boolean idxVal  = ((Boolean)rl.getRecordAt(j).elementAt(k)).booleanValue();

						int id = 0;

						if (colName.startsWith("u"))
							id = ((Integer)vUniqIndexes.elementAt(locUniqIdx++)).intValue();
						else
							id = ((Integer)vNormIndexes.elementAt(locNormIdx++)).intValue();

						field.fieldAttribs.addAttrib(""+id, idxVal);
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private static TableEntity retrieveTable(TableNode table, Vector vImpEnt)
	{
		String tableName = table.attrSet.getString("name");

		for(int i=0; i<vImpEnt.size(); i++)
		{
			AbstractEntity node = (AbstractEntity) vImpEnt.elementAt(i);

			if (node instanceof TableEntity)
				if (node.sName.equals(tableName)) return (TableEntity) node;
		}

		throw new DruidException(DruidException.INC_STR, "Table not found", table);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Other entities import
	//---
	//---------------------------------------------------------------------------

	private static void importView(AbstractNode folder, ViewEntity ent)
	{
		ViewNode node = new ViewNode(ent.sName);

		node.attrSet.setString("sqlCode", ent.sqlCode);
		DocManager.convert(node.xmlDoc, ent.sRemarks);

		folder.add(node);
	}

	//---------------------------------------------------------------------------

	private static void importProcedure(AbstractNode folder, ProcedureEntity ent)
	{
		ProcedureNode node = new ProcedureNode(ent.sName);

		node.attrSet.setString("sqlCode", ent.sqlCode);
		DocManager.convert(node.xmlDoc, ent.sRemarks);

		folder.add(node);
	}

	//---------------------------------------------------------------------------

	private static void importFunction(AbstractNode folder, FunctionEntity ent)
	{
		FunctionNode node = new FunctionNode(ent.sName);

		node.attrSet.setString("sqlCode", ent.sqlCode);
		DocManager.convert(node.xmlDoc, ent.sRemarks);

		folder.add(node);
	}

	//---------------------------------------------------------------------------

	private static void importSequence(AbstractNode folder, SequenceEntity ent)
	{
		SequenceNode node = new SequenceNode(ent.sName);

		AttribSet as = node.attrSet;

		as.setString("increment", ent.increment);
		as.setString("minValue",  ent.minValue);
		as.setString("maxValue",  ent.maxValue);
		as.setString("start",     ent.start);
		as.setString("cache",     ent.cache);
		as.setBool  ("cycle",     ent.cycle);
		as.setBool  ("order",     ent.order);

		DocManager.convert(node.xmlDoc, ent.sRemarks);

		folder.add(node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Build a vector with all entities that must be imported from a sub-tree
	//---
	//---------------------------------------------------------------------------

	public static Vector getImportEntities(TreeViewNode node, EntitySelector es)
	{
		//--- step 1 : we must expand all containers

		if (!JdbcLib.loadChildren(node)) return null;

		//------------------------------------------------------------------------
		//--- step 2 : collect nodes to import

		Enumeration e = node.preorderEnumeration();

		//--- skip the node itself
		e.nextElement();

		Vector v = new Vector();

		while (e.hasMoreElements())
		{
			TreeViewNode n = (TreeViewNode)e.nextElement();

			if (n instanceof AbstractEntity)
			{
				boolean bTable = (n instanceof TableEntity     && es.importTables());
				boolean bView  = (n instanceof ViewEntity      && es.importViews());
				boolean bProc  = (n instanceof ProcedureEntity && es.importProcedures());
				boolean bFunc  = (n instanceof FunctionEntity  && es.importFunctions());
				boolean bSequen= (n instanceof SequenceEntity  && es.importSequences());

				if (bTable || bView || bProc || bFunc || bSequen)
					v.addElement(n);
			}
		}

		return v;
	}
}

//==============================================================================
