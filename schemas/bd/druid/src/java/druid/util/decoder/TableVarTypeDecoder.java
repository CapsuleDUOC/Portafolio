//==============================================================================
//===
//===   TableVarTypeDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.TableVars;

//==============================================================================

public class TableVarTypeDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(TableVars.BOOL))   return "Bool";
		if (s.equals(TableVars.CHAR))   return "Char";
		if (s.equals(TableVars.DOUBLE)) return "Double";
		if (s.equals(TableVars.FLOAT))  return "Float";
		if (s.equals(TableVars.INT))    return "Integer";
		if (s.equals(TableVars.LONG))   return "Long";
		if (s.equals(TableVars.STRING)) return "String";

		return null;
	}
}

//==============================================================================
