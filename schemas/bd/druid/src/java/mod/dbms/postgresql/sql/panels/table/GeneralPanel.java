//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.postgresql.sql.panels.table;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DataTracker;
import druid.data.DatabaseNode;
import druid.data.TableNode;
import druid.dialogs.chooser.TableChooserDialog;
import druid.util.gui.ImageFactory;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;
import mod.dbms.postgresql.sql.DatabaseSettings;
import mod.dbms.postgresql.sql.TableSettings;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TButton;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

//==============================================================================

public class GeneralPanel extends TPanel implements ActionListener
{
	private TButton tbInherits = new TButton("", "inherits", this);

	private TTextFieldGuardian txtInherOR = new TTextFieldGuardian("InheritsOverride");
	private TComboBoxGuardian  tcbTSpace  = new TComboBoxGuardian("Tablespace",   true);

	private TableSettings ts;
	private DatabaseNode  dbNode;

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 3);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Inherits from"));
		add("0,1",   new TLabel("Inherits override"));
		add("0,2",   new TLabel("Tablespace"));

		add("1,0,x", tbInherits);
		add("1,1,x", txtInherOR);
		add("1,2,x", tcbTSpace);

		tbInherits.setHorizontalAlignment(SwingConstants.LEFT);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings gs, TableSettings ts, DatabaseNode dbNode)
	{
		this.ts     = ts;
		this.dbNode = dbNode;

		setTableName(ts.getInheritsFrom());
		txtInherOR.refresh(ts);

		//------------------------------------------------------------------------
		//--- setup tablespace

		tcbTSpace.removeAllItems();
		tcbTSpace.addItem("0", "-none-");

		AttribList tabs = gs.getTablespaces();
		AttribSet  asTS = null;

		int tspace = ts.getTablespace();

		for(int i=0; i<tabs.size(); i++)
		{
			int    id   = tabs.get(i).getInt("id");
			String name = tabs.get(i).getString("name");

			tcbTSpace.addItem(id, name);

			if (tspace == id)
				asTS = tabs.get(i);
		}

		//--- check if tablespace has been deleted

		if (asTS == null && tspace != 0)
			tcbTSpace.addItem(tspace, "<DELETED>");

		tcbTSpace.refresh(ts);
	}

	//---------------------------------------------------------------------------
	//---
	//--- ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		TableChooserDialog dlg = new TableChooserDialog(GuiUtil.getFrame(this));

		if (dlg.run(dbNode))
		{
			setTableName(dlg.getID());
			ts.setInheritsFrom(dlg.getID());
			DataTracker.setDataChanged();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void setTableName(int id)
	{
		if (id != 0)
		{
			TableNode node = dbNode.getTableByID(id);

			String text = "<DELETED>";

			if (node != null)
				text = node.attrSet.getString("name");

			tbInherits.setIcon(ImageFactory.TABLE);
			tbInherits.setText(text);
		}
		else
		{
			tbInherits.setIcon(null);
			tbInherits.setText("");
		}
	}
}

//==============================================================================
