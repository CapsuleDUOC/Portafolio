//==============================================================================
//===
//===   ConnectionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.connection;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.TButton;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextArea;
import org.dlib.gui.TTextField;

import druid.core.config.jdbc.AccountInfo;
import druid.core.jdbc.JdbcConnection;
import druid.data.DatabaseNode;
import druid.dialogs.accountman.AccountManager;
import druid.util.gui.ImageFactory;

//==============================================================================

public class ConnectionPanel extends JPanel implements ActionListener
{
	private JTextField txtUrl  = new TTextField();
	private JTextField txtUser = new TTextField();
	private JTextField txtPass = new JPasswordField();
	private TTextArea  txaLog  = new TTextArea();
	private TButton    btnUrl  = new TButton(ImageFactory.DROP_DOWN, "popup",  this, "Common urls");
	private TButton    btnWiz  = new TButton(ImageFactory.USER,      "wizard", this, "Account manager");

	private Font font = new Font("Monospaced", Font.PLAIN, 12);

	private TCheckBox chbAutoCommit = new TCheckBox();
	private TButton   btnConnect    = new TButton("Connect",    "connect",    this);
	private TButton   btnDisconnect = new TButton("Disconnect", "disconnect", this);

	private DatabaseNode dbaseNode;

	private static String[][] commonUrls = {
        {"Apache Derby",                       "jdbc:derby:<full_db_path>"},
        {"Cloudspace",                         "jdbc:cloudscape:rmi:<database_file>"},
        {"HSQL DB",                            "jdbc:hsqldb:<database>"},
        {"Hypersonic",                         "jdbc:HypersonicSQL:<database>"},
        {"IBM DB/2",                           "jdbc:db2://<host>/<database>"},
        {"Instant DB",                         "jdbc:idb:<property file>"},
        {"Interclient",                        "jdbc:interbase://<host>/<full_db_path>"},
        {"Firebird (FB Old format)",           "jdbc:firebirdsql:<host>/<port>:<full_db_path>"},
        {"Firebird (Standard format)",         "jdbc:firebirdsql://<host>:<port>/<full_db_path>"},
        {"JDBC RMI",                           "jdbc:jdbc:rmi:<host>/<database url>"},
        {"JDBC ODBC Bridge",                   "jdbc:odbc:<alias>"},
        {"Mckoi",                              "jdbc:mckoi://<host>"},
        {"Microsoft SQL Server (MS Driver)",   "jdbc:microsoft:sqlserver://<host>:<1433>"},
        {"Microsoft SQL Server (jtds driver)", "jdbc:jtds:sqlserver://<host>:<1433>"},
        {"Mimer SQL",                          "jdbc:mimer:/<dbname>"},
        {"MySQL",                              "jdbc:mysql://<host><:port>/<database>"},
        {"Oracle OCI",                         "jdbc:oracle:oci8:@<database>"},
        {"Oracle Thin",                        "jdbc:oracle:thin:@<host>:<1521>:<database>"},
        {"PostgreSQL",                         "jdbc:postgresql:<database>"},
        {"PostgreSQL (TCP connection)",        "jdbc:postgresql:<//host>:<port>/<database>"},
        {"Sunopsis XML",                       "jdbc:snps:xml?f=<file_name>&ro=true"},
        {"Sybase Adaptive Server Anywhere",    "jdbc:sybase:Tds:<host>:<port>?ServiceName=<database>"},
        {"Sybase Adaptive Server Enterprise",  "jdbc:sybase:Tds:<host>:<port>/<database>"},
		};

	private ActionListener al;
	private AccountManager accDlg;

	private JPopupMenu popup = new JPopupMenu();

	private static final int POPUP_WIDTH  = 220;
	private static final int POPUP_HEIGHT = 20 * commonUrls.length;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ConnectionPanel(ActionListener parentAL)
	{
		al = parentAL;

		FlexLayout flexL = new FlexLayout(4, 5, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(4, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Url"));
		add("0,1", new TLabel("User"));
		add("0,2", new TLabel("Password"));
		add("0,3", new TLabel("Auto commit"));

		add("1,0,x",     txtUrl);
		add("1,1,x,c,3", txtUser);
		add("1,2,x,c,3", txtPass);
		add("1,3,x",     chbAutoCommit);
		add("2,0",       btnUrl);
		add("3,0",       btnWiz);

		for(int i=0; i<commonUrls.length; i++)
			popup.add(MenuFactory.createItem(commonUrls[i][1], commonUrls[i][0], this));

		popup.setPopupSize(POPUP_WIDTH, POPUP_HEIGHT);

		//------------------------------------------------------------------------
		//--- build bottom panel

		TPanel p = new TPanel("Status");

		FlexLayout fL = new FlexLayout(2, 2, 4, 4);
		fL.setColProp(0, FlexLayout.EXPAND);
		fL.setColProp(1, FlexLayout.EXPAND);
		fL.setRowProp(0, FlexLayout.EXPAND);
		p.setLayout(fL);

		p.add("0,0,x,x,2", txaLog);
		p.add("0,1,r",     btnConnect);
		p.add("1,1,l",     btnDisconnect);

		add("0,4,x,x,4", p);

		Dimension d = new Dimension(120, 30);
		btnConnect.setPreferredSize(d);
		btnDisconnect.setPreferredSize(d);

		txaLog.setEditable(false);
		txaLog.setFont(font);

		txtUrl.addActionListener(this);
		txtUser.addActionListener(this);
		txtPass.addActionListener(this);

		btnConnect.setToolTipText("Press to connect to JDBC");
		btnDisconnect.setToolTipText("Press to disconnect from JDBC");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode dbNode)
	{
		dbaseNode = dbNode;

		boolean conn = dbNode.getJdbcConnection().isConnected();

		enableControls(conn);

		txtUrl .setText(dbNode.tempJdbcUrl);
		txtUser.setText(dbNode.tempJdbcUser);
		txtPass.setText(dbNode.tempJdbcPassword);
		txaLog .setText(dbNode.tempLog);

		chbAutoCommit.setSelected(dbNode.tempAutocommit);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(DatabaseNode dbNode)
	{
		dbNode.tempJdbcUrl      = txtUrl .getText();
		dbNode.tempJdbcUser     = txtUser.getText();
		dbNode.tempJdbcPassword = txtPass.getText();
		dbNode.tempLog          = txaLog .getText();
		dbNode.tempAutocommit   = chbAutoCommit.isSelected();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		     if (cmd.equals("connect"))    handleConnect();
		else if (cmd.equals("disconnect")) handleDisconnect();
		else if (cmd.equals("wizard"))     handleWizard();
		else if (cmd.equals("popup"))		  handlePopup();

		else if (e.getSource() instanceof JTextField)
		{
			//--- enter was pressed into a textfield
			handleConnect();
		}

		else
		{
			//--- popup selected
			txtUrl.setText(e.getActionCommand());
		}
	}

	//---------------------------------------------------------------------------

	private void handleDisconnect()
	{
		if (!dbaseNode.getJdbcConnection().disconnect(3000))
		{
			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"The disconnection thread has taken too much.\n" +
						"Check your DBMS before connecting again",
						"Problems", JOptionPane.WARNING_MESSAGE);
		}

		enableControls(false);
		txaLog.setText("");

		al.actionPerformed(new ActionEvent(this, 0, "disconnected"));
	}

	//---------------------------------------------------------------------------

	private void handleConnect()
	{
		GuiUtil.setWaitCursor(txaLog.getTextArea(), true);
		GuiUtil.setWaitCursor(this, true);

		txaLog.setText("Connecting...\n\n");

		String url    = txtUrl .getText();
		String user   = txtUser.getText();
		String passwd = txtPass.getText();

		try
		{
			JdbcConnection jdbcConn = dbaseNode.getJdbcConnection();

			if (!jdbcConn.connect(url, user, passwd, chbAutoCommit.isSelected()))
			{
				log("No suitable driver.");
				log("Try adding your driver in menu -> jdbc drivers");

				enableControls(false);
			}
			else
			{
				DatabaseMetaData md = jdbcConn.getMetaData();

				String driver  = md.getDriverName();
				String version = md.getDriverVersion();

				log(driver + " (" + version + ")");
				log("");
				log("Connected to : " + md.getDatabaseProductName());
				log("Version      : " + md.getDatabaseProductVersion());
				log("");
				log(jdbcConn.getLog());

				log("Done.");

				//------------------------------------------------------------------
				//--- last steps

				enableControls(true);

				al.actionPerformed(new ActionEvent(this, 0, "connected"));
			}
		}
		catch(SQLException e)
		{
			log("Raised SQL Exception:");
			log("   Message: " + e.getMessage());
			log("   Code   : " + e.getErrorCode());
			log("   State  : " + e.getSQLState());
			log("");
			log("Stack is:");

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log(sw.toString());
		}
		catch(Throwable e)
		{
			log("Jdbc driver problems:");
			log("");
			log("Stack is:");

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log(sw.toString());
		}

		GuiUtil.setWaitCursor(txaLog.getTextArea(), false);
		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleWizard()
	{
		if (accDlg == null)
			accDlg = new AccountManager(GuiUtil.getFrame(this));

		AccountInfo ai = accDlg.run();

		if (ai != null)
		{
			txtUrl .setText(ai.url);
			txtUser.setText(ai.user);
			txtPass.setText(ai.password);
			chbAutoCommit.setSelected(ai.autoCommit);
		}
	}

	//---------------------------------------------------------------------------

	private void handlePopup()
	{
		popup.show(btnUrl, btnUrl.getWidth()-POPUP_WIDTH, btnUrl.getHeight());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Private Methods
	//---
	//---------------------------------------------------------------------------

	private void enableControls(boolean conn)
	{
		txtUrl.setEnabled(!conn);
		txtUser.setEnabled(!conn);
		txtPass.setEnabled(!conn);
		chbAutoCommit.setEnabled(!conn);

		btnConnect.setEnabled(!conn);
		btnDisconnect.setEnabled(conn);
		btnUrl.setEnabled(!conn);
		btnWiz.setEnabled(!conn);
	}

	//---------------------------------------------------------------------------

	private void log(final String s)
	{
		txaLog.append(s + "\n");
	}
}

//==============================================================================
