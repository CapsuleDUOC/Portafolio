//==============================================================================
//===
//===   DatatypesView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.datatypes;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
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
import druid.core.DataTypeLib;
import druid.core.DruidException;
import druid.core.modules.ModuleManager;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.AbstractType;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.ConstFolder;
import druid.data.datatypes.DataTypes;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.datatypes.VarFolder;
import druid.dialogs.dtmerge.DtMergeDialog;
import druid.dialogs.tabledialog.TableDialog;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class DatatypesView extends JPanel implements PopupGenerator, ActionListener,
																	  TreeViewSelListener
{
	private DataModel    dataModel;
	protected TreeViewNode clipNode;
	private TreeViewNode currNode;
	private DatabaseNode dbNode;

	protected TreeView   tree    = new TreeView();
	private JTextField txtFind = new TTextField();

	private DndRuleManager dndManager = new PrivateDndRuleManager(ImageFactory.NODROP);

	//---------------------------------------------------------------------------

	public DatatypesView()
	{
		setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new TreeViewRenderer());
		tree.setRootNode(new DataTypes());
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
	//--- API
	//---
	//---------------------------------------------------------------------------

	public void setDatabaseNode(DatabaseNode node)
	{
		if (dbNode == node) return;

		currNode = null;
		dbNode = node;
		tree.setRootNode(node.dataTypes);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
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

	public JPopupMenu generate(TreeViewNode treeNode)
	{
		JPopupMenu popup = new JPopupMenu();

		boolean paste = (clipNode != null);

		//------------------------------------------------------------------------
		//--- folders

		if (treeNode instanceof ConstFolder || treeNode instanceof VarFolder)
		{
			popup.add(MenuFactory.createItem("pop_addType",   "Add basic type", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_paste",     "Paste", this, paste));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_expcoll",   "Expand / collapse", this));
			popup.add(MenuFactory.createItem("pop_sort",      "Sort children",     this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_removeall", "Remove all", this));
		}

		//------------------------------------------------------------------------
		//--- basic types

		if (treeNode instanceof ConstDataType || treeNode instanceof VarDataType)
		{
			popup.add(MenuFactory.createItem("pop_addType",   "Add alias", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_del",       "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",       "Cut",   this));
			popup.add(MenuFactory.createItem("pop_copy",      "Copy",  this));
			popup.add(MenuFactory.createItem("pop_paste",     "Paste", this, paste));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_sort",      "Sort children", this));

			if (treeNode instanceof ConstDataType)
			{
				popup.addSeparator();
				popup.add(MenuFactory.createItem("pop_merge",    "Merge with...", this));
				popup.add(MenuFactory.createItem("pop_usage",    "Usage...",      this));
			}
		}

		//------------------------------------------------------------------------
		//--- aliases

		if (treeNode instanceof ConstAlias || treeNode instanceof VarAlias)
		{
			popup.add(MenuFactory.createItem("pop_del",      "Remove", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_cut",      "Cut",  this));
			popup.add(MenuFactory.createItem("pop_copy",     "Copy", this));
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_merge",    "Merge with...", this));
			popup.add(MenuFactory.createItem("pop_usage",    "Usage...",      this));
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (treeNode == null)
			treeNode = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, treeNode, this, TreeNodeModule.DATATYPE);

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

		//--- we need to store GUI's data into internal structs
		//--- because several pop_xxx methods require fresh data

		dataModel.saveDataToNode(currNode);

		//--- handle popup menus ---

		if (cmd.equals("pop_addType"))   pop_addType();

		if (cmd.equals("pop_del"))       pop_del(false);

		if (cmd.equals("pop_copy"))      pop_copy();
		if (cmd.equals("pop_cut"))       pop_del(true);
		if (cmd.equals("pop_paste"))     pop_paste();

		if (cmd.equals("pop_expcoll"))   pop_expcoll();
		if (cmd.equals("pop_sort"))      pop_sort();

		if (cmd.equals("pop_removeall")) pop_removeall();
		if (cmd.equals("pop_merge"))     pop_merge();
		if (cmd.equals("pop_usage"))     pop_usage();

		if (cmd.equals("find"))          DataLib.find(tree, txtFind.getText());

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

	private void pop_addType()
	{
		TreeViewNode parent = tree.getSelectedNode();
		TreeViewNode node;

		if (parent instanceof ConstFolder)
			node = new ConstDataType();

		else if (parent instanceof VarFolder)
			node = new VarDataType();

		else if (parent instanceof ConstDataType)
			node = new ConstAlias();

		else if (parent instanceof VarDataType)
			node = new VarAlias();

		else
			throw new DruidException(DruidException.INC_STR, "Unknown node type", parent);

		//--- adds child, selecting it and making it visible
		parent.addChild(node);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	private void pop_expcoll()
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
		TreeViewNode node = tree.getSelectedNode();

		if (cut)
			clipNode = tree.getSelectedNode();

		//--- removes a node, selecting its father (or first brother if any)
		DruidUtil.removeNode(node, true);
	}

	//---------------------------------------------------------------------------

	protected void pop_copy()
	{
		clipNode = tree.getSelectedNode();

		clipNode = clipNode.duplicate();
		DataLib.remapIds((AbstractType) clipNode, false);
	}

	//---------------------------------------------------------------------------

	protected void pop_paste()
	{
		TreeViewNode node  = tree.getSelectedNode();
		AbstractType child = (AbstractType) clipNode;

		clipNode = clipNode.duplicate();
		DataLib.remapIds((AbstractType) clipNode, false);

		child = DataTypeLib.remapDataType(node, child);

		//--- add child, selecting it and making it visible
		node.addChild(child);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public void pop_removeall()
	{
		TreeViewNode node = tree.getSelectedNode();

		node.removeAllChildren();
		node.recalcChildren();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public void pop_merge()
	{
		AbstractType node = (AbstractType) tree.getSelectedNode();

		int oldId = node.attrSet.getInt("id");

		DtMergeDialog dtmd = new DtMergeDialog(GuiUtil.getFrame(this));

		if (dtmd.run(dbNode))
		{
			int newId = dtmd.getId();

			int res = DataTypeLib.mergeDataType(dbNode, oldId, newId);

			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"Changed " + res + " field(s)",
						"Fields Changed", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//---------------------------------------------------------------------------

	public void pop_usage()
	{
		AbstractType node = (AbstractType) tree.getSelectedNode();

		String name = node.attrSet.getString("name");
		int    id   = node.attrSet.getInt("id");

		Vector vFields = DataTypeLib.usage(dbNode, id);

		if (vFields.size() == 0)
		{
			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"The type '" + name + "' is not used",
						"DataType Usage", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			String title = "Datatype used by " + vFields.size() + " field(s)";

			TableDialog td = new TableDialog(GuiUtil.getFrame(this), title);

			td.setClickable(true);

			td.addColumn("Table", 200);
			td.addColumn("Field", 200);

			for(int i=0; i<vFields.size(); i++)
			{
				FieldNode field = (FieldNode) vFields.elementAt(i);
				TableNode table = (TableNode) field.getParent();

				String fName = field.attrSet.getString("name");
				String tName = table.attrSet.getString("name");

				td.append(tName, fName);
			}

			td.showDialog();

			if (!td.isCancelled())
			{
				FieldNode field = (FieldNode) vFields.get(td.getClickedRow());
				TableNode table = (TableNode) field.getParent();

				table.select();
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupDndRules()
	{
		dndManager.addRule(ConstDataType.class, ConstFolder.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstDataType.class, ConstDataType.class, TreeViewDndHandler.ACCEPT_BEFORE);

		dndManager.addRule(ConstDataType.class, VarFolder.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstDataType.class, VarDataType.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstDataType.class, VarAlias.class,      TreeViewDndHandler.ACCEPT_BEFORE);

		//---

		dndManager.addRule(ConstAlias.class,    ConstFolder.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstAlias.class,    ConstDataType.class, TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstAlias.class,    ConstAlias.class,    TreeViewDndHandler.ACCEPT_BEFORE);

		dndManager.addRule(ConstAlias.class,    VarFolder.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstAlias.class,    VarDataType.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(ConstAlias.class,    VarAlias.class,      TreeViewDndHandler.ACCEPT_BEFORE);

		//---

		dndManager.addRule(VarDataType.class,   ConstFolder.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarDataType.class,   ConstDataType.class, TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarDataType.class,   ConstAlias.class,    TreeViewDndHandler.ACCEPT_BEFORE);

		dndManager.addRule(VarDataType.class,   VarFolder.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarDataType.class,   VarDataType.class,   TreeViewDndHandler.ACCEPT_BEFORE);

		//---

		dndManager.addRule(VarAlias.class,      ConstFolder.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarAlias.class,      ConstDataType.class, TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarAlias.class,      ConstAlias.class,    TreeViewDndHandler.ACCEPT_BEFORE);

		dndManager.addRule(VarAlias.class,      VarFolder.class,     TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarAlias.class,      VarDataType.class,   TreeViewDndHandler.ACCEPT_INSIDE);
		dndManager.addRule(VarAlias.class,      VarAlias.class,      TreeViewDndHandler.ACCEPT_BEFORE);
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
			TreeViewNode node = tree.getSelectedNode();

			if (node instanceof ConstDataType || node instanceof ConstAlias ||
				 node instanceof VarDataType   || node instanceof VarAlias)
					pop_del(false);
		}
	}

	//---------------------------------------------------------------------------

	private class Cut extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node instanceof ConstDataType || node instanceof ConstAlias ||
				 node instanceof VarDataType   || node instanceof VarAlias)
					pop_del(true);
		}
	}

	//---------------------------------------------------------------------------

	private class Copy extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			if (node instanceof ConstDataType || node instanceof ConstAlias ||
				 node instanceof VarDataType   || node instanceof VarAlias)
					pop_copy();
		}
	}

	//---------------------------------------------------------------------------

	private class Paste extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			TreeViewNode node = tree.getSelectedNode();

			boolean paste = (clipNode != null);

			if (node instanceof ConstFolder || node instanceof VarFolder)
			{
				if (paste)
					pop_paste();
			}

			else if (node instanceof ConstDataType || node instanceof VarDataType)
			{
				if (paste)
					pop_paste();
			}
		}
	}
}

//==============================================================================

class PrivateDndRuleManager extends DndRuleManager
{
	public PrivateDndRuleManager(Cursor c)
	{
		super(c);
	}

	//---------------------------------------------------------------------------

	public void nodeDropped(TreeViewNode node)
	{
		AbstractType parent = (AbstractType) node.getParent();

		AbstractType child = DataTypeLib.remapDataType(parent, (AbstractType) node);

		//--- check if the datatype has been remapped

		if (child != node)
		{
			int index = parent.getIndex(node);
			parent.removeChild(node);
			parent.insertChild(child, index);
		}
	}
}

//==============================================================================
