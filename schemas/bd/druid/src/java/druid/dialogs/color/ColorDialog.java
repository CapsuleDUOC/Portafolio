//==============================================================================
//===
//===   ColorDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.color;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import druid.dialogs.BasicConfigDialog;

//==============================================================================

public class ColorDialog extends BasicConfigDialog
{
	private JColorChooser colEditor;

	private Color currColor = null;

	//---------------------------------------------------------------------------

	public ColorDialog(Frame frame, Color c)
	{
		super(frame);

		setTitle("Color editor");

		//------------------------------------------------------------------------
		//--- refresh data

		colEditor.setColor(c);
		showDialog();

		//------------------------------------------------------------------------
		//--- store data

		if (!isCancelled())
			currColor = colEditor.getColor();
	}

	//---------------------------------------------------------------------------

	protected JComponent getCentralPanel()
	{
		//--- we must build colEditor here because this method is called
		//--- BEFORE the constructor

		colEditor = new JColorChooser();

		return colEditor;
	}

	//---------------------------------------------------------------------------

	public Color getColor()
	{
		return currColor;
	}
}

//==============================================================================
