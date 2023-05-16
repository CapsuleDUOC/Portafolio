//==============================================================================
//===
//===   DefaultSqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.io.IOException;
import java.sql.SQLException;

import druid.core.DruidException;
import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.TriggerEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.data.AbstractNode;
import druid.data.FunctionNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.interfaces.ModuleOptions;
import druid.interfaces.SqlAdapter;
import factory.sql.SqlUtil;

//==============================================================================

public class DefaultSqlAdapter implements SqlAdapter
{
	public String getId()      { return "basSqlAd";       }
	public String getVersion() { return "1.0";            }
	public String getAuthor()  { return "Andrea Carboni"; }

	public String getDescription() { return "Default adapter to use"; }

	public ModuleOptions getModuleOptions(int environment) { return null; }

	//---------------------------------------------------------------------------
	//---
	//--- SqlAdapter interface
	//---
	//---------------------------------------------------------------------------

	public String getMatchString() { return "default"; }

	//---------------------------------------------------------------------------

	public void retrieveView       (ViewEntity      ent) {}
	public void retrieveProcedure  (ProcedureEntity ent) {}
	public void retrieveFunction   (FunctionEntity  ent) {}
	public void retrieveSequence   (SequenceEntity  ent) {}
	public void retrieveTriggers   (TableEntity     ent) {}
	public void retrieveConstraints(TableEntity     ent) {}

	//---------------------------------------------------------------------------

	public boolean isExecutionPlanSupported() { return false; }

	public ExecutionPlan retrieveExecutionPlan(JdbcConnection jdbcConn, String query) throws SQLException, IOException
	{
		return null;
	}

	//---------------------------------------------------------------------------

	public void dropEntity(AbstractNode node) throws SQLException, IOException
	{
		     if (node instanceof TableNode)     dropTable    ((TableNode)     node);
		else if (node instanceof ViewNode)      dropView     ((ViewNode)      node);
		else if (node instanceof ProcedureNode) dropProcedure((ProcedureNode) node);
		else if (node instanceof FunctionNode)  dropFunction ((FunctionNode)  node);
		else if (node instanceof SequenceNode)  dropSequence ((SequenceNode)  node);
		else if (node instanceof Trigger)       dropTrigger  ((Trigger)       node);
		else
			throw new DruidException(DruidException.INC_STR, "Unknown node type", node);
	}

	//---------------------------------------------------------------------------

	public void dropEntity(AbstractEntity ent) throws SQLException, IOException
	{
			  if (ent instanceof TableEntity)     dropTable    ((TableEntity)     ent);
		else if (ent instanceof ViewEntity)      dropView     ((ViewEntity)      ent);
		else if (ent instanceof ProcedureEntity) dropProcedure((ProcedureEntity) ent);
		else if (ent instanceof FunctionEntity)  dropFunction ((FunctionEntity)  ent);
		else if (ent instanceof SequenceEntity)  dropSequence ((SequenceEntity)  ent);
		else if (ent instanceof TriggerEntity)   dropTrigger  ((TriggerEntity)   ent);
		else
			throw new DruidException(DruidException.ILL_ARG, "Unknown entity", ent);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Drop methods
	//---
	//---------------------------------------------------------------------------

	protected void dropTable(TableNode node) throws SQLException, IOException
	{
		String name = node.attrSet.getString("name");

		node.getDatabase().getJdbcConnection().execute("DROP TABLE " + name, null);
	}

	//---------------------------------------------------------------------------

	protected void dropView(ViewNode node) throws SQLException, IOException
	{
		String code  = node.attrSet.getString("sqlCode");
		String query = "DROP VIEW " + SqlUtil.getNameFromCode(code);

		node.getDatabase().getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropProcedure(ProcedureNode node) throws SQLException, IOException
	{
		String code  = node.attrSet.getString("sqlCode");
		String query = "DROP PROCEDURE " + SqlUtil.getNameFromCode(code);

		node.getDatabase().getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropFunction(FunctionNode node) throws SQLException, IOException
	{
		String code  = node.attrSet.getString("sqlCode");
		String query = "DROP FUNCTION " + SqlUtil.getNameFromCode(code);

		node.getDatabase().getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropSequence(SequenceNode node) throws SQLException, IOException
	{
		String name = node.attrSet.getString("name");

		node.getDatabase().getJdbcConnection().execute("DROP SEQUENCE " + name, null);
	}

	//---------------------------------------------------------------------------

	protected void dropTrigger(Trigger node) throws SQLException, IOException
	{
		String name = node.attrSet.getString("name");

		node.getDatabase().getJdbcConnection().execute("DROP TRIGGER " + name, null);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Drop entity methods
	//---
	//---------------------------------------------------------------------------

	protected void dropTable(TableEntity node) throws SQLException, IOException
	{
		String query = "DROP TABLE " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropView(ViewEntity node) throws SQLException, IOException
	{
		String query = "DROP VIEW " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropProcedure(ProcedureEntity node) throws SQLException, IOException
	{
		String query = "DROP PROCEDURE " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropFunction(FunctionEntity node) throws SQLException, IOException
	{
		String query = "DROP FUNCTION " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropSequence(SequenceEntity node) throws SQLException, IOException
	{
		String query = "DROP SEQUENCE " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------

	protected void dropTrigger(TriggerEntity node) throws SQLException, IOException
	{
		String query = "DROP TRIGGER " + node.getFullName();

		node.getJdbcConnection().execute(query, null);
	}

	//---------------------------------------------------------------------------
	//--- interface enhancement: allow DB specific connection initialization

	public void initializeConnection(JdbcConnection connection)
	{
	}
}

//==============================================================================
