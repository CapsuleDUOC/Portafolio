//==============================================================================
//===
//===   DbImporterModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.iolinks;

import java.awt.Frame;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataTracker;
import druid.core.Serials;
import druid.data.ProjectNode;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TreeNodeModule;
import druid.util.gui.Dialogs;
import druid.util.io.DbIO;
import druid.util.io.DbIOInfo;

//==============================================================================

public class DbImporterModule implements TreeNodeModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "dbImporter";     }
	public String getAuthor()   { return "Andrea Carboni"; }
	public String getVersion()  { return "1.0";            }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "This module allows users to import a database from different formats. "+
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
		return "Import...";
	}

	//---------------------------------------------------------------------------

	public boolean isNodeAccepted(TreeViewNode node)
	{
		return (node instanceof ProjectNode);
	}

	//---------------------------------------------------------------------------

	public boolean isNodeEnabled(TreeViewNode node) { return true; }

	//---------------------------------------------------------------------------

	public void nodeSelected(Frame f, TreeViewNode node)
	{
		DbIOInfo info = DbIO.showImportDialog(f);

		if (info != null)
		{
			GuiUtil.setWaitCursor(f, true);

			ProjectNode curProjNode = (ProjectNode) node;
			ProjectNode newProjNode = new ProjectNode();

			try
			{
				info.dbMod.doImport(newProjNode, info.fileName);

				for(int i=0; i<newProjNode.getChildCount(); i++)
					curProjNode.addChild(newProjNode.getChild(i), false);

				int newSerial = newProjNode.attrSet.getInt("serial");

				if (newSerial > Serials.lastSerial)
					Serials.set(newSerial);

				DataTracker.setDataChanged();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Dialogs.showOpenError(f, info.fileName, e.getMessage());
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
