//==============================================================================
//===
//===   FontMappingPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.panels;

import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mod.datagen.docs.pdf.Settings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TLabel;
import org.dlib.gui.TTextField;

import druid.util.gui.guardians.TSliderGuardian;

//==============================================================================

class FontMappingPanel extends JPanel implements ChangeListener
{
	private TSliderGuardian tsSmaller = new TSliderGuardian("Smaller", -4, 20);
	private TSliderGuardian tsSmall   = new TSliderGuardian("Small",   -4, 20);
	private TSliderGuardian tsBig     = new TSliderGuardian("Big",     -4, 20);
	private TSliderGuardian tsBigger  = new TSliderGuardian("Bigger",  -4, 20);
	private TSliderGuardian tsBiggest = new TSliderGuardian("Biggest", -4, 20);
	private TSliderGuardian tsHuge    = new TSliderGuardian("Huge",    -4, 20);

	private ROTextField txtSmaller = new ROTextField(3);
	private ROTextField txtSmall   = new ROTextField(3);
	private ROTextField txtBig     = new ROTextField(3);
	private ROTextField txtBigger  = new ROTextField(3);
	private ROTextField txtBiggest = new ROTextField(3);
	private ROTextField txtHuge    = new ROTextField(3);

	private Hashtable htSliderMapping = new Hashtable();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FontMappingPanel()
	{
		FlexLayout flexL = new FlexLayout(3,6);
		flexL.setColProp(2, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Smaller (-2)"));
		add("0,1", new TLabel("Small (-1)"));
		add("0,2", new TLabel("Big (+1)"));
		add("0,3", new TLabel("Bigger (+2)"));
		add("0,4", new TLabel("Biggest (+3)"));
		add("0,5", new TLabel("Huge (+4)"));

		add("1,0", txtSmaller);
		add("1,1", txtSmall);
		add("1,2", txtBig);
		add("1,3", txtBigger);
		add("1,4", txtBiggest);
		add("1,5", txtHuge);

		add("2,0,x", tsSmaller);
		add("2,1,x", tsSmall);
		add("2,2,x", tsBig);
		add("2,3,x", tsBigger);
		add("2,4,x", tsBiggest);
		add("2,5,x", tsHuge);

		tsSmaller.addChangeListener(this);
		tsSmall.addChangeListener(this);
		tsBig.addChangeListener(this);
		tsBigger.addChangeListener(this);
		tsBiggest.addChangeListener(this);
		tsHuge.addChangeListener(this);

		htSliderMapping.put(tsSmaller, txtSmaller);
		htSliderMapping.put(tsSmall,   txtSmall);
		htSliderMapping.put(tsBig,     txtBig);
		htSliderMapping.put(tsBigger,  txtBigger);
		htSliderMapping.put(tsBiggest, txtBiggest);
		htSliderMapping.put(tsHuge,    txtHuge);

		//--- trigger events to sync the sliders with the textfields

		tsSmaller.setValue(4);
		tsSmall.setValue(4);
		tsBig.setValue(4);
		tsBigger.setValue(4);
		tsBiggest.setValue(4);
		tsHuge.setValue(4);
	}

	//---------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		JSlider slider = (JSlider) e.getSource();

		TTextField txt = (TTextField) htSliderMapping.get(slider);

		txt.setText(slider.getValue() +"");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		tsSmaller.refresh(s);
		tsSmall  .refresh(s);
		tsBig    .refresh(s);
		tsBigger .refresh(s);
		tsBiggest.refresh(s);
		tsHuge   .refresh(s);
	}
}

//==============================================================================
