//==============================================================================
//===
//===   Command Line Interface
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.cli;

import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import druid.core.io.ProjectManager;
import druid.core.modules.ModuleManager;
import druid.data.DatabaseNode;
import druid.data.ProjectNode;
import druid.interfaces.CodeGenModule;
import druid.interfaces.DataGenModule;
import druid.interfaces.DatadictGenModule;
import druid.interfaces.DocsGenModule;
import druid.interfaces.GenericGenModule;
import druid.interfaces.Logger;
import druid.interfaces.SqlGenModule;
import druid.interfaces.SummaryGenModule;
import druid.interfaces.TemplateGenModule;

//==============================================================================

public class Cli
{
	private static Hashtable htCommands = new Hashtable();

	//--- parameters
	private static String[] paramKW = { "-proj", "-gen", "-db", "-help" };

	//--- keywords of the "-gen" parameter
	private static String[] generKW = { "sql", "docs", "summ", "datad",
													"code", "generic", "template" };

	private static String sProjFile = null;
	private static int    iDbNum    = -1;

	//---------------------------------------------------------------------------

	public static void start(String[] args)
	{
		parse(args);
		checkParams();

		checkHelp();
		checkProj();
		checkGen();
		checkDb();

		//------------------------------------------------------------------------
		//--- load project

		ProjectNode projNode = new ProjectNode();

		try
		{
			ProjectManager.loadProject(projNode, sProjFile);
		}
		catch(FileNotFoundException e)
		{
			print("File not found : " + sProjFile);
			exit();
		}
		catch(Exception e)
		{
			print("Error reading the druid file : " + sProjFile);
			print("   --> " + e);
			exit();
		}

		//------------------------------------------------------------------------
		//--- do jobs

		if (iDbNum == -1)
		{
			for(int i=0; i<projNode.getChildCount(); i++)
			{
				DatabaseNode dbNode = (DatabaseNode) projNode.getChild(i);

				//put here integrity check for database

				generate(dbNode);
			}
		}
		else
		{
			int maxNum = projNode.getChildCount();

			if (iDbNum >= maxNum)
			{
				print("The db number is too high (max is " + (maxNum-1) + ")");
				exit();
			}
			else
			{
				DatabaseNode dbNode = (DatabaseNode) projNode.getChild(iDbNum);

				//put here integrity check for database

				generate(dbNode);
			}
		}

		//------------------------------------------------------------------------
		//--- exit

		System.exit(0);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Params checking and parsing
	//---
	//---------------------------------------------------------------------------

	private static void parse(String[] args)
	{
		for(int i=0; i<args.length; i++)
		{
			String param  = args[i];
			String vals   = "";
			Vector values = new Vector();

			int pos = param.indexOf(":");

			if (pos != -1)
			{
				vals  = param.substring(pos + 1);
				param = param.substring(0, pos);

				if (vals.equals(""))
				{
					print("You must supply at least one option after ':'");
					exit();
				}

				StringTokenizer st = new StringTokenizer(vals, ",");

				while (st.hasMoreTokens())
					values.addElement(st.nextToken());
			}

			if (htCommands.containsKey(param))
			{
				print("Parameter already given: " + param);
				exit();
			}
			else
			{
				htCommands.put(param, values);
			}
		}
	}

	//---------------------------------------------------------------------------

	private static void checkParams()
	{
		for(Enumeration e = htCommands.keys(); e.hasMoreElements();)
		{
			String param = (String) e.nextElement();

			boolean found = false;

			for(int i=0; i<paramKW.length; i++)
				if (param.equals(paramKW[i]))
					found = true;

			if (!found)
			{
				print("Unknown parameter: " + param);
				print("Use '-help' for help");
				exit();
			}
		}
	}

	//---------------------------------------------------------------------------

	private static void checkHelp()
	{
		if (!htCommands.containsKey("-help")) return;

		print("Usage: druid <param1> <param2> ...");
		print("Accepted parameters are:");
		print("");
		print("-proj:<druid-file>");
		print("     Indicates the druid file to load (mandatory)");
		print("");
		print("-gen:<opt1>,<opt2> ...");
		print("     Generates data. Options can be:sql,docs,summ,datad,code,generic,template");
		print("");
		print("-db:<db-number>");
		print("     Indicates the db inside the druid file");
		print("");
		print("-help");
		print("     Shows this help");
		print("");

		exit();
	}

	//---------------------------------------------------------------------------

	private static void checkProj()
	{
		Vector vProj = (Vector) htCommands.get("-proj");

		//------------------------------------------------------------------------

		if (vProj == null)
		{
			print("Missing the '-proj' parameter");
			exit();
		}

		//------------------------------------------------------------------------

        else if (vProj.size() == 0)
		{
			print("Missing druid file in '-proj' parameter");
			exit();
		}

		//------------------------------------------------------------------------

        else if (vProj.size() != 1)
		{
			print("Options not allowed for '-proj' parameter");
			exit();
		}

		//------------------------------------------------------------------------
        else
        {
            sProjFile = (String) vProj.elementAt(0);
        }
	}

	//---------------------------------------------------------------------------

	private static void checkGen()
	{
		Vector vGen = (Vector) htCommands.get("-gen");

		if (vGen == null) return;

		//------------------------------------------------------------------------

		if (vGen.size() == 0)
		{
			print("The '-gen' parameter needs options");
			exit();
		}

		//------------------------------------------------------------------------

		for(int i=0; i<vGen.size(); i++)
		{
			String option = (String) vGen.elementAt(i);

			boolean found = false;

			for(int j=0; j<generKW.length; j++)
				if (option.equals(generKW[j])) found = true;

			if (!found)
			{
				print("Unknown option for '-gen' parameter: " + option);
				print("Use '-help' for help");
				exit();
			}
		}
	}

	//---------------------------------------------------------------------------

	private static void checkDb()
	{
		Vector vProj = (Vector) htCommands.get("-db");

		//------------------------------------------------------------------------

		if (vProj == null) return;

		//------------------------------------------------------------------------

		if (vProj.size() == 0)
		{
			print("Missing db number in '-db' parameter");
			exit();
		}

		//------------------------------------------------------------------------

		if (vProj.size() != 1)
		{
			print("Options not allowed for '-db' parameter");
			exit();
		}

		//------------------------------------------------------------------------

		try
		{
			iDbNum = Integer.parseInt((String) vProj.elementAt(0));

			if (iDbNum < 0)
			{
				print("The db number must be positive (starting from 0)");
				exit();
			}
		}
		catch(NumberFormatException e)
		{
			print("The db number must be an integer (starting from 0)");
			exit();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Generation methods
	//---
	//---------------------------------------------------------------------------

	private static void generate(DatabaseNode dbNode)
	{
		Logger l = new Logger()
		{
			public void log(int type, String msg)
			{
				if (type == Logger.ALERT)
					System.out.println(msg);
			}
			public void logHeader(String msg) {}
		};

		if (!htCommands.containsKey("-gen")) return;

		if (existGenerOption("sql"))      generate(SqlGenModule.class,      l, dbNode);
		if (existGenerOption("docs"))     generate(DocsGenModule.class,     l, dbNode);
		if (existGenerOption("summ"))     generate(SummaryGenModule.class,  l, dbNode);
		if (existGenerOption("datad"))    generate(DatadictGenModule.class, l, dbNode);
		if (existGenerOption("code"))     generate(CodeGenModule.class,     l, dbNode);
		if (existGenerOption("generic"))  generate(GenericGenModule.class,  l, dbNode);
		if (existGenerOption("template")) generate(TemplateGenModule.class, l, dbNode);
	}

	//---------------------------------------------------------------------------

	private static boolean existGenerOption(String option)
	{
		Vector v = (Vector) htCommands.get("-gen");

		for(int i=0; i<v.size(); i++)
		{
			String o = (String) v.elementAt(i);

			if (option.equals(o)) return true;
		}
		return false;
	}

	//---------------------------------------------------------------------------

	private static void generate(Class modClass, Logger logger, DatabaseNode dbNode)
	{
		for(Enumeration e=ModuleManager.getModules(modClass); e.hasMoreElements();)
		{
			DataGenModule dgMod = (DataGenModule) e.nextElement();

			if (dbNode.modsUsage.contains(dgMod))
				dgMod.generate(logger, dbNode);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Useful methods
	//---
	//---------------------------------------------------------------------------

	private static void print(String line)
	{
		System.out.println(line);
	}

	//---------------------------------------------------------------------------

	private static void exit()
	{
		System.exit(-1);
	}
}

//==============================================================================
