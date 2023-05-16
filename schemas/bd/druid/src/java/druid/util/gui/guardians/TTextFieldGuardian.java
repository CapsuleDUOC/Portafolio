//==============================================================================
//===
//===   TTextFieldGuardian
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.guardians;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dlib.gui.TTextField;

//==============================================================================

public class TTextFieldGuardian extends TTextField implements DocumentListener
{
	private String  method;
	private Object  sett;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TTextFieldGuardian(String method)
	{
		this.method = method;

		getDocument().addDocumentListener(this);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(Object s)
	{
		//--- this is needed to prevent controls from firing events

		sett = null;
		setText(GuardianUtil.getValue(s, method));
		sett = s;
	}


	//---------------------------------------------------------------------------
	//---
	//--- DocumentListener
	//---
	//---------------------------------------------------------------------------

	public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
	public void removeUpdate(DocumentEvent e) { changedUpdate(e); }

	//---------------------------------------------------------------------------

	public void changedUpdate(DocumentEvent e)
	{
		if (sett != null)
			GuardianUtil.setValue(sett, method, getText());
	}
}

//==============================================================================
