//==============================================================================
//===
//===   SpecificPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.modules;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.ROCheckBox;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.tools.TVector;

import druid.interfaces.BasicModule;
import druid.interfaces.DataGenModule;
import druid.interfaces.DatabaseIOModule;
import druid.interfaces.RecordIOModule;
import druid.interfaces.SqlAdapter;
import druid.interfaces.TreeNodeModule;

//==============================================================================

public class SpecificPanel extends MultiPanel
{
	private DataGenPanel    dtgenPanel = new DataGenPanel();
	private TreeNodePanel   tnodePanel = new TreeNodePanel();
	private RecordIOPanel   recioPanel = new RecordIOPanel();
	private SqlAdapterPanel sqladPanel = new SqlAdapterPanel();
	private DatabaseIOPanel dbioPanel  = new DatabaseIOPanel();

	//---------------------------------------------------------------------------

	public SpecificPanel()
	{
		add(new JPanel(), "blank");
		add(dtgenPanel,   "datagen");
		add(tnodePanel,   "treenode");
		add(recioPanel,   "recordIO");
		add(sqladPanel,   "sqlAdapter");
		add(dbioPanel,    "dbIO");
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(BasicModule mod)
	{
		if (mod instanceof DataGenModule)
		{
			dtgenPanel.setCurrentModule((DataGenModule) mod);
			show("datagen");
		}

		else if (mod instanceof TreeNodeModule)
		{
			tnodePanel.setCurrentModule((TreeNodeModule) mod);
			show("treenode");
		}

		else if (mod instanceof RecordIOModule)
		{
			recioPanel.setCurrentModule((RecordIOModule) mod);
			show("recordIO");
		}

		else if (mod instanceof SqlAdapter)
		{
			sqladPanel.setCurrentModule((SqlAdapter) mod);
			show("sqlAdapter");
		}

		else if (mod instanceof DatabaseIOModule)
		{
			dbioPanel.setCurrentModule((DatabaseIOModule) mod);
			show("dbIO");
		}

		else
		{
			show("blank");
		}
	}
}

//==============================================================================

class DataGenPanel extends TPanel
{
	private JTextField txtFormat  = new ROTextField();

	//---------------------------------------------------------------------------

	public DataGenPanel()
	{
		super("Data Generation");

		FlexLayout flexL = new FlexLayout(2,1,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup panel

		add("0,0", new TLabel("Format"));

		add("1,0,x", txtFormat);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(DataGenModule mod)
	{
		txtFormat.setText(mod.getFormat());
	}
}

//==============================================================================

class TreeNodePanel extends TPanel
{
	private JTextField txtText  = new ROTextField();
	private JTextField txtEnvir = new ROTextField();

	//---------------------------------------------------------------------------

	public TreeNodePanel()
	{
		super("Tree Node");

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup panel

		add("0,0", new TLabel("Popup text"));
		add("0,1", new TLabel("Environment"));

		add("1,0,x", txtText);
		add("1,1,x", txtEnvir);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(TreeNodeModule mod)
	{
		txtText.setText(mod.getPopupText());
		txtEnvir.setText(getEnvir(mod.getEnvironment()));
	}

	//---------------------------------------------------------------------------

	private String getEnvir(int e)
	{
		TVector vs = new TVector();

		if ((e & TreeNodeModule.PROJECT)       != 0) vs.addElement("Project");
		if ((e & TreeNodeModule.DATATYPE)      != 0) vs.addElement("Datatype");
		if ((e & TreeNodeModule.JDBC_STRUCT)   != 0) vs.addElement("Jdbc/Struct");
		if ((e & TreeNodeModule.JDBC_SQLNAVIG) != 0) vs.addElement("Jdbc/SqlNavig");
		if ((e & TreeNodeModule.ER)            != 0) vs.addElement("ErView");
		if ((e & TreeNodeModule.ER_LEGEND)     != 0) vs.addElement("ErLegend");

		return vs.toString();
	}
}

//==============================================================================

class RecordIOPanel extends TPanel
{
	private JTextField txtFormat  = new ROTextField();
	private JTextField txtExten   = new ROTextField();
	private TCheckBox  chbCanImp  = new ROCheckBox();
	private TCheckBox  chbCanExp  = new ROCheckBox();

	//---------------------------------------------------------------------------

	public RecordIOPanel()
	{
		super("Record Import / Export");

		FlexLayout flexL = new FlexLayout(2,4,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup panel

		add("0,0", new TLabel("Format"));
		add("0,1", new TLabel("Extension"));
		add("0,2", new TLabel("Can import"));
		add("0,3", new TLabel("Can export"));

		add("1,0,x", txtFormat);
		add("1,1,x", txtExten);
		add("1,2,x", chbCanImp);
		add("1,3,x", chbCanExp);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(RecordIOModule mod)
	{
		txtFormat.setText(mod.getFormat());
		txtExten.setText(mod.getExtension());
		chbCanImp.setSelected(mod.canImport());
		chbCanExp.setSelected(mod.canExport());
	}
}

//==============================================================================

class SqlAdapterPanel extends TPanel
{
	private JTextField txtMatch = new ROTextField();

	//---------------------------------------------------------------------------

	public SqlAdapterPanel()
	{
		super("DBMS");

		FlexLayout flexL = new FlexLayout(2,1);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup panel

		add("0,0", new TLabel("JDBC match string"));

		add("1,0,x", txtMatch);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(SqlAdapter mod)
	{
		txtMatch.setText(mod.getMatchString());
	}
}

//==============================================================================

class DatabaseIOPanel extends TPanel
{
	private JTextField txtFormat  = new ROTextField();
	private JTextField txtExten   = new ROTextField();
	private TCheckBox  chbCanImp  = new ROCheckBox();
	private TCheckBox  chbCanExp  = new ROCheckBox();

	//---------------------------------------------------------------------------

	public DatabaseIOPanel()
	{
		super("Database Import / Export");

		FlexLayout flexL = new FlexLayout(2,4,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//--- setup panel

		add("0,0", new TLabel("Format"));
		add("0,1", new TLabel("Extension"));
		add("0,2", new TLabel("Can import"));
		add("0,3", new TLabel("Can export"));

		add("1,0,x", txtFormat);
		add("1,1,x", txtExten);
		add("1,2,x", chbCanImp);
		add("1,3,x", chbCanExp);
	}

	//---------------------------------------------------------------------------

	public void setCurrentModule(DatabaseIOModule mod)
	{
		txtFormat.setText(mod.getFormat());
		txtExten.setText(mod.getExtension());
		chbCanImp.setSelected(mod.canImport());
		chbCanExp.setSelected(mod.canExport());
	}
}

//==============================================================================
