//==============================================================================
//===
//===   AttribListIterator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.Iterator;

//==============================================================================

public class AttribListIterator implements Iterator
{
	private AttribList al;

	private int current=0;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AttribListIterator(AttribList al)
	{
		this.al = al;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Iterator interface
	//---
	//---------------------------------------------------------------------------

	public boolean hasNext()
	{
		return current < al.size();
	}

	//---------------------------------------------------------------------------

	public void remove()
	{
		al.remove(--current);
	}

	//---------------------------------------------------------------------------

	public Object next()
	{
		return al.get(current++);
	}
}

//==============================================================================
