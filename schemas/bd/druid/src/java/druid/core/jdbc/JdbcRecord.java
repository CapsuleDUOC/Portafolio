//==============================================================================
//===
//===   JdbcRecord
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.awt.Component;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.ProgressDialog;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.TFileFilter;

import ddf.type.QueryField;
import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.modules.ModuleManager;
import druid.interfaces.RecordIOModule;
import druid.interfaces.RecordIOModule.ImportListener.ActionType;
import druid.util.gui.Dialogs;

//==============================================================================

public class JdbcRecord
{
	private static JFileChooser jfcImport;
	private static JFileChooser jfcExport;
	private static JFileChooser jfcExpRec;

	//---------------------------------------------------------------------------
	//---
	//--- Export data from a table to a file
	//---
	//---------------------------------------------------------------------------

	public static void exportData(Component c, final JdbcConnection conn, final String query)
	{
		//--- build dialog if not created

		if (jfcExport == null)
		{
			jfcExport = new JFileChooser();

			jfcExport.setDialogTitle("Select a file to store exported data");
			initDialog(jfcExport, true);
		}

		//------------------------------------------------------------------------
		//--- show dialog and request file

		int res = jfcExport.showDialog(GuiUtil.getFrame(c), "Export");

		if (res != JFileChooser.APPROVE_OPTION) return;

		//------------------------------------------------------------------------
		//--- obtain export module

		String fileName = jfcExport.getSelectedFile().getPath();

		final RecordIOModule mod = getModule(jfcExport, true);

		if (mod == null) return;

		if (!fileName.endsWith("." + mod.getExtension()))
			fileName = fileName + "." + mod.getExtension();

		final String file = fileName;
		
		//------------------------------------------------------------------------
		//--- ok, ready to export

		final NumberFormat nf = NumberFormat.getIntegerInstance();
		
		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(c), "Exporting records");
		
		final Vector err = new Vector();

		Runnable run = new Runnable()
		{
			public void run()
			{
				progrDial.reset(1);

				RecordIOModule.ExportListener listener = new RecordIOModule.ExportListener()
				{
					public void exportedRow(List<Object> values, long recordNum) 
					{
						if (recordNum % 1000 == 0)
							progrDial.advance("Exported records :"+ nf.format(recordNum));
					}					
				};
				
				try
				{
					mod.doExport(conn, query, file, listener);
				}
				catch(Exception ex)
				{
					err.add(ex);
					progrDial.stop();
					return;
				}

				progrDial.stop();
			}
		};

		GuiUtil.setWaitCursor(c, true);
		progrDial.run(run);
		GuiUtil.setWaitCursor(c, false);

		if (err.size() != 0)
			Dialogs.showException((Exception) err.get(0));
	}
	
	//---------------------------------------------------------------------------
	//---
	//--- Import data from a file to a table
	//---
	//---------------------------------------------------------------------------

	public static void importData(Component c, final JdbcConnection conn, final String table)
	{
		//--- build dialog if not created

		if (jfcImport == null)
		{
			jfcImport = new JFileChooser();

			jfcImport.setDialogTitle("Select a file to import data from");
			initDialog(jfcImport, false);
		}

		//------------------------------------------------------------------------
		//--- show dialog and request file

		int res = jfcImport.showDialog(GuiUtil.getFrame(c), "Import");

		if (res != JFileChooser.APPROVE_OPTION) return;

		//------------------------------------------------------------------------
		//--- obtain export module

		String fileName = jfcImport.getSelectedFile().getPath();

		final RecordIOModule mod = getModule(jfcImport, false);

		if (mod == null) 
			return;

		if (!fileName.endsWith("." + mod.getExtension()))
			fileName = fileName + "." + mod.getExtension();

		final String file = fileName;
		
		//------------------------------------------------------------------------
		//--- ok, ready to export

		final NumberFormat nf = NumberFormat.getIntegerInstance();

		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(c), "Importing records");
		
		final Vector err = new Vector();

		Runnable run = new Runnable()
		{
			public void run()
			{
				progrDial.reset(1);

				RecordIOModule.ImportListener listener = new RecordIOModule.ImportListener()
				{
					@Override
					public void insertingRow(List<QueryField> fields, List<Object> row, long recordNum)
					{
						if (recordNum % 1000 == 0)
							progrDial.advance("Imported records :"+ nf.format(recordNum));
					}
					
					@Override
					public void updatingRow(List<QueryField> fields, List<Object> values, long recordNum) 
					{
						if (recordNum % 1000 == 0)
							progrDial.advance("Updated records :"+ nf.format(recordNum));
					}

					@Override
					public void deletingRow(List<QueryField> fields, List<Object> values, long recordNum) 
					{
						if (recordNum % 1000 == 0)
							progrDial.advance("Deleted records :"+ nf.format(recordNum));
					}

					@Override
					public ActionType onInsertError(SQLException e, long recordNum, String line)
					{
						int res = Dialogs.showDdfError(progrDial, e, recordNum, line, "insert");
						
						if (res == 0)
							return ActionType.RETRY;
						
						if (res == 1)
							return ActionType.SKIP;
						
						if (res == 2)
							return ActionType.SKIP_ALL;

						return ActionType.ABORT;
					}
				
					@Override
					public ActionType onUpdateError(SQLException e, long recordNum, String line) 
					{
						int res = Dialogs.showDdfError(progrDial, e, recordNum, line, "update");
						
						if (res == 0)
							return ActionType.RETRY;
						
						if (res == 1)
							return ActionType.SKIP;
						
						if (res == 2)
							return ActionType.SKIP_ALL;

						return ActionType.ABORT;
					}

					@Override
					public ActionType onDeleteError(SQLException e, long recordNum, String line) 
					{
						int res = Dialogs.showDdfError(progrDial, e, recordNum, line, "delete");
						
						if (res == 0)
							return ActionType.RETRY;
						
						if (res == 1)
							return ActionType.SKIP;
						
						if (res == 2)
							return ActionType.SKIP_ALL;

						return ActionType.ABORT;
					}
				};
				
				try
				{
					mod.doImport(conn, table, file, listener);
				}
				catch(Exception ex)
				{
					err.add(ex);
				}

				progrDial.stop();
			}
		};

		GuiUtil.setWaitCursor(c, true);
		progrDial.run(run);
		GuiUtil.setWaitCursor(c, false);

		if (err.size() != 0)
			Dialogs.showException((Exception) err.get(0));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table's data export facility
	//---
	//---------------------------------------------------------------------------

	public static void exportRecords(Component c, AbstractEntity node)
	{
		//--- build dialog if not created

		if (jfcExpRec == null)
		{
			jfcExpRec = new JFileChooser();

			jfcExpRec.setDialogTitle("Select a dir to store exported data");
			jfcExpRec.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			initDialog(jfcExpRec, true);
		}

		//------------------------------------------------------------------------
		//--- show dialog and request directory

		int res = jfcExpRec.showDialog(GuiUtil.getFrame(c), "Export");

		if (res != JFileChooser.APPROVE_OPTION) return;

		//------------------------------------------------------------------------
		//--- load children

		if (!JdbcLib.loadChildren(node)) return;

		//------------------------------------------------------------------------
		//--- obtain export module

		final String dirName = jfcExpRec.getSelectedFile().getPath();

		final RecordIOModule mod = getModule(jfcExpRec, true);

		String title = "Exporting records from " + node.getFullName() + "...";

		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(c), title);

		//------------------------------------------------------------------------
		//--- retrieve tables to export

		final Vector v = new Vector();

		Enumeration e = node.preorderEnumeration();
		e.nextElement();

		while(e.hasMoreElements())
		{
			TreeViewNode jen = (TreeViewNode) e.nextElement();

			if (jen instanceof TableEntity) 
				v.add(jen);
		}

		//------------------------------------------------------------------------

		final Vector err = new Vector();

		Runnable run = new Runnable()
		{
			public void run()
			{
				progrDial.reset(v.size());

				for(int i=0; i<v.size(); i++)
				{
					AbstractEntity eNode = (AbstractEntity) v.elementAt(i);

					String query = "SELECT * FROM " + eNode.getFullName();
					String file  = dirName + "/" + eNode.getFullName() + "." + mod.getExtension();

					progrDial.advance(eNode.getFullName());

					try
					{
						mod.doExport(eNode.getJdbcConnection(), query, file, null);
					}
					catch(Exception ex)
					{
						err.add(ex);
					}
				}

				progrDial.stop();
			}
		};

		GuiUtil.setWaitCursor(c, true);
		progrDial.run(run);
		GuiUtil.setWaitCursor(c, false);

		if (err.size() != 0)
			Dialogs.showException((Exception) err.get(0));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static void initDialog(JFileChooser jfc, boolean export)
	{
		jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());

		Enumeration e=ModuleManager.getModules(RecordIOModule.class);

		for(;e.hasMoreElements();)
		{
			RecordIOModule mod = (RecordIOModule) e.nextElement();

			if ((mod.canImport() && !export) || (mod.canExport() && export))
				jfc.addChoosableFileFilter(new TFileFilter(mod.getExtension(), mod.getFormat()));
		}
	}

	//---------------------------------------------------------------------------

	private static RecordIOModule getModule(JFileChooser jfc, boolean export)
	{
		String format = jfc.getFileFilter().getDescription();

		//------------------------------------------------------------------------
		//--- find export module

		RecordIOModule selMod = null;

		Enumeration e=ModuleManager.getModules(RecordIOModule.class);

		for(;e.hasMoreElements();)
		{
			RecordIOModule mod = (RecordIOModule) e.nextElement();

			if (mod.getFormat().equals(format))
				if ((mod.canImport() && !export) || (mod.canExport() && export))
				{
					selMod = mod;
					break;
				}
		}

		if (selMod == null)
			Dialogs.showModuleNotFound();

		return selMod;
	}
}

//==============================================================================
