//==============================================================================
//===
//===   TableVarV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.sets;

import org.dlib.tools.HtmlLib;
import org.dlib.xml.XmlCodec;

import druid.core.AttribSet;
import druid.data.TableVars;

//==============================================================================

public class TableVarV extends AbstractSetV
{
	//---------------------------------------------------------------------------

	public TableVarV(AttribSet as)
	{
		super(as);
	}

	//---------------------------------------------------------------------------

	public String getName()      { return as.getString("name");        }
	public String getXmlName()   { return XmlCodec.encode(getName());  }
	public String getHtmlName()  { return HtmlLib.encode(getName());   }
	public String getValue()     { return as.getString("value");       }
	public String getXmlValue()  { return XmlCodec.encode(getValue()); }
	public String getHtmlValue() { return HtmlLib.encode(getValue());  }
	public String getDescr()     { return as.getString("descr");       }
	public String getXmlDescr()  { return XmlCodec.encode(getDescr()); }
	public String getHtmlDescr() { return HtmlLib.encode(getDescr());  }

	public boolean getIsTypeBool()   { return as.getString("type").equals(TableVars.BOOL);   }
	public boolean getIsTypeString() { return as.getString("type").equals(TableVars.STRING); }
	public boolean getIsTypeInt()    { return as.getString("type").equals(TableVars.INT);    }
	public boolean getIsTypeLong()   { return as.getString("type").equals(TableVars.LONG);   }
	public boolean getIsTypeChar()   { return as.getString("type").equals(TableVars.CHAR);   }
	public boolean getIsTypeFloat()  { return as.getString("type").equals(TableVars.FLOAT);  }
	public boolean getIsTypeDouble() { return as.getString("type").equals(TableVars.DOUBLE); }
}

//==============================================================================
