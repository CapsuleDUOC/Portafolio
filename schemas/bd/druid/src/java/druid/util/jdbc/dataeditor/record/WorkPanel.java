//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.TToolBar;
import org.dlib.gui.treeview.TreeViewNode;

import ddf.type.SqlType;
import druid.core.DataModel;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.jdbc.ResultSetEditor;
import druid.util.jdbc.dataeditor.DataEditorPanel;
import druid.util.jdbc.dataeditor.record.largedata.LargeDataPanel;
import druid.util.jdbc.dataeditor.record.simple.SimplePanel;
import druid.util.jdbc.dataeditor.record.text.TextPanel;

//==============================================================================

public class WorkPanel extends JPanel implements ActionListener, KeyListener, DataModel
{
	private AbstractButton btnBack;
	private AbstractButton btnRefresh;
	private AbstractButton btnUpdate;

	private MultiPanel     multiPanel  = new MultiPanel();
	private SimplePanel    simplePanel = new SimplePanel(this);
	private TextPanel      textPanel   = new TextPanel(this);
	private LargeDataPanel largePanel  = new LargeDataPanel();

	private int currCol;
	private int editRow;

	private ResultSetEditor rsEditor;

	private DataEditorPanel dePanel;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		//------------------------------------------------------------------------
		//--- toolbar

		TToolBar toolBar = new TToolBar();

		btnBack    = toolBar.add(ImageFactory.LENS    , this, "back",    "Back to table editor", true);
		btnRefresh = toolBar.add(ImageFactory.REFRESH , this, "refresh", "Refresh current field's data");
		btnUpdate  = toolBar.add(ImageFactory.UPDATE,   this, "update",  "Save changes to DBMS");

		//------------------------------------------------------------------------
		//--- multipanel

		multiPanel.add("blank",      new JPanel());
		multiPanel.add("nothandled", new JPanel());
		multiPanel.add("simple",     simplePanel);
		multiPanel.add("text",       textPanel);
		multiPanel.add("binary",     largePanel);

		//------------------------------------------------------------------------
		//--- put all together

		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x",   toolBar);
		add("0,1,x,x", multiPanel);

		btnBack.setSelected(true);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API method
	//---
	//---------------------------------------------------------------------------

	public void setData(ResultSetEditor rse, int row)
	{
		rsEditor = rse;
		editRow  = row;
	}

	//---------------------------------------------------------------------------

	public void setDataEditor(DataEditorPanel dep)
	{
		dePanel = dep;
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		btnBack.setSelected(true);
		btnRefresh.setEnabled(false);
		btnUpdate.setEnabled(false);

		if (node == null)
		{
			multiPanel.show("blank");
		}
		else
		{
			try
			{
				currCol = node.getParent().getIndex(node);

				SqlType sqlType = rsEditor.getSqlType(currCol);

				if (sqlType.isNumber() || sqlType.isTemporalType())
					multiPanel.show("simple");

				else if (sqlType.isString() || sqlType.isLongVarChar() || sqlType.isClob())
					multiPanel.show("text");

				else if (sqlType.isBinaryType() || sqlType.isBlob())
					multiPanel.show("binary");

				else
					multiPanel.show("nothandled");

				btnRefresh.setEnabled(true);
				handle_refresh();
				return;
			}
			catch(Exception e)   
			{ 
				Dialogs.showException(e);    
			}

			multiPanel.show("blank");
		}
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node) {}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("back"))
		{
			dePanel.showTableEditor();
			return;
		}

		GuiUtil.setWaitCursor(this, true);

		try
		{
			if (cmd.equals("refresh")) handle_refresh();
			if (cmd.equals("update"))  handle_update();
		}
		catch(Exception ex) 
		{ 
			Dialogs.showException(ex);    
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------
	//--- REFRESH
	//---------------------------------------------------------------------------

	public void handle_refresh() throws SQLException, IOException
	{
		SqlType sqlType = rsEditor.getSqlType(currCol);

		//------------------------------------------------------------------------

		if (sqlType.isNumber() || sqlType.isTemporalType())
			simplePanel.setValue(rsEditor.getValueAt(editRow, currCol));

		//------------------------------------------------------------------------

		else if (sqlType.isString())
			textPanel.setValue((String) rsEditor.getValueAt(editRow, currCol));

		//------------------------------------------------------------------------
		//--- must be separated from previous
		//--- some DBMS don't have the getCharacterStream method

		else if (sqlType.isLongVarChar())
			textPanel.setValue(rsEditor.getLongStringAt(editRow, currCol));

		//------------------------------------------------------------------------

		else if (sqlType.isBinaryType())
			largePanel.setValue(rsEditor.getBinaryValueAt(editRow, currCol));

		//------------------------------------------------------------------------

		else if (sqlType.isBlob())
			largePanel.setValue(rsEditor.getBlobAt(editRow, currCol));

		//------------------------------------------------------------------------

		else if (sqlType.isClob())
			textPanel.setValue(rsEditor.getClobAt(editRow, currCol));
	}

	//---------------------------------------------------------------------------
	//--- UPDATE
	//---------------------------------------------------------------------------

	public void handle_update() throws SQLException, IOException
	{
		String sSimple = simplePanel.getValue();
		String sText   = textPanel.getValue();

		SqlType sqlType = rsEditor.getSqlType(currCol);

		//------------------------------------------------------------------------

		if (sqlType.isInteger())
		{
			try
			{
				rsEditor.setValueAt(new Long(sSimple), editRow, currCol);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"Value must be an integer",
						"Bad Number", JOptionPane.WARNING_MESSAGE);
			}
		}

		//------------------------------------------------------------------------

		else if (sqlType.isReal())
		{
			try
			{
				rsEditor.setValueAt(new Double(sSimple), editRow, currCol);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"Value must be a real number",
						"Bad Number", JOptionPane.WARNING_MESSAGE);
			}
		}

		//------------------------------------------------------------------------

		else if (sqlType.isTemporalType())
			rsEditor.setValueAt(sSimple, editRow, currCol);

		//------------------------------------------------------------------------

		else if (sqlType.isString())
			rsEditor.setValueAt(sText, editRow, currCol);

		//------------------------------------------------------------------------
		//--- this line cannot be merged with the previous one because
		//--- the previous one updates the grid also
		//--- furthermore, some DBMS don't have the setCharacterStream method

		else if (sqlType.isLongVarChar())
			rsEditor.setLongStringAt(sText, editRow, currCol);

		//------------------------------------------------------------------------

		else if (sqlType.isBinaryType()) {}

		//------------------------------------------------------------------------

		else if (sqlType.isBlob()) {}

		//------------------------------------------------------------------------

		else if (sqlType.isClob())
			rsEditor.setClobAt(sText, editRow, currCol);

		btnUpdate.setEnabled(false);
	}

	//---------------------------------------------------------------------------
	//---
	//---   TextArea / TextField listener
	//---
	//---------------------------------------------------------------------------

	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e){}

	//---------------------------------------------------------------------------

	public void keyTyped(KeyEvent e)
	{
		btnUpdate.setEnabled(true);
	}
}

//==============================================================================
