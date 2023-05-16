//==============================================================================
//===
//===   ErViewPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.config;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;

import druid.core.config.Config;

//==============================================================================

class ErViewPanel extends JPanel
{
	private TComboBox tcbSnapSize   = new TComboBox();
	private TComboBox tcbScrollSize = new TComboBox();

	//---------------------------------------------------------------------------

	public ErViewPanel()
	{
		FlexLayout flexL = new FlexLayout(2,2);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Snap size"));
		add("0,1",   new TLabel("Wheel scroll"));
		add("1,0,x", tcbSnapSize);
		add("1,1,x", tcbScrollSize);

		for(int i=4; i<=32; i += 4)
			tcbSnapSize.addItem(i, i+" pixels");

		for(int i=16; i<=96; i += 16)
			tcbScrollSize.addItem(i, i+" pixels");
	}

	//---------------------------------------------------------------------------

	public void refresh()
	{
		tcbSnapSize  .setSelectedKey(Config.erView.snapSize);
		tcbScrollSize.setSelectedKey(Config.erView.scrollSize);
	}

	//---------------------------------------------------------------------------

	public void store()
	{
		Config.erView.snapSize   = tcbSnapSize  .getSelectedIntKey();
		Config.erView.scrollSize = tcbScrollSize.getSelectedIntKey();
	}
}

//==============================================================================
