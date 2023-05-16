//==============================================================================
//===
//===   UrlInfo
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman.urlcreator;

import org.dlib.xml.XmlElement;

//==============================================================================

class UrlInfo
{
	public XmlElement elUrl;
	public XmlElement elDb;

	//---------------------------------------------------------------------------

	public UrlInfo(XmlElement url, XmlElement db)
	{
		elUrl = url;
		elDb  = db;
	}
}

//==============================================================================
