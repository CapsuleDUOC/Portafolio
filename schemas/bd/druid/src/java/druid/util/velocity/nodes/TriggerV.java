//==============================================================================
//===
//===   TriggerV
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import org.dlib.tools.HtmlLib;
import org.dlib.tools.TVector;

import druid.data.AbstractNode;
import druid.data.Trigger;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;

//==============================================================================

public class TriggerV extends AbstractNodeV
{
	private TriggerActivationDecoder trgActiv   = new TriggerActivationDecoder();
	private TriggerForEachDecoder    trgForEach = new TriggerForEachDecoder();

	//---------------------------------------------------------------------------

	public TriggerV(AbstractNode node) { super(node); }

	//---------------------------------------------------------------------------

	public String getWhen()     { return as.getString("when");      }
	public String getHtmlWhen() { return HtmlLib.encode(getWhen()); }
	public String getCode()     { return as.getString("code");      }
	public String getHtmlCode() { return HtmlLib.encode(getCode()); }

	public boolean getIsOnInsert() { return as.getBool("onInsert"); }
	public boolean getIsOnUpdate() { return as.getBool("onUpdate"); }
	public boolean getIsOnDelete() { return as.getBool("onDelete"); }

	public boolean getIsActivBefore()    { return as.getString("activation").equals(Trigger.ACTIV_BEFORE);    }
	public boolean getIsActivAfter()     { return as.getString("activation").equals(Trigger.ACTIV_AFTER);     }
	public boolean getIsActivInsteadOf() { return as.getString("activation").equals(Trigger.ACTIV_INSTEADOF); }

	public boolean getIsForEachStatem() { return as.getString("forEach").equals(Trigger.FOREACH_STATEMENT); }
	public boolean getIsForEachRow()    { return as.getString("forEach").equals(Trigger.FOREACH_ROW);       }

	public String getActivString()   { return trgActiv.decode(as.getString("activation")); }
	public String getForEachString() { return trgForEach.decode(as.getString("forEach"));  }

	//---------------------------------------------------------------------------

	public String getActivEvent()
	{
		TVector v = new TVector();

		v.setSeparator(" OR ");

		if (getIsOnInsert()) v.add("INSERT");
		if (getIsOnUpdate()) v.add("UPDATE");
		if (getIsOnDelete()) v.add("DELETE");

		return v.toString();
	}

}

//==============================================================================
