//==============================================================================
//===
//===   HelpNavigator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//=============================================================================

package druid.dialogs.helpnavigator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TToolBar;
import org.dlib.gui.html.HtmlPanel;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.config.Config;
import druid.util.gui.ImageFactory;
import druid.util.gui.TitleLabel;

//=============================================================================

public class HelpNavigator extends JFrame implements ActionListener, TreeViewSelListener
{
	private TreeView  treeView   = new TreeView();
	private JPanel    rightPanel = new JPanel();

	private TToolBar   toolBar   = new TToolBar();
	private TitleLabel tlCaption = new TitleLabel();
	private HtmlPanel  htmlPanel = new HtmlPanel(true, this);

	private AbstractButton btnPrev;
	private AbstractButton btnNext;
	private AbstractButton btnUp;

	private Vector vHistory    = new Vector();
	private int    iHistoryPos = -1;

	private HelpNode rootNode = new HelpNode();

	//---------------------------------------------------------------------------

	private class InnerWindowAdapter extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			dispose();
		}
	}

	//---------------------------------------------------------------------------

	public HelpNavigator(String file)
	{
		super("Druid's Help Navigator");

		//------------------------------------------------------------------------
		//--- setup split panel

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, rightPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(230);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		treeView.setMinimumSize(d);
		rightPanel.setMinimumSize(d);

		treeView.setPreferredSize(new Dimension(300,500));
		rightPanel.setPreferredSize(new Dimension(450,500));

		//------------------------------------------------------------------------
		//--- setup right panel

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);

		rightPanel.setLayout(flexL);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));

		rightPanel.add("0,0",       toolBar);
		rightPanel.add("1,0,x",     tlCaption);
		rightPanel.add("0,1,x,x,2", htmlPanel);

		btnPrev = toolBar.add(ImageFactory.LEFT,  this, "prev", "Previous page");
		btnNext = toolBar.add(ImageFactory.RIGHT, this, "next", "Next page");
		btnUp   = toolBar.add(ImageFactory.UP,    this, "up",   "One level up");

		btnPrev.setEnabled(false);
		btnNext.setEnabled(false);
		btnUp.setEnabled(false);

		htmlPanel.setActionCommand("link");

		//------------------------------------------------------------------------
		//--- setup treeView

//TODO	rootNode.load(Config.dir.docs + "/html/index.help");
		rootNode.setup();

		treeView.setRootNode(rootNode);
		treeView.setEditable(false);
		treeView.setCellRenderer(new HelpRenderer());
		treeView.addSelectionListener(this);

		for(int i=0; i<rootNode.getChildCount(); i++)
			((TreeViewNode)rootNode.getChildAt(i)).expand(true);

		//------------------------------------------------------------------------
		//--- show given page (if any)

		if (file != null)
		{
			HelpNode node = rootNode.getNodeFromFile(Config.dir.docs + "/html/" + file);

			if (node != null) node.select();
		}

		//------------------------------------------------------------------------
		//--- final steps

		addWindowListener(new InnerWindowAdapter());
		getContentPane().add(p, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
	}

	//---------------------------------------------------------------------------
	//---
	//---   TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		showPage((HelpNode) e.getSelectedNode());
	}

	//---------------------------------------------------------------------------
	//---
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("prev"))     goPrev();
		if (cmd.equals("next"))     goNext();
		if (cmd.equals("up"))       goUp();
		if (cmd.equals("link"))     goLink(e.getSource().toString());
	}

	//---------------------------------------------------------------------------
	//---
	//---   Private methods
	//---
	//---------------------------------------------------------------------------

	private void showPage(HelpNode node)
	{
		setPage(node);
		addEntry(node);
		updateButtons();
	}

	//---------------------------------------------------------------------------

	private void goPrev()
	{
		setPage((HelpNode)vHistory.elementAt(--iHistoryPos));
		updateButtons();
	}

	//---------------------------------------------------------------------------

	private void goNext()
	{
		setPage((HelpNode)vHistory.elementAt(++iHistoryPos));
		updateButtons();
	}

	//---------------------------------------------------------------------------

	private void goUp()
	{
		HelpNode node   = (HelpNode)vHistory.elementAt(iHistoryPos);
		HelpNode parent = (HelpNode)node.getParent();

		setPage(parent);
		addEntry(parent);
		updateButtons();
	}

	//---------------------------------------------------------------------------

	private void goLink(String url)
	{
		HelpNode node = rootNode.getNodeFromFile(url.substring(5));

		if (node != null)
		{
			addEntry(node);
			updateButtons();
			tlCaption.setText(node.name);
		}
	}

	//---------------------------------------------------------------------------

	private void setPage(HelpNode node)
	{
		tlCaption.setText(node.name);
		String fileName = Config.dir.docs + "/html/" + rootNode.getFile(node);
		htmlPanel.setPage("file:" + fileName);
	}

	//---------------------------------------------------------------------------

	private void addEntry(HelpNode node)
	{
		vHistory.addElement(node);

		if (vHistory.size() > 50)
			vHistory.removeElementAt(0);

		iHistoryPos = vHistory.size() -1;
	}

	//---------------------------------------------------------------------------

	private void updateButtons()
	{
		int size = vHistory.size();

		if (size == 0) return;

		btnPrev.setEnabled(iHistoryPos > 0);
		btnNext.setEnabled(iHistoryPos < size-1);

		HelpNode node = (HelpNode)vHistory.elementAt(iHistoryPos);

		btnUp.setEnabled(node.getLevel() >= 2);
	}
}

//=============================================================================
