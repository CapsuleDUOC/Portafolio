//==============================================================================
//===
//===   AboutDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.about;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ImagePanel;
import org.dlib.gui.TDialog;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.TTextArea;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.tools.TextFileLoader;

import druid.Druid;
import druid.core.config.Config;

//==============================================================================

public class AboutDialog extends TDialog
{
	private JLabel lVersion = new TLabel("The database manager, version " + Druid.VERSION);
	private JLabel lAutText = new TLabel("Author");
	private JLabel lAuthor  = new TLabel("Andrea Carboni");
	private JLabel lUrl     = new TLabel("http://sourceforge.net/projects/druid");

	private FlexTable ftContrib  = new FlexTable(false);
	private FlexTable ftTools    = new FlexTable(false);
	private FlexTable ftSystem   = new FlexTable(false);
	private TTextArea txaLicense = new TTextArea();

	private ImagePanel imgPanel = new ImagePanel(Config.dir.images + "/logo.png");

	private Font smallFont = new Font("helvetica", Font.PLAIN, 12);
	private Font bigFont   = new Font("helvetica", Font.BOLD,  14);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AboutDialog(Frame frame)
	{
		super(frame, "About...", true);

		JPanel p = new JPanel();

		FlexLayout flexL = new FlexLayout(1, 6, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(4, FlexLayout.EXPAND);
		flexL.setNullGaps(32, 32);

		p.setLayout(flexL);

		p.add("0,0,c",   imgPanel);
		p.add("0,1,c",   lVersion);
		p.add("0,2,c",   lAutText);
		p.add("0,3,c",   lAuthor);
		p.add("0,5,c",   lUrl);

		//------------------------------------------------------------------------

		TTabbedPane jtp = new TTabbedPane();

		jtp.add("About",        p);
		jtp.add("Contributors", ftContrib);
		jtp.add("Tools",        ftTools);
		jtp.add("System",       ftSystem);
		jtp.add("License",      txaLicense);

		jtp.setPreferredSize(new Dimension(550, 300));

		getContentPane().add(jtp, BorderLayout.CENTER);

		//------------------------------------------------------------------------
		//--- setup contrib and tool tables

		setupContribs();
		setupTools();
		setupSystem();

		//------------------------------------------------------------------------
		//--- setup textarea

		txaLicense.setEditable(false);

		TextFileLoader tfl = new TextFileLoader(Config.dir.docs + "/COPYING.txt");

		if (tfl.isLoaded())
			txaLicense.setText(tfl.getString());

		//------------------------------------------------------------------------
		//--- setup labels

		lAutText.setFont(smallFont);
		lAuthor.setFont(bigFont);

		imgPanel.setMargin(8);

		showDialog();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupContribs()
	{
		DefaultFlexTableModel mod = new DefaultFlexTableModel();

		mod.addColumn("Name", 80);
		mod.addColumn("Work", 270);

		//------------------------------------------------------------------------

		TextFileLoader tfl = new TextFileLoader(Config.dir.docs + "/contributors.txt");

		if (tfl.isLoaded())
			fillTable(mod, tfl);

		ftContrib.setFlexModel(mod);
	}

	//---------------------------------------------------------------------------

	private void setupTools()
	{
		DefaultFlexTableModel mod = new DefaultFlexTableModel();

		mod.addColumn("Name", 100);
		mod.addColumn("URL",  140);

		//------------------------------------------------------------------------

		TextFileLoader tfl = new TextFileLoader(Config.dir.docs + "/tools.txt");

		if (tfl.isLoaded())
			fillTable(mod, tfl);

		ftTools.setFlexModel(mod);
	}

	//---------------------------------------------------------------------------

	private void fillTable(DefaultFlexTableModel mod, TextFileLoader tfl)
	{
		for(int i=0; i<tfl.getRows(); i++)
		{
			StringTokenizer st = new StringTokenizer(tfl.getRowAt(i), "|");

			Vector v = new Vector();

			v.add(st.nextToken());
			v.add(st.nextToken());

			mod.addRow(v);
		}
	}

	//---------------------------------------------------------------------------

	private void setupSystem()
	{
		DefaultFlexTableModel mod = new DefaultFlexTableModel();

		mod.addColumn("Name",   90);
		mod.addColumn("Value", 150);

		//------------------------------------------------------------------------

		Properties p = System.getProperties();

		for(Enumeration e=p.propertyNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			String value= p.getProperty(name, "<NOT SET>");

			Vector v = new Vector();

			v.add(name);
			v.add(value);

			mod.addRow(v);
		}

		ftSystem.setFlexModel(mod);
	}
}

//==============================================================================
