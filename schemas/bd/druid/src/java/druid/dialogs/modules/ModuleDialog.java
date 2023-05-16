//==============================================================================
//===
//===   ModulesDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.modules;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JSplitPane;

import org.dlib.gui.TDialog;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

//==============================================================================

public class ModuleDialog extends TDialog implements TreeViewSelListener
{
	private ModuleView  modView  = new ModuleView(this);
	private ModulePanel modPanel = new ModulePanel();

	//---------------------------------------------------------------------------

	public ModuleDialog(Frame frame)
	{
		super(frame, "Loaded modules", true);

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modView, modPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		modView.setMinimumSize(d);
		modPanel.setMinimumSize(d);

		getContentPane().add(p, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		TreeViewNode node = e.getSelectedNode();

		modPanel.setCurrentModule(node == null ? null : node.getUserData());
	}
}

//==============================================================================
