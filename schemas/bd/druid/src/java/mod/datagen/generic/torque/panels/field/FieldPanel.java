//==============================================================================
//===
//===   FieldPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.torque.panels.field;

import javax.swing.JPanel;

import mod.datagen.generic.torque.FieldSettings;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;

//==============================================================================

public class FieldPanel extends JPanel
{
	private TTextFieldGuardian txtJavaName = new TTextFieldGuardian("JavaName");
	private TTextFieldGuardian txtInputVal = new TTextFieldGuardian("InputValid");
	private TComboBoxGuardian  tcbJavaType = new TComboBoxGuardian("JavaType");
	private TComboBoxGuardian  tcbJavaNam  = new TComboBoxGuardian("JavaNaming");
	private TCheckBoxGuardian  chbAutoInc  = new TCheckBoxGuardian("AutoIncrement", "Auto increment");

	//---------------------------------------------------------------------------

	public FieldPanel()
	{
		FlexLayout flexL = new FlexLayout(2, 6);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Java name"));
		add("0,1", new TLabel("Input valid."));
		add("0,2", new TLabel("Java type"));
		add("0,3", new TLabel("Java naming"));

		add("1,0,x", txtJavaName);
		add("1,1,x", txtInputVal);
		add("1,2,x", tcbJavaType);
		add("1,3,x", tcbJavaNam);

		add("0,5,x,c,2", chbAutoInc);

		//--- setup comboboxes

		tcbJavaType.addItem(FieldSettings.JAVATYPE_PRIMITIVE, "Primitive");
		tcbJavaType.addItem(FieldSettings.JAVATYPE_OBJECT,    "Object");

		tcbJavaNam.addItem(FieldSettings.JAVANAM_UNDERSCORE, "Underscore");
		tcbJavaNam.addItem(FieldSettings.JAVANAM_NOCHANGE,   "No change");
		tcbJavaNam.addItem(FieldSettings.JAVANAM_JAVANAME,   "Java name");
	}

	//---------------------------------------------------------------------------

	public void refresh(FieldSettings s)
	{
		txtJavaName.refresh(s);
		txtInputVal.refresh(s);
		tcbJavaType.refresh(s);
		tcbJavaNam .refresh(s);
		chbAutoInc .refresh(s);
	}
}

//==============================================================================
