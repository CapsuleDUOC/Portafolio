//==============================================================================
//===
//===   TextPanel : handles chars and varchars
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record.text;

import java.awt.Font;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TTextArea;

//==============================================================================

public class TextPanel extends JPanel
{
	private TTextArea txaValue = new TTextArea(20, 70);
	private Font      font     = new Font("Monospaced", Font.PLAIN, 12);

	//---------------------------------------------------------------------------

	public TextPanel(KeyListener kl)
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0,  FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", txaValue);

		txaValue.addKeyListener(kl);
		txaValue.setFont(font);
	}

	//---------------------------------------------------------------------------

	public void setValue(String value)
	{
		if (value == null) value = "";

		txaValue.setText(value);
	}

	//---------------------------------------------------------------------------

	public String getValue()
	{
		return txaValue.getText();
	}
}

//==============================================================================
