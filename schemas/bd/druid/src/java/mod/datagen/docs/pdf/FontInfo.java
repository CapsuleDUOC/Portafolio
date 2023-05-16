//==============================================================================
//===
//===   FontInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf;

import java.util.StringTokenizer;

import org.dlib.tools.TVector;

//==============================================================================

public class FontInfo
{
	public String  name;
	public int     size;
	public boolean bold;
	public boolean italic;

	private static final String BOLD   = "bold";
	private static final String ITALIC = "italic";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FontInfo(String name, int size, boolean bold, boolean italic)
	{
		this.name   = name;
		this.size   = size;
		this.bold   = bold;
		this.italic = italic;
	}

	//---------------------------------------------------------------------------

	public FontInfo(String definition)
	{
		StringTokenizer st = new StringTokenizer(definition, "-");

		name = st.nextToken();
		size = Integer.parseInt(st.nextToken());

		while (st.hasMoreTokens())
			handleStyle(st.nextToken());
	}

	//---------------------------------------------------------------------------

	private void handleStyle(String style)
	{
		if (style.equals(BOLD))   bold   = true;
		if (style.equals(ITALIC)) italic = true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String toString()
	{
		TVector v = new TVector();
		v.setSeparator("-");

		v.add(name);
		v.add(size +"");

		if (bold)   v.add(BOLD);
		if (italic) v.add(ITALIC);

		return v.toString();
	}
}

//==============================================================================
