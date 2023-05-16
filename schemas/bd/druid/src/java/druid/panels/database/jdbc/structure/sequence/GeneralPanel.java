//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.sequence;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ROCheckBox;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TLabel;

import druid.core.jdbc.entities.SequenceEntity;

//==============================================================================

public class GeneralPanel extends JPanel
{
	private ROTextField txtIncrem = new ROTextField();
	private ROTextField txtMinVal = new ROTextField();
	private ROTextField txtMaxVal = new ROTextField();
	private ROTextField txtStart  = new ROTextField();
	private ROTextField txtCache  = new ROTextField();

	private ROCheckBox  chbCycle  = new ROCheckBox();
	private ROCheckBox  chbOrder  = new ROCheckBox();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 7);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		//------------------------------------------------------------------------
		//--- setup panel

		add("0,0", new TLabel("Increment"));
		add("0,1", new TLabel("Min value"));
		add("0,2", new TLabel("Max value"));
		add("0,3", new TLabel("Start"));
		add("0,4", new TLabel("Cache"));
		add("0,5", new TLabel("Cycle"));
		add("0,6", new TLabel("Order"));

		add("1,0,x", txtIncrem);
		add("1,1,x", txtMinVal);
		add("1,2,x", txtMaxVal);
		add("1,3,x", txtStart);
		add("1,4,x", txtCache);
		add("1,5,x", chbCycle);
		add("1,6,x", chbOrder);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(SequenceEntity ent)
	{
		txtIncrem.setText(ent.increment);
		txtMinVal.setText(ent.minValue);
		txtMaxVal.setText(ent.maxValue);
		txtStart.setText (ent.start);
		txtCache.setText (ent.cache);

		chbCycle.setSelected(ent.cycle);
		chbOrder.setSelected(ent.order);
	}
}

//==============================================================================
