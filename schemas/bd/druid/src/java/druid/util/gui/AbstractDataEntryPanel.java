//==============================================================================
//===
//===   AbstractDataEntryPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;

//==============================================================================

public abstract class AbstractDataEntryPanel extends JPanel
															implements  ActionListener,
																			FlexTableSelListener
{
	protected TToolBar  toolBar   = new TToolBar();
	protected FlexTable flexTable = new FlexTable(true);

	protected DefaultFlexTableModel flexModel = new DefaultFlexTableModel();

	protected int iSelectedRow;

	protected Object clipData;

	protected AbstractButton btnNew;
	protected AbstractButton btnDel;
	protected AbstractButton btnCut;
	protected AbstractButton btnCopy;
	protected AbstractButton btnPaste;
	protected AbstractButton btnUp;
	protected AbstractButton btnDown;

	//---------------------------------------------------------------------------

	public AbstractDataEntryPanel()
	{
		setLayout(new BorderLayout(2,2));

		//------------------------------------------------------------------------
		//--- build toolbar

		btnNew = toolBar.add(ImageFactory.NEW,    this, "new",  "Add a new row");
		btnDel = toolBar.add(ImageFactory.DELETE, this, "del",  "Delete selected row");

		toolBar.addSeparator();

		btnCut   = toolBar.add(ImageFactory.CUT,  this, "cut",  "Cut row");
		btnCopy  = toolBar.add(ImageFactory.COPY, this, "copy", "Copy row");
		btnPaste = toolBar.add(ImageFactory.PASTE,this, "paste","Paste row");

		toolBar.addSeparator();

		btnUp   = toolBar.add(ImageFactory.UP,   this, "up",   "Move row up");
		btnDown = toolBar.add(ImageFactory.DOWN, this, "down", "Move row down");

		//------------------------------------------------------------------------
		//--- setup flextable

		flexTable.addSelectionListener(this);
		flexTable.setFlexModel(flexModel);

		//------------------------------------------------------------------------
		//--- put all together

		add(toolBar,   BorderLayout.NORTH);
		add(flexTable, BorderLayout.CENTER);

		//--- setup key bindings

		flexTable.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Del());

		flexTable.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), new Cut());
		flexTable.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), new Copy());
		flexTable.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), new Paste());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

			  if (command.equals("new"))   handleNew();
		else if (command.equals("del"))   handleDel(false);
		else if (command.equals("cut"))   handleDel(true);
		else if (command.equals("copy"))  handleCopy();
		else if (command.equals("paste")) handlePaste();
		else if (command.equals("up"))    handleUp();
		else if (command.equals("down"))  handleDown();
	}

	//---------------------------------------------------------------------------

	protected abstract void handleNew();
	protected abstract void handleDel(boolean cut);
	protected abstract void handleCopy();
	protected abstract void handlePaste();
	protected abstract void handleUp();
	protected abstract void handleDown();

	protected abstract int dataSize();

	//---------------------------------------------------------------------------
	//---
	//---   FlexTableSelListener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		iSelectedRow = e.getSelectedRow();

		if (iSelectedRow == -1)
		{
			btnDel.setEnabled(false);
			btnCut.setEnabled(false);
			btnCopy.setEnabled(false);
			btnPaste.setEnabled(clipData != null);

			btnUp.setEnabled(false);
			btnDown.setEnabled(false);
		}
		else
		{
			btnDel.setEnabled(true);
			btnCut.setEnabled(true);
			btnCopy.setEnabled(true);
			btnPaste.setEnabled(clipData != null);

			btnUp.setEnabled(iSelectedRow != 0);
			btnDown.setEnabled(iSelectedRow != dataSize()-1);
		}
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
