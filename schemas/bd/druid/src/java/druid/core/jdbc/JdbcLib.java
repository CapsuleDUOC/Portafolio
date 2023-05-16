//==============================================================================
//===
//===   JdbcLib
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.sql.DatabaseMetaData;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.ProgressDialog;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.ContainerEntity;
import druid.core.jdbc.entities.DatabaseEntity;
import druid.core.jdbc.entities.SchemaEntity;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.interfaces.SqlGenModule;

//==============================================================================

public class JdbcLib
{
	//---------------------------------------------------------------------------
	//---
	//--- Conversion methods
	//---
	//---------------------------------------------------------------------------

	public static String convertParam(Object o)
	{
		if (o == null) return "???";

		String s = o.toString();

		if (s.equals("" + DatabaseMetaData.procedureColumnUnknown)) return "Unknown";
		if (s.equals("" + DatabaseMetaData.procedureColumnIn))      return "In";
		if (s.equals("" + DatabaseMetaData.procedureColumnInOut))   return "In/ Out";
		if (s.equals("" + DatabaseMetaData.procedureColumnOut))     return "Out";
		if (s.equals("" + DatabaseMetaData.procedureColumnReturn))  return "Return";
		if (s.equals("" + DatabaseMetaData.procedureColumnResult))  return "Result";

		return "(meta err)";
	}

	//---------------------------------------------------------------------------

	public static boolean convertBool(Object o)
	{
		if (o == null) return false;

		String s = o.toString();

		return (s.equals("1") || s.equals("true"));
	}

	//---------------------------------------------------------------------------

	public static boolean convertNotBool(Object o)
	{
		if (o == null) return false;

		String s = o.toString();

		return !(s.equals("1") || s.equals("true"));
	}

	//---------------------------------------------------------------------------

	public static String convertString(Object o)
	{
		if (o == null) return null;

		return o.toString();
	}

	//---------------------------------------------------------------------------

	public static boolean convertNullField(Object o)
	{
		if (o == null) return false;

		String s = o.toString();

		return !s.equals("" + DatabaseMetaData.columnNullable);
	}

	//---------------------------------------------------------------------------

	public static boolean convertNullType(Object o)
	{
		if (o == null) return false;

		String s = o.toString();

		return (s.equals("" + DatabaseMetaData.typeNullable));
	}

	//---------------------------------------------------------------------------

	public static String convertSize(Object size, Object decim)
	{
		if (size == null) return null;

		String sSize = size.toString();

		if (decim == null) return sSize;

		String sDecim = decim.toString();

		try
		{
			if (Integer.parseInt(sDecim) <= 0)
				return sSize;
		}
		catch(NumberFormatException e)
		{
			return sSize;
		}

		return (sSize + "," + sDecim);
	}

	//---------------------------------------------------------------------------

	public static String convertOnRule(String s)
	{
		if (s == null) return "No Action";

		if (s.equals("" + DatabaseMetaData.importedKeyNoAction))
			return "No Action";

		if (s.equals("" + DatabaseMetaData.importedKeyCascade))
			return "Cascade";

		if (s.equals("" + DatabaseMetaData.importedKeySetNull))
			return "Set Null";

		if (s.equals("" + DatabaseMetaData.importedKeySetDefault))
			return "Set Default";

		return "No Action";
	}

	//---------------------------------------------------------------------------

	public static String convertOnRuleInt(String s)
	{
		if (s == null) return FieldNode.NOACTION;

		if (s.equals("" + DatabaseMetaData.importedKeyNoAction))
			return FieldNode.NOACTION;

		if (s.equals("" + DatabaseMetaData.importedKeyCascade))
			return FieldNode.CASCADE;

		if (s.equals("" + DatabaseMetaData.importedKeySetNull))
			return FieldNode.SETNULL;

		if (s.equals("" + DatabaseMetaData.importedKeySetDefault))
			return FieldNode.SETDEFAULT;

		return FieldNode.NOACTION;
	}

	//---------------------------------------------------------------------------

	public static boolean convertSearch(Object o)
	{
		if (o == null) return false;

		String s = o.toString();

		return (s.equals("" + DatabaseMetaData.typeSearchable));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Drops objects in a schema (or in the db if schemas are not supported)
	//---
	//---------------------------------------------------------------------------

	public static boolean dropEntities(Component c, final TreeViewNode node)
	{
		if (!loadChildren(node)) return true;

		String title = "Dropping objects in " + ((AbstractEntity)node).getFullName() + "...";

		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(c), title);

		final Vector err = new Vector();

		//------------------------------------------------------------------------

		Runnable run = new Runnable()
		{
			public void run()
			{
				while(node.getChildCount() != 0)
				{
					Vector v = new Vector();

					//---------------------------------------------------------------
					//--- collect all elements to remove

					Enumeration e = node.preorderEnumeration();
					e.nextElement();

					while(e.hasMoreElements())
					{
						TreeViewNode n = (TreeViewNode) e.nextElement();

						if (!(n instanceof ContainerEntity))
							v.addElement(n);
					}

					if (v.size() == 0) break;

					//---------------------------------------------------------------
					//--- remove collected elemenst

					progrDial.reset(v.size());

					boolean bDeleted = false;

					for(int i=0; i<v.size(); i++)
						bDeleted = bDeleted || dropEntityI((AbstractEntity)v.elementAt(i));

					//---------------------------------------------------------------
					//--- check if we must exit or must loop again

					if (!bDeleted)
					{
						err.addElement(null);
						break;
					}
				}

				progrDial.stop();
			}

			//---------------------------------------------------------------------

			private boolean dropEntityI(AbstractEntity n)
			{
				try
				{
					progrDial.advance(n.getFullName());
					n.getJdbcConnection().getSqlAdapter().dropEntity(n);
					n.removeFromParent();

					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			}
		};

		//------------------------------------------------------------------------

		progrDial.run(run);

		if (node instanceof DatabaseEntity || node instanceof SchemaEntity)
			((AbstractEntity)node).reset();

		else if (node instanceof ContainerEntity)
		{
			AbstractEntity parent = (AbstractEntity)node.getParent();

			parent.reset();
			parent.select();
		}

		return (err.size() == 0);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Load all children of a given jdbc entity from the database
	//---
	//---------------------------------------------------------------------------

	public static boolean loadChildren(TreeViewNode node)
	{
		GuiUtil.setWaitCursor(node.getTree(), true);

		try
		{
			if (!loadChild(node)) return false;

			for(int i=0; i<node.getChildCount(); i++)
			{
				TreeViewNode child = node.getChild(i);

				if (!loadChild(child))
					return false;
				else
				{
					for(int j=0; j<child.getChildCount(); j++)
						if (!loadChild(child.getChild(j)))
							return false;
				}
			}

			return true;
		}
		finally
		{
			GuiUtil.setWaitCursor(node.getTree(), false);
		}
	}

	//---------------------------------------------------------------------------

	private static boolean loadChild(TreeViewNode node)
	{
		//--- step 1 : we must expand all containers

		Vector vCont = new Vector();

		for(Enumeration e = node.preorderEnumeration(); e.hasMoreElements();)
		{
			TreeViewNode n = (TreeViewNode) e.nextElement();

			if (n instanceof ContainerEntity)
				vCont.addElement(n);
		}

		//--- ok, containers collected. now expand them

		for(int i=0; i<vCont.size(); i++)
		{
			ContainerEntity    ce  = (ContainerEntity) vCont.elementAt(i);
			TreeExpansionEvent tei = new TreeExpansionEvent(ce.getTree(), new TreePath(ce));

			try
			{
				ce.nodeWillExpand(tei);
			}
			catch(ExpandVetoException e)
			{
				return false;
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------

	public static void fillRebuildMenu(JMenu menu, AbstractNode node, String action, ActionListener al)
	{
		DatabaseNode dbNode = node.getDatabase();

		Enumeration e = ModuleManager.getModules(SqlGenModule.class);

		for(; e.hasMoreElements();)
		{
			SqlGenModule sqlMod = (SqlGenModule) e.nextElement();

			if (dbNode.modsUsage.contains(sqlMod))
				menu.add(MenuFactory.createItem(action+sqlMod.getId(), sqlMod.getFormat(), al, true));
		}
	}
}

//==============================================================================
