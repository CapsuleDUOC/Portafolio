//==============================================================================
//===
//===   StoragePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.oracle.sql.panels.table;

import mod.dbms.oracle.sql.DatabaseSettings;
import mod.dbms.oracle.sql.TableSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class StoragePanel extends TPanel
{
	private TComboBoxGuardian  tcbTSpace   = new TComboBoxGuardian("Tablespace",   true);
	private TComboBoxGuardian  tcbPKTSpace = new TComboBoxGuardian("PKTablespace", true);
	private TCheckBoxGuardian  tcbSeqGener = new TCheckBoxGuardian ("SeqGeneration", "Generate sequence for primary key");
	private TTextFieldGuardian ttfSeqTempl = new TTextFieldGuardian("SeqTemplate");

	//---------------------------------------------------------------------------

	public StoragePanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 5);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",       new TLabel("Tablespace"));
		add("0,1",       new TLabel("Primary key"));
		add("1,0,x",     tcbTSpace);
		add("1,1,x",     tcbPKTSpace);
		
		add("0,3,x,c,2", tcbSeqGener);
		add("0,4",       new TLabel("Sequence template"));
		add("1,4,x",     ttfSeqTempl);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings gs, TableSettings ts)
	{
		tcbTSpace.removeAllItems();
		tcbTSpace.addItem("0", "-none-");

		tcbPKTSpace.removeAllItems();
		tcbPKTSpace.addItem("0", "-none-");

		//------------------------------------------------------------------------
		//--- add tablespaces to combobox

		AttribList tabs = gs.getTablespaces();

		AttribSet asTS   = null;
		AttribSet asPKTS = null;

		int tspace   = ts.getTablespace();
		int pktspace = ts.getPKTablespace();

		for(int i=0; i<tabs.size(); i++)
		{
			int    id   = tabs.get(i).getInt("id");
			String name = tabs.get(i).getString("name");

			tcbTSpace  .addItem(id, name);
			tcbPKTSpace.addItem(id, name);

			if (tspace == id)
				asTS = tabs.get(i);

			if (pktspace == id)
				asPKTS = tabs.get(i);
		}

		//------------------------------------------------------------------------
		//--- check if tablespace has been deleted

		if (asTS == null && tspace != 0)
			tcbTSpace.addItem(tspace, "<DELETED>");

		if (asPKTS == null && pktspace != 0)
			tcbPKTSpace.addItem(pktspace, "<DELETED>");

		tcbTSpace  .refresh(ts);
		tcbPKTSpace.refresh(ts);
		tcbSeqGener.refresh(ts);
		ttfSeqTempl.refresh(ts);
	}
}

//==============================================================================
