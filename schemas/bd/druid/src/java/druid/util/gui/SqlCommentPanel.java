//==============================================================================
//===
//===   SqlCommentPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;

import druid.core.AttribSet;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class SqlCommentPanel extends TPanel
{
	private TTextFieldGuardian txtComm  = new TTextFieldGuardian("comment");

	//---------------------------------------------------------------------------

	public SqlCommentPanel()
	{
		super("Sql comment");

		FlexLayout flexL = new FlexLayout(1, 1, 4, 4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x", txtComm);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribSet as)
	{
		txtComm.refresh(as);
	}
}

//==============================================================================
