//==============================================================================
//===
//===   ErTreeView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.er;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;
import org.dlib.gui.print.GraphicPrinter;
import org.dlib.gui.treeview.DndRuleManager;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewDndHandler;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;
import org.dlib.tools.TFileFilter;

import druid.core.DataLib;
import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.config.Config;
import druid.core.er.ErScrView;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.dialogs.er.viewprop.ViewPropDialog;
import druid.dialogs.print.PrintPreviewDialog;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.ErViewRenderer;

//==============================================================================

public class ErTreeView extends JPanel implements PopupGenerator, ActionListener,
																  TreeViewSelListener
{
	private TreeViewNode   currNode;
	protected TreeViewNode   clipNode;
	private DatabaseNode   dbNode;

	protected TreeView   tree    = new TreeView();
	private JTextField txtFind = new TTextField();

	private TButton btnNew;
	private TButton btnCut;
	private TButton btnCopy;
	private TButton btnPaste;
	private TButton btnUp;
	private TButton btnDown;

	private ViewPropDialog     propDial;
	private PrintPreviewDialog printDial;

	private DataModel dataModel;

	private DndRuleManager dndManager = new DndRuleManager(ImageFactory.NODROP);

	private GraphicPrinter printer = new GraphicPrinter();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ErTreeView()
	{
		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

		//------------------------------------------------------------------------
		//--- setup tree

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new ErViewRenderer());
		tree.setRootNode(new TreeViewNode());
		tree.setDndHandler(dndManager);

		//------------------------------------------------------------------------
		//--- setup find textfield

		txtFind.setActionCommand("find");
		txtFind.addActionListener(this);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0,x,x,2", tree);
		add("0,1",       new TLabel("Find"));
		add("1,1,x",     txtFind);

		setupDndRules();

		//--- setup key bindings

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Del());

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), new Cut());
		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), new Copy());
		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), new Paste());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void setDatabaseNode(DatabaseNode node)
	{
		if (dbNode == node)
		{
			//--- ok. If we are here the user did not select a different db
			//--- so we could avoid to set the treeview again
			//--- the problem is that some tables could have been deleted so
			//--- we need to update the er view on

			dataModel.setCurrentNode(tree.getSelectedNode());
			return;
		}

		currNode = null;
		clipNode = null;
		dbNode   = node;
		tree.setRootNode(node.erViews);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel model)
	{
		dataModel = model;
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataModel.saveDataToNode(currNode);
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		dataModel.saveDataToNode(currNode);
		currNode = e.getSelectedNode();
		dataModel.setCurrentNode(currNode);
	}

	//---------------------------------------------------------------------------
	//---
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		boolean paste = (clipNode != null);

		if (node == null)
		{
			popup.add(MenuFactory.createItem("pop_new",   "Add E/R view", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_paste", "Paste",   this, paste));
		}
		else if (node instanceof ErView)
		{
			popup.add(MenuFactory.createItem("pop_del",       "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",       "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy",      "Copy",  this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_prop",      "Properties...",        this));
			popup.add(MenuFactory.createItem("pop_rebuild",   "Rebuild",              this));
			popup.add(MenuFactory.createItem("pop_save",      "Save E/R as image...", this));
			popup.add(MenuFactory.createItem("pop_print",     "Print...",             this));
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.ER);

		return popup;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		dataModel.saveDataToNode(currNode);

		//--- handle popup menus ---

		if (cmd.equals("pop_new"))       pop_new();

		if (cmd.equals("pop_del"))       pop_del(false);
		if (cmd.equals("pop_cut"))       pop_del(true);
		if (cmd.equals("pop_copy"))      pop_copy();
		if (cmd.equals("pop_paste"))     pop_paste();

		if (cmd.equals("pop_prop"))      pop_properties();
		if (cmd.equals("pop_rebuild"))   pop_rebuild();
		if (cmd.equals("pop_save"))      pop_save();
		if (cmd.equals("pop_print"))     pop_print();

		if (cmd.equals("find")) DataLib.find(tree, txtFind.getText());

		//------------------------------------------------------------------------
		//--- dispatch module events

		TreeViewNode node = tree.getSelectedNode();

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.dispatchTreeNodeEvent(GuiUtil.getFrame(this), cmd, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup Methods
	//---
	//---------------------------------------------------------------------------

	private void pop_new()
	{
		TreeViewNode parent = tree.getRootNode();
		TreeViewNode node   = new ErView();

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	//--- cut of E/R view

	private void pop_del(boolean cut)
	{
		TreeViewNode node = tree.getSelectedNode();

		if (cut)
			clipNode = node;
		else
		{
			if (!Dialogs.confirm(this, "Delete ev-view confirmation", "Are you sure you want to delete this er-view?"))
				return;
			
		}
		
		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(node, true);
	}

	//---------------------------------------------------------------------------
	//--- copy of E/R view

	protected void pop_copy()
	{
		clipNode = tree.getSelectedNode().duplicate();

//---	for now, we don't need to remap ids. The only id belongs to the legend color
//--- ((BasicNode)clipNode).remap();
	}

	//---------------------------------------------------------------------------
	//--- paste of E/R view

	protected void pop_paste()
	{
		AbstractNode child = (AbstractNode) clipNode;

		clipNode = clipNode.duplicate();

		//--- add child, selecting it and making it visible
		tree.getRootNode().addChild(child);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_properties()
	{
		TreeViewNode node = tree.getSelectedNode();

		if (propDial == null)
		{
			GuiUtil.setWaitCursor(this, true);
			propDial = new ViewPropDialog(GuiUtil.getFrame(this));
			GuiUtil.setWaitCursor(this, false);
		}

		ErView erNode = (ErView)node;

		propDial.run(erNode);

		dataModel.setCurrentNode(erNode);
	}

	//---------------------------------------------------------------------------

	private void pop_rebuild()
	{
		ErView erView = (ErView) tree.getSelectedNode();

		dataModel.setCurrentNode(erView);
	}

	//---------------------------------------------------------------------------

	private void pop_save()
	{
		JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Save E/R as png image");
		fc.addChoosableFileFilter(new TFileFilter("png", "Portable Network Graphics (PNG)"));

		int res = fc.showDialog(this, "Save");
		if (res == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();

			ErView    currErView = (ErView) tree.getSelectedNode();
			ErScrView erScrView  = new ErScrView(currErView, getGraphics(), true, false);

			try
			{
				ImageIO.write(erScrView.createErImage(), "png", f);
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(this,
						"Received an I/O exception:\n" + e.getMessage(),
						"I/O Exception", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	//---------------------------------------------------------------------------

	private void pop_print()
	{
		if (printDial == null)
			printDial = new PrintPreviewDialog(GuiUtil.getFrame(this));

		printer.setPageSize(Config.erView.print.pageSize);
		printer.setMargins (Config.erView.print.margins);

		ErView currErView = (ErView) tree.getSelectedNode();

		//--- proper print

		try
		{
			printDial.run(printer, currErView, getGraphics());
		}
		catch(Exception e)
		{
			System.out.println("Print exception --> " + e.getMessage());
			e.printStackTrace();
		}

		Config.erView.print.pageSize = printer.getPageSize();
		Config.erView.print.margins  = printer.getMargins();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupDndRules()
	{
		dndManager.addRule(ErView.class,   ErView.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ErEntity.class, ErEntity.class, TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ErEntity.class, ErView.class,   TreeViewDndHandler.ACCEPT_INSIDE);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Key bindings classes
	//---
	//---------------------------------------------------------------------------

	private class Del extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (tree.getSelectedNode() != null)
				pop_del(false);
		}
	}

	//---------------------------------------------------------------------------

	private class Cut extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node instanceof ErView)
				pop_del(true);
		}
	}

	//---------------------------------------------------------------------------

	private class Copy extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node instanceof ErView)
				pop_copy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (clipNode != null)
				pop_paste();
		}
	}
}

//==============================================================================
