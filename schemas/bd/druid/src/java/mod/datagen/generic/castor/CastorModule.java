//==============================================================================
//===
//===   CastorModule
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.castor;

import java.io.IOException;

import javax.swing.JComponent;

import mod.datagen.generic.castor.panels.database.DatabasePanel;
import mod.datagen.generic.castor.panels.field.FieldPanel;
import mod.datagen.generic.castor.panels.table.TablePanel;

import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlWriter;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.interfaces.GenericGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;

//==============================================================================

public class CastorModule implements GenericGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "castor"; }
	public String getVersion()  { return "2.0";    }
	public String getAuthor()   { return "Michael Hevery, Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates Castor JDO mapping xml file.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE) return databaseOptions;
		if (env == TABLE)		return tableOptions;
		if (env == FIELD)		return fieldOptions;

		return null;
	}

	//---------------------------------------------------------------------------
	//---
	//--- GenericGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Castor Mapping"; }
	public boolean isDirectoryBased() { return false;            }
	public boolean hasLargePanel()    { return true;             }

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		l.logHeader("Castor Mapping");

		String fileName = dbNode.modsConfig.getValue(this, "output");

		Generator gen = new Generator(l, this, dbNode);

		XmlDocument doc = new XmlDocument(gen.generate());

		doc.setDocType("mapping PUBLIC \"-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN\" "+
							"\"http://castor.exolab.org/mapping.dtd\"");

		try
		{
			new XmlWriter().write(fileName, doc);

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
			dbPanel.refresh(new DatabaseSettings(node.modsConfig, CastorModule.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, CastorModule.this), node.getDatabase());
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
			fieldPanel.refresh(new FieldSettings(node.modsConfig, CastorModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return fieldPanel; }
	};
}

//==============================================================================
