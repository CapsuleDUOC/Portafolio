//==============================================================================
//===
//===   FieldAttribsV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.sets;

import org.dlib.tools.HtmlLib;

import druid.core.AttribSet;
import druid.data.FieldAttribs;

//==============================================================================

public class FieldAttribsV extends AbstractSetV
{
	//---------------------------------------------------------------------------

	public FieldAttribsV(AttribSet as)
	{
		super(as);
	}

	//---------------------------------------------------------------------------

	public String getName()      { return as.getString("name");      }
	public String getHtmlName()  { return HtmlLib.encode(getName()); }
	public String getSqlName()   { return as.getString("sqlName");   }
	public int    getId()        { return as.getInt("id");           }

	public boolean getIsTypeBool()   { return as.getString("type").equals(FieldAttribs.TYPE_BOOL);   }
	public boolean getIsTypeString() { return as.getString("type").equals(FieldAttribs.TYPE_STRING); }
	public boolean getIsTypeInt()    { return as.getString("type").equals(FieldAttribs.TYPE_INT);    }

	public boolean getIsScopeField()  { return as.getString("scope").equals(FieldAttribs.SCOPE_FIELD);  }
	public boolean getIsScopeTable()  { return as.getString("scope").equals(FieldAttribs.SCOPE_TABLE);  }
	public boolean getIsScopeIndex()  { return as.getString("scope").equals(FieldAttribs.SCOPE_INDEX);  }
	public boolean getIsScopeUIndex() { return as.getString("scope").equals(FieldAttribs.SCOPE_UINDEX); }
	public boolean getIsScopeCustom() { return as.getString("scope").equals(FieldAttribs.SCOPE_CUSTOM); }

	public String getDescr()     { return as.getString("descr");      }
	public String getHtmlDescr() { return HtmlLib.encode(getDescr()); }

	public boolean getUseInDataDict() { return as.getBool("useInDD");   }
	public boolean getUseInSummary()  { return as.getBool("useInSumm"); }
}

//==============================================================================
