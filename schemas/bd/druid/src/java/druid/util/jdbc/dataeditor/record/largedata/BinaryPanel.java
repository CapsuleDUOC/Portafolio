//==============================================================================
//===
//===   BinaryPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record.largedata;

import java.awt.Font;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TTextArea;
import org.dlib.tools.Util;

//==============================================================================

class BinaryPanel extends JPanel
{
	private TTextArea txaValue = new TTextArea();
	private Font      font     = new Font("Monospaced", Font.PLAIN, 12);

	//---------------------------------------------------------------------------

	public BinaryPanel()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0,  FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", txaValue);

		txaValue.setEditable(false);
		txaValue.setFont(font);
	}

	//---------------------------------------------------------------------------

	public void setValue(byte[] data)
	{
		if (data == null || data.length == 1)
			txaValue.setText("");
		else
		{
			StringBuffer sb = new StringBuffer();

			int count = (data.length-1) >> 4;

			for(int i=0; i<=count; i++)
				sb.append(buildData(data, i*16));

			txaValue.setText(sb.toString());
		}
	}

	//---------------------------------------------------------------------------

	private String buildData(byte[] data, int pos)
	{
		StringBuffer sb = new StringBuffer();

		for (int i=0; i<16; i++)
			if (pos+i < data.length)
				sb.append(Util.convertToHex(data[pos+i], 2) + " ");

		return Util.convertToHex(pos, 6) + " : " + sb.toString() + "\n";
	}
}

//==============================================================================
