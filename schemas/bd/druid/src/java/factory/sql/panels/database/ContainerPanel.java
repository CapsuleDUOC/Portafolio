//==============================================================================
//===
//===   ContainerPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import factory.sql.BasicDatabaseSettings;

//==============================================================================

public class ContainerPanel extends JPanel
{
	private OptionsPanel  panOptions  = new OptionsPanel();
	private GeneratePanel panGenerate = new GeneratePanel();
	private MappingPanel  panMapping  = new MappingPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ContainerPanel()
	{
		FlexLayout flexL = new FlexLayout(1,3);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(2, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x",   panOptions);
		add("0,1,x",   panGenerate);
		add("0,2,x,x", panMapping);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(BasicDatabaseSettings s)
	{
		panOptions .refresh(s);
		panGenerate.refresh(s);
		panMapping .refresh(s.getSqlMapping());
	}
}

//==============================================================================
