//==============================================================================
//===
//===   FilePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.choice;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelListener;
import org.dlib.tools.TFileFilter;

import druid.Druid;
import druid.core.io.ProjectManager;
import druid.data.DatabaseNode;
import druid.data.ProjectNode;
import druid.util.gui.Dialogs;

//==============================================================================

class FilePanel extends TPanel implements ActionListener
{
	public  static final String NAME = "file";
	private static final String TEXT = "A database in another project file";

	private TLabel   label    = new TLabel(TEXT);
	private TreeView treeView = new TreeView();
	private TButton  tbOpen   = new TButton("Open", "open", this);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FilePanel(TreeViewSelListener l)
	{
		super("From file");

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup treeview

		treeView.setName(NAME);
		treeView.setEditable(false);
		treeView.setCellRenderer(new ChoiceViewRenderer());
		treeView.addSelectionListener(l);
		treeView.setPreferredSize(new Dimension(200,100));
		treeView.setShowRootHandles(false);

		//--- setup panel

		add("0,0,x,t",     label);
		add("1,0,c,x,1,2", treeView);
		add("0,1,c,t",     tbOpen);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void deselectTreeView()
	{
		treeView.clearSelection();
	}

	//---------------------------------------------------------------------------

	public TreeViewNode getSelectedNode()
	{
		return treeView.getSelectedNode();
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("open"))   handleOpen();
	}

	//---------------------------------------------------------------------------

	private void handleOpen()
	{
		JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Open project file");
		fc.addChoosableFileFilter(new TFileFilter(Druid.FILE_EXT, "Druid project file"));

		int res = fc.showDialog(this, "Open");

		if (res != JFileChooser.APPROVE_OPTION) return;

		//--- open project file and add databases to treeview

		ProjectNode projNode = new ProjectNode();

		String fileName = fc.getSelectedFile().getPath();

		try
		{
			ProjectManager.loadProject(projNode, fileName);

			if (projNode.getChildCount() == 0)
			{
				JOptionPane.showMessageDialog(this,
							"The selected project file is empty",
							"Database Error", JOptionPane.WARNING_MESSAGE);

				return;
			}

			//--- add databases to treeview

			TreeViewNode rootNode = new TreeViewNode();

			for(int i=0; i<projNode.getChildCount(); i++)
			{
				DatabaseNode node = (DatabaseNode) projNode.getChild(i);

				String dbName = node.attrSet.getString("name");

				rootNode.addChild(new TreeViewNode(dbName, node));
			}

			treeView.setRootNode(rootNode);
		}
		catch(FileNotFoundException e)
		{
			Dialogs.showFileNotFound(this, fileName);
		}
		catch(Exception e)
		{
			Dialogs.showOpenError(this, fileName, e.toString());
		}
	}
}

//==============================================================================
