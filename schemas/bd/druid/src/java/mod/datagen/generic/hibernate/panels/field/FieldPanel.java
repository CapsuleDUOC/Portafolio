//==============================================================================
//===
//===   FieldPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate.panels.field;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.hibernate.FieldSettings;
import mod.datagen.generic.hibernate.Util;

//==============================================================================

public class FieldPanel extends JPanel
{
	private TTextFieldGuardian txtPropName = new TTextFieldGuardian("PropertyName");
	private TTextFieldGuardian txtType     = new TTextFieldGuardian("Type");
	private TTextFieldGuardian txtAccess   = new TTextFieldGuardian("Access");
	private TTextFieldGuardian txtFormula  = new TTextFieldGuardian("Formula");

	private TComboBoxGuardian  tcbInsert  = Util.createYesNoDefGuardian("Insert");
	private TComboBoxGuardian  tcbUpdate  = Util.createYesNoDefGuardian("Update");

	//---------------------------------------------------------------------------

	public FieldPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 6);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Property name"));
		add("0,1", new TLabel("Type"));
		add("0,2", new TLabel("Access"));
		add("0,3", new TLabel("Formula"));
		add("0,4", new TLabel("Insert"));
		add("0,5", new TLabel("Update"));

		add("1,0,x", txtPropName);
		add("1,1,x", txtType);
		add("1,2,x", txtAccess);
		add("1,3,x", txtFormula);
		add("1,4,x", tcbInsert);
		add("1,5,x", tcbUpdate);
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldSettings s)
	{
		txtPropName.refresh(s);
		txtType    .refresh(s);
		txtAccess  .refresh(s);
		txtFormula .refresh(s);
		tcbInsert  .refresh(s);
		tcbUpdate  .refresh(s);
	}
}

//==============================================================================
