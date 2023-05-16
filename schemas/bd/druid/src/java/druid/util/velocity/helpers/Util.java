//==============================================================================
//===
//===   Util
//===
//===   Copyright (C) by Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.helpers;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.velocity.VelocityContext;

//==============================================================================

/** Utility class which is made availbale under the name of
  * "util" in your velocity context. It contains few usefull
  * functions in writing velocity templates.
  *
  * @author mhevery
  */

public class Util
{
	private VelocityContext context;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Util(VelocityContext context)
	{
		this.context = context;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Utility methods
	//---
	//---------------------------------------------------------------------------

	public static String[] splitString(String str)
	{
		return splitString(str, ",");
	}

	//---------------------------------------------------------------------------

	/** Splits a single string value into individual substrings
	  * and returns it as an array.
	  * @return Array of strings
	  * @param str String which needs to be split
	  *
	  * @param splitChar Character which seperates individual
	  */

	public static String[] splitString(String str, String splitChar)
	{
		ArrayList list = new ArrayList();

		char split = splitChar.charAt(0);

		int last=0;
		int index=0;

		while(index<str.length())
		{
			if (str.charAt(index) == split)
			{
				list.add(str.substring(last, index));
				last = index +1;
			}

			index++;
		}

		list.add(str.substring(last));

		String[] strings = new String[list.size()];

		int i=0;

		Iterator iter=list.iterator();

		while(iter.hasNext())
			strings[i++] = (String) iter.next();

		return strings;
	 }

	//---------------------------------------------------------------------------

	/** Load a utility class to a velocity context. It is often
	  * desirable to call custom functions while generating files
	  * in velocity.
	  * <p>
	  * TODO:<br>
	  * 1.) Write a class which has a defualt constructor and
	  *     has one or more of your functions in it. The functions
	  *     must follow velocity guidlines. <br>
	  * 2.) Add the class to your classpath so that class loader
	  *     can load them at runtime.
	  * 3.) In your velocity control file load your class:
	  *     <pre>#util.addClass("myContextName", "my.class.Name")</pre>
	  * 4.) You can then access your class from velocity context
	  *     as follows: <pre>$myContextName.myFunction()</pre>
	  */

	public void addClass(String contextName, String clazzName) throws Exception
	{
		Class clazz = Class.forName(clazzName);
		context.put(contextName, clazz.newInstance());
	}
}

//==============================================================================
