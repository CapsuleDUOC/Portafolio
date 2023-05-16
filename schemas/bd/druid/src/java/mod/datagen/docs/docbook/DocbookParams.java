//==============================================================================
//===
//===   DocbookParams
//===
//===   Copyright (C) by Andrea Carboni & Bruno Vernay.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.docbook;

import java.util.Enumeration;
import java.util.Hashtable;

import druid.core.config.Config;
import druid.data.DatabaseNode;
import druid.interfaces.Logger;
import druid.util.velocity.VelocityExecutor;
import druid.util.velocity.nodes.AbstractNodeV;

//==============================================================================

class DocbookParams
{
	public DatabaseNode dbNode;

	public Logger logger;
	public String outputDir;
	public String templateDir;

	private Hashtable htLangStrings;
        
	//---------------------------------------------------------------------------

	public DocbookParams(Logger l, DatabaseNode node, String outDir, String skin, Hashtable strings)
	{
		logger      = l;
		dbNode      = node;
		outputDir   = outDir;
		templateDir = Config.dir.data + "/templates/docgen/docbook/"+ skin +"/";
		htLangStrings = strings;

		//this.thumbSize = thumbSize;
	}

	//---------------------------------------------------------------------------

	public boolean applyTemplate(String sourceTemp, String destination)
	{
		return applyTemplate(sourceTemp, destination, null);
	}

	//---------------------------------------------------------------------------
/*
	public boolean applyTemplate(String sourceTemp, String destination, String name, AbstractNode node)
	{
		Hashtable ht = new Hashtable();

		ht.put(name, AbstractNodeV.convertNode(node));

		return applyTemplate(sourceTemp, destination, ht);
	}
*/
	//---------------------------------------------------------------------------

	public boolean applyTemplate(String sourceTemp, String destination, Hashtable bindings)
	{
		VelocityExecutor ve = new VelocityExecutor(logger);

		ve.register("db", AbstractNodeV.convertNode(dbNode));
		ve.register("lang", htLangStrings);

		if (bindings != null)
			for(Enumeration e=bindings.keys(); e.hasMoreElements();)
			{
				String name  = (String) e.nextElement();
				Object value = bindings.get(name);

				ve.register(name, value);
			}

		boolean result = ve.applyTemplate(templateDir, sourceTemp, outputDir + destination);

		if (!result)
			logger.log(Logger.ALERT, "Cannot create '"+ destination +"' file");

		return result;
	}
}

//==============================================================================
