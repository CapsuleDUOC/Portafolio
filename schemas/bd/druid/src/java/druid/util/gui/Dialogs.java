//==============================================================================
//===
//===   Dialogs
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

//==============================================================================

public class Dialogs
{
	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static void showOperationAborted(Component c, String message)
	{
		JOptionPane.showMessageDialog(c, message,
						"Operation aborted", JOptionPane.WARNING_MESSAGE);
	}

	//---------------------------------------------------------------------------
	//--- old

	public static void showFileNotFound(Component c, String fileName)
	{
		JOptionPane.showMessageDialog(c,
							"File '"+ fileName +"' not found.",
							"File Not Found", JOptionPane.WARNING_MESSAGE);
	}

	//---------------------------------------------------------------------------

	public static void showOpenError(Component c, String fileName, String result)
	{
		JOptionPane.showMessageDialog(c,
							"The following error has occured when opening : " + fileName + "\n" +
							"Error : " + result,
							"Open Error", JOptionPane.WARNING_MESSAGE);
	}

	//---------------------------------------------------------------------------

	public static void showSaveError(Component c, String fileName, String result)
	{
		JOptionPane.showMessageDialog(c,
							"The following error has occured when saving : " + fileName + "\n" +
							"Error : " + result,
							"Save Error", JOptionPane.WARNING_MESSAGE);
	}

	//---------------------------------------------------------------------------

	public static void showException(Exception e)
	{
		if (e instanceof IOException)
			JOptionPane.showMessageDialog(Frame.getFrames()[0],
					"Received an I/O exception:\n" + e.getMessage(),
					"I/O Exception", JOptionPane.WARNING_MESSAGE);
		else if (e instanceof IllegalArgumentException)
			JOptionPane.showMessageDialog(Frame.getFrames()[0],
					"Received an illegal argument exception:\n" + e.getMessage(),
					"Illegal Argument", JOptionPane.WARNING_MESSAGE);
		
		else if (e instanceof SQLException)
			JOptionPane.showMessageDialog(Frame.getFrames()[0], e.getMessage(),
					"Operation aborted", JOptionPane.WARNING_MESSAGE);
		
		else
			JOptionPane.showMessageDialog(Frame.getFrames()[0], e.getMessage(),
						"Operation aborted", JOptionPane.WARNING_MESSAGE);

		dumpExc(e);
	}

	//---------------------------------------------------------------------------

	public static void showModuleNotFound()
	{
		JOptionPane.showMessageDialog(Frame.getFrames()[0],
						"Cannot find the module related to the file extension",
						"Error", JOptionPane.WARNING_MESSAGE);
	}

	//---------------------------------------------------------------------------

	public static int showDataNeedSave(Component c)
	{
		Object[] btns = { "Ok, continue", "Let me save"};

		return JOptionPane.showOptionDialog(c,
						"Data is changed and not saved.\n"+
						"Do you want to continue ?",
						"Warning",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, btns,btns[1]);
	}

	//---------------------------------------------------------------------------

	public static int showDdfError(Component c, SQLException e, long recordNum, String line, String phase)
	{
		Object[] btns = { "Retry", "Skip", "Skip all", "Abort" };

		return JOptionPane.showOptionDialog(c,
						"Received an exception during "+ phase +" on record N. "+ recordNum +":\n"+
						e.getMessage() +"\n"+
						"The DDF line was:\n"+
						line +"\n"+
						"What do you want to do?",
						"Warning",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, btns, btns[1]);
	}

	//---------------------------------------------------------------------------
	
	public static boolean confirm(Component c, String title, String message)
	{
		int response = JOptionPane.showConfirmDialog(c, message, title, JOptionPane.YES_NO_OPTION);
		
		return (response == JOptionPane.YES_OPTION);
	
	}
	
	//---------------------------------------------------------------------------

	public static void showInfo(Component c, String message)
	{
		JOptionPane.showMessageDialog(c, message, "Informative message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static void dumpExc(Exception e)
	{

		System.out.println("---------------------------------------------------------");
		e.printStackTrace();
	}
}

//==============================================================================
