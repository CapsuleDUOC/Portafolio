//==============================================================================
//===
//===   VarAliasV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes.datatypes;

import druid.data.AbstractNode;

//==============================================================================

public class VarAliasV extends AbstractTypeV
{
	public VarAliasV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public String getSize()    { return as.getString("size");    }
	public String getDdEquiv() { return as.getString("ddEquiv"); }
}

//==============================================================================
