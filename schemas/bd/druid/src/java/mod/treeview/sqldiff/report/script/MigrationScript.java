//==============================================================================
//===
//===   MigrationScript
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.report.script;

import mod.treeview.sqldiff.struct.DiffSummary;

import druid.core.config.Config;
import druid.util.decoder.OnClauseDecoder;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;

//==============================================================================

public class MigrationScript
{
	private static final String LF = Config.os.lineSep;

	private StringBuffer sbScript;

	private OnClauseDecoder          onClauseDec = new OnClauseDecoder();
	private TriggerActivationDecoder trigActDec  = new TriggerActivationDecoder();
	private TriggerForEachDecoder    trigForDec  = new TriggerForEachDecoder();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MigrationScript() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String build(DiffSummary diffSumm)
	{
		sbScript = new StringBuffer("<under development>");

		buildDatabase(diffSumm);
		buildTablespaces(diffSumm);
		buildTables(diffSumm);
		buildViews(diffSumm);
		buildProcedures(diffSumm);
		buildFunctions(diffSumm);
		buildSequences(diffSumm);

		return sbScript.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Generation private methods
	//---
	//---------------------------------------------------------------------------

	private void buildDatabase(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildTablespaces(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildTables(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildViews(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildProcedures(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildFunctions(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------

	private void buildSequences(DiffSummary diffSumm)
	{
	}

	//---------------------------------------------------------------------------
	//---
	//--- Generation private methods
	//---
	//---------------------------------------------------------------------------

	/*private void writeSeparator(String text)
	{
		int len = 70 - text.length() -5;

		write("--- " +text+ " " +Util.replicate("-", len));
		write("");
	}*/

	//---------------------------------------------------------------------------

	/*private void writeBigHeader(String name)
	{
		write(Util.replicate("=", 70));
		write("=== ");
		write("=== " + name);
		write("=== ");
		write(Util.replicate("=", 70));
		write("");
	}*/

	//---------------------------------------------------------------------------

	/*private void writeSmallHeader(String name)
	{
		write(Util.replicate("-", 70));
		write(name);
		write(Util.replicate("-", 70));
		write("");
	}*/

	//---------------------------------------------------------------------------

	private void write(String text)
	{
		sbScript.append(text + LF);
	}
}

//==============================================================================
