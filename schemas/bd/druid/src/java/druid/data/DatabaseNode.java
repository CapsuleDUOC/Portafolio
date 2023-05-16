//==============================================================================
//===
//===   DatabaseNode
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data;

import druid.core.AttribSet;
import druid.core.jdbc.JdbcConnection;
import druid.data.datatypes.DataTypes;
import druid.data.er.ErView;
import java.util.Enumeration;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.Util;

//==============================================================================

public class DatabaseNode extends AbstractNode
{
	public Revisions     revisions    = new Revisions();
	public DataTypes     dataTypes    = new DataTypes();
	public FieldAttribs  fieldAttribs = new FieldAttribs();
	public DatabaseVars  dbVars       = new DatabaseVars();
	public SqlQuery      sqlQueries   = new SqlQuery();
	public ModulesUsage  modsUsage    = new ModulesUsage();
	public ErView        erViews      = new ErView();

	public String  tempJdbcUrl      = "jdbc:oracle:thin:@<host>:<1521>:<sid>";
	public String  tempJdbcUser     = "";
	public String  tempJdbcPassword = "";
	public String  tempLog          = "";
	public boolean tempAutocommit;

	//---------------------------------------------------------------------------
	//--- internal use only ---
	//-------------------------

	private JdbcConnection jdbcConn = new JdbcConnection();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DatabaseNode()
	{
		this("-UnNamed-");
	}

	//---------------------------------------------------------------------------

	public DatabaseNode(String name)
	{
		super(name);

		attrSet.addAttrib("preSql",  "");
		attrSet.addAttrib("postSql", "");

		attrSet.addAttrib("codeUseBuild",   true);
		attrSet.addAttrib("ddUseDDEquiv",   false);
		attrSet.addAttrib("summUseDDEquiv", true);

		dataTypes.setDatabase(this);
		erViews.setDatabase(this);
		sqlQueries.setDatabase(this);

		setDatabase(this);

		setToolTipText("A database node");
	}

	//---------------------------------------------------------------------------

	protected TreeViewNode getNewInstance() { return new DatabaseNode(); }

	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		DatabaseNode n = (DatabaseNode) node;

		n.revisions    = (Revisions)    revisions.duplicate();
		n.dataTypes    = (DataTypes)    dataTypes.duplicate();
		n.fieldAttribs = (FieldAttribs) fieldAttribs.duplicate();
		n.dbVars       = (DatabaseVars) dbVars.duplicate();
		n.sqlQueries   = (SqlQuery)     sqlQueries.duplicate();
		n.erViews      = (ErView)       erViews.duplicate();
		n.modsUsage    = modsUsage.duplicate();

		n.dataTypes.setDatabase(n);
		n.erViews.setDatabase(n);
		n.sqlQueries.setDatabase(n);

		super.copyTo(node);
	}

	//---------------------------------------------------------------------------

	public TableNode getTableByID(int id)
	{
		for(Enumeration e = preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode)e.nextElement();
			AttribSet as = node.attrSet;

			if (node instanceof TableNode)
				if (as.getInt("id") == id)
					return (TableNode) node;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public TableNode getTableByName(String name)
	{
		for(Enumeration e = preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();
			AttribSet as = node.attrSet;

			if (node instanceof TableNode)
				if (as.getString("name").equals(name))
					return (TableNode) node;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public ProjectNode getProjectNode()
	{
		return (ProjectNode) getParent();
	}

	//---------------------------------------------------------------------------

	public JdbcConnection getJdbcConnection()
	{
		return jdbcConn;
	}

	//---------------------------------------------------------------------------

	public String substVars(String text)
	{
		for(int i=0; i<dbVars.size(); i++)
		{
			AttribSet as = dbVars.get(i);

			String name = "{"+ as.getString("name") +"}";
			String value= as.getString("value");

			if (text.indexOf(name) != -1)
				text = Util.replaceStr(text, name, value);
		}

		return text;
	}
}

//==============================================================================
