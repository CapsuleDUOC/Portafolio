//==============================================================================
//===
//===   GenerateDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.generation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.swing.SwingUtilities;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.TDialog;
import org.dlib.gui.TTextArea;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.Util;

import druid.core.IntegrityChecker;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.AbstractType;
import druid.interfaces.DataGenModule;
import druid.interfaces.Logger;

//==============================================================================

public class GenerateDialog extends TDialog implements Runnable, Logger
{
	protected TTextArea txaGenLog = new TTextArea(20, 80);

	private DatabaseNode dbNode;
	private TreeViewNode startNode;

	protected boolean thereWereAlerts;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GenerateDialog(Frame frame)
	{
		super(frame, "", true);

		getContentPane().add(txaGenLog, BorderLayout.CENTER);

		txaGenLog.setEditable(false);
		txaGenLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Generation
	//---
	//---------------------------------------------------------------------------

	public void run(DatabaseNode db, TreeViewNode node)
	{
		thereWereAlerts = false;

		dbNode    = db;
		startNode = node;

		String dbName = dbNode.attrSet.getString("name");

		setTitle("Data Generation for Database : " + dbName);

		//------------------------------------------------------------------------
		//--- check phase

		IntegrityChecker ic = new IntegrityChecker();

		int res = ic.check(dbNode);

		if (res != IntegrityChecker.OK)
		{
			log(Logger.ALERT, "Integrity violation encountered for Database '" + dbName + "':");

			if (res == IntegrityChecker.ERROR_FIELD)
			{
				FieldNode f = ic.getField();
				TableNode t = (TableNode)f.getParent();

				log(Logger.ALERT, "   Table: " + t.attrSet.getString("name"));
				log(Logger.ALERT, "   Field: " + f.attrSet.getString("name"));
			}

			else if (res == IntegrityChecker.ERROR_TABLE)
			{
				TableNode t = ic.getTable();

				log(Logger.ALERT, "   Table: " + t.attrSet.getString("name"));
			}

			else if (res == IntegrityChecker.ERROR_TYPE)
			{
				AbstractType a = ic.getType();
				FieldNode    f = ic.getField();
				TableNode    t = (TableNode)f.getParent();

				log(Logger.ALERT, "   Table: " + t.attrSet.getString("name"));
				log(Logger.ALERT, "   Field: " + f.attrSet.getString("name"));
				log(Logger.ALERT, "   Type : " + a.attrSet.getString("name"));
			}

			log(Logger.ALERT, "   Error: " + ic.getError());
			log(Logger.ALERT, "");
			log(Logger.ALERT, "Please correct this error first.");
		}

		//--- proper generation

		else
		{
			new Thread(this, "generator").start();
		}

		showDialog();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Logger interface
	//---
	//---------------------------------------------------------------------------

	public void log(final int type, final String str)
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				String sType = (type == Logger.INFO) ? "[INFO]  " : "[ALERT] ";

				txaGenLog.append(sType + str + '\n');

				if (type == Logger.ALERT)
					thereWereAlerts = true;
			}
		};

		SwingUtilities.invokeLater(r);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Thread interface
	//---
	//---------------------------------------------------------------------------

	public void run()
	{
		GuiUtil.setWaitCursor(txaGenLog.getTextArea(), true);

		try
		{
			for(Enumeration e=startNode.preorderEnumeration(); e.hasMoreElements();)
			{
				TreeViewNode node = (TreeViewNode) e.nextElement();

				if (node.getUserData() instanceof DataGenModule)
				{
					DataGenModule mod = (DataGenModule) node.getUserData();

					mod.generate(this, dbNode);
				}
			}
		}
		catch(Exception e)
		{
			log(Logger.ALERT, "Raised exception --> " +e);

			StringWriter sw = new StringWriter();
			PrintWriter  pw = new PrintWriter(sw);

			e.printStackTrace(pw);

			log(Logger.ALERT, "Stack is: \n" +sw);
		}
		finally
		{
			GuiUtil.setWaitCursor(txaGenLog.getTextArea(), false);

			if (!thereWereAlerts)
				setVisible(false);
		}
	}

	//---------------------------------------------------------------------------
	/**
	 * Build the header of the generation dialog
	 * @param header - String with the Name of the Generator
	 */

	public void logHeader(String header)
	{
		String lateral = "---";
		String spaces = "   ";
		String generating = "Generating ";
		String separator = Util.replicate("-",
		        header.length() + generating.length() + 2 * (lateral.length() + spaces.length()));

		log(INFO, "");
		log(INFO, separator);
		log(INFO, lateral + spaces + generating + header + spaces + lateral);
		log(INFO, separator);
		log(INFO, "");
	}
}

//==============================================================================
