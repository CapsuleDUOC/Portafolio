//==============================================================================
//===
//===   MarginPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.panels;

import javax.swing.JPanel;

import mod.datagen.docs.pdf.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

class MarginsPanel extends JPanel
{
	private TComboBoxGuardian tcbTop    = new TComboBoxGuardian("TopMargin",    true);
	private TComboBoxGuardian tcbBottom = new TComboBoxGuardian("BottomMargin", true);
	private TComboBoxGuardian tcbLeft   = new TComboBoxGuardian("LeftMargin",   true);
	private TComboBoxGuardian tcbRight  = new TComboBoxGuardian("RightMargin",  true);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MarginsPanel()
	{
		FlexLayout flexL = new FlexLayout(2,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Top"));
		add("0,1", new TLabel("Bottom"));
		add("0,2", new TLabel("Left"));
		add("0,3", new TLabel("Right"));

		add("1,0,x", tcbTop);
		add("1,1,x", tcbBottom);
		add("1,2,x", tcbLeft);
		add("1,3,x", tcbRight);

		//--- setup combos

		setupCombo(tcbTop);
		setupCombo(tcbBottom);
		setupCombo(tcbLeft);
		setupCombo(tcbRight);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		tcbTop   .refresh(s);
		tcbBottom.refresh(s);
		tcbLeft  .refresh(s);
		tcbRight .refresh(s);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupCombo(TComboBox t)
	{
		t.addItem(14, "0.5 cm");
		t.addItem(28, "1 cm");
		t.addItem(42, "1.5 cm");
		t.addItem(57, "2 cm");
		t.addItem(71, "2.5 cm (about 1 inch)");
	}
}

//==============================================================================
