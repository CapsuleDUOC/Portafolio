//==============================================================================
//===
//===   DiffEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.struct;

import java.util.List;
import java.util.Vector;

//==============================================================================

public class DiffEntity
{
	//--- operation on entity

	public static final int NONE    = 0;
	public static final int ADDED   = 1;
	public static final int REMOVED = 2;
	public static final int CHANGED = 3;

	//--- database sub-entities

	public static final int DB_PRESQL         = 0; //--- String
	public static final int DB_POSTSQL        = 1; //--- String

	//---procedure/function/view sub-entities

	public static final int PFV_SQLCODE = 0; //--- String

	//--- table sub-entities

	//--- trigger sub-entities

	public static final int TR_ACTIVATION = 0; //--- String / dec
	public static final int TR_ONINSERT   = 1; //--- Boolean
	public static final int TR_ONUPDATE   = 2; //--- Boolean
	public static final int TR_ONDELETE   = 3; //--- Boolean
	public static final int TR_FOREACH    = 4; //--- String / dec
	public static final int TR_WHEN       = 5; //--- String
	public static final int TR_CODE       = 6; //--- String

	//--- field sub-entities

	public static final int FI_TYPE     = 0; //--- String
	public static final int FI_FKEY     = 1; //--- String
	public static final int FI_ONUPDATE = 2; //--- String / dec
	public static final int FI_ONDELETE = 3; //--- String / dec

	//--- fieldattribs sub-entities

	public static final int FA_SQLNAME = 0; //--- String
	public static final int FA_TYPE    = 1; //--- String
	public static final int FA_SCOPE   = 2; //--- String
	public static final int FA_VALUE   = 3; //--- String

	//--- sequence sub-entities

	public static final int SQ_INCREM  = 0; //--- String
	public static final int SQ_MINVAL  = 1; //--- String
	public static final int SQ_MAXVAL  = 2; //--- String
	public static final int SQ_START   = 3; //--- String
	public static final int SQ_CACHE   = 4; //--- String
	public static final int SQ_CYCLE   = 5; //--- Boolean
	public static final int SQ_ORDER   = 6; //--- Boolean

	//--- table rule sub-entities

	public static final int RU_USE    = 0; //--- Boolean
	public static final int RU_RULE   = 1; //--- String

	//---------------------------------------------------------------------------

	private String     sName;
	private int        iOperation;
	private DiffEntity entParent;
	private Vector     vDiffElements = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DiffEntity(String name, int oper)
	{
		this(name, oper, null);
	}

	//---------------------------------------------------------------------------

	public DiffEntity(String name, int oper, DiffEntity parent)
	{
		sName      = name;
		iOperation = oper;
		entParent  = parent;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String     getName()   { return sName;                 }
	public DiffEntity getParent() { return entParent;             }
	public boolean    isNone()    { return iOperation == NONE;    }
	public boolean    isAdded()   { return iOperation == ADDED;   }
	public boolean    isRemoved() { return iOperation == REMOVED; }
	public boolean    isChanged() { return iOperation == CHANGED; }

	//---------------------------------------------------------------------------

	public void add (DiffElement elem)
	{
		vDiffElements.add(elem);
	}

	//---------------------------------------------------------------------------

	public void added(int subEntity, Object value)
	{
		vDiffElements.add(new DiffElement(subEntity, null, value));
	}

	//---------------------------------------------------------------------------

	public void removed(int subEntity, Object value)
	{
		vDiffElements.add(new DiffElement(subEntity, value, null));
	}

	//---------------------------------------------------------------------------

	public void changed(int subEntity, Object oldValue, Object newValue)
	{
		vDiffElements.add(new DiffElement(subEntity, oldValue, newValue));
	}

	//---------------------------------------------------------------------------

	public DiffElement get(int subEntity)
	{
		for(int i=0; i<vDiffElements.size(); i++)
		{
			DiffElement e = (DiffElement) vDiffElements.get(i);

			if (e.iSubEntity == subEntity)
				return e;
		}

		return null;
	}

	//---------------------------------------------------------------------------

	public List list()
	{
		return vDiffElements;
	}

	//---------------------------------------------------------------------------

	public List list(int subEntity)
	{
		Vector vResult = new Vector();

		for(int i=0; i<vDiffElements.size(); i++)
		{
			DiffElement e = (DiffElement) vDiffElements.get(i);

			if (e.iSubEntity == subEntity)
				vResult.add(e);
		}

		return vResult;
	}

	//---------------------------------------------------------------------------

	public boolean isEmpty() { return vDiffElements.isEmpty(); }

	//---------------------------------------------------------------------------

	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("[DEN: ");
		sb.append("name="+sName +", ");
		sb.append("oper="+iOperation +"]");

		return sb.toString();
	}
}

//==============================================================================
