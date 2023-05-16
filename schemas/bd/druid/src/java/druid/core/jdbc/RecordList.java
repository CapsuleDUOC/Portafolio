//==============================================================================
//===
//===   RecordList
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc;

import java.util.Enumeration;
import java.util.Vector;

import org.dlib.gui.flextable.FlexTableColumn;
import org.dlib.gui.flextable.FlexTableModel;

//==============================================================================

public class RecordList implements FlexTableModel
{
	private Vector vHeaders = new Vector();
	private Vector vRecords = new Vector();
	private Vector vCurrRec;

	//---------------------------------------------------------------------------

	public RecordList() {}

	//---------------------------------------------------------------------------

	public FlexTableColumn addColumn(String label, int width)
	{
		FlexTableColumn ftc = new FlexTableColumn(label, width);
		ftc.setEditable(false);
		vHeaders.addElement(ftc);

		return ftc;
	}

	//---------------------------------------------------------------------------

	public FlexTableColumn insertColumn(int index, String label, int width)
	{
		FlexTableColumn ftc = new FlexTableColumn(label, width);
		ftc.setEditable(false);
		vHeaders.insertElementAt(ftc, index);

		for(int i=0; i<vRecords.size(); i++)
		{
			Vector row = (Vector) vRecords.elementAt(i);
			row.insertElementAt(null, index);
		}

		return ftc;
	}

	//---------------------------------------------------------------------------

	public void setColumn(int index, String label, int width)
	{
		FlexTableColumn ftc = (FlexTableColumn)vHeaders.elementAt(index);

		ftc.setHeaderValue(label);
		ftc.setPreferredWidth(width);
	}

	//---------------------------------------------------------------------------

	public void newRecord()
	{
		vCurrRec = new Vector();
		vRecords.addElement(vCurrRec);
	}

	//---------------------------------------------------------------------------

	public void newRecord(Vector row)
	{
		vCurrRec = row;
		vRecords.addElement(vCurrRec);
	}

	//---------------------------------------------------------------------------

	public void addToRecord(Object data)
	{
		vCurrRec.addElement(data);
	}

	//---------------------------------------------------------------------------

	public void removeColumn(int index)
	{
		vHeaders.removeElementAt(index);

		for(int i=0; i< vRecords.size(); i++)
			((Vector)vRecords.elementAt(i)).removeElementAt(index);
	}

	//---------------------------------------------------------------------------

	public void removeRecord(int index)
	{
		vRecords.removeElementAt(index);
	}

	//---------------------------------------------------------------------------

	public void removeRecord(String label, Object obj)
	{
		int col = getColumnIndex(label);

		if(col == -1) return;

		for (Enumeration e = vRecords.elements() ; e.hasMoreElements() ;)
		{
			Vector row = (Vector) e.nextElement();

			if(row.elementAt(col).equals(obj))
				vRecords.removeElement(row);
		}
	}

	//---------------------------------------------------------------------------

	public Vector getRecordAt(int index)
	{
		return (Vector)vRecords.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public int getColumnIndex(String label)
	{
		label = label.toLowerCase();

		for(int i=0; i<vHeaders.size(); i++)
		{
			FlexTableColumn ftc = (FlexTableColumn)vHeaders.elementAt(i);

			String currHeader = ftc.getHeaderValue().toString();

			if (label.equals(currHeader.toLowerCase())) return i;
		}

		return -1;
	}

	//---------------------------------------------------------------------------
	//---
	//---   FlexTableModel methods
	//---
	//---------------------------------------------------------------------------

	public int getColumnCount()
	{
		return vHeaders.size();
	}

	//---------------------------------------------------------------------------

	public int getRowCount()
	{
		return vRecords.size();
	}

	//---------------------------------------------------------------------------

	public FlexTableColumn getColumnAt(int index)
	{
		if ((index < 0) || (index >= vHeaders.size())) return null;

		return (FlexTableColumn)vHeaders.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public Object getValueAt(int row, int col)
	{
		return ((Vector)vRecords.elementAt(row)).elementAt(col);
	}

	//---------------------------------------------------------------------------

	public void setValueAt(Object o, int row, int col)
	{
		Vector v = (Vector)vRecords.elementAt(row);
		v.setElementAt(o, col);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Debug
	//---
	//---------------------------------------------------------------------------

	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("[HEADER]");
		sb.append("\n");

		for(int i=0; i<vHeaders.size(); i++)
		{
			FlexTableColumn ftc = (FlexTableColumn) vHeaders.get(i);
			sb.append(ftc.getHeaderValue().toString() +", ");
		}

		sb.append("\n");
		sb.append("\n");
		sb.append("[DATA]");
		sb.append("\n");

		for(int i=0; i<vRecords.size(); i++)
		{
			Vector row = (Vector) vRecords.get(i);

			for(int j=0; j<vHeaders.size(); j++)
			{
				sb.append(row.get(j) +", ");
			}

			sb.append("\n");
		}

		return sb.toString();
	}
}

//==============================================================================
