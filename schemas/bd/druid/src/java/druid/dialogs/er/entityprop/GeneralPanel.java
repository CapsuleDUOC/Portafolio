//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.entityprop;

import javax.swing.JPanel;

import org.dlib.gui.ColorIcon;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;

import druid.core.AttribSet;
import druid.data.er.ErEntity;
import druid.data.er.Legend;
import druid.data.er.LegendColor;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TComboBox tcbDetails = new TComboBox();
	private TComboBox tcbColor   = new TComboBox();

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 2, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",     new TLabel("Details"));
		add("0,1",     new TLabel("Color"));

		add("1,0,x",   tcbDetails);
		add("1,1,x",   tcbColor);

		//------------------------------------------------------------------------
		//--- setup combo

		tcbDetails.addItem(ErEntity.DEFAULT,      "Defalt (use view details)");
		tcbDetails.addItem(ErEntity.ONLY_NAME,    "Only entity name");
		tcbDetails.addItem(ErEntity.NAME_AND_PKS, "Name and primary keys");
		tcbDetails.addItem(ErEntity.NAME_PKS_FKS, "Name, primary keys and foreign keys");
		tcbDetails.addItem(ErEntity.ALL_FIELDS,   "All fields");
		tcbDetails.addItem(ErEntity.COMPLETE,     "Complete (include datatypes)");

		//------------------------------------------------------------------------
		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		tcbDetails.addItemListener(sent);
		tcbColor.addItemListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(ErEntity erEntity)
	{
		AttribSet as = erEntity.attrSet;

		tcbDetails.setSelectedKey(as.getString("details"));

		Legend leg = erEntity.getErView().legend;

		tcbColor.removeAllItems();

		for(int i=0; i<leg.getChildCount(); i++)
		{
			LegendColor legCol = (LegendColor) leg.getChild(i);

			int    colId   = legCol.attrSet.getInt("id");
			String colName = legCol.attrSet.getString("name");

			tcbColor.addItem(new ColorIcon(32,16, legCol.colBg), colId, colName);
		}

		tcbColor.setSelectedKey("" + as.getInt("colorId"));
	}

	//---------------------------------------------------------------------------

	public void store(ErEntity erEntity)
	{
		AttribSet as = erEntity.attrSet;

		as.setString("details", tcbDetails.getSelectedKey());
		as.setInt("colorId",    tcbColor.getSelectedIntKey());
	}
}

//==============================================================================
