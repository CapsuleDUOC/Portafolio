//==============================================================================
//===
//===   AccountInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.jdbc;

//==============================================================================

public class AccountInfo
{
	public String  name;
	public String  url;
	public String  user;
	public String  password;
	public boolean autoCommit;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AccountInfo() {}

	//---------------------------------------------------------------------------

	public AccountInfo(String name, String url, String user, String password, boolean autoCommit)
	{
		this.name       = name;
		this.url        = url;
		this.user       = user;
		this.password   = password;
		this.autoCommit = autoCommit;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public AccountInfo duplicate()
	{
		return new AccountInfo(name, url, user, password, autoCommit);
	}
}

//==============================================================================
