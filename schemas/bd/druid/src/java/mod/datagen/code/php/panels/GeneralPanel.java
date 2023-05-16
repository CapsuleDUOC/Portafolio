//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.php.panels;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import javax.swing.JPanel;
import mod.datagen.code.php.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

//==============================================================================

class GeneralPanel extends JPanel
{
	private TTextFieldGuardian txtExtends  = new TTextFieldGuardian("Extends");
	private TTextFieldGuardian txtNamePre  = new TTextFieldGuardian("NamePrefix");
	private TTextFieldGuardian txtNameSuf  = new TTextFieldGuardian("NameSuffix");
	private TTextFieldGuardian txtPathToDojo  = new TTextFieldGuardian("PathToDojo");
	private TComboBoxGuardian  tcbDbType   = new TComboBoxGuardian("NameDbType");

	private TCheckBoxGuardian  chbGenConst = new TCheckBoxGuardian("GenConsts", "Generate field length constants");
	private TCheckBoxGuardian  chbGenNames = new TCheckBoxGuardian("GenNames",  "Generate field name constants");
	private TCheckBoxGuardian  chbGenVaria = new TCheckBoxGuardian("GenVaria",  "Generate field variables + get/set methods");
	private TCheckBoxGuardian  chbGenPersi = new TCheckBoxGuardian("GenPersi",  "Generate persistence methods (load data, select distinct, search) [model]");
	private TCheckBoxGuardian  chbGenMVC = new TCheckBoxGuardian("GenMVC",  "Generate default view class to accompany each table [view], extend these for custom views");
	private TCheckBoxGuardian  chbGenDojo = new TCheckBoxGuardian("GenDojo",  "Generate example ajax edit/save method in view class using the dojo framework (dojotoolkit.org)");
	private TCheckBoxGuardian  chbGenTests = new TCheckBoxGuardian("GenTests",  "Generate unit tests using simpletest framework (simpletest.sourceforge.net)");
	private TCheckBoxGuardian  chbGenFrame = new TCheckBoxGuardian("GenFrame",  "Generate database level framework files (druid_handler.php, druid_classes.php)");
	private TCheckBoxGuardian  chbGenExample = new TCheckBoxGuardian("GenExample",  "Generate, if they don't exist, example website files (index.php, ajax_handler.php) [controler]");

	private TCheckBoxGuardian  chbDetectAutoPK = new TCheckBoxGuardian("DetectAutoPK",  "Try to determine if Primary Keys are surrogate numeric keys (e.g. bigint auto_increment in MySQL). ");
	
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2,18);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("1,0",       new TLabel("Generate one PHP class for each table."));		
		add("0,1",       new TLabel("Extends"));
		add("0,2",       new TLabel("Name prefix"));
		add("0,3",       new TLabel("Name suffix"));

		add("1,1,x",     txtExtends);
		add("1,2,x",     txtNamePre);
		add("1,3,x",     txtNameSuf);

		add("0,4,x,c,2", chbGenConst);
		add("0,5,x,c,2", chbGenNames);
		add("0,6,x,c,2", chbGenVaria);
		
		add("0,7,x,c,2", chbGenPersi);
		add("0,8",   new TLabel("Database type"));
		add("1,8,x", tcbDbType);
		add("0,9,x,c,2", chbDetectAutoPK);

		add("1,11",       new TLabel("Generate additional PHP classes and files."));	
		add("0,12,x,c,2", chbGenMVC);
		add("0,13,x,c,2", chbGenDojo);
		add("0,14", 	  new TLabel("Path to Dojo"));
		add("1,15,x",     txtPathToDojo);
		add("0,16,x,c,2", chbGenTests);
		add("0,16,x,c,2", chbGenFrame);
		add("0,17,x,c,2", chbGenExample);
		
		//--- setup combobox
        tcbDbType.addItem(Settings.TYPE_DB_MYSQL, "MySQL");
        tcbDbType.addItem(Settings.TYPE_DB_MYSQLI, "MySQLi");
		tcbDbType.addItem(Settings.TYPE_DB_ORACLE, "Oracle");
		tcbDbType.addItem(Settings.TYPE_DB_POSTGRES, "Postgresql");		
		
		txtExtends.setToolTipText("Name of the basic class (if any) that each generated class extends");
		txtNamePre.setToolTipText("Prefix for the class name for each generated class");
		txtNameSuf.setToolTipText("Suffix for the class name for each generated class");
		tcbDbType.setToolTipText("Type of underlying database classes are to connect to.");
		txtPathToDojo.setToolTipText("Address (url or relative path) at which dojo.js can be found.");
		chbDetectAutoPK.setToolTipText("If unchecked, treats all primary keys as surrogate numeric primary keys that are auto-incremented by the database, as with a sequence.  If checked, tries to detect auto_increment, default values with a sequence.nextval, or triggers that increment the Primary key.");
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		txtExtends .refresh(s);
		txtNamePre .refresh(s);
		txtNameSuf .refresh(s);
		tcbDbType  .refresh(s);
		chbGenConst.refresh(s);
		chbGenNames.refresh(s);
		chbGenVaria.refresh(s);
		chbGenPersi.refresh(s);
		chbGenMVC.refresh(s);
		chbGenDojo.refresh(s);
		txtPathToDojo.refresh(s);
		chbGenTests.refresh(s);
		chbGenFrame.refresh(s);
		chbGenExample.refresh(s);
		chbDetectAutoPK.refresh(s);
	}
}

//==============================================================================
