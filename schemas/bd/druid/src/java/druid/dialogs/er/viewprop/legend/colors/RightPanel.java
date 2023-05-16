//==============================================================================
//===
//===   RightPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop.legend.colors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.DataTracker;
import druid.data.er.LegendColor;
import druid.dialogs.color.ColorDialog;

//==============================================================================

public class RightPanel extends TPanel implements ActionListener
{
	private TButton btnName   = new TButton("", "name",   this);
	private TButton btnNameBg = new TButton("", "nameBg", this);
	private TButton btnText   = new TButton("", "text",   this);
	private TButton btnTextBg = new TButton("", "textBg", this);
	private TButton btnBg     = new TButton("", "bg",     this);
	private TButton btnBorder = new TButton("", "border", this);

	private LegendColor currColor;

	//---------------------------------------------------------------------------

	public RightPanel()
	{
		super("Colors");

		//------------------------------------------------------------------------
		//--- setp panel

		FlexLayout flexL = new FlexLayout(2,6,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Name"));
		add("0,1", new TLabel("Name backgnd"));
		add("0,2", new TLabel("Text"));
		add("0,3", new TLabel("Text backgnd"));
		add("0,4", new TLabel("Background"));
		add("0,5", new TLabel("Border"));

		add("1,0,x", btnName);
		add("1,1,x", btnNameBg);
		add("1,2,x", btnText);
		add("1,3,x", btnTextBg);
		add("1,4,x", btnBg);
		add("1,5,x", btnBorder);
	}

	//---------------------------------------------------------------------------

	public void refresh(LegendColor lc)
	{
		currColor = lc;

		btnName.setBackground(lc.colName);
		btnNameBg.setBackground(lc.colNameBg);
		btnText.setBackground(lc.colText);
		btnTextBg.setBackground(lc.colTextBg);
		btnBg.setBackground(lc.colBg);
		btnBorder.setBackground(lc.colBorder);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(LegendColor lc)
	{
		lc.colName   = getColor(btnName);
		lc.colNameBg = getColor(btnNameBg);
		lc.colText   = getColor(btnText);
		lc.colTextBg = getColor(btnTextBg);
		lc.colBg     = getColor(btnBg);
		lc.colBorder = getColor(btnBorder);
	}

	//---------------------------------------------------------------------------

	private Color getColor(TButton tb)
	{
		Color c = tb.getBackground();

		return new Color(c.getRed(), c.getGreen(), c.getBlue());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("name"))   setColor(btnName);
		if (cmd.equals("nameBg")) setColor(btnNameBg);
		if (cmd.equals("text"))   setColor(btnText);
		if (cmd.equals("textBg")) setColor(btnTextBg);
		if (cmd.equals("bg"))     setColor(btnBg);
		if (cmd.equals("border")) setColor(btnBorder);
	}

	//---------------------------------------------------------------------------

	private void setColor(TButton tb)
	{
		Color c = tb.getBackground();

		ColorDialog cd = new ColorDialog(GuiUtil.getFrame(this), c);

		c = cd.getColor();

		if (c != null)
		{
			tb.setBackground(c);

			DataTracker.setDataChanged();

			saveDataToNode(currColor);
			currColor.refresh();
		}
	}

}

//==============================================================================
