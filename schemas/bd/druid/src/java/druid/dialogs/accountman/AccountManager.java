//==============================================================================
//===
//===   AccountManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TDialog;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableClickListener;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;

import druid.core.config.Config;
import druid.core.config.jdbc.AccountInfo;
import druid.util.gui.ImageFactory;

//==============================================================================

public class AccountManager extends TDialog implements FlexTableSelListener,
																		 FlexTableClickListener,
																		 ActionListener
{
	private TToolBar toolBar = new TToolBar();

	private TButton btnNew   = new TButton(ImageFactory.NEW,    "new",  this, "New account");
    protected TButton btnDel   = new TButton(ImageFactory.DELETE, "del",  this, "Delete account");
    protected TButton btnCut   = new TButton(ImageFactory.CUT,    "cut",  this, "Cut account");
	protected TButton btnCopy  = new TButton(ImageFactory.COPY,   "copy", this, "Copy account");
	protected TButton btnPaste = new TButton(ImageFactory.PASTE,  "paste",this, "Paste account");
	private TButton btnEdit  = new TButton(ImageFactory.LENS,   "edit", this, "Edit account");

	private FlexTable table = new FlexTable();

	private DefaultFlexTableModel model = new DefaultFlexTableModel();

	private int selRow = -1;

	private AccountInfo clipData;

	private boolean firstInstance = true;

	private AccountEditor accEditor;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AccountManager(Frame frame)
	{
		super(frame, "Account manager", true);

		//--- setup flex table

		model.addColumn(null,  10);
		model.addColumn(null, 350);

		for(int i=0; i<Config.jdbc.account.count(); i++)
			append(Config.jdbc.account.getAt(i).name);

		table.addSelectionListener(this);
		table.addClickListener(this);
		table.setPreferredSize(new Dimension(400, 300));
		table.setFlexModel(model);
		table.setShowGrid(false);

		//--- setup toolbar and buttons

		toolBar.add(btnNew);
		toolBar.add(btnDel);
		toolBar.addSeparator();
		toolBar.add(btnCut);
		toolBar.add(btnCopy);
		toolBar.add(btnPaste);
		toolBar.addSeparator();
		toolBar.add(btnEdit);

		btnDel.setEnabled(false);
		btnCut.setEnabled(false);
		btnCopy.setEnabled(false);
		btnPaste.setEnabled(false);
		btnEdit.setEnabled(false);

		//--- setup layout

		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);

		JPanel p = new JPanel();
		p.setLayout(flexL);

		p.add("0,0,x",   toolBar);
		p.add("0,1,x,x", table);

		getContentPane().add(p, BorderLayout.CENTER);

		//--- setup key bindings

		table.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Del());

		table.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), new Cut());
		table.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), new Copy());
		table.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), new Paste());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public AccountInfo run()
	{
		if (firstInstance)
		{
			firstInstance = false;
			showDialog();
		}
		else
		{
			setVisible(true);
			clearCancelled();
		}

		if (isCancelled())
			return null;

		return (selRow == -1) ? null : Config.jdbc.account.getAt(selRow);
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTable Click Listener
	//---
	//---------------------------------------------------------------------------

	public void rowClicked(FlexTableSelEvent e)
	{
		selRow = e.getSelectedRow();

		hide();
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTable Selection Listener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		selRow = e.getSelectedRow();

		btnDel  .setEnabled(selRow != -1);
		btnEdit .setEnabled(selRow != -1);
		btnCut  .setEnabled(selRow != -1);
		btnCopy .setEnabled(selRow != -1);
		btnPaste.setEnabled(clipData != null);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("new"))   handleNew();
		if (cmd.equals("del"))   handleDel(false);
		if (cmd.equals("cut"))   handleDel(true);
		if (cmd.equals("copy"))  handleCopy();
		if (cmd.equals("paste")) handlePaste();
		if (cmd.equals("edit"))  handleEdit();
	}

	//---------------------------------------------------------------------------

	private void handleNew()
	{
		if (accEditor == null)
			accEditor = new AccountEditor(GuiUtil.getFrame(this));

		AccountInfo ai = new AccountInfo();

		if (accEditor.run(ai))
		{
			Config.jdbc.account.add(ai);
			append(ai.name);
			table.updateTable();
			table.selectLastRow();
		}
	}

	//---------------------------------------------------------------------------

	private void handleDel(boolean cut)
	{
		if (cut)
			clipData = Config.jdbc.account.getAt(selRow);

		Config.jdbc.account.removeAt(selRow);
		model.removeRow(selRow);
		table.updateTable();

		int rows = table.getRowCount();

		if (rows == selRow)
			table.clearSelection();

		btnPaste.setEnabled(clipData != null);
	}

	//---------------------------------------------------------------------------

	protected void handleCopy()
	{
		clipData = Config.jdbc.account.getAt(selRow).duplicate();

		btnPaste.setEnabled(true);
	}

	//---------------------------------------------------------------------------

	protected void handlePaste()
	{
		AccountInfo ai = clipData.duplicate();

		Config.jdbc.account.add(ai);
		append(ai.name);
		table.updateTable();
		table.selectLastRow();
	}

	//---------------------------------------------------------------------------

	private void handleEdit()
	{
		if (accEditor == null)
			accEditor = new AccountEditor(GuiUtil.getFrame(this));

		AccountInfo ai = Config.jdbc.account.getAt(selRow);

		if (accEditor.run(ai))
		{
			model.setValueAt(ai.name, selRow, 1);
			table.updateTable();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void append(String name)
	{
		Vector v = new Vector();

		v.addElement(ImageFactory.USER);
		v.addElement(name);

		model.addRow(v);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Key bindings classes
	//---
	//---------------------------------------------------------------------------

	private class Del extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (btnDel.isEnabled())
				handleDel(false);
		}
	}

	//---------------------------------------------------------------------------

	private class Cut extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (btnCut.isEnabled())
				handleDel(true);
		}
	}

	//---------------------------------------------------------------------------

	private class Copy extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (btnCopy.isEnabled())
				handleCopy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (btnPaste.isEnabled())
				handlePaste();
		}
	}
}

//==============================================================================
