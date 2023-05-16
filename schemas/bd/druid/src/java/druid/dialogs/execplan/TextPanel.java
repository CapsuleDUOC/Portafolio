//==============================================================================
//===
//===   TextPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.execplan;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.dlib.gui.FlexLayout;

//==============================================================================

public class TextPanel extends JPanel
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TextPanel()
	{
		FlexLayout fl = new FlexLayout(1, 1);
		fl.setColProp(0, FlexLayout.EXPAND);
		fl.setRowProp(0, FlexLayout.EXPAND);
		setLayout(fl);

		add("0,0,x,x", new JScrollPane(txaText));
		
		Font f = new Font(Font.MONOSPACED, Font.PLAIN, 12);
		txaText.setFont(f);
		txaText.setEditable(false);
	}

	//---------------------------------------------------------------------------
	//---
	//---   API methods
	//---
	//---------------------------------------------------------------------------

	public void setText(String text)
	{
		txaText.setText(text);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private JTextArea txaText = new JTextArea();
}

//==============================================================================
