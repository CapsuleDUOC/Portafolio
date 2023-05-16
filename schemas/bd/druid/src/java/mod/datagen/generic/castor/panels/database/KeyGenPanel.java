//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor.panels.database;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.util.gui.guardians.TTextFieldGuardian;
import druid.util.gui.guardians.TCheckBoxGuardian;
import mod.datagen.generic.castor.DatabaseSettings;

//==============================================================================

public class KeyGenPanel extends JPanel
{
	private HighLowPanel  panHighlow  = new HighLowPanel();
	private SequencePanel panSequence = new SequencePanel();

	//---------------------------------------------------------------------------

	public KeyGenPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", panHighlow);
		add("0,1,x", panSequence);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		panHighlow .refresh(s);
		panSequence.refresh(s);
	}
}

//==============================================================================

class HighLowPanel extends TPanel
{
	private TTextFieldGuardian txtTable    = new TTextFieldGuardian("HLTable");
	private TTextFieldGuardian txtKeyField = new TTextFieldGuardian("HLKeyField");
	private TTextFieldGuardian txtValField = new TTextFieldGuardian("HLValueField");
	private TTextFieldGuardian txtGrabSize = new TTextFieldGuardian("HLGrabSize");

	private TCheckBoxGuardian  chbSameConn = new TCheckBoxGuardian("HLSameConn", "Same connection");
	private TCheckBoxGuardian  chbGlobal   = new TCheckBoxGuardian("HLGlobal",   "Global");

	//---------------------------------------------------------------------------

	public HighLowPanel()
	{
		super("High Low");

		FlexLayout flexL = new FlexLayout(2, 7);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Table"));
		add("0,1", new TLabel("Key column"));
		add("0,2", new TLabel("Value column"));
		add("0,3", new TLabel("Grab size"));

		add("1,0,x", txtTable);
		add("1,1,x", txtKeyField);
		add("1,2,x", txtValField);
		add("1,3,x", txtGrabSize);

		add("0,5,x,c,2", chbSameConn);
		add("0,6,x,c,2", chbGlobal);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtTable   .refresh(s);
		txtKeyField.refresh(s);
		txtValField.refresh(s);
		txtGrabSize.refresh(s);

		chbSameConn.refresh(s);
		chbGlobal  .refresh(s);
	}
}

//==============================================================================

class SequencePanel extends TPanel
{
	private TTextFieldGuardian txtSequen  = new TTextFieldGuardian("SEQSequence");
	private TTextFieldGuardian txtIncrem  = new TTextFieldGuardian("SEQIncrement");

	private TCheckBoxGuardian  chbReturn  = new TCheckBoxGuardian("SEQReturning", "Returning");
	private TCheckBoxGuardian  chbTrigger = new TCheckBoxGuardian("SEQTrigger",   "Trigger");

	//---------------------------------------------------------------------------

	public SequencePanel()
	{
		super("Sequence");

		FlexLayout flexL = new FlexLayout(2, 5);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Sequence"));
		add("0,1", new TLabel("Increment"));

		add("1,0,x", txtSequen);
		add("1,1,x", txtIncrem);

		add("0,3,x,c,2", chbReturn);
		add("0,4,x,c,2", chbTrigger);
	}

	//---------------------------------------------------------------------------

	public void refresh(DatabaseSettings s)
	{
		txtSequen.refresh(s);
		txtIncrem.refresh(s);

		chbReturn .refresh(s);
		chbTrigger.refresh(s);
	}
}

//==============================================================================
