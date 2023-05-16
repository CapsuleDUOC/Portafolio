//==============================================================================
//===
//===   AccountEditor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;

import druid.core.config.jdbc.AccountInfo;
import druid.dialogs.BasicConfigDialog;
import druid.dialogs.accountman.urlcreator.UrlCreator;
import druid.util.gui.ImageFactory;

//==============================================================================

public class AccountEditor extends BasicConfigDialog
{
	private JTextField txtName;
	private JTextField txtUrl;
	private JTextField txtUser;
	private JTextField txtPass;
	private JCheckBox  chbAutocommit;

	private TButton btnWiz;

	private boolean firstInstance = true;

	private UrlCreator urlCreator;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AccountEditor(Frame frame)
	{
		super(frame);

		setTitle("Edit account");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean run(AccountInfo ai)
	{
		//------------------------------------------------------------------------
		//--- refresh data

		txtName.setText(ai.name);
		txtUrl .setText(ai.url);
		txtUser.setText(ai.user);
		txtPass.setText(ai.password);
		chbAutocommit.setSelected(ai.autoCommit);

		txtName.requestFocus();

		if (firstInstance)
		{
			firstInstance = false;
			showDialog();
		}
		else
		{
			clearCancelled();
			setVisible(true);
		}

		//------------------------------------------------------------------------
		//--- store data

		if (!isCancelled())
		{
			ai.name       = txtName.getText();
			ai.url        = txtUrl.getText();
			ai.user       = txtUser.getText();
			ai.password   = txtPass.getText();
			ai.autoCommit = chbAutocommit.isSelected();
		}

		return !isCancelled();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected JComponent getCentralPanel()
	{
		txtName       = new TTextField(25);
		txtUrl        = new TTextField();
		txtUser       = new TTextField();
		txtPass       = new JPasswordField();
		chbAutocommit = new JCheckBox();

		btnWiz = new TButton(ImageFactory.WIZARD, "wizard",  this, "URL manager");

		//--- setup layout

		FlexLayout flexL = new FlexLayout(3,5);
		flexL.setColProp(1, FlexLayout.EXPAND);

		JPanel p = new JPanel();
		p.setLayout(flexL);

		p.add("0,0",   new TLabel("Name"));
		p.add("0,1",   new TLabel("URL"));
		p.add("0,2",   new TLabel("User"));
		p.add("0,3",   new TLabel("Password"));
		p.add("0,4",   new TLabel("Auto commit"));

		p.add("1,0,x,c,2",  txtName);
		p.add("1,1,x",      txtUrl);
		p.add("1,2,x,c,2",  txtUser);
		p.add("1,3,x,c,2",  txtPass);
		p.add("1,4,x",      chbAutocommit);
		p.add("2,1",        btnWiz);

		return p;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String cmd = e.getActionCommand();

		if (cmd.equals("wizard"))
			handleWizard();
	}

	//---------------------------------------------------------------------------

	private void handleWizard()
	{
		if (urlCreator == null)
			urlCreator = new UrlCreator(GuiUtil.getFrame(this));

		String url = urlCreator.run();

		if (url != null)
			txtUrl.setText(url);
	}
}

//==============================================================================
