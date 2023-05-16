//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.field.options.general;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.data.FieldNode;
import druid.util.gui.SqlCommentPanel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private SqlCommentPanel commPanel = new SqlCommentPanel();

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 2, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", commPanel);
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldNode node)
	{
		commPanel.refresh(node.attrSet);
	}
}

//==============================================================================
