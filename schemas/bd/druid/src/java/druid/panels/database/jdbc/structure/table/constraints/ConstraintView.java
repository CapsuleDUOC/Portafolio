//==============================================================================
//===
//===   ConstraintView
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.constraints;

import druid.core.DataModel;
import druid.core.jdbc.entities.ConstraintEntity;
import druid.core.modules.ModuleManager;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.SimpleTreeViewRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

//==============================================================================
//--- constraints support

public class ConstraintView extends JPanel implements PopupGenerator, ActionListener,
																	TreeViewSelListener
{
	private DataModel      dataModel;
	private TreeViewNode   currNode;
	private ConstraintEntity  constraintNode;

	protected TreeView tree = new TreeView();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ConstraintView()
	{
		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		tree.addSelectionListener(this);
		tree.setPopupGen(this);
		tree.setCellRenderer(new SimpleTreeViewRenderer(ImageFactory.BOOK_OPEN));
		tree.setRootNode(new ConstraintEntity());

		//--- setup panel

		add("0,0,x,x", tree);

		//--- setup key bindings

		tree.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Delete());
	}

	//---------------------------------------------------------------------------
	//---
	//--- PopupMenu building methods
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode treeNode)
	{
		JPopupMenu popup = new JPopupMenu();

		//------------------------------------------------------------------------
		//--- nothing selected

		if (treeNode != null)
			popup.add(MenuFactory.createItem("pop_drop", "Drop Constraint", this));

		//------------------------------------------------------------------------
		//--- add modules

		if (treeNode == null)
			treeNode = tree.getRootNode();

		ModuleManager.addTreeNodeModules(popup, treeNode, this, TreeNodeModule.JDBC_CONSTRAINT);

		return popup;
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
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

		//--- handle popup menus ---

		if (cmd.equals("pop_drop")) pop_drop();

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

	protected void pop_drop()
	{
		GuiUtil.setWaitCursor(this, true);

		ConstraintEntity node = (ConstraintEntity) tree.getSelectedNode();

		try
		{
			node.getJdbcConnection().getSqlAdapter().dropEntity(node);
			DruidUtil.removeNode(node, false);
		}
		catch(Exception e)
		{
			System.out.println("-->"+e);
			e.printStackTrace();
			Dialogs.showOperationAborted(this, e.getMessage());
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API Methods
	//---
	//---------------------------------------------------------------------------

	public void setConstraintNode(ConstraintEntity node)
	{
		if (constraintNode == node)
			return;

		currNode = null;
		constraintNode = node;
		tree.setRootNode(node);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Key bindings classes
	//---
	//---------------------------------------------------------------------------

	private class Delete extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (tree.getSelectedNode() != null)
				pop_drop();
		}
	}
}

//==============================================================================
