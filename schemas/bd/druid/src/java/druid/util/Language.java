//==============================================================================
//===
//===   Language
//===
//===   Copyright (C) by Andrea Carboni, Antonio Gallardo
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.dlib.gui.TComboBox;

import druid.core.config.Config;

//==============================================================================

public class Language
{
	private static final String BASIC_LOCALE_PATH = "/locale";

	private static final String msgEmptyDir = "<cannot scan directory>";
	private static final String prefix      = "message_";
	private static final String suffix      = ".properties";

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static void fillComboBox(TComboBox tcb)
	{
		tcb.removeAllItems();

		File files[] = new File(Config.dir.data + BASIC_LOCALE_PATH).listFiles();

		if (files != null)
		{
			for (int i=0; i<files.length; i++)
				if (files[i].isFile())
				{
					String fileName = files[i].getName();

					if (fileName.startsWith(prefix) && fileName.endsWith(suffix))
					{
						Locale newLocale = parseLocale(fileName.substring(prefix.length(), fileName.length() - suffix.length()));

						if (newLocale != null)
							tcb.addItem(newLocale.toString(), newLocale.getDisplayName());
					}
				}
		}
		else
			tcb.addItem("en", msgEmptyDir);
	}

	//---------------------------------------------------------------------------

	public static Hashtable loadLanguage(String localePath, String language)
	{
		String basicLocalePath = Config.dir.data + BASIC_LOCALE_PATH;

		Hashtable htStrings = loadLanguageI(basicLocalePath, language);

		if (htStrings != null)
			if (localePath != null)
			{
				Hashtable ht = loadLanguageI(localePath, language);

				if (ht != null)
					htStrings.putAll(ht);
			}

		return htStrings;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static Hashtable loadLanguageI(String localePath, String language)
	{
		Hashtable htLangStrings = new Hashtable(200);

		try
		{
			//--- Define Locale Language

			Locale currentLocale = parseLocale(language);

			if (currentLocale == null)
				currentLocale = Locale.getDefault();

			//--- Set the path to the data/locale dir (dont remove!)

			URL localeURL[] = new URL[1];
	  		localeURL[0] = new URL("file:" + localePath +"/");
		  	URLClassLoader pcl = new URLClassLoader(localeURL);

			//--- Get all locale messages and put them into a HashTable

			ResourceBundle bundle = ResourceBundle.getBundle("message", currentLocale, pcl);

		  	for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); )
			{
				String str = e.nextElement().toString();

				htLangStrings.put(str, bundle.getString(str));
		  	}

			return htLangStrings;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//---------------------------------------------------------------------------

	/** parseLocale String from the file name
	  * @param locale The String to be parsed, like "xx[_XX[_xx]]" .
	  * @return Locale class parsed on success
	  */

	private static Locale parseLocale(String locale)
	{
		String language="";
		String country = "";
		String variant = "";

		StringTokenizer st = new StringTokenizer(locale, "_");

		if (st.hasMoreTokens()) language = st.nextToken();
		if (st.hasMoreTokens()) country  = st.nextToken();
		if (st.hasMoreTokens()) variant  = st.nextToken();

		return new Locale(language, country, variant);
	}
}

//==============================================================================
