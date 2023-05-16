//==============================================================================
//===
//===   SynonymPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.jdbc.structure.synonym;

import druid.core.jdbc.entities.RecordBasedEntity;
import druid.util.gui.Dialogs;
import druid.util.jdbc.dataeditor.DataEditorPanel;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TTabbedPane;

//==============================================================================

public class SynonymPanel extends TTabbedPane implements ChangeListener
{
	private static final int TIMEOUT = 20; //--- max seconds to wait for jdbc thread

	private DataEditorPanel   dataPanel = new DataEditorPanel();
	private boolean           bDataRefreshed;
	private RecordBasedEntity rbeNode;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SynonymPanel()
	{
		addTab("Data", dataPanel);

		addChangeListener(this);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public boolean refresh(final RecordBasedEntity node)
	{
	  rbeNode        = node;
		bDataRefreshed = false;

		final Vector vExc = new Vector();

		//------------------------------------------------------------------------
		//--- create runnable object

		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					node.loadInfo();
				}
				catch(SQLException e)
				{
					vExc.addElement(e);
				}
			}
		};

		//------------------------------------------------------------------------
		//--- create and start reading thread

		Thread loader = new Thread(run);
		loader.start();

		while(true)
		{
			try
			{
				loader.join(TIMEOUT*1000);
			}
			catch(InterruptedException e) {}

			if (loader.isAlive())
			{
				//--- if the thread doesn't join, we need to make decisions

				Object[] btns = { "Ok", "Wait more"};

				int resp = JOptionPane.showOptionDialog(this,
				"The process has taken too long to finish.\n" +
				"Because stopping a thread is an unsafe operation,\n" +
				"it has been left to its destiny hoping that it will\n" +
				"finish in the near future. However, if you encounter\n" +
				"anomalies I suggest you to disconnect from jdbc\n" +
				"and check your dbms state.",
				"Please Note",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, btns, btns[0]);

				if (resp == 0)
				{
					setNullData();
					return false;
				}
			}
			else
				break;
		}

		//--- phase 2

		if (vExc.size() != 0)
		{
			Dialogs.showException((SQLException) vExc.get(0));
			setNullData();

			return false;
		}
		else
		{
			if (getSelectedComponent() instanceof DataEditorPanel)
			{
				dataPanel.setNode(node);
				bDataRefreshed = true;
			}

			return true;
		}
	}

	//---------------------------------------------------------------------------

	private void setNullData()
	{
		dataPanel.clearNode();

		rbeNode = null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- ChangeListener
	//---
	//---------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		if (rbeNode != null && !bDataRefreshed)
			if (getSelectedComponent() instanceof DataEditorPanel)
			{
				GuiUtil.setWaitCursor(this, true);
				dataPanel.setNode(rbeNode);
				GuiUtil.setWaitCursor(this, false);
				bDataRefreshed = true;
			}
	}
}

//==============================================================================
