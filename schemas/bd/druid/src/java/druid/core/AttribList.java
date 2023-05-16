//==============================================================================
//===
//===   AttribList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.Vector;

//==============================================================================

/** The following methods set the dataChanged bit:
  * - append, insert, remove, swap
  *
  * The following methods don't affect the dataChanged bit:
  * - addAttrib, removeAttrib, remove, duplicate, load, save
  */

public class AttribList
{
	private AttribSet asTempl = new AttribSet();	//--- dummy object, cloned during load
																//--- it is also useful to store default data
	private Vector    vNames  = new Vector();		//--- contains attrib names (for orderer enum)
	private Vector    vList   = new Vector();		//--- a vector of AttribSet (real data)

	//---------------------------------------------------------------------------
	protected AttribList getNewInstance() { return new AttribList(); }
	//---------------------------------------------------------------------------

	public AttribList()  {}

	//---------------------------------------------------------------------------

	public void addAttrib(String name, String data)
	{
		asTempl.addAttrib(name, data);

		//--- if addAttrib doesn't raise an exception, add the name

		vNames.addElement(name);
	}

	//---------------------------------------------------------------------------

	public void addAttrib(String name, int data)
	{
		asTempl.addAttrib(name, data);

		//--- if addAttrib doesn't raise an exception, add the name

		vNames.addElement(name);
	}

	//---------------------------------------------------------------------------

	public void addAttrib(String name, boolean data)
	{
		asTempl.addAttrib(name, data);

		//--- if addAttrib doesn't raise an exception, add the name

		vNames.addElement(name);
	}

	//---------------------------------------------------------------------------

	public void removeAttrib(int index)
	{
		String name = (String)vNames.elementAt(index);

		removeAttrib(name);
	}

	//---------------------------------------------------------------------------

	public void removeAttrib(String name)
	{
		asTempl.removeAttrib(name);
		vNames.remove(name);

		for(int i=0; i<vList.size(); i++)
		{
			AttribSet as = (AttribSet)vList.elementAt(i);

			as.removeAttrib(name);
		}
	}

	//---------------------------------------------------------------------------

	public boolean containsAttrib(String name)
	{
		return asTempl.contains(name);
	}

	//---------------------------------------------------------------------------

	public String getAttrib(int index)
	{
		return (String)vNames.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public int attribSize()
	{
		return vNames.size();
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public AttribSet append()
	{
		AttribSet as = asTempl.duplicate();

		vList.addElement(as);

		if (as.contains("id"))
			as.setInt("id", Serials.get());

		DataTracker.setDataChanged();
		return as;
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public void append(AttribSet as)
	{
		as.sync(asTempl);
		vList.addElement(as);

		if (as.contains("id"))
			as.setInt("id", Serials.get());

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public void insert(int index, AttribSet as)
	{
		as.sync(asTempl);
		vList.insertElementAt(as, index);

		if (as.contains("id"))
			as.setInt("id", Serials.get());

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public AttribSet insert(int index)
	{
		AttribSet as = asTempl.duplicate();

		vList.insertElementAt(as, index);

		if (as.contains("id"))
			as.setInt("id", Serials.get());

		DataTracker.setDataChanged();
		return as;
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public void remove(int index)
	{
		vList.remove(index);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public AttribSet get(int index)
	{
		return (AttribSet)vList.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public AttribSet find(int id)
	{
		for(int i=0; i<size(); i++)
		{
			AttribSet as = get(i);

			if (id == as.getInt("id"))
				return as;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public int size()
	{
		return vList.size();
	}

	//---------------------------------------------------------------------------
	/** Fires "data changed" */

	public void swap(int i, int j)
	{
		Object io = vList.elementAt(i);
		Object jo = vList.elementAt(j);

		vList.setElementAt(io, j);
		vList.setElementAt(jo, i);

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public AttribList duplicate()
	{
		AttribList al = getNewInstance();

		al.asTempl = asTempl.duplicate();

		//------------------------------------------------------------------------
		//--- duplicate fields

		for(int i=0; i<vNames.size(); i++)
		{
			//--- this two lines ensure that the entire string is copied
			//--- (not just its reference)

			String s = (String)vNames.elementAt(i);

			al.vNames.addElement(s);
		}

		//------------------------------------------------------------------------
		//--- duplicate AttribSets

		for(int i=0; i<vList.size(); i++)
		{
			AttribSet as = (AttribSet)vList.elementAt(i);

			al.vList.addElement(as.duplicate());
		}

		return al;
	}

	//---------------------------------------------------------------------------

	public void sync(AttribList al)
	{
		asTempl.sync(al.asTempl);

		for(int i=0; i<vList.size(); i++)
			get(i).sync(asTempl);

		//--- stage 1 : remove all unused attribs

		for(int i=0; i<vNames.size(); i++)
		{
			String attrib = (String) vNames.get(i);

			if (!asTempl.contains(attrib))
				vNames.remove(i--);
		}

		//--- stage 2 : add remaining attribs

		if (vNames.size() != al.vNames.size())
			for(int i=0; i<al.vNames.size(); i++)
			{
				String attrib = (String) al.vNames.get(i);

				if (vNames.size()-1 < i || !getAttrib(i).equals(attrib))
					vNames.insertElementAt(attrib, i);
			}
	}

	//---------------------------------------------------------------------------

	public boolean isAttribAString (String name) { return asTempl.isAttribAString(name); }
	public boolean isAttribAnInt   (String name) { return asTempl.isAttribAnInt(name);   }
	public boolean isAttribABoolean(String name) { return asTempl.isAttribABoolean(name);}

	//---------------------------------------------------------------------------

	public String toString()
	{
		StringBuffer s = new StringBuffer("[ATTRLIST:\n");

		for (int i = 0; i < vList.size(); i++)
		{
			s.append("  ").append(vList.get(i)).append("\n");
		}

		return s.append("]").toString();
	}
}

//==============================================================================
