//==============================================================================
//===
//===   AbstractLang
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package factory.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.dlib.tools.Util;

import druid.core.config.Config;
import druid.data.DatabaseNode;
import druid.data.TableNode;
import druid.interfaces.CodeGenModule;
import druid.interfaces.Logger;
import druid.util.DruidUtil;

//==============================================================================

public abstract class AbstractLang implements CodeGenModule
{
	protected DatabaseNode dbNode;

	protected String sBuild;

	//--- must be set by the module
	protected String sOutput;

	protected String LF = Config.os.lineSep;

	//---------------------------------------------------------------------------

	protected void setup(DatabaseNode node)
	{
		dbNode = node;

		if (!dbNode.attrSet.getBool("codeUseBuild"))
			sBuild = "";
		else
			sBuild = "Build:" + dbNode.getProjectNode().attrSet.getInt("build");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Class preview
	//---
	//---------------------------------------------------------------------------

	public final String getClassCode(Logger logger, TableNode node)
	{
		setup(node.getDatabase());

		return getClassCodeInt(logger, node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Data generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger logger, DatabaseNode dbNode) 
	{
		logger.logHeader(getMessage());

		//------------------------------------------------------------------------

		setup(dbNode);

		//------------------------------------------------------------------------
		//--- checks
		String res = checkOptions();

		if (res != null) 
		{
			logger.log(Logger.ALERT, res);
			return;
		}

		if (sOutput.length() == 0) 
		{
			logger.log(Logger.ALERT, "The 'output' field cannot be empty.");
			logger.log(Logger.ALERT, "Generation aborted.");
			return;
		}

		//------------------------------------------------------------------------

		Vector tables = dbNode.getObjects(TableNode.class);

		sOutput = DruidUtil.toAbsolutePath(sOutput);
		
		if (isClassOriented()) 
		{
			File f = new File(sOutput);
			f.mkdirs();

			if (!sOutput.endsWith("/")) sOutput += "/";

			for (int i = 0; i < tables.size(); i++) 
			{
                Writer w = null;
                BufferedWriter bw = null;
				try 
				{
					TableNode node = (TableNode)tables.elementAt(i);

					String code = getClassCodeInt(logger, node);

					if (code.length() > 0) 
					{
						w = new FileWriter(sOutput + getClassCodeName(node)+ "." + getExtension());
                        bw = new BufferedWriter(w);
						bw.write(code + LF);
					}
				} 
				catch(IOException e) 
				{
					logger.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
				} 
				finally 
				{
                    try 
                    {
                        if (bw != null) bw.close();
                        if (w != null)  w.close();
                    } 
                    catch (IOException e) 
                    { 
                    	/* do nothing*/ 
                    }
                }
            }
		} 
		else 
		{
            Writer w = null;
            BufferedWriter bw = null;
			try 
			{
				w  = new FileWriter(sOutput);
                bw = new BufferedWriter(w);
                
				//--- write header ---
				bw.write(getHeader());

				for (int i = 0; i < tables.size(); i++) 
				{
					TableNode node = (TableNode)tables.elementAt(i);
					String code = getClassCodeInt(logger, node);
					
					if (code.length() > 0) 
						bw.write(code);
				}

				//--- write footer ---
				bw.write(getFooter());
			} 
			catch(IOException e) 
			{
				logger.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
            } 
			finally 
			{
                try 
                {
                    if (bw != null) bw.close();
                    if (w != null) w.close();
                } 
                catch (IOException e) 
                { 
                	/* do nothing*/ 
                }
            }
		}

		//------------------------------------------------------------------------

		logger.log(Logger.INFO, "");
		logger.log(Logger.INFO, "Done.");
	}

	//---------------------------------------------------------------------------

	protected String getSeparator() { return Util.replicate("=", 78); }

	//---------------------------------------------------------------------------

	/** Null means no checks
	  */

	protected String checkOptions()	{ return null; }

	//---------------------------------------------------------------------------

	abstract protected String  getMessage();
	abstract protected String  getClassCodeInt(Logger l, TableNode node);
	abstract protected String  getExtension();
	abstract protected boolean isClassOriented();
	abstract protected String  getHeader();
	abstract protected String  getFooter();
	abstract protected String  getClassCodeName(TableNode node);
}

//==============================================================================
