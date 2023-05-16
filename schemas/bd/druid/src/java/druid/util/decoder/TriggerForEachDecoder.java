//==============================================================================
//===
//===   TriggerForEachDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.Trigger;

//==============================================================================

public class TriggerForEachDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(Trigger.FOREACH_ROW))       return "ROW";
		if (s.equals(Trigger.FOREACH_STATEMENT)) return "STATEMENT";

		return null;
	}
}

//==============================================================================
