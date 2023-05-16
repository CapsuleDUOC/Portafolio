//==============================================================================
//===
//===   SqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import java.io.IOException;
import java.sql.SQLException;

import druid.core.jdbc.ExecutionPlan;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.data.AbstractNode;

//==============================================================================

public interface SqlAdapter extends BasicModule
{
	//--- interface enhancement: allow DB specific connection initialization
	public void initializeConnection(JdbcConnection connection);
	public String getMatchString();

	public void retrieveView(ViewEntity ent);
	public void retrieveProcedure(ProcedureEntity ent);
	public void retrieveFunction(FunctionEntity ent);
	public void retrieveSequence(SequenceEntity ent);
	public void retrieveTriggers(TableEntity ent);
	
	public boolean isExecutionPlanSupported();

	public ExecutionPlan retrieveExecutionPlan(JdbcConnection jdbcConn, String query) throws SQLException, IOException;
	
	//--- constraints support
	public void retrieveConstraints(TableEntity entity);

	public void dropEntity(AbstractNode  node) throws SQLException, IOException;
	public void dropEntity(AbstractEntity ent) throws SQLException, IOException;
}

//==============================================================================
