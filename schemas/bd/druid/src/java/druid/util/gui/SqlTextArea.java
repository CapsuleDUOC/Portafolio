//==============================================================================
//===
//===   SqlTextArea
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import druid.core.config.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlException;
import org.dlib.xml.reader.XmlReader;
import org.jedit.KeywordMap;
import org.jedit.SQLTokenMarker;
import org.jedit.Token;

//==============================================================================

public class SqlTextArea extends BasicTextArea
{
	private static boolean bInit = false;

	private static String syntax;

	private static KeywordMap kMap;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public SqlTextArea()
	{
		if (!bInit || syntax.compareTo(Config.general.sqlSyntax) != 0)
		{
			bInit = true;
			syntax=Config.general.sqlSyntax;
			kMap=null; // To force syntax reloading on configuration changes
		}

		if(kMap==null)
		{
			kMap = new KeywordMap(true);
 			fillKeywordMap(kMap);
		}

		setTokenMarker(new SQLTokenMarker(kMap, true));
	}

	//---------------------------------------------------------------------------
	/**
	 * Method to read syntax highlight from xml file
	 */

	private void fillKeywordMap(KeywordMap kmap)
	{
		String syntaxFile=Config.dir.data + File.separator + "syntax" + File.separator + Config.general.sqlSyntax + ".xml";
		try
		{
			XmlDocument syntax=new XmlReader().read(syntaxFile);
			XmlElement root=syntax.getRootElement();
			fillKeywords(root.getChild("keywords"),  kmap, Token.KEYWORD1);
			fillKeywords(root.getChild("functions"), kmap, Token.KEYWORD2);
			fillKeywords(root.getChild("datatypes"), kmap, Token.KEYWORD3);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (XmlException e)
		{
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------
	/**
	 * Fill keywordMap with another portion of keywords
	 * @param root root element containing keywords
	 * @param map map to fill
	 * @param keyword type of keywords
	 */

	private void fillKeywords(XmlElement root, KeywordMap map, byte keyword)
	{
		if(root==null || map==null)
			return;

		List lst=root.getChildren();
		Iterator it=lst.iterator();
		String value;

		while(it.hasNext())
		{
			value=((XmlElement)it.next()).getValue().trim();

			if(value!=null && value.length() > 0)
				map.add(value, keyword);
		}
	}
}

//==============================================================================

