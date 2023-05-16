//==============================================================================
//===
//===   OptionsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.options;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;

import druid.core.AttribSet;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class OptionsPanel extends JPanel
{
	TCheckBox chbDd       = new TCheckBox("Add 'DD Equiv' to the data dictionary");
	TCheckBox chbSumm     = new TCheckBox("Add 'DD Equiv' to the summary");
	TCheckBox chbUseBuild = new TCheckBox("Add build to generated code");

	//---------------------------------------------------------------------------

	public OptionsPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 5, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", chbDd);
		add("0,1,x", chbSumm);
		add("0,2,x", chbUseBuild);

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		chbDd.addItemListener(sent);
		chbSumm.addItemListener(sent);
		chbUseBuild.addItemListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribSet as)
	{
		chbDd.setSelected(as.getBool("ddUseDDEquiv"));
		chbSumm.setSelected(as.getBool("summUseDDEquiv"));
		chbUseBuild.setSelected(as.getBool("codeUseBuild"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(AttribSet as)
	{
		as.setBool("ddUseDDEquiv",  chbDd.isSelected());
		as.setBool("summUseDDEquiv",chbSumm.isSelected());
		as.setBool("codeUseBuild",  chbUseBuild.isSelected());
	}
}

//==============================================================================
