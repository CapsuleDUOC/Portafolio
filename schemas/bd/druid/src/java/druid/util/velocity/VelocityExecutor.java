//==============================================================================
//===
//===   VelocityExecutor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

import druid.interfaces.Logger;
import druid.util.velocity.helpers.Html;
import druid.util.velocity.helpers.JavaUtil;
import druid.util.velocity.helpers.Sys;
import druid.util.velocity.helpers.Util;

//==============================================================================

public class VelocityExecutor
{
	protected Logger logger;

	private VelocityContext context = new VelocityContext();
	private VelocityEngine  engine  = new VelocityEngine();

	protected boolean execOk;

	//---------------------------------------------------------------------------
	//--- LogSystem
	//---------------------------------------------------------------------------

	private LogSystem logSystem = new LogSystem()
	{
		public void init(RuntimeServices rs) throws Exception {}

		//------------------------------------------------------------------------

		public void logVelocityMessage(int level, String message)
		{
			if (level == LogSystem.WARN_ID || level == LogSystem.ERROR_ID)
			{
				logger.log(Logger.ALERT, message);

				execOk = false;
			}
		}
	};

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public VelocityExecutor(Logger l)
	{
		logger = l;

		engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM,  logSystem);
		engine.setProperty(VelocityEngine.VM_CONTEXT_LOCALSCOPE,  "true");
		engine.setProperty(VelocityEngine.VM_LIBRARY,             "macros.vm");

		initContext();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void register(String name, Object obj)
	{
		context.put(name, obj);
	}

	//---------------------------------------------------------------------------

	/** @param templateDir full path where the template is located
	  * @param template file name of the template to apply
	  * @param outfile name of the file that will be created
	  */

	public boolean applyTemplate(String templateDir, String template, String outFile)
	{
		engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateDir);

		execOk = true;

        FileOutputStream fos = null;
        Writer w = null;
        BufferedWriter bw = null;
		try {
			engine.init();

            fos = new FileOutputStream(outFile);
            w = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(w);
			engine.mergeTemplate(template, context, bw);
		} catch (Exception e) {
			StringWriter s = new StringWriter();
			PrintWriter  p = new PrintWriter(s);

			e.printStackTrace(p);

			logger.log(Logger.ALERT, "Velocity exception --> " + e.getMessage());
			logger.log(Logger.ALERT, "Stack:\n" + s);

			return false;
		} finally {
            try {
                if (bw != null) bw.close();
                if (w != null) w.close();
                if (fos != null) fos.close();
            } catch (IOException e) { /* empty */ }
        }
        return execOk;
	}

	//---------------------------------------------------------------------------

	private void initContext()
	{
		context.put("Sys",   new Sys());
		context.put("Html",  new Html());
		context.put("Util",  new Util(context));
		context.put("JUtil", new JavaUtil());
	}
}

//==============================================================================
