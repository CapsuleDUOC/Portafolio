//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.cpp;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

class OptionPanel extends TPanel
{
	private TTextFieldGuardian txtPackage = new TTextFieldGuardian("PreLine");

	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2,1,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Pre Line"));
		add("1,0,x", txtPackage);

		txtPackage.setToolTipText("The text you enter here will be placed "+
										  "before any constant definition");
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		txtPackage.refresh(s);
	}
}

//==============================================================================
