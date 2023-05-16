//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni, Antonio Gallardo
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.html;

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
	private TComboBoxGuardian tcbThumb    = new TComboBoxGuardian("Thumbnails", true);

	private static final String msgEmptyDir = "<cannot scan directory>";

	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2,3);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Language"));
		add("0,1",   new TLabel("Skin"));
		add("0,2",   new TLabel("Thumbnails"));

		add("1,0,x", tcbLanguage);
		add("1,1,x", tcbSkin);
		add("1,2,x", tcbThumb);

		tcbLanguage.setToolTipText("Language to use to generate the html files");
		tcbSkin.setToolTipText("Skin (html templates) to use to generate the html files");
		tcbThumb.setToolTipText("Size of the er-views thumbnails");

		//--- setup comboboxes

		tcbThumb.addItem(128, "128 x 128");
		tcbThumb.addItem(192, "192 x 192");
		tcbThumb.addItem(256, "256 x 256");

		Language.fillComboBox(tcbLanguage);

		setupSkins();
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		tcbLanguage.refresh(s);
		tcbSkin    .refresh(s);
		tcbThumb   .refresh(s);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupSkins()
	{
		String path   = Config.dir.data +"/"+ HtmlDocs.SKIN_PATH;

		String dirs[] = new File(path).list();

		if (dirs == null)
			tcbSkin.addItem("Classic", msgEmptyDir);
		else
		{
			for(int i=0; i<dirs.length; i++)
				if (!dirs[i].equals("CVS"))
					tcbSkin.addItem(dirs[i], dirs[i]);
		}
	}
}

//==============================================================================
