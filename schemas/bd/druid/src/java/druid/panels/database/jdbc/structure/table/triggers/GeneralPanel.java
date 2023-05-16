//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.triggers;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ROCheckBox;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.AttribSet;
import druid.data.Trigger;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TComboBox   tcbActivat = new TComboBox();
	private TComboBox   tcbForEach = new TComboBox();
	private ROCheckBox  chbInsert  = new ROCheckBox("Insert");
	private ROCheckBox  chbUpdate  = new ROCheckBox("Update");
	private ROCheckBox  chbDelete  = new ROCheckBox("Delete");
	private ROTextField txtWhen    = new ROTextField();
	private SqlTextArea txaCode    = new SqlTextArea();

	private Font font = new Font("Monospaced", Font.PLAIN, 12);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

		FlexLayout flexL = new FlexLayout(3, 6, 4, 4);
		flexL.setColProp(2, FlexLayout.EXPAND);
		flexL.setRowProp(5, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup comboboxes

		tcbActivat.addItem(Trigger.ACTIV_BEFORE,     "Before");
		tcbActivat.addItem(Trigger.ACTIV_AFTER,      "After");
		tcbActivat.addItem(Trigger.ACTIV_INSTEADOF,  "Instead of");

		tcbForEach.addItem(Trigger.FOREACH_ROW,      "Row");
		tcbForEach.addItem(Trigger.FOREACH_STATEMENT,"Statement");

		//------------------------------------------------------------------------
		//--- setup code panel

		TPanel     tp = new TPanel("Trigger code");
		FlexLayout fl = new FlexLayout(1, 1);
		fl.setColProp(0, FlexLayout.EXPAND);
		fl.setRowProp(0, FlexLayout.EXPAND);
		tp.setLayout(fl);

		tp.add("0,0,x,x", txaCode);

		txaCode.setFont(font);
		txaCode.setEditable(false);

		tcbActivat.setEnabled(false);
		tcbForEach.setEnabled(false);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0", new TLabel("Activation"));
		add("0,3", new TLabel("For each"));
		add("0,4", new TLabel("When"));

		add("1,0", tcbActivat);
		add("1,3", tcbForEach);

		add("2,0,x", chbInsert);
		add("2,1,x", chbUpdate);
		add("2,2,x", chbDelete);

		add("1,4,x,c,2", txtWhen);
		add("0,5,x,x,3", tp);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Trigger trigger)
	{
		AttribSet as = trigger.attrSet;

		tcbActivat.setSelectedKey(as.getString("activation"));
		tcbForEach.setSelectedKey(as.getString("forEach"));

		chbInsert.setSelected(as.getBool("onInsert"));
		chbUpdate.setSelected(as.getBool("onUpdate"));
		chbDelete.setSelected(as.getBool("onDelete"));

		txtWhen.setText(as.getString("when"));
		txaCode.setText(as.getString("code"));
	}
}

//==============================================================================
