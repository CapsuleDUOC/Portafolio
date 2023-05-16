//==============================================================================
//===
//===   General
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.config.general;

import org.dlib.gui.CustomLook;
import org.dlib.tools.Util;
import org.dlib.xml.XmlElement;

//==============================================================================

public class General
{
	public static final String TAGNAME = "general";

	public boolean showTip        = true;
	public boolean reloadLastProj = true;
	public boolean createBackup   = false;
	public boolean guiAAliasing   = true;
	public String  sqlTerminator  = ";";
	public String  sqlSyntax      = "postgres";

	public Window window = new Window();

	//---------------------------------------------------------------------------

	private boolean textAAliasing  = true;

	//---------------------------------------------------------------------------

	private static final String SHOW_TIP       = "showTip";
	private static final String RELOAD_PROJ    = "reloadLastProj";
	private static final String CREATE_BACKUP  = "createBackup";
	private static final String TEXT_AALIASING = "textAAliasing";
	private static final String GUI_AALIASING  = "guiAAliasing";
	private static final String SQL_TERMINATOR = "sqlTerminator";
	private static final String SQL_SYNTAX     = "sqlSyntax";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public General() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setupConfig(XmlElement el)
	{
		if (el == null) return;

		showTip        = Util.getBooleanValue(el.getChildValue(SHOW_TIP),       true);
		reloadLastProj = Util.getBooleanValue(el.getChildValue(RELOAD_PROJ),    true);
		createBackup   = Util.getBooleanValue(el.getChildValue(CREATE_BACKUP), false);
		textAAliasing  = Util.getBooleanValue(el.getChildValue(TEXT_AALIASING), true);
		guiAAliasing   = Util.getBooleanValue(el.getChildValue(GUI_AALIASING),  true);
		sqlTerminator  = Util.getStringValue(el.getChildValue(SQL_TERMINATOR), ";");
		sqlSyntax      = Util.getStringValue(el.getChildValue(SQL_SYNTAX)    , "postgres");

		window.setupConfig(el.getChild(Window.TAGNAME));

		CustomLook.setTextAntiAliasing(textAAliasing);
	}

	//---------------------------------------------------------------------------

	public XmlElement getConfig()
	{
		XmlElement elRoot = new XmlElement(TAGNAME);

		elRoot	.addChild(new XmlElement(SHOW_TIP,       showTip        +""))
					.addChild(new XmlElement(RELOAD_PROJ,    reloadLastProj +""))
					.addChild(new XmlElement(CREATE_BACKUP,  createBackup   +""))
					.addChild(new XmlElement(TEXT_AALIASING, textAAliasing  +""))
					.addChild(new XmlElement(GUI_AALIASING,  guiAAliasing   +""))
					.addChild(new XmlElement(SQL_TERMINATOR,  sqlTerminator   +""))
					.addChild(new XmlElement(SQL_SYNTAX,	  sqlSyntax	 +""))
					.addChild(window.getConfig());

		return elRoot;
	}

	//---------------------------------------------------------------------------

	public void setTextAAliasing(boolean yesno)
	{
		textAAliasing = yesno;
		CustomLook.setTextAntiAliasing(yesno);
	}

	//---------------------------------------------------------------------------

	public boolean isTextAAliased() { return textAAliasing; }
}

//==============================================================================
