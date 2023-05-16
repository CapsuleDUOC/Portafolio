//==============================================================================
//===
//===   TComboBoxGuardian
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.guardians;

import java.lang.reflect.Method;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import druid.core.DataTracker;

//==============================================================================

public class TSliderGuardian extends JSlider implements ChangeListener
{
	private String  method;
	private Object  sett;

	private static Class aSetArgsInt[] = { int.class    };

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public TSliderGuardian(String method, int min, int max)
	{
		super(min, max);

		this.method = method;

		addChangeListener(this);
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
		setValue(getValueInt(s));
		sett = s;
	}

	//---------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		if (sett != null)
		{
			setValueInt(sett, getValue());

			DataTracker.setDataChanged();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private int getValueInt(Object s)
	{
		try
		{
			Method m = s.getClass().getMethod("get" + method, (Class[]) null);
			Object v = m.invoke(s, (Object[]) null);

			return ((Integer) v).intValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	//---------------------------------------------------------------------------

	private void setValueInt(Object s, int value)
	{
		Object aObj[] = { new Integer(value) };

		try
		{
			Method m = s.getClass().getMethod("set" + method, aSetArgsInt);
			m.invoke(s, aObj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

//==============================================================================
