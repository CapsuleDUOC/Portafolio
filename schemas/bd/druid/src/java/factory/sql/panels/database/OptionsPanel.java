//==============================================================================
//===
//===   OptionsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.database;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TComboBoxGuardian;
import factory.sql.BasicDatabaseSettings;

//==============================================================================

public class OptionsPanel extends TPanel
{
	private TComboBoxGuardian tcbOrder = new TComboBoxGuardian("Order");

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptionsPanel()
	{
		super("Options");

		FlexLayout flexL = new FlexLayout(2,1);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Objects order"));
		add("1,0,x", tcbOrder);

		//--- setup Order combo

		tcbOrder.addItem(BasicDatabaseSettings.ORDER_OPTIMIZED,  "Optimized");
		tcbOrder.addItem(BasicDatabaseSettings.ORDER_SEQUENCIAL, "Sequential");

		tcbOrder.setToolTipText("Optimized means that tables are sorted to preserve foreign keys");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Object s)
	{
		tcbOrder.refresh(s);
	}
}

//==============================================================================
