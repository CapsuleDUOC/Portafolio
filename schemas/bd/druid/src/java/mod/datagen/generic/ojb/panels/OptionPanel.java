//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Antonio Gallardo.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.ojb.panels;

import mod.datagen.generic.ojb.Settings;

import org.dlib.gui.TTabbedPane;

//==============================================================================

/**
 *  OJB Main Configuration Panel
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: OptionPanel.java,v 1.4.2.1 2006/01/17 01:54:48 antoniog Exp $
*/

public class OptionPanel extends TTabbedPane
{
	private JdbcPanel panJdbc = new JdbcPanel();
	private OjbPanel  panOjb  = new OjbPanel();

	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		addTab("JDBC Connection", panJdbc);
		addTab("Apache OJB",      panOjb);
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		panJdbc.refresh(s);
		panOjb.refresh(s);
	}
}

//==============================================================================
