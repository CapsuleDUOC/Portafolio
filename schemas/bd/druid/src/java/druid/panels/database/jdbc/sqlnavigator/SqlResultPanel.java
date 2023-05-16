//==============================================================================
//===
//===   SqlResultPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.sqlnavigator;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TLabel;
import org.dlib.gui.TStatusBar;
import org.dlib.gui.TTextField;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableColumn;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;
import org.dlib.tools.Util;

import druid.core.jdbc.ExecutionPlan;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcRecord;
import druid.core.jdbc.RecordList;
import druid.dialogs.execplan.ExecPlanFrame;
import druid.interfaces.SqlAdapter;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.gui.SqlTextArea;
import druid.util.jdbc.ResultSetEditor;

//==============================================================================

public class SqlResultPanel extends JPanel implements ActionListener, FlexTableSelListener
{
	private SqlTextArea  txaQuery;
	private FlexTable    flexTable = new FlexTable();
	private TStatusBar   statusBar = new TStatusBar(2);

	private int    rows;
	private String sQuery;

	private JdbcConnection jdbcConn;

	private AbstractButton btnExecute;
	private AbstractButton btnExpPlan;
	private AbstractButton btnFit;
	private AbstractButton btnAutosize;
	private AbstractButton btnExport;
	private AbstractButton btnCommit;
	private AbstractButton btnRollback;

	private JTextField txtMaxRows;
	private JTextField txtFind = new TTextField(8);

	private ResultSetEditor rsEditor;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlResultPanel()
	{
		FlexLayout flexL = new FlexLayout(3,3,4,4);
		flexL.setColProp(2, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup toolbar

		TToolBar toolBar = new TToolBar();

		btnExecute = toolBar.add(ImageFactory.GEAR,         this, "execute",  "Execute query");
		btnExpPlan = toolBar.add(ImageFactory.FIRST_AID,    this, "plan",     "Explain plan");
		btnRollback= toolBar.add(ImageFactory.ROLLBACK,     this, "rollback", "Rollback connection");
		btnCommit  = toolBar.add(ImageFactory.COMMIT,       this, "commit",   "Commit connection");

		toolBar.addSeparator();

		btnFit     = toolBar.add(ImageFactory.COL_FIT,      this, "fit",     "Show all fields in visible space",     true);
		btnAutosize= toolBar.add(ImageFactory.COL_AUTOSIZE, this, "autosize","Use recommended width for fields",     true);

		toolBar.addSeparator();

		btnExport = toolBar.add(ImageFactory.EXPORT, this, "export", "Export results to a file");

		toolBar.addSeparator();

		toolBar.add("Max rows");

		txtMaxRows = toolBar.addText(this, 5);

		toolBar.addSeparator();

		//------------------------------------------------------------------------
		//--- build panel

		add("0,0,x,c,3", toolBar);
		add("0,1,x,x,3", flexTable);
		add("0,2",       new TLabel("Find"));
		add("1,2",       txtFind);
		add("2,2,x",     statusBar);

		flexTable.addSelectionListener(this);

		btnFit.setSelected(true);
		btnAutosize.setSelected(false);
		txtMaxRows.setText(100 +"");
		txtFind.addActionListener(this);
		txtFind.setName("find");

		statusBar.setSlotExpansion(0, true);
		statusBar.setSlotExpansion(1, true);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setConnection(JdbcConnection conn)
	{
		jdbcConn = conn;
		rsEditor = new ResultSetEditor(conn);

		setTransactionButtons();
	}

	//---------------------------------------------------------------------------

  public void clear()
	{
		flexTable.setFlexModel(new RecordList());

		btnExecute.setEnabled(false);
		btnExpPlan.setEnabled(false);
		enableButtons(false);

		sQuery = "???";

		statusBar.setText(0," ");
		statusBar.setText(1," ");
	}

	//---------------------------------------------------------------------------

	public void enableExecute(boolean yesno)
	{
		btnExecute.setEnabled(yesno && jdbcConn.isConnected());
		btnExpPlan.setEnabled(yesno && jdbcConn.isConnected() && jdbcConn.getSqlAdapter().isExecutionPlanSupported());
		setTransactionButtons();
	}

	//---------------------------------------------------------------------------

	public void setQuerySource(SqlTextArea t)
	{
		txaQuery = t;
	}

	//---------------------------------------------------------------------------
	//---
	//---   Listeners
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		int row = e.getSelectedRow();

		statusBar.getLabel(1).setForeground(Color.black);

		if (row == -1)
		{
			String s = "Records: " + rows;

			if (rows == jdbcConn.getMaxRows())
			{
				s += " (MAX)";
				statusBar.getLabel(1).setForeground(Color.red);
			}

			statusBar.setText(1, s);
		}
		else
			statusBar.setText(1, "Position: " + (row+1) + " / " + rows);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (e.getSource() instanceof JTextField)
		{
			JTextField txt = (JTextField) e.getSource();

			if ("find".equals(txt.getName()))	handleFind();
				else										handleExecute();
		}

		else if (cmd.equals("execute"))  handleExecute();
		else if (cmd.equals("plan"))     handleExplainPlan();
		else if (cmd.equals("fit"))      handleFit();
		else if (cmd.equals("autosize")) handleAutosize();
		else if (cmd.equals("export"))   handleExport();
		else if (cmd.equals("commit"))   handleCommit();
		else if (cmd.equals("rollback")) handleRollback();
	}

	//---------------------------------------------------------------------------

	private void handleExecute()
	{
		sQuery = txaQuery.getText();

		GuiUtil.setWaitCursor(this, true);

		//--- save column widths

		Vector vOldWidth = new Vector();

		for(int i=0; i<rsEditor.getColumnCount(); i++)
			vOldWidth.addElement(new Integer(rsEditor.getColumnAt(i).getPreferredWidth()));

		try
		{
			long startTime = System.currentTimeMillis();

			if (sQuery.toLowerCase().startsWith("select "))
			{
				int maxRows = Util.getIntValueMinMax(txtMaxRows.getText(), 100, 1, 50000);
				jdbcConn.setMaxRows(maxRows);
				txtMaxRows.setText(maxRows +"");

				rsEditor.refresh(sQuery);
				flexTable.setFlexModel(rsEditor);

				handleFit();

				if (btnAutosize.isSelected())
					handleAutosize();
				else
				{
					//--- restore previous column width

					if (vOldWidth.size() == rsEditor.getColumnCount())
						for(int i=0; i<vOldWidth.size(); i++)
						{
							FlexTableColumn ftc   = rsEditor.getColumnAt(i);
							Integer         width = (Integer) vOldWidth.elementAt(i);

							ftc.setPreferredWidth(width.intValue());
						}
				}

				rows = rsEditor.getRowCount();
				rowSelected(new FlexTableSelEvent(this, -1));

				enableButtons(true);
			}
			else
			{
				int res = jdbcConn.execute(sQuery, null);

				JOptionPane.showMessageDialog(Frame.getFrames()[0],
						"Rows affected : " + res,
						"Success", JOptionPane.INFORMATION_MESSAGE);

				enableButtons(false);
			}

			long endTime = System.currentTimeMillis();

			float secs = ((float)(endTime - startTime) / 1000);
			statusBar.setText(0, "Executed in (secs):" + secs);
		}
		catch(Exception e) 
		{ 
			Dialogs.showException(e); 
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------
	
	private void handleExplainPlan()
	{
		String query = txaQuery.getText();

		GuiUtil.setWaitCursor(this, true);

		try
		{
			SqlAdapter adapter = jdbcConn.getSqlAdapter();

			ExecutionPlan plan = adapter.retrieveExecutionPlan(jdbcConn, query);
			ExecPlanFrame frame = new ExecPlanFrame();
			frame.showPlan(query, plan);
		}
		catch(Exception e) 
		{ 
			Dialogs.showException(e); 
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleCommit()
	{
		GuiUtil.setWaitCursor(this, true);

		try
		{
			jdbcConn.getConnection().commit();
		}
		catch(SQLException e)
		{
			Dialogs.showException(e);
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleRollback()
	{
		GuiUtil.setWaitCursor(this, true);

		try
		{
			jdbcConn.getConnection().rollback();
		}
		catch(SQLException e)
		{
			Dialogs.showException(e);
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleFit()
	{
		flexTable.setAutoResizeMode(btnFit.isSelected() ? JTable.AUTO_RESIZE_ALL_COLUMNS
											 							: JTable.AUTO_RESIZE_OFF);
	}

	//---------------------------------------------------------------------------

	private void handleAutosize()
	{
		for(int i=0; i<rsEditor.getColumnCount(); i++)
		{
			FlexTableColumn ftc = rsEditor.getColumnAt(i);

			int prefWidth = ((Integer) ftc.getUserObject()).intValue();

			if (btnAutosize.isSelected())	ftc.setPreferredWidth(prefWidth);
				else								ftc.setPreferredWidth(ResultSetEditor.DEFAULT_WIDTH);
		}
	}

	//---------------------------------------------------------------------------

	private void handleExport()
	{
		JdbcRecord.exportData(this, jdbcConn, sQuery);
	}

	//---------------------------------------------------------------------------

	private void handleFind()
	{
		GuiUtil.setWaitCursor(this, true);
		DruidUtil.find(flexTable, txtFind.getText());
		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Private methods
	//---
	//---------------------------------------------------------------------------

	private void enableButtons(boolean yesno)
	{
		btnFit     .setEnabled(yesno);
		btnAutosize.setEnabled(yesno);
		btnExport  .setEnabled(yesno);
		txtFind    .setEnabled(yesno);
	}

	//---------------------------------------------------------------------------

	private void setTransactionButtons()
	{
		boolean isAutoCommit = true;

		try
		{
			isAutoCommit = jdbcConn.getConnection().getAutoCommit();
		}
		catch(Exception ignore) {}

		if (btnCommit != null)
		{
			btnCommit.setVisible(!isAutoCommit);
			btnCommit.setEnabled(jdbcConn.isConnected());
		}

		if (btnRollback != null)
		{
			btnRollback.setVisible(!isAutoCommit);
			btnRollback.setEnabled(jdbcConn.isConnected());
		}
	}
}

//==============================================================================
