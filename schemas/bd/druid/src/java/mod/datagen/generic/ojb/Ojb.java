//==============================================================================
//===
//===   Ojb
//===
//===   Copyright (C) by Antonio Gallardo & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.generic.ojb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;

import mod.datagen.generic.ojb.panels.OptionPanel;

import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlCodec;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlWriter;

import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.GenericGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.util.JavaUtil;
import factory.sql.FKeyEntry;
import factory.sql.SqlUtil;

//==============================================================================

/**
 *  OJB Generator
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: Ojb.java,v 1.20.2.5 2006/01/16 09:15:58 antoniog Exp $
*/
public class Ojb implements GenericGenModule, ModuleOptions
{
    private OptionPanel optPanel = new OptionPanel();

    //---------------------------------------------------------------------------
    //---
    //--- Module interface
    //---
    //---------------------------------------------------------------------------

    public String getId() { return "ojb"; }
    public String getVersion() { return "1.0"; }
    public String getAuthor() { return "Antonio Gallardo"; }

    //---------------------------------------------------------------------------

    public String getDescription() {
        return "Generates Apache OJB repository.xml files. " +
        "More info: http://db.apache.org/ojb/repository.html";
    }

    //---------------------------------------------------------------------------

    public ModuleOptions getModuleOptions(int env) {
        if (env == DATABASE)
            return this;
        return null;
    }

    //---------------------------------------------------------------------------

    public JComponent getPanel() { return optPanel; }

    //---------------------------------------------------------------------------

    public void refresh(AbstractNode node)
	 {
		optPanel.refresh(new Settings(node.modsConfig,this));
    }

    //---------------------------------------------------------------------------

    public String getFormat() { return "Apache OJB"; }
    public boolean isDirectoryBased() { return true; }
    public boolean hasLargePanel() { return true; }

    //---------------------------------------------------------------------------
    //---
    //--- Module interface
    //---
    //---------------------------------------------------------------------------

    protected boolean packageDefined(String name) {
        return (!name.equals("") ? true : false);
    }

    //---------------------------------------------------------------------------

    /**
     * Called when the user clic on the button in the Data Generation Dialog.
     */
    public void generate(Logger l, DatabaseNode dbNode)
    {
        l.logHeader("Apache OJB Mapping");

		// Get settings of the user
		Settings settings = new Settings(dbNode.modsConfig, this);
		OjbParams params = new OjbParams(l, dbNode, settings, this);

		if (packageDefined(params.ojbPackage))
		{
			 new File(params.dirName).mkdirs();

	        // Create the OJB repository.xml
	        writeRepositoryXML(params);
			l.log(Logger.INFO, "");
			l.log(Logger.INFO, "Done.");
		} else {
			l.log(Logger.ALERT, "Please, specify a java package in the 'JDO Options' tab");
			l.log(Logger.ALERT, "Generation aborted.");
		}
    }

    private XmlElement buildClassDescriptor(OjbParams p, TableNode tableNode)
	{
		XmlElement root = new XmlElement("class-descriptor");
		String tableName = tableNode.attrSet.getString("name");
		// Build class Name
		String className = buildClassName(p, tableName);

		// Add attributes to class-descriptor
		root.setAttribute(new XmlAttribute("class", className));
		// root.setAttribute(new XmlAttribute("proxy", "dynamic"));
		root.setAttribute(new XmlAttribute("table", tableName.toUpperCase()));
		// Add fields
		int count = tableNode.getChildCount();
		for (int i = 0; i < count; i++) {
			XmlElement field = buildFieldDescriptor((FieldNode)tableNode.getChild(i), p);
			root.insertChildAt(i, field);
		}
		// Define foreign keys
        if (p.referenceDescriptor)
            buildReferences(p, root, tableNode);
		// Define collectionDescriptor
		if (p.collectionDescriptor)
		    buildCollectionDescriptor(p, root, tableNode);
		return root;
	}

	private void buildCollectionDescriptor(OjbParams p, XmlElement root, TableNode tn)
	{
		Iterator iterRef = DataLib.getReferences(tn, false).iterator();
        String tableName = tn.attrSet.getString("name");

		while (iterRef.hasNext()) {
			TableNode tnRef = (TableNode)iterRef.next();
            Vector foreignkeyVector = SqlUtil.getForeignKeys(tnRef, tableName);
            boolean useForeingKeyAsName = foreignkeyVector.size() > 1;
            for (int i=0; i<foreignkeyVector.size(); i++)
            {
                FKeyEntry fk = ((FKeyEntry)foreignkeyVector.elementAt(i));

                // Create a new collection descriptor for this table
                XmlElement coll = new XmlElement("collection-descriptor");

                String tableNameRef = tnRef.attrSet.getString("name");

                // Trying to setup a unique name for the collection-descriptor.
                // Is posible that we have several reference to this table
                // from one single table; in that case we can't use the name of
                // the table referenced, so we use the name of the attribute foreing key
                // that name have to be unique.
                String strName = JavaUtil.lcJavaName(tableNameRef);
                if (useForeingKeyAsName) {
                    // Name of the first Attribute Foreing Key
                    strName =  JavaUtil.lcJavaName((String)fk.vFields.elementAt(0)) + JavaUtil.ucJavaName(tableNameRef);
                }
                coll.setAttribute(new XmlAttribute("name", strName + "List"));

                // Attribute CollectionDescriptor Class Name
                coll.setAttribute(new XmlAttribute("element-class-ref", buildClassName(p, tableNameRef)));

                if (fk.onUpd.equals(FieldNode.CASCADE) && fk.onDel.equals(FieldNode.CASCADE)) {
                    coll.setAttribute(new XmlAttribute("auto-update", true));
                    coll.setAttribute(new XmlAttribute("auto-delete", true));
                } else {
                    coll.setAttribute(new XmlAttribute("auto-update", false));
                    coll.setAttribute(new XmlAttribute("auto-delete", false));
                }

                coll.setAttribute(new XmlAttribute("proxy", false));

                Vector vfields = fk.vFields;
                for (int j=0; j<vfields.size(); j++) {
                    XmlElement iFKey = new XmlElement("inverse-foreignkey");
                    iFKey.setAttribute(new XmlAttribute("field-ref", JavaUtil.lcJavaName((String)vfields.elementAt(j))));
                    coll.addChild(iFKey);
                }
                root.addChild(coll);

            }
		}
	}

	private String buildClassName(OjbParams p, String tableName) {
		// Add PackageName
		String name = p.ojbPackage;
		// Add the at the end a dot if does not exist
		name += p.ojbPackage.endsWith(".") ? "": "." + JavaUtil.ucJavaName(tableName);
		//	Add class Suffix if exists
		name += (!p.ojbClassSuffix.equals("")? "_" + p.ojbClassSuffix : "");
		return name;
	}

	/**
	 * Build the references (foreign keys) of the class
	 * @param p
	 * @param root - XmlElement of the class
	 * @param foreignkeyVector -- a Foreign key Vector
	 */
	private void buildReferences(OjbParams p, XmlElement root, TableNode tableNode)
	{
        Vector foreignkeyVector = SqlUtil.getForeignKeys(tableNode);
        for (int i=0; i<foreignkeyVector.size(); i++)
        {
            String tableFKey = ((FKeyEntry)foreignkeyVector.elementAt(i)).fkTable;
        	// reference-descriptor create a new one.
        	XmlElement reference = new XmlElement("reference-descriptor");
            // reference-descriptor attribute "name" are to be the first attribute name of the foreing key
            reference.setAttribute(new XmlAttribute("name",
                    JavaUtil.lcJavaName((String)((FKeyEntry)foreignkeyVector.elementAt(i)).vFields.elementAt(0)) +
                   "Ref"));
        	// Attribute Reference Class Name
        	reference.setAttribute(new XmlAttribute("class-ref", buildClassName(p, tableFKey)));
        	reference.setAttribute(new XmlAttribute("auto-update", false));
        	reference.setAttribute(new XmlAttribute("auto-delete", false));
        	reference.setAttribute(new XmlAttribute("proxy", false));

        	// Build the the foreignkey of the reference
            Vector vfields = ((FKeyEntry)foreignkeyVector.elementAt(i)).vFields;
            for (int j=0; j<vfields.size(); j++) {
            	addForeignKey(reference, JavaUtil.lcJavaName((String)vfields.elementAt(j)));
            }
        	root.addChild(reference);
        }
	}

	private void addForeignKey(XmlElement root, String fieldFKey)
	{
		XmlElement foreignKey = new XmlElement("foreignkey");
		foreignKey.setAttribute(new XmlAttribute("field-ref", fieldFKey));
		root.addChild(foreignKey);
	}

	//---------------------------------------------------------------------------

	private XmlElement buildFieldDescriptor(FieldNode fn, OjbParams p) {
		XmlElement root = new XmlElement("field-descriptor");
		root.setAttribute(new XmlAttribute("name", JavaUtil.lcJavaName(fn.attrSet.getString("name"))));
		if (DataLib.isPrimaryKey(fn))
			root.setAttribute(new XmlAttribute("primarykey", true));
		if (DataLib.isNotNull(fn))
			root.setAttribute(new XmlAttribute("nullable", false));
		root.setAttribute(new XmlAttribute("default-fetch", true));
		// Define sequence
		Integer defIndex = new Integer (DataLib.getDefault(fn.getDatabase().fieldAttribs,false));
		if (defIndex.intValue() != 0) {
			String strDef = getSequenceName(fn.fieldAttribs.getString(defIndex.toString()));
			if (strDef.length() > 0){
				root.setAttribute(new XmlAttribute("autoincrement", true));
				root.setAttribute(new XmlAttribute("sequence-name", strDef));
			}
		}
		root.setAttribute(new XmlAttribute("column", fn.attrSet.getString("name").toUpperCase()));
		TypeInfo ti = DataTypeLib.getTypeInfo(fn);
		String jdbcType = convertToJdbcTypel(ti);
		root.setAttribute(new XmlAttribute("jdbc-type", jdbcType));
		if (p.useDateConvertor) {
            if ("DATE".equals(jdbcType))
                root.setAttribute(new XmlAttribute("conversion", "org.apache.ojb.broker.accesslayer.conversions.JavaDate2SqlDateFieldConversion"));
            else if ("TIMESTAMP".equals(jdbcType))
                root.setAttribute(new XmlAttribute("conversion", "org.apache.ojb.broker.accesslayer.conversions.JavaDate2SqlTimestampFieldConversion"));
        }
		return root;
	}

    //---------------------------------------------------------------------------

    private String getSequenceName(String strDef)
    {
		if (strDef.startsWith("nextval(")) {
			strDef = strDef.substring(strDef.indexOf("(")+1, strDef.lastIndexOf(")"));
			while (!Character.isLetterOrDigit(strDef.charAt(0)) && strDef.length() > 0)
				strDef=strDef.substring(1);
			while (!Character.isLetterOrDigit(strDef.charAt(strDef.length()-1))  && strDef.length() > 0)
								strDef=strDef.substring(0, strDef.length()-1);
			return strDef;
		}
		return "";
    }

	private String convertToJdbcTypel(TypeInfo ti)
	{
		String type = ti.basicType.toLowerCase();

		if ("int4".equals(ti.basicType)) return "INTEGER";
		if ("int8".equals(ti.basicType)) return "LONG";
		if ("text".equals(ti.basicType)) return "VARCHAR";
		if ("boolean".equals(ti.basicType)) return "BIT";
		return type.toUpperCase();
	}

    //---------------------------------------------------------------------------

	/**
     * Writes the repository_database.xml. This file is always the same
 	 */
	private XmlElement getDBConnection(OjbParams p)
	{
		XmlElement root = new XmlElement("jdbc-connection-descriptor");

		root.setAttribute(new XmlAttribute(Settings.JCDALIAS, p.jcdAlias));
		root.setAttribute(new XmlAttribute(Settings.DEFAULTCONNECTION, p.defaultConnection));
		root.setAttribute(new XmlAttribute(Settings.DBPLAT, p.dbPlat));
		root.setAttribute(new XmlAttribute(Settings.JDBCLEVEL, p.jdbcLevel));

		String sDriver, sSubProtocol;
		if (p.dbPlat.equals(Settings.DBPLAT_POSTGRES)) {
			sDriver = "org.postgresql.Driver";
			sSubProtocol = "postgresql";
		}
		else {
			sDriver= "";
			sSubProtocol = "";
		}
		root.setAttribute(new XmlAttribute("driver", sDriver));
		root.setAttribute(new XmlAttribute("protocol", "jdbc"));
		root.setAttribute(new XmlAttribute("subprotocol", sSubProtocol));
		root.setAttribute(new XmlAttribute("dbalias", p.dbAlias));
		root.setAttribute(new XmlAttribute(Settings.DBUSER, p.dbUser));
		root.setAttribute(new XmlAttribute(Settings.DBPASSWORD, p.dbPassword));
		root.setAttribute(new XmlAttribute("eager-release", false));
		root.setAttribute(new XmlAttribute("batch-mode", false));
		root.setAttribute(new XmlAttribute("useAutoCommit", 1));
		root.setAttribute(new XmlAttribute("ignoreAutoCommitExceptions", false));

		// connection-pool child
		XmlElement pool = new XmlElement("connection-pool");
		pool.setAttribute(new XmlAttribute("maxActive", 21));
		pool.setAttribute(new XmlAttribute("validationQuery", ""));
		root.addChild(pool);

		// Sequence-manager child
		XmlElement sequence = new XmlElement("sequence-manager");
		sequence.setAttribute(new XmlAttribute("className", "org.apache.ojb.broker.util.sequence.SequenceManager" + p.sequenceManager + "Impl"));
		root.addChild(sequence);

		//	Attributes of default Sequence-manager
		XmlElement autoNaming = new XmlElement("attribute");

		// Apply to all the sequences Manager
		autoNaming.setAttribute(new XmlAttribute("attribute-name", "autoNaming"));
		autoNaming.setAttribute(new XmlAttribute("attribute-value", true));
		sequence.addChild(autoNaming);

		if (p.sequenceManager.equals(Settings.SEQUENCE_HIGHLOW ) ||
			p.sequenceManager.equals(Settings.SEQUENCE_SEQHILO))
		{
			XmlElement grabSize = new XmlElement("attribute");
			grabSize.setAttribute(new XmlAttribute("attribute-name", "grabSize"));
			grabSize.setAttribute(new XmlAttribute("attribute-value", 20));
			sequence.addChild(grabSize);

			if (p.sequenceManager.equals(Settings.SEQUENCE_HIGHLOW ))
			{
				XmlElement globalSequenceId = new XmlElement("attribute");
				globalSequenceId.setAttribute(new XmlAttribute("attribute-name", "globalSequenceId"));
				globalSequenceId.setAttribute(new XmlAttribute("attribute-value", false));
				sequence.addChild(globalSequenceId);

				XmlElement globalSequenceStart = new XmlElement("attribute");
				globalSequenceStart.setAttribute(new XmlAttribute("attribute-name", "globalSequenceStart"));
				globalSequenceStart.setAttribute(new XmlAttribute("attribute-value", 10000));
				sequence.addChild(globalSequenceStart);
			}
		}
		return root;
	}

	//---------------------------------------------------------------------------

	/**
     * Writes the repository.xml. This file is always the same
	 */
	private void writeRepositoryXML(OjbParams p)
	{
		XmlElement root = new XmlElement("descriptor-repository");
		XmlDocument doc = new XmlDocument(root);

		doc.setDocType("descriptor-repository PUBLIC " +
		"\"-//Apache Software Foundation//DTD OJB Repository//EN\" " +
		"\"repository.dtd\"");
		root.setAttribute(new XmlAttribute("version", "1.0"));
		root.setAttribute(new XmlAttribute("isolation-level", "read-uncommitted"));
		root.setValue("\n\n&database;\n\n&tables;\n\n");

		// Set jdbc-connection
		root.addChild(getDBConnection(p));
		// Set classes
		Vector tables = p.dbNode.getObjects(TableNode.class);

		for (int i = 0; i < tables.size(); i++)
			root.addChild(buildClassDescriptor(p, (TableNode)tables.elementAt(i)));

		String fileContent = new XmlWriter().write(doc);
		fileContent = XmlCodec.decode(fileContent);
		String fileName = p.dirName + "/" + Settings.REPOSITORY_XML;
		writeFile(p.logger, fileName, fileContent);
	}

	/**
	 * Helper method. Write the file and it contents to the specified file
	 */
	private void writeFile(Logger l, String fileName, String fileContent) {
        Writer w = null;
        BufferedWriter bw = null;
		try {
			w = new FileWriter(fileName);
            bw = new BufferedWriter(w);
			l.log(Logger.INFO, "Writing: " + fileName);
			bw.write(fileContent);
		} catch (java.io.IOException e) {
			l.log(Logger.ALERT, e.getMessage());
        } finally {
            try {
                if (bw != null) bw.close();
                if (w != null) w.close();
            } catch (IOException e) { /* do nothing*/ }
        }
	}
}

//==============================================================================
