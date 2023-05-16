//==============================================================================
//===
//===   DriverDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.jdbc.drivers;

import druid.core.jdbc.manager.JdbcDriver;
import druid.core.jdbc.manager.JdbcManager;
import druid.util.gui.ImageFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.GuiUtil;
import org.dlib.gui.TDialog;
import org.dlib.gui.TToolBar;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;

//==============================================================================

public class DriverDialog extends TDialog implements ActionListener, FlexTableSelListener
{
	private FlexTable ft = new FlexTable(false);

	private DefaultFlexTableModel flexModel = new DefaultFlexTableModel();

	private AbstractButton btnAdd;
	private AbstractButton btnRem;
	private AbstractButton btnInfo;

	private int iSelectedRow = -1;

	private JFileChooser fc = new JFileChooser();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DriverDialog(Frame frame)
	{
		super(frame, "Jdbc Drivers", true);

		JPanel p = new JPanel();

		FlexLayout flexL = new FlexLayout(1,2,4,4);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(1, FlexLayout.EXPAND);
		p.setLayout(flexL);

		TToolBar toolBar = new TToolBar();

		btnAdd  = toolBar.add(ImageFactory.NEW,    this, "add",    "Add a new driver (should support the JDBC 2.1 API)");
		btnRem  = toolBar.add(ImageFactory.DELETE, this, "remove", "Remove the current driver from list");

		p.add("0,0,x,x", toolBar);
		p.add("0,1,x,x", ft);

		getContentPane().add(p, BorderLayout.CENTER);

		setupTable();
		refreshButtons();

		ft.addSelectionListener(this);
		ft.setFlexModel(flexModel);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Private methods
	//---
	//---------------------------------------------------------------------------

	private void setupTable()
	{
		ft.setPreferredSize(new Dimension(550, 150));

		flexModel.addColumn("Name",    230);
		flexModel.addColumn("Version", 60);
		flexModel.addColumn("Status",  50);
		flexModel.addColumn("Driver",  260);

		//------------------------------------------------------------------------
		//--- update table and exit

		for(int i=0; i<JdbcManager.getDriverCount(); i++)
			addTableRow(JdbcManager.getJdbcDriverAt(i));

		ft.updateTable();
	}

	//---------------------------------------------------------------------------

	private void addTableRow(JdbcDriver drv)
	{
		ImageIcon img = ImageFactory.UNKNOWN;

		if (drv.isLoaded())
			img = drv.isCompliant() ? ImageFactory.SET : ImageFactory.REDSET;

		Vector row = new Vector();

		row.addElement(drv.getName());
		row.addElement(drv.getVersion());
		row.addElement(img);
		row.addElement(drv.getFile());

		flexModel.addRow(row);
	}

	//---------------------------------------------------------------------------

	private void refreshButtons()
	{
		btnRem.setEnabled(iSelectedRow != -1);
	}

	//---------------------------------------------------------------------------
	//---
	//---   Listeners
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("add"))    handle_add();
		if (cmd.equals("remove")) handle_remove();
	}

	//---------------------------------------------------------------------------

	private void handle_add()
	{
		fc.setDialogTitle("Select a driver");

		int res = fc.showDialog(GuiUtil.getFrame(this), "Ok");
		if (res == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();

			//--- support for multiple driver classes in one driver JAR file
			List drivers = JdbcManager.importDriver(f.getPath());

			if (drivers != null)
			{
				for (int i = 0; i < drivers.size(); i++)
				{
					JdbcDriver drv = (JdbcDriver) drivers.get(i);
					addTableRow(drv);
					ft.updateTable();
					ft.selectLastRow();
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this,
								"Cannot add the driver to the system.\n" +
								"Possible causes:\n" +
								" - The selected file doesn't contain any driver class \n" +
								" - The selected file is not a valid jar archive \n" +
								" - The driver has already been added" +
								"",
								"Driver Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	//---------------------------------------------------------------------------

	private void handle_remove()
	{
		String name = (String) ft.getValueAt(iSelectedRow, 0);

		if (JdbcManager.removeDriver(name))
		{
			flexModel.removeRow(iSelectedRow);
			ft.updateTable();

			if (iSelectedRow == ft.getRowCount())
				ft.clearSelection();
		}
	}

	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		iSelectedRow = e.getSelectedRow();

		refreshButtons();
	}
}

//==============================================================================
