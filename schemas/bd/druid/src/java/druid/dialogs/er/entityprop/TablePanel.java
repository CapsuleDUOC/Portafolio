//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.entityprop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;

import druid.core.DataTracker;
import druid.data.er.ErEntity;
import druid.dialogs.er.entityimport.EntityImportDialog;
import druid.util.DruidUtil;
import druid.util.gui.ImageFactory;

//==============================================================================

public class TablePanel extends JPanel implements ActionListener,	FlexTableSelListener
{
	private FlexTable table = new FlexTable(false);

	private DefaultFlexTableModel flexModel = new DefaultFlexTableModel();

	private AbstractButton btnNew;
	private AbstractButton btnDel;
	private AbstractButton btnUp;
	private AbstractButton btnDown;

	private ErEntity erEntity;

	private int iCurrPos;

	private EntityImportDialog entImpDlg;

	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		//------------------------------------------------------------------------
		//--- toolbar

		TToolBar toolBar = new TToolBar();

		btnNew = toolBar.add(ImageFactory.NEW,    this, "pop_new",  "Add more tables");
		btnDel = toolBar.add(ImageFactory.DELETE, this, "pop_del",  "Remove table");

		toolBar.addSeparator();

		btnUp   = toolBar.add(ImageFactory.UP,   this, "pop_moveup",   "Move table up");
		btnDown = toolBar.add(ImageFactory.DOWN, this, "pop_movedown", "Move table down");

		//------------------------------------------------------------------------
		//--- setup table

		flexModel.addColumn("Table", 100);
		table.addSelectionListener(this);
		table.setFlexModel(flexModel);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0,x",   toolBar);
		add("0,1,x,x", table);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void refresh(ErEntity e)
	{
		erEntity = e;

		for(int i=0; i<e.getTableNum(); i++)
		{
			Vector v = new Vector();

			v.addElement(e.getTableNodeAt(i).attrSet.getString("name"));

			flexModel.addRow(v);
		}

		iCurrPos = -1;
		updateButtons();
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableSelListener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		iCurrPos = e.getSelectedRow();

		updateButtons();
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		//--- handle popup menus ---

		if (cmd.equals("pop_new"))       pop_new();
		if (cmd.equals("pop_del"))       pop_del();
		if (cmd.equals("pop_moveup"))    pop_moveup();
		if (cmd.equals("pop_movedown"))  pop_movedown();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup Methods
	//---
	//---------------------------------------------------------------------------

	private void pop_new()
	{
		if (entImpDlg == null)
			entImpDlg = new EntityImportDialog(GuiUtil.getFrame(this));

		entImpDlg.run(erEntity.getDatabase(), false);

		if (entImpDlg.isCancelled())
			return;

		//--- add selected tables

		List<Integer> v = entImpDlg.getTableIds();

		int tabNum = erEntity.getTableNum();

		boolean bAddedTables = false;

		for(int id : v)
			if (!erEntity.existsTable(id))
			{
				erEntity.addTable(id);

				Vector vRow = new Vector();

				vRow.addElement(erEntity.getTableNodeAt(tabNum++).attrSet.getString("name"));

				flexModel.addRow(vRow);

				bAddedTables = true;
			}

		table.updateTable();
		updateButtons();

		if (bAddedTables)
			DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_del()
	{
		erEntity.removeTableAt(iCurrPos);
		iCurrPos = DruidUtil.removeRowAndSelect(table, iCurrPos);

		updateButtons();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_moveup()
	{
		swapTables(-1);
	}

	//---------------------------------------------------------------------------

	private void pop_movedown()
	{
		swapTables(1);
	}

	//---------------------------------------------------------------------------

	private void swapTables(int dy)
	{
		flexModel.swapRows(iCurrPos, iCurrPos+dy);
		table.updateTable();

		int t1 = erEntity.getTableAt(iCurrPos);
		int t2 = erEntity.getTableAt(iCurrPos+dy);

		erEntity.setTableAt(iCurrPos,    t2);
		erEntity.setTableAt(iCurrPos+dy, t1);

		iCurrPos += dy;
		updateButtons();

		table.selectRow(iCurrPos);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void updateButtons()
	{
		btnDel.setEnabled(iCurrPos != -1);

		if (iCurrPos == -1)
		{
			btnUp.setEnabled(false);
			btnDown.setEnabled(false);
		}
		else
		{
			if (erEntity.getTableNum() == 1)
			{
				btnUp.setEnabled(false);
				btnDown.setEnabled(false);
			}
			else
			{
				btnUp.setEnabled(iCurrPos != 0);
				btnDown.setEnabled(iCurrPos != erEntity.getTableNum()-1);
			}
		}
	}
}

//==============================================================================
