//==============================================================================
//===
//===   JdbcManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.manager;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//==============================================================================

public class JdbcManager
{
	private static Vector vDrivers;

	//---------------------------------------------------------------------------
	//---
	//--- Init
	//---
	//---------------------------------------------------------------------------

	public static void init()
	{
		vDrivers = new Vector();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static void addDriver(String driver)
	{
		int pos = driver.indexOf(":");

		String name = driver.substring(0, pos);
		String file = driver.substring(pos+1);

		vDrivers.addElement(new JdbcDriver(name, file));
	}

	//---------------------------------------------------------------------------

	public static String getDriverAt(int index)
	{
		JdbcDriver drv = (JdbcDriver) vDrivers.elementAt(index);

		return drv.getName() + ":" + drv.getFile();
	}

	//---------------------------------------------------------------------------

	public static int getDriverCount()
	{
		return vDrivers.size();
	}

	//---------------------------------------------------------------------------

	public static JdbcDriver getJdbcDriverAt(int index)
	{
		return (JdbcDriver)vDrivers.elementAt(index);
	}

	//---------------------------------------------------------------------------

	public static List importDriver(String fileName)
	{
		try
		{
			//--- support for multiple driver classes in one driver JAR file
			List    result = new ArrayList();
			JarFile jf     = new JarFile(fileName);

			URL url = new URL("file:" + fileName);

			URLClassLoader ucl = new URLClassLoader(new URL[]{url}, JdbcManager.class.getClassLoader());

			//--- scan all jar entries

			for(Enumeration e=jf.entries(); e.hasMoreElements();)
			{
				JarEntry je = (JarEntry) e.nextElement();
				String name = je.getName();

				if (!je.isDirectory() && name.endsWith(".class"))
				{
					name = name.substring(0, name.length() -6);
					name = name.replace('/', '.');

					try
					{
						Object o = ucl.loadClass(name).newInstance();

						if (o instanceof Driver)
						{
							//--- check if the driver has already been imported

							for(int i=0; i<vDrivers.size(); i++)
							{
								JdbcDriver drv = (JdbcDriver) vDrivers.elementAt(i);

								if (drv.getName().equals(name)) return null;
							}

							//--- ok, driver can be added

							Driver drv = (Driver) o;

							JdbcDriver d = new JdbcDriver(drv, name, fileName);

							vDrivers.addElement(d);
							result.add(d);
						}
					}
					catch(Throwable t) {}
				}
			}

			return result;
		}
		catch(IOException e) {}

		return null;
	}

	//---------------------------------------------------------------------------

	public static boolean removeDriver(String name)
	{
		for(int i=0; i<vDrivers.size(); i++)
		{
			JdbcDriver drv = (JdbcDriver) vDrivers.elementAt(i);

			if (drv.getName().equals(name))
			{
				vDrivers.removeElementAt(i);
				return true;
			}
		}

		return false;
	}

	//---------------------------------------------------------------------------

	public static Connection connect(String url, String user, String password)
												throws SQLException
	{
		//--- scann all drivers to search one that can connect to the given url

		for(int i=0; i<vDrivers.size(); i++)
		{
			JdbcDriver jdbcDrv = (JdbcDriver) vDrivers.elementAt(i);

			if (jdbcDrv.isLoaded())
			{
				Driver drv = jdbcDrv.getDriver();

				Properties p = new Properties();

				p.setProperty("user",     user);
				p.setProperty("password", password);

//				//MSD: Rob -hack this to get oracle driver to return remarks
//				//Oracle requires parameters to be set in a properties object
//
//				if(drv.getClass().getName().indexOf("oracle.")>-1)
//					p.setProperty("remarksReporting","true");

				//--- interface enhancement: allow DB specific connection initialization
				//--- previous oracle hack removed

				Connection sqlConn = drv.connect(url, p);

				if (sqlConn != null)
					return sqlConn;
			}
		}

		//--- null means "no suitable driver"

		return null;
	}
}

//==============================================================================
