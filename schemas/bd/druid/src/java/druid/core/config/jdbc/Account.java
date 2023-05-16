//==============================================================================
//===
//===   Account
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.jdbc;

import java.util.List;
import java.util.Vector;
import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;

//==============================================================================

public class Account
{
	public static final String TAGNAME = "accounts";

	//---------------------------------------------------------------------------

	private static final String ACCOUNT       = "account";
	private static final String    NAME       = "name";
	private static final String    URL        = "url";
	private static final String    USER       = "user";
	private static final String    PASSWORD   = "password";
	private static final String    AUTOCOMMIT = "autocommit";

	private static Vector accounts = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	Account() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	void setupConfig(XmlElement el)
	{
		if (el == null) return;

		List list = el.getChildren(ACCOUNT);

		for (int i=0; i<list.size(); i++)
		{
			XmlElement elChild = (XmlElement) list.get(i);

			String name     = elChild.getChildValue(NAME);
			String url      = elChild.getChildValue(URL);
			String user     = elChild.getChildValue(USER);
			String password = elChild.getChildValue(PASSWORD);

			XmlElement autoCommit = elChild.getChild(AUTOCOMMIT);

			add(new AccountInfo(name, url, user, unscramble(password), autoCommit != null));
		}
	}

	//---------------------------------------------------------------------------

	XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		for(int i=0; i<count(); i++)
		{
			AccountInfo ai = getAt(i);

			XmlElement el = new XmlElement(ACCOUNT);

			el	.addChild(new XmlElement(NAME,     ai.name))
				.addChild(new XmlElement(URL,      ai.url))
				.addChild(new XmlElement(USER,     ai.user))
				.addChild(new XmlElement(PASSWORD, scramble(ai.password)));

	      if (ai.autoCommit)
   	     el.addChild(new XmlElement(AUTOCOMMIT));

			elRoot.addChild(el);
		}

		return elRoot;
	}

	//---------------------------------------------------------------------------

	public void add(AccountInfo account)
	{
		accounts.add(account);
	}

	//---------------------------------------------------------------------------

	public AccountInfo getAt(int index)
	{
		return (AccountInfo) accounts.get(index);
	}

	//---------------------------------------------------------------------------

	public void removeAt(int index)
	{
		accounts.removeElementAt(index);
	}

	//---------------------------------------------------------------------------

	public int count()
	{
		return accounts.size();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String scramble(String text)
	{
		StringBuffer sb = new StringBuffer();

		for(int i=0; i<text.length(); i++)
		{
			int data = text.charAt(i) ^ 0xAAAA ;
			sb.append(Util.convertToHex(data, 4));
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private String unscramble(String text)
	{
		if (!isScrambled(text))
			return text;

		StringBuffer sb = new StringBuffer();

		for(int i=0; i<text.length();)
		{
			String data  = text.substring(i, i+4);
			char   digit = (char) (Util.convertFromHex(data) ^ 0xAAAA);

			sb.append(digit);
			i+=4;
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	public boolean isScrambled(String text)
	{
		for(int i=0; i<text.length(); i++)
		{
			if (text.charAt(i) >= '0' && text.charAt(i) <= '9')
				continue;

			if (text.charAt(i) >= 'A' && text.charAt(i) <= 'F')
				continue;

			return false;
		}

		if (text.length() % 4 != 0)
			return false;

		return true;
	}
}

//==============================================================================
