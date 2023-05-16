//==============================================================================
//===
//===   TriggerActivationDecoder
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.decoder;

import druid.data.Trigger;

//==============================================================================

public class TriggerActivationDecoder implements Decoder
{
	public String decode(String s)
	{
		if (s.equals(Trigger.ACTIV_BEFORE))    return "BEFORE";
		if (s.equals(Trigger.ACTIV_AFTER))     return "AFTER";
		if (s.equals(Trigger.ACTIV_INSTEADOF)) return "INSTEAD OF";

		return null;
	}
}

//==============================================================================
