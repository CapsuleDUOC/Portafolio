//==============================================================================
//===
//===   SqlDiffModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff;

import java.awt.Frame;

import javax.swing.JOptionPane;

import mod.treeview.sqldiff.dialog.choice.ChoiceDialog;
import mod.treeview.sqldiff.dialog.result.ResultDialog;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TreeNodeModule;

//==============================================================================

public class SqlDiffModule implements TreeNodeModule
{
	private ResultDialog diffDlg;

	//---------------------------------------------------------------------------

	public String getId()       { return "sqlDiff"; }
	public String getAuthor()   { return "Andrea Carboni"; }
	public String getVersion()  { return "1.0"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Performs a diff between the selected database and another. The second " +
				 "database can be on the same project or loaded from a file. " +
				 "The diff regards only sql differences.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public String getPopupText()
	{
		return "Sql diff...";
	}

	//---------------------------------------------------------------------------

	public boolean isNodeAccepted(TreeViewNode node)
	{
		return (node instanceof DatabaseNode);
	}

	//---------------------------------------------------------------------------

	public boolean isNodeEnabled(TreeViewNode node)
	{
		return true;
	}

	//---------------------------------------------------------------------------

	public void nodeSelected(Frame f, TreeViewNode node)
	{
		ChoiceDialog cd = new ChoiceDialog(f, (DatabaseNode) node);

		cd.showDialog();

		if (cd.isCancelled()) return;

		DatabaseNode dbNodeTo   = (DatabaseNode) node;
		DatabaseNode dbNodeFrom = cd.getDatabaseForDiff();
//		SqlGenModule sqlMod     = cd.getSqlGenModule();

		if (diffDlg == null)
			diffDlg = new ResultDialog(f);

		diffDlg.setDatabases(dbNodeFrom, dbNodeTo);

		if (diffDlg.diff(false))
		{
			JOptionPane.showMessageDialog(f,
							"The two databases seem to be identical",
							"Diff result", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		diffDlg.showDialog();
	}

	//---------------------------------------------------------------------------

	public int getEnvironment()
	{
		return PROJECT;
	}
}

//==============================================================================
