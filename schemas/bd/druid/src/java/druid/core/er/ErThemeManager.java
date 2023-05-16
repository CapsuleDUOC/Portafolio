//==============================================================================
//===
//===   ErThemeManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.er;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlException;
import org.dlib.xml.reader.XmlReader;

import druid.data.er.LegendColor;

//==============================================================================

public class ErThemeManager
{
	private static Map<String, List<LegendColor>> themes = new HashMap<String, List<LegendColor>>();

	//---------------------------------------------------------------------------
	//---
	//--- Init method
	//---
	//---------------------------------------------------------------------------

	public static void init(String path)
	{
		File themeFiles[] = new File(path, "er").listFiles();

		if (themeFiles == null)
			System.out.println("Cannot scan directory : "+ path+"/er");
		else
			for (File themeFile : themeFiles)
				if (themeFile.isFile() && themeFile.getName().endsWith(".xml"))
					try
					{
						XmlDocument doc = new XmlReader().read(themeFile.getAbsolutePath());
						parseTheme(doc.getRootElement());
					
					}
					catch(XmlException e)
					{
						System.out.println("Theme has an invalid xml : "+ themeFile);
						System.out.println(" (C) --> "+ e.getMessage());
					}
					catch(IOException e)
					{
						System.out.println("Raised exception while reading theme : "+ themeFile);					
						System.out.println(" (C) --> "+ e.getMessage());
					}
	}

	//---------------------------------------------------------------------------
	
	private static void parseTheme(XmlElement root)
	{
		String name = root.getChildValue("name");
		
		List<LegendColor> colors = new ArrayList<LegendColor>();
		
		for (XmlElement col : (List<XmlElement>) root.getChildren("legendColor"))
			colors.add(buildLegendColor(col));
		
		themes.put(name, colors);
	}
	
	//---------------------------------------------------------------------------
	
	private static LegendColor buildLegendColor(XmlElement lcXml)
	{
		LegendColor lg = new LegendColor();
	
		lg.setName(lcXml.getAttributeValue("name"));
		
		for (XmlElement col : (List<XmlElement>) lcXml.getChildren("color"))
		{
			String type  = col.getAttributeValue("type");
			String color = col.getAttributeValue("color");
			
			if ("name".equals(type))
				lg.colName = buildColor(color);
			
			else if ("nameBg".equals(type))
				lg.colNameBg = buildColor(color);
			
			else if ("text".equals(type))
				lg.colText = buildColor(color);
			
			else if ("textBg".equals(type))
				lg.colTextBg = buildColor(color);
			
			else if ("bg".equals(type))
				lg.colBg = buildColor(color);
			
			else if ("border".equals(type))
				lg.colBorder = buildColor(color);
		}
		
		return lg;
	}
	
	//---------------------------------------------------------------------------
	
	private static Color buildColor(String color)
	{
		StringTokenizer st = new StringTokenizer(color, ",");
		
		int red   = Integer.parseInt(st.nextToken().trim());
		int green = Integer.parseInt(st.nextToken().trim());
		int blue  = Integer.parseInt(st.nextToken().trim());
		
		return new Color(red, green, blue);
	}
	
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------
	
	public static Set<String> getThemeNames()
	{
		return themes.keySet();
	}
	
	//---------------------------------------------------------------------------
	
	public static List<LegendColor> getThemeColors(String name)
	{
		return themes.get(name);
	}
}

//==============================================================================
