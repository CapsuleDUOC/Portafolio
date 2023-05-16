//==============================================================================
//===
//===   PrintPreviewDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.print;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;

import javax.swing.AbstractButton;
import javax.swing.JScrollPane;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.ProgressDialog;
import org.dlib.gui.TButton;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TDialog;
import org.dlib.gui.TToolBar;
import org.dlib.gui.print.GraphicPrinter;
import org.dlib.gui.print.GraphicPrinterAdapter;
import org.dlib.gui.print.PrintPreviewPanel;

import druid.core.config.Config;
import druid.core.er.ErScrView;
import druid.data.er.ErView;
import druid.util.gui.ImageFactory;

//==============================================================================

public class PrintPreviewDialog extends TDialog implements ActionListener, ItemListener
{
	private TToolBar toolBar = new TToolBar();

	private PrintPreviewPanel previewPanel = new PrintPreviewPanel();

	private boolean firstTime = true;

	private TButton btnPrint = new TButton(ImageFactory.PRINT, "print", this, "Print");
	private TButton btnProp  = new TButton(ImageFactory.POPUP, "props", this, "Print properties");

	private AbstractButton btnBW;

	private TComboBox tcbScale = new TComboBox();
	private TComboBox tcbSize  = new TComboBox();

	protected GraphicPrinter printer;

	private ErView   erView;
	private Graphics g;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public PrintPreviewDialog(Frame frame)
	{
		super(frame, "Print Preview", true);

		//---setup toolbar

		toolBar.add(btnPrint);
		toolBar.add(btnProp);
		btnBW = toolBar.add(ImageFactory.BLACK_WHITE, this, "bw", "Black & white printing", true);

		toolBar.addSeparator();
		toolBar.add("Scale");
		toolBar.add(tcbScale);

		toolBar.addSeparator();
		toolBar.add("Preview");
		toolBar.add(tcbSize);

		//--- setup dialog

		JScrollPane scrollPane = new JScrollPane(previewPanel);

		scrollPane.setPreferredSize(new Dimension(500, 400));

		getContentPane().add(toolBar,    BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		//--- init some controls

		btnBW.setSelected(Config.erView.print.blackWhite);

		for(int i=1; i<=15; i++)
			tcbScale.addItem(i*10, i*10+" %");

		for(int i=128; i<=256; i=i+64)
			tcbSize.addItem(i, i+" pixels");

		tcbScale.setSelectedKey((int) (Config.erView.print.scale*100));
		tcbScale.addItemListener(this);

		tcbSize.addItemListener(this);

		previewPanel.setPreviewWidth(128);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void run(GraphicPrinter p, ErView er, Graphics g)
	{
		printer = p;
		erView  = er;

		this.g = g;

		ErScrView erScrView  = new ErScrView(erView, g, true, Config.erView.print.blackWhite);
		printer.setPrinterSource(erScrView);

		printer.setScaleFactor(Config.erView.print.scale);
		previewPanel.preview(printer);

		if (firstTime)
		{
			firstTime = false;
			showDialog();
		}
		else
			setVisible(true);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("print")) handlePrint();
		if (cmd.equals("props")) handleProps();
		if (cmd.equals("bw"))    handleBW();
	}

	//---------------------------------------------------------------------------

	protected void handlePrint()
	{
		final ProgressDialog progDlg = new ProgressDialog(this, "Print status");

		Runnable run = new Runnable()
		{
			public void run()
			{
				printer.setPrinterListener(new GraphicPrinterAdapter()
				{
					public void begin(int totPages)
					{
						progDlg.reset(totPages);
					}

					public void printing(int currPage, int totPages)
					{
						progDlg.advance("Printing page "+currPage+"/"+totPages);
					}
				});

				try
				{
					printer.print();
					progDlg.stop();
				}
				catch (PrinterException e)
				{
					System.out.println("error ->" + e);
				}
			}
		};

		progDlg.run(run);
	}

	//---------------------------------------------------------------------------

	private void handleProps()
	{
		if (printer.showDialog())
			preview();
	}

	//---------------------------------------------------------------------------

	private void handleBW()
	{
		Config.erView.print.blackWhite = btnBW.isSelected();

		ErScrView erScrView  = new ErScrView(erView, g, true, Config.erView.print.blackWhite);
		printer.setPrinterSource(erScrView);

		preview();
	}

	//---------------------------------------------------------------------------
	//---
	//--- ItemListener
	//---
	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			if (e.getSource() == tcbScale)
			{
				Config.erView.print.scale = ((double) tcbScale.getSelectedIntKey()) / 100;
				printer.setScaleFactor(Config.erView.print.scale);
			}
			else
				previewPanel.setPreviewWidth(tcbSize.getSelectedIntKey());

			preview();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void preview()
	{
		GuiUtil.setWaitCursor(this, true);
		previewPanel.preview(printer);
		GuiUtil.setWaitCursor(this, false);
	}
}

//==============================================================================
