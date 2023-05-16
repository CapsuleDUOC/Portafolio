//==============================================================================
//===
//===   FontPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import mod.datagen.docs.pdf.FontInfo;
import mod.datagen.docs.pdf.Settings;
import mod.datagen.docs.pdf.dialogs.FontChooserDialog;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;

import druid.core.DataTracker;

//==============================================================================

class FontPanel extends JPanel implements ActionListener
{
	private TButton tbCover   = new TButton("?", "cover",   this);
	private TButton tbChapter = new TButton("?", "chapter", this);
	private TButton tbSection = new TButton("?", "section", this);
	private TButton tbSubSect = new TButton("?", "subsect", this);
	private TButton tbNormal  = new TButton("?", "normal",  this);
	private TButton tbCode    = new TButton("?", "code",    this);
	private TButton tbTitle   = new TButton("?", "title",   this);
	private TButton tbHeader  = new TButton("?", "header",  this);
	private TButton tbCell    = new TButton("?", "cell",    this);

	private FontChooserDialog fontChooser;

	private Settings sett;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FontPanel()
	{
		FlexLayout flexL = new FlexLayout(2,9);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Cover"));
		add("0,1", new TLabel("Chapter"));
		add("0,2", new TLabel("Section"));
		add("0,3", new TLabel("Subsection"));
		add("0,4", new TLabel("Normal"));
		add("0,5", new TLabel("Code"));
		add("0,6", new TLabel("Table title"));
		add("0,7", new TLabel("Table header"));
		add("0,8", new TLabel("Table cell"));

		add("1,0,x", tbCover);
		add("1,1,x", tbChapter);
		add("1,2,x", tbSection);
		add("1,3,x", tbSubSect);
		add("1,4,x", tbNormal);
		add("1,5,x", tbCode);
		add("1,6,x", tbTitle);
		add("1,7,x", tbHeader);
		add("1,8,x", tbCell);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		sett = s;

		tbCover.setText  (s.getCoverFont().toString());
		tbChapter.setText(s.getChapterFont().toString());
		tbSection.setText(s.getSectionFont().toString());
		tbSubSect.setText(s.getSubSectionFont().toString());
		tbNormal.setText (s.getNormalFont().toString());
		tbCode.setText   (s.getCodeFont().toString());
		tbTitle.setText  (s.getTitleFont().toString());
		tbHeader.setText (s.getHeaderFont().toString());
		tbCell.setText   (s.getCellFont().toString());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		TButton tb = (TButton) e.getSource();

		FontInfo fi = new FontInfo(tb.getText());

		if (fontChooser == null)
			fontChooser = new FontChooserDialog(GuiUtil.getFrame(this));

		fi = fontChooser.run(fi);

		if (fi != null)
		{
			tb.setText(fi.toString());

			String cmd = tb.getActionCommand();

	 		     if (cmd.equals("cover"))   sett.setCoverFont(fi);
			else if (cmd.equals("chapter")) sett.setChapterFont(fi);
			else if (cmd.equals("section")) sett.setSectionFont(fi);
			else if (cmd.equals("subsect")) sett.setSubSectionFont(fi);
			else if (cmd.equals("normal"))  sett.setNormalFont(fi);
			else if (cmd.equals("code"))    sett.setCodeFont(fi);
			else if (cmd.equals("title"))   sett.setTitleFont(fi);
			else if (cmd.equals("header"))  sett.setHeaderFont(fi);
			else if (cmd.equals("cell"))    sett.setCellFont(fi);

			else
				throw new IllegalStateException("Unknown action command --> "+cmd);

			DataTracker.setDataChanged();
		}
	}
}

//==============================================================================
