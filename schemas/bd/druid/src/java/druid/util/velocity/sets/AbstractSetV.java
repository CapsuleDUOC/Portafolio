//==============================================================================
//===
//===   AbstractSetV
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.sets;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Vector;

import druid.core.AttribList;
import druid.core.AttribSet;

//==============================================================================

public class AbstractSetV
{
	protected AttribSet as;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	protected AbstractSetV(AttribSet as)
	{
		this.as = as;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static Collection convertSet(String className, AttribList al)
	{
		Vector v = new Vector();

		 for(int i=0; i<al.size(); i++)
			v.add(convertSet(className, al.get(i)));

		return v;
	}

	//---------------------------------------------------------------------------

	public boolean equals(Object obj)
	{
		if (obj == null) return false;

		AbstractSetV other = (AbstractSetV) obj;

		return other.as == this.as;
	}

	//---------------------------------------------------------------------------

	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append(getClass().getName());
		buf.append("{\n");
		buf.append(as.toString());
		buf.append("}\n");

		return buf.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Reserved methods
	//---
	//---------------------------------------------------------------------------

	protected static AbstractSetV convertSet(String className, AttribSet as)
	{
		try
		{
			Class clazz = Class.forName("druid.util.velocity.sets."+ className);

			Constructor  constructor = clazz.getConstructor(new Class[]{ AttribSet.class});
			AbstractSetV abstractSet = (AbstractSetV) constructor.newInstance(new Object[]{ as });

			return abstractSet;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}

//==============================================================================
