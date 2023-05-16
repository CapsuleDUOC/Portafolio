//==============================================================================
//===
//===   ProjectManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlException;
import org.dlib.xml.XmlWriter;
import org.dlib.xml.reader.XmlReader;
import org.dlib.xml.reader.XmlSimpleReader;

import druid.data.ProjectNode;

//==============================================================================

public class ProjectManager
{
	static final int VERSION = 2;

	//---------------------------------------------------------------------------
	//---
	//--- LoadProject
	//---
	//---------------------------------------------------------------------------

	public static void loadProject(ProjectNode projNode, String filename)
								throws FileNotFoundException, IOException, XmlException
	{
		XmlElement elRoot;

		if (isOldFormat(filename))
		{
			System.out.println("Loading old format");
			elRoot = new XmlSimpleReader().read(filename).getRootElement();
		}
		else
			elRoot = new XmlReader().read(filename).getRootElement();

		Loader.load(elRoot, projNode);
	}

	//---------------------------------------------------------------------------
	//---
	//--- SaveProject
	//---
	//---------------------------------------------------------------------------

	public static void saveProject(ProjectNode projNode, String fileName) throws IOException
	{
		XmlElement elRoot = Saver.save(projNode);

		new XmlWriter().write(fileName, new XmlDocument(elRoot));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static boolean isOldFormat(String filename)
	{
		String line;

        FileReader fr = null;
        BufferedReader r = null;
		try
		{
			//--- open file ---

            fr = new FileReader(filename);
			r = new BufferedReader(fr);

			//--- the first readLine skips the xml header

			line = r.readLine();
			line = r.readLine();
			line = r.readLine();
        } catch (IOException e) {
            //--- true or false is the same
            return false;
        } finally {
            try {
                if (r != null ) r.close();
                if (fr != null) fr.close();
            } catch (IOException e) {/* Do nothing */}
        }

		if (line == null)
			return false;

		int start = line.indexOf("\"");
		int end   = line.lastIndexOf("\"");

		int version = Integer.parseInt(line.substring(start+1, end));

		return version < VERSION;
	}
}

//==============================================================================
