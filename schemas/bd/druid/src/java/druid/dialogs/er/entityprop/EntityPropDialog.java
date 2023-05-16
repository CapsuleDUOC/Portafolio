//==============================================================================
//===
//===   EntityPropDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.entityprop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import org.dlib.gui.TDialog;
import org.dlib.gui.TTabbedPane;

import druid.core.DataTracker;
import druid.data.er.ErEntity;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class EntityPropDialog extends TDialog
{
	private GeneralPanel genPanel   = new GeneralPanel();
	private TablePanel   tablePanel = new TablePanel();
	private DocEditor    docEditor  = new DocEditor();

	//---------------------------------------------------------------------------

	public EntityPropDialog(Frame frame)
	{
		super(frame, "Entity properties", true);

		TTabbedPane tp = new TTabbedPane();

		tp.addTab("General", genPanel);
		tp.addTab("Tables",  tablePanel);
		tp.addTab("Docs",    docEditor);

		getContentPane().add(tp, BorderLayout.CENTER);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());

		tp.setPreferredSize(new Dimension(614, 350));
		pack();
		setLocationRelativeTo(getParent());
	}

	//---------------------------------------------------------------------------

	public void run(ErEntity erEntity)
	{
		DataTracker.setEnabled(false);

		genPanel.refresh(erEntity);
		tablePanel.refresh(erEntity);
		docEditor.setDoc(erEntity.xmlDoc);

		DataTracker.setEnabled(true);

		show();

		genPanel.store(erEntity);
		docEditor.getDoc(erEntity.xmlDoc);
	}
}

//==============================================================================
