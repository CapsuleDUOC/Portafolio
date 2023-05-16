//==============================================================================
//===
//===   OutputPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.generation.modules.general;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;

import druid.util.gui.ChangeSentinel;

//==============================================================================

public class OutputPanel extends TPanel implements ActionListener
{
	private JTextField txtOutput = new TTextField();
	private JButton    btnBrowse = new TButton("Browse", "browse",  this);

	private boolean bDirectoryBased;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OutputPanel()
	{
		super(" "); /* This must be non-empty String! */

		FlexLayout flexL = new FlexLayout(2, 1, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", txtOutput);
		add("1,0",   btnBrowse);

		txtOutput.getDocument().addDocumentListener(ChangeSentinel.getInstance());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setOutput(String output)
	{
		txtOutput.setText(output);
	}

	//---------------------------------------------------------------------------

	public void setDirectoryBased(boolean bFlag)
	{
		setTitle("Output " + (bFlag ? "Directory" : "File"));
		bDirectoryBased = bFlag;
	}

	//---------------------------------------------------------------------------

	public String getOutput() { return txtOutput.getText(); }

	//---------------------------------------------------------------------------
	//---
	//--- Listener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command.equals("browse"))
			browse();
	}

	//---------------------------------------------------------------------------

	private void browse()
	{
		String title = "Select a " +
					(bDirectoryBased ? "directory to store data" : "destination file");

		JFileChooser fc = new JFileChooser(txtOutput.getText());

		fc.setDialogTitle(title);

		if (bDirectoryBased)
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int res = fc.showDialog(GuiUtil.getFrame(this), "Ok");

		if (res == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();
			txtOutput.setText(f.getPath());
		}
	}
}

//==============================================================================
