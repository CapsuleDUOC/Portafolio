//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.hsqldb.sql.panels.table;

import mod.dbms.hsqldb.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

public class GeneralPanel extends TPanel
{
	private TComboBoxGuardian tcbType = new TComboBoxGuardian("Type");

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 1);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Table type"));
		add("1,0,x", tcbType);

		//--- setup combobox

		tcbType.addItem(TableSettings.TYPE_DEFAULT, "Default");
		tcbType.addItem(TableSettings.TYPE_MEMORY,  "Memory");
		tcbType.addItem(TableSettings.TYPE_CACHED,  "Cached");
		tcbType.addItem(TableSettings.TYPE_TEMP,    "Temporary");
		tcbType.addItem(TableSettings.TYPE_TEXT,    "Text");
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts)
	{
		tcbType.refresh(ts);
	}
}

//==============================================================================
