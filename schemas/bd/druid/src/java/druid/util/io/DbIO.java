//==============================================================================
//===
//===   DbIO
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.io;

import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JFileChooser;

import org.dlib.tools.TFileFilter;

import druid.core.modules.ModuleManager;
import druid.interfaces.DatabaseIOModule;

//==============================================================================

public class DbIO
{
	private static JFileChooser jfcImport;
	private static JFileChooser jfcExport;

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static DbIOInfo showImportDialog(Component c)
	{
		if (jfcImport == null)
			setupDialogs();

		int res = jfcImport.showDialog(c, "Import");

		if (res == JFileChooser.APPROVE_OPTION)
		{
			String fileName = jfcImport.getSelectedFile().getPath();
			String format   = jfcImport.getFileFilter().getDescription();

			DatabaseIOModule dbMod = getModuleByFormat(format);

			String ext = "." + dbMod.getExtension();

			if (!fileName.endsWith(ext))
				fileName += ext;

			return new DbIOInfo(fileName, dbMod);
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public static DbIOInfo showExportDialog(Component c)
	{
		if (jfcExport == null)
			setupDialogs();

		int res = jfcExport.showDialog(c, "Export");

		if (res == JFileChooser.APPROVE_OPTION)
		{
			String fileName = jfcExport.getSelectedFile().getPath();
			String format   = jfcExport.getFileFilter().getDescription();

			DatabaseIOModule dbMod = getModuleByFormat(format);

			String ext = "." + dbMod.getExtension();

			if (!fileName.endsWith(ext))
				fileName += ext;

			return new DbIOInfo(fileName, dbMod);
		}

		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static void setupDialogs()
	{
		jfcImport = new JFileChooser();
		jfcExport = new JFileChooser();

		jfcImport.setDialogTitle("Database Import");
		jfcImport.setAcceptAllFileFilterUsed(false);

		jfcExport.setDialogTitle("Database Export");
		jfcExport.setAcceptAllFileFilterUsed(false);

		Enumeration e=ModuleManager.getModules(DatabaseIOModule.class);

		while (e.hasMoreElements())
		{
			DatabaseIOModule dbMod = (DatabaseIOModule) e.nextElement();

			if (dbMod.canImport())
				jfcImport.addChoosableFileFilter(new TFileFilter(dbMod.getExtension(), dbMod.getFormat()));

			if (dbMod.canExport())
				jfcExport.addChoosableFileFilter(new TFileFilter(dbMod.getExtension(), dbMod.getFormat()));
		}
	}

	//---------------------------------------------------------------------------

	private static DatabaseIOModule getModuleByFormat(String format)
	{
		Enumeration e=ModuleManager.getModules(DatabaseIOModule.class);

		while (e.hasMoreElements())
		{
			DatabaseIOModule dbMod = (DatabaseIOModule) e.nextElement();

			if (dbMod.getFormat().equals(format))
				return dbMod;
		}

		return null;
	}
}

//==============================================================================
