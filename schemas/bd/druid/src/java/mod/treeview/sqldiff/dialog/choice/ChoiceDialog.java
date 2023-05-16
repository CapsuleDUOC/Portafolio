//==============================================================================
//===
//===   ChoiceDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.choice;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TButton;
import org.dlib.gui.TDialog;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.data.DatabaseNode;
import druid.interfaces.SqlGenModule;

//==============================================================================

public class ChoiceDialog extends TDialog implements ActionListener, TreeViewSelListener
{
	private LocalPanel     panLocal  = new LocalPanel(this);
	private FilePanel      panFile   = new FilePanel(this);
	private SqlModulePanel panSqlMod = new SqlModulePanel();
	private TButton        tbOk      = new TButton("Ok", "ok", this);

	private TreeViewNode dbNodeLocal = null;
	private TreeViewNode dbNodeFile  = null;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ChoiceDialog(Frame frame, DatabaseNode dbNode)
	{
		super(frame, "Select source", true);

		FlexLayout flexL = new FlexLayout(1,4,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);

		JPanel p = new JPanel();

		p.setLayout(flexL);
		p.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

		p.add("0,0,x,x", panLocal);
		p.add("0,1,x,x", panFile);
//		p.add("0,2,x",   panSqlMod);
		p.add("0,3,c",   tbOk);

		getContentPane().add(p, BorderLayout.CENTER);

		panLocal.setDatabase(dbNode);
		tbOk.setEnabled(false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public DatabaseNode getDatabaseForDiff()
	{
		if (dbNodeLocal != null)
			return (DatabaseNode) dbNodeLocal.getUserData();

		if (dbNodeFile != null)
			return (DatabaseNode) dbNodeFile.getUserData();

		return null;
	}

	//---------------------------------------------------------------------------

	public SqlGenModule getSqlGenModule()
	{
		return panSqlMod.getSelectedModule();
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		String       sourceName = ((TreeView) e.getSource()).getName();
		TreeViewNode selNode    = e.getSelectedNode();

		if (sourceName.equals(LocalPanel.NAME))
		{
			dbNodeLocal = selNode;

			if (selNode != null)
				panFile.deselectTreeView();
		}

		else
		{
			dbNodeFile = selNode;

			if (selNode != null)
				panLocal.deselectTreeView();
		}

		boolean enableOk = (dbNodeLocal != null) || (dbNodeFile != null);

		tbOk.setEnabled(enableOk);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("ok"))   setVisible(false);
	}

	//---------------------------------------------------------------------------
}

//==============================================================================
