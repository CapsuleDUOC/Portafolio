//==============================================================================
//===
//===   JdoPanel
//===
//===   Copyright (C) by Andrea Carboni & Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.java.panels;

import javax.swing.JPanel;

import mod.datagen.code.java.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

class JdoPanel extends JPanel
{
	private TComboBoxGuardian tcbAccess               = new TComboBoxGuardian("Access");
	private TCheckBoxGuardian chkGetter               = new TCheckBoxGuardian("Getter",       "Getter methods");
	private TCheckBoxGuardian chkSetter               = new TCheckBoxGuardian("Setter",       "Setter methods");
	private TCheckBoxGuardian chkDirtyCheck           = new TCheckBoxGuardian("DirtyCheck",   "Check dirty (for setter/getter)");
	private TCheckBoxGuardian chkForeignKeyArrayCheck = new TCheckBoxGuardian("GenFKArray",   "Array for foreign key");
	private TCheckBoxGuardian chkForeignKeyAsClassRef = new TCheckBoxGuardian("FKClassRef",   "Foreign key as class reference");
	private TCheckBoxGuardian chkSerializable         = new TCheckBoxGuardian("Serializable", "Serializable");
	private TCheckBoxGuardian chkJdoFile              = new TCheckBoxGuardian("JdoFile",      "Generate JDO File");
	private TCheckBoxGuardian useCamelCase            = new TCheckBoxGuardian("CamelCase",    "Convert '_' to camel case");

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public JdoPanel()
	{
		FlexLayout flexL = new FlexLayout(2,10);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Access"));
		add("1,0,x", tcbAccess);

		add("0,2,x,c,2", chkGetter);
		add("0,3,x,c,2", chkSetter);
		add("0,4,x,c,2", chkDirtyCheck);
		add("0,5,x,c,2", chkForeignKeyArrayCheck);
		add("0,6,x,c,2", chkForeignKeyAsClassRef);
		add("0,7,x,c,2", chkSerializable);
		add("0,8,x,c,2", chkJdoFile);
		add("0,9,x,c,2", useCamelCase);

		tcbAccess.setToolTipText("Access type for fields/vars");
		chkGetter.setToolTipText("Generate getXXX() methods (one for each field)");
		chkSetter.setToolTipText("Generate setXXX() methods (one for each field)");
		chkDirtyCheck.setToolTipText("Perform a dirty check in setter methods");
		chkForeignKeyArrayCheck.setToolTipText("Create an array for classes that use this class as foreign key");
		chkForeignKeyAsClassRef.setToolTipText("Create a class reference for foreign key");
		chkJdoFile.setToolTipText("Generate package.jdo file for JDO enhancer");
		useCamelCase.setToolTipText("Remove '_' in name and capitalize the next letter");

		tcbAccess.addItem(Settings.ACCESS_PUBLIC,    "Public");
		tcbAccess.addItem(Settings.ACCESS_PROTECTED, "Protected");
		tcbAccess.addItem(Settings.ACCESS_PRIVATE,   "Private");
		tcbAccess.addItem(Settings.ACCESS_PACKAGE,   "Package");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		tcbAccess.refresh(s);
		chkGetter.refresh(s);
		chkSetter.refresh(s);
		chkDirtyCheck.refresh(s);
		chkForeignKeyArrayCheck.refresh(s);
		chkForeignKeyAsClassRef.refresh(s);
		chkSerializable.refresh(s);
		chkJdoFile.refresh(s);
		useCamelCase.refresh(s);
	}
}

//==============================================================================
