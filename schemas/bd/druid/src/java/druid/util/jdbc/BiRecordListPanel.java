//==============================================================================
//===
//===   BiRecordListPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;
import org.dlib.gui.flextable.FlexTable;

import druid.core.jdbc.RecordList;

//==============================================================================

public class BiRecordListPanel extends JPanel
{
	private FlexTable ftUp   = new FlexTable();
	private FlexTable ftDown = new FlexTable();

	private TPanel p1 = new TPanel("");
	private TPanel p2 = new TPanel("");

	//---------------------------------------------------------------------------

	public BiRecordListPanel()
	{
		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------

		FlexLayout flexL1 = new FlexLayout(1,1);
		flexL1.setColProp(0, FlexLayout.EXPAND);
		flexL1.setRowProp(0, FlexLayout.EXPAND);
		p1.setLayout(flexL1);

		p1.add("0,0,x,x", ftUp);

		//------------------------------------------------------------------------

		FlexLayout flexL2 = new FlexLayout(1,1);
		flexL2.setColProp(0, FlexLayout.EXPAND);
		flexL2.setRowProp(0, FlexLayout.EXPAND);
		p2.setLayout(flexL2);

		p2.add("0,0,x,x", ftDown);

		//------------------------------------------------------------------------

		add("0,0,x,x", p1);
		add("0,1,x,x", p2);
	}

	//---------------------------------------------------------------------------

	public void setTitles(String upTitle, String downTitle)
	{
		p1.setTitle(upTitle);
		p2.setTitle(downTitle);
	}

	//---------------------------------------------------------------------------

	public void setEditable(boolean yesnoUp, boolean yesnoDown)
	{
		ftUp.setEditable(yesnoUp);
		ftDown.setEditable(yesnoDown);
	}

	//---------------------------------------------------------------------------

	public void refresh(RecordList rlUp, RecordList rlDown)
	{
		if (rlUp   == null) rlUp   = new RecordList();
		if (rlDown == null) rlDown = new RecordList();

		ftUp.setFlexModel(rlUp);
		ftDown.setFlexModel(rlDown);
	}
}

//==============================================================================
