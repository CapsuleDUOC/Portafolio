//==============================================================================
//===
//===   DbWizard
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.wizards.dbwizard;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TTextArea;

import druid.core.DataTracker;
import druid.core.config.Config;
import druid.core.io.ProjectManager;
import druid.data.DatabaseNode;
import druid.data.ProjectNode;
import druid.dialogs.BasicWizard;

//==============================================================================

public class DbWizard extends BasicWizard implements ItemListener
{
	private ProjectNode  projNode;

	private TTextArea    txaTip   = new TTextArea(4, 30);
	private TComboBox    tcbTempl = new TComboBox();
	private JRadioButton jrbEmpty = new JRadioButton("Create an empty database");
	private JRadioButton jrbMinim = new JRadioButton("Create a minimal database (recommended)");
	private JRadioButton jrbTempl = new JRadioButton("Create the database from a template");

	private String sTemplateDir = Config.dir.data + "/templates/database";

	private String sEmpty = "The database is created without any data (no field-attribs and " +
									"no datatypes). For expert users only.";


	private String sMinim = "The database is created with most common field-attribs and " +
									" a minimal set of datatypes. Good to start a new db.";

	//---------------------------------------------------------------------------

	public DbWizard(Frame f, ProjectNode proj)
	{
		super(f);

		projNode = proj;

		setCaption("Database Wizard");

		JPanel p = addPage();

		FlexLayout flexL = new FlexLayout(2, 6, 0, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x,c,2", jrbEmpty);
		p.add("1,1,x", new TTextArea(sEmpty,3,30, true));

		p.add("0,2,x,c,2", jrbMinim);
		p.add("1,3,x", new TTextArea(sMinim,3,30, true));

		p.add("0,4,x,c,2", jrbTempl);
		p.add("1,5,x",     tcbTempl);

		//------------------------------------------------------------------------
		//--- setup radio-button's group

		ButtonGroup bg = new ButtonGroup();
		bg.add(jrbEmpty);
		bg.add(jrbMinim);
		bg.add(jrbTempl);

		jrbMinim.setSelected(true);

		//------------------------------------------------------------------------
		//--- fill template's combo

		String[] templates = new File(sTemplateDir).list();

		if (templates == null)
		{
			JOptionPane.showMessageDialog(Frame.getFrames()[0],
						"Cannot access the data/templates dir.\n" +
						"Possible causes:\n" +
						" - Dir not found\n" +
						" - Access denied (check user privilegs)\n",
						"Operation aborted", JOptionPane.WARNING_MESSAGE);

			return;
		}

		for(int i=0; i<templates.length; i++)
		{
			String t = templates[i];

			if (t.endsWith(".druid") && !t.startsWith("_"))
				tcbTempl.addItem(t, t.substring(0, t.indexOf(".druid")));
		}

		tcbTempl.setEnabled(tcbTempl.getItemCount() != 0);
		tcbTempl.addItemListener(this);

		showDialog();
	}

	//---------------------------------------------------------------------------

	protected void doJob()
	{
		//------------------------------------------------------------------------
		//--- create an empty database

		if (jrbEmpty.isSelected())
		{
			//--- adds child, selecting it and making it visible
			projNode.addChild(new DatabaseNode());
		}

		//------------------------------------------------------------------------
		//--- create a minimal database

		else if (jrbMinim.isSelected())
			addFromTemplate("_minimal.druid");

		//------------------------------------------------------------------------
		//--- create a database from a template

		else
			addFromTemplate(tcbTempl.getSelectedKey());

		//--- enables events and triggers a data-changed

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void addFromTemplate(String fileName)
	{
		try
		{
			ProjectNode auxProjNode = new ProjectNode();

			ProjectManager.loadProject(auxProjNode, sTemplateDir + "/" + fileName);

			if (auxProjNode.getChildCount() != 0)
				projNode.addChild(auxProjNode.getChild(0));
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this,
					"The following error has occured when loading the '" + fileName + "' template.\n" +
					"Error : " + e,
					"Open Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		jrbTempl.setSelected(true);
	}
}

//==============================================================================
