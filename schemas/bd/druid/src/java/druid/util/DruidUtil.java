//==============================================================================
//===
//===   DruidUtil
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JOptionPane;

import org.dlib.gui.flextable.DefaultFlexTableModel;
import org.dlib.gui.flextable.FlexTable;
import org.dlib.gui.treeview.TreeViewNode;

import druid.core.DataTracker;
import druid.core.config.Config;
import druid.util.decoder.Decoder;
import druid.util.gui.Dialogs;

//==============================================================================

public class DruidUtil
{
	public static void moveTreeNode(TreeViewNode node, boolean moveUp)
	{
		TreeViewNode parent = (TreeViewNode)node.getParent();

		int pos = parent.getIndex(node);

		if (moveUp)
			parent.swapNodes(pos, pos-1);
		else
			parent.swapNodes(pos, pos+1);

		node.select();

		DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public static void removeNode(TreeViewNode node, boolean setChangedBit)
	{
		TreeViewNode parent = (TreeViewNode)node.getParent();

		int ndx = parent.getIndex(node);
		parent.removeChild(node);

		if (ndx == parent.getChildCount()) ndx--;

		TreeViewNode show = (parent.getChildCount() == 0) ? parent : parent.getChild(ndx);

		show.select();

		if (setChangedBit)
			DataTracker.setDataChanged();
	}

	//---------------------------------------------------------------------------

	public static void find(FlexTable table, String pattern)
	{
		if (pattern == null || pattern.equals("")) return;

		pattern = pattern.toLowerCase();

		int selRow   = table.getSelectedRow();
		int firstRow = selRow;
		int tableCol = table.getColumnCount();
		int rowCount = table.getRowCount();

		if (selRow != -1)
			if (++selRow == rowCount) selRow = 0;

		while(true)
		{
			while(selRow != -1)
			{
				for(int i=0; i<tableCol; i++)
				{
					Object o = table.getValueAt(selRow, i);

					if (o != null)
					{
						if (o.toString().toLowerCase().indexOf(pattern) != -1)
						{
							table.selectRow(selRow);
							return;
						}
					}
				}

				if (selRow == firstRow)
				{
					JOptionPane.showMessageDialog(table, "'" + pattern + "' not found",
													"Search result", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				selRow++;

				if (selRow == rowCount)
				{
					if (firstRow == -1)
					{
						JOptionPane.showMessageDialog(table, "'" + pattern + "' not found",
														"Search result", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					selRow = 0;
				}
			}

			if (table.getRowCount() == 0) return;

			selRow = 0;
		}
	}

	//---------------------------------------------------------------------------

	public static int removeRowAndSelect(FlexTable flexTable, int row)
	{
		DefaultFlexTableModel flexModel = (DefaultFlexTableModel) flexTable.getFlexModel();

		flexModel.removeRow(row);
		flexTable.updateTable();

		if (flexModel.getRowCount() == 0)
		{
			flexTable.clearSelection();
			return -1;
		}

		if (row == flexModel.getRowCount()) row--;

		flexTable.selectRow(row);
		return row;
	}

	//---------------------------------------------------------------------------

	public static boolean saveText(Component c, String fileName, String text) {
        Writer fw = null;
        BufferedWriter bw = null;
		try {
			fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
			bw.write(text);
		} catch(IOException e) {
			Dialogs.showSaveError(c, fileName, e.toString());
			return false;
		} finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) { /* Do nothing */ }
        }
        return true;
	}

	//---------------------------------------------------------------------------

	public static String applyDecoder(Decoder dec, String s)
	{
		if (dec != null) 		return dec.decode(s);
			else					return s;
	}
	
	//---------------------------------------------------------------------------
	
	public static String toAbsolutePath(String output)
	{
		if (!new File(output).isAbsolute())
			if (Config.recentFiles.getFileCount() != 0)
			{
				File prjDir = new File(Config.recentFiles.getFileAt(0)).getParentFile();
				
				if (prjDir != null)
					return new File(prjDir, output).getAbsolutePath();
			}
		
		return output;
	}
}

//==============================================================================
