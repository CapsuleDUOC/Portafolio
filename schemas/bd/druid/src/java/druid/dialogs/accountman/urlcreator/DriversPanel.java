//==============================================================================
//===
//===   DriversPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman.urlcreator;

import java.util.List;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextArea;
import org.dlib.xml.XmlElement;

import druid.core.config.Config;

//==============================================================================

class DriversPanel extends TPanel
{
	private TTextArea txaDrivers = new TTextArea(5,36);

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DriversPanel()
	{
		super("Drivers");

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", txaDrivers);

		txaDrivers.setEditable(false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setDrivers(UrlInfo ui)
	{
		setVisible(ui != null);

		if (ui == null) return;

		StringBuffer sb = new StringBuffer();

		List list = ui.elDb.getChildren("driver");

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elDriver = (XmlElement) list.get(i);

			sb.append(elDriver.getValue() + Config.os.lineSep);
		}

		txaDrivers.setText(sb.toString());
	}
}

//==============================================================================
