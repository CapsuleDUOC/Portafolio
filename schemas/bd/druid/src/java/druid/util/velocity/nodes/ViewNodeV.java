//==============================================================================
//===
//===   ViewNodeV
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import org.dlib.tools.HtmlLib;
import org.dlib.xml.XmlCodec;

import druid.data.AbstractNode;

//==============================================================================

public class ViewNodeV extends AbstractNodeV
{
	public ViewNodeV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public String getCode()     { return as.getString("sqlCode");    }
	public String getXmlCode()  { return XmlCodec.encode(getCode()); }
	public String getHtmlCode() { return HtmlLib.encode(getCode());  }
}

//==============================================================================
