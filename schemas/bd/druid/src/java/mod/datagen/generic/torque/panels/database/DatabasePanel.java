//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque.panels.database;

import mod.datagen.generic.torque.DatabaseSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class DatabasePanel extends TPanel
{
	private TTextFieldGuardian txtPackage   = new TTextFieldGuardian("Package");
	private TTextFieldGuardian txtBaseClass = new TTextFieldGuardian("BaseClass");
	private TTextFieldGuardian txtBasePeer  = new TTextFieldGuardian("BasePeer");
	private TComboBoxGuardian  tcbIdMeth    = new TComboBoxGuardian("DefaultIdMethod");
	private TComboBoxGuardian  tcbJavaType  = new TComboBoxGuardian("DefaultJavaType");
	private TComboBoxGuardian  tcbJavaNam   = new TComboBoxGuardian("DefaultJavaNaming");
	private TCheckBoxGuardian  chbHeavyIdx  = new TCheckBoxGuardian("HeavyIndexing", "Heavy indexing");

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 8);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Package"));
		add("0,1", new TLabel("Base class"));
		add("0,2", new TLabel("Base peer"));
		add("0,3", new TLabel("Def. id method"));
		add("0,4", new TLabel("Def. java type"));
		add("0,5", new TLabel("Def. java naming"));

		add("1,0,x", txtPackage);
		add("1,1,x", txtBaseClass);
		add("1,2,x", txtBasePeer);
		add("1,3,x", tcbIdMeth);
		add("1,4,x", tcbJavaType);
		add("1,5,x", tcbJavaNam);

		add("0,7,x,c,2", chbHeavyIdx);

		//--- setup comboboxes

		tcbIdMeth.addItem(DatabaseSettings.IDMETHOD_NONE,     "None");
		tcbIdMeth.addItem(DatabaseSettings.IDMETHOD_NATIVE,   "Native");
		tcbIdMeth.addItem(DatabaseSettings.IDMETHOD_IDBROKER, "ID broker");

		tcbJavaType.addItem(DatabaseSettings.JAVATYPE_PRIMITIVE, "Primitive");
		tcbJavaType.addItem(DatabaseSettings.JAVATYPE_OBJECT,    "Object");

		tcbJavaNam.addItem(DatabaseSettings.JAVANAM_UNDERSCORE, "Underscore");
		tcbJavaNam.addItem(DatabaseSettings.JAVANAM_NOCHANGE,   "No change");
		tcbJavaNam.addItem(DatabaseSettings.JAVANAM_JAVANAME,   "Java name");
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtPackage  .refresh(s);
		txtBaseClass.refresh(s);
		txtBasePeer .refresh(s);
		tcbIdMeth   .refresh(s);
		tcbJavaType .refresh(s);
		tcbJavaNam  .refresh(s);
		chbHeavyIdx .refresh(s);
	}
}

//==============================================================================
