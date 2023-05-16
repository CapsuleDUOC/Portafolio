//==============================================================================
//===
//===   Jdbc
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.jdbc;

import org.dlib.xml.XmlElement;

//==============================================================================

public class Jdbc
{
	public static final String TAGNAME = "jdbc";

	public Driver  driver  = new Driver();
	public Account account = new Account();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Jdbc() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setupConfig(XmlElement el)
	{
		if (el == null) return;

		driver .setupConfig(el.getChild(Driver.TAGNAME));
		account.setupConfig(el.getChild(Account.TAGNAME));
	}

	//---------------------------------------------------------------------------

	public XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		elRoot	.addChild(driver .getConfig())
					.addChild(account.getConfig());

		return elRoot;
	}
}

//==============================================================================
