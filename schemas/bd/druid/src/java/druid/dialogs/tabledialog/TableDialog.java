//==============================================================================
//===
//===   TableDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.tabledialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;

import org.dlib.gui.TDialog;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableClickListener;
import org.dlib.gui.flextable.FlexTableSelEvent;

//==============================================================================

public class TableDialog extends TDialog implements FlexTableClickListener
{
	private FlexTable ft = new FlexTable(false);

	private DefaultFlexTableModel flexModel;

	private boolean enableClick = false;

	private int selRow;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TableDialog(Frame frame, String title)
	{
		super(frame, title, true);

		ft.setPreferredSize(new Dimension(320, 300));

		flexModel = (DefaultFlexTableModel) ft.getFlexModel();

		getContentPane().add(ft, BorderLayout.CENTER);
		ft.addClickListener(this);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void addColumn(String header, int width)
	{
		flexModel.addColumn(header, width);
	}

	//---------------------------------------------------------------------------

	public void showDialog()
	{
		ft.updateFlexModel();

		super.showDialog();
	}

	//---------------------------------------------------------------------------
	//--- 1 column methods
	//---------------------------------------------------------------------------

	public void append(String value)
	{
		Vector v = new Vector();

		v.addElement(value);

		flexModel.addRow(v);
	}

	//---------------------------------------------------------------------------
	//--- 2 columns methods
	//---------------------------------------------------------------------------

	public void append(String name, float value)
	{
		append(name, new Float(value));
	}

	//---------------------------------------------------------------------------

	public void append(String name, int value)
	{
		append(name, new Integer(value));
	}

	//---------------------------------------------------------------------------

	public void append(String name, Object value)
	{
		Vector v = new Vector();

		v.addElement(name);
		v.addElement(value);

		flexModel.addRow(v);
	}

	//---------------------------------------------------------------------------

	public void setClickable(boolean yesno)
	{
		enableClick = yesno;
	}

	//---------------------------------------------------------------------------

	public int getClickedRow()
	{
		return selRow;
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTable Click Listener
	//---
	//---------------------------------------------------------------------------

	public void rowClicked(FlexTableSelEvent e)
	{
		selRow = e.getSelectedRow();

		if (enableClick)
			hide();
	}
}

//==============================================================================
