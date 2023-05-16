//==============================================================================
//===
//===   BasicWizard
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.TButton;
import org.dlib.gui.TSeparator;

//==============================================================================

public class BasicWizard extends BasicDialog implements ActionListener
{
	private TButton btnPrev   = new TButton("<-- Prev", "prev",   this);
	private TButton btnNext   = new TButton("Next -->", "next",   this);
	private TButton btnFinish = new TButton("Finish",   "finish", this);

	private MultiPanel wizPanel = new MultiPanel();

	private int pageNum  = 0;
	private int currPage = 0;

	//---------------------------------------------------------------------------

	public BasicWizard(Frame f)
	{
		super(f);

		setImage("wand.gif");

		//------------------------------------------------------------------------
		//--- setup buttons' panel. We need this panel because the flexLayout
		//--- doesn't handle multicolumns components

		JPanel bp = new JPanel();
		FlexLayout fL = new FlexLayout(3, 1, 4, 0);
		fL.setColProp(0, FlexLayout.EXPAND);
		bp.setLayout(fL);

		bp.add("0,0,r", btnPrev);
		bp.add("1,0",   btnNext);
		bp.add("2,0",   btnFinish);

		//------------------------------------------------------------------------
		//--- build main panel

		JPanel p = getInnerPanel();

		FlexLayout flexL = new FlexLayout(1, 3, 0, 0);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x,x", wizPanel);
		p.add("0,1,x,c", new TSeparator(TSeparator.HORIZONTAL));
		p.add("0,2,x,c", bp);

		p.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		wizPanel.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));
	}

	//---------------------------------------------------------------------------

	public void showDialog()
	{
		currPage = 0;

		wizPanel.show("" + currPage);

		if (pageNum == 1)
		{
			btnPrev.setVisible(false);
			btnNext.setVisible(false);
			btnFinish.setEnabled(shouldEnableNext(currPage));
		}
		else
		{
			btnPrev.setEnabled(false);
			btnNext.setEnabled(shouldEnableNext(currPage));
			btnFinish.setEnabled(false);
		}
		super.showDialog();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Internal Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("prev"))   handle_prev();
		if (cmd.equals("next"))   handle_next();
		if (cmd.equals("finish")) handle_finish();
	}

	//---------------------------------------------------------------------------

	private void handle_prev()
	{
		currPage--;

		if (currPage == 0)
			btnPrev.setEnabled(false);

		btnNext.setEnabled(shouldEnableNext(currPage));
		btnFinish.setEnabled(false);

		wizPanel.show("" + currPage);
	}

	//---------------------------------------------------------------------------

	private void handle_next()
	{
		currPage++;

		btnPrev.setEnabled(true);

		if (currPage != pageNum-1)
		{
			btnNext.setEnabled(shouldEnableNext(currPage));
		}
		else
		{
			btnNext.setEnabled(false);
			btnFinish.setEnabled(shouldEnableNext(currPage));
		}

		wizPanel.show("" + currPage);
	}

	//---------------------------------------------------------------------------

	private void handle_finish()
	{
		setVisible(false);
		doJob();
	}

	//---------------------------------------------------------------------------
	//---
	//---   API
	//---
	//---------------------------------------------------------------------------

	public JPanel addPage()
	{
		JPanel newPage = new JPanel();
		wizPanel.add("" + pageNum, newPage);
		pageNum++;

		return newPage;
	}

	//---------------------------------------------------------------------------

	public void enableNextButton(boolean yesno)
	{
		if (pageNum == 1)
			btnFinish.setEnabled(yesno);
		else
			btnNext.setEnabled(yesno);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Behavior methods (should be redefined in subclasses)
	//---
	//---------------------------------------------------------------------------

	/** When the wizard shows a page, it first calls this method to know if the
	  * "next" button must be enabled. Sub classes may redefine this method in
	  * order to perform a check on their data to see if the user can proceed
	  * to the next page. If the wizard has only one page this behavior is
	  * applied to the "finish" button.
	  */

	protected boolean shouldEnableNext(int pageNum)
	{
		return true;
	}

	//---------------------------------------------------------------------------

	/** Called when the user presses the "finish" button. Sub classes should
	  * redefine this method in order to perform the operation the wizard was
	  * called for.
	  */

	protected void doJob()
	{
	}
}

//==============================================================================
