//==============================================================================
//===
//===   MainFrame
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuBuilder;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.MessageWindow;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.TFileFilter;

import druid.Druid;
import druid.core.AttribSet;
import druid.core.DataTracker;
import druid.core.Serials;
import druid.core.config.Config;
import druid.core.io.ProjectManager;
import druid.data.ProjectNode;
import druid.dialogs.about.AboutDialog;
import druid.dialogs.config.DruidConfigDialog;
import druid.dialogs.jdbc.drivers.DriverDialog;
import druid.dialogs.modules.ModuleDialog;
import druid.dialogs.tipoftheday.TipOfTheDay;
import druid.util.gui.Dialogs;
import druid.util.gui.StatusBar;

//==============================================================================

public class MainFrame extends JFrame implements ActionListener
{
	private ProjectView  projView  = new ProjectView();
	private WorkPanel    workPanel = new WorkPanel();

	private String projFullname;
	private String projFilename;

	private MessageWindow msgWnd = new MessageWindow(this);

	private JMenu prjRecFiles;

	//---------------------------------------------------------------------------
	//--- dialogs

	private DriverDialog      dlgJdbcDrivers;
	private DruidConfigDialog dlgOptions;
	private ModuleDialog      dlgModules;

	//---------------------------------------------------------------------------

	private class InnerWindowAdapter extends WindowAdapter
	{
		private boolean isClosing = false;

		public void windowClosing(WindowEvent e)
		{
			if (isClosing) return;

			isClosing = true;
			proj_exit();
			isClosing = false;
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MainFrame()
	{
		super("");

		addWindowListener(new InnerWindowAdapter());

		projView.setDataModel(workPanel);

		//------------------------------------------------------------------------
		//--- setup splitpane

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projView, workPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		sp.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		projView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		//------------------------------------------------------------------------
		//--- setup main panel

		StatusBar sb = StatusBar.getInstance();

		getContentPane().add(sp, BorderLayout.CENTER);
		getContentPane().add(sb, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//------------------------------------------------------------------------
		//--- create druid's menu and setup recent files list items

		MenuBuilder menuBuilder = new MenuBuilder();

		menuBuilder.setActionListener(this);
		menuBuilder.setXmlFile(Config.dir.data +"/menu/main.xml");

		prjRecFiles = menuBuilder.getMenu("prjRecFiles");

		setJMenuBar(menuBuilder.getMenuBar());

		rebuildRecentFilesMenu();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Event Handling
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		Object source= e.getSource();
		String cmd   = e.getActionCommand();

		//--- is a message from the DataTracker ?

		if (cmd.equals(DataTracker.NAME))
		{
			updateTitle();
			return;
		}

		//--- save data stored in the current panel inside its node

		projView.saveDataToNode();

		//--- handle menu events ---

		if (cmd.equals("prjNew"))       proj_new();
		if (cmd.equals("prjOpen"))      proj_open();
		if (cmd.equals("prjSaveAs"))    proj_saveas();
		if (cmd.equals("prjSave"))      proj_save();
		if (cmd.equals("prjExit"))      proj_exit();

		if (cmd.equals("cfgJdbc"))      config_jdbc();
		if (cmd.equals("cfgOptions"))   config_options();
		if (cmd.equals("cfgModules"))   config_modules();

		if (cmd.equals("hlpAbout"))     help_about();
		if (cmd.equals("hlpDocs"))      help_docs();
		if (cmd.equals("hlpTip"))       help_tip();

		//--- handle recent files list ---

		if (cmd.equals("prjLoadRecent"))
			proj_loadrecent(((JMenuItem)source).getText());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Menu Events
	//---
	//---------------------------------------------------------------------------

	public void proj_new()
	{
		//--- if data is changed shows a dialog,
		//--- prompts the user and (if case) exits

		if (dataNeedSave()) return;

		//------------------------------------------------------------------------

		projFilename = "unnamed" + "." + Druid.FILE_EXT;
		projFullname = "/" + projFilename;

		projView.setProject(new ProjectNode());

		Serials.set(0);
		DataTracker.reset();
	}

	//---------------------------------------------------------------------------

	public void proj_open()
	{
		//--- if data is changed shows a dialog,
		//--- prompts the user and (if case) exits

		if (dataNeedSave()) return;

		//------------------------------------------------------------------------

		JFileChooser fc = new JFileChooser(projFullname);

		fc.setDialogTitle("Open an existing Project");
		fc.addChoosableFileFilter(new TFileFilter(Druid.FILE_EXT, "Druid project file"));

		int res = fc.showDialog(this, "Open");

		if (res == JFileChooser.APPROVE_OPTION)
			do_open(fc.getSelectedFile());
	}

	//---------------------------------------------------------------------------

	public void proj_loadrecent(String file)
	{
		//--- if data is changed shows a dialog,
		//--- prompts the user and (if case) exits

		if (dataNeedSave()) return;

		do_open(new File(file));
	}

	//---------------------------------------------------------------------------

	public void do_open(File f)
	{
		projFilename = f.getName();
		projFullname = f.getPath();

		//--- changing data into an attribset will fire "changed" events that
		//--- change the window title. To prevent this, during loading we
		//--- disable changes

		DataTracker.setEnabled(false);

		GuiUtil.setWaitCursor(this, true);
		msgWnd.showMessage("Loading");
		setEnabled(false);

		//------------------------------------------------------------------------

		Runnable run = new Runnable()
		{
			public void run()
			{
				final ProjectNode projNode = new ProjectNode();
				final Exception   loadExc[]= new Exception[1];

				try
				{
					ProjectManager.loadProject(projNode, projFullname);
				}
				catch(Exception e)
				{
					loadExc[0] = e;
				}

				//------------------------------------------------------------------

				Runnable run = new Runnable()
				{
					public void run()
					{
						msgWnd.hide();
						GuiUtil.setWaitCursor(MainFrame.this, false);
						setEnabled(true);

						Exception e = loadExc[0];

						if (e instanceof FileNotFoundException)
							Dialogs.showFileNotFound(MainFrame.this, projFullname);

						else if (e != null)
						{
							Dialogs.showOpenError(MainFrame.this, projFullname, e.toString());
							e.printStackTrace();
						}

						else
						{
							projView.setProject(projNode);

							Serials.set(projNode.attrSet.getInt("serial"));

							if (projNode.getChildCount() == 0)
								workPanel.setCurrentNode(null);
							else
							{
								TreeViewNode dbNode = projNode.getChild(0);
								workPanel.setCurrentNode(dbNode);
								dbNode.select();
							}

							//---------------------------------------------------------
							//--- update recent file list

							Config.recentFiles.addFile(projFullname);
							rebuildRecentFilesMenu();
						}

						DataTracker.setEnabled(true);
						DataTracker.reset();
					}
				};

				SwingUtilities.invokeLater(run);
			}
		};

		new Thread(run).start();
	}

	//---------------------------------------------------------------------------

	public void proj_saveas()
	{
		JFileChooser fc = new JFileChooser(projFullname);

		fc.setDialogTitle("Save the current Project");
		fc.addChoosableFileFilter(new TFileFilter(Druid.FILE_EXT, "Druid project file"));

		int res = fc.showDialog(this, "Save");
		if (res == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();

			projFilename = f.getName();
			projFullname = f.getPath();

			if (!projFilename.endsWith("." + Druid.FILE_EXT))
			{
				projFilename = projFilename + "." + Druid.FILE_EXT;
				projFullname = projFullname + "." + Druid.FILE_EXT;
			}

			proj_save();

			//---------------------------------------------------------------------
			//--- update recent file list

			Config.recentFiles.addFile(projFullname);
			rebuildRecentFilesMenu();
		}
	}

	//---------------------------------------------------------------------------

	public void proj_save()
	{
		//--- check if we must create a backup

		if (Config.general.createBackup)
		{
			File src = new File(projFullname);
			File dst = new File(projFullname + ".bak");

			dst.delete();

			src.renameTo(dst);
		}

		//------------------------------------------------------------------------
		//--- incrementing the serial will fire the "changed" event
		//--- to prevent this we must disable changes

		DataTracker.setEnabled(false);

		final ProjectNode projNode = projView.getProjectNode();

		AttribSet as = projNode.attrSet;

		//--- inc build & update serial

		int build = as.getInt("build");
		as.setInt("build", build +1);

		as.setInt("serial", Serials.lastSerial);

		GuiUtil.setWaitCursor(this, true);
		msgWnd.showMessage("Saving");
		setEnabled(false);

		//------------------------------------------------------------------------

		Runnable run = new Runnable()
		{
			public void run()
			{
				final Exception saveExc[]= new Exception[1];

				try
				{
					ProjectManager.saveProject(projNode, projFullname);
				}
				catch(IOException e)
				{
					saveExc[0] = e;
				}

				//------------------------------------------------------------------

				Runnable run = new Runnable()
				{
					public void run()
					{
						setEnabled(true);
						msgWnd.hide();
						GuiUtil.setWaitCursor(MainFrame.this, false);

						DataTracker.setEnabled(true);
						DataTracker.reset();

						if (saveExc[0] != null)
							Dialogs.showSaveError(MainFrame.this, projFullname, saveExc[0].toString());
					}
				};

				SwingUtilities.invokeLater(run);
			}
		};

		new Thread(run).start();
	}

	//---------------------------------------------------------------------------

	public void proj_exit()
	{
		//--- if data is changed shows a dialog,
		//--- prompts the user and (if case) exits

		if (dataNeedSave()) return;

		Config.general.window.width  = getWidth();
		Config.general.window.height = getHeight();

		Config.save();

		System.exit(0);
	}

	//---------------------------------------------------------------------------
	//--- CONFIG
	//---------------------------------------------------------------------------

	public void config_jdbc()
	{
		if (dlgJdbcDrivers == null)
			dlgJdbcDrivers = new DriverDialog(this);

		dlgJdbcDrivers.showDialog();
	}

	//---------------------------------------------------------------------------

	public void config_options()
	{
		if (dlgOptions == null)
			dlgOptions = new DruidConfigDialog(this);

		dlgOptions.run();
		GuiUtil.getFrame(this).repaint();
	}

	//---------------------------------------------------------------------------

	public void config_modules()
	{
		if (dlgModules == null)
		{
			dlgModules = new ModuleDialog(this);

			dlgModules.showDialog();
		}
		else
			dlgModules.setVisible(true);
	}

	//---------------------------------------------------------------------------
	//--- HELP
	//---------------------------------------------------------------------------

	public void help_about()
	{
		new AboutDialog(this);
	}

	//---------------------------------------------------------------------------

	public void help_docs()
	{
		JOptionPane.showMessageDialog(this,
							"See into the 'docs/manuals' directory for the manuals",
							"Docs", JOptionPane.INFORMATION_MESSAGE);
	}

	//---------------------------------------------------------------------------

	public void help_tip()
	{
		new TipOfTheDay(this);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void updateTitle()
	{
		int    build = projView.getProjectNode().attrSet.getInt("build");
		String title = Druid.TITLE + " '" + projFilename + "' build:" + build;

		if (DataTracker.isDataChanged())
			title += " [changed]";

		setTitle(title);
	}

	//---------------------------------------------------------------------------

	private boolean dataNeedSave()
	{
		if (!DataTracker.isDataChanged()) return false;

		return (Dialogs.showDataNeedSave(this) != 0);
	}

	//---------------------------------------------------------------------------

	private void rebuildRecentFilesMenu()
	{
		prjRecFiles.removeAll();

		for(int i=0; i<Config.recentFiles.getFileCount(); i++)
		{
			String file = Config.recentFiles.getFileAt(i);

			prjRecFiles.add(MenuFactory.createItem("prjLoadRecent", file, this));
		}
	}
}

//==============================================================================
