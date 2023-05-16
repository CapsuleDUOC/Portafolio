//==============================================================================
//===
//===   ErEntity
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.data.er;

import java.util.Vector;

import org.dlib.gui.treeview.TreeViewNode;

import druid.data.AbstractNode;
import druid.data.TableNode;

//==============================================================================

public class ErEntity extends AbstractNode
{
	//---------------------------------------------------------------------------
	//--- details constants

	public static final String DEFAULT      = "-";
	public static final String ONLY_NAME    = "on";
	public static final String NAME_AND_PKS = "np";
	public static final String NAME_PKS_FKS = "pf";
	public static final String ALL_FIELDS   = "af";
	public static final String COMPLETE     = "co";

	//--- this vector will contain all ids of tables that form the entity
	//--- class of values : Integer

	private Vector vTablesId = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ErEntity()
	{
		this("-UnNames-");
	}

	//---------------------------------------------------------------------------

	public ErEntity(String name)
	{
		super(name);

		attrSet.addAttrib("colorId",  0);
		attrSet.addAttrib("locX",     0);
		attrSet.addAttrib("locY",     0);
		attrSet.addAttrib("details",  DEFAULT);

		setToolTipText("An E/R entity representing on or more tables");
	}

	//---------------------------------------------------------------------------
	protected TreeViewNode getNewInstance() { return new ErEntity(); }
	//---------------------------------------------------------------------------

	public void copyTo(TreeViewNode node)
	{
		ErEntity n = (ErEntity) node;

		n.vTablesId = new Vector();

		for(int i=0; i<vTablesId.size(); i++)
			n.vTablesId.addElement(new Integer(getTableAt(i)));

		super.copyTo(node);
	}

	//---------------------------------------------------------------------------

	public String getDetails()
	{
		String details = attrSet.getString("details");

		if (details.equals(DEFAULT))
			return getErView().attrSet.getString("details");

		return details;
	}

	//---------------------------------------------------------------------------

	public LegendColor getLegendColor()
	{
		int colId = attrSet.getInt("colorId");

		return getErView().legend.getColor(colId);
	}

	//---------------------------------------------------------------------------

	public void setDefLegendColor()
	{
		attrSet.setInt("colorId", getErView().legend.getFirstColorId());
	}

	//---------------------------------------------------------------------------

	public void addTable(int id)
	{
		vTablesId.addElement(new Integer(id));
	}

	//---------------------------------------------------------------------------

	public void removeTable(int id)
	{
		for(int i=0; i<vTablesId.size(); i++)
		{
			int currId = ((Integer)vTablesId.elementAt(i)).intValue();

			if (id == currId)
			{
				vTablesId.removeElementAt(i);
				return;
			}
		}
	}

	//---------------------------------------------------------------------------

	public void removeTableAt(int pos)
	{
		vTablesId.removeElementAt(pos);
	}

	//---------------------------------------------------------------------------

	public boolean existsTable(int id)
	{
		for(int i=0; i<vTablesId.size(); i++)
		{
			int currId = ((Integer)vTablesId.elementAt(i)).intValue();

			if (id == currId) return true;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	public int getTableAt(int pos)
	{
		return ((Integer)vTablesId.elementAt(pos)).intValue();
	}

	//---------------------------------------------------------------------------

	public void setTableAt(int pos, int table)
	{
		vTablesId.setElementAt(new Integer(table), pos);
	}

	//---------------------------------------------------------------------------

	public TableNode getTableNodeAt(int pos)
	{
		return getDatabase().getTableByID(getTableAt(pos));
	}

	//---------------------------------------------------------------------------

	public int getTableNum()
	{
		return vTablesId.size();
	}

	//---------------------------------------------------------------------------

	public ErView getErView() { return (ErView) getParent(); }

	//---------------------------------------------------------------------------

	public void updateData()
	{
		for(int i=0; i<vTablesId.size(); i++)
			if (getTableNodeAt(i) == null) vTablesId.removeElementAt(i--);
	}
}

//==============================================================================
