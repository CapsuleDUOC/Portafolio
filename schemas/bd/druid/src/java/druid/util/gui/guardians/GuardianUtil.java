//==============================================================================
//===
//===   GuardianUtil
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.guardians;

import java.lang.reflect.Method;

import druid.core.AttribSet;
import druid.core.DataTracker;

//==============================================================================

public class GuardianUtil
{
	private static Class aSetArgs[] = { String.class };

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static String getValue(Object s, String method)
	{
		if (s instanceof AttribSet)
			return ((AttribSet) s).getString(method);

		try
		{
			Method m = s.getClass().getMethod("get" + method, (Class[]) null);
			Object v = m.invoke(s, (Object[]) null);

			return (String) v;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//---------------------------------------------------------------------------

	public static void setValue(Object s, String method, String value)
	{
		DataTracker.setDataChanged();

		if (s instanceof AttribSet)
		{
			((AttribSet) s).setString(method, value);

			return;
		}

		Object aObj[] = { value };

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
