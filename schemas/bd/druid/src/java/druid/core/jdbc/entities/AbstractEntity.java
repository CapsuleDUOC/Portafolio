//==============================================================================
//===
//===   AbstractEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.entities;

import java.sql.SQLException;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.Util;

import druid.core.jdbc.JdbcConnection;

//==============================================================================

public abstract class AbstractEntity extends TreeViewNode
{
	protected static final String UNKNOWN = "????";

	public String sName;
	public String sRemarks;

	private boolean bInfoLoaded;

	private JdbcConnection jdbcConn;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AbstractEntity(JdbcConnection conn, String name, String type, String rems)
	{
		jdbcConn = conn;

		if (name == null) name = "????";
		if (type == null) type = "OTHER";
		if (rems == null) rems = "";

		sName    = name;
		sRemarks = rems;

		setText(name);
		setToolTipText("This object is a " + type);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setName(String name)
	{
		sName = name;
		setText(name);
	}

	//---------------------------------------------------------------------------

	public boolean isInfoLoaded() { return bInfoLoaded; }

	//---------------------------------------------------------------------------

	public JdbcConnection getJdbcConnection()
	{
		return jdbcConn;
	}

	//---------------------------------------------------------------------------

	public String getFullName()
	{
		String schema = getSchema();

		if (schema == null)	return sName;
			else 					return schema + "." + sName;
	}

	//---------------------------------------------------------------------------

	public String getSchema()
	{
		AbstractEntity node = this;

		while(node != null)
		{
			if (node instanceof SchemaEntity && !(node instanceof DatabaseEntity))
				return node.sName;

			node = (AbstractEntity) node.getParent();
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public void loadInfo() throws SQLException
	{
		if (bInfoLoaded) return;

		loadInfoI();
		bInfoLoaded = true;

		refreshName();
	}

	//---------------------------------------------------------------------------

	public void refreshName()
	{
		if (bInfoLoaded)
		{
			String	s = Util.replaceStr(sName, "<", "&lt;");
						s = Util.replaceStr(s,     ">", "&gt;");

			setText("<HTML><B>" +s+ "</B>");
		}
		else
			setText(sName);

		refresh();
//		select();
	}

	//---------------------------------------------------------------------------

	public void reset()
	{
		bInfoLoaded = false;

		refreshName();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected void loadInfoI() throws SQLException {}
}

//==============================================================================
