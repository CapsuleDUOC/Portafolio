//==============================================================================
//===
//===   PrintingPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.er.viewprop;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;
import org.dlib.tools.Util;

import druid.core.AttribSet;
import druid.data.er.ErView;
import druid.util.gui.ChangeSentinel;

//==============================================================================

public class PrintingPanel extends JPanel
{
	private TCheckBox  chbBlackW = new TCheckBox();
	private JTextField txtScale  = new TTextField();

	//---------------------------------------------------------------------------

	public PrintingPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 2, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Black and white"));
		add("0,1", new TLabel("Scale factor (%)"));

		add("1,0,x", chbBlackW);
		add("1,1,x", txtScale);

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		chbBlackW.addItemListener(sent);
		txtScale.getDocument().addDocumentListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(ErView erView)
	{
		AttribSet as = erView.attrSet;

		chbBlackW.setSelected(as.getBool("printBlackWhite"));
		txtScale.setText(""+as.getInt("printScale"));
	}

	//---------------------------------------------------------------------------

	public void store(ErView erView)
	{
		AttribSet as = erView.attrSet;

		as.setBool("printBlackWhite", chbBlackW.isSelected());
		as.setInt( "printScale",      Util.getIntValueMinMax(txtScale.getText(), 100, 10, 300));
	}
}

//==============================================================================
