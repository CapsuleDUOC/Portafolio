//==============================================================================
//===
//===   FieldPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.field;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.castor.FieldSettings;

//==============================================================================

public class FieldPanel extends JPanel
{
	private TTextFieldGuardian txtGetMeth = new TTextFieldGuardian("GetMethod");
	private TTextFieldGuardian txtSetMeth = new TTextFieldGuardian("SetMethod");
	private TTextFieldGuardian txtCreMeth = new TTextFieldGuardian("CreateMethod");
	private TTextFieldGuardian txtHasMeth = new TTextFieldGuardian("HasMethod");
	private TTextFieldGuardian txtHandler = new TTextFieldGuardian("Handler");

	private TComboBoxGuardian  tcbCollect = new TComboBoxGuardian("Collection");
	private TComboBoxGuardian  tcbContain = new TComboBoxGuardian("Container");
	private TComboBoxGuardian  tcbSqlDirt = new TComboBoxGuardian("SqlDirty");

	private TCheckBoxGuardian  chbDirect  = new TCheckBoxGuardian("Direct",       "Direct");
	private TCheckBoxGuardian  chbLazy    = new TCheckBoxGuardian("Lazy",         "Lazy");
	private TCheckBoxGuardian  chbTrans   = new TCheckBoxGuardian("Transient",    "Transient");
	private TCheckBoxGuardian  chbSqlRO   = new TCheckBoxGuardian("SqlReadOnly",  "Read only (sql)");
	private TCheckBoxGuardian  chbSqlTrans= new TCheckBoxGuardian("SqlTransient", "Transient (sql)");

	//---------------------------------------------------------------------------

	public FieldPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 14);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Get method"));
		add("0,1", new TLabel("Set method"));
		add("0,2", new TLabel("Create method"));
		add("0,3", new TLabel("Has method"));
		add("0,4", new TLabel("Handler"));
		add("0,5", new TLabel("Collection"));
		add("0,6", new TLabel("Container"));
		add("0,7", new TLabel("Dirty (sql)"));

		add("1,0,x", txtGetMeth);
		add("1,1,x", txtSetMeth);
		add("1,2,x", txtCreMeth);
		add("1,3,x", txtHasMeth);
		add("1,4,x", txtHandler);
		add("1,5,x", tcbCollect);
		add("1,6,x", tcbContain);
		add("1,7,x", tcbSqlDirt);

		add("0,9,x,c,2",  chbDirect);
		add("0,10,x,c,2", chbLazy);
		add("0,11,x,c,2", chbTrans);
		add("0,12,x,c,2", chbSqlRO);
		add("0,13,x,c,2", chbSqlTrans);

		//--- setup comboboxes

		tcbCollect.addItem(FieldSettings.COLL_NONE,    "None");
		tcbCollect.addItem(FieldSettings.COLL_ARRAY,   "Array");
		tcbCollect.addItem(FieldSettings.COLL_ARRLIST, "Array list");
		tcbCollect.addItem(FieldSettings.COLL_COLLECT, "Collection");
		tcbCollect.addItem(FieldSettings.COLL_ENUM,    "Enumeration");
		tcbCollect.addItem(FieldSettings.COLL_HASHTBL, "Hashtable");
		tcbCollect.addItem(FieldSettings.COLL_MAP,     "Map");
		tcbCollect.addItem(FieldSettings.COLL_SET,     "Set");
		tcbCollect.addItem(FieldSettings.COLL_VECTOR,  "Vector");

		tcbContain.addItem(FieldSettings.TRUE,  "True");
		tcbContain.addItem(FieldSettings.FALSE, "False");
		tcbContain.addItem(FieldSettings.OMIT,  "Omit");

		tcbSqlDirt.addItem(FieldSettings.DIRTY_CHECK,  "Check");
		tcbSqlDirt.addItem(FieldSettings.DIRTY_IGNORE, "Ignore");
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldSettings s)
	{
		txtGetMeth.refresh(s);
		txtSetMeth.refresh(s);
		txtCreMeth.refresh(s);
		txtHasMeth.refresh(s);
		txtHandler.refresh(s);

		tcbCollect.refresh(s);
		tcbContain.refresh(s);
		tcbSqlDirt.refresh(s);

		chbDirect  .refresh(s);
		chbLazy    .refresh(s);
		chbTrans   .refresh(s);
		chbSqlRO   .refresh(s);
		chbSqlTrans.refresh(s);
	}
}

//==============================================================================
