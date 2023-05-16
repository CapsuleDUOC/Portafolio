//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.rules.general;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextArea;

import druid.core.AttribSet;
import druid.data.TableRule;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TCheckBox chbUse  = new TCheckBox("Use rule during sql generation");
	private TTextArea txaRule = new TTextArea();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 2);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		txaRule.getDocument().addDocumentListener(sent);

		chbUse.addItemListener(sent);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,1,l,t",   new TLabel("Rule"));
		add("0,0,x,c,2", chbUse);
		add("1,1,x,x",   txaRule);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableRule rule)
	{
		AttribSet as = rule.attrSet;

		chbUse.setSelected(as.getBool("use"));
		txaRule.setText(as.getString("rule"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TableRule rule)
	{
		AttribSet as = rule.attrSet;

		as.setBool("use",    chbUse.isSelected());
		as.setString("rule", txaRule.getText());
	}
}

//==============================================================================
