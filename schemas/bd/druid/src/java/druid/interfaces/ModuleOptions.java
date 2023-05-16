//==============================================================================
//===
//===   ModuleOptions
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import javax.swing.JComponent;

import druid.data.AbstractNode;

//==============================================================================

public interface ModuleOptions
{
	/** Called when the module must update its option panel with
	  * data supplied by current node.
	  */

	public void refresh(AbstractNode node);

	/** Returns the panel used to show module options.
	  */

	public JComponent getPanel();
}

//==============================================================================
