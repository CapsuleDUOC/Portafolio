//==============================================================================
//===
//===   StructureView
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.ProgressDialog;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;
import org.dlib.gui.treeview.PopupGenerator;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;

import druid.core.DataLib;
import druid.core.DataModel;
import druid.core.jdbc.JdbcImport;
import druid.core.jdbc.JdbcLib;
import druid.core.jdbc.JdbcRecord;
import druid.core.jdbc.entities.AbstractEntity;
import druid.core.jdbc.entities.ContainerEntity;
import druid.core.jdbc.entities.DatabaseEntity;
import druid.core.jdbc.entities.FunctionEntity;
import druid.core.jdbc.entities.ProcedureEntity;
import druid.core.jdbc.entities.SchemaEntity;
import druid.core.jdbc.entities.SequenceEntity;
import druid.core.jdbc.entities.SequenceList;
import druid.core.jdbc.entities.SystemList;
import druid.core.jdbc.entities.TableEntity;
import druid.core.jdbc.entities.TableList;
import druid.core.jdbc.entities.ViewEntity;
import druid.core.jdbc.entities.ViewList;
import druid.core.modules.ModuleManager;
import druid.data.DatabaseNode;
import druid.dialogs.jdbc.entityselector.EntitySelector;
import druid.interfaces.TreeNodeModule;
import druid.util.DruidUtil;
import druid.util.gui.Dialogs;
import druid.util.gui.renderers.JdbcTreeRenderer;

//==============================================================================

public class StructureView extends JPanel implements PopupGenerator, ActionListener,
																	  TreeViewSelListener
{
	private TreeView treeView  = new TreeView();
	private JTextField txtFind = new TTextField();

	private DataModel    dataModel;
	private TreeViewNode currNode;
	protected DatabaseNode dbNode;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public StructureView()
	{
		setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

		FlexLayout flexL = new FlexLayout(2,2,4,4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		treeView.addSelectionListener(this);
		treeView.setPopupGen(this);
		treeView.setCellRenderer(new JdbcTreeRenderer());
		treeView.setRootVisible(true);
		treeView.setEditable(false);

		txtFind.setActionCommand("find");
		txtFind.addActionListener(this);

		//--- setup panel

		add("0,0,x,x,2", treeView);
		add("0,1",       new TLabel("Find"));
		add("1,1,x",     txtFind);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode node, boolean force)
	{
		if (!force && node == dbNode)
			return;

		currNode = null;
		dbNode   = node;

		TreeViewNode rootNode = dbNode.getJdbcConnection().getDatabaseEntity();
		treeView.setRootNode(rootNode);
		rootNode.expand(false);
	}

	//---------------------------------------------------------------------------

	public void setDataModel(DataModel dm)
	{
		dataModel = dm;
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		currNode = e.getSelectedNode();

		if (currNode != null)
		{
			Component tree = currNode.getTree();

			GuiUtil.setWaitCursor(tree, true);

			try
			{
				((AbstractEntity) currNode).loadInfo();
			}
			catch(SQLException ex)
			{
				Dialogs.showException(ex);
				currNode = null;
			}

			GuiUtil.setWaitCursor(tree, false);
		}

		dataModel.setCurrentNode(currNode);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Popup Generation
	//---
	//---------------------------------------------------------------------------

	public JPopupMenu generate(TreeViewNode node)
	{
		JPopupMenu popup = new JPopupMenu();

		if (node instanceof DatabaseEntity || node instanceof SchemaEntity)
		{
			//---------------------------------------------------------------------
			//--- database or schema selected

			popup.add(MenuFactory.createItem("pop_refresh",   "Refresh",           this));
			popup.add(MenuFactory.createItem("pop_importObj", "Import entities...",this));
			popup.add(MenuFactory.createItem("pop_exportRec", "Export records...", this));
			popup.add(MenuFactory.createItem("pop_dropObj",   "Drop all objects",  this));
		}

		else if (node != null)
		{
			if (node instanceof ContainerEntity || node instanceof TableList || node instanceof ViewList ||
				 node instanceof SequenceList    || node instanceof SystemList)
			{
				//--- in this case a folder was selected, like tables etc...

				popup.add(MenuFactory.createItem("pop_foImportObj", "Import entities...",this));
				popup.add(MenuFactory.createItem("pop_foExportRec", "Export records...", this));
				popup.add(MenuFactory.createItem("pop_foDropObj",   "Drop all objects",  this));
			}
			else
			{
				boolean enabled = node instanceof TableEntity     ||
										node instanceof ViewEntity      ||
										node instanceof ProcedureEntity ||
										node instanceof FunctionEntity  ||
										node instanceof SequenceEntity;

				popup.add(MenuFactory.createItem("pop_enRefresh", "Refresh", this));
				popup.add(MenuFactory.createItem("pop_enImport",  "Import",  this, enabled));
				popup.add(MenuFactory.createItem("pop_enDrop",    "Drop",    this));
			}
		}

		//------------------------------------------------------------------------
		//--- add modules

		if (node == null)
			node = treeView.getRootNode();

		ModuleManager.addTreeNodeModules(popup, node, this, TreeNodeModule.JDBC_STRUCT);

		return popup;
	}

	//---------------------------------------------------------------------------
	//---
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		dataModel.saveDataToNode(currNode);

		if (cmd.equals("pop_refresh"))     pop_refresh();
		if (cmd.equals("pop_importObj"))   pop_importObj();
		if (cmd.equals("pop_exportRec"))   pop_exportRec();
		if (cmd.equals("pop_dropObj"))     pop_dropObj();

		if (cmd.equals("pop_foImportObj")) pop_importObj();
		if (cmd.equals("pop_foExportRec")) pop_exportRec();
		if (cmd.equals("pop_foDropObj"))   pop_dropObj();

		if (cmd.equals("pop_enRefresh"))   pop_refresh();
		if (cmd.equals("pop_enImport"))    pop_enImport();
		if (cmd.equals("pop_enDrop"))      pop_drop();

		if (cmd.equals("find")) DataLib.find(treeView, txtFind.getText());

		//------------------------------------------------------------------------
		//--- dispatch module events

		TreeViewNode node = treeView.getSelectedNode();

		if (node == null)
			node = treeView.getRootNode();

		ModuleManager.dispatchTreeNodeEvent(GuiUtil.getFrame(this), cmd, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Popup handlers
	//---
	//---------------------------------------------------------------------------

	private void pop_refresh()
	{
		AbstractEntity node = (AbstractEntity) currNode;

		GuiUtil.setWaitCursor(node.getTree(), true);

		try
		{
			node.reset();
			node.loadInfo();
		}
		catch(SQLException e)
		{
			Dialogs.showException(e);

			currNode = null;
		}

		dataModel.setCurrentNode(currNode);
		GuiUtil.setWaitCursor(node.getTree(), false);
	}

	//---------------------------------------------------------------------------

	private void pop_importObj()
	{
		EntitySelector es = new EntitySelector(GuiUtil.getFrame(this));

		if (es.isCancelled())
			return;

		final Vector vImpEnt = JdbcImport.getImportEntities(treeView.getSelectedNode(), es);

		if (vImpEnt == null)
			return;

		if (vImpEnt.size() == 0)
		{
			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"No data to import",
						"Import structure", JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		//------------------------------------------------------------------------

		String title = "Importing structure...";

		final ProgressDialog progrDial = new ProgressDialog(GuiUtil.getFrame(this), title);

		final Exception exc[] = new Exception[1];

		//------------------------------------------------------------------------

		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					JdbcImport.importObjects(progrDial, dbNode, vImpEnt);
				}
				catch(Exception e)
				{
					exc[0] = e;
				}

				progrDial.stop();
			}
		};

		//------------------------------------------------------------------------

		GuiUtil.setWaitCursor(this, true);
		progrDial.run(run);
		GuiUtil.setWaitCursor(this, false);

		if (exc[0] != null)
			Dialogs.showOperationAborted(this, exc[0].getMessage());
	}

	//---------------------------------------------------------------------------

	private void pop_exportRec()
	{
		AbstractEntity node = (AbstractEntity) treeView.getSelectedNode();

		JdbcRecord.exportRecords(node.getTree(), node);
	}

	//---------------------------------------------------------------------------

	private void pop_dropObj()
	{
		TreeViewNode node = treeView.getSelectedNode();

		if (!JdbcLib.dropEntities(this, node))
		{
			JOptionPane.showMessageDialog(this,
						"Some objects cannot be dropped.\n"+
						"Probably they are referenced somewhere or\n"+
						"the account has no enough privileges.",
						"Objects left", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//---------------------------------------------------------------------------

	private void pop_drop()
	{
		GuiUtil.setWaitCursor(this, true);

		AbstractEntity node = (AbstractEntity) treeView.getSelectedNode();

		try
		{
			node.getJdbcConnection().getSqlAdapter().dropEntity(node);
			DruidUtil.removeNode(node, false);
		}
		catch(Exception e)
		{
			Dialogs.showOperationAborted(this, e.getMessage());
		}

		GuiUtil.setWaitCursor(this, false);
	}

	//---------------------------------------------------------------------------

	private void pop_enImport()
	{
		Vector vImpEnt = new Vector();
		vImpEnt.addElement(treeView.getSelectedNode());

		try
		{
			JdbcImport.importObjects(null, dbNode, vImpEnt);
		}
		catch (Exception e)
		{
			Dialogs.showOperationAborted(this, e.getMessage());
		}
	}
}

//==============================================================================
