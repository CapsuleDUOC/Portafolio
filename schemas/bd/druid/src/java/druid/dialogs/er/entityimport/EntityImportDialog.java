//==============================================================================
//===
//===   EntityImportDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.entityimport;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;
import org.dlib.gui.treetable.TreeTable;
import org.dlib.gui.treeview.TreeViewNode;

import druid.data.DatabaseNode;
import druid.data.FolderNode;
import druid.data.TableNode;
import druid.dialogs.BasicConfigDialog;
import druid.util.gui.renderers.TreeViewRenderer;

//==============================================================================

public class EntityImportDialog extends BasicConfigDialog
{
	private JLabel     lbName;
	private JTextField txtName;
	private TreeTable  ttbObjects;
	private FlexLayout flexL;
	private String     entityName;
	private JCheckBox  chbSingleImp;
	
	private List<Integer> vTables = new ArrayList<Integer>();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public EntityImportDialog(Frame frame)
	{
		super(frame);

		//--- setup treetable

		ttbObjects.addHeader("Importable objects", 250, true);
		ttbObjects.addHeader("Import ?",            50, true);
		ttbObjects.setPreferredSize(new Dimension(400,400));
		ttbObjects.setTreeCellRenderer(new TreeViewRenderer());

		pack();
		setLocationRelativeTo(getParent());
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void run(DatabaseNode dbNode, boolean showName)
	{
		clearCancelled();
		txtName.setText("");

		if (showName)	setTitle("New entity");
			else		setTitle("Add objects to entity");

		lbName      .setVisible(showName);
		txtName     .setVisible(showName);
		chbSingleImp.setVisible(showName);
		
		//--- setup treetable

		DatabaseNode newDbNode = (DatabaseNode) dbNode.duplicate();

		buildTreeTable(newDbNode);

		show();

		if (!isCancelled())
		{
			vTables.clear();
			buildTableVector(newDbNode, false);

			entityName = buildEntName(dbNode);
		}
	}

	//---------------------------------------------------------------------------

	public List<Integer> getTableIds() { return vTables;    }
	public String        getEntName()  { return entityName; }

	//---------------------------------------------------------------------------

	public JComponent getCentralPanel()
	{
		flexL = new FlexLayout(2,3);
		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(2, FlexLayout.EXPAND);
		flexL.setNullGaps(0,0);

		JPanel p = new JPanel();
		p.setLayout(flexL);

		lbName      = new TLabel("Entity name");
		txtName     = new TTextField(30);
		ttbObjects  = new TreeTable(true);
		chbSingleImp=new TCheckBox("Import as single entities");
		
		p.add("0,0",       lbName);
		p.add("1,0,x",     txtName);
		p.add("0,1,x,c,2", chbSingleImp);
		p.add("0,2,x,x,2", ttbObjects);

		return p;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void buildTreeTable(DatabaseNode dbNode)
	{
		//--- add one extra column to db tree

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
			((TreeViewNode)e.nextElement()).addColumn(Boolean.FALSE);

		//--- enumerate database objects collecting tables etc because
		//--- we cannot change the tree during an enumeration

		List<TreeViewNode> vOther = new ArrayList<TreeViewNode>();

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			TreeViewNode node = (TreeViewNode) e.nextElement();

			if (!(node instanceof TableNode || node instanceof FolderNode))
				vOther.add(node);
		}

		//--- remove objects that are not tables nor folders

		for(int i=0; i<vOther.size(); i++)
			vOther.get(i).removeFromParent();

		ttbObjects.setRootNode(dbNode);
	}

	//---------------------------------------------------------------------------

	private void buildTableVector(TreeViewNode node, boolean force)
	{
		for(int i=0; i<node.getChildCount(); i++)
		{
			TreeViewNode currNode = node.getChild(i);
			boolean      currSel  = ((Boolean)currNode.getColumnAt(0)).booleanValue();

			if (currSel || force)
			{
				if (currNode instanceof TableNode)
					vTables.add(((TableNode) currNode).attrSet.getInt("id"));
				else
					buildTableVector(currNode, true);
			}
			else
			{
				if (currNode instanceof FolderNode)
					buildTableVector(currNode, false);
			}
		}
	}

	//---------------------------------------------------------------------------

	private String buildEntName(DatabaseNode dbNode)
	{
		if (!txtName.getText().equals(""))
			return txtName.getText();

		if (vTables.size() != 1)
			return "-UnNamed-";

		int tabId = vTables.get(0);

		TableNode node = dbNode.getTableByID(tabId);

		return  (node == null) ? "????" : node.attrSet.getString("name");
	}
}

//==============================================================================
