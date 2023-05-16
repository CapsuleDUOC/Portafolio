//==============================================================================
//===
//===   TitleLabel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;

import org.dlib.gui.TLabel;

//==============================================================================

public class TitleLabel extends TLabel
{
	private static Font  font = new Font("helvetica", Font.BOLD,  16);
	private static Color col  = new Color(160, 160, 160);

	//---------------------------------------------------------------------------

	public TitleLabel()
	{
		setFont(font);
		setForeground(Color.black);
		setBackground(col);
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
	}

	//---------------------------------------------------------------------------

	public TitleLabel(String text)
	{
		this();

		setText(text);
	}

	//---------------------------------------------------------------------------

	public void setText(String title)
	{
		super.setText("  " + title);
	}
}

//==============================================================================
