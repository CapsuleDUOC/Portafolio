//==============================================================================
//===
//===   ConstraintPanel
//===
//===   Copyright (C) by Helmut Reichhold.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.table.constraints;

import javax.swing.JPanel;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TSplitPane;
import druid.core.jdbc.entities.TableEntity;

//==============================================================================
//--- constraints support

public class ConstraintPanel extends JPanel
{
	private ConstraintView constraintView = new ConstraintView();
	private WorkPanel   workPanel   = new WorkPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ConstraintPanel()
	{
		FlexLayout flexL = new FlexLayout(1, 1, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		constraintView.setDataModel(workPanel);

		add("0,0,x,x", new TSplitPane(constraintView, workPanel));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableEntity ent)
	{
		constraintView.setConstraintNode(ent.constraints);
	}
}

//==============================================================================
