//==============================================================================
//===
//===   DataLib
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.DataTypes;
import druid.data.er.ErEntity;
import druid.data.er.ErView;

//==============================================================================

public class DataLib
{
	//---------------------------------------------------------------------------

	public static void syncAllFields(AbstractNode node)
	{
		DatabaseNode dbNode = node.getDatabase();

		for(Enumeration nodes = node.preorderEnumeration(); nodes.hasMoreElements();)
		{
			AbstractNode aNode = (AbstractNode) nodes.nextElement();

			if (aNode instanceof FieldNode)
				syncField(dbNode, (FieldNode) aNode);
		}
	}

	//---------------------------------------------------------------------------

	/** This method syncs the contents of the field's fieldAttribs with attrib
	  * entries in the databaseNode. In particular:
	  * - it deletes attribs in the fieldNode that have been removed from the database
	  * - it adds new attribs in the fieldNode if some attribs have been added to the db
	  *
	  * Note that this method doesn't set the dataChanged bit
	  */

	public static void syncField(DatabaseNode dbNode, FieldNode fNode)
	{
		FieldAttribs fAttr  = dbNode.fieldAttribs;
		AttribSet    attrAS = fNode.fieldAttribs;

		//--- scan field's fieldAttribs to remove unused entries
		//--- (for example because they have been removed)

		Vector v = new Vector();

		for(Enumeration e=attrAS.attribs(); e.hasMoreElements();)
			v.addElement(e.nextElement());

		//--- previous step is necessary because during enum we must remove attribs

		for(int j=0; j<v.size(); j++)
		{
			String name = (String) v.elementAt(j);

			boolean found = false;

			for(int k=0; k<fAttr.size(); k++)
			{
				if (name.equals("" + fAttr.get(k).getInt("id")))
				{
					found = true;
					break;
				}
			}

			if (!found)
				attrAS.removeAttrib(name);
		}

		//--- ok, now add new attribs if they don't exist
		//--- or update existing ones

		for(int j=0; j<fAttr.size(); j++)
		{
			String id   = "" + fAttr.get(j).getInt("id");
			String type = fAttr.get(j).getString("type");

			if (!attrAS.contains(id))
			{
				//--- attrib not found. we must add it

				if (type.equals(FieldAttribs.TYPE_BOOL))
					attrAS.addAttrib(id, false);

				else if (type.equals(FieldAttribs.TYPE_INT))
					attrAS.addAttrib(id, 0);

				else if (type.equals(FieldAttribs.TYPE_STRING))
					attrAS.addAttrib(id, "");

				else
					throw new DruidException(DruidException.ILL_ARG, "Unknown FieldAttribs type", type);
			}
			else
			{
				//--- attrib found. check if type is ok

				Object obj = attrAS.getData(id);

				if ((obj instanceof Boolean) && (type.equals(FieldAttribs.TYPE_BOOL)))
					continue;

				if ((obj instanceof Integer) && (type.equals(FieldAttribs.TYPE_INT)))
					continue;

				if ((obj instanceof String) && (type.equals(FieldAttribs.TYPE_STRING)))
					continue;

				//--- mhmm, we must reset the type

				attrAS.removeAttrib(id);

				if (type.equals(FieldAttribs.TYPE_BOOL))
					attrAS.addAttrib(id, false);

				else if (type.equals(FieldAttribs.TYPE_INT))
					attrAS.addAttrib(id, 0);

				else if (type.equals(FieldAttribs.TYPE_STRING))
					attrAS.addAttrib(id, "");

				else
					throw new DruidException(DruidException.ILL_ARG, "Unknown FieldAttribs type", type);
			}
		}
	}

	//---------------------------------------------------------------------------

	public static Vector getFieldRow(FieldNode fNode)
	{
		FieldAttribs dbFAttr= fNode.getDatabase().fieldAttribs;
		AttribSet    fAS    = fNode.attrSet;
		AttribSet    attrAS = fNode.fieldAttribs;

		Vector v = new Vector();

		v.addElement(fAS.getString("name"));
		v.addElement(DataTypeLib.getTypeDef(fNode));

		for(int j = 0; j < dbFAttr.size(); j++)
		{
			String id = "" + dbFAttr.get(j).getInt("id");

			v.addElement(attrAS.getData(id));
		}
		return v;
	}

	//---------------------------------------------------------------------------

	public static void find(TreeView tree, String pattern)
	{
		TreeViewNode selNode   = tree.getSelectedNode();
		TreeViewNode firstNode = selNode;

		if (selNode != null)
			selNode = (TreeViewNode)selNode.getNextNode();

		pattern = pattern.toLowerCase();

		while(true)
		{
			while(selNode != null)
			{
				String nodeText = selNode.getText().toLowerCase();

				if (nodeText.indexOf(pattern) != -1)
				{
					selNode.select();
					return;
				}

				if (selNode == firstNode)
				{
					JOptionPane.showMessageDialog(tree, "'" + pattern + "' not found",
													"Search result", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				selNode = (TreeViewNode)selNode.getNextNode();

				if (selNode == null && firstNode == null)
				{
					JOptionPane.showMessageDialog(tree, "'" + pattern + "' not found",
													"Search result", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			TreeViewNode rootNode = tree.getRootNode();

			//-- if there are not databases we must return

			if (rootNode.getChildCount() == 0)
				return;

			selNode = rootNode.getChild(0);
		}
	}

	//---------------------------------------------------------------------------

	/** Order tables depending on their dependances. On success the srcTables
	  * vector is empty and the returned vector contains the tables ordered.
	  * if the method exits and the srcVector not empty then the srcVector
	  * contains tables that could not be resolved.
	  */

	public static Vector getOrderedTables(Vector srcTables)
	{
		Vector desTables = new Vector();

		//------------------------------------------------------------------------
		//--- order tables depending on their dependances

		while(srcTables.size() != 0)
		{
			boolean moved = false;

			for(int i=0; i<srcTables.size(); i++)
			{
				if (canBeMoved((TableNode)srcTables.elementAt(i), desTables, srcTables))
				{
					moved = true;
					desTables.add(srcTables.get(i));
					srcTables.removeElementAt(i);
					i--;
				}
			}
			if (!moved)
				return desTables;
		}

		return desTables;
	}

	//---------------------------------------------------------------------------

	private static boolean canBeMoved(TableNode tableNode, Vector desTables, Vector srcTables)
	{
		int tId = tableNode.attrSet.getInt("id");

		for(int i=0; i<tableNode.getChildCount(); i++)
		{
			FieldNode fieldNode = (FieldNode)tableNode.getChild(i);
			AttribSet as        = fieldNode.attrSet;

			//--- is field an fkey ? ---

			if (as.getInt("type") == 0 && as.getInt("refTable") != tId)
			{
				boolean isContained = false;

				//--- check if fkey-table is in tables ---

				for(int j=0; j<desTables.size(); j++)
				{
					TableNode currTable = ((TableNode) desTables.get(j));

					if (as.getInt("refTable") == currTable.attrSet.getInt("id"))
						isContained = true;
				}

				//--- if the fkey is not contained into the destination table vector
				//--- we cannot return false because the fkey can be outside the set
				//--- src + dest (for example because the user selected a folder which
				//--- doesn't have all fkeys)

				if (!isContained)
					for(int j=0; j<srcTables.size(); j++)
					{
						TableNode currTable = ((TableNode) srcTables.get(j));

						if (as.getInt("refTable") == currTable.attrSet.getInt("id"))
							return false;
					}
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------

	/** Tells if the given field is a primary key for the table it belongs to.
	  * The primary key is an attrib which has "primary key" as sql name.
	  */

	public static boolean isPrimaryKey(FieldNode node)
	{
		Vector vAttribKeys = new Vector();

		DatabaseNode dbNode = node.getDatabase();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are primary keys

		for(int i=0, size=attribs.size(); i<size; i++)
		{
			AttribSet as      = attribs.get(i);
			String    sqlName = as.getString("sqlName").toLowerCase();
			String    type    = as.getString("type");

			if (sqlName.equals("primary key") && type.equals(FieldAttribs.TYPE_BOOL))
				vAttribKeys.addElement("" + as.getInt("id"));
		}

		//------------------------------------------------------------------------
		//--- scan primary keys to see if field has one set

		for(int i=0, size=vAttribKeys.size(); i<size; i++)
		{
			String attribId = (String) vAttribKeys.elementAt(i);

			if (node.fieldAttribs.getBool(attribId))
				return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	/** Tells if the given field is a not null for the table it belongs to.
	  * The "not null" is an attrib which has "not null" as sql name.
	  */

	public static boolean isNotNull(FieldNode node)
	{
		DatabaseNode dbNode = node.getDatabase();

		Vector vAttribNotNull = new Vector();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are not null

		for(int i=0; i< attribs.size(); i++)
		{
			AttribSet as      = attribs.get(i);
			String    sqlName = as.getString("sqlName").toLowerCase();
			String    type    = as.getString("type");

			if (sqlName.equals("not null") && type.equals(FieldAttribs.TYPE_BOOL))
				vAttribNotNull.addElement("" + as.getInt("id"));
		}

		//------------------------------------------------------------------------
		//--- scan not null attribs to see if field has one set

		for(int i=0; i<vAttribNotNull.size(); i++)
		{
			String attribId = (String) vAttribNotNull.elementAt(i);

			if (node.fieldAttribs.getBool(attribId))
				return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	/** Tells if the given field is unique for the table it belongs to.
	  * The "unique" is an attrib which has "unique" as sql name.
	  */

	public static boolean isUnique(FieldNode node)
	{
		DatabaseNode dbNode = node.getDatabase();

		Vector vAttribUnique = new Vector();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are unique

		for (int i = 0; i < attribs.size(); i++)
		{
			AttribSet as      = attribs.get(i);
			String    sqlName = as.getString("sqlName").toLowerCase();
			String    type    = as.getString("type");

			if (sqlName.equals("unique") && type.equals(FieldAttribs.TYPE_BOOL))
				vAttribUnique.addElement("" + as.getInt("id"));
		}

		//------------------------------------------------------------------------
		//--- scan unique attribs to see if field has one set

		for(int i=0; i<vAttribUnique.size(); i++)
		{
			String attribId = (String) vAttribUnique.elementAt(i);

			if (node.fieldAttribs.getBool(attribId))
				return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	/** Returns the value for the 'default' attrib (or null if the attrib is not defined).
	  * The 'default' is an attrib which has 'default' as sql name and is of string type.
	  */

	public static String getDefaultValue(FieldNode node)
	{
		DatabaseNode dbNode = node.getDatabase();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are not null

		for (int i = 0; i < attribs.size(); i++)
		{
			AttribSet as      = attribs.get(i);
			String    sqlName = as.getString("sqlName").toLowerCase();
			String    type    = as.getString("type");

			if (sqlName.equals("default") && type.equals(FieldAttribs.TYPE_STRING))
			{
				int id = as.getInt("id");

				String value = node.fieldAttribs.getString(id +"");

				if (!value.trim().equals(""))
					return value;
			}
		}

		return null;
	}

	//---------------------------------------------------------------------------

	/** Get a vector of database objects (tables, views, etc...) and order them
	  * on their name
	  */

	public static void sortObjects(Vector v)
	{
		boolean bChanged = true;

		AbstractNode node1, node2;

		String name1, name2;

		while(bChanged)
		{
			bChanged = false;

			for(int i=0; i<v.size()-1; i++)
			{
				node1 = (AbstractNode) v.elementAt(i);
				node2 = (AbstractNode) v.elementAt(i+1);

				name1 = node1.attrSet.getString("name");
				name2 = node2.attrSet.getString("name");

				if (name1.compareToIgnoreCase(name2) > 0)
				{
					bChanged = true;

					v.setElementAt(node2, i);
					v.setElementAt(node1, i+1);
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	/** Return all tables that have a fkey to the given table
	  */

	public static Vector<TableNode> getReferences(TableNode node, boolean includeItself)
	{
		int tId = node.attrSet.getInt("id");

		DatabaseNode dbNode = node.getDatabase();

		Vector<TableNode> res = new Vector<TableNode>();

		for(Enumeration e = dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode n = (AbstractNode) e.nextElement();

			if (n instanceof TableNode)
			{
				boolean bOk = false;

				for(int i=0; i<n.getChildCount(); i++)
				{
					AbstractNode f = (AbstractNode) n.getChild(i);

					int dtype = f.attrSet.getInt("type");
					int reft  = f.attrSet.getInt("refTable");

					if (dtype == 0 && reft == tId)
					{
						bOk = true;
						break;
					}
				}

				//--- must we return the table ?

				if (bOk)
				{
					if (node == n && !includeItself) continue;

					res.add((TableNode)n);
				}
			}
		}

		return res;
	}

	//---------------------------------------------------------------------------

	public static int getPrimaryKey(FieldAttribs fa, boolean addIfNotExists)
	{
		for (int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			int id = as.getInt("id");

			String sqlName = as.getString("sqlName").toLowerCase().trim();
			String type    = as.getString("type");
			String scope   = as.getString("scope");

			if (sqlName.equals("primary key")       &&
				 type.equals(FieldAttribs.TYPE_BOOL) &&
				 scope.equals(FieldAttribs.SCOPE_TABLE)) return id;
		}

		if (addIfNotExists)
		{
			AttribSet as = fa.append();

			as.setString("name",    "PrKey");
			as.setString("sqlName", "primary key");
			as.setString("scope",   FieldAttribs.SCOPE_TABLE);

			return as.getInt("id");
		}

		return 0;
	}

	//---------------------------------------------------------------------------

	public static int getNotNull(FieldAttribs fa, boolean addIfNotExists)
	{
		for (int i=0; i<fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			int id = as.getInt("id");

			String sqlName = as.getString("sqlName").toLowerCase().trim();
			String type    = as.getString("type");
			String scope   = as.getString("scope");

			if (sqlName.equals("not null")          &&
				 type.equals(FieldAttribs.TYPE_BOOL) &&
				 scope.equals(FieldAttribs.SCOPE_FIELD)) return id;
		}

		if (addIfNotExists)
		{
			AttribSet as = fa.append();

			as.setString("name",    "NotN");
			as.setString("sqlName", "not null");

			return as.getInt("id");
		}

		return 0;
	}

	//---------------------------------------------------------------------------

	public static int getDefault(FieldAttribs fa, boolean addIfNotExists)
	{
		for ( int i = 0; i < fa.size(); i++)
		{
			AttribSet as = fa.get(i);

			int id = as.getInt("id");

			String sqlName = as.getString("sqlName").toLowerCase().trim();
			String type    = as.getString("type");
			String scope   = as.getString("scope");

			if (sqlName.equals("default")           &&
				 type.equals(FieldAttribs.TYPE_STRING) &&
				 scope.equals(FieldAttribs.SCOPE_FIELD)) return id;
		}

		if (addIfNotExists)
		{
			AttribSet as = fa.append();

			as.setString("name",    "Def");
			as.setString("sqlName", "default");
			as.setString("type",    FieldAttribs.TYPE_STRING);

			return as.getInt("id");
		}

		return 0;
	}

	//---------------------------------------------------------------------------

	/** Return the number of fields that use the given field attrib
	  */

	public static Vector fieldAttribUsage(DatabaseNode dbNode, int attribID)
	{
		Vector vResult = new Vector();

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (node instanceof FieldNode)
			{
				FieldNode field = (FieldNode) node;

				Object data = field.fieldAttribs.getData(attribID +"");

				if (data instanceof Boolean)
				{
					boolean value = ((Boolean) data).booleanValue();

					if (value)
						vResult.add(field);
				}

				else if (data instanceof String)
				{
					if (!data.toString().equals(""))
						vResult.add(field);
				}

				else if (data instanceof Integer)
				{
					int value = ((Integer) data).intValue();

					if (value != 0)
						vResult.add(field);
				}

				else
					throw new DruidException(DruidException.INC_STR, "Unknown object instance !!!", data);
			}
		}

		return vResult;
	}

	//---------------------------------------------------------------------------

	public static void fieldAttribMerge(DatabaseNode dbNode, int srcAttribID, int dstAttribID)
	{
		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (node instanceof FieldNode)
			{
				FieldNode field = (FieldNode) node;

				Object srcData = field.fieldAttribs.getData(srcAttribID +"");
				Object dstData = null;

				if (srcData instanceof String)
					dstData = srcData.toString();

				else if (srcData instanceof Integer)
					dstData = new Integer(srcData.toString());

				else if (srcData instanceof Boolean)
					dstData = Boolean.valueOf(srcData.toString());

				field.fieldAttribs.setData(dstAttribID +"", dstData);
			}
		}
	}

	//---------------------------------------------------------------------------

	/** Remaps the given node and all its children: if the node contains an "id"
	  * attrib it is changed into a new one. Usually, remap occurs after a paste/drag
	  * operation.
	  *
	  * @param returnMapping if true, the method returns an hashtable (null otherwise)
	  * @return an hashtable that will contain the mapping between old ids (String)
	  *         and new ones (Integer)
	  */

	public static Hashtable remapIds(AbstractNode node, boolean returnMapping)
	{
		Hashtable mapping = new Hashtable();

		for (Enumeration e=node.preorderEnumeration(); e.hasMoreElements();)
		{
			AttribSet as = ((AbstractNode) e.nextElement()).attrSet;

			//--- first, remap id

			if (as.contains("id"))
			{
				int oldId = as.getInt("id");
				int newId = Serials.get();
				as.setInt("id", newId);

				if (returnMapping)
					mapping.put(oldId +"", new Integer(newId));
			}
		}

		return returnMapping ? mapping : null;
	}

	//---------------------------------------------------------------------------

	/** Used during a cut & paste operation. 'node' represents a subtree. If a field
	  * (descendant of 'node') is a foreign keys which table's id is in mapping,
	  * then the old id is changed into the new one.
	  */

	public static void remapFKeys(AbstractNode node, Hashtable mapping)
	{
		for (Enumeration e=node.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode currNode = (AbstractNode) e.nextElement();

			if (currNode instanceof FieldNode)
			{
				FieldNode field = (FieldNode) currNode;

				if (field.isFkey())
				{
					AttribSet as = currNode.attrSet;

					int refTable = as.getInt("refTable");
					int refField = as.getInt("refField");

					if (mapping.containsKey(refTable +""))
					{
						Integer newRefTable = (Integer) mapping.get(refTable +"");
						Integer newRefField = (Integer) mapping.get(refField +"");

						as.setInt("refTable", newRefTable.intValue());
						as.setInt("refField", newRefField.intValue());
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	/** Adjust nodes if 'node' is pasted into a different db. Adjusting regards:
	  *  - creation of datatypes that don't exist
	  *  - removing of foreign keys to tables that don't exist
	  *  - setting/unsetting of field attribs for fields
	  *
	  * Note that 'node' has already been added to desDb
	  */

	public static void migrateNodes(AbstractNode node, DatabaseNode srcDB, DatabaseNode desDB)
	{
		HashSet ids = getIds(node);

		DataTypes srcDT = srcDB.dataTypes;
		DataTypes desDT = desDB.dataTypes;

		for (Enumeration e=node.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode currNode = (AbstractNode) e.nextElement();

			if (currNode instanceof FieldNode)
			{
				FieldNode field = (FieldNode) currNode;
				AttribSet as    = field.attrSet;

				if (field.isFkey())
				{
					int refTable = as.getInt("refTable");
					int refField = as.getInt("refField");

					if (!ids.contains(refTable +"") || !ids.contains(refField +""))
					{
						as.setInt("type",     0);
						as.setInt("refTable", 0);
						as.setInt("refField", 0);
					}
				}
				else
				{
					int type = as.getInt("type");

					as.setInt("type", DataTypeLib.migrateDataType(srcDT.getTypeFromId(type), desDT));
				}

				//--- if the field is pasted into a different db we  must set/unset the
				//--- field attribs too

				syncField(desDB, field);
			}
		}
	}

	//---------------------------------------------------------------------------

	public static HashSet getIds(AbstractNode node)
	{
		HashSet ids = new HashSet();

		for (Enumeration e=node.preorderEnumeration(); e.hasMoreElements();)
		{
			AttribSet as = ((AbstractNode) e.nextElement()).attrSet;

			//--- first, remap id

			if (as.contains("id"))
			{
				int id = as.getInt("id");

				ids.add(id +"");
			}
		}

		return ids;
	}

	//---------------------------------------------------------------------------

	public static void remapERViews(ErView rootView, Hashtable mapping)
	{
		for (Enumeration e=rootView.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode currNode = (AbstractNode) e.nextElement();

			if (currNode instanceof ErEntity)
			{
				ErEntity erEnt = (ErEntity) currNode;

				for(int i=0; i<erEnt.getTableNum(); i++)
				{
					int oldId = erEnt.getTableAt(i);

					Integer newId = (Integer) mapping.get(oldId +"");

					if (newId != null)
						erEnt.setTableAt(i, newId.intValue());
				}
			}
		}
	}

	/** Tells if the given field is an unsigned integer type.
	  * Unsigned is an attrib which has "unsigned" as sql name.
	  * Note: Won't return true if unsigned is part of the data
	  * type name e.g. "INT UNSIGNED".
	  */
	public static boolean isUnsigned(FieldNode node)
	{
		Vector vAttribKeys = new Vector();

		DatabaseNode dbNode = node.getDatabase();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are unsigned

		for(int i=0, size=attribs.size(); i<size; i++)
		{
			AttribSet as      = attribs.get(i);
			String    sqlName = as.getString("sqlName").toLowerCase();
			String    type    = as.getString("type");
			if (sqlName.toLowerCase().equals("unsigned") && type.equals(FieldAttribs.TYPE_BOOL)) { 
				vAttribKeys.addElement("" + as.getInt("id"));				
			}
		}

		//------------------------------------------------------------------------
		//--- scan attributes to see if field has unsigned set

		for(int i=0, size=vAttribKeys.size(); i<size; i++)
		{
			String attribId = (String) vAttribKeys.elementAt(i);
			if (node.fieldAttribs.getBool(attribId))
				return true;
		}

		return false;
	}	
	
	/** Tells if the given field is indexed either by itself.
	  * or as part of a multiple field index.
	  */
	public static boolean isIndexed(FieldNode node)
	{
		Vector vAttribIndecies = new Vector();

		DatabaseNode dbNode = node.getDatabase();

		AttribList attribs = dbNode.fieldAttribs;

		//------------------------------------------------------------------------
		//--- retrieve attribs that are indicies (normal, unique, and fulltext)

		for(int i=0, size=attribs.size(); i<size; i++)
		{
			AttribSet as      = attribs.get(i);
			String    type    = as.getString("scope");

			if (type.equals(FieldAttribs.SCOPE_INDEX) ||type.equals(FieldAttribs.SCOPE_UINDEX) ||type.equals(FieldAttribs.SCOPE_FTINDEX) )
				vAttribIndecies.addElement("" + as.getInt("id"));
		}

		//------------------------------------------------------------------------
		//--- scan indicies to see if field is member of one

		for(int i=0, size=vAttribIndecies.size(); i<size; i++)
		{
			String attribId = (String) vAttribIndecies.elementAt(i);

			if (node.fieldAttribs.getBool(attribId))
				return true;
		}

		return false;
	}	
}

//==============================================================================
