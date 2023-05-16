//==============================================================================
//===
//===   FieldsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.fields;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.flextable.FlexTableConfirmator;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTracker;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.dialogs.dtselector.DataTypeSelector;
import druid.util.gui.AbstractDataEntryPanel;

//==============================================================================

public class FieldsPanel extends AbstractDataEntryPanel implements FlexTableConfirmator
{
	private TableNode tableNode;

	private TButton btnType = new TButton("", "ddSelector", this);

	private Vector vFieldId;

	private DataTypeSelector dts;

	private DatabaseNode clipDB;

	//---------------------------------------------------------------------------

	public FieldsPanel()
	{
		flexModel.setConfirmator(this);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableNode node)
	{
		tableNode = node;

		//------------------------------------------------------------------------
		//--- reset flex table

		flexModel.clearColumns();
		flexModel.clearData();

		iSelectedRow = -1;

		//------------------------------------------------------------------------
		//--- add basic headers

		flexModel.addColumn("Name", 150);
		flexModel.addColumn("Type", 200, btnType);

		//------------------------------------------------------------------------
		//--- add attrib headers

		vFieldId = new Vector();

		DatabaseNode db = node.getDatabase();
		FieldAttribs fa = db.fieldAttribs;

		for(int i=0; i<fa.size(); i++)
		{
			AttribSet as    = fa.get(i);
			String    label = as.getString("name");
			int       width = as.getInt("width");

			flexModel.addColumn(label, width);

			vFieldId.addElement("" + as.getInt("id"));
		}

		//------------------------------------------------------------------------
		//--- add table rows (loop on fields)

		for(int i=0; i<tableNode.getChildCount(); i++)
		{
			FieldNode fNode = (FieldNode)tableNode.getChild(i);

			flexModel.addRow(DataLib.getFieldRow(fNode));
		}

		flexTable.setFlexModel(flexModel);

		//------------------------------------------------------------------------
		//--- disable buttons

		btnDel.setEnabled(false);
		btnCut.setEnabled(false);
		btnCopy.setEnabled(false);

		btnUp.setEnabled(false);
		btnDown.setEnabled(false);

		btnPaste.setEnabled(clipData != null);
	}

	//---------------------------------------------------------------------------

	public int dataSize() { return tableNode.getChildCount(); }

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	protected void handleNew()
	{
		DatabaseNode dbNode = tableNode.getDatabase();

		FieldNode node = new FieldNode();

		tableNode.addChild(node, false);
		DataLib.syncField(dbNode, node);

		flexModel.addRow(DataLib.getFieldRow(node));
		flexTable.updateTable();
		flexTable.selectLastRow();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void handleDel(boolean cut)
	{
		TreeViewNode node = tableNode.getChild(iSelectedRow);

		if (cut)
		{
			clipData = node;
			clipDB   = tableNode.getDatabase();
		}

		tableNode.removeChild(node);

		flexModel.removeRow(iSelectedRow);
		flexTable.updateTable();

		if (flexTable.getRowCount() == iSelectedRow)
			flexTable.clearSelection();
		else
		if (flexTable.getRowCount() == iSelectedRow-1)
			btnDown.setEnabled(false);

		btnPaste.setEnabled(clipData != null);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void handleCopy()
	{
		clipData = tableNode.getChild(iSelectedRow).duplicate();
		clipDB   = tableNode.getDatabase();

		DataLib.remapIds((AbstractNode) clipData, false);

		btnPaste.setEnabled(true);
	}

	//---------------------------------------------------------------------------

	protected void handlePaste()
	{
		FieldNode child = (FieldNode) clipData;

		clipData = child.duplicate();
		DataLib.remapIds((AbstractNode) clipData, false);

		if (iSelectedRow == -1 || iSelectedRow == flexTable.getRowCount() -1)
		{
			tableNode.addChild(child, false);

			if (child.getDatabase() != clipDB)
				DataLib.migrateNodes(child, clipDB, child.getDatabase());

			flexModel.addRow(DataLib.getFieldRow(child));
			flexTable.updateTable();
			flexTable.selectLastRow();
		}
		else
		{
			tableNode.insertChild(child, iSelectedRow +1, false);

			if (child.getDatabase() != clipDB)
				DataLib.migrateNodes(child, clipDB, child.getDatabase());

			flexModel.insertRow(iSelectedRow +1, DataLib.getFieldRow(child));
			flexTable.updateTable();
			flexTable.selectRow(iSelectedRow +1);
		}

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void handleUp()
	{
		int i = iSelectedRow;

		flexModel.swapRows(i, i-1);
		flexTable.updateTable();
		tableNode.swapNodes(i, i-1);

		flexTable.selectRow(i-1);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	protected void handleDown()
	{
		int i = iSelectedRow;

		flexModel.swapRows(i, i+1);
		flexTable.updateTable();
		tableNode.swapNodes(i, i+1);

		flexTable.selectRow(i+1);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	//---
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		if (e.getActionCommand().equals("ddSelector"))
		{
			FieldNode node = (FieldNode)tableNode.getChild(iSelectedRow);

			if (dts == null)
				dts = new DataTypeSelector(GuiUtil.getFrame(this));

			dts.run(node);

			String sType = DataTypeLib.getTypeDef(node);

			flexTable.setValueAt(sType, iSelectedRow, 1);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   FlexTableConfirmator
	//---
	//---------------------------------------------------------------------------

	public boolean confirmValueChanged(int row, int col, Object value)
	{
		FieldNode f  = (FieldNode) tableNode.getChild(row);
		AttribSet as = f.fieldAttribs;

		if (col < 2)
		{
			if (col == 0)
			{
				if (!value.toString().equals(""))
				{
					//--- field name changed

					f.attrSet.setData("name", value);
					f.setText((String)value);
					f.refresh();
				}
				else
					return false;
			}
		}
		else
		{
			//--- attrib changed

			String id = (String)vFieldId.elementAt(col -2);

			if (value != null)	as.setData(id, value);
				else					return false;
		}

		return true;
	}
}

//==============================================================================
