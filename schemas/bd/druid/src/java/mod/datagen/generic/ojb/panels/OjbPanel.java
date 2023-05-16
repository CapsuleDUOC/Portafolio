//==============================================================================
//===
//===   OjbPanel
//===
//===   Copyright (C) by Antonio Gallardo.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.ojb.panels;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.datagen.generic.ojb.Settings;

//==============================================================================

/**
 *  OJB Panel Configuration
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: OjbPanel.java,v 1.5.2.1 2006/01/17 01:54:48 antoniog Exp $
*/

class OjbPanel extends JPanel
{
	private TTextFieldGuardian txtPackage              = new TTextFieldGuardian("OjbPackage");
	private TTextFieldGuardian txtClassSuffix          = new TTextFieldGuardian("OjbClassSuffix");
	private TCheckBoxGuardian  chkCollectionDescriptor = new TCheckBoxGuardian("CollectionDescriptor", "Use Collection Descriptor");
	private TCheckBoxGuardian  chkReferenceDescriptor  = new TCheckBoxGuardian("ReferenceDescriptor",  "Use Reference Descriptor");
	private TCheckBoxGuardian  chkDateConvertor        = new TCheckBoxGuardian("DateConvertor",        "Use Date Convertor");

	//---------------------------------------------------------------------------

	public OjbPanel()
	{
		FlexLayout flexL = new FlexLayout(2,6);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0",   new TLabel("Package"));
		add("0,1",   new TLabel("Class Suffix"));

		add("1,0,x", txtPackage);
		add("1,1,x", txtClassSuffix);

		add("0,3,x,c,2", chkCollectionDescriptor);
		add("0,4,x,c,2", chkReferenceDescriptor);
		add("0,5,x,c,2", chkDateConvertor);

		chkDateConvertor.setToolTipText("Allow conversion between java.util.Date (Bean) <-> java.sql.Date (OJB)");
	}

	//---------------------------------------------------------------------------

	public void refresh(Settings s)
	{
		txtPackage.refresh(s);
		txtClassSuffix.refresh(s);
		chkCollectionDescriptor.refresh(s);
		chkReferenceDescriptor.refresh(s);
		chkDateConvertor.refresh(s);
	}
}

//==============================================================================
