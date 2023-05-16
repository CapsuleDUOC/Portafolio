//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.panels;

import javax.swing.JPanel;

import mod.datagen.docs.pdf.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.Language;
import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

class GeneralPanel extends JPanel
{
	private TComboBoxGuardian tcbLang    = new TComboBoxGuardian("Language");
	private TComboBoxGuardian tcbThumb   = new TComboBoxGuardian("Thumbnails",    true);
	private TComboBoxGuardian tcbPage    = new TComboBoxGuardian("PageSize",      true);
	private TComboBoxGuardian tcbEntScal = new TComboBoxGuardian("EntityScaling", true);
	private TComboBoxGuardian tcbCellPad = new TComboBoxGuardian("CellPadding",   true);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2,5);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Language"));
		add("0,1", new TLabel("Thumbnails"));
		add("0,2", new TLabel("Page size"));
		add("0,3", new TLabel("Entity scaling"));
		add("0,4", new TLabel("Cell padding"));

		add("1,0,x", tcbLang);
		add("1,1,x", tcbThumb);
		add("1,2,x", tcbPage);
		add("1,3,x", tcbEntScal);
		add("1,4,x", tcbCellPad);

		//--- setup combos

		Language.fillComboBox(tcbLang);

		tcbThumb.addItem(128, "128 x 128");
		tcbThumb.addItem(192, "192 x 192");
		tcbThumb.addItem(256, "256 x 256");

		tcbPage.addItem(Settings.PAGESIZE_A4, "A4");
		tcbPage.addItem(Settings.PAGESIZE_LETTER, "Letter");
		tcbPage.addItem(Settings.PAGESIZE_LEGAL, "Legal");

		for(int i=30; i<=100; i+=10)
			tcbEntScal.addItem(i, i +" %");

		for(int i=0; i<=5; i++)
			tcbCellPad.addItem(i, i +"");

		tcbLang   .setToolTipText("Used to localize common text (like 'chapter, 'table')");
		tcbThumb  .setToolTipText("Size (in pixel) of E/R thumbnails");
		tcbEntScal.setToolTipText("Used to make the E/R entity images smaller");
		tcbCellPad.setToolTipText("Cell padding used for tables (as grids, not db tables)");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		tcbLang   .refresh(s);
		tcbThumb  .refresh(s);
		tcbPage   .refresh(s);
		tcbEntScal.refresh(s);
		tcbCellPad.refresh(s);
	}
}

//==============================================================================
