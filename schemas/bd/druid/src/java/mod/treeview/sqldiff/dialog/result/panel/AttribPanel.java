//==============================================================================
//===
//===   AttribPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JPanel;

import mod.treeview.sqldiff.struct.DiffElement;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;

import druid.util.DruidUtil;
import druid.util.decoder.Decoder;
import druid.util.gui.ImageFactory;

//==============================================================================

public class AttribPanel extends JPanel
{
	private TPanel tpOld = new TPanel("Old");
	private TPanel tpNew = new TPanel("New");

	private FlexTable oldFlex = new FlexTable(false);
	private FlexTable newFlex = new FlexTable(false);

	protected DefaultFlexTableModel oldModel = new DefaultFlexTableModel();
	protected DefaultFlexTableModel newModel = new DefaultFlexTableModel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AttribPanel()
	{
		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		setupTable(oldFlex, oldModel);
		setupTable(newFlex, newModel);

		setupTPanel(tpOld, oldFlex);
		setupTPanel(tpNew, newFlex);

		add("0,0,x,x", tpOld);
		add("0,1,x,x", tpNew);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void initAttribs()
	{
		oldModel.clearData();
		newModel.clearData();
	}

	//---------------------------------------------------------------------------

	public void setAttrib(DiffElement diff, String name)
	{
		setAttrib(diff, name, null);
	}

	//---------------------------------------------------------------------------

	public void setAttrib(DiffElement diff, String name, Decoder dec)
	{
		if (diff == null) return;

		if (diff.isAdded())
		{
			Vector row = new Vector();

			row.add(ImageFactory.NEW);
			row.add(name);
			row.add(DruidUtil.applyDecoder(dec, diff.objNewValue.toString()));

			newModel.addRow(row);
		}

		else if (diff.isRemoved())
		{
			Vector row = new Vector();

			row.add(ImageFactory.DELETE);
			row.add(name);
			row.add(DruidUtil.applyDecoder(dec, diff.objOldValue.toString()));

			oldModel.addRow(row);
		}

		else
		{
			Vector row = new Vector();

			row.add(ImageFactory.LENS);
			row.add(name);
			row.add(DruidUtil.applyDecoder(dec, diff.objOldValue.toString()));

			oldModel.addRow(row);

			row = new Vector();

			row.add(ImageFactory.LENS);
			row.add(name);
			row.add(DruidUtil.applyDecoder(dec, diff.objNewValue.toString()));

			newModel.addRow(row);
		}
	}

	//---------------------------------------------------------------------------

	public boolean updatePanel()
	{
		tpOld.setVisible(oldModel.getRowCount() !=0);
		tpNew.setVisible(newModel.getRowCount() !=0);

		oldFlex.updateTable();
		newFlex.updateTable();

		return (oldModel.getRowCount() !=0) || (newModel.getRowCount() !=0);
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

	//---------------------------------------------------------------------------

	protected void setupTable(FlexTable flex, DefaultFlexTableModel model)
	{
		model.addColumn("Oper",       30);
		model.addColumn("Attribute", 240);
		model.addColumn("Value",     200);

		flex.setFlexModel(model);
	}
}

//==============================================================================
