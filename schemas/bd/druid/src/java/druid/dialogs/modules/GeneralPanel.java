//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.modules;

import java.awt.Color;

import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextArea;
import org.dlib.gui.TTextField;

import druid.interfaces.BasicModule;

//==============================================================================

public class GeneralPanel extends TPanel
{
	private JTextField txtId      = new TTextField();
	private JTextField txtVersion = new TTextField();
	private JTextField txtAuthor  = new TTextField();
	private TTextArea  txaDescr   = new TTextArea(6, 40);

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2,4,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup textfields

		txtId.setEditable(false);
		txtVersion.setEditable(false);
		txtAuthor.setEditable(false);
		txaDescr.setEditable(false);

		txtId.setBackground(Color.white);
		txtVersion.setBackground(Color.white);
		txtAuthor.setBackground(Color.white);

		//--- setup panel

		add("0,0",     new TLabel("Id"));
		add("0,1",     new TLabel("Version"));
		add("0,2",     new TLabel("Author"));
		add("0,3,l,t", new TLabel("Descr"));

		add("1,0,x", txtId);
		add("1,1,x", txtVersion);
		add("1,2,x", txtAuthor);
		add("1,3,x", txaDescr);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(BasicModule mod)
	{
		txtId.setText(mod.getId());
		txtVersion.setText(mod.getVersion());
		txtAuthor.setText(mod.getAuthor());
		txaDescr.setText(mod.getDescription());
	}
}

//==============================================================================
