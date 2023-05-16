//==============================================================================
//===
//===   ConstAliasV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes.datatypes;

import druid.data.AbstractNode;

//==============================================================================

public class ConstAliasV extends AbstractTypeV
{
	public ConstAliasV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public String getDdEquiv() { return as.getString("ddEquiv");  }
}

//==============================================================================
