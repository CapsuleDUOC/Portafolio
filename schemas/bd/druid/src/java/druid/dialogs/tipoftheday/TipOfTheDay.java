//==============================================================================
//===
//===   TipOfTheDay
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.tipoftheday;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TButton;
import org.dlib.gui.TTextArea;
import org.dlib.tools.TextFileLoader;

import druid.core.config.Config;
import druid.dialogs.BasicDialog;

//==============================================================================

public class TipOfTheDay extends BasicDialog implements ActionListener
{
	private TTextArea tlbTip  = new TTextArea("", 4, 30, true);
	private TButton   btnNext = new TButton("Next Tip", "next", this);

	private TextFileLoader tfl = new TextFileLoader(Config.dir.docs +"/tips.txt");

	private int tipNum = -1;

	//---------------------------------------------------------------------------

	public TipOfTheDay(Frame f)
	{
		super(f);

		setCaption("Did you know ?");
		setImage("lightbulb.gif");

		JPanel p = getInnerPanel();

		FlexLayout flexL = new FlexLayout(1, 2, 0, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x,x", tlbTip);
		p.add("0,1,r",   btnNext);

		actionPerformed(null);

		showDialog();
	}

	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent ae)
	{
		if (!tfl.isLoaded())
			return;

		if (tipNum == -1)
			tipNum = (int)(Math.random() * tfl.getRows());

		if (++tipNum == tfl.getRows())
			tipNum = 0;

		tlbTip.setText(tfl.getRowAt(tipNum).replace('|','\n'));
	}
}

//==============================================================================
