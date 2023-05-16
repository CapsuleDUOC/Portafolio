//==============================================================================
//===
//===   ContentPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui.editor.struct.work;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextArea;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.html.HtmlToolkit;

import druid.util.gui.editor.struct.ElementInfo;

//==============================================================================

public class ContentPanel extends JPanel
{
	private TTextArea taContent = new TTextArea(4, 20);
	private FlexTable ftAttribs = new FlexTable(false);

	private DefaultFlexTableModel model = new DefaultFlexTableModel();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ContentPanel()
	{
		FlexLayout fl = new FlexLayout(1,2);
		fl.setColProp(0, FlexLayout.EXPAND);
		fl.setRowProp(1, FlexLayout.EXPAND);
		setLayout(fl);

		add("0,0,x",   new TPanel("Attribs", ftAttribs));
		add("0,1,x,x", new TPanel("Content", taContent));

		model.addColumn("Name",  100);
		model.addColumn("Value", 100);
		ftAttribs.setFlexModel(model);
		ftAttribs.setPreferredSize(new Dimension(250,130));

		taContent.setEditable(false);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(ElementInfo info, HtmlToolkit kit)
	{
		fillTable(info);

		try
		{
			String text = kit.getDocument().getText(info.start, info.end - info.start);

			taContent.setText(text);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void fillTable(ElementInfo info)
	{
		model.clearData();

		for(Enumeration e=info.attribs.keys(); e.hasMoreElements();)
		{
			Object name  = e.nextElement();
			Object value = info.attribs.get(name);

			Vector row = new Vector();
			row.add(name);
			row.add(value);

			model.addRow(row);
		}

		ftAttribs.updateTable();
	}
}

//==============================================================================
