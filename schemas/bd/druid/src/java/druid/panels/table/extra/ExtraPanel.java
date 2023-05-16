//==============================================================================
//===
//===   ExtraPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.extra;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.data.TableNode;

//==============================================================================

public class ExtraPanel extends JPanel
{
	private SqlCommandsPanel panSqlCmd = new SqlCommandsPanel();

	//---------------------------------------------------------------------------

	public ExtraPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", panSqlCmd);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableNode tableNode)
	{
		panSqlCmd.refresh(tableNode.attrSet);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TableNode tableNode)
	{
		panSqlCmd.store(tableNode.attrSet);
	}
}

//==============================================================================
