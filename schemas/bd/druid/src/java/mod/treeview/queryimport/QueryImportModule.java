//==============================================================================
//===
//===   QueryImportModule
//===
//===   Copyright (C) by David Hoag.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.queryimport;

import java.awt.Frame;

import javax.swing.JFileChooser;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.TextFileLoader;

import druid.core.DataTracker;
import druid.data.SqlQuery;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TreeNodeModule;

//==============================================================================

public class QueryImportModule implements TreeNodeModule
{
	public String getId()       { return "queryImp"; }
	public String getAuthor()   { return "David Hoag"; }
	public String getVersion()  { return "1.0"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "This module allows sql to be read from a file. If numerous sql statements "+
				 "are found (separated by a ';') then multiple queries are added";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public String getPopupText()
	{
		return "Import from file";
	}

	//---------------------------------------------------------------------------

	public boolean isNodeAccepted(TreeViewNode node)
	{
		return node.isRoot();
	}

	//---------------------------------------------------------------------------

	public boolean isNodeEnabled(TreeViewNode node)
	{
		return true;
	}

	//---------------------------------------------------------------------------

	public void nodeSelected(Frame f, TreeViewNode node)
	{
		JFileChooser fc = new JFileChooser();

		int res = fc.showDialog( f, "Select File");

		if (res != JFileChooser.APPROVE_OPTION) return;

		//------------------------------------------------------------------------
		//--- proper import

		TextFileLoader tfl = new TextFileLoader(fc.getSelectedFile().getPath());

		if (!tfl.isLoaded())
		{
			System.out.println("Error loading --> " + tfl.getErrorMessage());
			return;
		}

		StringBuffer buffer = new StringBuffer();
		SqlQuery     child;

		for(int i=0; i<tfl.getRows(); i++)
		{
			String line = tfl.getRowAt(i);

			buffer.append( line + "\n");

			if( line.endsWith( ";" ) )
			{
				child = new SqlQuery();

				child.attrSet.setString("sqlCode",buffer.toString());

				node.addChild(child);

				buffer = new StringBuffer();
				DataTracker.setDataChanged();
			}
		}
		if( buffer.length() != 0 )
		{
			child = new SqlQuery();

			child.attrSet.setString("sqlCode",buffer.toString());

			node.addChild(child);
			DataTracker.setDataChanged();
		}
	}

	//---------------------------------------------------------------------------

	public int getEnvironment()
	{
		return JDBC_SQLNAVIG;
	}
}

//==============================================================================
