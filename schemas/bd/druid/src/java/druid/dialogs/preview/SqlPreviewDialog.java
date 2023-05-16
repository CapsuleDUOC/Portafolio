//==============================================================================
//===
//===   SqlPreviewDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Enumeration;

import javax.swing.JComponent;

import org.dlib.gui.TDialog;
import org.dlib.gui.TTabbedPane;

import druid.core.config.Config;
import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.interfaces.SqlGenModule;
import druid.util.gui.SqlTextArea;
import factory.sql.algorithm.AbstractGenerator;

//==============================================================================

public class SqlPreviewDialog extends TDialog
{
	private TTabbedPane jtPane = new TTabbedPane();

	private String LF = Config.os.lineSep;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlPreviewDialog(Frame frame)
	{
		super(frame, "Sql Preview", true);

		jtPane.setPreferredSize(new Dimension(750,550));

		getContentPane().add(jtPane, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void run(AbstractNode node)
	{
		buildSql(node);
		showDialog();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void buildSql(AbstractNode node)
	{
		jtPane.removeAll();

		DatabaseNode dbNode = node.getDatabase();

		Enumeration e = ModuleManager.getModules(SqlGenModule.class);

		for(; e.hasMoreElements();)
		{
			SqlGenModule sqlMod = (SqlGenModule) e.nextElement();

			if (dbNode.modsUsage.contains(sqlMod))
			{
				String sql = new AbstractGenerator(null, sqlMod, dbNode).generate(node);

				JComponent c = buildComponent(sql);

				jtPane.addTab(sqlMod.getFormat(), c);
			}
		}
	}

	//---------------------------------------------------------------------------

	private JComponent buildComponent(String text)
	{
		SqlTextArea txaSql = new SqlTextArea();

		txaSql.setEditable(false);
		txaSql.setText(text);
		txaSql.scrollTo(0,0);

		return txaSql;
	}
}

//==============================================================================
