//==============================================================================
//===
//===   TableEditorPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.table;

import druid.core.jdbc.JdbcRecord;
import druid.core.jdbc.entities.RecordBasedEntity;
import druid.dialogs.jdbc.fieldselector.FieldSelector;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.jdbc.ResultSetEditor;
import druid.util.jdbc.dataeditor.DataEditorPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TStatusBar;
import org.dlib.gui.TTextField;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableColumn;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;
import org.dlib.tools.Util;

//==============================================================================

public class TableEditorPanel extends JPanel implements FlexTableSelListener, ActionListener
{
	private JTextField txtWhere     = new TTextField();
	private TButton    btnPrevWhere = new TButton(ImageFactory.DROP_DOWN, "previousWhere",  this, "Previous Where");
	private FlexTable  flexTable    = new FlexTable(true);
	private TStatusBar statusBar    = new TStatusBar(2);

	private int rows;
	private int iSelectedRow;

	private AbstractButton btnNew;
	private AbstractButton btnEdit;
	private AbstractButton btnRemove;
	private AbstractButton btnCopy;
	private AbstractButton btnClear;

	private AbstractButton btnRefresh;
	private AbstractButton btnChoose;
	private AbstractButton btnFit;
	private AbstractButton btnAutosize;

	private AbstractButton btnExport;
	private AbstractButton btnImport;

	private AbstractButton btnCommit;
	private AbstractButton btnRollback;

	private JTextField txtMaxRows;
	private JTextField txtFind = new TTextField(8);

	private RecordBasedEntity eNode;
	private ResultSetEditor   rsEditor;

	private DataEditorPanel dePanel;

	private List<String> previousWhere = new ArrayList<String>();

	private static final int POPUP_WIDTH  = 350;
	private static final int POPUP_LINE_HEIGHT = 20;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TableEditorPanel(DataEditorPanel dep)
	{
		dePanel = dep;

		FlexLayout flexL = new FlexLayout(4,4);
		flexL.setColProp(2, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup toolbar

		TToolBar toolBar = new TToolBar();

		btnNew     = toolBar.add(ImageFactory.NEW,      this, "new",    "Enter insert mode. Press again to add new record");
		btnEdit    = toolBar.add(ImageFactory.LENS,     this, "edit",   "Edit current record");
		btnRemove  = toolBar.add(ImageFactory.DELETE,   this, "remove", "Delete current record");
		btnCopy    = toolBar.add(ImageFactory.COPY,     this, "copy",   "Copy current record");
		btnClear   = toolBar.add(ImageFactory.BOMB,     this, "clear",  "Delete ALL records that match WHERE");
		btnRollback= toolBar.add(ImageFactory.ROLLBACK, this, "rollback", "Rollback connection");
		btnCommit  = toolBar.add(ImageFactory.COMMIT,   this, "commit", "Commit connection");

		toolBar.addSeparator();

		btnRefresh = toolBar.add(ImageFactory.REFRESH,      this, "refresh", "Refresh view");
		btnChoose  = toolBar.add(ImageFactory.COLUMN,       this, "choose",  "Choose fields to display");
		btnFit     = toolBar.add(ImageFactory.COL_FIT,      this, "fit",     "Show all fields in visible space", true);
		btnAutosize= toolBar.add(ImageFactory.COL_AUTOSIZE, this, "autosize","Use recommended width for fields", true);

		toolBar.addSeparator();

		btnExport = toolBar.add(ImageFactory.EXPORT, this, "export", "Export table data to a file");
		btnImport = toolBar.add(ImageFactory.IMPORT, this, "import", "Import table data from a file");

		toolBar.addSeparator();

		toolBar.add("Max rows");

		txtMaxRows = toolBar.addText(this, 5);

		toolBar.addSeparator();

		//------------------------------------------------------------------------
		//--- build panel

		add("0,0,x,c,4", toolBar);
		add("0,1,x,x,4", flexTable);

		add("0,2",       new TLabel("Where"));
		add("0,3",       new TLabel("Find"));
		add("1,2,x,c,2", txtWhere);
		add("1,3",       txtFind);
		add("2,3,x,c,2", statusBar);
		add("3,2,l,c",   btnPrevWhere);

		flexTable.addSelectionListener(this);
		txtWhere.addActionListener(this);
		btnPrevWhere.addActionListener(this);

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

	public void setNode(RecordBasedEntity node)
	{
		txtWhere.setText(node.where);
		btnNew.setIcon(ImageFactory.NEW);

		eNode    = node;
		rsEditor = new ResultSetEditor(node);

		setupButtons(true);
		setTransactionButtons();
		handleRefresh();
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableListener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		iSelectedRow = e.getSelectedRow();

		btnEdit  .setEnabled(iSelectedRow != -1);
		btnRemove.setEnabled(iSelectedRow != -1);
		btnCopy  .setEnabled(iSelectedRow != -1 && !rsEditor.isInserting());

		if (rsEditor.isInserting() && iSelectedRow == rows-1)
			btnEdit.setEnabled(false);

		statusBar.getLabel(1).setForeground(Color.black);

		if (iSelectedRow == -1)
		{
			String s = "Records: " + rows;

			if (rows == eNode.getJdbcConnection().getMaxRows())
			{
				statusBar.getLabel(1).setForeground(Color.red);
				s += " (MAX)";
			}

			statusBar.setText(1, s);
		}
		else
			statusBar.setText(1, "Position: " + (iSelectedRow+1) + " / " + rows);
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
				else										handleRefresh();
		}

		else if (cmd.equals("new"))           handleNew();
		else if (cmd.equals("edit"))          handleEdit();
		else if (cmd.equals("remove"))        handleRemove();
		else if (cmd.equals("copy"))          handleCopy();
		else if (cmd.equals("clear"))         handleClear();

		else if (cmd.equals("refresh"))       handleRefresh();
		else if (cmd.equals("choose"))        handleChoose();
		else if (cmd.equals("fit"))           handleFit();
		else if (cmd.equals("autosize"))      handleAutosize();

		else if (cmd.equals("export"))        handleExport();
		else if (cmd.equals("import"))        handleImport();

		else if (cmd.equals("commit"))        handleCommit();
		else if (cmd.equals("rollback"))      handleRollback();
		else if (cmd.equals("previousWhere")) handlePreviousWhere();

		else
		{
			//-- previous where selected
			txtWhere.setText(e.getActionCommand());
			handleRefresh();
		}
  }

	//---------------------------------------------------------------------------

	private void handleCommit()
	{
		GuiUtil.setWaitCursor(this, true);

		try
		{
			eNode.getJdbcConnection().getConnection().commit();
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
			eNode.getJdbcConnection().getConnection().rollback();
		}
		catch(SQLException e)
		{
			Dialogs.showException(e);
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

  private void handleNew()
	{
		int resp = rsEditor.newRecord();

		if (resp == ResultSetEditor.NEWRECORD_START)
		{
			rows++;
			flexTable.updateTable();
			flexTable.selectLastRow();
			btnCopy.setEnabled(false);
		}
		else if (resp == ResultSetEditor.NEWRECORD_END)
		{
			handleRefresh();
		}

		if (rsEditor.isInserting())	btnNew.setIcon(ImageFactory.UPDATE);
			else 								btnNew.setIcon(ImageFactory.NEW);
	}

	//---------------------------------------------------------------------------

	private void handleEdit()
	{
		dePanel.showRecordEditor(rsEditor, iSelectedRow);
	}

	//---------------------------------------------------------------------------

	private void handleRemove()
	{
		if (rsEditor.removeRecord(iSelectedRow))
		{
			flexTable.updateTable();

			rows = flexTable.getRowCount();

			if (rows == iSelectedRow)
			{
				flexTable.clearSelection();
				btnEdit.setEnabled(false);
				btnRemove.setEnabled(false);
				btnCopy.setEnabled(false);
				btnNew.setIcon(ImageFactory.NEW);
			}
			else
			{
				rowSelected(new FlexTableSelEvent(this, iSelectedRow));
			}
		}
	}

	//---------------------------------------------------------------------------

	private void handleCopy()
	{
		rsEditor.copyRecord(iSelectedRow);

		rows++;
		flexTable.updateTable();
		flexTable.selectLastRow();

		btnNew.setIcon(ImageFactory.UPDATE);
		btnCopy.setEnabled(false);
	}

	//---------------------------------------------------------------------------

	private void handleClear()
	{
		Object[] btns = { "Ok", "Cancel"};

		int resp = JOptionPane.showOptionDialog(this,
								"Do you want to delete selected records ?",
								"Please Confirm",
								JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
								null, btns, btns[1]);


		if (resp != 0) return;

		//------------------------------------------------------------------------
		//--- ok ,destroy

		GuiUtil.setWaitCursor(this, true);

		try
		{
			String query = "DELETE FROM "+eNode.getFullName();

			if (!txtWhere.getText().equals(""))
				query += " WHERE " + txtWhere.getText();

			eNode.getJdbcConnection().execute(query, null);
		}
		catch(Exception e)   
		{ 
			Dialogs.showException(e);    
		}

		handleRefresh();
	}

	//---------------------------------------------------------------------------

	private void handleRefresh()
	{
		GuiUtil.setWaitCursor(this, true);

		//--- save column widths

		List<Integer> vOldWidth = new ArrayList<Integer>();

		for(int i=0; i<rsEditor.getColumnCount(); i++)
			vOldWidth.add(rsEditor.getColumnAt(i).getPreferredWidth());

		try
		{
			int maxRows = Util.getIntValueMinMax(txtMaxRows.getText(), 100, 1, 50000);
			eNode.getJdbcConnection().setMaxRows(maxRows);
			txtMaxRows.setText(maxRows +"");

			String where = txtWhere.getText();
			rsEditor.refresh(where);
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
						FlexTableColumn ftc  = rsEditor.getColumnAt(i);
						ftc.setPreferredWidth(vOldWidth.get(i));
					}
			}

			flexTable.clearSelection();

			rows = rsEditor.getRowCount();

			btnNew.setIcon(ImageFactory.NEW);
			statusBar.setText(0, rsEditor.isEditable()  ? "Editable" : "Assisted Editing");

			rowSelected(new FlexTableSelEvent(this, -1));

			if(!previousWhere.contains(where) && !"".equals(where))
				previousWhere.add(where);
			
			eNode.where = where;
		}
		catch(SQLException e)
		{
			Dialogs.showException(e);
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleChoose()
	{
		new FieldSelector(GuiUtil.getFrame(this), eNode);

		handleRefresh();
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

	private void handleFind()
	{
		GuiUtil.setWaitCursor(this, true);
		DruidUtil.find(flexTable, txtFind.getText());
		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void handleExport()
	{
		JdbcRecord.exportData(this, eNode.getJdbcConnection(), rsEditor.getCurrentQuery());
	}

	//---------------------------------------------------------------------------

	private void handleImport()
	{
		JdbcRecord.importData(this, eNode.getJdbcConnection(), eNode.getFullName());
		handleRefresh();
	}

	//---------------------------------------------------------------------------

	private void handlePreviousWhere()
	{
		JPopupMenu popupPreviousWhere = new JPopupMenu();

		int size = previousWhere.size();

		if(size == 0)
		{
			size=1;
			popupPreviousWhere.add(MenuFactory.createItem("", "", this));
		}
		else
		{
			popupPreviousWhere.removeAll();

			for(Iterator iterator = previousWhere.iterator(); iterator.hasNext();)
			{
				String where = (String) iterator.next();
				popupPreviousWhere.add(MenuFactory.createItem(where, where, this));
			}
		}

		popupPreviousWhere.setPopupSize(POPUP_WIDTH, POPUP_LINE_HEIGHT*size);
		popupPreviousWhere.show(btnPrevWhere, 	btnPrevWhere.getWidth()-POPUP_WIDTH,
												btnPrevWhere.getHeight());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupButtons(boolean yesno)
	{
		btnEdit  .setEnabled(false);
		btnRemove.setEnabled(false);
		btnCopy  .setEnabled(false);

		btnNew     .setEnabled(yesno);
		btnClear   .setEnabled(yesno);
		btnRefresh .setEnabled(yesno);
		btnChoose  .setEnabled(yesno);
		btnFit     .setEnabled(yesno);
		btnAutosize.setEnabled(yesno);
		btnExport  .setEnabled(yesno);
		btnImport  .setEnabled(yesno);

		txtWhere.setEnabled(yesno);
		txtFind .setEnabled(yesno);
	}

	//---------------------------------------------------------------------------

	private void setTransactionButtons()
	{
		boolean isAutoCommit = true;

		try
		{
			isAutoCommit = eNode.getJdbcConnection().getConnection().getAutoCommit();
		}
		catch(Exception ignore) {}

		if (btnCommit   != null) btnCommit  .setVisible(!isAutoCommit);
		if (btnRollback != null) btnRollback.setVisible(!isAutoCommit);
	}
}

//==============================================================================
