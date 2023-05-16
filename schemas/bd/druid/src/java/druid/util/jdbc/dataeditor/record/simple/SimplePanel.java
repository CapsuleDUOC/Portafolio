//==============================================================================
//===
//===   SimplePanel : handles numbers and dates
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record.simple;

import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TTextField;

//==============================================================================

public class SimplePanel extends JPanel
{
	private JTextField txtValue = new TTextField();

	//---------------------------------------------------------------------------

	public SimplePanel(KeyListener kl)
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", txtValue);

		txtValue.addKeyListener(kl);
	}

	//---------------------------------------------------------------------------

	public void setValue(Object obj)
	{
		String value = "";

		if (obj != null)
			value = obj.toString();

		txtValue.setText(value);
	}

	//---------------------------------------------------------------------------

	public String getValue()
	{
		return txtValue.getText();
	}
}

//==============================================================================
