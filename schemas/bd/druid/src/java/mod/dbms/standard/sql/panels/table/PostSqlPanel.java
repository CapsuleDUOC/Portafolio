//==============================================================================
//===
//===   PostSqlPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.standard.sql.panels.table;

import java.awt.Dimension;

import mod.dbms.standard.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TTextAreaGuardian;

//==============================================================================

public class PostSqlPanel extends TPanel
{
	private static final String TIP = "<HTML><B>NOTE</B><BR>"+
												"Don't use this feature to add statements like <B>INSERT(...)</B>.<BR>"+
												"For such statements use the <B>Sql Commands</B> area in the <B>Extra</B> tab.";

	private TTextAreaGuardian txaPostSql = new TTextAreaGuardian("PostSql");

	//---------------------------------------------------------------------------

	public PostSqlPanel()
	{
		super("Post SQL");

		FlexLayout flexL = new FlexLayout(1, 1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x",  txaPostSql);

		txaPostSql.setPreferredSize(new Dimension(100, 120));
		txaPostSql.setToolTipText(TIP);
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts)
	{
		txaPostSql.refresh(ts);
	}
}

//==============================================================================
