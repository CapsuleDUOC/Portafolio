//==============================================================================
//===
//===   JdbcPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.config;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;

//==============================================================================

class JdbcPanel extends JPanel
{
	private TCheckBox chbSavePasswd = new TCheckBox("...");

	//---------------------------------------------------------------------------

	public JdbcPanel()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

//		add("0,0,x", chbSavePasswd);
	}

	//---------------------------------------------------------------------------

	public void refresh()
	{
//TODO		chbSavePasswd.setSelected(Config.bSavePassword);
	}

	//---------------------------------------------------------------------------

	public void store()
	{
//TODO		Config.bSavePassword = chbSavePasswd.isSelected();
	}
}

//==============================================================================
