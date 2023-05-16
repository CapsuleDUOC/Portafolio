//==============================================================================
//===
//===   OnClauseDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.FieldNode;

//==============================================================================

public class OnClauseDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(FieldNode.CASCADE))    return "CASCADE";
		if (s.equals(FieldNode.SETDEFAULT)) return "SET DEFAULT";
		if (s.equals(FieldNode.SETNULL))    return "SET NULL";
		if (s.equals(FieldNode.NOACTION))   return "NO ACTION";
		if (s.equals(FieldNode.RESTRICT))   return "RESTRICT";

		return null;
	}
}

//==============================================================================
