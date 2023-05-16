//==============================================================================
//===
//===   VelocityModule
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.template.velocity;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

import org.dlib.tools.IOLib;
import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;
import org.dlib.xml.reader.XmlReader;

import druid.core.config.Config;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.interfaces.TemplateGenModule;
import druid.util.velocity.VelocityExecutor;
import druid.util.velocity.nodes.AbstractNodeV;

//==============================================================================

public class VelocityModule implements TemplateGenModule, LogSystem, ModuleOptions
{
	static final String PREFIX = "generator-";

	private OptionPanel optPanel = new OptionPanel();

	private Logger logger;

	//---------------------------------------------------------------------------
	//--- tags and attributes for generator files

	public static final String TAG_ROOT            = "generator";
	public static final String    TAG_PROPERTIES   = "properties";
	public static final String       TAG_DESCR     = "descr";
	public static final String       TAG_LONGDESCR = "longDescr";
	public static final String    TAG_COMMANDS     = "commands";
	public static final String       CMD_APPLY     = "apply";
	public static final String       CMD_COPY      = "copy";
	public static final String       CMD_LOOP      = "loop";
	public static final String       CMD_MAKEDIR   = "makeDir";

	public static final String ATT_TEMPLATE = "template";
	public static final String ATT_OUTPUT   = "output";
	public static final String ATT_FILE     = "file";
	public static final String ATT_TO       = "to";
	public static final String ATT_ON       = "on";
	public static final String ATT_NAME     = "name";

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	 public String getId()       { return "velocity"; }
	 public String getVersion()  { return "1.0";      }
	 public String getAuthor()   { return "Michael Hevery & Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Uses Velocity template engine to generate files.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE)	return this;
			else					return null;
	}

	//---------------------------------------------------------------------------

	public JComponent getPanel() { return optPanel; }

	//---------------------------------------------------------------------------

	public void refresh(AbstractNode node)
	{
		optPanel.refresh(this, node.modsConfig);
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Velocity"; }
	public boolean isDirectoryBased() { return true;       }
	public boolean hasLargePanel()    { return true;       }

	//---------------------------------------------------------------------------
	//---
	//--- Velocity Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		logger = l;

		l.logHeader("Velocity Templates");

		String outDir = dbNode.modsConfig.getValue(this, "output");

		//--- scan all generators

		Vector vGen = Generator.getGenerators();

		for(int i=0; i<vGen.size(); i++)
		{
			Generator gen = (Generator) vGen.elementAt(i);

			String value = dbNode.modsConfig.getValue(this, PREFIX + gen.sDir);

			if (!value.equals(""))
			{
				boolean use = value.substring(0,1).equals("Y");

				if (use)
				{
					String fullOutDir = value.substring(2);

					if (!outDir.equals(""))
						fullOutDir = outDir + Config.os.fileSep + fullOutDir;

					new File(fullOutDir).mkdirs();

					logger.log(Logger.INFO, "--- " + gen.sDescr + " ---------------");
					generate(gen, fullOutDir, dbNode);
					logger.log(Logger.INFO, "");
				}
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- LogSystem interface
	//---
	//---------------------------------------------------------------------------

	public void init(RuntimeServices rs) {}

	//---------------------------------------------------------------------------

	public void logVelocityMessage(int level, String message)
	{
		if (level == LogSystem.WARN_ID || level == LogSystem.ERROR_ID)
			logger.log(Logger.ALERT, message);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Executes a generator
	//---
	//---------------------------------------------------------------------------

	public void generate(Generator gen, String outDir, DatabaseNode dbNode)
	{
		try
		{
			XmlElement elRoot = new XmlReader().read(gen.sGenFile).getRootElement();
			XmlElement elCmds = elRoot.getChild(TAG_COMMANDS);

			if (elCmds == null)
			{
				logger.log(Logger.INFO, "No '" +TAG_COMMANDS+ "' tag for generator");
				return;
			}

			List listCmds = elCmds.getChildren();

			for(int i=0; i<listCmds.size(); i++)
			{
				XmlElement elCmd = (XmlElement) listCmds.get(i);

				String cmd = elCmd.getName();

				     if (cmd.equals(CMD_APPLY))   cmdApply(elCmd, gen, outDir, dbNode);
				else if (cmd.equals(CMD_COPY))    cmdCopy(elCmd, outDir);
				else if (cmd.equals(CMD_LOOP))    cmdLoop(elCmd, gen, outDir, dbNode);
				// Changed by: jmoreira@e-terno.net
				else if (cmd.equals(CMD_MAKEDIR)) cmdMakeDir(elCmd, outDir, dbNode);
				else
				{
					logger.log(Logger.ALERT, "Unknown command --> " + cmd);
				}
			}
		}
		catch(Exception e)
		{
			logger.log(Logger.ALERT, "Raised exc --> " + e);
		}
	}

	//---------------------------------------------------------------------------
	//--- Commands
	//---------------------------------------------------------------------------

	private void cmdApply(XmlElement el, Generator gen, String outDir, DatabaseNode dbNode)
	{
		String template = getParam(el, CMD_APPLY, ATT_TEMPLATE);
		String outFile  = getParam(el, CMD_APPLY, ATT_OUTPUT);

		if (template == null || outFile == null) return;

		//--- proper operation

		if (!outDir.equals("") && !outFile.startsWith("/"))
			outFile = outDir + Config.os.fileSep + outFile;

		applyTemplate(gen.sFullDir, template, outFile, dbNode);
	}

	//---------------------------------------------------------------------------

	private void cmdCopy(XmlElement el, String outDir)
	{
		String fromFile = getParam(el, CMD_COPY, ATT_FILE);
		String toFile   = getParam(el, CMD_COPY, ATT_TO);

		if (fromFile == null || toFile == null) return;

		//--- proper operation

		if (!outDir.equals(""))
		{
			if (!fromFile.startsWith("/"))
				fromFile = outDir + Config.os.fileSep + fromFile;

			if (!toFile.startsWith("/"))
				toFile = outDir + Config.os.fileSep + toFile;
		}

		if (!IOLib.copy(fromFile, toFile))
			logger.log(Logger.ALERT, "Cannot copy file '" +fromFile+ "' to '" +toFile+ "'");
	}

	//---------------------------------------------------------------------------

	private void cmdLoop(XmlElement el, Generator gen, String outDir, DatabaseNode dbNode)
	{
		String template = getParam(el, CMD_LOOP, ATT_TEMPLATE);
		String outFile  = getParam(el, CMD_LOOP, ATT_OUTPUT);
		String onObj    = getParam(el, CMD_LOOP, ATT_ON);

		if (template == null || outFile == null || onObj == null) return;

		//--- proper operation

		if (!outDir.equals("") && !outFile.startsWith("/"))
			outFile = outDir + Config.os.fileSep + outFile;

		for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (AbstractNodeV.getNodeType(node).equals(onObj))
			{
				String name = node.attrSet.getString("name");

				name = Util.replaceStr(outFile, "$", name);

				applyTemplate(gen.sFullDir, template, name, node);
			}
		}
	}

	//---------------------------------------------------------------------------
	//--- Changed by: jmoreira@e-terno.net
	//--- added 'on=""' to 'makedir' generator.xml node,
	//--- allowing to use ex.: output="$/$Service.class.php"

	private void cmdMakeDir(XmlElement el, String outDir, DatabaseNode dbNode)
	{
		//--- Parameter 'name' passed in generator.xml

		String name  = getParam(el, CMD_MAKEDIR, ATT_NAME);
		String onObj = el.getAttributeValue(ATT_ON);

		if (name == null) return;

		//--- match onObj with a db node name

		if (onObj != null)
		{
			for(Enumeration e=dbNode.preorderEnumeration(); e.hasMoreElements();)
			{
				AbstractNode node = (AbstractNode) e.nextElement();

				if (AbstractNodeV.getNodeType(node).equals(onObj))
				{
					String nodeName = node.attrSet.getString("name");

					// Replace $ in name with the node name (a table name for example)

					nodeName = Util.replaceStr(name, "$", nodeName);

					// Convert to full path

					if (!outDir.equals("") && !nodeName.startsWith("/"))
						nodeName = outDir + Config.os.fileSep + nodeName;

					if (!new File(nodeName).mkdirs())
						logger.log(Logger.ALERT, "Cannot create directory '"+nodeName+"'");
				}
			}
		}
		else
		{
			//--- onObj not used

			if (!outDir.equals("") && !name.startsWith("/"))
				name = outDir + Config.os.fileSep + name;

			if (!new File(name).mkdirs())
				logger.log(Logger.ALERT, "Cannot create directory '"+name+"'");
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Utility methods
	//---
	//---------------------------------------------------------------------------

	private void applyTemplate(String templateDir, String template, String outfile, AbstractNode node)
	{
		VelocityExecutor ve = new VelocityExecutor(logger);

		ve.register("db", AbstractNodeV.convertNode(node));

		ve.applyTemplate(templateDir, template, outfile);
	}

	//---------------------------------------------------------------------------

	private String getParam(XmlElement el, String cmd, String attrib)
	{
		String value  = el.getAttributeValue(attrib);

		if (value == null)
		{
			logger.log(Logger.ALERT, "Error on command '" +cmd+ "'");
			logger.log(Logger.ALERT, "   Missing '" +attrib+ "' attribute");
		}

		return value;
	}
}

//==============================================================================
