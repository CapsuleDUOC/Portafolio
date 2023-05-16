//==============================================================================
//===
//===   FontPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TFont;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;
import org.dlib.tools.TVector;
import org.dlib.tools.Util;

import druid.core.AttribSet;

//==============================================================================

public class FontPanel extends TPanel implements ActionListener, ItemListener
{
	private TComboBox  tcbFamily  = new TComboBox();
	private TComboBox  tcbStyle   = new TComboBox();
	private JTextField txtSize    = new TTextField();
	private JTextField txtPreview = new TTextField();

	private static TVector tvFontNames;

	//---------------------------------------------------------------------------

	public FontPanel(String title)
	{
		super(title);

		FlexLayout flexL = new FlexLayout(2,4,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(3, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Family"));
		add("0,1", new TLabel("Style"));
		add("0,2", new TLabel("Size"));
		add("0,3", new TLabel("Preview"));

		add("1,0,x",   tcbFamily);
		add("1,1,x",   tcbStyle);
		add("1,2,x",   txtSize);
		add("1,3,x,x", txtPreview);

		//------------------------------------------------------------------------
		//--- controls setup

		if (tvFontNames == null)
		{
			tvFontNames = new TVector();

			String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

			for(int i=0; i<fonts.length; i++)
			{
				Font f = new Font(fonts[i], Font.PLAIN, 12);

				if (f.getNumGlyphs() != 0)
					tvFontNames.addElement(fonts[i]);
			}
		}

		//--- family combo


		for(int i=0; i<tvFontNames.size(); i++)
			tcbFamily.addItem(ImageFactory.FONT_FAMIL, "?", tvFontNames.elementAt(i));

		tcbFamily.addItemListener(this);

		//--- style combo

		tcbStyle.addItem(ImageFactory.STYLE_PLAIN,    TFont.PLAIN,       "Plain");
		tcbStyle.addItem(ImageFactory.STYLE_BOLD,     TFont.BOLD,        "Bold");
		tcbStyle.addItem(ImageFactory.STYLE_ITALIC,   TFont.ITALIC,      "Italic");
		tcbStyle.addItem(ImageFactory.STYLE_BOLDITAL, TFont.BOLD_ITALIC, "Bold and Italic");

		tcbStyle.addItemListener(this);

		//--- size field

		txtSize.setActionCommand("preview");
		txtSize.addActionListener(this);

		//--- preview field

		txtPreview.setEditable(false);
		txtPreview.setText("Ma La Volpe Col Suo Balzo Ha Raggiunto Il Quieto Fido");
		txtPreview.setPreferredSize(new Dimension(100,50));
	}

	//---------------------------------------------------------------------------

	public void refresh(String prefix, AttribSet as)
	{
		String name  = as.getString(prefix + "Family");
		String style = as.getString(prefix + "Style");
		int    size  = as.getInt(   prefix + "Size");

		tcbFamily.setSelectedItem(name);
		tcbStyle.setSelectedKey(style);
		txtSize.setText("" + size);

		showPreview();
	}

	//---------------------------------------------------------------------------

	public void store(String prefix, AttribSet as)
	{
		String newName  = (String) tcbFamily.getSelectedItem();
		String newStyle = tcbStyle.getSelectedKey();
		int    newSize  = Util.getIntValueMinMax(txtSize.getText(), 8, 4, 50);

		as.setString(prefix + "Family", newName);
		as.setString(prefix + "Style",  newStyle);
		as.setInt(   prefix + "Size",   newSize);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("preview")) showPreview();
	}

	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		showPreview();
	}

	//---------------------------------------------------------------------------

	private void showPreview()
	{
		String name  = (String) tcbFamily.getSelectedItem();
		int    style = TFont.convertToNative(tcbStyle.getSelectedKey());
		int    size  = Util.getIntValueMinMax(txtSize.getText(), 8, 4, 50);

		Font f = new Font(name, style, size);

		txtPreview.setFont(f);
	}
}

//==============================================================================
