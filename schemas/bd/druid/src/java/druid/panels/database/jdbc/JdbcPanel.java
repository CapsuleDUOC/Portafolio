//==============================================================================
//===
//===   JdbcPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dlib.gui.TTabbedPane;

import druid.data.DatabaseNode;
import druid.panels.database.jdbc.connection.ConnectionPanel;
import druid.panels.database.jdbc.sqlnavigator.SqlNavigator;
import druid.panels.database.jdbc.structure.StructurePanel;

//==============================================================================

public class JdbcPanel extends TTabbedPane implements ActionListener
{
	private ConnectionPanel connPanel   = new ConnectionPanel(this);
	private StructurePanel  structPanel = new StructurePanel();
	private SqlNavigator    sqlNavig    = new SqlNavigator();

	private DatabaseNode dbaseNode;

	//---------------------------------------------------------------------------

	public JdbcPanel()
	{
		addTab("Connection",    connPanel);
		addTab("Structure",     structPanel);
		addTab("Sql Navigator", sqlNavig);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode dbNode)
	{
		dbaseNode = dbNode;

		connPanel.refresh(dbNode);
		sqlNavig.refresh(dbNode);

		boolean connected = dbNode.getJdbcConnection().isConnected();

		setEnabledAt(1, connected);

		if (!connected)
		{
			if (getSelectedIndex()==1)
				setSelectedIndex(0);
		}
		else
		{
			structPanel.refresh(dbNode, false);
		}
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(DatabaseNode dbNode)
	{
		connPanel.saveDataToNode(dbNode);
		sqlNavig.saveDataToNode();
	}

	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		//------------------------------------------------------------------------

		if (cmd.equals("connected"))
		{
			setEnabledAt(1, true);

			structPanel.refresh(dbaseNode, true);

			sqlNavig.connected();
		}

		//------------------------------------------------------------------------

		if (cmd.equals("disconnected"))
		{
			setEnabledAt(1, false);
			sqlNavig.disconnected();
		}
	}
}

//==============================================================================
