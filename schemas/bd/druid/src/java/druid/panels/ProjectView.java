//==============================================================================
//===
//===   ProjectView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.ProgressDialog;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;
import org.dlib.gui.treeview.DndRuleManager;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewDndHandler;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.DataLib;
import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.jdbc.JdbcConnection;
import druid.core.jdbc.JdbcExport;
import druid.core.jdbc.JdbcLib;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.ProjectNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.dialogs.preview.CodePreviewDialog;
import druid.dialogs.preview.SqlPreviewDialog;
import druid.dialogs.wizards.dbwizard.DbWizard;
import druid.interfaces.SqlGenModule;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class ProjectView extends JPanel implements PopupGenerator, ActionListener,
																	TreeViewSelListener
{
	protected DataModel    dataModel;
	protected AbstractNode clipNode;
	private DatabaseNode clipDbNode;
	private TreeViewNode currNode;

	protected TreeView   tree    = new TreeView();
	private JTextField txtFind = new TTextField();

	private DndRuleManager dndManager = new PrivateDndRuleManager(ImageFactory.NODROP);

	private SqlPreviewDialog  sqlDlg;
	private CodePreviewDialog codeDlg;

	private static final String REBUILD_DB  = "pop_dbRebuild";
	private static final String REBUILD_ENT = "pop_entRebuild";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ProjectView()
	{
		setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new TreeViewRenderer());
		tree.setRootNode(new ProjectNode());
		tree.setDndHandler(dndManager);

		txtFind.setActionCommand("find");
		txtFind.addActionListener(this);

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
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		boolean db       = clipNode instanceof DatabaseNode;
		boolean folder   = clipNode instanceof FolderNode;
		boolean table    = clipNode instanceof TableNode;
		boolean view     = clipNode instanceof ViewNode;
		boolean proc     = clipNode instanceof ProcedureNode;
		boolean func     = clipNode instanceof FunctionNode;
		boolean field    = clipNode instanceof FieldNode;
		boolean sequence = clipNode instanceof SequenceNode;
		boolean notes    = clipNode instanceof NotesNode;

		boolean dbPaste = folder || table || view || proc || func || sequence || notes;

		//------------------------------------------------------------------------

		boolean isDatabaseNode = node instanceof DatabaseNode;
		boolean isFolderNode   = node instanceof FolderNode;
		boolean isTableNode    = node instanceof TableNode;
		boolean isViewNode     = node instanceof ViewNode;
		boolean isProcedureNode= node instanceof ProcedureNode;
		boolean isFunctionNode = node instanceof FunctionNode;
		boolean isFieldNode    = node instanceof FieldNode;
		boolean isSequenceNode = node instanceof SequenceNode;
		boolean isNotesNode    = node instanceof NotesNode;

		boolean connected = false;

		//------------------------------------------------------------------------
		//--- check if a node is selected

		if (node == null)
		{
			popup.add(MenuFactory.createItem("pop_addDb", "Add Database...", this));
			popup.add(MenuFactory.createItem("pop_paste", "Paste", this, db));
		}
		else
		{
			connected = ((AbstractNode)node).getDatabase().getJdbcConnection().isConnected();
		}

		//------------------------------------------------------------------------
		//--- database or folder selected

		if (isDatabaseNode || isFolderNode)
		{
			popup.add(MenuFactory.createItem("pop_addFolder", "Add folder",    this));
			popup.add(MenuFactory.createItem("pop_addTable",  "Add table",     this));
			popup.add(MenuFactory.createItem("pop_addView",   "Add view",      this));
			popup.add(MenuFactory.createItem("pop_addProc",   "Add procedure", this));
			popup.add(MenuFactory.createItem("pop_addFunc",   "Add function",  this));
			popup.add(MenuFactory.createItem("pop_addSeq",    "Add sequence",  this));
			popup.add(MenuFactory.createItem("pop_addNotes",  "Add notes",     this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_del",       "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",       "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy",      "Copy",  this));
			popup.add(MenuFactory.createItem("pop_paste",     "Paste", this, dbPaste));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_expColl",   "Expand / collapse", this));
			popup.add(MenuFactory.createItem("pop_sort",      "Sort children",     this));

			popup.addSeparator();

			JMenu menu = MenuFactory.createMenu("Rebuild in jdbc DB");

			menu.setEnabled(connected);
			popup.add(menu);
			JdbcLib.fillRebuildMenu(menu, (AbstractNode) node, REBUILD_DB, this);
		}

		//------------------------------------------------------------------------
		//--- table selected

		if (isTableNode)
		{
			popup.add(MenuFactory.createItem("pop_addField", "Add field", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_del",       "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",       "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy",      "Copy",  this));
			popup.add(MenuFactory.createItem("pop_paste",     "Paste", this, field));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_expColl",   "Expand / Collapse", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_sqlPreview",  "Sql preview...",     this));
			popup.add(MenuFactory.createItem("pop_codePreview", "Code preview...",    this));

			JMenu menu = MenuFactory.createMenu("Rebuild in jdbc DB");

			menu.setEnabled(connected);
			popup.add(menu);
			JdbcLib.fillRebuildMenu(menu, (AbstractNode) node, REBUILD_ENT, this);
		}

		//------------------------------------------------------------------------
		//--- field or view selected

		if (isFieldNode || isViewNode || isProcedureNode || isFunctionNode || isSequenceNode || isNotesNode)
		{
			popup.add(MenuFactory.createItem("pop_del",  "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",  "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy", "Copy",  this));

			if (isViewNode || isProcedureNode || isFunctionNode || isSequenceNode)
			{
				popup.addSeparator();
				popup.add(MenuFactory.createItem("pop_sqlPreview", "Sql preview...",     this));

				JMenu menu = MenuFactory.createMenu("Rebuild in jdbc DB");

				menu.setEnabled(connected);
				popup.add(menu);
				JdbcLib.fillRebuildMenu(menu, (AbstractNode) node, REBUILD_ENT, this);
			}
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.PROJECT);

		return popup;
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
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		//--- we need to store GUI's data into internal structs
		//--- because several pop_xxx methods require fresh data

		dataModel.saveDataToNode(currNode);

		//--- handle popup menus ---

		if (cmd.equals("pop_addDb"))          pop_addDb();
		if (cmd.equals("pop_addFolder"))      pop_addFolder();
		if (cmd.equals("pop_addTable"))       pop_addTable();
		if (cmd.equals("pop_addView"))        pop_addView();
		if (cmd.equals("pop_addProc"))        pop_addProc();
		if (cmd.equals("pop_addFunc"))        pop_addFunc();
		if (cmd.equals("pop_addField"))       pop_addField();
		if (cmd.equals("pop_addSeq"))         pop_addSequence();
		if (cmd.equals("pop_addNotes"))       pop_addNotes();

		if (cmd.equals("pop_del"))            pop_del(false);

		if (cmd.equals("pop_copy"))           pop_copy();
		if (cmd.equals("pop_cut"))            pop_del(true);
		if (cmd.equals("pop_paste"))          pop_paste();

		if (cmd.equals("pop_expColl"))        pop_expColl();
		if (cmd.equals("pop_sort"))           pop_sort();

		if (cmd.equals("pop_sqlPreview"))     pop_sqlPreview();
		if (cmd.equals("pop_codePreview"))    pop_codePreview();

		if (cmd.startsWith(REBUILD_ENT))
		{
			String       id  = cmd.substring(REBUILD_ENT.length());
			SqlGenModule mod = (SqlGenModule) ModuleManager.getModule(SqlGenModule.class, id);

			pop_entRebuild(mod);
		}

		if (cmd.startsWith(REBUILD_DB))
		{
			String      id   = cmd.substring(REBUILD_DB.length());
			SqlGenModule mod = (SqlGenModule) ModuleManager.getModule(SqlGenModule.class, id);

			pop_dbRebuild(mod);
		}

		if (cmd.equals("find"))
			DataLib.find(tree, txtFind.getText());

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

	private void pop_addDb()
	{
		//--- DataTracker is changed by the wizard

		new DbWizard(GuiUtil.getFrame(this), getProjectNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addFolder()
	{
		addNode(new FolderNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addTable()
	{
		addNode(new TableNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addView()
	{
		addNode(new ViewNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addProc()
	{
		addNode(new ProcedureNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addFunc()
	{
		addNode(new FunctionNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addField()
	{
		TreeViewNode parent = tree.getSelectedNode();
		FieldNode    node   = new FieldNode();

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		DataLib.syncField(node.getDatabase(), node);
		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_addSequence()
	{
		addNode(new SequenceNode());
	}

	//---------------------------------------------------------------------------

	private void pop_addNotes()
	{
		addNode(new NotesNode());
	}

	//---------------------------------------------------------------------------

	private void pop_expColl()
	{
		TreeViewNode node = tree.getSelectedNode();
		boolean      exp  = node.isExpanded();

		for(int i=0; i<node.getChildCount(); i++)
			node.getChild(i).expand(!exp);

		node.expand(!exp);
	}

	//---------------------------------------------------------------------------

	private void pop_sort()
	{
		tree.getSelectedNode().sortChildren();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_del(boolean cut)
	{
		AbstractNode node = (AbstractNode) tree.getSelectedNode();

		if (cut)
		{
			clipNode   =  node;
			clipDbNode =  node.getDatabase();
		}

		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(node, true);
	}

	//---------------------------------------------------------------------------

	protected void pop_copy()
	{
		AbstractNode node = (AbstractNode) tree.getSelectedNode();

		clipNode   = (AbstractNode) node.duplicate();
		clipDbNode = node.getDatabase();

		//--- id remapping

		Hashtable mapping = DataLib.remapIds(clipNode, true);

		DataLib.remapFKeys(clipNode, mapping);

		if (clipNode instanceof DatabaseNode)
			DataLib.remapERViews(((DatabaseNode) clipNode).erViews, mapping);
	}

	//---------------------------------------------------------------------------

	protected void pop_paste()
	{
		TreeViewNode node  = tree.getSelectedNode();
		AbstractNode child = clipNode;

		clipNode = (AbstractNode) clipNode.duplicate();

		//--- id remapping

		Hashtable mapping = DataLib.remapIds(clipNode, true);

		DataLib.remapFKeys(clipNode, mapping);

		if (clipNode instanceof DatabaseNode)
			DataLib.remapERViews(((DatabaseNode) clipNode).erViews, mapping);

		//--- if none is selected -> select root node

		if (node == null)
			node = tree.getRootNode();

		//--- adds child and making it visible

		node.addChild(child, false);

		//--- remap fields and create datatypes if a node is pasted into a different db

		if (!(child instanceof DatabaseNode))
		{
			DatabaseNode srcDb = clipDbNode;
			DatabaseNode desDb = child.getDatabase();

			if (srcDb != desDb)
				DataLib.migrateNodes(child, srcDb, desDb);
		}

		//--- we must select the node AFTER remapping it
		//--- because the remap method may change its data
		child.select();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_sqlPreview()
	{
		AbstractNode node = (AbstractNode) tree.getSelectedNode();

		if (sqlDlg == null)
			sqlDlg = new SqlPreviewDialog(GuiUtil.getFrame(this));

		sqlDlg.run(node);
	}

	//---------------------------------------------------------------------------

	private void pop_codePreview()
	{
		AbstractNode node = (AbstractNode)tree.getSelectedNode();

		if (codeDlg == null)
			codeDlg = new CodePreviewDialog(GuiUtil.getFrame(this));

		codeDlg.run(node);
	}

	//---------------------------------------------------------------------------

	private void pop_entRebuild(SqlGenModule mod)
	{
		AbstractNode   node     = (AbstractNode)tree.getSelectedNode();
		JdbcConnection jdbcConn = node.getDatabase().getJdbcConnection();

		GuiUtil.setWaitCursor(this, true);

		try
		{
			JdbcExport.rebuildEntity(jdbcConn, mod, node);
		}
		catch(Exception e)
		{
			Dialogs.showOperationAborted(this, e.getMessage());
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void pop_dbRebuild(final SqlGenModule mod)
	{
		final AbstractNode node = (AbstractNode)tree.getSelectedNode();

		String title = "Rebuilding ";

		if (node instanceof DatabaseNode)	title += "Database...";
			else 										title += "Folder...";

		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(this), title);

		final Exception exc[] = new Exception[1];

		//------------------------------------------------------------------------

		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					JdbcExport.rebuildDatabase(progrDial, mod, node);
				}
				catch(Exception e)
				{
					exc[0] = e;
				}

				progrDial.stop();
			}
		};

		//------------------------------------------------------------------------

		progrDial.run(run);

		if (exc[0] != null)
			Dialogs.showOperationAborted(this, exc[0].getMessage());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public ProjectNode getProjectNode()
	{
		return (ProjectNode) tree.getRootNode();
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode()
	{
		dataModel.saveDataToNode(currNode);
	}

	//---------------------------------------------------------------------------

	public void setProject(ProjectNode project)
	{
		currNode = null;
		tree.setRootNode(project);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void addNode(AbstractNode node)
	{
		TreeViewNode parent = tree.getSelectedNode();

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void setupDndRules()
	{
		dndManager.addRule(FieldNode.class, FieldNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FieldNode.class, TableNode.class,      TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(TableNode.class, TableNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, ViewNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, ProcedureNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, FunctionNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, SequenceNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, NotesNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(TableNode.class, FolderNode.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(TableNode.class, DatabaseNode.class,   TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(FolderNode.class, TableNode.class,     TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, ViewNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, ProcedureNode.class, TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, FunctionNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, SequenceNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, NotesNode.class,     TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FolderNode.class, FolderNode.class,    TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(FolderNode.class, DatabaseNode.class,  TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(ViewNode.class, TableNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, ViewNode.class,        TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, ProcedureNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, FunctionNode.class,    TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, SequenceNode.class,    TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, NotesNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ViewNode.class, FolderNode.class,      TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ViewNode.class, DatabaseNode.class,    TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(ProcedureNode.class, TableNode.class,     TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, ViewNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, ProcedureNode.class, TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, FunctionNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, SequenceNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, NotesNode.class,     TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(ProcedureNode.class, FolderNode.class,    TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ProcedureNode.class, DatabaseNode.class,  TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(FunctionNode.class, TableNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, ViewNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, ProcedureNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, FunctionNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, SequenceNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, NotesNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(FunctionNode.class, FolderNode.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(FunctionNode.class, DatabaseNode.class,   TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(SequenceNode.class, TableNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, ViewNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, ProcedureNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, FunctionNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, SequenceNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, NotesNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(SequenceNode.class, FolderNode.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(SequenceNode.class, DatabaseNode.class,   TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(NotesNode.class, TableNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, ViewNode.class,       TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, ProcedureNode.class,  TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, FunctionNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, SequenceNode.class,   TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, NotesNode.class,      TreeViewDndHandler.ACCEPT_BEFORE);
		dndManager.addRule(NotesNode.class, FolderNode.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(NotesNode.class, DatabaseNode.class,   TreeViewDndHandler.ACCEPT_INSIDE);

		dndManager.addRule(DatabaseNode.class, DatabaseNode.class, TreeViewDndHandler.ACCEPT_BEFORE);
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
			if (tree.getSelectedNode() != null)
				pop_del(true);
		}
	}

	//---------------------------------------------------------------------------

	private class Copy extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (tree.getSelectedNode() != null)
				pop_copy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			boolean db       = clipNode instanceof DatabaseNode;
			boolean folder   = clipNode instanceof FolderNode;
			boolean table    = clipNode instanceof TableNode;
			boolean view     = clipNode instanceof ViewNode;
			boolean proc     = clipNode instanceof ProcedureNode;
			boolean func     = clipNode instanceof FunctionNode;
			boolean field    = clipNode instanceof FieldNode;
			boolean sequence = clipNode instanceof SequenceNode;
			boolean notes    = clipNode instanceof NotesNode;

			boolean dbPaste = folder || table || view || proc || func || sequence || notes;

			//------------------------------------------------------------------------

			boolean isDatabaseNode = node instanceof DatabaseNode;
			boolean isFolderNode   = node instanceof FolderNode;
			boolean isTableNode    = node instanceof TableNode;

			//------------------------------------------------------------------------

			if (node == null)
			{
				if (db)
					pop_paste();
			}

			else if (isDatabaseNode || isFolderNode)
			{
				if (dbPaste)
					pop_paste();
			}

			else if (isTableNode)
			{
				if (field)
					pop_paste();
			}
		}
	}

//==============================================================================

private class PrivateDndRuleManager extends DndRuleManager
{
	public PrivateDndRuleManager(Cursor c)
	{
		super(c);
	}

	//---------------------------------------------------------------------------

	public void handleDrop(TreeViewNode source, TreeViewNode dest)
	{
		AbstractNode node = (AbstractNode) source;

		DatabaseNode srcDB = node.getDatabase();
		super.handleDrop(source, dest);
		DatabaseNode desDB = node.getDatabase();

		if (srcDB != desDB)
		{
			DataLib.remapFKeys(node, DataLib.remapIds(node, true));
			DataLib.migrateNodes(node, srcDB, desDB);

			dataModel.saveDataToNode(node);
			dataModel.setCurrentNode(node);
		}
	}
}

//==============================================================================
}
