//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.constraints;

import druid.core.AttribSet;
import druid.data.Constraint;
import druid.util.gui.SqlTextArea;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

//==============================================================================
//--- constraints support

public class GeneralPanel extends JPanel
{
	private SqlTextArea txaCode  = new SqlTextArea();
	private JLabel      lbStatus = new JLabel();

	private Font font = new Font("Monospaced", Font.PLAIN, 12);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

		FlexLayout flexL = new FlexLayout(1, 2, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup code panel

		TPanel     tp = new TPanel("Constraint code");
		FlexLayout fl = new FlexLayout(1, 1);
		fl.setColProp(0, FlexLayout.EXPAND);
		fl.setRowProp(0, FlexLayout.EXPAND);
		tp.setLayout(fl);

		tp.add("0,0,x,x", txaCode);

		lbStatus.setFont(font);
		txaCode.setFont(font);
		txaCode.setEditable(false);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0", lbStatus);
		add("0,1,x,x", tp);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Constraint constraint)
	{
		AttribSet as = constraint.attrSet;

		txaCode.setText(as.getString("code"));
		lbStatus.setText("Status: " + as.getString("status"));
	}
}

//==============================================================================
