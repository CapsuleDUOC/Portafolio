//==============================================================================
//===
//===   DbExporterModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.iolinks;

import java.awt.Frame;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TreeNodeModule;
import druid.util.gui.Dialogs;
import druid.util.io.DbIO;
import druid.util.io.DbIOInfo;

//==============================================================================

public class DbExporterModule implements TreeNodeModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "dbExporter";     }
	public String getAuthor()   { return "Andrea Carboni"; }
	public String getVersion()  { return "1.0";            }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "This module allows users to export a database into different formats. "+
				 "It is only the glue between the popup menuitem and all DatabaseIOModules.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env) { return null; }

	//---------------------------------------------------------------------------
	//---
	//--- TreeNodeModule interface
	//---
	//---------------------------------------------------------------------------

	public String getPopupText()
	{
		return "Export...";
	}

	//---------------------------------------------------------------------------

	public boolean isNodeAccepted(TreeViewNode node)
	{
		return (node instanceof DatabaseNode);
	}

	//---------------------------------------------------------------------------

	public boolean isNodeEnabled(TreeViewNode node) { return true; }

	//---------------------------------------------------------------------------

	public void nodeSelected(Frame f, TreeViewNode node)
	{
		DbIOInfo info = DbIO.showExportDialog(f);

		if (info != null)
		{
			GuiUtil.setWaitCursor(f, true);

			try
			{
				info.dbMod.doExport((DatabaseNode) node, info.fileName);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Dialogs.showSaveError(f, info.fileName, e.getMessage());
			}

			GuiUtil.setWaitCursor(f, false);
		}
	}

	//---------------------------------------------------------------------------

	public int getEnvironment()
	{
		return PROJECT;
	}
}

//==============================================================================
