//==============================================================================
//===
//===   FieldSelector
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.jdbc.fieldselector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import org.dlib.gui.TDialog;

import druid.core.jdbc.entities.RecordBasedEntity;
import druid.util.jdbc.RecordListPanel;

//==============================================================================

public class FieldSelector extends TDialog
{
	//---------------------------------------------------------------------------

	public FieldSelector(Frame frame, RecordBasedEntity node)
	{
		super(frame, "Choose Fields", true);

		RecordListPanel p = new RecordListPanel();
		p.refresh(node.rlSelectedFields);
		p.setEditable(true);

		getContentPane().add(p, BorderLayout.CENTER);

		p.setPreferredSize(new Dimension(200, 300));
		showDialog();
	}
}

//==============================================================================
