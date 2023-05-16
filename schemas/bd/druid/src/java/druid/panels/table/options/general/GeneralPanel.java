//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.options.general;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.data.TableNode;
import druid.util.gui.SqlCommentPanel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private GenOptionsPanel genOptPanel = new GenOptionsPanel();
	private SqlCommentPanel commPanel   = new SqlCommentPanel();
	private TemplatePanel   tempPanel   = new TemplatePanel();

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 3);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", genOptPanel);
		add("0,1,x", commPanel);
		add("0,2,x", tempPanel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableNode node)
	{
		genOptPanel.refresh(node);
		commPanel  .refresh(node.attrSet);
		tempPanel  .refresh(node.attrSet);
	}
	
	//---------------------------------------------------------------------------

	public void saveDataToNode(TableNode node)
	{
		genOptPanel.saveDataToNode(node);
	}
}

//==============================================================================
