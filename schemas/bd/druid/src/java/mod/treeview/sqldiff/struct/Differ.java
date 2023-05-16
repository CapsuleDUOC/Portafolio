//==============================================================================
//===
//===   Differ
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.struct;

import java.util.Enumeration;
import java.util.Vector;

import org.dlib.gui.treeview.TreeViewNode;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.TableRule;
import druid.data.Trigger;
import druid.data.ViewNode;

//==============================================================================

public class Differ
{
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static DiffSummary diff(DatabaseNode oldDb, DatabaseNode newDb)
	{
		DiffSummary diffSumm = new DiffSummary();

		databaseDiff(diffSumm,   oldDb, newDb);

		tableDiff(diffSumm, toVector(oldDb, TableNode.class),     toVector(newDb, TableNode.class));
		viewDiff (diffSumm, toVector(oldDb, ViewNode.class),      toVector(newDb, ViewNode.class));
		procDiff (diffSumm, toVector(oldDb, ProcedureNode.class), toVector(newDb, ProcedureNode.class));
		funcDiff (diffSumm, toVector(oldDb, FunctionNode.class),  toVector(newDb, FunctionNode.class));
		seqDiff  (diffSumm, toVector(oldDb, SequenceNode.class),  toVector(newDb, SequenceNode.class));

		return diffSumm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static Vector toVector(DatabaseNode dbNode, Class c)
	{
		Vector v = new Vector();

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements(); )
		{
			Object obj = e.nextElement();

			if (c.isInstance(obj))
				v.add(obj);
		}

		return v;
	}

	//---------------------------------------------------------------------------

	private static AbstractNode findNodeByName(String name, Vector data)
	{
		for(int i=0; i<data.size(); i++)
		{
			AbstractNode node = (AbstractNode) data.get(i);

			if (name.equals(node.attrSet.getString("name")))
				 return node;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	private static AbstractNode findNodeByName(String name, TreeViewNode parentNode)
	{
		for(int i=0; i<parentNode.getChildCount(); i++)
		{
			AbstractNode node = (AbstractNode) parentNode.getChild(i);

			if (name.equals(node.attrSet.getString("name")))
				 return node;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	/*private static AttribSet findAttribSetByName(String name, AttribList al)
	{
		for(int i=0; i<al.size(); i++)
		{
			AttribSet as = al.get(i);

			if (as.getString("name").equals(name))
				return as;
		}

		return null;
	}*/

	//---------------------------------------------------------------------------

	private static DiffElement diffString(int subEntity, String attrib, AttribSet as1, AttribSet as2)
	{
		String value1 = as1.getString(attrib).trim();
		String value2 = as2.getString(attrib).trim();

		if (value1.equals(value2)) return null;

		if (value1.equals(""))
			return new DiffElement(subEntity, null, value2);

		else if (value2.equals(""))
			return new DiffElement(subEntity, value1, null);

		else
			return new DiffElement(subEntity, value1, value2);
	}

	//---------------------------------------------------------------------------

	private static DiffElement diffBoolean(int subEntity, String attrib, AttribSet as1, AttribSet as2)
	{
		boolean value1 = as1.getBool(attrib);
		boolean value2 = as2.getBool(attrib);

		if (value1 == value2) return null;

		return new DiffElement(subEntity, Boolean.valueOf(value1), Boolean.valueOf(value2));
	}

	//---------------------------------------------------------------------------

	private static void addIfCase(DiffEntity ent, DiffElement elem)
	{
		if (elem != null)
			ent.add(elem);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Database diff
	//---
	//---------------------------------------------------------------------------

	private static void databaseDiff(DiffSummary diffSumm, DatabaseNode oldDB, DatabaseNode newDB)
	{
		AttribSet oldAS = oldDB.attrSet;
		AttribSet newAS = newDB.attrSet;

		DiffEntity ent = new DiffEntity("database", DiffEntity.CHANGED);

		addIfCase(ent, diffString (DiffEntity.DB_PRESQL,  "preSql",  oldAS, newAS));
		addIfCase(ent, diffString (DiffEntity.DB_POSTSQL, "postSql", oldAS, newAS));

		if (!ent.isEmpty())
			diffSumm.add(DiffSummary.DATABASE, ent);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table diff
	//---
	//---------------------------------------------------------------------------

	private static void tableDiff(DiffSummary diffSumm, Vector oldTables, Vector newTables)
	{
		for(int i=0; i<oldTables.size(); i++)
		{
			TableNode oldTable = (TableNode) oldTables.get(i);
			String    oldName  = oldTable.attrSet.getString("name");
			TableNode newTable = (TableNode) findNodeByName(oldName, newTables);

			if (newTable == null)
				tableRemoved(diffSumm, oldTable);
			else
				tableChanged(diffSumm, oldTable, newTable);
		}

		//------------------------------------------------------------------------

		for(int i=0; i<newTables.size(); i++)
		{
			TableNode newTable = (TableNode) newTables.get(i);
			String    newName  = newTable.attrSet.getString("name");
			TableNode oldTable = (TableNode) findNodeByName(newName, oldTables);

			if (oldTable == null)
				tableAdded(diffSumm, newTable);
		}
	}

	//---------------------------------------------------------------------------

	private static void tableAdded(DiffSummary diffSumm, TableNode table)
	{
		String name = table.attrSet.getString("name");

		DiffEntity ent = new DiffEntity(name, DiffEntity.ADDED);

		diffSumm.add(DiffSummary.TABLE, ent);

		//------------------------------------------------------------------------
		//--- handle fields and triggers

		fieldDiff  (diffSumm, new TableNode(), table,          ent);
		triggerDiff(diffSumm, new Trigger(),   table.triggers, ent);
		tabRuleDiff(diffSumm, new TableRule(), table.rules,    ent);
	}

	//---------------------------------------------------------------------------

	private static void tableRemoved(DiffSummary diffSumm, TableNode table)
	{
		String name = table.attrSet.getString("name");

		DiffEntity ent = new DiffEntity(name, DiffEntity.REMOVED);

		diffSumm.add(DiffSummary.TABLE, ent);

		//------------------------------------------------------------------------
		//--- handle fields and triggers

		fieldDiff  (diffSumm, table,          new TableNode(), ent);
		triggerDiff(diffSumm, table.triggers, new Trigger(),   ent);
		tabRuleDiff(diffSumm, table.rules,    new TableRule(), ent);
	}

	//---------------------------------------------------------------------------

	private static void tableChanged(DiffSummary diffSumm, TableNode oldTable, TableNode newTable)
	{
		AttribSet oldAS = oldTable.attrSet;
//		AttribSet newAS = newTable.attrSet;

		String oldName = oldAS.getString("name");

		DiffEntity ent = new DiffEntity(oldName, DiffEntity.CHANGED);

		//------------------------------------------------------------------------
		//--- handle fields and triggers

		boolean existFields   = fieldDiff  (diffSumm, oldTable,          newTable,          ent);
		boolean existTriggers = triggerDiff(diffSumm, oldTable.triggers, newTable.triggers, ent);
		boolean existRules    = tabRuleDiff(diffSumm, oldTable.rules,    newTable.rules,    ent);

		//------------------------------------------------------------------------

		if (!ent.isEmpty() || existFields || existTriggers || existRules)
			diffSumm.add(DiffSummary.TABLE, ent);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Field diff
	//---
	//---------------------------------------------------------------------------

	private static boolean fieldDiff(DiffSummary diffSumm, TableNode oldTable, TableNode newTable, DiffEntity entTable)
	{
		boolean existFields = false;

		for(int j=0; j<oldTable.getChildCount(); j++)
		{
			FieldNode oldField = (FieldNode) oldTable.getChild(j);
			String    oldName  =             oldField.attrSet.getString("name");
			FieldNode newField = (FieldNode) findNodeByName(oldName, newTable);

			if (newField == null)
			{
				fieldRemoved(diffSumm, oldField, entTable);
				existFields = true;
			}
			else
			{
				if (fieldChanged(diffSumm, oldField, newField, entTable))
					existFields = true;
			}
		}

		//------------------------------------------------------------------------

		for(int j=0; j<newTable.getChildCount(); j++)
		{
			FieldNode newField = (FieldNode) newTable.getChild(j);
			String    newName  = newField.attrSet.getString("name");
			FieldNode oldField = (FieldNode) findNodeByName(newName, oldTable);

			if (oldField == null)
			{
				fieldAdded(diffSumm, newField, entTable);
				existFields = true;
			}
		}

		return existFields;
	}

	//---------------------------------------------------------------------------

	private static void fieldAdded(DiffSummary diffSumm, FieldNode field, DiffEntity entTable)
	{
		AttribSet as = field.attrSet;

		DiffEntity ent = new DiffEntity(as.getString("name"), DiffEntity.ADDED, entTable);

		ent.added(DiffEntity.FI_TYPE, DataTypeLib.getSqlType(field));

		if (field.isFkey())
			fillAddedFKey(ent, field);

		diffSumm.add(DiffSummary.FIELD, ent);

		attribDiff(diffSumm, null, field, ent);
	}

	//---------------------------------------------------------------------------

	private static void fillAddedFKey(DiffEntity ent, FieldNode field)
	{
		AttribSet as = field.attrSet;

		ent.added(DiffEntity.FI_FKEY, DataTypeLib.getTypeDef(field));

		String onUpd = as.getString("onUpdate");
		String onDel = as.getString("onDelete");

		if (!onUpd.equals(FieldNode.NOACTION))
			ent.added(DiffEntity.FI_ONUPDATE, onUpd);

		if (!onDel.equals(FieldNode.NOACTION))
			ent.added(DiffEntity.FI_ONDELETE, onDel);
	}

	//---------------------------------------------------------------------------

	private static void fieldRemoved(DiffSummary diffSumm, FieldNode field, DiffEntity entTable)
	{
		AttribSet as = field.attrSet;

		DiffEntity ent = new DiffEntity(as.getString("name"), DiffEntity.REMOVED, entTable);

		ent.removed(DiffEntity.FI_TYPE, DataTypeLib.getSqlType(field));

		if (field.isFkey())
			fillRemovedFKey(ent, field);

		diffSumm.add(DiffSummary.FIELD, ent);

		attribDiff(diffSumm, field, null, ent);
	}

	//---------------------------------------------------------------------------

	private static void fillRemovedFKey(DiffEntity ent, FieldNode field)
	{
		AttribSet as = field.attrSet;

		ent.removed(DiffEntity.FI_FKEY, DataTypeLib.getTypeDef(field));

		String onUpd = as.getString("onUpdate");
		String onDel = as.getString("onDelete");

		if (!onUpd.equals(FieldNode.NOACTION))
			ent.removed(DiffEntity.FI_ONUPDATE, onUpd);

		if (!onDel.equals(FieldNode.NOACTION))
			ent.removed(DiffEntity.FI_ONDELETE, onDel);
	}

	//---------------------------------------------------------------------------

	private static boolean fieldChanged(DiffSummary diffSumm, FieldNode oldField, FieldNode newField, DiffEntity entTable)
	{
		DiffEntity ent = new DiffEntity(oldField.attrSet.getString("name"), DiffEntity.CHANGED, entTable);

		String oldType = DataTypeLib.getSqlType(oldField);
		String newType = DataTypeLib.getSqlType(newField);

		if (!oldType.equals(newType))
			ent.changed(DiffEntity.FI_TYPE, oldType, newType);

		//------------------------------------------------------------------------
		//--- check foreign key

		if (oldField.isFkey() && !newField.isFkey())
			fillRemovedFKey(ent, oldField);

		else if (!oldField.isFkey() && newField.isFkey())
			fillAddedFKey(ent, newField);

		else if (oldField.isFkey() && newField.isFkey())
		{
			String oldDef = DataTypeLib.getTypeDef(oldField);
			String newDef = DataTypeLib.getTypeDef(newField);

			if (!oldDef.equals(newDef))
				ent.changed(DiffEntity.FI_FKEY, oldDef, newDef);

			String oldUpd = oldField.attrSet.getString("onUpdate");
			String newUpd = newField.attrSet.getString("onUpdate");

			checkOnClause(ent, DiffEntity.FI_ONUPDATE, oldUpd, newUpd);

			String oldDel = oldField.attrSet.getString("onDelete");
			String newDel = newField.attrSet.getString("onDelete");

			checkOnClause(ent, DiffEntity.FI_ONDELETE, oldDel, newDel);
		}

		//------------------------------------------------------------------------
		//--- check field attribs

		boolean existAttribs = attribDiff(diffSumm, oldField, newField, ent);

		//------------------------------------------------------------------------

		if (!ent.isEmpty() || existAttribs)
		{
			diffSumm.add(DiffSummary.FIELD, ent);
			existAttribs = true;
		}

		return existAttribs;
	}

	//---------------------------------------------------------------------------

	private static void checkOnClause(DiffEntity ent, int subEntity, String oldOn, String newOn)
	{
		if (oldOn.equals(newOn)) return;

		if (oldOn.equals(FieldNode.NOACTION))
			ent.added(subEntity, newOn);

		else if (newOn.equals(FieldNode.NOACTION))
			ent.removed(subEntity, oldOn);

		else
			ent.changed(subEntity, oldOn, newOn);
	}

	//---------------------------------------------------------------------------

	private static boolean attribDiff(DiffSummary diffSumm, FieldNode oldField, FieldNode newField, DiffEntity entField)
	{
		boolean existData = false;

		AttribList oldAL = null;
		AttribList newAL = null;

		if (oldField != null)
			oldAL = oldField.getDatabase().fieldAttribs;

		if (newField != null)
			newAL = newField.getDatabase().fieldAttribs;

		if (oldAL != null)
			for(int i=0; i<oldAL.size(); i++)
			{
				AttribSet oldAS = oldAL.get(i);
				AttribSet newAS = findAttrib(oldAS, newAL);

				String oldId    = oldAS.getInt("id") +"";
				String oldValue = oldField.fieldAttribs.getData(oldId).toString();

				if (newAS == null)
				{
					if (attribRemoved(diffSumm, oldAS, oldValue, entField))
						existData = true;
				}
				else
				{
					String newId    = newAS.getInt("id") +"";
					String newValue = newField.fieldAttribs.getData(newId).toString();

					if (attribChanged(diffSumm, oldAS, newAS, oldValue, newValue, entField))
						existData = true;
				}
			}

		//------------------------------------------------------------------------

		if (newAL != null)
			for(int i=0; i<newAL.size(); i++)
			{
				AttribSet newAS = newAL.get(i);
				AttribSet oldAS = findAttrib(newAS, oldAL);

				if (oldAS == null)
				{
					String newId    = newAS.getInt("id") +"";
					String newValue = newField.fieldAttribs.getData(newId).toString();

					if (attribAdded(diffSumm, newAS, newValue, entField))
						existData = true;
				}
			}

		return existData;
	}

	//---------------------------------------------------------------------------

	private static AttribSet findAttrib(AttribSet as, AttribList alData)
	{
		if (alData == null) 
			return null;

		String name  = as.getString("name");
		String sql   = as.getString("sqlName").toLowerCase();
		String type  = as.getString("type");
		String scope = as.getString("scope");

//		if (sql.equals(""))
//			sql = name;

		for(int i=0; i<alData.size(); i++)
		{
			AttribSet as2 = alData.get(i);

			String name2  = as2.getString("name");
			String sql2   = as2.getString("sqlName").toLowerCase();
			String type2  = as2.getString("type");
			String scope2 = as2.getString("scope");

//			if (sql2.equals(""))
//				sql2 = name2;

			if (name.equals(name2) && sql.equals(sql2) && type.equals(type2) && scope.equals(scope2))
				return as2;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	private static boolean attribAdded(DiffSummary diffSumm, AttribSet as, String value, DiffEntity entField)
	{
		String name  = as.getString("name");
		String sql   = as.getString("sqlName").toLowerCase();
		String type  = as.getString("type");
		String scope = as.getString("scope");

		if (sql.equals(""))
			sql = name;

		DiffEntity ent = new DiffEntity(name, DiffEntity.ADDED, entField);

		ent.added(DiffEntity.FA_SQLNAME, sql);
		ent.added(DiffEntity.FA_TYPE,    type);
		ent.added(DiffEntity.FA_SCOPE,   scope);
		ent.added(DiffEntity.FA_VALUE,   value);

		if (isAttribAddable(type, value))
		{
			diffSumm.add(DiffSummary.FIELDATTRIB, ent);
			return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	private static boolean attribRemoved(DiffSummary diffSumm, AttribSet as, String value, DiffEntity entField)
	{
		String name  = as.getString("name");
		String sql   = as.getString("sqlName").toLowerCase();
		String type  = as.getString("type");
		String scope = as.getString("scope");

		if (sql.equals(""))
			sql = name;

		DiffEntity ent = new DiffEntity(name, DiffEntity.REMOVED, entField);

		ent.removed(DiffEntity.FA_SQLNAME, sql);
		ent.removed(DiffEntity.FA_TYPE,    type);
		ent.removed(DiffEntity.FA_SCOPE,   scope);
		ent.removed(DiffEntity.FA_VALUE,   value);

		if (isAttribAddable(type, value))
		{
			diffSumm.add(DiffSummary.FIELDATTRIB, ent);
			return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	private static boolean isAttribAddable(String type, String value)
	{
		boolean condition = (type.equals(FieldAttribs.TYPE_BOOL)   && value.equals("false")) ||
							(type.equals(FieldAttribs.TYPE_STRING) && value.equals(""));

		return !condition;
	}

	//---------------------------------------------------------------------------

	private static boolean attribChanged(DiffSummary diffSumm, AttribSet oldAS, AttribSet newAS,
													 String oldValue, String newValue, DiffEntity entField)
	{
		boolean keepParent = false;

		if (!oldValue.equals(newValue))
		{
			String name  = oldAS.getString("name");
			String sql   = oldAS.getString("sqlName").toLowerCase();
			String type  = oldAS.getString("type");
			String scope = oldAS.getString("scope");

			if (sql.equals(""))
				sql = name;

			//--- string case

			if (type.equals(FieldAttribs.TYPE_STRING))
			{
				if (oldValue.equals(""))
				{
					attribAdded(diffSumm, newAS, newValue, entField);
					return true;
				}

				if (newValue.equals(""))
				{
					attribRemoved(diffSumm, oldAS, oldValue, entField);
					return true;
				}
			}

			//--- boolean case

			else if (type.equals(FieldAttribs.TYPE_BOOL))
			{
				if (oldValue.equals("false"))
					attribAdded(diffSumm, newAS, newValue, entField);
				else
					attribRemoved(diffSumm, oldAS, oldValue, entField);

				return true;
			}

			DiffEntity ent = new DiffEntity(name, DiffEntity.CHANGED, entField);

			ent.changed(DiffEntity.FA_SQLNAME, sql,      sql);
			ent.changed(DiffEntity.FA_TYPE,    type,     type);
			ent.changed(DiffEntity.FA_SCOPE,   scope,    scope);
			ent.changed(DiffEntity.FA_VALUE,   oldValue, newValue);

			diffSumm.add(DiffSummary.FIELDATTRIB, ent);

			keepParent = true;
		}

		return keepParent;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Trigger diff
	//---
	//---------------------------------------------------------------------------

	private static boolean triggerDiff(DiffSummary diffSumm, Trigger oldTriggers, Trigger newTriggers, DiffEntity parent)
	{
		boolean keepParent = false;

		for(int i=0; i<oldTriggers.getChildCount(); i++)
		{
			Trigger oldTrigger = (Trigger) oldTriggers.getChild(i);
			String  oldName    = oldTrigger.attrSet.getString("name");
			Trigger newTrigger = (Trigger) findNodeByName(oldName, newTriggers);

			if (newTrigger == null)
			{
				AttribSet as = oldTrigger.attrSet;

				DiffEntity entTr = new DiffEntity(as.getString("name"), DiffEntity.REMOVED, parent);

				entTr.removed(DiffEntity.TR_ACTIVATION, as.getString ("activation"));
				entTr.removed(DiffEntity.TR_ONINSERT,   as.getBoolean("onInsert"));
				entTr.removed(DiffEntity.TR_ONUPDATE,   as.getBoolean("onUpdate"));
				entTr.removed(DiffEntity.TR_ONDELETE,   as.getBoolean("onDelete"));
				entTr.removed(DiffEntity.TR_FOREACH,    as.getString ("forEach"));

				if (!as.getString("when").equals(""))
					entTr.removed(DiffEntity.TR_WHEN,    as.getString ("when"));

				entTr.removed(DiffEntity.TR_CODE,       as.getString ("code"));

				diffSumm.add(DiffSummary.TRIGGER, entTr);

				keepParent = true;
			}
			else
			{
				AttribSet oldAS = oldTrigger.attrSet;
				AttribSet newAS = newTrigger.attrSet;

				DiffEntity entTr = new DiffEntity(oldName, DiffEntity.CHANGED, parent);

				addIfCase(entTr, diffString (DiffEntity.TR_ACTIVATION, "activation", oldAS, newAS));
				addIfCase(entTr, diffBoolean(DiffEntity.TR_ONINSERT,   "onInsert",   oldAS, newAS));
				addIfCase(entTr, diffBoolean(DiffEntity.TR_ONUPDATE,   "onUpdate",   oldAS, newAS));
				addIfCase(entTr, diffBoolean(DiffEntity.TR_ONDELETE,   "onDelete",   oldAS, newAS));
				addIfCase(entTr, diffString (DiffEntity.TR_FOREACH,    "forEach",    oldAS, newAS));
				addIfCase(entTr, diffString (DiffEntity.TR_WHEN,       "when",       oldAS, newAS));
				addIfCase(entTr, diffString (DiffEntity.TR_CODE,       "code",       oldAS, newAS));

				if (!entTr.isEmpty())
				{
					diffSumm.add(DiffSummary.TRIGGER, entTr);

					keepParent = true;
				}
			}
		}

		//------------------------------------------------------------------------

		for(int i=0; i<newTriggers.getChildCount(); i++)
		{
			Trigger newTrigger = (Trigger) newTriggers.getChild(i);
			String  newName    = newTrigger.attrSet.getString("name");
			Trigger oldTrigger = (Trigger) findNodeByName(newName, oldTriggers);

			if (oldTrigger == null)
			{
				AttribSet as = newTrigger.attrSet;

				DiffEntity entTr = new DiffEntity(as.getString("name"), DiffEntity.ADDED, parent);

				entTr.added(DiffEntity.TR_ACTIVATION, as.getString ("activation"));
				entTr.added(DiffEntity.TR_ONINSERT,   as.getBoolean("onInsert"));
				entTr.added(DiffEntity.TR_ONUPDATE,   as.getBoolean("onUpdate"));
				entTr.added(DiffEntity.TR_ONDELETE,   as.getBoolean("onDelete"));
				entTr.added(DiffEntity.TR_FOREACH,    as.getString ("forEach"));

				if (!as.getString("when").equals(""))
					entTr.added(DiffEntity.TR_WHEN,       as.getString ("when"));

				entTr.added(DiffEntity.TR_CODE,       as.getString ("code"));

				diffSumm.add(DiffSummary.TRIGGER, entTr);

				keepParent = true;
			}
		}

		return keepParent;
	}

	//---------------------------------------------------------------------------
	//---
	//--- TableRule diff
	//---
	//---------------------------------------------------------------------------

	private static boolean tabRuleDiff(DiffSummary diffSumm, TableRule oldRules, TableRule newRules, DiffEntity parent)
	{
		boolean existFields = false;

		for(int i=0; i<oldRules.getChildCount(); i++)
		{
			TableRule oldRule = (TableRule) oldRules.getChild(i);
			String    oldName = oldRule.attrSet.getString("name");
			TableRule newRule = (TableRule) findNodeByName(oldName, newRules);

			if (newRule == null)
			{
				AttribSet as = oldRule.attrSet;

				DiffEntity entTr = new DiffEntity(as.getString("name"), DiffEntity.REMOVED, parent);

				entTr.removed(DiffEntity.RU_USE, as.getBoolean("use"));

				if (!as.getString("rule").equals(""))
					entTr.removed(DiffEntity.RU_RULE, as.getString ("rule"));

				diffSumm.add(DiffSummary.TABLERULE, entTr);

				existFields = true;
			}
			else
			{
				AttribSet oldAS = oldRule.attrSet;
				AttribSet newAS = newRule.attrSet;

				DiffEntity entTr = new DiffEntity(oldName, DiffEntity.CHANGED, parent);

				addIfCase(entTr, diffBoolean(DiffEntity.RU_USE,  "use",  oldAS, newAS));
				addIfCase(entTr, diffString (DiffEntity.RU_RULE, "rule", oldAS, newAS));

				if (!entTr.isEmpty())
				{
					diffSumm.add(DiffSummary.TABLERULE, entTr);

					existFields = true;
				}
			}
		}

		//------------------------------------------------------------------------

		for(int i=0; i<newRules.getChildCount(); i++)
		{
			TableRule newRule = (TableRule) newRules.getChild(i);
			String    newName = newRule.attrSet.getString("name");
			TableRule oldRule = (TableRule) findNodeByName(newName, oldRules);

			if (oldRule == null)
			{
				AttribSet as = newRule.attrSet;

				DiffEntity entTr = new DiffEntity(as.getString("name"), DiffEntity.ADDED, parent);

				entTr.added(DiffEntity.RU_USE, as.getBoolean("use"));

				if (!as.getString("rule").equals(""))
					entTr.added(DiffEntity.RU_RULE, as.getString ("rule"));

				diffSumm.add(DiffSummary.TABLERULE, entTr);

				existFields = true;
			}
		}

		return existFields;
	}

	//---------------------------------------------------------------------------
	//---
	//--- View/procedure/function diff
	//---
	//---------------------------------------------------------------------------

	private static void viewDiff(DiffSummary diffSumm, Vector oldViews, Vector newViews)
	{
		sqlCodeDiff(diffSumm, DiffSummary.VIEW, oldViews, newViews);
	}

	//---------------------------------------------------------------------------

	private static void procDiff(DiffSummary diffSumm, Vector oldProcs, Vector newProcs)
	{
		sqlCodeDiff(diffSumm, DiffSummary.PROCEDURE, oldProcs, newProcs);
	}

	//---------------------------------------------------------------------------

	private static void funcDiff(DiffSummary diffSumm, Vector oldFuncs, Vector newFuncs)
	{
		sqlCodeDiff(diffSumm, DiffSummary.FUNCTION, oldFuncs, newFuncs);
	}

	//---------------------------------------------------------------------------

	private static void sqlCodeDiff(DiffSummary diffSumm, int catalog, Vector oldData, Vector newData)
	{
		for(int i=0; i<oldData.size(); i++)
		{
			AbstractNode oldObj  = (AbstractNode) oldData.get(i);
			String       oldName = oldObj.attrSet.getString("name");
			AbstractNode newObj  = findNodeByName(oldName, newData);

			String oldCode = oldObj.attrSet.getString("sqlCode").trim();

			if (newObj == null)
			{
				DiffEntity ent = new DiffEntity(oldName, DiffEntity.REMOVED);

				ent.removed(DiffEntity.PFV_SQLCODE, oldCode);

				diffSumm.add(catalog, ent);
			}
			else
			{
				String newCode = newObj.attrSet.getString("sqlCode").trim();

				if (!oldCode.equals(newCode))
				{
					DiffEntity ent = new DiffEntity(oldName, DiffEntity.CHANGED);

					ent.changed(DiffEntity.PFV_SQLCODE, oldCode, newCode);

					diffSumm.add(catalog, ent);
				}
			}
		}

		//------------------------------------------------------------------------

		for(int i=0; i<newData.size(); i++)
		{
			AbstractNode newObj  = (AbstractNode) newData.get(i);
			String       newName = newObj.attrSet.getString("name");
			AbstractNode oldObj  = findNodeByName(newName, oldData);

			String newCode = newObj.attrSet.getString("sqlCode");

			if (oldObj == null)
			{
				DiffEntity ent = new DiffEntity(newName, DiffEntity.ADDED);

				ent.added(DiffEntity.PFV_SQLCODE, newCode);

				diffSumm.add(catalog, ent);
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Sequence diff
	//---
	//---------------------------------------------------------------------------

	private static void seqDiff(DiffSummary diffSumm, Vector oldData, Vector newData)
	{
		for(int i=0; i<oldData.size(); i++)
		{
			AbstractNode oldObj  = (AbstractNode) oldData.get(i);
			String       oldName = oldObj.attrSet.getString("name");
			AbstractNode newObj  = findNodeByName(oldName, newData);

			if (newObj == null)
			{
				AttribSet as = oldObj.attrSet;

				DiffEntity ent = new DiffEntity(oldName, DiffEntity.REMOVED);

				ent.removed(DiffEntity.SQ_INCREM, as.getString("increment"));
				ent.removed(DiffEntity.SQ_MINVAL, as.getString("minValue"));
				ent.removed(DiffEntity.SQ_MAXVAL, as.getString("maxValue"));
				ent.removed(DiffEntity.SQ_START,  as.getString("start"));
				ent.removed(DiffEntity.SQ_CACHE,  as.getString("cache"));
				ent.removed(DiffEntity.SQ_CYCLE,  as.getBoolean("cycle"));
				ent.removed(DiffEntity.SQ_ORDER,  as.getBoolean("order"));

				diffSumm.add(DiffSummary.SEQUENCE, ent);
			}
			else
			{
				AttribSet oldAS = oldObj.attrSet;
				AttribSet newAS = newObj.attrSet;

				DiffEntity entSeq = new DiffEntity(oldName, DiffEntity.CHANGED);

				addIfCase(entSeq, diffString (DiffEntity.SQ_INCREM, "increment", oldAS, newAS));
				addIfCase(entSeq, diffString (DiffEntity.SQ_MINVAL, "minValue",  oldAS, newAS));
				addIfCase(entSeq, diffString (DiffEntity.SQ_MAXVAL, "maxValue",  oldAS, newAS));
				addIfCase(entSeq, diffString (DiffEntity.SQ_START,  "start",     oldAS, newAS));
				addIfCase(entSeq, diffString (DiffEntity.SQ_CACHE,  "cache",     oldAS, newAS));
				addIfCase(entSeq, diffBoolean(DiffEntity.SQ_CYCLE,  "cycle",     oldAS, newAS));
				addIfCase(entSeq, diffBoolean(DiffEntity.SQ_ORDER,  "order",     oldAS, newAS));

				if (!entSeq.isEmpty())
					diffSumm.add(DiffSummary.SEQUENCE, entSeq);
			}
		}

		//------------------------------------------------------------------------

		for(int i=0; i<newData.size(); i++)
		{
			AbstractNode newObj  = (AbstractNode) newData.get(i);
			String       newName = newObj.attrSet.getString("name");
			AbstractNode oldObj  = findNodeByName(newName, oldData);

			if (oldObj == null)
			{
				AttribSet as = newObj.attrSet;

				DiffEntity entSeq = new DiffEntity(as.getString("name"), DiffEntity.ADDED);

				entSeq.added(DiffEntity.SQ_INCREM, as.getString("increment"));
				entSeq.added(DiffEntity.SQ_MINVAL, as.getString("minValue"));
				entSeq.added(DiffEntity.SQ_MAXVAL, as.getString("maxValue"));
				entSeq.added(DiffEntity.SQ_START,  as.getString("start"));
				entSeq.added(DiffEntity.SQ_CACHE,  as.getString("cache"));
				entSeq.added(DiffEntity.SQ_CYCLE,  as.getBoolean("cycle"));
				entSeq.added(DiffEntity.SQ_ORDER,  as.getBoolean("order"));

				diffSumm.add(DiffSummary.SEQUENCE, entSeq);
			}
		}
	}
}

//==============================================================================
