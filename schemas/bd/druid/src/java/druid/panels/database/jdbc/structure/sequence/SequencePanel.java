//==============================================================================
//===
//===   SequencePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.sequence;

import org.dlib.gui.TTabbedPane;

import druid.core.jdbc.entities.SequenceEntity;

//==============================================================================

public class SequencePanel extends TTabbedPane
{
	private GeneralPanel genPanel = new GeneralPanel();

	//---------------------------------------------------------------------------

	public SequencePanel()
	{
		addTab("General", genPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(SequenceEntity node)
	{
		genPanel.refresh(node);
	}
}

//==============================================================================
