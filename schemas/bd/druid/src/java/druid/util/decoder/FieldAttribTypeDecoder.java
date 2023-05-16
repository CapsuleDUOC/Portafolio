//==============================================================================
//===
//===   FieldAttribTypeDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.FieldAttribs;

//==============================================================================

public class FieldAttribTypeDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(FieldAttribs.TYPE_BOOL))    return "Bool";
		if (s.equals(FieldAttribs.TYPE_INT))     return "Int";
		if (s.equals(FieldAttribs.TYPE_STRING))  return "String";

		return null;
	}
}

//==============================================================================
