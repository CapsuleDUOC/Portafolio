//==============================================================================
//===
//===   ChangeSentinel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import druid.core.DataTracker;

//==============================================================================

public class ChangeSentinel implements ItemListener, DocumentListener
{
	private static ChangeSentinel singleton;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	private ChangeSentinel() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static ChangeSentinel getInstance()
	{
		if (singleton == null)
			singleton = new ChangeSentinel();

		return singleton;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ItemListener
	//---
	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e) { handleEvent(); }

	//---------------------------------------------------------------------------
	//---
	//--- DocumentListener
	//---
	//---------------------------------------------------------------------------

	public void insertUpdate(DocumentEvent e)  { handleEvent(); }
	public void removeUpdate(DocumentEvent e)  { handleEvent(); }
	public void changedUpdate(DocumentEvent e) { handleEvent(); }

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void handleEvent()
	{
		DataTracker.setDataChanged();
	}
}

//==============================================================================
