//==============================================================================
//===
//===   RangePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.datatypes;

import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;

import druid.util.gui.ChangeSentinel;

//==============================================================================

public class RangePanel extends TPanel
{
	private JTextField txtMin   = new TTextField();
	private JTextField txtMax   = new TTextField();
	private TCheckBox  chbRange = new TCheckBox("Outside");

	//---------------------------------------------------------------------------

	public RangePanel()
	{
		super("Range");

		FlexLayout flexL = new FlexLayout(2,3,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Min value"));
		add("0,1",   new TLabel("Max value"));
		add("0,2",   new TLabel("Range is"));
		add("1,0,x", txtMin);
		add("1,1,x", txtMax);
		add("1,2,x", chbRange);

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		txtMin.getDocument().addDocumentListener(sent);
		txtMax.getDocument().addDocumentListener(sent);

		chbRange.addItemListener(sent);
	}

	//---------------------------------------------------------------------------

	public void set(String min, String max, boolean outside)
	{
		txtMin.setText(min);
		txtMax.setText(max);

		chbRange.setSelected(outside);
	}

	//---------------------------------------------------------------------------

	public String  getMin()    { return txtMin.getText();      }
	public String  getMax()    { return txtMax.getText();      }
	public boolean isOutside() { return chbRange.isSelected(); }
}

//==============================================================================
