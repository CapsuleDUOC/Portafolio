//==============================================================================
//===
//===   SchemaPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.table.options.general;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import druid.core.AttribSet;
import druid.data.TableNode;
import druid.util.gui.ChangeSentinel;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class GenOptionsPanel extends TPanel
{
	private TableNode node;
	
	private TTextFieldGuardian txtSchema = new TTextFieldGuardian("schema");
	private TCheckBox          chbGhost  = new TCheckBox("Ghost table");

	//---------------------------------------------------------------------------

	public GenOptionsPanel()
	{
		super("General options");

		FlexLayout flexL = new FlexLayout(2, 3, 4, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Schema"));
		
		add("1,0,x",     txtSchema);
		add("0,2,x,c,2", chbGhost);
		
		chbGhost.addItemListener(ChangeSentinel.getInstance());
		chbGhost.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e) 
			{
				node.refreshIcon();
			}
		});
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(TableNode node)
	{
		this.node = node;
		
		AttribSet as = node.attrSet;
		
		txtSchema.refresh(as);
		chbGhost .setSelected(as.getBool("ghost"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(TableNode node)
	{
		AttribSet as = node.attrSet;

		as.setBool("ghost", chbGhost.isSelected());
	}
}

//==============================================================================
