//==============================================================================
//===
//===   GeneratePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql.panels.database;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TCheckBoxGuardian;

//==============================================================================

public class GeneratePanel extends TPanel
{
	private TCheckBoxGuardian chbTables    = new TCheckBoxGuardian("GenTables",     "Tables");
	private TCheckBoxGuardian chbInlineFKs = new TCheckBoxGuardian("GenInlineFKs",  "Inline Foreign Keys");
	private TCheckBoxGuardian chbViews     = new TCheckBoxGuardian("GenViews",      "Views");
	private TCheckBoxGuardian chbProcedures= new TCheckBoxGuardian("GenProcedures", "Procedures");
	private TCheckBoxGuardian chbFunctions = new TCheckBoxGuardian("GenFunctions",  "Functions");
	private TCheckBoxGuardian chbSequences = new TCheckBoxGuardian("GenSequences",  "Sequences");
	private TCheckBoxGuardian chbTriggers  = new TCheckBoxGuardian("GenTriggers",   "Triggers");
	private TCheckBoxGuardian chbRules     = new TCheckBoxGuardian("GenRules",      "Rules");
	private TCheckBoxGuardian chbIndexes   = new TCheckBoxGuardian("GenIndexes",    "Indexes");
	private TCheckBoxGuardian chbComments  = new TCheckBoxGuardian("GenComments",   "Comments");
	private TCheckBoxGuardian chbDrops     = new TCheckBoxGuardian("GenDropStmts",  "DROP statements");
	private TCheckBoxGuardian chbPreSQL    = new TCheckBoxGuardian("GenPreSQL",     "Pre SQL");
	private TCheckBoxGuardian chbPostSQL   = new TCheckBoxGuardian("GenPostSQL",    "Post SQL");

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneratePanel()
	{
		super("Generate");

		FlexLayout flexL = new FlexLayout(2,7);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", chbTables);
		add("0,1,x", chbTriggers);
		add("0,2,x", chbRules);
		add("0,3,x", chbIndexes);
		add("0,4,x", chbComments);
		add("0,5,x", chbPreSQL);
		add("0,6,x", chbPostSQL);

		add("1,0,x", chbViews);
		add("1,1,x", chbProcedures);
		add("1,2,x", chbFunctions);
		add("1,3,x", chbSequences);
		add("1,4,x", chbDrops);
		add("1,5,x", chbInlineFKs);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Object s)
	{
		chbTables    .refresh(s);
		chbInlineFKs .refresh(s);
		chbViews     .refresh(s);
		chbProcedures.refresh(s);
		chbFunctions .refresh(s);
		chbSequences .refresh(s);
		chbTriggers  .refresh(s);
		chbRules     .refresh(s);
		chbIndexes   .refresh(s);
		chbComments  .refresh(s);
		chbDrops     .refresh(s);
		chbPreSQL    .refresh(s);
		chbPostSQL   .refresh(s);
	}
}

//==============================================================================
