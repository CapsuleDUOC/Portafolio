//==============================================================================
//===
//===   RevisionV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.sets;

import org.dlib.tools.HtmlLib;

import druid.core.AttribSet;

//==============================================================================

public class RevisionV extends AbstractSetV
{
	//---------------------------------------------------------------------------

	public RevisionV(AttribSet as)
	{
		super(as);
	}

	//---------------------------------------------------------------------------

	public String getVersion()   { return as.getString("version");    }
	public String getDate()      { return as.getString("date");       }
	public String getDescr()     { return as.getString("descr");      }
	public String getHtmlDescr() { return HtmlLib.encode(getDescr()); }
}

//==============================================================================
