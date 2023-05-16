//==============================================================================
//===
//===   FieldAttribPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel.field;

import java.util.Vector;

import mod.treeview.sqldiff.dialog.result.panel.AttribPanel;
import mod.treeview.sqldiff.struct.DiffElement;
import mod.treeview.sqldiff.struct.DiffEntity;

import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;

import druid.util.decoder.Decoder;
import druid.util.decoder.FieldAttribScopeDecoder;
import druid.util.decoder.FieldAttribTypeDecoder;
import druid.util.gui.ImageFactory;

//==============================================================================

public class FieldAttribPanel extends AttribPanel
{
	private FieldAttribTypeDecoder  decType  = new FieldAttribTypeDecoder();
	private FieldAttribScopeDecoder decScope = new FieldAttribScopeDecoder();

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	/** this method should not be used */

	public void setAttribs(DiffElement diff, String name, Decoder dec) {}

	//---------------------------------------------------------------------------

	public void setAttrib(DiffEntity ent)
	{
		if (ent == null) return;

		DiffElement sql   = ent.get(DiffEntity.FA_SQLNAME);
		DiffElement type  = ent.get(DiffEntity.FA_TYPE);
		DiffElement scope = ent.get(DiffEntity.FA_SCOPE);
		DiffElement value = ent.get(DiffEntity.FA_VALUE);

		if (ent.isAdded())
		{
			Vector row = new Vector();

			row.add(ImageFactory.NEW);
			row.add(sql.objNewValue);
			row.add(decType .decode(type .objNewValue.toString()));
			row.add(decScope.decode(scope.objNewValue.toString()));
			row.add(value.objNewValue);

			newModel.addRow(row);
		}

		else if (ent.isRemoved())
		{
			Vector row = new Vector();

			row.add(ImageFactory.DELETE);
			row.add(sql.objOldValue);
			row.add(decType .decode(type .objOldValue.toString()));
			row.add(decScope.decode(scope.objOldValue.toString()));
			row.add(value.objOldValue);

			oldModel.addRow(row);
		}

		else
		{
			Vector row = new Vector();

			row.add(ImageFactory.LENS);
			row.add(sql.objNewValue);
			row.add(decType .decode(type .objNewValue.toString()));
			row.add(decScope.decode(scope.objNewValue.toString()));
			row.add(value.objOldValue);

			oldModel.addRow(row);

			row = new Vector();

			row.add(ImageFactory.LENS);
			row.add(sql.objNewValue);
			row.add(decType .decode(type .objNewValue.toString()));
			row.add(decScope.decode(scope.objNewValue.toString()));
			row.add(value.objNewValue);

			newModel.addRow(row);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	protected void setupTable(FlexTable flex, DefaultFlexTableModel model)
	{
		model.addColumn("Oper",    30);
		model.addColumn("Attrib", 120);
		model.addColumn("Type",   120);
		model.addColumn("Scope",  120);
		model.addColumn("Value",  100);

		flex.setFlexModel(model);
	}
}

//==============================================================================
