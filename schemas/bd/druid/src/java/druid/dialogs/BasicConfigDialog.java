//==============================================================================
//===
//===   BasicConfigDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TButton;
import org.dlib.gui.TDialog;
import org.dlib.gui.TSeparator;

//==============================================================================

public abstract class BasicConfigDialog extends TDialog implements ActionListener
{
	private TButton tbOk     = new TButton("Ok",     "ok",     this);
	private TButton tbCancel = new TButton("Cancel", "cancel", this);

	//---------------------------------------------------------------------------

	public BasicConfigDialog(Frame frame)
	{
		super(frame, "", true);

		//--- button panel

		JPanel bp = new JPanel();

		FlexLayout fL = new FlexLayout(3,1,4,4);
		fL.setColProp(0, FlexLayout.EXPAND);
		fL.setNullGaps(100, 0);

		bp.setLayout(fL);

		bp.add("1,0", tbOk);
		bp.add("2,0", tbCancel);

		//--- main panel

		JPanel p = new JPanel();

		FlexLayout flexL = new FlexLayout(1,3);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);

		p.setLayout(flexL);
		p.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

		p.add("0,0,x,x", getCentralPanel());
		p.add("0,1,x",   new TSeparator(TSeparator.HORIZONTAL));
		p.add("0,2,x",   bp);

		getContentPane().add(p, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------

	protected abstract JComponent getCentralPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Listener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("ok")) hide();

		if (cmd.equals("cancel"))
		{
			setCancelled();
			hide();
		}
	}
}

//==============================================================================
