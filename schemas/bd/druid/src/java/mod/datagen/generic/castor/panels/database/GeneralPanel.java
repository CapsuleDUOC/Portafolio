//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TTextFieldGuardian;
import druid.util.gui.guardians.TCheckBoxGuardian;
import mod.datagen.generic.castor.DatabaseSettings;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TTextFieldGuardian txtPackage     = new TTextFieldGuardian("Package");
	private TTextFieldGuardian txtClassSuffix = new TTextFieldGuardian("ClassSuffix");
	private TCheckBoxGuardian  chbClassSuffix = new TCheckBoxGuardian("UsingDDEquiv",
																	"Use DDEquiv to map datatypes to java types");

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Package"));
		add("0,1", new TLabel("Class suffix"));

		add("1,0,x", txtPackage);
		add("1,1,x", txtClassSuffix);

		add("0,3,x,c,2", chbClassSuffix);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtPackage    .refresh(s);
		txtClassSuffix.refresh(s);
		chbClassSuffix.refresh(s);
	}
}

//==============================================================================
