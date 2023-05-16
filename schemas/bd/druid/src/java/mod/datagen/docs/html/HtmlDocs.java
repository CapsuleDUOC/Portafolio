//==============================================================================
//===
//===   HtmlDocs
//===
//===   Copyright (C) by Andrea Carboni, Antonio Gallardo.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.html;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.dlib.tools.IOLib;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlElement;

import druid.core.DocManager;
import druid.core.config.Config;
import druid.core.er.ErScrEntity;
import druid.core.er.ErScrView;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.interfaces.DocsGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.util.Language;

//==============================================================================

public class HtmlDocs implements DocsGenModule, ModuleOptions
{
	public static final String LOCALE_PATH = "/locale/docgen/html";
	public static final String SKIN_PATH   = "/templates/docgen/html";

	//--- private consts

	private static final String HTML_SUFFIX = ".html";
	private static final String PNG_SUFFIX  = ".png";

	private OptionPanel optPanel = new OptionPanel();

	//---------------------------------------------------------------------------
	//---
	//--- DocsGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "html"; }
	public String getVersion()  { return "2.1";  }
	public String getAuthor()   { return "Andrea Carboni, Antonio Gallardo, Bruno Vernay"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return 	"Generates docs in browsable xhtml 1.1 frame format using velocity. " +
					"More skins and languages can be added. The templates located in "+
					"'data/templates' can be easily customized.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE)	return this;
			else					return null;
	}

	//---------------------------------------------------------------------------

	public JComponent getPanel() { return optPanel; }

	//---------------------------------------------------------------------------

	public void refresh(AbstractNode node)
	{
		optPanel.refresh(new Settings(node.modsConfig, this));
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Browsable XHTML"; }
	public boolean isDirectoryBased() { return true;              }
	public boolean hasLargePanel()    { return false;             }

	//---------------------------------------------------------------------------
	//---
	//--- Html Doc Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		Settings s = new Settings(dbNode.modsConfig, this);

		String outDir    = dbNode.modsConfig.getValue(this, "output");
		String skin      = s.getSkin();
		String language  = s.getLanguage();
		int    thumbSize = s.getThumbnails();

		if (!outDir.endsWith("/"))
			outDir += "/";

		l.logHeader("XHTML Documentation");

		String localePath = Config.dir.data + LOCALE_PATH;

		Hashtable htLangStrings = Language.loadLanguage(localePath, language);

		if (htLangStrings == null)
		{
			l.log(Logger.ALERT, "Cannot load language strings from '"+localePath+"'");
			return;
		}

		HtmlParams params = new HtmlParams(l, dbNode, outDir, skin, thumbSize, htLangStrings);

		if (!checkDuplicates(params))     return;

		l.log(Logger.INFO, "Copying files...");

		if (!createDirs(params))          return;
		if (!copyFiles(params))           return;

		l.log(Logger.INFO, "Generating main stuff...");

		if (!createMainStuff(params))     return;

		l.log(Logger.INFO, "Generating folders...");

		if (!createFolderObjects(params)) return;

		l.log(Logger.INFO, "Generating objects...");

		if (!createObjects(params))       return;

		l.log(Logger.INFO, "Generating E/R images...");

		if (!createErImages(params))      return;
		if (!copyDocImages(params))       return;

		l.log(Logger.INFO, "");
		l.log(Logger.INFO, "Done.");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Check for duplicated entity names
	//---
	//---------------------------------------------------------------------------

	private boolean checkDuplicates(HtmlParams params)
	{
		Hashtable htEntities = new Hashtable(100);

		Enumeration e = params.dbNode.preorderEnumeration();

		//--- skip the database node

		e.nextElement();

		while(e.hasMoreElements())
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (node instanceof FieldNode || node instanceof FolderNode)
				continue;

			String name = node.attrSet.getString("name");

			if (htEntities.containsKey(name))
			{
				params.logger.log(Logger.ALERT, "The name '"+name+"' is used for more than");
				params.logger.log(Logger.ALERT, "one entity. Because each entity generates one");
				params.logger.log(Logger.ALERT, "file, this fact creates problems.");
				params.logger.log(Logger.ALERT, "Please, rename one of these entities.");

				return false;
			}

			htEntities.put(name, node);
		}

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create docs dirs into destination directory
	//---
	//---------------------------------------------------------------------------

	private boolean createDirs(HtmlParams params)
	{
		final String dirs[] = { "main", "images/large", "images/er-views", "objects",
										"custom/images", "folders", "er-views" };

		for(int i=0; i<dirs.length; i++)
			new File(params.outputDir + dirs[i]).mkdirs();

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Copy files needed for docs (images, stylesheet ...)
	//---
	//---------------------------------------------------------------------------

	private boolean copyFiles(HtmlParams params)
	{
		//------------------------------------------------------------------------
		//--- copy .gif images

		String imgPath = Config.dir.images + "/";
		String desPath = params.outputDir + "images/";

		final String files[] = {	"table.png",      "view.png",      "procedure.png",
											"function.gif",   "sequence.png",  "folder.png",
											"aliasdt.gif",    "basicdt.gif",   "set.gif",
											"unset.gif",	   "blueball.gif",  "greenball.gif",
											"yellowball.gif", "purpleball.gif"
									  };

		for(int i=0; i<files.length; i++)
			if (!copy(params.logger, imgPath, desPath, files[i]))
				return false;

		//------------------------------------------------------------------------
		// -- copy .png images

		final String large[] = { "info", "alert", "danger" };

		for(int i=0; i<large.length; i++)
		{
			if (!copy(params.logger, imgPath, desPath, large[i] + PNG_SUFFIX))
				return false;

			if (!copy(params.logger, imgPath +"large/", desPath +"large/", large[i] + PNG_SUFFIX))
				return false;
		}

		//------------------------------------------------------------------------
		//--- copy stylesheet.css

		desPath = params.outputDir + "main/";

		if (!copy(params.logger, params.templateDir, desPath, "stylesheet.css"))
			return false;

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create main stuff
	//---
	//---------------------------------------------------------------------------

	private boolean createMainStuff(HtmlParams params)
	{
		final String temp[] = {
				"main-index",  "index",
				"db-index",    "main/index",
				"all-folders", "main/all-folders",
				"all-objects", "main/all-objects",
				"revisions",   "main/revisions",
				"datatypes",   "main/datatypes"
		};

		for(int i=0; i<temp.length; i+=2)
			if (!params.applyTemplate(temp[i] + HTML_SUFFIX, temp[i +1] + HTML_SUFFIX))
				return false;

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create all <foldername>.html files for the objectsFrame
	//---
	//---------------------------------------------------------------------------

	private boolean createFolderObjects(HtmlParams params)
	{
		Vector folders = params.dbNode.getObjects(FolderNode.class);

		for(int i=0; i<folders.size(); i++)
		{
			FolderNode node = (FolderNode)folders.elementAt(i);

			String folderPath = IOLib.getFNString(node.attrSet.getString("name"));

			AbstractNode folderNode = node;

			while ((folderNode = (AbstractNode) folderNode.getParent()) instanceof FolderNode)
				folderPath = IOLib.getFNString(folderNode.attrSet.getString("name"))+ '_' + folderPath;

			String file = folderPath + HTML_SUFFIX;

			if (!params.applyTemplate("folder-content.html", "folders/"+ file, "folder", node))
				return false;
		}

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create the object files "objects/<name>.html"
	//---
	//---------------------------------------------------------------------------

	private boolean createObjects(HtmlParams params)
	{
		final String fileName [] = { "table", "view", "procedure", "function", "sequence", "notes" };

		for(Enumeration e=params.dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			String file = "objects/" + IOLib.getFNString(node.attrSet.getString("name")) + HTML_SUFFIX;

			int i=-1;

			     if (node instanceof TableNode)     i = 0;
			else if (node instanceof ViewNode)      i = 1;
			else if (node instanceof ProcedureNode) i = 2;
			else if (node instanceof FunctionNode)  i = 3;
			else if (node instanceof SequenceNode)  i = 4;
			else if (node instanceof NotesNode)     i = 5;

			if (i != -1)
				if (!params.applyTemplate(fileName[i] + HTML_SUFFIX, file, fileName[i], node))
					return false;
		}

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create er images
	//---
	//---------------------------------------------------------------------------

	private boolean createErImages(HtmlParams params)
	{
		Graphics aux = new BufferedImage(10,10, BufferedImage.TYPE_4BYTE_ABGR).createGraphics();

		for(Enumeration e=params.dbNode.erViews.children(); e.hasMoreElements();)
		{
			ErView erView = (ErView) e.nextElement();

			String fileName = params.outputDir + "images/er-views/"+ getFileName(erView);

			ErScrView     erScrView = new ErScrView(erView, aux);
			BufferedImage erImage   = erScrView.createErImage();
			BufferedImage bimg      = createThumbnail(params.thumbSize, erScrView, erImage);

			if (!saveImage(params.logger, fileName +"_small.png", bimg))
				return false;

			if (!saveImage(params.logger, fileName + PNG_SUFFIX, erImage))
				return false;

			fileName = "er-views/" + getFileName(erView) + HTML_SUFFIX;

			if (!params.applyTemplate("er-view.html", fileName, "erView", erView))
				return false;

			for(int i=0; i<erView.getChildCount(); i++)
			{
				ErEntity erEnt = (ErEntity) erView.getChild(i);

				//--- write entity html page

				fileName = "er-views/" + getFileName(erView) +"_"+ getFileName(erEnt) + HTML_SUFFIX;

				if (!params.applyTemplate("er-entity.html", fileName, "erEntity", erEnt))
					return false;

				//--- write entity image

				fileName = params.outputDir + "images/er-views/" + getFileName(erView) +"_"+ getFileName(erEnt) + PNG_SUFFIX;

				ErScrEntity erScrEnt = erScrView.getObjectFromEntity(erEnt);

				if (!saveImage(params.logger, fileName, erScrEnt.getImage()))
					return false;
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------

	private BufferedImage createThumbnail(int thumbSize, ErScrView erScrView, BufferedImage erImage)
	{
		Dimension erSize = erScrView.getErSize();

		int width  = thumbSize;
		int height = thumbSize * erSize.height / erSize.width;

		Image img = erImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = bimg.createGraphics();

		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Copy images referenced inside docs to <destination>/custom/images directory
	//---
	//---------------------------------------------------------------------------

	private boolean copyDocImages(HtmlParams params)
	{
		for(Enumeration e = params.dbNode.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			if (!handleImages(params, node.xmlDoc))
				return false;

			//--- handle database

			if (node instanceof DatabaseNode)
			{
				DatabaseNode db = (DatabaseNode) node;

				copyDocImages(params, db.dataTypes);
				copyDocImages(params, db.erViews);
			}

			//--- handle tables

			else if (node instanceof TableNode)
			{
				TableNode t = (TableNode) node;

				copyDocImages(params, t.triggers);
				copyDocImages(params, t.rules);
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------

	private boolean copyDocImages(HtmlParams params, AbstractNode node)
	{
		for(Enumeration e = node.preorderEnumeration(); e.hasMoreElements();)
		{
			AbstractNode child = (AbstractNode) e.nextElement();

			if (!handleImages(params, child.xmlDoc))
				return false;
		}

		return true;
	}

	//---------------------------------------------------------------------------

	private boolean handleImages(HtmlParams params, XmlElement docs)
	{
		java.util.List list = docs.preorderEnum();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement el = (XmlElement) list.get(i);

			if (el.getName().equals(DocManager.IMAGE))
			{
				XmlAttribute attr = el.getAttribute(DocManager.ATTR_IMAGE_SRC);

				String source = attr.getValue().substring(5);
				String destin = params.outputDir + "custom/images/" + new File(source).getName();

				if (!IOLib.copy(source, destin))
				{
					params.logger.log(Logger.ALERT, "Cannot copy image '"+source+"' to '"+destin+"'");
					return false;
				}
			}
		}

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- General private methods
	//---
	//---------------------------------------------------------------------------

/*	private boolean saveTextFile(Logger l, String fileName, String text)
	{
		boolean ok = IOLib.saveTextFile(fileName, text);

		if (!ok)
			l.log(Logger.ALERT, "Cannot create file '"+ fileName +"'");

		return ok;
	}*/

	//---------------------------------------------------------------------------

	private boolean copy(Logger l, String srcPath, String desPath, String file)
	{
		return copy(l, srcPath, desPath, file, file);
	}

	//---------------------------------------------------------------------------

	private boolean copy(Logger l, String srcPath, String desPath, String source, String destin)
	{
		if (!IOLib.copy(srcPath + source, desPath + destin))
		{
			l.log(Logger.ALERT, "Cannot copy file '"+ srcPath + source +"' to '"+ desPath + destin +"'");
			return false;
		}
		return true;
	}

	//---------------------------------------------------------------------------

	private boolean saveImage(Logger l, String fileName, BufferedImage img)
	{
		try
		{
			boolean result = ImageIO.write(img, "png", new File(fileName));

			if (!result)
				l.log(Logger.ALERT, "The 'png' ImageWriter doesn't exist");

			return result;
		}
		catch(IOException e)
		{
			l.log(Logger.ALERT, "Cannot create image '"+ fileName +"'");

			return false;
		}
	}

	//---------------------------------------------------------------------------

	private String getFileName(AbstractNode node)
	{
		return IOLib.getFNString(node.attrSet.getString("name"));
	}
}

//==============================================================================
