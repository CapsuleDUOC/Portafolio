//==============================================================================
//===
//===   TorqueModule
//===
//===   Copyright (C) by Andrea Carboni
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque;

import java.io.IOException;

import javax.swing.JComponent;

import mod.datagen.generic.torque.panels.database.DatabasePanel;
import mod.datagen.generic.torque.panels.field.FieldPanel;
import mod.datagen.generic.torque.panels.table.TablePanel;

import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlWriter;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.interfaces.BasicModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class TorqueModule implements GenericGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "torque"; }
	public String getVersion()  { return "1.0";    }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the database in XML using the torque schema format";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == BasicModule.DATABASE) return databaseOptions;
		if (env == BasicModule.TABLE)		return tableOptions;
		if (env == BasicModule.FIELD)		return fieldOptions;

		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- GenericGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Torque Schema"; }
	public boolean isDirectoryBased() { return false; }
	public boolean hasLargePanel()    { return false; }

	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		l.logHeader("Torque XML Schema");

		String fileName = dbNode.modsConfig.getValue(this, "output");

		Generator gen = new Generator(l);

		XmlElement elRoot = gen.generate(this, dbNode);

		try
		{
			new XmlWriter().write(fileName, new XmlDocument(elRoot));

			l.log(Logger.INFO, "Done.");
		}
		catch(IOException e)
		{
			l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- ModuleOptions handlers
	//---
	//---------------------------------------------------------------------------

	//---------------------------------------------------------------------------
	//--- Env : DATABASE
	//---------------------------------------------------------------------------

	private ModuleOptions databaseOptions = new ModuleOptions()
	{
		private DatabasePanel dbPanel = new DatabasePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			dbPanel.refresh(new DatabaseSettings(node.modsConfig, TorqueModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return dbPanel; }
	};

	//---------------------------------------------------------------------------
	//--- Env : TABLE
	//---------------------------------------------------------------------------

	private ModuleOptions tableOptions = new ModuleOptions()
	{
		private TablePanel tablePanel = new TablePanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			tablePanel.refresh(new TableSettings(node.modsConfig, TorqueModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return tablePanel; }
	};

	//---------------------------------------------------------------------------
	//--- Env : FIELD
	//---------------------------------------------------------------------------

	private ModuleOptions fieldOptions = new ModuleOptions()
	{
		private FieldPanel fieldPanel = new FieldPanel();

		//---------------------------------------------------------------------------

		public void refresh(AbstractNode node)
		{
			fieldPanel.refresh(new FieldSettings(node.modsConfig, TorqueModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return fieldPanel; }
	};
}

//==============================================================================
