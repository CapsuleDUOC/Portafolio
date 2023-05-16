//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque.panels.table;

import javax.swing.JPanel;

import mod.datagen.generic.torque.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class TablePanel extends JPanel
{
	private TTextFieldGuardian txtJavaName  = new TTextFieldGuardian("JavaName");
	private TTextFieldGuardian txtBaseClass = new TTextFieldGuardian("BaseClass");
	private TTextFieldGuardian txtBasePeer  = new TTextFieldGuardian("BasePeer");
	private TTextFieldGuardian txtAlias     = new TTextFieldGuardian("Alias");
	private TTextFieldGuardian txtInterface = new TTextFieldGuardian("Interface");
	private TComboBoxGuardian  tcbIdMeth    = new TComboBoxGuardian("IdMethod");
	private TComboBoxGuardian  tcbJavaNam   = new TComboBoxGuardian("JavaNaming");
	private TCheckBoxGuardian  chbSkipSql   = new TCheckBoxGuardian("SkippingSql",   "Skip SQL");
	private TCheckBoxGuardian  chbAbstract  = new TCheckBoxGuardian("Abstract",      "Abstract");
	private TCheckBoxGuardian  chbHeavyIdx  = new TCheckBoxGuardian("HeavyIndexing", "Heavy indexing");

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(2, 11);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Java name"));
		add("0,1", new TLabel("Base class"));
		add("0,2", new TLabel("Base peer"));
		add("0,3", new TLabel("Alias"));
		add("0,4", new TLabel("Interface"));
		add("0,5", new TLabel("ID method"));
		add("0,6", new TLabel("Java naming"));

		add("1,0,x", txtJavaName);
		add("1,1,x", txtBaseClass);
		add("1,2,x", txtBasePeer);
		add("1,3,x", txtAlias);
		add("1,4,x", txtInterface);
		add("1,5,x", tcbIdMeth);
		add("1,6,x", tcbJavaNam);

		add("0,8,x,c,2",  chbSkipSql);
		add("0,9,x,c,2",  chbAbstract);
		add("0,10,x,c,2", chbHeavyIdx);


		//--- setup comboboxes

		tcbIdMeth.addItem(TableSettings.IDMETHOD_NULL,     "Null");
		tcbIdMeth.addItem(TableSettings.IDMETHOD_NONE,     "None");
		tcbIdMeth.addItem(TableSettings.IDMETHOD_NATIVE,   "Native");
		tcbIdMeth.addItem(TableSettings.IDMETHOD_IDBROKER, "ID broker");

		tcbJavaNam.addItem(TableSettings.JAVANAM_UNDERSCORE, "Underscore");
		tcbJavaNam.addItem(TableSettings.JAVANAM_NOCHANGE,   "No change");
		tcbJavaNam.addItem(TableSettings.JAVANAM_JAVANAME,   "Java name");
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings s)
	{
		txtJavaName .refresh(s);
		txtBaseClass.refresh(s);
		txtBasePeer .refresh(s);
		txtAlias    .refresh(s);
		txtInterface.refresh(s);
		tcbIdMeth   .refresh(s);
		tcbJavaNam  .refresh(s);
		chbSkipSql  .refresh(s);
		chbAbstract .refresh(s);
		chbHeavyIdx .refresh(s);
	}
}

//==============================================================================
