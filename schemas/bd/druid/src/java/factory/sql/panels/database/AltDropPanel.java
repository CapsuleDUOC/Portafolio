//==============================================================================
//===
//===   AltDropPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;

import druid.util.gui.guardians.SqlTextAreaGuardian;
import druid.util.gui.guardians.TCheckBoxGuardian;

//==============================================================================

public class AltDropPanel extends JPanel
{
	private TCheckBoxGuardian   chbAltDropTable  = new TCheckBoxGuardian  ("AltDropTable", "Use alternate drop table statement");
	private SqlTextAreaGuardian txaDropTableStmt = new SqlTextAreaGuardian("AltDropTableStmt");
	private TCheckBoxGuardian   chbAltDropSequen = new TCheckBoxGuardian  ("AltDropSequence", "Use alternate drop sequence statement");
	private SqlTextAreaGuardian txaDropSequenStmt= new SqlTextAreaGuardian("AltDropSequenceStmt");

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AltDropPanel()
	{
		FlexLayout flexL = new FlexLayout(1,5);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(4, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x",   chbAltDropTable);
		add("0,1,x,x", txaDropTableStmt);
		add("0,3,x",   chbAltDropSequen);
		add("0,4,x,x", txaDropSequenStmt);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Object s)
	{
		chbAltDropTable  .refresh(s);
		txaDropTableStmt .refresh(s);
		chbAltDropSequen .refresh(s);
		txaDropSequenStmt.refresh(s);
	}
}

//==============================================================================
