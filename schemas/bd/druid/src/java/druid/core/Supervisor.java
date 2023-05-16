//==============================================================================
//===
//===   Supervisor
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import druid.util.gui.ImageFactory;
import druid.util.gui.StatusBar;

//==============================================================================

public class Supervisor implements ActionListener
{
	public static final int DELAY = 1000;

	private static final Color YELLOW = new Color(160, 80, 0);

	private static final int SERIAL_DELTA    = 1000;
	private static final int COUNTDOWN_START =   20000/DELAY;
	private static final int COUNTDOWN_STOP  =    4000/DELAY;

	//---------------------------------------------------------------------------

	private int     lastSerial = Serials.lastSerial;
	private boolean firstCheck = true;

	//--- message queue variables

	private int countDown[] = new int[StatusBar.NUM_ICONS];

	private Vector messages = new Vector();

	//---------------------------------------------------------------------------
	//---
	//--- Supervisor
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		updateMemorySlot();
		checkSerials();
		handleMessageQueue();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Memory status
	//---
	//---------------------------------------------------------------------------

	private void updateMemorySlot()
	{
		Runtime rt = Runtime.getRuntime();

		double free  = rt.freeMemory();
		double total = rt.totalMemory();
		double max  = rt.maxMemory();

		double used = total - free;

		String sUsed = getMemoryString(used);
		String sMax  = getMemoryString(max);

		final String memory =  sUsed +" / "+ sMax;

		final String toolTip= "<HTML>"+
									"Used Memory : <B>"+ sUsed +"</B><BR>"+
									"Total Memory: <B>"+ sMax  +"</B>";

		Color col = Color.BLACK;

		double ratio = used / max;

		if (ratio >= 0.90) col = YELLOW;
		if (ratio >= 0.98) col = Color.RED;

		//--- update status bar

		StatusBar.getInstance().displayMemory(memory, toolTip, col);
	}

	//---------------------------------------------------------------------------

	private String getMemoryString(double memory)
	{
		if (memory < 1024)
			return memory + " bytes";

		memory = memory / 1024;

		if (memory < 1024)
			return ((double)((int)(memory*10)))/10 + " KB";

		memory = memory / 1024;

		if (memory < 1024)
			return ((double)((int)(memory*10)))/10 + " MB";

		memory = memory / 1024;

		return ((double)((int)(memory*10)))/10 + " GB";
	}

	//---------------------------------------------------------------------------
	//---
	//--- Serials status
	//---
	//---------------------------------------------------------------------------

	private void checkSerials()
	{
		if (lastSerial > Serials.lastSerial)
		{
			lastSerial = Serials.lastSerial;
			addInfo("<HTML>Serial set to : <B>"+ lastSerial +"</B>");
		}
		else if (Serials.lastSerial - lastSerial > SERIAL_DELTA)
		{
			lastSerial = Serials.lastSerial;
			addInfo("<HTML>Serial increased to : <B>"+ lastSerial +"</B>");
		}

		if (Serials.lastSerial < 0 && firstCheck)
		{
			firstCheck = false;

			addAlert("<HTML>Serial overflow : " +Serials.lastSerial+
						"<BR>Remapping should be performed.");
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Message queue handling
	//---
	//---------------------------------------------------------------------------

	private void handleMessageQueue()
	{
		//--- handle countdowns

		for(int i=0; i<StatusBar.NUM_ICONS; i++)
		{
			if (countDown[i] != 0)
			{
				countDown[i]--;

				if (countDown[i] == COUNTDOWN_STOP)
					hideMessage(i);
			}
		}

		//--- check if we have to show some messages

		if (messages.size() != 0)
		{
			int freeIcon = -1;

			//--- look for a free icon slot

			for(int i=0; i<StatusBar.NUM_ICONS; i++)
				if (countDown[i] == 0)
				{
					freeIcon = i;
					break;
				}

			//--- if there is a free slot then show the message

			if (freeIcon != -1)
			{
				MsgInfo mi = (MsgInfo) messages.get(0);
				messages.removeElementAt(0);
				showMessage(freeIcon, mi);

				countDown[freeIcon] = COUNTDOWN_START;
			}
		}
	}

	//---------------------------------------------------------------------------

	private void showMessage(int slot, MsgInfo mi)
	{
		StatusBar.getInstance().showImage(slot, mi.getImage(), mi.getMessage());
	}

	//---------------------------------------------------------------------------

	private void hideMessage(int slot)
	{
		StatusBar.getInstance().hideImage(slot);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Information management
	//---
	//---------------------------------------------------------------------------

	private void addInfo(String text)
	{
		messages.add(MsgInfo.createInfoMsg(text));
	}

	//---------------------------------------------------------------------------

	private void addAlert(String text)
	{
		messages.add(MsgInfo.createAlertMsg(text));
	}

	//---------------------------------------------------------------------------

/*	private void addDanger(String text)
	{
		messages.add(MsgInfo.createDangerMsg(text));
	}*/
}

//==============================================================================

class MsgInfo
{
	private static final int INFO   = 0;
	private static final int ALERT  = 1;
	private static final int DANGER = 2;

	//---------------------------------------------------------------------------

	private int    type;
	private String message;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public MsgInfo(int type, String message)
	{
		this.type    = type;
		this.message = message;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String getMessage()
	{
		return message;
	}

	//---------------------------------------------------------------------------

	public Image getImage()
	{
		if (type == INFO)  return ImageFactory.INFO2.getImage();
		if (type == ALERT) return ImageFactory.ALERT2.getImage();

		return ImageFactory.DANGER2.getImage();
	}

	//---------------------------------------------------------------------------

	public static MsgInfo createInfoMsg(String message)
	{
		return new MsgInfo(INFO, message);
	}

	//---------------------------------------------------------------------------

	public static MsgInfo createAlertMsg(String message)
	{
		return new MsgInfo(ALERT, message);
	}

	//---------------------------------------------------------------------------

	public static MsgInfo createDangerMsg(String message)
	{
		return new MsgInfo(DANGER, message);
	}
}

//==============================================================================
