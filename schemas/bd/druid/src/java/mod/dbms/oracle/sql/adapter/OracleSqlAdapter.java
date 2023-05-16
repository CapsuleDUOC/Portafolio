//==============================================================================
//===
//===   OracleSqlAdapter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql.adapter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.Util;

import druid.core.AttribSet;
import druid.core.jdbc.DefaultSqlAdapter;
import druid.core.jdbc.ExecutionPlan;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.RecordList;
import druid.core.jdbc.entities.ConstraintEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.TriggerEntity;
import druid.core.jdbc.entities.ViewEntity;
import druid.data.Constraint;
import druid.data.Trigger;

//==============================================================================

public class OracleSqlAdapter extends DefaultSqlAdapter
{
	public String getId()      { return "oracleSqlAd";    }
	public String getVersion() { return "1.1";            }
	public String getAuthor()  { return "Andrea Carboni"; }

	public String getDescription() { return "Adapter for the Oracle DBMS"; }

	private static final String[] HEADER = 
	{ 
		"Operation", "Name", "Rows", "Bytes", "Cost (%CPU)", "Time" 
	};
	
	//---------------------------------------------------------------------------
	//---
	//--- SqlAdapter interface
	//---
	//---------------------------------------------------------------------------

	public String getMatchString() { return "oracle"; }

	//---------------------------------------------------------------------------
	//---
	//--- Overridable methods
	//---
	//---------------------------------------------------------------------------

	public void retrieveView(ViewEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		//--- bug fixes in ORACLE module: add owner field in selects
		String query = "SELECT text FROM SYS.ALL_VIEWS " +
							"WHERE view_name='"+ent.sName+"' " +
							"AND owner='" + ent.getSchema() + "'";

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			
			rs.next();
			ent.sqlCode = "CREATE VIEW "+ent.sName+" AS\n"+rs.getString(1);
			
			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			ent.sqlCode = "?? raised exception during retrieval ??\n\n"+e;
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	public void retrieveSequence(SequenceEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		try
		{
			//--- bug fixes in ORACLE module: add owner field in selects
			String query = "SELECT * FROM SYS.ALL_SEQUENCES " +
								"WHERE sequence_name='"+ent.sName+"' " +
								"AND sequence_owner = '" + ent.getSchema() + "'";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			
			rs.next();

			ent.increment = ""+ rs.getInt("increment_by");
			ent.minValue  = ""+ rs.getInt("min_value");
			ent.maxValue  = ""+ rs.getBigDecimal("max_value");
			ent.cache     = ""+ rs.getBigDecimal("cache_size");
			ent.start     = ""+ rs.getBigDecimal("last_number");

			ent.cycle = "y".equals(rs.getString("cycle_flag").toLowerCase());
			ent.order = "y".equals(rs.getString("order_flag").toLowerCase());

			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			ent.increment = "?? raised exception during retrieval ??";
			ent.minValue  = e.toString();

			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	public void retrieveProcedure(ProcedureEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		//--- bug fixes in ORACLE module: add owner field in selects
		String query = "SELECT text FROM SYS.ALL_SOURCE "+
							"WHERE type='PROCEDURE' AND name='"+ent.sName+"' " +
							"AND owner='" + ent.getSchema() + "' " +
							"ORDER BY line";

		ent.sqlCode = "CREATE ";

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			

			while (rs.next())
				ent.sqlCode += rs.getString(1);

			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			ent.sqlCode = "?? raised exception during retrieval ??\n\n"+e;
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	public void retrieveFunction(FunctionEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		//--- bug fixes in ORACLE module: add owner field in selects
		String query = "SELECT text FROM SYS.ALL_SOURCE "+
							"WHERE type='FUNCTION' AND name='"+ent.sName+ "' " +
							"AND owner='" + ent.getSchema() + "' " +
							"ORDER BY line";

		ent.sqlCode = "CREATE ";

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			

			while (rs.next())
				ent.sqlCode += rs.getString(1);

			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			ent.sqlCode = "?? raised exception during retrieval ??\n\n"+e;
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------

	public void retrieveTriggers(TableEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		String query = "SELECT trigger_name, trigger_type, triggering_event, "+
							"       when_clause, trigger_body "+
							"FROM   SYS.ALL_TRIGGERS "+
							"WHERE  table_name='"+ent.sName+ "' AND table_owner='"+ent.getSchema()+"'";

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			

			while (rs.next())
			{
				Trigger t = new Trigger();

				String name  = Util.getStringValue(rs.getString(1), "");
				String type  = Util.getStringValue(rs.getString(2), "");
				String event = Util.getStringValue(rs.getString(3), "");
				String when  = Util.getStringValue(rs.getString(4), "");
				String body  = Util.getStringValue(rs.getString(5), "");

				String activ = Trigger.ACTIV_INSTEADOF;

				if (type.indexOf("BEFORE") != -1) activ = Trigger.ACTIV_BEFORE;
				if (type.indexOf("AFTER")  != -1) activ = Trigger.ACTIV_AFTER;

				String forEach = Trigger.FOREACH_STATEMENT;

				if (type.indexOf("ROW") != -1)
					forEach = Trigger.FOREACH_ROW ;

				AttribSet as = t.attrSet;

				as.setString("name",       name);
				as.setString("activation", activ);
				as.setString("forEach",    forEach);
				as.setString("when",       when);
				as.setString("code",       body);

				as.setBool("onInsert", event.indexOf("INSERT") != -1);
				as.setBool("onUpdate", event.indexOf("UPDATE") != -1);
				as.setBool("onDelete", event.indexOf("DELETE") != -1);

				t.setText(name);

				ent.triggers.addChild(new TriggerEntity(ent.getJdbcConnection(), name, t));
			}

			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------
	//--- constraints support

	public void retrieveConstraints(TableEntity ent)
	{
		Connection conn = ent.getJdbcConnection().getConnection();

		String query = "SELECT c.constraint_name, c.constraint_type, "
						+ "c.search_condition, c.r_owner, c.r_constraint_name, "
						+ "c.delete_rule, c.status, cc.column_name, cc.position"
						+ " FROM   SYS.ALL_CONSTRAINTS c , SYS.ALL_CONS_COLUMNS cc"
						+ " WHERE  c.table_name='"+ent.sName+ "' AND c.owner='"+ent.getSchema()+"'"
						+ " AND cc.table_name=c.table_name AND cc.owner=c.owner AND "
						+ "cc.constraint_name=c.constraint_name "
						+ "ORDER BY c.constraint_name, cc.position";

		try
		{
			Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(query);			

			while (rs.next())
			{
				Constraint c = new Constraint();

				String name         = Util.getStringValue(rs.getString(1), "");
				String type         = Util.getStringValue(rs.getString(2), "");
				String conditon     = Util.getStringValue(rs.getString(3), "");
				String r_owner      = Util.getStringValue(rs.getString(4), "");
				String r_constr_name= Util.getStringValue(rs.getString(5), "");
//				String delete_rule  = Util.getStringValue(rs.getString(6), "");
				String status       = Util.getStringValue(rs.getString(7), "");
				String column_name  = Util.getStringValue(rs.getString(8), "");

				StringBuffer code = new StringBuffer();

				if (type.equals("C"))
				{
					code.append("Check ");

					if (conditon.indexOf(column_name) < 0)
						code.append(column_name);

					//--- not nulls are already shown in the structure view
					if (conditon.endsWith("IS NOT NULL"))
						continue;

					code.append(" ").append(conditon);
				}
				else if (type.equals("P"))
				{
					code.append("Primary Key (");
					code.append(column_name);

					while(rs.next())
					{
						String nextName = Util.getStringValue(rs.getString(1), "");

						if (nextName.equals(name))
						{
							code.append(", ")
							.append(Util.getStringValue(rs.getString(8), ""));
						}
						else
						{
							break;
						}
					}

					code.append(")");
					rs.previous();

					//--- primary keys are already shown in the structure view
					continue;
				}
				else if (type.equals("R"))
				{
					code  .append("r_owner: ")
							.append(r_owner)
							.append("\nr_constraint: ")
							.append(r_constr_name);

					//--- foreign keys are already shown in the references view
					continue;
				}
				else
				{
					code.append("unknown type: ").append(type);
				}

				AttribSet as = c.attrSet;

				as.setString("name",   name);
				as.setString("code",   code.toString());
				as.setString("status", status);

				c.setText(name);

				ent.constraints.addChild(new ConstraintEntity(ent.getJdbcConnection(), name, c));
			}

			rs.close();
			st.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------
	//--- interface enhancement: allow DB specific connection initialization

	public void initializeConnection(JdbcConnection connection)
	{
		try
		{
			Connection oracleConnection = connection.getConnection();

			Method method = oracleConnection.getClass().getDeclaredMethod("setRemarksReporting",
																							  new Class[]{boolean.class});

			method.invoke(oracleConnection, new Object[]{Boolean.TRUE});
		}
		catch (Exception e) {}
	}
	
	//---------------------------------------------------------------------------
	//--- Execution plan
	//---------------------------------------------------------------------------

	public boolean isExecutionPlanSupported() { return true; }

	//---------------------------------------------------------------------------

	public ExecutionPlan retrieveExecutionPlan(JdbcConnection jdbcConn, String query) throws SQLException, IOException
	{
		jdbcConn.execute("explain plan for "+ query, null);
		
		ResultSet  rs = jdbcConn.select("select plan_table_output from table(dbms_xplan.display())", null);
		RecordList rl = jdbcConn.retrieveResultSet(rs);

		//---build text part

		StringBuilder sb = new StringBuilder();

		for (int i=0; i<rl.getRowCount(); i++)
		{
			String line = rl.getValueAt(i, 0).toString();
			sb.append(line +"\n");
		}

		//--- collect information to build graphic part

		int dashes = 0;
		int currId = 0;

		List<PlanInfo> plan = new ArrayList<PlanInfo>();

		for (int i=0; i<rl.getRowCount(); i++)
		{
			String line = rl.getValueAt(i, 0).toString().trim();
			
			if (line.isEmpty())
				continue;
			
			else if (line.startsWith("-"))
				dashes++;

			else if (dashes == 2)
			{
				PlanInfo info = new PlanInfo();

				StringTokenizer st = new StringTokenizer(line, "|");
				
				String id     = st.nextToken();
				info.operation= st.nextToken();
				info.name     = st.nextToken().trim();
				info.rows     = st.nextToken().trim();
				info.bytes    = st.nextToken().trim();
				info.cpuCost  = st.nextToken().trim();
				info.time     = st.nextToken().trim();
				info.depth    = calcDepth(info.operation);
				info.operation= info.operation.trim();

				plan.add(info);
//				System.out.println("depth:"+ info.depth +" oper:"+ info.operation);
			}
			
			else if (dashes == 4)
			{
				int idx = line.indexOf(" - ");
				
				if (idx == -1)
					plan.get(currId).predicate += "\n"+ line;
				else
				{
					Integer id = getInteger(line.substring(0, idx));
					
					if (id == null)
						plan.get(currId).predicate += "\n"+ line;
					else
					{
						currId = id;
						plan.get(currId).predicate = line.substring(idx +3).trim();
					}
				}
			}
		}

		//--- build graphic part
		
		TreeViewNode root = new TreeViewNode();
		TreeViewNode prev = root;
		root.setUserData(new Integer(0));
		root.addColumn("");
		root.addColumn("");
		root.addColumn("");
		root.addColumn("");
		root.addColumn("");

		for (PlanInfo info : plan)
		{
			TreeViewNode node = new TreeViewNode();
			node.setText  (info.operation);
			node.addColumn(info.name);
			node.addColumn(info.rows);
			node.addColumn(info.bytes);
			node.addColumn(info.cpuCost);
			node.addColumn(info.time);
			node.setUserData(info.depth);

			if (info.predicate != null)
			{
				node.setText("<html><b>"+ info.operation +"</b>");
				node.setToolTipText(info.predicate);
			}

			int prevDepth = (Integer) prev.getUserData();

			while (info.depth <= prevDepth--)
				prev = (TreeViewNode) prev.getParent();
				
			prev.addChild(node);
			prev = node;
		}

		return new ExecutionPlan(root, Arrays.asList(HEADER), sb.toString());
	}
	
	//---------------------------------------------------------------------------
	
	private int calcDepth(String name)
	{
		for (int i=0; i<name.length(); i++)
			if (name.charAt(i) != ' ')
				return i;
		
		return name.length();
	}
	
	//---------------------------------------------------------------------------
	
	private Integer getInteger(String text)
	{
		try
		{
			return Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
}

//==============================================================================

class PlanInfo
{
	public String operation;
	public String name;
	public String rows;
	public String bytes;
	public String cpuCost;
	public String time;
	public String predicate;
	public int    depth;
}

//==============================================================================
