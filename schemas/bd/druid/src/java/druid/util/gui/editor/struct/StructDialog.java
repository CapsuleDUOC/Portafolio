//==============================================================================
//===
//===   StructDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.editor.struct;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Enumeration;

import javax.swing.JSplitPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;

import org.dlib.gui.TDialog;
import org.dlib.gui.TSplitPane;
import org.dlib.gui.html.HtmlToolkit;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.util.gui.editor.struct.work.WorkPanel;

//==============================================================================

public class StructDialog extends TDialog implements  DocumentListener,
																		TreeViewSelListener,
																		CaretListener
{
	private TreeView  docView   = new TreeView(false);
	private WorkPanel workPanel = new WorkPanel();

	private HtmlToolkit toolKit;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public StructDialog(Frame f)
	{
		super(f, "Document structure", false);

		JSplitPane p = new TSplitPane(docView, workPanel);
		getContentPane().add(p, BorderLayout.CENTER);

		docView.setRootVisible(false);
		docView.setCellRenderer(new TreeViewRenderer());
		docView.addSelectionListener(this);

		p.setPreferredSize(new Dimension(550,300));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setHtmlToolkit(HtmlToolkit kit)
	{
		toolKit = kit;

		kit.getDocument().addDocumentListener(this);
		kit.getEditorPane().addCaretListener(this);

		refresh();
	}

	//---------------------------------------------------------------------------

	public void refresh()
	{
		TreeViewNode root = buildTree(retrieveBody(toolKit.getRootElement()));

		docView.setRootNode(root);
		root.expand(true, 10);

		selectCurrentNode(toolKit.getCaretPosition());
	}

	//---------------------------------------------------------------------------
	//---
	//--- DocumentListener
	//---
	//---------------------------------------------------------------------------

	public void changedUpdate(DocumentEvent e)
	{
		if (!isVisible())
			return;

		refresh();
	}

	public void insertUpdate(DocumentEvent e)
	{
		if (!isVisible())
			return;

		refresh();
	}

	public void removeUpdate(DocumentEvent e)
	{
		if (!isVisible())
			return;

		refresh();
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		TreeViewNode node = e.getSelectedNode();
		ElementInfo  info = null;

		if (node != null)
			info = (ElementInfo) node.getUserData();

		workPanel.refresh(info, toolKit);
	}

	//---------------------------------------------------------------------------
	//---
	//--- CaretListener
	//---
	//---------------------------------------------------------------------------

	public void caretUpdate(CaretEvent e)
	{
		if (!isVisible())
			return;

		selectCurrentNode(e.getDot());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private Element retrieveBody(Element e)
	{
		for(int i=0; i<e.getElementCount(); i++)
		{
			Element child = e.getElement(i);

			if (child.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY)
				return child;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	private TreeViewNode buildTree(Element e)
	{
		ElementInfo  info = new ElementInfo(e);
		TreeViewNode node = new TreeViewNode(info.toString(), info);

		for(int i=0; i<e.getElementCount(); i++)
			node.addChild(buildTree(e.getElement(i)));

		return node;
	}

	//---------------------------------------------------------------------------

	private void selectCurrentNode(int pos)
	{
		TreeViewNode root = docView.getRootNode();

		for(Enumeration e=root.depthFirstEnumeration(); e.hasMoreElements();)
		{
			TreeViewNode node = (TreeViewNode) e.nextElement();
			ElementInfo  info = (ElementInfo)  node.getUserData();

			boolean inside = (info.start <= pos) && (pos <= info.end);

			if (node.isLeaf() && inside)
			{
				node.select();
				return;
			}
		}
	}
}

//==============================================================================
