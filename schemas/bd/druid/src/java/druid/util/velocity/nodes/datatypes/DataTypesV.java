//==============================================================================
//===
//===   DataTypesNodeV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes.datatypes;

import druid.data.AbstractNode;
import druid.util.velocity.nodes.AbstractNodeV;

//==============================================================================

public class DataTypesV extends AbstractTypeV
{
	public DataTypesV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public AbstractNodeV getConstFolder() { return convertNode((AbstractNode) node.getChild(0));  }
	public AbstractNodeV getVarFolder()   { return convertNode((AbstractNode) node.getChild(1));  }
}

//==============================================================================
