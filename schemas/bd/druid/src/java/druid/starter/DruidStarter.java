//==============================================================================
//===
//===   DruidStarter
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.starter;

import java.io.File;

import javax.swing.Timer;

import org.dlib.gui.SplashScreen;

import druid.boot.Starter;
import druid.core.DataTracker;
import druid.core.Supervisor;
import druid.core.cli.Cli;
import druid.core.config.Config;
import druid.core.er.ErThemeManager;
import druid.core.modules.ModuleManager;
import druid.dialogs.tipoftheday.TipOfTheDay;
import druid.panels.MainFrame;
import druid.util.gui.ImageFactory;

//==============================================================================

public class DruidStarter implements Starter
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DruidStarter() {}

	//---------------------------------------------------------------------------
	//---
	//--- Start method
	//---
	//---------------------------------------------------------------------------

	public void start(String[] args, String progPath)
	{
		//------------------------------------------------------------------------
		//--- load config file and setup configuration

		Config.init(progPath);

		//------------------------------------------------------------------------
		//--- load images. We must load images here because they can be used
		//--- by some modules (their gui panels)

		ImageFactory.init(Config.dir.images);

		//------------------------------------------------------------------------
		//--- setup module manager & theme manager

		ModuleManager.init(Config.dir.modules);

		ErThemeManager.init(Config.dir.data);
		
		//------------------------------------------------------------------------
		//--- check to see if the cli must be executed

		String projToLoad = null;

		if (args.length == 1 && args[0].startsWith("-proj:"))
			projToLoad = args[0].substring(6);

		if (args.length != 0 && projToLoad == null)
			Cli.start(args);

		//------------------------------------------------------------------------
		//--- show splash-screen and startup system

		SplashScreen splashScr = new SplashScreen(Config.dir.images + "/logo.png");

		//------------------------------------------------------------------------
		//--- create the druid window

//		try
//		{
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//		}
//		catch(Exception e) {}

		MainFrame mainFrame = new MainFrame();

		//------------------------------------------------------------------------
		//--- show main window

		DataTracker.init(mainFrame);
		mainFrame.proj_new();

		mainFrame.setSize(Config.general.window.width, Config.general.window.height);
		mainFrame.setIconImage(ImageFactory.ICON.getImage());
		mainFrame.setVisible(true);

		splashScr.dispose();

		new Timer(Supervisor.DELAY, new Supervisor()).start();

		//------------------------------------------------------------------------
		//--- show tip of the day (if the case)

		if (Config.general.showTip)
			new TipOfTheDay(mainFrame);

		//------------------------------------------------------------------------
		//--- reload last project (if the case)

		if (projToLoad != null)
			mainFrame.do_open(new File(projToLoad));

		else if (Config.general.reloadLastProj && Config.recentFiles.getFileCount() != 0)
		{
			String lastProj = Config.recentFiles.getFileAt(0);

			mainFrame.do_open(new File(lastProj));
		}
	}
}

//==============================================================================
