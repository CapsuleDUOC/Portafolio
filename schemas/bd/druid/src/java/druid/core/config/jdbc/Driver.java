//==============================================================================
//===
//===   Driver
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.jdbc;

import java.util.List;
import java.util.Vector;

import org.dlib.xml.XmlElement;

//==============================================================================

public class Driver
{
	public static final String TAGNAME = "drivers";

	//---------------------------------------------------------------------------

	private static final String DRIVER = "driver";

	private static Vector drivers = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Driver() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	void setupConfig(XmlElement el)
	{
		if (el == null) return;

		List list = el.getChildren(DRIVER);

		for (int i=0; i<list.size(); i++)
		{
			XmlElement elChild = (XmlElement) list.get(i);

			add(elChild.getValue());
		}
	}

	//---------------------------------------------------------------------------

	XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		for(int i=0; i<count(); i++)
			elRoot.addChild(new XmlElement(DRIVER, getAt(i)));

		return elRoot;
	}

	//---------------------------------------------------------------------------

	public void clear()
	{
		drivers.removeAllElements();
	}

	//---------------------------------------------------------------------------

	public void add(String driver)
	{
		drivers.addElement(driver);
	}

	//---------------------------------------------------------------------------

	public String getAt(int index)
	{
		return (String) drivers.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public int count()
	{
		return drivers.size();
	}
}

//==============================================================================
