//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.config;

import druid.core.config.Config;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;

//==============================================================================

class GeneralPanel extends JPanel
{
	private TCheckBox  chbReload = new TCheckBox("Reload last project");
	private TCheckBox  chbTip    = new TCheckBox("Show 'Tip of the day' next time");
	private TCheckBox  chbBackup = new TCheckBox("Create backup files when saving");
	private TCheckBox  chbTextAA = new TCheckBox("Enable text anti aliasing");
	private TCheckBox  chbGuiAA  = new TCheckBox("Enable GUI anti aliasing");
	private JTextField txtTermin = new TTextField(); //WCC
	private TComboBox  sqlSyntax = new TComboBox();

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 7);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,c,2", chbReload);
		add("0,1,x,c,2", chbTip);
		add("0,2,x,c,2", chbBackup);
		add("0,3,x,c,2", chbTextAA);
		add("0,4,x,c,2", chbGuiAA);

		add("0,5", new TLabel("SQL Syntax")); // Added by Ruslan A. Bondar
		add("0,6", new TLabel("SQL Terminator"));
		add("1,5,x", sqlSyntax); // Added by Ruslan A. Bondar
		add("1,6,x", txtTermin);
	}

	//---------------------------------------------------------------------------

	public void refresh()
	{
		chbReload.setSelected(Config.general.reloadLastProj);
		chbTip   .setSelected(Config.general.showTip);
		chbBackup.setSelected(Config.general.createBackup);
		chbTextAA.setSelected(Config.general.isTextAAliased());
		chbGuiAA .setSelected(Config.general.guiAAliasing);
		txtTermin.setText(Config.general.sqlTerminator);
		sqlSyntax.removeAllItems();

		sqlSyntax.addItem();

		for(String syn : getSyntaxList())
			sqlSyntax.addItem(syn, syn);

		sqlSyntax.setSelectedItem(Config.general.sqlSyntax);
	}

	//---------------------------------------------------------------------------

	public void store()
	{
		Config.general.reloadLastProj = chbReload.isSelected();
		Config.general.showTip        = chbTip   .isSelected();
		Config.general.createBackup   = chbBackup.isSelected();
		Config.general.guiAAliasing   = chbGuiAA .isSelected();

		Config.general.setTextAAliasing(chbTextAA.isSelected());
		Config.general.sqlTerminator = txtTermin.getText();
		Config.general.sqlSyntax     = sqlSyntax.getSelectedItem().toString();
	}

	//---------------------------------------------------------------------------
	/**
	 * Get a list of possible SQL Dialects.
	 * This method reads data/syntaax directory, and return a list of xml files found.
	 * @return a list of available Dialects
	 */

	private ArrayList<String> getSyntaxList()
	{
		File dir=new File(Config.dir.data + File.separator + "syntax");

		ArrayList<String> retval = new ArrayList<String>();
		File[] files  = dir.listFiles();

		for(int i=0; i<files.length; i++)
		{
			String fname=files[i].getName();

			if(fname.substring(fname.length()-3, fname.length()).compareTo("xml")==0)
				retval.add(fname.substring(0, fname.length()-4));
		}

		return retval;
	}
}

//==============================================================================
