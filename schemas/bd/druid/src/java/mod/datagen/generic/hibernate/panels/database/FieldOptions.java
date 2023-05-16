//==============================================================================
//===
//===   FieldOptions
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.hibernate.DatabaseSettings;

//==============================================================================

public class FieldOptions extends JPanel
{
	private TTextFieldGuardian txtPropName = new TTextFieldGuardian("PropertyName");
	private TTextFieldGuardian txtType     = new TTextFieldGuardian("Type");
	private TTextFieldGuardian txtAccess   = new TTextFieldGuardian("Access");
	private TTextFieldGuardian txtFormula  = new TTextFieldGuardian("Formula");

	private TCheckBoxGuardian  chbInsert  = new TCheckBoxGuardian("Insert", "Insert");
	private TCheckBoxGuardian  chbUpdate  = new TCheckBoxGuardian("Update", "Update");

	//---------------------------------------------------------------------------

	public FieldOptions()
	{
		FlexLayout flexL = new FlexLayout(2, 7);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Property name"));
		add("0,1", new TLabel("Type"));
		add("0,2", new TLabel("Access"));
		add("0,3", new TLabel("Formula"));

		add("1,0,x", txtPropName);
		add("1,1,x", txtType);
		add("1,2,x", txtAccess);
		add("1,3,x", txtFormula);

		add("0,5,x,c,2", chbInsert);
		add("0,6,x,c,2", chbUpdate);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtPropName.refresh(s);
		txtType    .refresh(s);
		txtAccess  .refresh(s);
		txtFormula .refresh(s);
		chbInsert  .refresh(s);
		chbUpdate  .refresh(s);
	}
}

//==============================================================================
