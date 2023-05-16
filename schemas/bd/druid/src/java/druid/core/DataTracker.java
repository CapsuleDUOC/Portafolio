//==============================================================================
//===
//===   DataTracker
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

//==============================================================================

public class DataTracker
{
	public static final String NAME = "dataTracker";

	private static ActionListener actList;
	private static ActionEvent    actEvent;

	private static boolean bDataChanged;
	private static boolean bChangeEnabled;

	private static Stack stack = new Stack();

	//---------------------------------------------------------------------------

	public static void init(ActionListener al)
	{
		actList  = al;
		actEvent = new ActionEvent("", ActionEvent.ACTION_PERFORMED, NAME);

		bDataChanged   = false;
		bChangeEnabled = true;
	}

	//---------------------------------------------------------------------------

	public static void setEnabled(boolean yesno)
	{
		bChangeEnabled = yesno;
	}

	//---------------------------------------------------------------------------

	public static void beginDisabledSection()
	{
		stack.push(Boolean.valueOf(bChangeEnabled));

		bChangeEnabled = false;
	}

	//---------------------------------------------------------------------------

	public static void endDisabledSection()
	{
		Boolean b = (Boolean) stack.pop();

		bChangeEnabled = b.booleanValue();
	}

	//---------------------------------------------------------------------------

	/** Used by client methods to indicate that data has been written to disk and
	  * that it is changed no longer.
	  */

	public static void reset()
	{
		bDataChanged = false;

		if (actList != null)
			actList.actionPerformed(actEvent);
	}

	//---------------------------------------------------------------------------

	/** Used by client methods to indicate that some internal data is changed so
	  * the system may notify the user when he want to exit.
	  */

	public static void setDataChanged()
	{
		if (bChangeEnabled)
			fireEvent(true);
	}

	//---------------------------------------------------------------------------

	public static boolean isDataChanged()
	{
		return bDataChanged;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static void fireEvent(boolean newDataChanged)
	{
		if (bDataChanged != newDataChanged)
		{
			bDataChanged = newDataChanged;

			if (actList != null)
				actList.actionPerformed(actEvent);
		}
	}
}

//==============================================================================
