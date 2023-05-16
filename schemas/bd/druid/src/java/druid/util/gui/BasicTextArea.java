//==============================================================================
//===
//===   BasicTextArea
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.text.PlainDocument;

import org.dlib.gui.CustomLook;
import org.jedit.DefaultInputHandler;
import org.jedit.InputHandler;
import org.jedit.JEditTextArea;
import org.jedit.SyntaxDocument;

//==============================================================================

public class BasicTextArea extends JEditTextArea
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public BasicTextArea()
	{
		setPreferredSize(new Dimension(200,200));
		setBorder(BorderFactory.createEtchedBorder(Color.white, Color.gray));
		setDocument(new SyntaxDocument());

		InputHandler ih = new DefaultInputHandler();
		ih.addDefaultKeyBindings();

		ih.addKeyBinding("C+X", new Cut());
		ih.addKeyBinding("C+C", new Copy());
		ih.addKeyBinding("C+V", new Paste());

		setInputHandler(ih);

		painter.setEOLMarkersPainted(false);
		painter.setInvalidLinesPainted(false);
		painter.setLineHighlightEnabled(false);

		setFont(CustomLook.monospacedFont);

		//--- set tab size

		getDocument().putProperty(PlainDocument.tabSizeAttribute, new Integer(3));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setFont(Font font)
	{
		painter.setFont(font);
	}

	//---------------------------------------------------------------------------

	public void setText(String text)
	{
		super.setText(text);
		scrollTo(0,0);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Support classes
	//---
	//---------------------------------------------------------------------------

	private class Cut implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			cut();
		}
	}

	//---------------------------------------------------------------------------

	private class Copy implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			copy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			paste();
		}
	}
}

//==============================================================================
