//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.notes.general;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;

import druid.data.NotesNode;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.ImageFactory;

//==============================================================================

public class GeneralPanel extends JPanel implements ItemListener
{
	private TComboBox tcbType = new TComboBox();

	private NotesNode notes;
	private boolean   eventEnabled;

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 1);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Type"));
		add("1,0,x", tcbType);

		tcbType.addItem(ImageFactory.INFO,   NotesNode.INFO,   "Info");
		tcbType.addItem(ImageFactory.ALERT,  NotesNode.ALERT,  "Alert");
		tcbType.addItem(ImageFactory.DANGER, NotesNode.DANGER, "Danger");

		tcbType.addItemListener(ChangeSentinel.getInstance());
		tcbType.addItemListener(this);
	}

	//---------------------------------------------------------------------------

	public void refresh(NotesNode node)
	{
		eventEnabled = false;

		notes = node;
		tcbType.setSelectedKey(node.attrSet.getString("type"));

		eventEnabled = true;
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(NotesNode node)
	{
		node.attrSet.setString("type", tcbType.getSelectedKey());
	}

	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		if (eventEnabled)
			if (e.getStateChange() == ItemEvent.SELECTED)
				notes.refreshIcon();
	}
}

//==============================================================================
