//==============================================================================
//===
//===   DatabasePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.data.DatabaseNode;
import druid.data.DatabaseVars;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.dialogs.tabledialog.TableDialog;
import druid.panels.database.datatypes.DatatypesPanel;
import druid.panels.database.er.ErDesigner;
import druid.panels.database.generation.GenerationPanel;
import druid.panels.database.jdbc.JdbcPanel;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.DataEntryPanel;
import druid.util.gui.ExtraSqlPanel;
import druid.util.gui.ImageFactory;
import druid.util.gui.editor.DocEditor;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.flextable.FlexTableSelEvent;

//==============================================================================

public class DatabasePanel extends TTabbedPane
{
	private DataEntryPanel  revisPanel    = new DataEntryPanel();
	private FAttribPanel    fattribsPanel = new FAttribPanel();
	private DatatypesPanel  dtypesPanel   = new DatatypesPanel();
	private DocEditor       docEditor     = new DocEditor();
	private DataEntryPanel  varsPanel     = new DataEntryPanel();
	private GenerationPanel generatPanel  = new GenerationPanel();
	private ExtraSqlPanel   sqlPanel      = new ExtraSqlPanel(true);
	private JdbcPanel       jdbcPanel     = new JdbcPanel();
	private ErDesigner      erDesigner    = new ErDesigner();

	private DatabaseNode     dbNode;

	//---------------------------------------------------------------------------

	public DatabasePanel()
	{
		addTab("Revisions",     revisPanel);
		addTab("Docs",          docEditor);
		addTab("Data Types",    dtypesPanel);
		addTab("Field Attribs", fattribsPanel);
		addTab("Vars",          varsPanel);
		addTab("Extra Sql",     sqlPanel);
		addTab("Generation",    generatPanel);
		addTab("Jdbc",          jdbcPanel);
		addTab("E/R Views",     erDesigner);

		//------------------------------------------------------------------------

		revisPanel.addAttrib("version", "Version",     100);
		revisPanel.addAttrib("date",    "Date",        150);
		revisPanel.addAttrib("descr",   "Description", 600);

		//------------------------------------------------------------------------

		TComboBox tcbType = new TComboBox();

		tcbType.addItem(FieldAttribs.TYPE_BOOL,   "Bool");
		tcbType.addItem(FieldAttribs.TYPE_STRING, "String");
		tcbType.addItem(FieldAttribs.TYPE_INT,    "Int");

		TComboBox tcbScope = new TComboBox();

		tcbScope.addItem(FieldAttribs.SCOPE_FIELD, "Field");
		tcbScope.addItem(FieldAttribs.SCOPE_TABLE, "Table");
		tcbScope.addItem(FieldAttribs.SCOPE_INDEX, "Index");
		tcbScope.addItem(FieldAttribs.SCOPE_UINDEX,"Index-U");
		tcbScope.addItem(FieldAttribs.SCOPE_FTINDEX,"Index-FullText");
		tcbScope.addItem(FieldAttribs.SCOPE_CUSTOM,"Custom");

		fattribsPanel.addAttrib("name",      "Name",      150);
		fattribsPanel.addAttrib("sqlName",   "Sql name",  190);
		fattribsPanel.addAttrib("type",      "Type",      130, tcbType);
		fattribsPanel.addAttrib("scope",     "Scope",     130, tcbScope);
		fattribsPanel.addAttrib("useInDD",   "In DD",     110);
		fattribsPanel.addAttrib("useInSumm", "In Sum",    110);
		fattribsPanel.addAttrib("width",     "Width",     100);
		fattribsPanel.addAttrib("descr",     "Descr",     100);

		docEditor.getDocument().addDocumentListener(ChangeSentinel.getInstance());

		//------------------------------------------------------------------------
		//--- setup db vars

		tcbType = new TComboBox();

		tcbType.addItem(DatabaseVars.BOOL,   "Bool");
		tcbType.addItem(DatabaseVars.STRING, "String");
		tcbType.addItem(DatabaseVars.INT,    "Int");
		tcbType.addItem(DatabaseVars.LONG,   "Long");
		tcbType.addItem(DatabaseVars.FLOAT,  "Float");
		tcbType.addItem(DatabaseVars.DOUBLE, "Double");
		tcbType.addItem(DatabaseVars.CHAR,   "Char");

		varsPanel.addAttrib("name",  "Name",  220);
		varsPanel.addAttrib("type",  "Type",  130, tcbType);
		varsPanel.addAttrib("value", "Value", 200);
		varsPanel.addAttrib("descr", "Descr", 400);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseNode dbaseNode)
	{
		dbNode = dbaseNode;

		fattribsPanel.setDatabase(dbNode);

		docEditor.setDoc(dbaseNode.xmlDoc);
		sqlPanel.refresh(dbaseNode.attrSet);
		dtypesPanel.refresh(dbaseNode);
		varsPanel.setAttribList(dbaseNode.dbVars);
		generatPanel.refresh(dbaseNode);
		jdbcPanel.refresh(dbaseNode);
		erDesigner.refresh(dbaseNode);

		revisPanel.setAttribList(dbaseNode.revisions);
		fattribsPanel.setAttribList(dbaseNode.fieldAttribs);
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(DatabaseNode dbaseNode)
	{
		docEditor.getDoc(dbaseNode.xmlDoc);
		sqlPanel.saveDataToNode(dbaseNode.attrSet);
		dtypesPanel.saveDataToNode();
		generatPanel.saveDataToNode(dbaseNode);
		jdbcPanel.saveDataToNode(dbaseNode);
		erDesigner.saveDataToNode();
	}
}

//==============================================================================

class FAttribPanel extends DataEntryPanel
{
	private DatabaseNode dbNode;

	private AbstractButton btnUsage;
	private AbstractButton btnMerge;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FAttribPanel()
	{
		toolBar.addSeparator();

		btnUsage = toolBar.add(ImageFactory.LENS,  this, "usage",  "Usage");
		btnMerge = toolBar.add(ImageFactory.MERGE, this, "merge",  "Merge with another");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setDatabase(DatabaseNode node)
	{
		dbNode = node;
		setAttribList(dbNode.fieldAttribs);

		btnUsage.setEnabled(false);
		btnMerge.setEnabled(false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("usage"))
			handleUsage();

		else if (cmd.equals("merge"))
			handleMerge();

		else
			super.actionPerformed(e);
	}

	//---------------------------------------------------------------------------

	protected void handleNew()
	{
		super.handleNew();
		sync();
	}

	//---------------------------------------------------------------------------

	protected void handleDel(boolean cut)
	{
		super.handleDel(cut);
		sync();

		btnUsage.setEnabled(btnCopy.isEnabled());
		btnMerge.setEnabled(btnCopy.isEnabled());
	}

	//---------------------------------------------------------------------------

	protected void handlePaste()
	{
		super.handlePaste();
		sync();
	}

	//---------------------------------------------------------------------------

	private void handleUsage()
	{
		AttribSet as = alData.get(iSelectedRow);

		int    id   = as.getInt("id");
		String name = as.getString("name");

		Vector vFields = DataLib.fieldAttribUsage(dbNode, id);

		if (vFields.size() == 0)
		{
			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"The field attrib '" + name + "' is not used",
						"Field Attrib Usage", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			String title = "Field attrib used by " + vFields.size() + " field(s)";

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

	private void handleMerge()
	{
		AttribSet as = alData.get(iSelectedRow);

		int    srcID   = as.getInt("id");
		String srcName = as.getString("name");
		String type    = as.getString("type");

		Vector attribs = new Vector();

		for(int i=0; i<alData.size(); i++)
			if (i != iSelectedRow)
			{
				AttribSet currAS = alData.get(i);

				if (currAS.getString("type").equals(type))
					attribs.add(currAS);
			}

		if (attribs.size() == 0)
		{
			JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"There are no field attribs that can be merged to '" + srcName + "'",
						"Field Attrib Merge", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			String title = "Select the destination field attrib";

			TableDialog td = new TableDialog(GuiUtil.getFrame(this), title);

			td.setClickable(true);

			td.addColumn("Name",     200);
			td.addColumn("Sql Name", 200);

			for(int i=0; i<attribs.size(); i++)
			{
				as = (AttribSet) attribs.get(i);

				td.append(as.getString("name"), as.getString("sqlName"));
			}

			td.showDialog();

			if (!td.isCancelled())
			{
				as = (AttribSet) attribs.get(td.getClickedRow());

				int    dstID   = as.getInt("id");
				String dstName = as.getString("name");

				DataLib.fieldAttribMerge(dbNode, srcID, dstID);

				int size = DataLib.fieldAttribUsage(dbNode, srcID).size();

				JOptionPane.showMessageDialog(GuiUtil.getFrame(this),
						"The field attrib '" + srcName + "' has been merged to '"+dstName+"' ("+size+" changes)",
						"Field Attrib Merge", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableConfirmator
	//---
	//---------------------------------------------------------------------------

	public boolean confirmValueChanged(int row, int col, Object value)
	{
		if (col == 2)
		{
			Object[] btns = { "Change", "Cancel"};

			int resp = JOptionPane.showOptionDialog(this,
									"Changing an attrib's type is dangerous.\n"+
									"The attrib's value for all fields will be set to its default.\n"+
									"Change anyway ?",
									"Warning",
									JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
									null, btns,btns[1]);

			if (resp != 0) return false;
		}

		boolean confirm = super.confirmValueChanged(row, col, value);

		if (confirm && col == 2)
			sync();

		return confirm;
	}

	//---------------------------------------------------------------------------
	//---
	//---   FlexTableSelListener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		super.rowSelected(e);

		btnUsage.setEnabled(btnCopy.isEnabled());
		btnMerge.setEnabled(btnCopy.isEnabled());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void sync()
	{
		GuiUtil.setWaitCursor(this, true);
		DataLib.syncAllFields(dbNode);
		GuiUtil.setWaitCursor(this, false);
	}
}

//==============================================================================
