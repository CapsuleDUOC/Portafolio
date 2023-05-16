//==============================================================================
//===
//===   RecordListPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.jdbc;

import druid.core.jdbc.RecordList;
import druid.util.gui.ImageFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.FlexTable;

//==============================================================================

public class RecordListPanel extends JPanel
{
	protected FlexTable flexTable = new FlexTable();

	//--- column fit button in table structure view added (in case a table has
	//--- long comments and many indices etc.)

	protected AbstractButton btnFit;

	//---------------------------------------------------------------------------

	public RecordListPanel()
	{
		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		TToolBar toolBar = new TToolBar();

		btnFit = toolBar.add(ImageFactory.COL_FIT,
						new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								flexTable.setAutoResizeMode(btnFit.isSelected()
												? JTable.AUTO_RESIZE_ALL_COLUMNS
												: JTable.AUTO_RESIZE_OFF);
							}
						},
						"fit", "Show all fields in visible space", true);

		btnFit.setSelected(true);


		add("0,0,x",   toolBar);
		add("0,1,x,x", flexTable);
	}

	//---------------------------------------------------------------------------

	public void setEditable(boolean yesno)
	{
		flexTable.setEditable(yesno);
	}

	//---------------------------------------------------------------------------

	public void refresh(RecordList rl)
	{
		if (rl == null) rl = new RecordList();

		flexTable.setFlexModel(rl);
	}
}

//==============================================================================
