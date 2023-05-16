//==============================================================================
//===
//===   DatabaseOptions
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.hibernate.DatabaseSettings;

//==============================================================================

public class DatabaseOptions extends JPanel
{
	private TTextFieldGuardian txtPackage = new TTextFieldGuardian("Package");
	private TComboBoxGuardian  tcbDefCasc = new TComboBoxGuardian("DefaultCascade");
	private TCheckBoxGuardian  chbAutoImp = new TCheckBoxGuardian("AutoImport", "Auto import");

	//---------------------------------------------------------------------------

	public DatabaseOptions()
	{
		FlexLayout flexL = new FlexLayout(2, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Package"));
		add("0,1", new TLabel("Default cascade"));

		add("1,0,x", txtPackage);
		add("1,1,x", tcbDefCasc);

		add("0,3,x,c,2", chbAutoImp);

		//--- setup comboboxes

		tcbDefCasc.addItem(DatabaseSettings.DEFCASCADE_NONE,    "None");
		tcbDefCasc.addItem(DatabaseSettings.DEFCASCADE_SAVEUPD, "Save update");
		tcbDefCasc.addItem(DatabaseSettings.DEFCASCADE_ALL,     "All");
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtPackage.refresh(s);
		tcbDefCasc.refresh(s);
		chbAutoImp.refresh(s);
	}
}

//==============================================================================
