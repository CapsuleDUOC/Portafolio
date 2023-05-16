//==============================================================================
//===
//===   AnsiC
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.ansic;

import javax.swing.JComponent;

import org.dlib.tools.Util;

import druid.core.AttribSet;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.TableVars;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import factory.code.AbstractLang;

//==============================================================================

public class AnsiC extends AbstractLang implements ModuleOptions
{

	private String      sPreLine;
	private OptionPanel optPanel = new OptionPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()         { return "c";   }
	public String getVersion()    { return "1.0"; }
	public String getAuthor()     { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates code in Ansi C";
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

	public String  getFormat()        { return "Ansi C"; }
	public boolean isDirectoryBased() { return false;    }
	public boolean hasLargePanel()    { return false;    }

	//---------------------------------------------------------------------------
	//---
	//--- Setup
	//---
	//---------------------------------------------------------------------------

	protected void setup(DatabaseNode dbNode)
	{
		super.setup(dbNode);

		sOutput  = dbNode.modsConfig.getValue(this, "output");
		sPreLine = new Settings(dbNode.modsConfig, this).getPreLine();
	}

	//---------------------------------------------------------------------------
	//---
	//--- C Code Generation
	//---
	//---------------------------------------------------------------------------

	protected String getClassCodeInt(Logger logger, TableNode tableNode)
	{
		String tableName = tableNode.attrSet.getString("name");

		StringBuffer w = new StringBuffer();
        w.append("/* ===   ").append(Util.pad(tableName,15));
		w.append(" ======================================================== */").append(LF).append(LF);

		//--- write all table constants ---

		for (int i = 0; i < tableNode.getChildCount(); i++)
		{
			FieldNode f = (FieldNode)tableNode.getChild(i);
			w.append(getField(f, tableName));
		}

		//--- write eventual extra java code ---

		w.append(getVars(tableName, tableNode));
		w.append(LF);

		return w.toString();
	}

	//---------------------------------------------------------------------------

	private String getField(FieldNode f, String tableName)
	{
		String w = "";

		TypeInfo ti = DataTypeLib.getTypeInfo(f);

		//--- ti cannot be null due to the integrity check
		if (ti != null && ti.size != null)
		{
			String fName = f.attrSet.getString("name");

			String name = tableName.toUpperCase() + "_"+ fName.toUpperCase() + "_SIZE";
			w += "#define ";

			w += Util.pad(name, 30) + " " + ti.size + LF;
		}

		return w;
	}

	//---------------------------------------------------------------------------

	private String getVars(String tableName, TableNode node)
	{
		TableVars tv = node.tableVars;

		StringBuffer w = new StringBuffer();

		for (int i = 0; i < tv.size(); i++)
		{
			AttribSet as = tv.get(i);

			String name  = as.getString("name");
			String type  = as.getString("type");
			StringBuffer value = new StringBuffer(as.getString("value"));
			String descr = as.getString("descr");

			if (descr.length() > 0)
			{
				w.append(LF);
				w.append("/*** ").append(descr).append(" ***/").append(LF);
			}

			StringBuffer line = new StringBuffer("#define ");

			if (type.equals(TableVars.STRING) && value.length() > 0)
			{
				if (!value.toString().startsWith("\"")) value.insert(0, "\"");
				if (!value.toString().endsWith("\""))   value.append("\"");
			}

			line.append(tableName.toUpperCase()).append("_").append(name.toUpperCase());

			w.append(Util.pad(line.toString(), 38)).append(" ").append(value).append(LF);
		}

		return w.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Abstract methods implementation
	//---
	//---------------------------------------------------------------------------

	protected String getMessage() { return "C Code"; }

	//---------------------------------------------------------------------------

	protected String  getExtension()    { return "c"; }
	protected boolean isClassOriented() { return false; }

	//---------------------------------------------------------------------------

	protected String getHeader()
	{
		String w = "";

		w += "/* " + getSeparator() + " */" + LF;
		w += "/* ===   " + sOutput + "                        " + sBuild + " */" + LF;
		w += "/* " + getSeparator() + " */" + LF;
		w += LF;

		if (!sPreLine.equals(""))
		{
			w += sPreLine + LF;
			w += LF;
		}

		return w;
	}

	//---------------------------------------------------------------------------

	protected String getFooter()
	{
		String w = "";

		w += LF;
		w += "/* " + getSeparator() + " */" + LF;

		return w;
	}

	//---------------------------------------------------------------------------
	/**
	  * @see mod.datagen.code.java.Java#getClassCodeName(Logger, TableNode)
	  */

	protected String getClassCodeName(TableNode node)
	{
		String className  = node.attrSet.getString("name");
		return className;
	}
}

//==============================================================================
