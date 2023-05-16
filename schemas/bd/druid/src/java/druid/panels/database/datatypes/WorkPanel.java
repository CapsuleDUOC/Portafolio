//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.datatypes;


import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.AttribSet;
import druid.core.DataModel;
import druid.core.DataTracker;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.ConstFolder;
import druid.data.datatypes.Domain;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.datatypes.VarFolder;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class WorkPanel extends MultiPanel implements ItemListener, DataModel
{
	private JTextField txtEquiv   = new TTextField(30);
	private JTextField txtSize    = new TTextField();
	private TComboBox  tcbDomain  = new TComboBox();

	private JLabel     lbSize     = new TLabel("Size");

	private TPanel     contPanel  = new TPanel("");
	private MultiPanel multiPanel = new MultiPanel();
	private RangePanel rangePanel = new RangePanel();
	private SetPanel   setPanel   = new SetPanel();

	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		//--- build multipanel

		multiPanel.add("blank", new JPanel());
		multiPanel.add("range", rangePanel);
		multiPanel.add("set",   setPanel);

		//--- put all together

		FlexLayout flexL = new FlexLayout(2, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(3, FlexLayout.EXPAND);
		contPanel.setLayout(flexL);

		contPanel.add("0,0",   new TLabel("DD equiv."));
		contPanel.add("0,1",   lbSize);
		contPanel.add("0,2",   new TLabel("Domain"));

		contPanel.add("1,0,x", txtEquiv);
		contPanel.add("1,1,x", txtSize);
		contPanel.add("1,2,x", tcbDomain);

		contPanel.add("0,3,x,x,2", multiPanel);

		//--- setup TComboBox

		tcbDomain.addItem(Domain.NONE,  "None");
		tcbDomain.addItem(Domain.LOWER, "Lower case text");
		tcbDomain.addItem(Domain.UPPER, "Upper case text");
		tcbDomain.addItem(Domain.RANGE, "Range of values");
		tcbDomain.addItem(Domain.SET,   "Set of values");

		tcbDomain.addItemListener(this);

		//--- build main panel

		add("blank", new JPanel());
		add("main",  contPanel);

		setPreferredSize(new Dimension(600,400));

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		txtEquiv.getDocument().addDocumentListener(sent);
		txtSize.getDocument().addDocumentListener(sent);

		tcbDomain.addItemListener(sent);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ItemListener
	//---
	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		String type = tcbDomain.getSelectedKey();

		//--- show proper panel

		if (type.equals(Domain.RANGE))
			multiPanel.show("range");

		else if (type.equals(Domain.SET))
			multiPanel.show("set");

		else
			multiPanel.show("blank");
	}

	//---------------------------------------------------------------------------
	//---
	//--- DataModel
	//---
	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		if (node == null || node instanceof ConstFolder || node instanceof VarFolder ||
			 node instanceof VarDataType)
		{
			show("blank");
			return;
		}

		//------------------------------------------------------------------------
		//--- ok, we must show something

		DataTracker.beginDisabledSection();

		show("main");

		AbstractType absNode = (AbstractType) node;

		boolean bSize    = false;

		AttribSet as    = absNode.attrSet;
		AttribSet domAs = absNode.domain.attrSet;

		txtEquiv.setText(as.getString("ddEquiv"));

		//------------------------------------------------------------------------

		if (absNode instanceof ConstDataType)
			contPanel.setTitle("Const Basic Type");

		//------------------------------------------------------------------------

		else if (absNode instanceof ConstAlias)
			contPanel.setTitle("Const Alias");

		//------------------------------------------------------------------------

		else if (absNode instanceof VarAlias)
		{
			contPanel.setTitle("Var Alias");

			bSize = true;

			txtSize.setText(as.getString("size"));
		}

		contPanel.repaint();

		//------------------------------------------------------------------------
		//--- setup components

		lbSize.setVisible(bSize);
		txtSize.setVisible(bSize);

		//------------------------------------------------------------------------
		//--- handle TComboBox / Range / Set

		String type = domAs.getString("type");

		//--- select key and immediately triggers the event (which shows proper panel)

		tcbDomain.setSelectedKey(type);

		rangePanel.set(domAs.getString("minValue"),
							domAs.getString("maxValue"),
							domAs.getBool  ("outRange"));

		setPanel.refresh(absNode.domain.valueList);

		DataTracker.endDisabledSection();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TreeViewNode node)
	{
		if (node == null || node instanceof ConstFolder ||
			 node instanceof VarFolder || node instanceof VarDataType)
			return;

		AbstractType absType = (AbstractType) node;

		AttribSet as = absType.attrSet;

		as.setString("ddEquiv", txtEquiv.getText());

		if (as.contains("size"))
			as.setString("size", txtSize.getText());

		AttribSet domAs = absType.domain.attrSet;

		domAs.setString("type",     tcbDomain.getSelectedKey());
		domAs.setString("minValue", rangePanel.getMin());
		domAs.setString("maxValue", rangePanel.getMax());
		domAs.setBool  ("outRange", rangePanel.isOutside());
	}
}

//==============================================================================
