//==============================================================================
//===
//===   Java
//===
//===   Copyright (C) by Andrea Carboni & Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.code.java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.swing.JComponent;

import mod.datagen.code.java.panels.OptionPanel;

import org.dlib.tools.Util;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlCodec;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlWriter;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.TableVars;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.util.JavaUtil;
import factory.code.AbstractLang;
import factory.sql.FKeyEntry;
import factory.sql.SqlUtil;

//==============================================================================

public class Java extends AbstractLang implements ModuleOptions
{
	private Settings sett;

	private OptionPanel optPanel = new OptionPanel();

	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "java"; }
	public String getVersion()  { return "1.4";  }
	public String getAuthor()   { return "Andrea Carboni, Misko Hevery, Antonio Gallardo, Mark Heinze"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates code in Java. Supports JDO 1.01.";
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

	public String  getFormat()        { return "Java"; }
	public boolean isDirectoryBased() { return true;   }
	public boolean hasLargePanel()    { return true;   }

	//---------------------------------------------------------------------------
	//---
	//--- Setup
	//---
	//---------------------------------------------------------------------------

	protected void setup(DatabaseNode dbNode)
	{
		super.setup(dbNode);

		sOutput = dbNode.modsConfig.getValue(this, "output");
		sett    = new Settings(dbNode.modsConfig, this);

		// Hack to allow the *.jdo file generation
		buildJdoFile(dbNode);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Java Classes Generation
	//---
	//---------------------------------------------------------------------------

	protected String getClassCodeInt(Logger logger, TableNode node)
	{
		String name = node.attrSet.getString("name");

		String consts = getConsts(node);
		String vars   = getVars(node.tableVars, dbNode);
		String jdoData= getJdoData(logger, node);

		if (consts.equals("") && vars.equals("") && jdoData.equals(""))
			return "";

		return getHeader(name, node) + consts + vars + jdoData + getFooter();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private String getConsts(TableNode node)
	{
		StringBuffer w = new StringBuffer();

		if (sett.isGenNames())
			for (int i = 0; i < node.getChildCount(); i++)
			{
				FieldNode f = (FieldNode)node.getChild(i);
				String name = f.attrSet.getString("name");

				//--- put comment (if any)
				String comment = f.attrSet.getString("comment").trim();

				if (comment.length() > 0)
					w.append("   /** ").append(comment).append(" */").append(LF);

				//--- put field name as constant
				w.append("   public static final String ");
				w .append(Util.pad(name.toUpperCase(), 17)).append(" = \"").append(name).append("\";").append(LF);
			}

		w.append(getSmallSeparator());

		if (sett.isGenConsts())
			for( int i = 0; i < node.getChildCount(); i++)
			{
				FieldNode f = (FieldNode)node.getChild(i);
				TypeInfo ti = DataTypeLib.getTypeInfo(f);

				//--- put field size (if the case)
				if (ti != null && ti.size != null)
				{
					try
					{
						Integer.parseInt(ti.size);

						String name = f.attrSet.getString("name").toUpperCase() + "_SIZE";
						w.append("   public static final int ");
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

	private String getVars(TableVars tv, DatabaseNode dbNode)
	{
		if (tv.size() == 0) return "";

		String w = getSmallSeparator();

		for (int i = 0; i < tv.size(); i++)
		{
			AttribSet as = tv.get(i);

			String name  = as.getString("name");
			String type  = as.getString("type");
			String value = as.getString("value");
			String descr = as.getString("descr");

			if (!descr.equals(""))
			{
				w += LF;
				w += "   //--- " + descr + LF;
			}

			String line = "   public static final ";

			if (type.equals(TableVars.BOOL))   line += "boolean ";
			if (type.equals(TableVars.INT))    line += "int     ";
			if (type.equals(TableVars.LONG))   line += "long    ";
			if (type.equals(TableVars.FLOAT))  line += "float   ";
			if (type.equals(TableVars.DOUBLE)) line += "double  ";

			if (type.equals(TableVars.CHAR))
			{
				line += "char    ";

				if (!value.equals(""))
					value = fillBounds(value, "'");
			}

			if (type.equals(TableVars.STRING))
			{
				line += "String  ";

				if (!value.equals(""))
					value = fillBounds(value, "\"");
			}

			if (value.equals(""))
				w += Util.pad(line + name, 47) + ";" + LF;
			else
				w += Util.pad(line + name, 47) + " = " + dbNode.substVars(value) + ";" + LF;
		}

		return w;
	}

	//---------------------------------------------------------------------------
	//---
	//--- JDO methods
	//---
	//---------------------------------------------------------------------------

	private String getJdoData(Logger logger, TableNode node)
	{
		if (!sett.isUsingJDO()) return "";

		String fields     = getJdoFields(logger, node);
		String getset     = getGetterSetter(node);
		String arrays     = getArrays(node);
		String references = getJdoReferences(node);

		return fields + getset + arrays + references;
	}

	//---------------------------------------------------------------------------

	private String getJdoFields(Logger logger, TableNode node)
	{
		StringBuffer w = new StringBuffer();

		for (int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);
			TypeInfo  ti    = DataTypeLib.getTypeInfo(field);
			String    type  = ti.ddEquiv;

			if (type.equals(""))
				logger.log(Logger.ALERT, "Warning : DDEquiv is empty for type --> " + ti.name);

			if (i == 0)
				w.append(getSmallSeparator());

			w	.append("   ")
				.append(getAccess(sett.getAccess()))
				.append(" ")
				.append(Util.pad(type, 10))
				.append(" ")
            .append(convertToCamelCase(JavaUtil.lcJavaName(field.attrSet.getString("name"))))
				.append(";")
				.append(LF);
		}

		return w.toString();
	}

	//---------------------------------------------------------------------------

	private String getGetterSetter(TableNode node)
	{
		if (!sett.isGetter() && !sett.isSetter())
			return "";

		String w = "";

		for (int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);
			TypeInfo ti = DataTypeLib.getTypeInfo(field);
			String name = field.attrSet.getString("name");
			String type = ti.ddEquiv;

			w += getSmallSeparator();

			//---------------------------------------------------------------------

			String javaMethodName    = JavaUtil.ucJavaName(name);
			String javaAttributeName = convertToCamelCase(JavaUtil.lcJavaName(name));

			if(sett.isGetter())
			{
				String prefix = "boolean".equals(type) ? "is" : "get";

				w += "   public "+ type +" "+ prefix + javaMethodName +"() { return "+ javaAttributeName +"; }" + LF;
				w += LF;
			}

			//---------------------------------------------------------------------

			if(sett.isSetter())
			{
				w += "   public void set" + javaMethodName + "(" + type + " " + javaAttributeName + ")" + LF;
				w += "   {" +LF;
				if (sett.isDirtyCheck())
					w += "      checkDirty(this."+ javaAttributeName +", "+ javaAttributeName +");" + LF;
				w += "      this."+ javaAttributeName +" = "+ javaAttributeName +";" + LF;
				w += "   }" + LF;
			}
		}

		return w;
	}

	//---------------------------------------------------------------------------

	private String getArrays(TableNode node)
	{
		if (!sett.isGenFKArray())
			return "";

		//--- get references to given table

		Vector refs = DataLib.getReferences(node, false);

		if (refs.size() == 0)
			return "";

		//--- generate jdo data

		StringBuffer w = new StringBuffer();

		String tableName  = node.attrSet.getString("name");

		for (int i = 0; i < refs.size(); i++)
		{
			TableNode tnRef = (TableNode) refs.elementAt(i);
            Vector foreignkeyVector = SqlUtil.getForeignKeys(tnRef, tableName);
            boolean useForeingKeyAsName = foreignkeyVector.size() > 1;
            for (int j=0; j<foreignkeyVector.size(); j++)
            {
                FKeyEntry fk = ((FKeyEntry)foreignkeyVector.elementAt(j));
                String tableNameRef = tnRef.attrSet.getString("name");
                String lcName = JavaUtil.lcJavaName(tableNameRef);
                String ucName = JavaUtil.ucJavaName(tableNameRef);
                String ucClassNameRef = JavaUtil.ucJavaName(tableNameRef);
                String lcClassNameRef = JavaUtil.lcJavaName(tableNameRef);
                if (useForeingKeyAsName)
                {
                    lcName =  JavaUtil.lcJavaName((String)fk.vFields.elementAt(0)) + ucClassNameRef;
                    ucName =  JavaUtil.ucJavaName((String)fk.vFields.elementAt(0)) + ucClassNameRef;
                }

                w.append(getSmallSeparator());
                w.append("   " + getAccess(sett.getAccess()) + " Collection "+lcName+"List = new ArrayList();" +LF);
                w.append(LF);

                if (sett.isGetter())
                {
                    w.append("   public Collection get"+ucName+"List() { return "+lcName+"List;}" +LF);
                    w.append(LF);
                }

                if (sett.isSetter())
                {
                    w.append("   public void set"+ucName+"List(Collection c) { this." + lcName+"List = c;}" +LF);
                    w.append(LF);
                }

                w.append("   public void add"+ucName+"( "+ sett.getNamePrefix()+ucClassNameRef+sett.getNameSuffix() +" " + lcClassNameRef + " )" +LF);
                w.append("   {" +LF);

                if (sett.isFKClassRef())
                    w.append("      " + lcClassNameRef + ".set"+ JavaUtil.ucJavaName((String)fk.vFields.elementAt(0)) +"Ref(this);" +LF);

                w.append("      " + lcName+"List.add( " + lcClassNameRef + " );"+LF);
                w.append("   }" +LF);
            }
		}

		return w.toString();
	}

	//---------------------------------------------------------------------------

	private String getJdoReferences(TableNode node)
	{
        String w = "";
        Vector foreignkeyVector = SqlUtil.getForeignKeys(node);
        for (int i=0; i<foreignkeyVector.size(); i++)
		{
            String ucClassName = JavaUtil.ucJavaName(((FKeyEntry)foreignkeyVector.elementAt(i)).fkTable);
            String lcClassName = JavaUtil.lcJavaName(((FKeyEntry)foreignkeyVector.elementAt(i)).fkTable);
            String name = (String)((FKeyEntry)foreignkeyVector.elementAt(i)).vFields.elementAt(0);
			String lcName = JavaUtil.lcJavaName(name);
			String ucName = JavaUtil.ucJavaName(name);

			w += getSmallSeparator();
			w += "   "+ getAccess(sett.getAccess()) +" "+ ucClassName +" "+ lcName +"Ref;" +LF + LF;

			if (sett.isGetter())
			{
				w += "   public " + ucClassName + " get" + ucName +"Ref() { return "+ lcName +"Ref; }" + LF;
				w += LF;
			}

			if(sett.isSetter())
			{
				w += "   public void set"+ucName +"Ref("+ ucClassName +" "+ lcClassName + ")" + LF;
				w += "   {" +LF;
				if (sett.isDirtyCheck())
					w += "      checkDirty(this."+ lcName +"Ref, "+ lcClassName +");" + LF;
				w += "      this."+ lcName +"Ref = "+ lcClassName +";" + LF;
				w += "   }" + LF + LF;
			}
		}

		return w;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Abstract methods implementation
	//---
	//---------------------------------------------------------------------------

	protected String getMessage() { return "Java Classes"; }

	//---------------------------------------------------------------------------

	protected String  getExtension()    { return "java"; }
	protected boolean isClassOriented() { return true;   }

	//---------------------------------------------------------------------------

	protected String checkOptions()
	{
		if (sett.getPackage().equals(""))
			return 	"Please, specify a java package in the 'code options' field.\n" +
						"Generation aborted.";
		return null;
	}

	//---------------------------------------------------------------------------

	private String getHeader(String name, TableNode node)
	{
		String w = "";

		w += "//" + getSeparator() + LF;
		w += "//===   " + name + ".java                        " + sBuild + LF;
		w += "//" + getSeparator() + LF;
		w += LF;
		w += "package " + dbNode.substVars(sett.getPackage()) + ";" + LF;
		w += LF;
		w += getExtraImports(node);

//		if (sett.isFKClassRef())
//			w += "import " + dbNode.substVars(sett.getPackage()) + ".*;" + LF;

		w += LF;
		w += "//" + getSeparator() + LF;
		w += LF;

		//--- put comment (if any)
		String comment = node.attrSet.getString("comment").trim();

		if (!comment.equals(""))
			w += "/** "+comment+" */" + LF + LF;

		w += "public class " + sett.getNamePrefix() + JavaUtil.ucJavaName(name) + sett.getNameSuffix();

		if (!sett.getExtends().equals(""))
			w += " extends " + sett.getExtends();

		if (sett.isSerializable())
			w += " implements java.io.Serializable";

		w += LF;
		w += "{" + LF;

		return w;
	}

	//---------------------------------------------------------------------------

	protected String getHeader() { return null; }

	//---------------------------------------------------------------------------

	protected String getFooter()
	{
		String w = "}" + LF + LF;
		w += "//" + getSeparator() + LF;
		return w;
	}

	//---------------------------------------------------------------------------

	protected String getClassCodeName(TableNode node)
	{
		String className  = node.attrSet.getString("name");

		return sett.getNamePrefix() +  JavaUtil.ucJavaName(className) + sett.getNameSuffix();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private utility methods
	//---
	//---------------------------------------------------------------------------

	private String getExtraImports(TableNode node)
	{
		StringBuffer w = new StringBuffer();

		for (int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) node.getChild(i);
			TypeInfo  ti    = DataTypeLib.getTypeInfo(field);
			String className = ti.ddEquiv;

			Class clazz = null;
			try
			{
				clazz = Class.forName("java.util." + className);
			}
			catch (ClassNotFoundException e)
			{
				try
				{
					clazz = Class.forName("java.math." + className);
				}
				catch (ClassNotFoundException ex) {}
			}

			if (clazz != null)
			{
				w.append("import ");
				w.append(clazz.getName());
				w.append(";");
				w.append(LF);
			}
		}

		if (!DataLib.getReferences(node, false).isEmpty())
		{
			w.append("import java.util.Collection;");
			w.append(LF);
			w.append("import java.util.ArrayList;");
			w.append(LF);
		}

		return w.toString();
	}

	//---------------------------------------------------------------------------

	private String convertToCamelCase(String str)
	{
		if (!sett.isCamelCase())
			return str;

		if (str.length() < 1)
			return str;

		String[] words = str.split("_");
		StringBuffer javaName = new StringBuffer(str.length());
		javaName.append(words[0]);

		for (int i = 1; i < words.length; i++)
		{
			javaName.append(words[i].substring(0,1).toUpperCase());
			javaName.append(words[i].substring(1));
		}

		return javaName.toString();
	}

	//---------------------------------------------------------------------------

	private String fillBounds(String text, String boundary)
	{
		if (!text.startsWith(boundary)) text = boundary + text;
		if (!text.endsWith(boundary))   text += boundary;

		return text;
	}

	//---------------------------------------------------------------------------

	private String getSmallSeparator()
	{
		return LF + "   //" + Util.replicate("-", 75) + LF + LF;
	}

	//---------------------------------------------------------------------------

	private String getAccess(String access)
	{
		if (access.equals(Settings.ACCESS_PUBLIC))    return "public";
		if (access.equals(Settings.ACCESS_PROTECTED)) return "protected";
		if (access.equals(Settings.ACCESS_PRIVATE))   return "private";

		return "";
	}

	//---------------------------------------------------------------------------

	private void buildJdoFile(DatabaseNode dbNode)
	{
		// Is there a package and user selected JdoFile generation?
		if (!sett.getPackage().equals("") && sett.isJdoFile())
		{
			XmlElement root = new XmlElement("jdo");
			XmlElement xmlPackage = new XmlElement("package");
			xmlPackage.setAttribute(new XmlAttribute("name", sett.getPackage()));
			root.addChild(xmlPackage);
			// Build classes Elements
			Vector tables = dbNode.getObjects(TableNode.class);

			for (int i = 0; i < tables.size(); i++)
				buildJdoClassElement(xmlPackage, (TableNode)tables.elementAt(i));

			XmlDocument doc = new XmlDocument(root);
			doc.setDocType("jdo SYSTEM \"file:/javax/jdo/jdo.dtd\"");
			String fileContent = new XmlWriter().write(doc);
			fileContent = XmlCodec.decode(fileContent);
			//	Define the outputFile
			String fileName = dbNode.modsConfig.getValue(this, "output") + "/package.jdo";
			writeXmlFile(fileName, fileContent);
			buildAntFile(dbNode);
		}
	}

	//---------------------------------------------------------------------------

	private void buildJdoClassElement(XmlElement xmlPackage, TableNode tableNode)
	{
		XmlElement xmlClass = new XmlElement("class");
		String tableName = tableNode.attrSet.getString("name");
		// Build class Name
		String className = JavaUtil.ucJavaName(tableName);

		// Add attributes to class
		xmlClass.setAttribute(new XmlAttribute("name", className));
		xmlClass.setAttribute(new XmlAttribute("identity-type", "datastore"));
		// Add extension element
		XmlElement xmlExtension = new XmlElement("extension");
		// Add attributes to extension element
		addJdoAttributes(xmlExtension, "table", tableName.toUpperCase());
		// Add fields
		int count = tableNode.getChildCount();
		for (int i = 0; i < count; i++)
		{
			XmlElement field = buildXmlField((FieldNode)tableNode.getChild(i));
			xmlClass.addChild(field);
		}
		xmlPackage.addChild(xmlClass);
	}

	//---------------------------------------------------------------------------

	private XmlElement buildXmlField(FieldNode fn)
	{
		XmlElement root = new XmlElement("field");
		root.setAttribute(new XmlAttribute("name", JavaUtil.lcJavaName(fn.attrSet.getString("name"))));
		root.setAttribute(new XmlAttribute("persistence-modifier", "persistent"));
		// Add extension element
		XmlElement xmlExtension = new XmlElement("extension");
		// Add attributes to extension element
		addJdoAttributes(xmlExtension, "column", fn.attrSet.getString("name").toUpperCase());
		root.addChild(xmlExtension);

		return root;
	}

	//---------------------------------------------------------------------------

	private void addJdoAttributes(XmlElement element, String key, String value)
	{
		element.setAttribute(new XmlAttribute("vendor-name", "ojb"));
		element.setAttribute(new XmlAttribute("key", key));
		element.setAttribute(new XmlAttribute("value", value));
	}

	//---------------------------------------------------------------------------
	/**
	 * Create the build.xml file for Ant generation of a package.jar file.
	 * @param dbNode
	 */

	private void buildAntFile(DatabaseNode dbNode)
	{
		XmlElement root = new XmlElement("project");
		// Define project Name
		String projectName = sett.getPackage().substring(sett.getPackage().lastIndexOf(".") + 1);
		root.setAttribute(new XmlAttribute("name", projectName));
		root.setAttribute(new XmlAttribute("basedir", "."));
		root.setAttribute(new XmlAttribute("default", "all"));
		// Add Ant properties
		addAntProperty(root, "src.dir",          "${basedir}/src");
		addAntProperty(root, "lib",              "${basedir}/lib");
		addAntProperty(root, "fullbuildpath",    "${basedir}/build");
		addAntProperty(root, "jdofile",          "package.jdo");
		addAntProperty(root, "package",          sett.getPackage().replace('.', '/'));
		addAntProperty(root, "packagebuildpath", "build/${package}");
		addAntProperty(root, "jdoenhancerclass", "com.sun.jdori.enhancer.Main");
		addAntProperty(root, "jarfile",          projectName +  ".jar");
		addAntProperty(root, "javadocdir",       "${basedir}/docs");
		// Create build path
		addAntBuildPath(root);
		// Create make-class target
		addAntMakeClassTarget(root, sett.getPackage());
		// Add jdo-enhancer target
		addAntJdoEnhancerTarget(root, dbNode, sett.getPackage());
		// Add jar target
		addAntJarTarget(root);
		// Add javadoc target
		addAntJavadocTarget(root, sett.getPackage(), projectName);
		// Add all target
		XmlElement all = new XmlElement("target");
		all.setAttribute(new XmlAttribute("name", "all"));
		all.setAttribute(new XmlAttribute("depends", "make-class, jdo-enhancer, jar, javadocs"));
		root.addChild(all);
		// Create the Xml Document
		XmlDocument doc = new XmlDocument(root);
		String fileContent = new XmlWriter().write(doc);
		fileContent = XmlCodec.decode(fileContent);
		// Define the outputFile
		String fileName = dbNode.modsConfig.getValue(this, "output") + "/build.xml";
		writeXmlFile(fileName, fileContent);
	}

	//---------------------------------------------------------------------------

	private void addAntJavadocTarget(XmlElement root, String sPackage, String projectName)
	{
		XmlElement target = new XmlElement("target");
		target.setAttribute(new XmlAttribute("name", "javadocs"));
		XmlElement info = new XmlElement("echo");
		info.setValue("Creating javadocs");
		target.addChild(info);
		addAntMkDir(target, "${javadocdir}");
		XmlElement javadocTask = new XmlElement("javadoc");
		javadocTask.setAttribute(new XmlAttribute("packagenames", sPackage + ".*"));
		javadocTask.setAttribute(new XmlAttribute("sourcepath", "${src.dir}"));
		javadocTask.setAttribute(new XmlAttribute("defaultexcludes", "yes"));
		javadocTask.setAttribute(new XmlAttribute("destdir", "${javadocdir}"));
		javadocTask.setAttribute(new XmlAttribute("author", true));
		javadocTask.setAttribute(new XmlAttribute("version", true));
		javadocTask.setAttribute(new XmlAttribute("use", true));
		javadocTask.setAttribute(new XmlAttribute("windowTitle", projectName));
		target.addChild(javadocTask);
		root.addChild(target);
	}

	//---------------------------------------------------------------------------

	private void addAntJarTarget(XmlElement root)
	{
		XmlElement target = new XmlElement("target");
		target.setAttribute(new XmlAttribute("name", "jar"));
		XmlElement info = new XmlElement("echo");
		info.setValue("Creating jar file");
		target.addChild(info);
		//	Jar task
		XmlElement jarTask = new XmlElement("jar");
		jarTask.setAttribute(new XmlAttribute("file", "${jarfile}"));
		jarTask.setAttribute(new XmlAttribute("basedir", "${fullbuildpath}"));
		jarTask.setAttribute(new XmlAttribute("index", true));
		target.addChild(jarTask);
		root.addChild(target);
	}

	//---------------------------------------------------------------------------

	private void addAntJdoEnhancerTarget(XmlElement root, DatabaseNode dbNode, String sPackage)
	{
		XmlElement target = new XmlElement("target");
		target.setAttribute(new XmlAttribute("name", "jdo-enhancer"));
		addAntMkDir(target, "lib");
		XmlElement info = new XmlElement("echo");
		info.setValue("\n\n*** NOTE: Copy the jdo.jar and the jdori.jar to the lib directory\n\n");
		target.addChild(info);
		addAntMkDir(target, "${fullbuildpath}/META-INF");
		// Copy jdo file
		XmlElement copyJdoFile = new XmlElement("copy");
		copyJdoFile.setAttribute(new XmlAttribute("file", "${jdofile}"));
		copyJdoFile.setAttribute(new XmlAttribute("todir", "${fullbuildpath}/META-INF"));
		target.addChild(copyJdoFile);
		// Build classes Elements
		Vector tables = dbNode.getObjects(TableNode.class);

		for (int i = 0; i < tables.size(); i++)
			addAntJdoEnhacerTask(target, (TableNode)tables.elementAt(i));

		root.addChild(target);
	}

	//---------------------------------------------------------------------------

	private void addAntJdoEnhacerTask(XmlElement root, TableNode tn)
	{
		String className = JavaUtil.ucJavaName(tn.attrSet.getString("name")) + ".class";
		// Add some info
		XmlElement info = new XmlElement("echo");
		info.setValue("JDO Enhancing: " + className);
		root.addChild(info);
		// Add java task for JDO enhancement
		XmlElement task = new XmlElement("java");
		task.setAttribute(new XmlAttribute("fork", "yes"));
		task.setAttribute(new XmlAttribute("failonerror", "yes"));
		task.setAttribute(new XmlAttribute("classname", "${jdoenhancerclass}"));
		task.setAttribute(new XmlAttribute("classpathref", "jdo-classpath"));
		// Add command line
		XmlElement arg = new XmlElement("arg");
		String line = "-f -d ${fullbuildpath} ${jdofile} ${packagebuildpath}/" + className;
		arg.setAttribute(new XmlAttribute("line", line));
		task.addChild(arg);
		root.addChild(task);
	}

	//---------------------------------------------------------------------------

	private void addAntMakeClassTarget(XmlElement root, String sPackage)
	{
		XmlElement target = new XmlElement("target");
		target.setAttribute(new XmlAttribute("name", "make-class"));
		// Create source Dirs
		addAntMkDir(target, "${src.dir}/${package}");
		// Create destination dir
		addAntMkDir(target, "${fullbuildpath}");
		// Copy sources
		XmlElement copySrc = new XmlElement("copy");
		copySrc.setAttribute(new XmlAttribute("todir", "${src.dir}/${package}"));
		XmlElement fileSetCopySrc = new XmlElement("fileset");
		// Add fileSet for Copy
		fileSetCopySrc.setAttribute(new XmlAttribute("dir", "."));
		// Add include for fileSet of Copy
		XmlElement includeFileSetCopySrc = new XmlElement("include");
		includeFileSetCopySrc.setAttribute(new XmlAttribute("name", "*.java"));
		fileSetCopySrc.addChild(includeFileSetCopySrc);
		copySrc.addChild(fileSetCopySrc);
		target.addChild(copySrc);
		// Add compile task
		XmlElement javaC = new XmlElement("javac");
		//javaC.setAttribute(new XmlAttribute("compiler", "javac1.3"));
		javaC.setAttribute(new XmlAttribute("srcdir", "src"));
		javaC.setAttribute(new XmlAttribute("destdir", "build"));
		javaC.setAttribute(new XmlAttribute("extdirs", "lib"));

		int pos = sPackage.indexOf('.');

		String includes = (pos == -1 ? sPackage : sPackage.substring(0, pos)) + "/**";

		javaC.setAttribute(new XmlAttribute("includes", includes));
		target.addChild(javaC);
		root.addChild(target);
	}

	//---------------------------------------------------------------------------

	private void addAntMkDir(XmlElement root, String dir)
	{
		XmlElement mkDir = new XmlElement("mkdir");
		mkDir.setAttribute(new XmlAttribute("dir", dir));
		root.addChild(mkDir);
	}

	//---------------------------------------------------------------------------

	private void addAntBuildPath(XmlElement root)
	{
		XmlElement path = new XmlElement("path");
		path.setAttribute(new XmlAttribute("id", "jdo-classpath"));
		addAntPathElement(path, "build");
		//addAntPathElement(path, "${java.library.path}");
		XmlElement fileSet = new XmlElement("fileset");
		fileSet.setAttribute(new XmlAttribute("dir", "${lib}"));
		XmlElement include = new XmlElement("include");
		include.setAttribute(new XmlAttribute("name", "**/*.jar"));
		fileSet.addChild(include);
		path.addChild(fileSet);
		root.addChild(path);
	}

	//---------------------------------------------------------------------------

	private void addAntPathElement(XmlElement root, String path)
	{
		XmlElement pathElement = new XmlElement("pathelement");
		pathElement.setAttribute(new XmlAttribute("path", path));
		root.addChild(pathElement);
	}

	//---------------------------------------------------------------------------

	private void addAntProperty(XmlElement root, String name, String value)
	{
		XmlElement property = new XmlElement("property");
		property.setAttribute(new XmlAttribute("name", name));
		property.setAttribute(new XmlAttribute("value", value));
		root.addChild(property);
	}

	//---------------------------------------------------------------------------
	/**
	* Helper method. Write a xml file and it contents to the specified file
	*/

	private void writeXmlFile(String fileName, String fileContent) {
        Writer w = null;
        BufferedWriter bw = null;
		try {
			w = new FileWriter(fileName);
            bw = new BufferedWriter(w);
			bw.write(fileContent);
		} catch (java.io.IOException e) {
            /* do nothing */
        } finally {
            try {
                if (bw != null) bw.close();
                if (w != null) w.close();
            } catch (IOException e) { /* do nothing*/ }
        }
	}
}

//==============================================================================
