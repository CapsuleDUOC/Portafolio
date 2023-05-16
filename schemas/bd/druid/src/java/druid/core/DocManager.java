//==============================================================================
//===
//===   DocManager
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.util.List;

import org.dlib.tools.HtmlLib;
import org.dlib.xml.XmlElement;

import druid.core.config.Config;

//==============================================================================

public class DocManager
{
	public static final String PARAG    = "p";
	public static final String    TEXT  = "t";
	public static final String    IMAGE = "i";
	public static final String OLIST    = "ol";
	public static final String ULIST    = "ul";
	public static final String    ITEM  = "li";

	//--- paragraph attributes

	public static final String ATTR_ALIGN        = "a";
	public static final String ATTR_ALIGN_LEFT   = "l";
	public static final String ATTR_ALIGN_CENTER = "c";
	public static final String ATTR_ALIGN_RIGHT  = "r";

	//--- text attributes

	public static final String ATTR_BOLD        = "b";
	public static final String ATTR_ITALIC      = "i";
	public static final String ATTR_UNDERLINE   = "u";
	public static final String ATTR_COLOR       = "c";
	public static final String ATTR_FONTNAME    = "fn";
	public static final String ATTR_FONTSIZE    = "fs";

	//--- image attributes

	public static final String ATTR_IMAGE_SRC   = "s";

	//---------------------------------------------------------------------------

	public static void convert(XmlElement xmlDoc, String text)
	{
		XmlElement elPar  = new XmlElement(PARAG);
		XmlElement elText = new XmlElement(TEXT, text);

		elPar.addChild(elText);
		xmlDoc.addChild(elPar);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Conversion : DOC -> ASCII
	//---
	//---------------------------------------------------------------------------

	public static String toText(XmlElement docs)
	{
		StringBuffer sb = new StringBuffer();

		//--- handle children

		List list = docs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.PARAG))
				sb.append(toTextParagraph(child));

			if (child.getName().equals(DocManager.OLIST))
				sb.append(toTextList(true, child));

			if (child.getName().equals(DocManager.ULIST))
				sb.append(toTextList(false, child));
		}

		return sb.toString().trim();
	}

	//---------------------------------------------------------------------------

	private static String toTextList(boolean enumerate, XmlElement e)
	{
		StringBuffer sb = new StringBuffer("\n");

		//--- handle children

		List list = e.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			String prefix = (enumerate) ? Integer.toString(i+1) : "-";

			if (child.getName().equals(DocManager.ITEM))
				sb.append(prefix +" "+ toText(child));
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private static String toTextParagraph(XmlElement e)
	{
		StringBuffer sb = new StringBuffer();

		//--- handle children (if any)

		List list = e.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.TEXT))
				sb.append(child.getValue());

			if (child.getName().equals(DocManager.IMAGE))
				sb.append("<image>");
		}

		//--- append closing tag

		sb.append(Config.os.lineSep);

		return sb.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Conversion : DOC -> HTML
	//---
	//---------------------------------------------------------------------------

	public static String toHtml(XmlElement docs)
	{
		StringBuffer sb = new StringBuffer();

		//--- handle children

		List list = docs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.PARAG))
				sb.append(toHtmlParagraph(child));

			if (child.getName().equals(DocManager.OLIST))
				sb.append(toHtmlList("ol", child));

			if (child.getName().equals(DocManager.ULIST))
				sb.append(toHtmlList("ul", child));
		}

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private static String toHtmlList(String tag, XmlElement e)
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<"+tag+">");

		//--- handle children

		List list = e.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.ITEM))
				sb.append(toHtmlListItem(child));
		}

		sb.append("</"+tag+">");

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private static String toHtmlListItem(XmlElement e)
	{
		return "<li>"+ toHtml(e) +"</li>";
	}

	//---------------------------------------------------------------------------

	private static String toHtmlParagraph(XmlElement e)
	{
		StringBuffer sb = new StringBuffer();

		//--- handle align attrib

		String align = e.getAttributeValue(DocManager.ATTR_ALIGN);

		if (align == null)
			sb.append("<p>");
		else
		{
			sb.append("<p align=\"");

			if (align.equals(DocManager.ATTR_ALIGN_RIGHT))
				sb.append("right");

			else if (align.equals(DocManager.ATTR_ALIGN_CENTER))
				sb.append("center");

			else
				sb.append("left");

			sb.append("\">");
		}

		//--- handle children (if any)

		List list = e.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.TEXT))
				sb.append(toHtmlText(child));

			if (child.getName().equals(DocManager.IMAGE))
				sb.append(toHtmlImage(child));
		}

		//--- append closing tag

		sb.append("</p>");

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private static String toHtmlText(XmlElement e)
	{
		String pre  = "";
		String post = "";

		String bold  = e.getAttributeValue(DocManager.ATTR_BOLD);
		String ital  = e.getAttributeValue(DocManager.ATTR_ITALIC);
		String under = e.getAttributeValue(DocManager.ATTR_UNDERLINE);
		String col   = e.getAttributeValue(DocManager.ATTR_COLOR);
		String font  = e.getAttributeValue(DocManager.ATTR_FONTNAME);
		String size  = e.getAttributeValue(DocManager.ATTR_FONTSIZE);

		if (font != null || size != null || col != null)
		{
			pre = pre + "<font";

			if (font != null)
				pre = pre + " face=\""+ font +"\"";

			if (size != null)
				pre = pre + " size=\""+ size +"\"";

			if (col != null)
				pre = pre + " color=\"#"+ col +"\"";

			pre = pre + ">";
			post = "</font>" + post;
		}

		if (bold != null)
		{
			pre = pre + "<b>";
			post = "</b>" + post;
		}

		if (ital != null)
		{
			pre = pre + "<i>";
			post = "</i>" + post;
		}

		if (under != null)
		{
			pre = pre + "<u>";
			post = "</u>" + post;
		}

		return pre + HtmlLib.encode(e.getValue()) + post;
	}

	//---------------------------------------------------------------------------

	private static String toHtmlImage(XmlElement e)
	{
		String src = e.getAttributeValue(DocManager.ATTR_IMAGE_SRC);

		return "<img align=\"bottom\" border=\"1\" src=\""+ src +"\"></img>";
	}
}

//==============================================================================
