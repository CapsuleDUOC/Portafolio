//==============================================================================
//===
//===   SqlDiffMenu
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.dlib.gui.MenuBuilder;
import org.dlib.tools.TFileFilter;

import druid.core.config.Config;
import druid.util.DruidUtil;

//==============================================================================

public class MenuHandler implements ActionListener
{
	public JMenuBar menuBar;

	private MenuBuilder menuBuilder = new MenuBuilder();

	private ResultDialog resDlg;
	private JMenuItem    miInvDiff;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MenuHandler(ResultDialog dlg)
	{
		resDlg = dlg;

		menuBuilder.setActionListener(this);
		menuBuilder.setXmlFile(Config.dir.data +"/menu/sql-diff.xml");

		menuBar   = menuBuilder.getMenuBar();
		miInvDiff = menuBuilder.getMenuItem("invDiff");
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("saveText"))   handleSaveText();
		if (cmd.equals("saveScript")) handleSaveScript();
		if (cmd.equals("invDiff"))    handleInvDiff();
		if (cmd.equals("exit"))       resDlg.setVisible(false);
	}

	//---------------------------------------------------------------------------

	private void handleSaveText()
	{
		JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Save text diff");
		fc.addChoosableFileFilter(new TFileFilter("txt", "Text file"));

		int res = fc.showDialog(resDlg, "Save");
		if (res == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().getPath();

			if (!fileName.endsWith(".txt"))
				fileName += ".txt";

			DruidUtil.saveText(resDlg, fileName, resDlg.getTextDiff());
		}
	}

	//---------------------------------------------------------------------------

	private void handleSaveScript()
	{
		JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Save migration script");
		fc.addChoosableFileFilter(new TFileFilter("sql", "Sql command file"));

		int res = fc.showDialog(resDlg, "Save");
		if (res == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().getPath();

			if (!fileName.endsWith(".sql"))
				fileName += ".sql";

			DruidUtil.saveText(resDlg, fileName, resDlg.getMigrScript());
		}
	}

	//---------------------------------------------------------------------------

	private void handleInvDiff()
	{
		resDlg.diff(miInvDiff.isSelected());
	}
}

//==============================================================================
