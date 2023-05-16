//==============================================================================
//===
//===   QueryField
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the LGPL license.
//==============================================================================

package ddf.type;

//==============================================================================

public class QueryField
{
	private String  name;
	private SqlType type;
	private boolean isPkey;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------
	
	public QueryField(String name, SqlType type, boolean isPkey)
	{
		this.name   = name;
		this.type   = type;
		this.isPkey = isPkey;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String  getName() { return name;   }
	public SqlType getType() { return type;   }
	public boolean isPKey()  { return isPkey; }

	//---------------------------------------------------------------------------
	
	public void setPKey(boolean yesno)
	{
		isPkey = yesno;
	}
}

//==============================================================================
