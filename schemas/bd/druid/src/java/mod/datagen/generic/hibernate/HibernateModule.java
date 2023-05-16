//==============================================================================
//===
//===   HibernateModule
//===
//===   Copyright (C) by Andrea Carboni, Damien Boucquey
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.hibernate;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JComponent;

import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlWriter;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.TableNode;
import druid.interfaces.BasicModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;

import mod.datagen.generic.hibernate.panels.database.DatabasePanel;
import mod.datagen.generic.hibernate.panels.field.FieldPanel;
import mod.datagen.generic.hibernate.panels.table.TablePanel;

//==============================================================================

public class HibernateModule implements GenericGenModule
{
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "hibernate";      }
	public String getVersion()  { return "1.0";            }
	public String getAuthor()   { return "Andrea Carboni, Damien Boucquey"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates the object-relational mapping for Hibernate.";
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

	public String  getFormat()        { return "Hibernate Mapping"; }
	public boolean isDirectoryBased() { return true; }
	public boolean hasLargePanel()    { return true; }

	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		l.logHeader("Hibernate Mapping");

		String outDir = dbNode.modsConfig.getValue(this, "output");

		if (outDir.equals(""))
		{
			l.log(Logger.ALERT, "The 'output' field cannot be empty.");
			l.log(Logger.ALERT, "Generation aborted.");
			return;
		}

		//------------------------------------------------------------------------

		Vector tables = dbNode.getObjects(TableNode.class);

		File f = new File(outDir);
		f.mkdirs();

		if (!outDir.endsWith("/"))
			outDir += "/";

		Generator gen = new Generator(l);

		for (int i = 0; i < tables.size(); i++)
			try
			{
				TableNode node = (TableNode) tables.get(i);
				String    name = node.attrSet.getString("name");

				XmlDocument doc = new XmlDocument(gen.generate(this, dbNode, node));

				doc.setDocType("hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 2.0//EN\" "+
		        					"\"http://hibernate.sourceforge.net/hibernate-mapping-2.0.dtd\"");

				new XmlWriter().write(outDir + name + ".hbm.xml", doc);

			}
			catch(IOException e)
			{
				l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
			}

		l.log(Logger.INFO, "Done.");
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
			dbPanel.refresh(new DatabaseSettings(node.modsConfig, HibernateModule.this));
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
			tablePanel.refresh(new TableSettings(node.modsConfig, HibernateModule.this));
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
			fieldPanel.refresh(new FieldSettings(node.modsConfig, HibernateModule.this));
		}

		//---------------------------------------------------------------------------

		public JComponent getPanel() { return fieldPanel; }
	};
}

//==============================================================================
