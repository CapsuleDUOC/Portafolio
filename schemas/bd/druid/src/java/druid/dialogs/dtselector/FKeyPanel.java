//==============================================================================
//===
//===   FKeyPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.dtselector;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.data.FieldNode;

//==============================================================================

public class FKeyPanel extends TPanel implements ItemListener
{
	private TComboBox tcbMatch  = new TComboBox();
	private TComboBox tcbUpdate = new TComboBox();
	private TComboBox tcbDelete = new TComboBox();

	private FieldNode fieldNode;

	//---------------------------------------------------------------------------

	public FKeyPanel()
	{
		super("Foreign Key Flags");

		FlexLayout flexL = new FlexLayout(2, 3, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Match Type"));
		add("0,1", new TLabel("On Update"));
		add("0,2", new TLabel("On Delete"));

		add("1,0,x", tcbMatch);
		add("1,1,x", tcbUpdate);
		add("1,2,x", tcbDelete);

		tcbMatch.addItem(FieldNode.FULL,    "Full");
		tcbMatch.addItem(FieldNode.PARTIAL, "Partial");
		tcbMatch.addItem(FieldNode.SIMPLE,  "Simple");

		tcbUpdate.addItem(FieldNode.NOACTION,   "No Action");
		tcbUpdate.addItem(FieldNode.CASCADE,    "Cascade");
		tcbUpdate.addItem(FieldNode.SETNULL,    "Set Null");
		tcbUpdate.addItem(FieldNode.SETDEFAULT, "Set Default");
		tcbUpdate.addItem(FieldNode.RESTRICT,   "Restrict");

		tcbDelete.addItem(FieldNode.NOACTION,   "No Action");
		tcbDelete.addItem(FieldNode.CASCADE,    "Cascade");
		tcbDelete.addItem(FieldNode.SETNULL,    "Set Null");
		tcbDelete.addItem(FieldNode.SETDEFAULT, "Set Default");
		tcbDelete.addItem(FieldNode.RESTRICT,   "Restrict");

		tcbMatch .addItemListener(this);
		tcbUpdate.addItemListener(this);
		tcbDelete.addItemListener(this);

		tcbMatch .setActionCommand("match");
		tcbUpdate.setActionCommand("update");
		tcbDelete.setActionCommand("delete");
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldNode field)
	{
		fieldNode = field;

		tcbMatch .setSelectedKey(field.attrSet.getString("matchType"));
		tcbUpdate.setSelectedKey(field.attrSet.getString("onUpdate"));
		tcbDelete.setSelectedKey(field.attrSet.getString("onDelete"));
	}

	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		String cmd = ((TComboBox)e.getSource()).getActionCommand();

		//--- the following if are necessary because the setSelectedKey method
		//--- executes the listeners immediatly

		if (cmd.equals("match"))
			fieldNode.attrSet.setString("matchType", tcbMatch.getSelectedKey());

		if (cmd.equals("update"))
			fieldNode.attrSet.setString("onUpdate", tcbUpdate.getSelectedKey());

		if (cmd.equals("delete"))
			fieldNode.attrSet.setString("onDelete", tcbDelete.getSelectedKey());
	}
}

//==============================================================================
