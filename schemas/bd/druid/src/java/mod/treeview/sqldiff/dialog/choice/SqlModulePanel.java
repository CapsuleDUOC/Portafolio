//==============================================================================
//===
//===   SqlModulePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.choice;

import java.util.Enumeration;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.modules.ModuleManager;
import druid.interfaces.SqlGenModule;

//==============================================================================

class SqlModulePanel extends TPanel
{
	private TComboBox tcbModule = new TComboBox();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlModulePanel()
	{
		super("Sql module");

		FlexLayout flexL = new FlexLayout(2,1,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Sql Module to use"));
		add("1,0,x", tcbModule);

		Enumeration e = ModuleManager.getModules(SqlGenModule.class);

		while (e.hasMoreElements())
		{
			SqlGenModule mod = (SqlGenModule) e.nextElement();

			tcbModule.addItem(mod.getId(), mod.getFormat());
		}

		tcbModule.setSelectedKey("stdSql");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public SqlGenModule getSelectedModule()
	{
		return (SqlGenModule) ModuleManager.getModule(SqlGenModule.class, tcbModule.getSelectedKey());
	}
}

//==============================================================================
