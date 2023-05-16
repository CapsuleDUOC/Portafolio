//==============================================================================
//===
//===   SqlCommandsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.extra;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.core.AttribSet;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class SqlCommandsPanel extends TPanel
{
	private SqlTextArea sqlTxa = new SqlTextArea();

	//---------------------------------------------------------------------------

	public SqlCommandsPanel()
	{
		super("Sql commands");

		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", sqlTxa);

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		sqlTxa.getDocument().addDocumentListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribSet as)
	{
		sqlTxa.setText(as.getString("sqlCommands"));
	}

	//---------------------------------------------------------------------------

	public void store(AttribSet as)
	{
		as.setString("sqlCommands", sqlTxa.getText());
	}
}

//==============================================================================
