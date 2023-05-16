//==============================================================================
//===
//===   Cpp
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.cpp;

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

public class Cpp extends AbstractLang implements ModuleOptions
{
	private String      sPreLine;
	private OptionPanel optPanel = new OptionPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "cpp"; }
	public String getVersion()  { return "1.0"; }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates code in C++";
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

	public String  getFormat()        { return "C++"; }
	public boolean isDirectoryBased() { return true;  }
	public boolean hasLargePanel()    { return false; }

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
	//--- C++ Classes Generation
	//---
	//---------------------------------------------------------------------------

	public String getClassCodeInt(Logger logger, TableNode node)
	{
		String name = node.attrSet.getString("name");

		String fields = getFields(node);
		String vars   = getVars(node.tableVars);

		if (fields.equals("") && vars.equals("")) return "";

		return getHeader(name) + fields + vars + getFooter();
	}

	//---------------------------------------------------------------------------

	private String getFields(TableNode node)
	{
		StringBuffer w = new StringBuffer();

		for (int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode f = (FieldNode)node.getChild(i);

			TypeInfo ti = DataTypeLib.getTypeInfo(f);

			if (ti != null && ti.size != null)
			{
				try
				{
					Integer.parseInt(ti.size);

					String name = f.attrSet.getString("name").toUpperCase() + "_SIZE";
					w.append("      static const int ");

					w.append(Util.pad(name, 20)).append(" = ").append(ti.size).append(";").append(LF);
				}
				catch(NumberFormatException e)
				{
					//--- we arrive here if the size is something like '10,3'
					//--- in this case we don't generate the entry
				}
			}
		}
		return w.toString();
	}

	//---------------------------------------------------------------------------

	private String getVars(TableVars tv)
	{
		String w = "";

		if (tv.size() != 0) w += LF;

		for(int i=0; i<tv.size(); i++)
		{
			AttribSet as = tv.get(i);

			String name  = as.getString("name");
			String type  = as.getString("type");
			String value = as.getString("value");
			String descr = as.getString("descr");

			if (!descr.equals(""))
			{
				w += LF;
				w += "      //--- " + descr + LF;
			}

			String line = "      static const ";

			if (type.equals(TableVars.BOOL))   line += "bool     ";
			if (type.equals(TableVars.INT))    line += "int      ";
			if (type.equals(TableVars.LONG))   line += "long int ";
			if (type.equals(TableVars.FLOAT))  line += "float    ";
			if (type.equals(TableVars.DOUBLE)) line += "double   ";

			if (type.equals(TableVars.CHAR))
			{
				line += "char    ";

				if (!value.equals(""))
				{
					if (!value.startsWith("'")) value = "'" + value;
					if (!value.endsWith("'"))   value = value + "'";
				}
			}

			if (type.equals(TableVars.STRING))
			{
				line += "char ";
				name += "[]";

				if (!value.equals(""))
				{
					if (!value.startsWith("\"")) value = "\"" + value;
					if (!value.endsWith("\""))   value = value + "\"";
				}
			}

			if (value.equals(""))
				w += Util.pad(line + name, 43) + ";" + LF;
			else
				w += Util.pad(line + name, 43) + " = " + value + ";" + LF;
		}

		return w;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Abstract methods implementation
	//---
	//---------------------------------------------------------------------------

	protected String getMessage() { return "C++ Classes"; }

	//---------------------------------------------------------------------------

	protected String  getExtension()    { return "cpp"; }
	protected boolean isClassOriented() { return true;  }

	//---------------------------------------------------------------------------

	private String getHeader(String name)
	{
		String w = "";

		w += "//" + getSeparator() + LF;
		w += "//===   " + name + ".cpp                        " + sBuild + LF;
		w += "//" + getSeparator() + LF;
		w += LF;

		if (!sPreLine.equals(""))
		{
			w += sPreLine + LF;
			w += LF;
		}

		w += "//" + getSeparator() + LF;
		w += LF;
		w += "class " + name + LF;
		w += "{" + LF;
		w += "   public:" + LF;

		return w;
	}

	//---------------------------------------------------------------------------

	protected String getHeader() { return null; }

	//---------------------------------------------------------------------------

	protected String getFooter()
	{
		String w = "";

		w += "};" + LF;
		w += LF;
		w += "//" + getSeparator() + LF;

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
