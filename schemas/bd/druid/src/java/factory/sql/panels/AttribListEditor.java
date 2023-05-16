//==============================================================================
//===
//===   AttribListEditor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels;

import java.util.Vector;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableConfirmator;

import druid.core.AttribList;
import druid.core.AttribSet;

//==============================================================================

public class AttribListEditor extends JPanel implements  FlexTableConfirmator
{
	protected FlexTable flexTable = new FlexTable(true);

	protected DefaultFlexTableModel flexModel = new DefaultFlexTableModel();

	protected Vector vAttribs = new Vector();

	protected AttribList alData;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AttribListEditor()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup flextable

		flexTable.setFlexModel(flexModel);
		flexModel.setConfirmator(this);

		//------------------------------------------------------------------------
		//--- put all together

		add("0,0,x,x", flexTable);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void addAttrib(String name, String label, int width)
	{
		addAttrib(name, label, width, true);
	}

	//---------------------------------------------------------------------------

	public void addAttrib(String name, String label, int width, boolean editable)
	{
		flexModel.addColumn(label, width, editable);
		vAttribs.addElement(name);
	}

	//---------------------------------------------------------------------------

	public void addAttrib(String name, String label, int width, TComboBox tcb)
	{
		flexModel.addColumn(label, width, tcb);
		vAttribs.addElement(name);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribList al)
	{
		alData = al;

		flexModel.clearData();

		for(int i=0; i<al.size(); i++)
			flexModel.addRow(buildVector(al.get(i)));

		flexTable.setFlexModel(flexModel);
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableConfirmator
	//---
	//---------------------------------------------------------------------------

	public boolean confirmValueChanged(int row, int col, Object value)
	{
		if (value == null)
			return false;

		String name = (String) vAttribs.get(col);

		//--- this 'if' is needed because the combo gui works with strings
		//--- without this 'if' the setData(name, value)  method will rise
		//--- an exception

		if (alData.isAttribAnInt(name))
			alData.get(row).setData(name, new Integer(value.toString()));
		else
			alData.get(row).setData(name, value);

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private Vector buildVector(AttribSet as)
	{
		Vector row = new Vector();

		for(int j=0; j<vAttribs.size(); j++)
		{
			String name = (String)vAttribs.get(j);
			row.add(as.getData(name));
		}

		return row;
	}
}

//==============================================================================
