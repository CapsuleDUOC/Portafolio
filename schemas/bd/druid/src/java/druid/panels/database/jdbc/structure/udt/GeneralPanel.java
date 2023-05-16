//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.udt;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TLabel;

import druid.core.jdbc.entities.UDTEntity;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private JTextField txtClass    = new ROTextField();
	private JTextField txtDataType = new ROTextField();
	private JTextField txtBaseType = new ROTextField();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2,3,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Java class"));
		add("0,1",   new TLabel("Data type"));
		add("0,2",   new TLabel("Base type"));

		add("1,0,x", txtClass);
		add("1,1,x", txtDataType);
		add("1,2,x", txtBaseType);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(UDTEntity node)
	{
		txtClass.setText(node.sClass);
		txtDataType.setText(node.sDataType);
		txtBaseType.setText(node.sBaseType);
	}
}

//==============================================================================
