//==============================================================================
//===
//===   MatchTypeDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.FieldNode;

//==============================================================================

public class MatchTypeDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(FieldNode.FULL))    return "FULL";
		if (s.equals(FieldNode.PARTIAL)) return "PARTIAL";
		if (s.equals(FieldNode.SIMPLE))  return "SIMPLE";

		return null;
	}
}

//==============================================================================
