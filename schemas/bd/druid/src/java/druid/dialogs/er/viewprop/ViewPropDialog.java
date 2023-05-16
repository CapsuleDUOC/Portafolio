//==============================================================================
//===
//===   ViewPropDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import org.dlib.gui.TDialog;
import org.dlib.gui.TTabbedPane;

import druid.core.DataTracker;
import druid.data.er.ErView;
import druid.dialogs.er.viewprop.legend.LegendPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.editor.DocEditor;

//==============================================================================

public class ViewPropDialog extends TDialog
{
	private GeneralPanel genPanel    = new GeneralPanel();
	private FontsPanel   fontsPanel  = new FontsPanel();
	private LegendPanel  legendPanel = new LegendPanel();
	private DocEditor    docEditor   = new DocEditor();

	//---------------------------------------------------------------------------

	public ViewPropDialog(Frame frame)
	{
		super(frame, "View properties", true);

		TTabbedPane tp = new TTabbedPane();

		tp.addTab("General",  genPanel);
		tp.addTab("Fonts",    fontsPanel);
		tp.addTab("Legend",   legendPanel);
		tp.addTab("Docs",     docEditor);

		getContentPane().add(tp, BorderLayout.CENTER);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());

		tp.setPreferredSize(new Dimension(614, 350));
		pack();
		setLocationRelativeTo(getParent());
	}

	//---------------------------------------------------------------------------

	public void run(ErView erView)
	{
		DataTracker.setEnabled(false);

		genPanel.refresh(erView);
		fontsPanel.refresh(erView);
		legendPanel.refresh(erView.legend);
		docEditor.setDoc(erView.xmlDoc);

		DataTracker.setEnabled(true);

		show();

		genPanel.store(erView);
		fontsPanel.store(erView);
		legendPanel.store(erView.legend);
		docEditor.getDoc(erView.xmlDoc);
	}
}

//==============================================================================
