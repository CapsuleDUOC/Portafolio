//==============================================================================
//===
//===   OptionPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.template.velocity;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextArea;
import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.flextable.FlexTableConfirmator;
import org.dlib.gui.flextable.FlexTableSelEvent;
import org.dlib.gui.flextable.FlexTableSelListener;

import druid.data.ModulesConfig;
import druid.interfaces.BasicModule;

//==============================================================================

class OptionPanel extends TPanel implements FlexTableSelListener, FlexTableConfirmator
{
	private FlexTable flexTable = new FlexTable();
	private TTextArea txaDescr  = new TTextArea(4, 30);

	private int iSelRow;

	private Vector vGenerators;

	private DefaultFlexTableModel flexModel = new DefaultFlexTableModel();

	private BasicModule   mod;
	private ModulesConfig mc;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OptionPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0,x,x", flexTable);
		add("0,1,x",   getTPanel());

		flexModel.addColumn("Generator", 100, false);
		flexModel.addColumn("Output",    100);
		flexModel.addColumn("Use",        16);
		flexModel.setConfirmator(this);

		flexTable.addSelectionListener(this);
		flexTable.setEditable(true);
		flexTable.setFlexModel(flexModel);

		txaDescr.setEditable(false);
	}

	//---------------------------------------------------------------------------

	private JPanel getTPanel()
	{
		TPanel p = new TPanel("Description");

		FlexLayout flexL = new FlexLayout(1,1);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		p.setLayout(flexL);

		p.add("0,0,x,x", txaDescr);

		return p;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void refresh(BasicModule mod, ModulesConfig mc)
	{
		this.mod = mod;
		this.mc  = mc;

		flexModel.clearData();

		vGenerators = Generator.getGenerators();

		if (vGenerators == null)
		{
			JOptionPane.showMessageDialog(this,
						"Cannot obtain generators list\n"+
						"The list is located in <druid>/data/templates/velocity",
						"Problem...", JOptionPane.WARNING_MESSAGE);
		}

		else
		{
			for(int i=0; i<vGenerators.size(); i++)
			{
				Generator gen = (Generator) vGenerators.get(i);

				Vector row = new Vector();

				row.addElement(gen.sDescr);

				String value = mc.getValue(mod, VelocityModule.PREFIX + gen.sDir);

				if (value.equals(""))
				{
					row.addElement("");
					row.addElement(Boolean.FALSE);
				}
				else
				{
					boolean use = value.substring(0,1).equals("Y");

					row.addElement(value.substring(2));
					row.addElement(Boolean.valueOf(use));
				}

				flexModel.addRow(row);
			}
		}

		flexTable.updateTable();
		flexTable.clearSelection();

		iSelRow = -1;
		updateControls();
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableConfirmator
	//---
	//---------------------------------------------------------------------------

	public boolean confirmValueChanged(int row, int col, Object value)
	{
		if (vGenerators != null)
		{
			Generator gen = (Generator) Generator.getGenerators().get(row);

			String  out = (col == 1) 	? value.toString()
												: flexModel.getValueAt(row, 1).toString();

			boolean use = (col == 2) 	? ((Boolean)value).booleanValue()
												: ((Boolean) flexModel.getValueAt(row, 2)).booleanValue();

			String sUse = use ? "Y" : "N";

			mc.setValue(mod, VelocityModule.PREFIX + gen.sDir, sUse + "|" + out);
		}

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- FlexTableSelListener
	//---
	//---------------------------------------------------------------------------

	public void rowSelected(FlexTableSelEvent e)
	{
		iSelRow = e.getSelectedRow();
		updateControls();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void updateControls()
	{
		if (iSelRow == -1)
		{
			txaDescr.setText("");
		}
		else
		{
			Generator gen = (Generator) Generator.getGenerators().get(iSelRow);
			txaDescr.setText(gen.sLongDescr);
		}
	}
}

//==============================================================================
