//==============================================================================
//===
//===   WorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.editor.struct.work;

import javax.swing.JPanel;
import javax.swing.text.html.HTML;

import org.dlib.gui.MultiPanel;
import org.dlib.gui.html.HtmlToolkit;

import druid.util.gui.editor.struct.ElementInfo;

//==============================================================================

public class WorkPanel extends MultiPanel
{
	private ContentPanel panContent = new ContentPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public WorkPanel()
	{
		add("blank",   new JPanel());
		add("content", panContent);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(ElementInfo info, HtmlToolkit kit)
	{
		if (info == null)
		{
			show("blank");
			return;
		}

		if (info.tag == HTML.Tag.CONTENT || info.tag == HTML.Tag.P)
		{
			panContent.refresh(info, kit);
			show("content");
		}

		else
		{
			show("blank");
		}
	}
}

//==============================================================================
