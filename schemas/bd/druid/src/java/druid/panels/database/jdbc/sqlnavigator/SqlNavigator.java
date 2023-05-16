//==============================================================================
//===
//===   SqlNavigator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.sqlnavigator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import druid.data.DatabaseNode;

//==============================================================================

public class SqlNavigator extends JPanel
{
	private SqlView        sqlView     = new SqlView();
	private SqlWorkPanel   workPanel   = new SqlWorkPanel();
	private SqlResultPanel resultPanel = new SqlResultPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlNavigator()
	{
		sqlView.setDataModel(workPanel);
		sqlView.setResultPanel(resultPanel);

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sqlView, workPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		p.setContinuousLayout(true);

		JSplitPane bp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, p, resultPanel);
		bp.setOneTouchExpandable(true);
		bp.setDividerLocation(200);

		Dimension d = new Dimension(100,50);

		p.setMinimumSize(d);
		sqlView.setMinimumSize(d);
		workPanel.setMinimumSize(d);
		resultPanel.setMinimumSize(d);

		setLayout(new BorderLayout());
		add(bp, BorderLayout.CENTER);

		resultPanel.setQuerySource(workPanel.getQuerySource());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Refresh / Store methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode node)
	{
		sqlView.setRootNode(node.sqlQueries);
		resultPanel.setConnection(node.getJdbcConnection());
	}

	//---------------------------------------------------------------------------

	/** Called when the user presses the 'connect' button */

	public void connected()
	{
		if (sqlView.isANodeSelected())
			resultPanel.enableExecute(true);
	}

	//---------------------------------------------------------------------------

	/** Called when the user presses the 'disconnect' button */

	public void disconnected()
	{
		resultPanel.clear();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		sqlView.saveDataToNode();
	}
}

//==============================================================================
