//==============================================================================
//===
//===   Generator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.template.velocity;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlException;
import org.dlib.xml.reader.XmlReader;

import druid.core.config.Config;

//==============================================================================

public class Generator
{
	private static Vector vGenerators;

	public String sDir;
	public String sFullDir;
	public String sGenFile;
	public String sDescr;
	public String sLongDescr;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	private Generator() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static Vector getGenerators()
	{
		if (vGenerators == null)
			vGenerators = init();

		return vGenerators;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static Vector init()
	{
		String LS = Config.os.fileSep;

		String path   = Config.dir.data + LS +"templates"+ LS +"velocity";
		String dirs[] = new File(path).list();

		if (dirs == null) return null;

		Vector v = new Vector();

		for(int i=0; i<dirs.length; i++)
		{
			String fullDir = path + LS + dirs[i];
			String file    = fullDir + LS + "generator.xml";

			try
			{
				XmlElement elRoot  = new XmlReader().read(file).getRootElement();
				XmlElement elProps = elRoot.getChild(VelocityModule.TAG_PROPERTIES);

				if (elProps == null) continue;

				String descr     = elProps.getChildValue(VelocityModule.TAG_DESCR);
				String longDescr = elProps.getChildValue(VelocityModule.TAG_LONGDESCR);

				if (descr == null) continue;

				Generator gen = new Generator();

				gen.sDir       = dirs[i];
				gen.sFullDir   = fullDir;
				gen.sGenFile   = file;
				gen.sDescr     = descr;
				gen.sLongDescr = longDescr;

				v.addElement(gen);
			}
			catch(IOException e)
			{
			}
			catch(XmlException e)
			{
				System.out.println("Raised xml exc --> " +e);
			}
		}

		return v;
	}
}

//==============================================================================
