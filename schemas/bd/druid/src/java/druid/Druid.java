//==============================================================================
//===   Druid
//==============================================================================
//=== Copyright (C) 2000  Andrea Carboni
//===
//=== This library is free software; you can redistribute it and/or
//=== modify it under the terms of the GNU General Public
//=== License as published by the Free Software Foundation; either
//=== version 2 of the License, or (at your option) any later version.
//===
//=== This library is distributed in the hope that it will be useful,
//=== but WITHOUT ANY WARRANTY; without even the implied warranty of
//=== MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//=== General Public License for more details.
//===
//=== You should have received a copy of the GNU General Public
//=== License along with this library; if not, write to the Free Software
//=== Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
//===
//=== the full license is in docs/COPYING
//===
//=== author : Andrea Carboni
//=== email  : acarboni@users.sourceforge.net
//==============================================================================

package druid;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Vector;

import druid.boot.Starter;

//==============================================================================

public class Druid
{
	//---------------------------------------------------------------------------
	//--- constants and static vars

	public static final String VERSION  = "3.13";
	public static final String FILE_EXT = "druid";
	public static final String TITLE    = "Druid v" + VERSION + ": Editing project";

	//---------------------------------------------------------------------------
	//---
	//--- Main method
	//---
	//---------------------------------------------------------------------------

	public static void main(String[] args)
	{
		String progDir = getProgDir();

		URL urls[] = getJarUrls(progDir);

		if (urls == null)
			return;

		URLClassLoader mcl = new URLClassLoader(urls);

		try
		{
			Starter starter = (Starter) Class.forName("druid.starter.DruidStarter", true, mcl).newInstance();

			starter.start(args, progDir);
		}
		catch(Throwable e)
		{
			System.out.println("Raised exception while running druid \n");
			e.printStackTrace();

			//--- this line is needed to exit from druid in case of Errors
			//--- (not Exceptions)

			System.exit(0);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Support methods (public because are used by the AntTask class)
	//---
	//---------------------------------------------------------------------------

	private static String getProgDir()
	{
		String dir = ClassLoader.getSystemResource("druid/Druid.class").toString();

		try
		{
			dir = URLDecoder.decode(dir, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			//--- this should not happen but ...

			e.printStackTrace();
		}

		dir = dir.replace('\\', '/');

		if (dir.startsWith("jar:"))	dir = dir.substring(4);
		if (dir.startsWith("file:"))	dir = dir.substring(5);

		//--- skip the ending string "/druid/Druid.class"

		dir = dir.substring(0, dir.length() - "/druid/Druid.class".length());

		//--- we must skip the "druid.jar!" string (if the case)

		if (dir.endsWith("!"))
			dir = dir.substring(0, dir.length() - "/druid.jar!".length());

		//--- handle the case druid is executed from an IDE

		if (dir.endsWith("/build/classes"))
			dir = dir.substring(0, dir.length() - "/build/classes".length());

		//--- hack for windows : dirs like '/C:/...' must be changed to remove the
		//--- starting slash

		if (dir.startsWith("/") && dir.indexOf(":") != -1)
			dir = dir.substring(1);

		return dir;
	}

	//---------------------------------------------------------------------------

	public static URL[] getJarUrls(String progDir)
	{
		try
		{
			String jars[] = new File(progDir + "/libs").list();

			Vector v = new Vector();

			for(int i=0; i<jars.length; i++)
				if (jars[i].endsWith(".jar"))
					 v.addElement(jars[i]);

			URL urls[] = new URL[v.size()];

			for(int i=0; i<v.size(); i++)
				urls[i] = new URL("file:" + progDir + "/libs/" + v.get(i));

			return urls;
		}
		catch(MalformedURLException e)
		{
			System.out.println("Raised exc --> " + e);
			return null;
		}
		catch(NullPointerException e)
		{
			System.out.println("Found dir : " +progDir);
			System.out.println("Problems scanning the 'libs' directory.");
			System.out.println("Usually this is due to read/scan privileges.");
			System.out.println("Change privileges and try again.");

			return null;
		}
	}
}

//==============================================================================

