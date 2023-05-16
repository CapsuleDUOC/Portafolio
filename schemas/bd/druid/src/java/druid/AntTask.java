//==============================================================================
//===
//===   AntTask
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import druid.boot.Starter;

//==============================================================================

public class AntTask extends Task
{
	private String command;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public AntTask() {}

	//---------------------------------------------------------------------------
	//---
	//--- Setters
	//---
	//---------------------------------------------------------------------------

	public void setCommand(String cmd)
	{
		command = cmd;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Ant methods
	//---
	//---------------------------------------------------------------------------

	public void init()
	{
		command = null;
	}

	//---------------------------------------------------------------------------

	public void execute() throws BuildException
	{
		if (command == null)
			throw new BuildException("The 'command' attrib must be specified");

		String progDir = getProgDir();

		URL urls[] = Druid.getJarUrls(progDir);

		if (urls == null)
			throw new BuildException("Problems scanning 'libs' directory");

		URLClassLoader mcl = new URLClassLoader(urls, getClass().getClassLoader());

		try
		{
			Starter starter = (Starter) Class.forName("druid.starter.DruidStarter", true, mcl).newInstance();

			starter.start(buildArgs(command), progDir);
		}
		catch(Throwable e)
		{
			e.printStackTrace();

			throw new BuildException(e);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String getProgDir()
	{
		String dir = getClass().getClassLoader().getResource("druid/Druid.class").toString();

		dir = dir.replace('\\', '/');

		if (dir.startsWith("jar:"))	dir = dir.substring(4);
		if (dir.startsWith("file:"))	dir = dir.substring(5);

		//--- skip the ending string "/druid/Druid.class"

		dir = dir.substring(0, dir.length() - "/druid/Druid.class".length());

		//--- we must skip the "druid.jar!" string (if the case)

		if (dir.endsWith("!"))
			dir = dir.substring(0, dir.length() - "/libs/ant-task.jar!".length());

		//--- handle the case druid is executed from an IDE

		if (dir.endsWith("/build/classes"))
			dir = dir.substring(0, dir.length() - "/build/classes".length());

		return dir;
	}

	//---------------------------------------------------------------------------

	private String[] buildArgs(String cmd)
	{
		StringTokenizer st = new StringTokenizer(cmd, " ");

		String args[] = new String[st.countTokens()];

		int i=0;

		while(st.hasMoreTokens())
			args[i++] = st.nextToken();

		return args;
	}
}

//==============================================================================
