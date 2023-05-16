//==============================================================================
//===
//===   JdbcPanel
//===
//===   Copyright (C) by Antonio Gallardo.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.ojb.panels;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.ojb.Settings;

//==============================================================================

/**
 *  JDBC Connection Panel Configuration
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: JdbcPanel.java,v 1.11 2004/01/09 07:29:03 antoniog Exp $
*/

public class JdbcPanel extends JPanel
{
	private TTextFieldGuardian txtjcdAlias          = new TTextFieldGuardian("JcdAlias");
	private TCheckBoxGuardian  chkDefaultConnection = new TCheckBoxGuardian("DefaultConnection", "Default Connection");
	private TComboBoxGuardian  tcbPlataform         = new TComboBoxGuardian("DbPlat");
	private TComboBoxGuardian  tcbJDBCLevel         = new TComboBoxGuardian("JdbcLevel");
	private TComboBoxGuardian  tcbSequenceManager   = new TComboBoxGuardian("SequenceManager");
	private TTextFieldGuardian txtDBHost            = new TTextFieldGuardian("DbHost");
	private TTextFieldGuardian txtDBPort            = new TTextFieldGuardian("DbPort");
	private TTextFieldGuardian txtDBUser            = new TTextFieldGuardian("DbUser");
	private TTextFieldGuardian txtDBPassword        = new TTextFieldGuardian("DbPassword");

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public JdbcPanel()
    {
		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", buildGeneralPanel());
		add("0,1,x", buildDatabasePanel());

		// Fill Database Plataforms ComboBox
		loadPlataforms(tcbPlataform);

		// Fill JDBC Level ComboBox
		loadJdbcLevel(tcbJDBCLevel);

		// Fill Sequences Manager ComboBox
		loadSequenceManager(tcbSequenceManager);
	}

	//---------------------------------------------------------------------------

	private JPanel buildGeneralPanel()
	{
		TPanel p= new TPanel("General");

		FlexLayout flexL = new FlexLayout(2,6);
		flexL.setColProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0",   new TLabel("jcdAlias"));
		p.add("0,1",   new TLabel("Platform"));
		p.add("0,2",   new TLabel("JDBC Level"));
		p.add("0,3",   new TLabel("Sequence Manager"));

		p.add("1,0,x", txtjcdAlias);
		p.add("1,1,x", tcbPlataform);
		p.add("1,2,x", tcbJDBCLevel);
		p.add("1,3,x", tcbSequenceManager);

		p.add("0,5,x,c,2", chkDefaultConnection);

		return p;
	}

	//---------------------------------------------------------------------------

	private JPanel buildDatabasePanel()
	{
		TPanel p= new TPanel("Database");

		FlexLayout flexL = new FlexLayout(2,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0",   new TLabel("Host"));
		p.add("0,1",   new TLabel("Port"));
		p.add("0,2",   new TLabel("User"));
		p.add("0,3",   new TLabel("Password"));
		p.add("1,0,x", txtDBHost);
		p.add("1,1,x", txtDBPort);
		p.add("1,2,x", txtDBUser);
		p.add("1,3,x", txtDBPassword);

		return p;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		txtjcdAlias.refresh(s);
		chkDefaultConnection.refresh(s);
		tcbPlataform.refresh(s);
		tcbJDBCLevel.refresh(s);
		tcbSequenceManager.refresh(s);
		txtDBHost.refresh(s);
		txtDBPort.refresh(s);
		txtDBUser.refresh(s);
		txtDBPassword.refresh(s);
	}

	//---------------------------------------------------------------------------

	private void loadPlataforms(TComboBoxGuardian t)
	{
		t.addItem(Settings.DBPLAT_AXION,       Settings.DBPLAT_AXION);
		t.addItem(Settings.DBPLAT_DB2,         Settings.DBPLAT_DB2);
		t.addItem(Settings.DBPLAT_FIREBIRD,    Settings.DBPLAT_FIREBIRD);
		t.addItem(Settings.DBPLAT_HSQLDB,      Settings.DBPLAT_HSQLDB);
		t.addItem(Settings.DBPLAT_INFORMIX,    Settings.DBPLAT_INFORMIX);
		t.addItem(Settings.DBPLAT_MSACCESS,    Settings.DBPLAT_MSACCESS);
		t.addItem(Settings.DBPLAT_MSSQLSERVER, Settings.DBPLAT_MSSQLSERVER);
		t.addItem(Settings.DBPLAT_MYSQL,       Settings.DBPLAT_MYSQL);
		t.addItem(Settings.DBPLAT_NONSTOPSQL,  Settings.DBPLAT_NONSTOPSQL);
		t.addItem(Settings.DBPLAT_ORACLE,      Settings.DBPLAT_ORACLE);
		t.addItem(Settings.DBPLAT_ORACLE9,     Settings.DBPLAT_ORACLE9);
		t.addItem(Settings.DBPLAT_POSTGRES,    Settings.DBPLAT_POSTGRES);
		t.addItem(Settings.DBPLAT_SAPDB,       Settings.DBPLAT_SAPDB);
		t.addItem(Settings.DBPLAT_SYBASE,      Settings.DBPLAT_SYBASE);
		t.addItem(Settings.DBPLAT_SYBASEASE,   Settings.DBPLAT_SYBASEASE);
		t.addItem(Settings.DBPLAT_SYBASEASA,   Settings.DBPLAT_SYBASEASA);
	}

	//---------------------------------------------------------------------------

	private void loadJdbcLevel(TComboBoxGuardian t)
	{
		t.addItem(Settings.JDBC_LEVEL1, Settings.JDBC_LEVEL1);
		t.addItem(Settings.JDBC_LEVEL2, Settings.JDBC_LEVEL2);
		t.addItem(Settings.JDBC_LEVEL3, Settings.JDBC_LEVEL3);
	}

	//---------------------------------------------------------------------------

	private void loadSequenceManager(TComboBoxGuardian t)
	{
		t.addItem(Settings.SEQUENCE_HIGHLOW,         Settings.SEQUENCE_HIGHLOW_N);
		t.addItem(Settings.SEQUENCE_INMEMORY,        Settings.SEQUENCE_INMEMORY_N);
		t.addItem(Settings.SEQUENCE_NEXTVAL,         Settings.SEQUENCE_NEXTVAL_N);
		t.addItem(Settings.SEQUENCE_SEQHILO,         Settings.SEQUENCE_SEQHILO_N);
		t.addItem(Settings.SEQUENCE_STOREDPROCEDURE, Settings.SEQUENCE_STOREDPROCEDURE_N);
		t.addItem(Settings.SEQUENCE_MSSQLGUID,       Settings.SEQUENCE_MSSQLGUID_N);
		t.addItem(Settings.SEQUENCE_MYSQL,           Settings.SEQUENCE_MYSQL_N);
	}
}

//==============================================================================
