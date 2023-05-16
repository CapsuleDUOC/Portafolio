//==============================================================================
//===
//===   ElementInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.editor.struct;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;

//==============================================================================

public class ElementInfo
{
	public HTML.Tag tag;
	public int      start;
	public int      end;

	public Hashtable attribs = new Hashtable();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ElementInfo(Element e)
	{
		tag   = (HTML.Tag) e.getAttributes().getAttribute(StyleConstants.NameAttribute);
		start = e.getStartOffset();
		end   = e.getEndOffset();

		AttributeSet as = e.getAttributes();

		for(Enumeration en =as.getAttributeNames(); en.hasMoreElements();)
		{
			Object name = en.nextElement();
			Object obj  = as.getAttribute(name);

			attribs.put(name, obj.toString());
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String toString()
	{
		return "["+ start +" "+ end +"]";
	}
}

//==============================================================================
