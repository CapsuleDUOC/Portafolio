//==============================================================================
//===
//===   MySqlModule
//===
//===   Copyright (C) by Jaime Sastre.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mysql.sql;

import java.util.Enumeration;

import javax.swing.JComponent;

import mod.dbms.mysql.sql.panels.database.DatabasePanel;
import mod.dbms.mysql.sql.panels.table.TablePanel;

import org.dlib.tools.TVector;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.data.AbstractNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.interfaces.BasicModule;
import druid.interfaces.ModuleOptions;
import druid.util.decoder.OnClauseDecoder;
import factory.sql.AbstractSqlGenModule;
import factory.sql.BasicDatabaseSettings;
import factory.sql.FKeyEntry;
import factory.sql.SqlUtil;

//==============================================================================

public class MySqlModule extends AbstractSqlGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()        { return "mysql"; }
	public String getVersion()   { return "1.0";   }
	public String getAuthor()    { return "Jaime Sastre, Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates the sql script that creates the database. "   +
				"Supports multiple foreign keys, sequences, triggers and table rules.";
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "MySql"; }
	public boolean isDirectoryBased() { return false;   }
	public boolean hasLargePanel()    { return true;    }

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == BasicModule.DATABASE) return databaseOptions;
		if (env == BasicModule.TABLE)		return tableOptions;

		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ModuleOptions handlers
	//---
	//---------------------------------------------------------------------------

	//---------------------------------------------------------------------------
	//--- Env : DATABASE
	//---------------------------------------------------------------------------

	private ModuleOptions databaseOptions = new ModuleOptions()
	{
		private DatabasePanel dbPanel = new DatabasePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			dbPanel.refresh(new BasicDatabaseSettings(node.modsConfig, MySqlModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return dbPanel; }
	};

	//---------------------------------------------------------------------------
	//--- Env : TABLE
	//---------------------------------------------------------------------------

	private ModuleOptions tableOptions = new ModuleOptions()
	{
		private TablePanel tablePanel = new TablePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			tablePanel.refresh(new TableSettings(node.modsConfig, MySqlModule.this),
									 node.getDatabase());
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return tablePanel; }
	};

	//---------------------------------------------------------------------------
	//---
	//--- Overwritten methods
	//---
	//---------------------------------------------------------------------------

	protected String genTablePost(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		return LF + genTableEngine(node) + genTableCharset(node) + ts.getPostSql().trim();
	}

	//---------------------------------------------------------------------------

	protected String genTableEngine(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		String engine = ts.getEngine();

		if (engine.equals(TableSettings.TYPE_MYISAM))
			return " ENGINE = MyISAM";

		return " ENGINE = InnoDB";
	}

	//---------------------------------------------------------------------------

	protected String genTableCharset(TableNode node)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		String charset = ts.getCharset().trim();

		if (charset.length() != 0)
			return " DEFAULT CHARSET = "+ charset;

		return "";
	}

	//---------------------------------------------------------------------------

	protected String generateIndex(TableNode node, AttribSet indexAS, TVector fields, boolean unique, int cnt)
	{
		TableSettings ts = new TableSettings(node.modsConfig, this);

		AttribList idxAL = ts.getIndexOpt(node.getDatabase());

		return SqlUtil.generateIndex(node, indexAS, fields, unique, cnt, idxAL,true);
	}

	//---------------------------------------------------------------------------

	protected String genForeignKey(TableNode node, FKeyEntry fk, int cnt)
	{
		StringBuffer index = new StringBuffer("INDEX FK_");

		for(Enumeration e= fk.vFields.elements(); e.hasMoreElements();)
		{
			index.append(e.nextElement().toString());
			index.append('_');
		}

//		index.append(cnt);
		index.append("(");
		index.append(fk.vFields.toString());
		index.append("),"+ LF);

		//--- par_ind (idPropiedad),
		String s =  "foreign key(" + fk.vFields.toString() + ") references " + fk.fkTable +
						"(" + fk.vFkFields.toString() + ")";

		OnClauseDecoder onClauseDec = new OnClauseDecoder();

		//--- on update

		if (!fk.onUpd.equals(FieldNode.NOACTION))
			s += " on update " + onClauseDec.decode(fk.onUpd);

		//--- on delete

		if (!fk.onDel.equals(FieldNode.NOACTION))
			s += " on delete " + onClauseDec.decode(fk.onDel);

		//--- add constraint name to FKEY

		StringBuffer sb = new StringBuffer(index.toString());

		sb.append("    ");

		String name = node.attrSet.getString("tempFK");

		if (!name.equals(""))
		{
			sb.append("CONSTRAINT ");
			sb.append(SqlUtil.expandTemplate(name, node.attrSet.getString("name"), fk.vFields, cnt));
			sb.append(" ");
		}

		sb.append(s);

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	protected String genDropTable(TableNode node)
	{
		return "DROP TABLE IF EXISTS "+ node.getQualifiedName();
	}

	//---------------------------------------------------------------------------

	protected String genDropView(ViewNode node)
	{
		String code = node.attrSet.getString("sqlCode");

		return "DROP "+ SqlUtil.getTypeFromCode(code) +" IF EXISTS "+ SqlUtil.getNameFromCode(code);
	}

	//---------------------------------------------------------------------------
	
	protected String getExtraAttribs(FieldNode field) 
	{
		TableSettings ts = new TableSettings(((TableNode) field.getParent()).modsConfig, this);

		if (field.isPkey() && ts.isAutoInc())
			return "AUTO_INCREMENT";

		return null;
	}
}

//==============================================================================
