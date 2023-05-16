//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.triggers.general;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;

import druid.core.AttribSet;
import druid.data.Trigger;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.SqlTextArea;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TComboBox   tcbActivat = new TComboBox();
	private TComboBox   tcbForEach = new TComboBox();
	private TCheckBox   chbInsert  = new TCheckBox("Insert");
	private TCheckBox   chbUpdate  = new TCheckBox("Update");
	private TCheckBox   chbDelete  = new TCheckBox("Delete");
	private JTextField  txtWhen    = new TTextField();
	private JTextField  txtOf      = new TTextField();
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

		FlexLayout flexL = new FlexLayout(6, 6, 4, 4);
		flexL.setColProp(4, FlexLayout.EXPAND);
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

		//------------------------------------------------------------------------
		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		tcbActivat.addItemListener(sent);
		tcbForEach.addItemListener(sent);

		txtWhen.getDocument().addDocumentListener(sent);
		txtOf.getDocument().addDocumentListener(sent);
		txaCode.getDocument().addDocumentListener(sent);

		chbInsert.addItemListener(sent);
		chbUpdate.addItemListener(sent);
		chbDelete.addItemListener(sent);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0", new TLabel("Activation"));
		add("0,3", new TLabel("For each"));
		add("0,4", new TLabel("When"));
		add("3,1", new TLabel("Of"));

		add("1,0", tcbActivat);
		add("1,3", tcbForEach);

		add("2,0,x", chbInsert);
		add("2,1,x", chbUpdate);
		add("2,2,x", chbDelete);

		add("1,4,x,c,5", txtWhen);
		add("4,1,x,c,2", txtOf);
		add("0,5,x,x,6", tp);
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
		txtOf.setText(as.getString("of"));
		txaCode.setText(as.getString("code"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(Trigger trigger)
	{
		AttribSet as = trigger.attrSet;

		as.setString("activation", tcbActivat.getSelectedKey());
		as.setString("forEach",    tcbForEach.getSelectedKey());
		as.setString("when",       txtWhen.getText().trim());
		as.setString("of",         txtOf.getText().trim());
		as.setString("code",       txaCode.getText());

		as.setBool("onInsert",  chbInsert.isSelected());
		as.setBool("onUpdate",  chbUpdate.isSelected());
		as.setBool("onDelete",  chbDelete.isSelected());
	}
}

//==============================================================================
