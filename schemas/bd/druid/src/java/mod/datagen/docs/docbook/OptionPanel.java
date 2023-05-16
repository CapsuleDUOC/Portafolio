//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================
/*
    modif Bruno Vernay 05/08/2003
*/
package mod.datagen.docs.docbook;

import java.io.File;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.config.Config;
import druid.util.Language;
import druid.util.gui.guardians.TComboBoxGuardian;

//==============================================================================

class OptionPanel extends TPanel
{
	private TComboBoxGuardian tcbLanguage = new TComboBoxGuardian("Language");
	private TComboBoxGuardian tcbSkin     = new TComboBoxGuardian("Skin");
/*	private TComboBox  tcbLanguage = new TComboBox();
	private TComboBox  tcbSkin     = new TComboBox();
	private TComboBox  tcbThumb    = new TComboBox();
*/
	private static final String msgEmptyDir = "<cannot scan directory>";

	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		super("General");

		//setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

		FlexLayout flexL = new FlexLayout(2,2);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Language"));
		add("0,1",   new TLabel("Template"));
		//add("0,2",   new TLabel("Thumbnails"));

		add("1,0,x", tcbLanguage);
		add("1,1,x", tcbSkin);
		//add("1,2,x", tcbThumb);

		tcbLanguage.setToolTipText("Language to use to generate the html files");
		tcbSkin.setToolTipText("Templates to use to generate the docbook files");

		//--- setup comboboxes

		Language.fillComboBox(tcbLanguage);

		setupSkins();

	}

	//---------------------------------------------------------------------------

	public void refresh(SettingsDbk s)
	{
		tcbLanguage.refresh(s);
		tcbSkin    .refresh(s);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupSkins()
	{
		String path   = Config.dir.data + "/templates/docgen/docbook";

		String dirs[] = new File(path).list();

		if (dirs == null)
			tcbSkin.addItem("Classic", msgEmptyDir);
		else
            for(int i=0; i<dirs.length; i++)
					if (!dirs[i].equals("CVS"))
   	             		tcbSkin.addItem(dirs[i], dirs[i]);
	}
}

//==============================================================================
