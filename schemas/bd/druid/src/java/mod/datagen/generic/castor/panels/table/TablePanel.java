//==============================================================================
//===
//===   TablePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TButton;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.castor.TableSettings;
import druid.util.gui.ImageFactory;
import druid.data.TableNode;
import druid.dialogs.chooser.TableChooserDialog;
import org.dlib.gui.GuiUtil;
import druid.data.DatabaseNode;
import druid.core.DataTracker;

//==============================================================================

public class TablePanel extends JPanel implements ActionListener
{
	private TButton btnExtends = new TButton("?", "extends", this);
	private TButton btnDepends = new TButton("?", "depends", this);

	private TComboBoxGuardian  tcbAccess  = new TComboBoxGuardian("Access");
	private TComboBoxGuardian  tcbKeyGen  = new TComboBoxGuardian("KeyGen");
	private TComboBoxGuardian  tcbCacheTyp= new TComboBoxGuardian("CacheType");
	private TTextFieldGuardian txtCapac   = new TTextFieldGuardian("CacheCap");
	private TCheckBoxGuardian  chbAutoCom = new TCheckBoxGuardian("AutoComplete", "Auto complete");
	private TCheckBoxGuardian  chbVerCon  = new TCheckBoxGuardian("VerifyConstr", "Verify constructable");

	private DatabaseNode  dbNode;
	private TableSettings sett;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TablePanel()
	{
		FlexLayout flexL = new FlexLayout(2, 9);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Extends"));
		add("0,1", new TLabel("Depends"));
		add("0,2", new TLabel("Access"));
		add("0,3", new TLabel("Key gen"));
		add("0,4", new TLabel("Cache type"));
		add("0,5", new TLabel("Cache capacity"));

		add("1,0,x", btnExtends);
		add("1,1,x", btnDepends);
		add("1,2,x", tcbAccess);
		add("1,3,x", tcbKeyGen);
		add("1,4,x", tcbCacheTyp);
		add("1,5,x", txtCapac);

		add("0,7,x,c,2", chbAutoCom);
		add("0,8,x,c,2", chbVerCon);

		//--- setup comboboxes

		tcbAccess.addItem(TableSettings.ACCESS_READONLY,  "Read only");
		tcbAccess.addItem(TableSettings.ACCESS_SHARED,    "Shared");
		tcbAccess.addItem(TableSettings.ACCESS_EXCLUSIVE, "Exclusive");
		tcbAccess.addItem(TableSettings.ACCESS_DBLOCKED,  "DB locked");

		tcbKeyGen.addItem(TableSettings.KEYGEN_NONE,      "None");
		tcbKeyGen.addItem(TableSettings.KEYGEN_MAX,       "'MAX( pk ) + 1' generic algorithm");
		tcbKeyGen.addItem(TableSettings.KEYGEN_HIGHLOW,   "HIGH/LOW generic algorithm");
		tcbKeyGen.addItem(TableSettings.KEYGEN_UUID,      "UUID generic algorithm");
		tcbKeyGen.addItem(TableSettings.KEYGEN_IDENTITY,  "Autoincrement identity fields");
		tcbKeyGen.addItem(TableSettings.KEYGEN_SEQUENCE,  "Sequences");

		tcbCacheTyp.addItem(TableSettings.CACHETYPE_NONE,      "None");
		tcbCacheTyp.addItem(TableSettings.CACHETYPE_COUNTLIM,  "Count limited");
		tcbCacheTyp.addItem(TableSettings.CACHETYPE_TIMELIM,   "Time limited");
		tcbCacheTyp.addItem(TableSettings.CACHETYPE_UNLIMITED, "Unlimited");

		btnExtends.setHorizontalAlignment(SwingConstants.LEFT);
		btnDepends.setHorizontalAlignment(SwingConstants.LEFT);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableSettings s, DatabaseNode node)
	{
		dbNode = node;
		sett   = s;

		tcbAccess  .refresh(s);
		tcbKeyGen  .refresh(s);
		tcbCacheTyp.refresh(s);
		txtCapac   .refresh(s);
		chbAutoCom .refresh(s);
		chbVerCon  .refresh(s);

		setTableName(btnExtends, s.getExtends());
		setTableName(btnDepends, s.getDepends());
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("extends"))
		{
			TableChooserDialog dlg = new TableChooserDialog(GuiUtil.getFrame(this));

			if (dlg.run(dbNode))
			{
				setTableName(btnExtends, dlg.getID());
				sett.setExtends(dlg.getID());
				DataTracker.setDataChanged();
			}
		}

		else if (cmd.equals("depends"))
		{
			TableChooserDialog dlg = new TableChooserDialog(GuiUtil.getFrame(this));

			if (dlg.run(dbNode))
			{
				setTableName(btnDepends, dlg.getID());
				sett.setDepends(dlg.getID());
				DataTracker.setDataChanged();
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setTableName(TButton btn, int id)
	{
		if (id != 0)
		{
			TableNode node = dbNode.getTableByID(id);

			String text = "<DELETED>";

			if (node != null)
				text = node.attrSet.getString("name");

			btn.setIcon(ImageFactory.TABLE);
			btn.setText(text);
		}
		else
		{
			btn.setIcon(null);
			btn.setText("");
		}
	}
}

//==============================================================================
