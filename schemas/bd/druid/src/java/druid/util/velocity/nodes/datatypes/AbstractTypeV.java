//==============================================================================
//===
//===   AbstractTypeNodeV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes.datatypes;

import org.dlib.tools.HtmlLib;

import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.datatypes.AbstractType;
import druid.util.velocity.nodes.AbstractNodeV;

//==============================================================================

public class AbstractTypeV extends AbstractNodeV
{
	private String domain;

	//---------------------------------------------------------------------------

	public AbstractTypeV(AbstractNode node)
	{
		super(node);

		domain = DataTypeLib.getTypeCheckString("field", (AbstractType) node);
	}

	//---------------------------------------------------------------------------

	public String getDomain()     { return domain; }
	public String getHtmlDomain() { return HtmlLib.encode(domain); }
}

//==============================================================================
