//==============================================================================
//===
//===   CodeGenModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import druid.data.TableNode;

//==============================================================================

public interface CodeGenModule extends DataGenModule 
{
	public String getClassCode(Logger logger, TableNode node);
}

//==============================================================================
