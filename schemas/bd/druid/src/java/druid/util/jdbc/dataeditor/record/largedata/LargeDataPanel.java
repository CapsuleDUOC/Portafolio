//==============================================================================
//===
//===   LargeDataPanel : handles binary data and blobs
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc.dataeditor.record.largedata;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class LargeDataPanel extends TTabbedPane
{
	private AsciiPanel  asciiPanel  = new AsciiPanel();
	private BinaryPanel binaryPanel = new BinaryPanel();

	//---------------------------------------------------------------------------

	public LargeDataPanel()
	{
		addTab("Ascii",  asciiPanel);
		addTab("Binary", binaryPanel);
	}

	//---------------------------------------------------------------------------

	public void setValue(byte[] data)
	{
		asciiPanel.setValue(data);
		binaryPanel.setValue(data);
	}
}

//==============================================================================
