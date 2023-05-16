//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.sequence.general;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;

import druid.core.AttribSet;
import druid.data.SequenceNode;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private JTextField txtIncrem = new TTextField();
	private JTextField txtMinVal = new TTextField();
	private JTextField txtMaxVal = new TTextField();
	private JTextField txtStart  = new TTextField();
	private JTextField txtCache  = new TTextField();

	private TCheckBox  chbCycle  = new TCheckBox();
	private TCheckBox  chbOrder  = new TCheckBox();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 7);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		txtIncrem.getDocument().addDocumentListener(sent);
		txtMinVal.getDocument().addDocumentListener(sent);
		txtMaxVal.getDocument().addDocumentListener(sent);
		txtStart.getDocument().addDocumentListener(sent);
		txtCache.getDocument().addDocumentListener(sent);

		chbCycle.addItemListener(sent);
		chbOrder.addItemListener(sent);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0", new TLabel("Increment"));
		add("0,1", new TLabel("Min value"));
		add("0,2", new TLabel("Max value"));
		add("0,3", new TLabel("Start"));
		add("0,4", new TLabel("Cache"));
		add("0,5", new TLabel("Cycle"));
		add("0,6", new TLabel("Order"));

		add("1,0,x", txtIncrem);
		add("1,1,x", txtMinVal);
		add("1,2,x", txtMaxVal);
		add("1,3,x", txtStart);
		add("1,4,x", txtCache);
		add("1,5,x", chbCycle);
		add("1,6,x", chbOrder);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(SequenceNode node)
	{
		AttribSet as = node.attrSet;

		txtIncrem.setText(as.getString("increment"));
		txtMinVal.setText(as.getString("minValue"));
		txtMaxVal.setText(as.getString("maxValue"));
		txtStart.setText(as.getString("start"));
		txtCache.setText(as.getString("cache"));

		chbCycle.setSelected(as.getBool("cycle"));
		chbOrder.setSelected(as.getBool("order"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(SequenceNode node)
	{
		AttribSet as = node.attrSet;

		as.setString("increment", txtIncrem.getText().trim());
		as.setString("minValue",  txtMinVal.getText().trim());
		as.setString("maxValue",  txtMaxVal.getText().trim());
		as.setString("start",     txtStart.getText().trim());
		as.setString("cache",     txtCache.getText().trim());

		as.setBool("cycle",  chbCycle.isSelected());
		as.setBool("order",  chbOrder.isSelected());
	}
}

//==============================================================================
