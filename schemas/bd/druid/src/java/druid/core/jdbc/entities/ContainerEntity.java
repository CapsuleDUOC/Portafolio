//==============================================================================
//===
//===   ContainerEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.SQLException;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.ExpandVetoException;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.Util;

import druid.core.jdbc.JdbcConnection;
import druid.util.gui.Dialogs;

//==============================================================================

public abstract class ContainerEntity extends AbstractEntity
{
	private boolean bStructLoaded;

	//---------------------------------------------------------------------------
	//---
	//---   Constructor
	//---
	//---------------------------------------------------------------------------

	public ContainerEntity(JdbcConnection conn, String name, String type, String rems)
	{
		super(conn, name, type, rems);

		add(new TreeViewNode());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void loadStruct() throws SQLException
	{
		if (bStructLoaded) return;

		try
		{
			removeAllChildren();
			loadStructI();
			recalcChildren();

			bStructLoaded = true;
		}
		catch(SQLException e)
		{
			removeAllChildren();
			add(new TreeViewNode());
			recalcChildren();

			throw e;
		}
	}

	//---------------------------------------------------------------------------

	public void reset()
	{
		removeAllChildren();
		add(new TreeViewNode());
		recalcChildren();
		expand(false);

		bStructLoaded = false;

		super.reset();
	}

	//---------------------------------------------------------------------------

	public void refreshName()
	{
		if (bStructLoaded)
		{
			String name = sName +" ("+ getChildCount() +")";

			if (isInfoLoaded())
			{
				String	s = Util.replaceStr(name, "<", "&lt;");
							s = Util.replaceStr(s,    ">", "&gt;");

				setText("<HTML><B>" +s+ "</B>");
			}
			else
				setText(name);

			refresh();
		}

		else
			super.refreshName();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Node expansion methods
	//---
	//---------------------------------------------------------------------------

	public void nodeWillExpand(TreeExpansionEvent e) throws ExpandVetoException
	{
		if (bStructLoaded) return;

		GuiUtil.setWaitCursor(getTree(),true);

		try
		{
			loadStruct();
			refreshName();
		}
		catch(SQLException ex)
		{
			Dialogs.showException(ex);

			throw new ExpandVetoException(e);
		}
		finally
		{
			GuiUtil.setWaitCursor(getTree(),false);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   Struct retrieval methods
	//---
	//---------------------------------------------------------------------------

	protected void loadStructI() throws SQLException {}
}

//==============================================================================
