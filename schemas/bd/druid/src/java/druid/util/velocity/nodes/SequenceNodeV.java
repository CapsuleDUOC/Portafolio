//==============================================================================
//===
//===   SequenceNodeV
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import druid.data.AbstractNode;

//==============================================================================

public class SequenceNodeV extends AbstractNodeV
{
	public SequenceNodeV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public String  getIncrement()  { return as.getString("increment"); }
	public String  getMinValue()   { return as.getString("minValue");  }
	public String  getMaxValue()   { return as.getString("maxValue");  }
	public String  getStart()      { return as.getString("start");     }
	public String  getCache()      { return as.getString("cache");     }
	public boolean getIsCycleSet() { return as.getBool  ("cycle");     }
	public boolean getIsOrderSet() { return as.getBool  ("order");     }
}

//==============================================================================
