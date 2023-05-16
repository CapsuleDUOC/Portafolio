//==============================================================================
//===
//===   JdbcDriver
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.jdbc.manager;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

//==============================================================================

public class JdbcDriver
{
	private String  name;
	private String  version;
	private String  file;
	private boolean compliant;
	private Driver  driver;

	//---------------------------------------------------------------------------
	//---
	//---   Constructors
	//---
	//---------------------------------------------------------------------------

	public JdbcDriver(Driver drv, String drvName, String drvFile)
	{
		name      = drvName;
		file      = drvFile;
		version   = drv.getMajorVersion() + "." + drv.getMinorVersion();
		compliant = drv.jdbcCompliant();
		driver    = drv;
	}

	//---------------------------------------------------------------------------

	public JdbcDriver(String drvName, String drvFile)
	{
		name      = drvName;
		file      = drvFile;
		version   = "?";
		compliant = false;
		driver    = null;

		try
		{
			URL url = new URL("file:" + drvFile);

			URLClassLoader ucl = new URLClassLoader(new URL[]{url}, JdbcDriver.class.getClassLoader());

			Object o = ucl.loadClass(name).newInstance();

			if (o instanceof Driver)
			{
				driver    = (Driver) o;
				version   = driver.getMajorVersion() + "." + driver.getMinorVersion();
				compliant = driver.jdbcCompliant();
			}
		}
		catch(Throwable t) {}
	}

	//---------------------------------------------------------------------------

	public Driver getDriver() { return driver; }

	//---------------------------------------------------------------------------
	//---
	//---   API
	//---
	//---------------------------------------------------------------------------

	public String  getName()     { return name;           }
	public String  getVersion()  { return version;        }
	public String  getFile()     { return file;           }
	public boolean isCompliant() { return compliant;      }
	public boolean isLoaded()    { return driver != null; }
	public String  toString()    { return name;           }
}

//==============================================================================
