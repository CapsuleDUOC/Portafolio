//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.java.panels;

import javax.swing.JPanel;

import mod.datagen.code.java.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

class GeneralPanel extends JPanel
{
	private TTextFieldGuardian txtPackage  = new TTextFieldGuardian("Package");
	private TTextFieldGuardian txtExtends  = new TTextFieldGuardian("Extends");
	private TTextFieldGuardian txtNamePre  = new TTextFieldGuardian("NamePrefix");
	private TTextFieldGuardian txtNameSuf  = new TTextFieldGuardian("NameSuffix");

	private TCheckBoxGuardian  chbGenConst = new TCheckBoxGuardian("GenConsts", "Generate field constants");
	private TCheckBoxGuardian  chbGenNames = new TCheckBoxGuardian("GenNames",  "Generate field names");
	private TCheckBoxGuardian  chbUseJdo   = new TCheckBoxGuardian("UsingJDO",  "Use JDO");

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2,8);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",       new TLabel("Package"));
		add("0,1",       new TLabel("Extends"));
		add("0,2",       new TLabel("Name prefix"));
		add("0,3",       new TLabel("Name suffix"));

		add("1,0,x",     txtPackage);
		add("1,1,x",     txtExtends);
		add("1,2,x",     txtNamePre);
		add("1,3,x",     txtNameSuf);

		add("0,5,x,c,2", chbGenConst);
		add("0,6,x,c,2", chbGenNames);
		add("0,7,x,c,2", chbUseJdo);

		txtPackage.setToolTipText("Name of the class package");
		txtExtends.setToolTipText("Name of the basic class (if any)");
		txtNamePre.setToolTipText("Prefix for the class name");
		txtNameSuf.setToolTipText("Suffix for the class name");
		chbUseJdo .setToolTipText("Select to activate JDO generation for table fields");
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		txtPackage .refresh(s);
		txtExtends .refresh(s);
		txtNamePre .refresh(s);
		txtNameSuf .refresh(s);
		chbGenConst.refresh(s);
		chbGenNames.refresh(s);
		chbUseJdo  .refresh(s);
	}
}

//==============================================================================
