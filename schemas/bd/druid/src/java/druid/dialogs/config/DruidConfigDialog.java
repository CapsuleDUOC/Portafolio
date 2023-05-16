//==============================================================================
//===
//===   DruidConfigDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.config;

import java.awt.Frame;

import javax.swing.JComponent;

import org.dlib.gui.TTabbedPane;

import druid.dialogs.BasicConfigDialog;

//==============================================================================

public class DruidConfigDialog extends BasicConfigDialog
{
	private GeneralPanel panGeneral;
	private JdbcPanel    panJdbc;
	private ErViewPanel  panErView;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DruidConfigDialog(Frame frame)
	{
		super(frame);

		setTitle("Options");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void run()
	{
		//------------------------------------------------------------------------
		//--- refresh data

		panGeneral.refresh();
		panJdbc.refresh();
		panErView.refresh();

		showDialog();

		//------------------------------------------------------------------------
		//--- store data

		if (!isCancelled())
		{
			panGeneral.store();
			panJdbc.store();
			panErView.store();

			//--- repaint owner because the anti-aliasing options can be changed
			getOwner().repaint();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected JComponent getCentralPanel()
	{
		TTabbedPane tabP = new TTabbedPane();

		panGeneral = new GeneralPanel();
		panJdbc    = new JdbcPanel();
		panErView  = new ErViewPanel();

		tabP.add("General",  panGeneral);
//		tabP.add("Jdbc",     panJdbc);
		tabP.add("E/R View", panErView);

		return tabP;
	}
}

//==============================================================================
