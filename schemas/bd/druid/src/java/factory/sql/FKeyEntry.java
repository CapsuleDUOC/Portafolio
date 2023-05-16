//==============================================================================
//===
//===   FKeyEntry
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.sql;

import org.dlib.tools.TVector;

//==============================================================================

public class FKeyEntry
{
	public TVector vFields   = new TVector();
	public TVector vFkFields = new TVector();

	public String fkTable;
	public String matchType;
	public String onUpd;
	public String onDel;

	//---------------------------------------------------------------------------

	public FKeyEntry(String field, String fkTable, String fkField, String matchType, String onUpd, String onDel)
	{
		this.fkTable  = fkTable;
		this.matchType= matchType;
		this.onUpd    = onUpd;
		this.onDel    = onDel;

		vFields.add(field);
		vFkFields.add(fkField);
	}

	//---------------------------------------------------------------------------

	public boolean merge(FKeyEntry fke)
	{
		if (!fkTable.equals(fke.fkTable))
			return false;

		String field   = (String) fke.vFields.elementAt(0);
		String fkField = (String) fke.vFkFields.elementAt(0);

		if (vFkFields.contains(fkField))
			return false;

		vFields.add(field);
		vFkFields.add(fkField);

		return true;
	}
}

//==============================================================================
