//==============================================================================
//===
//===   Config
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.dlib.tools.RecentFiles;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlWriter;
import org.dlib.xml.reader.XmlReader;

import druid.core.config.erview.ErView;
import druid.core.config.general.General;
import druid.core.config.jdbc.Jdbc;
import druid.core.jdbc.manager.JdbcManager;

//==============================================================================

public class Config
{
	//---------------------------------------------------------------------------
	//--- configuration constants

	private static final String CONFIG_FILE  = "config.xml";

	//--- tags

	private static final String CONFIG       = "config";
	private static final String RECENT_FILES = "recentFiles";
	private static final String FILE         = "file";

	//---------------------------------------------------------------------------
	//--- Configuration vars

	//--- vars from environment

	public static User user;
	public static Dir  dir;
	public static Os   os;

	//--- vars from configuration

	public static final Jdbc        jdbc        = new Jdbc();
	public static final ErView      erView      = new ErView();
	public static final General     general     = new General();
	public static final RecentFiles recentFiles = new RecentFiles();

	//---------------------------------------------------------------------------
	//---
	//--- Init method
	//---
	//---------------------------------------------------------------------------

	public static void init(String druidDir)
	{
		user = new User();
		os   = new Os();
		dir  = new Dir(druidDir, user, os);

		//------------------------------------------------------------------------
		//--- load config file

		try
		{
			XmlDocument xmlDoc = new XmlReader().read(dir.config + CONFIG_FILE);

			setup(xmlDoc.getRootElement());
		}
		catch(FileNotFoundException e)
		{
			//--- the config file doesn't exist
		}
		catch(Exception e)
		{
			System.out.println("Error loading config file --> " +e);
			e.printStackTrace();
		}

		//------------------------------------------------------------------------
		//--- add loaded drivers to the jdbc driver manager

		JdbcManager.init();

		for(int i=0; i<jdbc.driver.count(); i++)
			JdbcManager.addDriver(jdbc.driver.getAt(i));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Setup
	//---
	//---------------------------------------------------------------------------

	private static void setup(XmlElement elConfig)
	{
		jdbc   .setupConfig(elConfig.getChild(Jdbc.TAGNAME));
		erView .setupConfig(elConfig.getChild(ErView.TAGNAME));
		general.setupConfig(elConfig.getChild(General.TAGNAME));

		//------------------------------------------------------------------------
		//--- recent files

		XmlElement el = elConfig.getChild(RECENT_FILES);

		if (el != null)
		{
			List list = el.getChildren(FILE);

			for (int i=list.size()-1; i>=0; i--)
			{
				XmlElement elChild = (XmlElement) list.get(i);

				recentFiles.addFile(elChild.getValue());
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Save
	//---
	//---------------------------------------------------------------------------

	public static boolean save()
	{
		//------------------------------------------------------------------------
		//--- update config before it is saved

		jdbc.driver.clear();

		for(int i=0; i<JdbcManager.getDriverCount(); i++)
			jdbc.driver.add(JdbcManager.getDriverAt(i));

		//------------------------------------------------------------------------
		//--- build recent files element

		XmlElement elRecFiles = new XmlElement(RECENT_FILES);

		for(int i=0; i<recentFiles.getFileCount(); i++)
			elRecFiles.addChild(new XmlElement(FILE, recentFiles.getFileAt(i)));

		//------------------------------------------------------------------------
		//--- put all together

		XmlElement elRoot = new XmlElement(CONFIG);

		elRoot	.addChild(jdbc   .getConfig())
					.addChild(erView .getConfig())
					.addChild(general.getConfig())
					.addChild(elRecFiles);

		new File(dir.config).mkdirs();

		try
		{
			new XmlWriter().write(dir.config + CONFIG_FILE, new XmlDocument(elRoot));
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Error saving config file --> " + dir.config + CONFIG_FILE);
			e.printStackTrace();
			return false;
		}
	}
}

//==============================================================================
