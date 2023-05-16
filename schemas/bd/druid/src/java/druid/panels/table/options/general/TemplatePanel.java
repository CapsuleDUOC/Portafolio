//==============================================================================
//===
//===   TemplatePanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.options.general;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.AttribSet;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class TemplatePanel extends TPanel
{
	private TTextFieldGuardian txtPrimKey = new TTextFieldGuardian("tempPK");
	private TTextFieldGuardian txtForKey  = new TTextFieldGuardian("tempFK");
	private TTextFieldGuardian txtUnique  = new TTextFieldGuardian("tempOther");

	private static final String pkTip =	"<HTML>Template to use to build the name of the constraint.<BR>"+
													"Example : PK_{table}{cnt}";

	private static final String fkTip =	"<HTML>Template to use to build the name of the constraint.<BR>"+
													"Example : FK_{table}_{ufields}";

	//---------------------------------------------------------------------------

	public TemplatePanel()
	{
		super("Templates for table constraint names");

		FlexLayout flexL = new FlexLayout(2, 3);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Primary keys"));
		add("0,1",   new TLabel("Foreign keys"));
		add("0,2",   new TLabel("Other"));

		add("1,0,x", txtPrimKey);
		add("1,1,x", txtForKey);
		add("1,2,x", txtUnique);

		txtPrimKey.setToolTipText(pkTip);
		txtForKey .setToolTipText(fkTip);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribSet as)
	{
		txtPrimKey.refresh(as);
		txtForKey .refresh(as);
		txtUnique .refresh(as);
	}
}

//==============================================================================
