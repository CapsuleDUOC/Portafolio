//==============================================================================
//===
//===   ModulesConfig
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import java.util.Enumeration;
import java.util.Hashtable;

import org.dlib.xml.XmlElement;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.modules.ModuleManager;
import druid.interfaces.BasicModule;

//==============================================================================

public class ModulesConfig
{
	public Hashtable htGroups = new Hashtable();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ModulesConfig() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void remove(BasicModule mod)
	{
		String group = ModuleManager.getModuleGroup(mod);

		//--- retrieve hashtable for all modules that belong to the same group

		Hashtable htGrp = (Hashtable) htGroups.get(group);

		if (htGrp == null)
			return;

		//--- now remove module settings

		htGrp.remove(mod.getId());
	}

	//---------------------------------------------------------------------------

	public String getValue(BasicModule mod, String name, String defValue)
	{
		String res = (String) getModuleHT(mod).get(name);

		return (res != null) ? res : defValue;
	}

	//---------------------------------------------------------------------------

	public int getValue(BasicModule mod, String name, int defValue)
	{
		String value = getValue(mod, name, null);

		if (value == null) 	return defValue;
			else					return Integer.parseInt(value);
	}

	//---------------------------------------------------------------------------

	public boolean getValue(BasicModule mod, String name, boolean defValue)
	{
		String value = getValue(mod, name, null);

		if (value == null) 	return defValue;
			else					return "Y".equals(value);
	}

	//---------------------------------------------------------------------------

	public AttribSet getAttribSet(BasicModule mod, String name, AttribSet defValue)
	{
		Hashtable ht = getModuleHT(mod);
		AttribSet as = (AttribSet) ht.get(name);

		if (as == null)
		{
			as = defValue.duplicate();
			ht.put(name, as);
		}

		else
		{
			//--- a sync is required because different druid releases may have
			//--- a different set of attribs

			as.sync(defValue);
		}

		return as;
	}

	//---------------------------------------------------------------------------

	public AttribList getAttribList(BasicModule mod, String name, AttribList defValue)
	{
		Hashtable  ht = getModuleHT(mod);
		AttribList al = (AttribList) ht.get(name);

		if (al == null)
		{
			al = defValue.duplicate();
			ht.put(name, al);
		}

		else
		{
			//--- a sync is required because different druid releases may have
			//--- a different set of attribs

			al.sync(defValue);
		}

		return al;
	}

	//---------------------------------------------------------------------------

	public XmlElement getXmlElement(BasicModule mod, String name, XmlElement defValue)
	{
		Hashtable  ht = getModuleHT(mod);
		XmlElement el = (XmlElement) ht.get(name);

		if (el == null)
		{
			el = defValue.duplicate();
			ht.put(name, el);
		}

		return el;
	}

	//---------------------------------------------------------------------------

	public String getValue(BasicModule mod, String name)
	{
		return getValue(mod, name, "");
	}

	//---------------------------------------------------------------------------

	public boolean getBoolValue(BasicModule mod, String name)
	{
		return getValue(mod, name, false);
	}

	//---------------------------------------------------------------------------
	//--- Setters
	//---------------------------------------------------------------------------

	public void setValue(BasicModule mod, String name, String value)
	{
		getModuleHT(mod).put(name, value);
	}

	//---------------------------------------------------------------------------

	public void setValue(BasicModule mod, String name, boolean yesno)
	{
		setValue(mod, name, yesno ? "Y" : "N");
	}

	//---------------------------------------------------------------------------

	public void setValue(BasicModule mod, String name, int value)
	{
		setValue(mod, name, Integer.toString(value));
	}

	//---------------------------------------------------------------------------

	public ModulesConfig duplicate()
	{
		ModulesConfig mc = new ModulesConfig();

		for(Enumeration e = htGroups.keys(); e.hasMoreElements();)
		{
			String    name  = (String)    e.nextElement();
			Hashtable htGrp = (Hashtable) htGroups.get(name);

			mc.htGroups.put(name, duplicateGroup(htGrp));
		}

		return mc;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private Hashtable getModuleHT(BasicModule mod)
	{
		String group = ModuleManager.getModuleGroup(mod);

		//--- retrieve hashtable for all modules that belong to the same group

		Hashtable htGrp = (Hashtable) htGroups.get(group);

		if (htGrp == null)
		{
			htGrp = new Hashtable();

			htGroups.put(group, htGrp);
		}

		//--- now retrieve hashtable for selected module

		Hashtable htMod = (Hashtable) htGrp.get(mod.getId());

		if (htMod == null)
		{
			htMod = new Hashtable();

			htGrp.put(mod.getId(), htMod);
		}

		return htMod;
	}

	//---------------------------------------------------------------------------

	private Hashtable duplicateGroup(Hashtable htGrp)
	{
		Hashtable htNewGrp = new Hashtable();

		for(Enumeration e = htGrp.keys(); e.hasMoreElements();)
		{
			String    name  = (String)    e.nextElement();
			Hashtable htMod = (Hashtable) htGrp.get(name);

			htNewGrp.put(name, duplicateModule(htMod));
		}

		return htNewGrp;
	}

	//---------------------------------------------------------------------------

	private Hashtable duplicateModule(Hashtable htMod)
	{
		Hashtable htNewMod = new Hashtable();

		for(Enumeration e = htMod.keys(); e.hasMoreElements();)
		{
			String name  = (String) e.nextElement();
			Object value = htMod.get(name);

			if (value instanceof AttribSet)
				value = ((AttribSet) value).duplicate();

			else if (value instanceof AttribList)
				value = ((AttribList) value).duplicate();

			else if (value instanceof XmlElement)
				value = ((XmlElement) value).duplicate();

			htNewMod.put(name, value);
		}

		return htNewMod;
	}
}

//==============================================================================
