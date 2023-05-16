//==============================================================================
//===
//===   DruidException
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

//==============================================================================

public class DruidException extends RuntimeException
{
	public static final int ILL_ARG  = 0; //--- illegal argument
	public static final int INC_STR  = 1; //--- inconsistent internal structure
	public static final int BAD_FRM  = 2; //--- bad format for a file
	public static final int OPE_ABO  = 3; //--- operation aborted

	public int    iType;
	public String sMessage;
	public Object oParam;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DruidException(int type, String message)
	{
		this(type, message, null);
	}

	//---------------------------------------------------------------------------

	public DruidException(int type, String message, int param)
	{
		this(type, message, "" + param);
	}

	//---------------------------------------------------------------------------

	public DruidException(int type, String message, Object param)
	{
		super(message);
		
		iType    = type;
		sMessage = message;
		oParam   = param;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String toString()
	{
		return	"---------------------------------------------\n" +
					"Druid Error\n\n" +
					"Type   : " + getType() + "\n" +
					"Message: " + sMessage  + "\n" +
					"Object : " + oParam    + "\n" +
					"---------------------------------------------\n";
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String getType()
	{
		switch(iType)
		{
			case ILL_ARG:
				return "Illegal Argument";

			case INC_STR:
				return "Inconsistent Structure";

			case BAD_FRM:
				return "Bad Format";

			case OPE_ABO:
				return "Operation Aborted";

			default:
				return "<unknown type>";
		}
	}
}

//==============================================================================
