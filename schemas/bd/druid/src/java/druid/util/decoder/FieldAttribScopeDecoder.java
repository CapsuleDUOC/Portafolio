//==============================================================================
//===
//===   FieldAttribScopeDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.FieldAttribs;

//==============================================================================

public class FieldAttribScopeDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(FieldAttribs.SCOPE_TABLE))  return "Table";
		if (s.equals(FieldAttribs.SCOPE_FIELD))  return "Field";
		if (s.equals(FieldAttribs.SCOPE_INDEX))  return "Index";
		if (s.equals(FieldAttribs.SCOPE_UINDEX)) return "Index-U";
		if (s.equals(FieldAttribs.SCOPE_FTINDEX)) return "Index-FullText";
		if (s.equals(FieldAttribs.SCOPE_CUSTOM)) return "Custom";

		return null;
	}
}

//==============================================================================
