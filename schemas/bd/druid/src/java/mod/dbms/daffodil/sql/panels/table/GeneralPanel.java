//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.daffodil.sql.panels.table;

import druid.data.DatabaseNode;
import druid.util.gui.guardians.TTextFieldGuardian;

import mod.dbms.daffodil.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

//==============================================================================

public class GeneralPanel extends TPanel
{
	private TTextFieldGuardian tfgCountry = new TTextFieldGuardian("Country");
	private TTextFieldGuardian tfgLanguage= new TTextFieldGuardian("Language");

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 2);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Country"));
		add("0,1",   new TLabel("Language"));

		add("1,0,x", tfgCountry);
		add("1,1,x", tfgLanguage);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts, DatabaseNode dbNode)
	{
		tfgCountry .refresh(ts);
		tfgLanguage.refresh(ts);
	}
}

//==============================================================================
