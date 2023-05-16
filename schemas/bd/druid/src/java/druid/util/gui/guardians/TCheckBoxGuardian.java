//==============================================================================
//===
//===   TCheckBoxGuardian
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.guardians;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;

import org.dlib.gui.TCheckBox;

import druid.core.DataTracker;

//==============================================================================

public class TCheckBoxGuardian extends TCheckBox implements ItemListener
{
	private String  method;
	private Object  sett;

	private static Class aSetArgs[] = { boolean.class };

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TCheckBoxGuardian(String method, String text)
	{
		super(text);
		this.method = method;

		addItemListener(this);
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
		setSelected(getValue(s));
		sett = s;
	}


	//---------------------------------------------------------------------------
	//---
	//--- ItemListener
	//---
	//---------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent e)
	{
		if (sett != null)
		{
			setValue(sett, isSelected());
			DataTracker.setDataChanged();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private boolean getValue(Object s)
	{
		try
		{
			Method m = s.getClass().getMethod("is" + method, (Class[]) null);
			Object v = m.invoke(s, (Object[]) null);

			return ((Boolean) v).booleanValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	//---------------------------------------------------------------------------

	private void setValue(Object s, boolean value)
	{
		Object aObj[] = { Boolean.valueOf(value) };

		try
		{
			Method m = s.getClass().getMethod("set" + method, aSetArgs);
			m.invoke(s, aObj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

//==============================================================================
