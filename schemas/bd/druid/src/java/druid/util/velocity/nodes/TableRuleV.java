//==============================================================================
//===
//===   TableRuleV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import org.dlib.tools.HtmlLib;

import druid.data.AbstractNode;

//==============================================================================

public class TableRuleV extends AbstractNodeV
{
	public TableRuleV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public boolean getIsInUse()  { return as.getBool("use");         }
	public String  getRule()     { return as.getString("rule");      }
	public String  getHtmlRule() { return HtmlLib.encode(getRule()); }
}

//==============================================================================
