//==============================================================================
//===
//===   FontChooserDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf.dialogs;

import java.awt.Frame;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mod.datagen.docs.pdf.FontInfo;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.ROTextField;
import org.dlib.gui.TCheckBox;
import org.dlib.gui.TComboBox;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

import com.lowagie.text.FontFactory;

import druid.dialogs.BasicConfigDialog;
import druid.util.gui.ImageFactory;

//==============================================================================

public class FontChooserDialog extends BasicConfigDialog
{
	private FontPanel fontPanel;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public FontChooserDialog(Frame f)
	{
		super(f);

		setTitle("Choose font");
	}

	//---------------------------------------------------------------------------

	protected JComponent getCentralPanel()
	{
		fontPanel = new FontPanel();

		return fontPanel;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public FontInfo run(FontInfo fi)
	{
		fontPanel.refresh(fi);
		showDialog();

		return isCancelled() ? null : fontPanel.getFontInfo();
	}
}

//==============================================================================

class FontPanel extends JPanel implements ChangeListener
{
	private TComboBox   tcbFamily = new TComboBox();
	private ROTextField txtSize   = new ROTextField(3);
	private JSlider     jsSize    = new JSlider(4, 40);
	private JCheckBox   jchBold   = new TCheckBox("Bold");
	private JCheckBox   jchItalic = new TCheckBox("Italic");

	//---------------------------------------------------------------------------

	public FontPanel()
	{
		FlexLayout flexL = new FlexLayout(3,3);
		flexL.setColProp(2, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",       new TLabel("Family"));
		add("0,1",       new TLabel("Size"));
		add("0,2,x,c,3", createStylePanel());

		add("1,0,x,c,2", tcbFamily);
		add("1,1",       txtSize);
		add("2,1,x",     jsSize);

		//------------------------------------------------------------------------
		//--- controls setup

		//--- family combo

		tcbFamily.addItem(ImageFactory.FONT_FAMIL, FontFactory.HELVETICA, FontFactory.HELVETICA);
		tcbFamily.addItem(ImageFactory.FONT_FAMIL, FontFactory.TIMES,     FontFactory.TIMES);
		tcbFamily.addItem(ImageFactory.FONT_FAMIL, FontFactory.COURIER,   FontFactory.COURIER);

		//--- trigger events to sync the slider with the textfield

		jsSize.addChangeListener(this);
		jsSize.setValue(4);
	}

	//---------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		JSlider slider = (JSlider) e.getSource();

		txtSize.setText(slider.getValue() +"");
	}

	//---------------------------------------------------------------------------

	public void refresh(FontInfo fi)
	{
		tcbFamily.setSelectedKey(fi.name);
		jsSize.setValue(fi.size);
		jchBold.setSelected(fi.bold);
		jchItalic.setSelected(fi.italic);
	}

	//---------------------------------------------------------------------------

	public FontInfo getFontInfo()
	{
		String  name   = tcbFamily.getSelectedKey();
		int     size   = jsSize.getValue();
		boolean bold   = jchBold.isSelected();
		boolean italic = jchItalic.isSelected();

		return new FontInfo(name, size, bold, italic);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private JPanel createStylePanel()
	{
		TPanel p = new TPanel("Style");

		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x", jchBold);
		p.add("0,1,x", jchItalic);

		return p;
	}
}

//==============================================================================
