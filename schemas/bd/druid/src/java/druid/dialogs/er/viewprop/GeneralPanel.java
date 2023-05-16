//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;

import druid.core.AttribSet;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private TComboBox tcbDetails = new TComboBox();
	private JCheckBox chbAttribs = new JCheckBox();
	private JCheckBox chbBgCol   = new JCheckBox();
	
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 3, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Details"));
		add("0,1",   new TLabel("Show attributes"));
		add("0,2",   new TLabel("Show bg color"));

		add("1,0,x", tcbDetails);
		add("1,1,x", chbAttribs);
		add("1,2,x", chbBgCol);

		//------------------------------------------------------------------------
		//--- setup combo

		tcbDetails.addItem(ErEntity.ONLY_NAME,    "Only entity name");
		tcbDetails.addItem(ErEntity.NAME_AND_PKS, "Name and primary keys");
		tcbDetails.addItem(ErEntity.NAME_PKS_FKS, "Name, primary keys and foreign keys");
		tcbDetails.addItem(ErEntity.ALL_FIELDS,   "All fields");
		tcbDetails.addItem(ErEntity.COMPLETE,     "Complete (include datatypes)");

		//------------------------------------------------------------------------
		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		tcbDetails.addItemListener(sent);
		chbAttribs.addItemListener(sent);
		chbBgCol  .addItemListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(ErView erView)
	{
		AttribSet as = erView.attrSet;

		tcbDetails.setSelectedKey(as.getString("details"));
		chbAttribs.setSelected(erView.isShowAttribs());
		chbBgCol  .setSelected(erView.isShowBgColor());
	}

	//---------------------------------------------------------------------------

	public void store(ErView erView)
	{
		AttribSet as = erView.attrSet;

		as.setString("details",     tcbDetails.getSelectedKey());
		as.setBool  ("showAttribs", chbAttribs.isSelected());
		as.setBool  ("showBgColor", chbBgCol  .isSelected());
	}
}

//==============================================================================
