//==============================================================================
//===
//===   JavaUtil
//===
//===   Copyright (C) by Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.helpers;

//==============================================================================

	/** Java utility calss that might be usefull in generating
	  * java related files from velocity.
	  *
	  * @author mhevery
	  */

public class JavaUtil
{
	/** Formats a string to accepted java codeing style attribute
	  * name. This is done by making the first letter lowercase.
	  */

	public String attribute(String str)
	{
		return org.dlib.tools.Util.firstCharLowerCase(str);
	}

	//---------------------------------------------------------------------------

	/** Formats a string to accepted java codeing style method
	  * name. This is done by making the first letter uppercase.
	  */

	public String method(String str)
	{
		return org.dlib.tools.Util.firstCharUpperCase(str);
	}

	//---------------------------------------------------------------------------

	/** Formats a string to accepted java codeing style method
	  * getter name. This is done by prepending "get" and making
	  * the first letter uppercase.
	  */

	public String getter(String str)
	{
		return "get" + method(str);
	}

	//---------------------------------------------------------------------------

	/** Formats a string to accepted java codeing style method
	  * setter name. This is done by prepending "set" and making
	  * the first letter uppercase.
	  */

	public String setter(String str)
	{
		return "set" + method(str);
	}
}

//==============================================================================
