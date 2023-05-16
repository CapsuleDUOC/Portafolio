//==============================================================================
//===
//===   DocbookDocs
//===
//===   Copyright (C) by Andrea Carboni & Bruno Vernay.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.docbook;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;

import org.dlib.tools.IOLib;
import org.dlib.xml.XmlUtil;

import druid.core.config.Config;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.NotesNode;
import druid.interfaces.DocsGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.util.Language;

//==============================================================================

public class DocbookDocs implements DocsGenModule, ModuleOptions
{
	public static final String LANGUAGE   = "language";
	public static final String SKIN       = "skin";
	public static final String LOCALE_PATH = "/locale/docgen/html";

	private OptionPanel optPanel = new OptionPanel();

	//---------------------------------------------------------------------------
	//---
	//--- DocsGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "Docbook"; }
	public String getVersion()  { return "1.0";  }
	public String getAuthor()   { return "Bruno Vernay"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return 	"Generates docs in docbook xml format using velocity. " +
				 "The file can then be used with an XSL stylesheet";
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
        optPanel.refresh(new SettingsDbk(node.modsConfig, this));
	}

	//---------------------------------------------------------------------------

	public String  getFormat()                { return "Docbook XML";   }
	public boolean isDirectoryBased()         { return false;           }
	public boolean hasLargePanel()            { return false;           }

	//---------------------------------------------------------------------------
	//---
	//--- Docbook Doc Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		SettingsDbk s = new SettingsDbk(dbNode.modsConfig, this);

		String output     = dbNode.modsConfig.getValue(this, "output").replace('\\','/');
		String skin       = s.getSkin();
		String language   = s.getLanguage();

		String outDir  = output.substring( 0, output.lastIndexOf( "/") +1);
		String outFile = output.substring( output.lastIndexOf( "/") +1, output.length());

		l.log(Logger.INFO, "");
		l.log(Logger.INFO, "----------------------------------------");
		l.log(Logger.INFO, "--- Generating Docbook Documentation ---");
		l.log(Logger.INFO, "----------------------------------------");
        //l.log(Logger.INFO, "outDir : " + outDir);
        //l.log(Logger.INFO, "outFile : " + outFile);

		String localePath = Config.dir.data + LOCALE_PATH;

		Hashtable htLangStrings = Language.loadLanguage(localePath, language);

		if (htLangStrings == null)
		{
			l.log(Logger.ALERT, "Cannot load language strings from '"+localePath+"'");
			return;
		}

		DocbookParams params = new DocbookParams(l, dbNode, outDir, skin, htLangStrings);

		if (!checkDuplicates(params))          return;
		if (!copyFiles(params))                return;
		if (!createMainStuff(params, outFile)) return;

		l.log(Logger.INFO, "");
		l.log(Logger.INFO, "Done.");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Check for duplicated entity names
	//---
	//---------------------------------------------------------------------------

	private boolean checkDuplicates(DocbookParams params)
	{
		Hashtable htEntities = new Hashtable(100);

		Enumeration e = params.dbNode.preorderEnumeration();

		//--- skip the database node

		e.nextElement();

		while(e.hasMoreElements())
		{
			AbstractNode node = (AbstractNode) e.nextElement();

			String oName = node.attrSet.getString("name");
            String nccName = XmlUtil.makeNCNameChar( oName);
            String name = nccName;

            if (!(oName.equals(nccName))) {
                params.logger.log(Logger.ALERT, "The name '"+oName+"' has been changed to ");
                params.logger.log(Logger.ALERT, "'"+nccName+"' in the references id of the document.\n");
            }

			if (node instanceof FolderNode)
            {
                AbstractNode folderNode = node;
                while ((folderNode = (AbstractNode) folderNode.getParent()) instanceof FolderNode)
                {
                    name = XmlUtil.makeNCNameChar( folderNode.attrSet.getString("name")) + '.' + name;
                }
                name = "folder_" + name;
            }
            if (node instanceof FieldNode)
            {
                AbstractNode tableNode = (AbstractNode) node.getParent();
                String tName = tableNode.attrSet.getString("name");
                name = "field_" + XmlUtil.makeNCNameChar( tName) + '.' + nccName;
            }
            if (node instanceof NotesNode)
            {
                name = "note_" + nccName;
            }

			if (htEntities.containsKey(name))
			{
				params.logger.log(Logger.ALERT, "The name '"+name+"' is used for more than");
				params.logger.log(Logger.ALERT, "one entity. Because each entity generates one");
				params.logger.log(Logger.ALERT, "IdRef, this fact creates problems.");
				params.logger.log(Logger.ALERT, "Please, rename one of these entities.\n");
				return false;
			}

			htEntities.put(name, node);
		}

		return true;
	}


	//---------------------------------------------------------------------------
	//---
	//--- Copy files needed for docs (images, stylesheet ...)
	//---
	//---------------------------------------------------------------------------

	private boolean copyFiles(DocbookParams params)
	{
		//------------------------------------------------------------------------
		//--- copy stylesheet.css

		String desPath = params.outputDir;

		if (!copy(params.logger, params.templateDir, desPath, "stylesheet.css"))
			return false;

		return true;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create main stuff
	//---
	//---------------------------------------------------------------------------

	private boolean createMainStuff(DocbookParams params, String outFile)
	{

        if (!params.applyTemplate("docbook.tpl", outFile))
				return false;

		return true;
	}



	//---------------------------------------------------------------------------
	//---
	//--- General private methods
	//---
	//---------------------------------------------------------------------------

	/*private boolean saveTextFile(Logger l, String fileName, String text)
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

/*	private String getFileName(AbstractNode node)
	{
		return IOLib.getFNString(node.attrSet.getString("name"));
	}*/
}

//==============================================================================
