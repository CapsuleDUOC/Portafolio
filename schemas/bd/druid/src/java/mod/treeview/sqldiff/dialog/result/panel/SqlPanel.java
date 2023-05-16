//==============================================================================
//===
//===   SqlPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel;

import java.awt.Component;

import javax.swing.JPanel;

import mod.treeview.sqldiff.struct.DiffElement;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.util.gui.SqlTextArea;

//==============================================================================

public class SqlPanel extends JPanel
{
	private TPanel tpOld = new TPanel("Old");
	private TPanel tpNew = new TPanel("New");

	private SqlTextArea txaOld = new SqlTextArea();
	private SqlTextArea txaNew = new SqlTextArea();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlPanel()
	{
		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		txaOld.setEditable(false);
		txaNew.setEditable(false);

		setupTPanel(tpOld, txaOld);
		setupTPanel(tpNew, txaNew);

		add("0,0,x,x", tpOld);
		add("0,1,x,x", tpNew);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean setText(DiffElement diff)
	{
		if (diff == null)
		{
			tpOld.setVisible(false);
			tpNew.setVisible(false);

			return false;
		}

		String oldText = (String) diff.objOldValue;
		String newText = (String) diff.objNewValue;

		if (oldText != null)
			txaOld.setText(oldText);

		if (newText != null)
			txaNew.setText(newText);

		tpOld.setVisible(oldText != null);
		tpNew.setVisible(newText != null);

		return (oldText != null) || (newText != null);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupTPanel(TPanel p, Component c)
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);

		p.setLayout(flexL);
		p.add("0,0,x,x", c);
	}
}

//==============================================================================
