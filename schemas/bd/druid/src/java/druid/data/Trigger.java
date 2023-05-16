//==============================================================================
//===
//===   Trigger
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public class Trigger extends AbstractNode
{
	public static final String ACTIV_BEFORE      = "b";
	public static final String ACTIV_AFTER       = "a";
	public static final String ACTIV_INSTEADOF   = "i";

	public static final String FOREACH_ROW       = "r";
	public static final String FOREACH_STATEMENT = "s";

	//---------------------------------------------------------------------------

	public Trigger()
	{
		attrSet.addAttrib("activation", ACTIV_BEFORE);
		attrSet.addAttrib("onInsert",   false);
		attrSet.addAttrib("onUpdate",   false);
		attrSet.addAttrib("onDelete",   false);
		attrSet.addAttrib("forEach",    FOREACH_ROW);
		attrSet.addAttrib("when",       "");
		attrSet.addAttrib("of",         ""); 
		attrSet.addAttrib("code",       "BEGIN\n\t...\nEND\n");

		setToolTipText("A trigger of your table");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new Trigger(); }
}

//==============================================================================
