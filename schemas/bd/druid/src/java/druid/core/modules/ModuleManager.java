//==============================================================================
//===
//===   ModuleManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.modules;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JPopupMenu;

import org.dlib.gui.MenuFactory;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DruidException;
import druid.interfaces.BasicModule;
import druid.interfaces.CodeGenModule;
import druid.interfaces.DatabaseIOModule;
import druid.interfaces.DatadictGenModule;
import druid.interfaces.DocsGenModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.JdbcPanelModule;
import druid.interfaces.RecordEditorModule;
import druid.interfaces.RecordIOModule;
import druid.interfaces.SqlAdapter;
import druid.interfaces.SqlGenModule;
import druid.interfaces.SummaryGenModule;
import druid.interfaces.TemplateGenModule;
import druid.interfaces.TreeNodeModule;

//==============================================================================

public class ModuleManager
{
	//--- classes of modules

	private static final Class clGroups[] =
	{
		CodeGenModule.class,
		DatadictGenModule.class,
		DocsGenModule.class,
		GenericGenModule.class,
		SqlGenModule.class,
		SummaryGenModule.class,
		TemplateGenModule.class,
		DatabaseIOModule.class,
		JdbcPanelModule.class,
		RecordIOModule.class,
		RecordEditorModule.class,
		TreeNodeModule.class,
		SqlAdapter.class
	};

	//---------------------------------------------------------------------------
	//--- this is an hashtable that contains all classes (hashtable)
	//--- each class contains class specific modules

	private static Hashtable htGroups;

	//---------------------------------------------------------------------------
	//---
	//--- Init method
	//---
	//---------------------------------------------------------------------------

	public static void init(String path)
	{
		//--- init structs

		htGroups = new Hashtable();

		for(int i=0; i<clGroups.length; i++)
			htGroups.put(clGroups[i], new Hashtable());

		//--- load modules from jars

		String[] mods = new File(path).list();

		if (mods == null) return;

		for(int i=0; i<mods.length; i++)
		{
			if (mods[i].toLowerCase().endsWith(".jar"))
				loadJar(path + "/" + mods[i]);
		}
	}

	//---------------------------------------------------------------------------

	private static void loadJar(String fileName)
	{
		//------------------------------------------------------------------------
		//--- open the jar

		try
		{
			JarFile jf = new JarFile(fileName);

			URL url = new URL("file:" + fileName);

			URLClassLoader ucl = new URLClassLoader(new URL[]{url}, ModuleManager.class.getClassLoader());

			for(Enumeration e=jf.entries(); e.hasMoreElements();)
			{
				JarEntry je = (JarEntry) e.nextElement();
				String name = je.getName();

				if (!je.isDirectory() && name.endsWith(".class"))
				{
					name = name.substring(0, name.length() -6);
					name = name.replace('/', '.');

					try
					{
						Object o = ucl.loadClass(name).newInstance();

						//------------------------------------------------------------------

						for(int i=0; i<clGroups.length; i++)
							if (clGroups[i].isInstance(o))
							{
								Hashtable   ht  = (Hashtable) htGroups.get(clGroups[i]);
								BasicModule mod = (BasicModule) o;

								if (ht.containsKey(mod.getId()))
									printErr(fileName, mod, "Id already in use");
								else
									ht.put(mod.getId(), mod);

								break;
							}
					}
					catch(Throwable t)
					{
//						System.out.println("THROWED : " + t);
//						t.printStackTrace();
					}
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Cannot open jar : " + fileName);
		}
	}

	//---------------------------------------------------------------------------

	private static void printErr(String file, BasicModule mod, String msg)
	{
		System.out.println("--- Module Error -------------------------");
		System.out.println("File   :" + file);
		System.out.println("Module :" + mod.getClass().getName());
		System.out.println("Message:" + msg);
		System.out.println("");
	}

	//---------------------------------------------------------------------------

/*	private static void addModule(BasicModule mod, Class c)
	{
		Hashtable ht  = (Hashtable) htGroups.get(c);
		ht.put(mod.getId(), mod);
	}*/

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static String getModuleGroup(BasicModule mod)
	{
		for(int i=0; i<clGroups.length; i++)
			if (clGroups[i].isInstance(mod))
			{
				String className = clGroups[i].getName();
				int    lastDot   = className.lastIndexOf(".");

				return className.substring(lastDot +1);
			}

		throw new DruidException(DruidException.ILL_ARG, "Unknown module type", mod);
	}

	//---------------------------------------------------------------------------

	public static String getAbsoluteID(BasicModule mod)
	{
		return getModuleGroup(mod) +"."+ mod.getId();
	}

	//---------------------------------------------------------------------------

	public static Enumeration getAllModules()
	{
		Vector v = new Vector();

		for(int i=0; i<clGroups.length; i++)
			for(Enumeration e=getModules(clGroups[i]); e.hasMoreElements();)
				v.addElement(e.nextElement());

		return v.elements();
	}

	//---------------------------------------------------------------------------

	public static Enumeration getModules(Class modClass)
	{
		Hashtable ht = (Hashtable) htGroups.get(modClass);

		if (ht == null)
			throw new DruidException(DruidException.ILL_ARG, "Class not found --> " + modClass);

		return ht.elements();
	}

	//---------------------------------------------------------------------------

	public static BasicModule getModule(Class modClass, String id)
	{
		Hashtable ht = (Hashtable) htGroups.get(modClass);

		if (ht == null)
			throw new DruidException(DruidException.ILL_ARG, "Class not found", modClass);

		return (BasicModule) ht.get(id);
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeNode modules methods
	//---
	//---------------------------------------------------------------------------

	public static void addTreeNodeModules(JPopupMenu popup, TreeViewNode node,
													  ActionListener al, int environ)
	{
		Vector v = new Vector();

		//--- collect module list

		for(Enumeration e = getModules(TreeNodeModule.class); e.hasMoreElements();)
		{
			TreeNodeModule mod = (TreeNodeModule) e.nextElement();

			if (mod.isNodeAccepted(node))
				if ((mod.getEnvironment() & environ) != 0) v.addElement(mod);
		}

		//------------------------------------------------------------------------
		//--- add entries to popup

		if (v.size() == 0) return;

		popup.addSeparator();

		for(int i=0; i<v.size(); i++)
		{
			TreeNodeModule mod = (TreeNodeModule) v.elementAt(i);

			popup.add(MenuFactory.createItem(mod.getId(), mod.getPopupText(), al,
														mod.isNodeEnabled(node)));
		}
	}

	//---------------------------------------------------------------------------

	public static void dispatchTreeNodeEvent(Frame f, String cmd, TreeViewNode node)
	{
		for(Enumeration e = getModules(TreeNodeModule.class); e.hasMoreElements();)
		{
			TreeNodeModule mod = (TreeNodeModule) e.nextElement();

			if (mod.getId().equals(cmd))
			{
				mod.nodeSelected(f, node);
				return;
			}
		}
	}
}

//==============================================================================
