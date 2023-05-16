//==============================================================================
//===
//===   Php
//===
//===   Copyright (C) by Andrea Carboni
//===   Alterations by Paul J. Morris  
//===   Copyright (C) 2008 President and Fellows of Harvard College
//===   This file may be distributed under the terms of the GPL license.
//===   File last changed on $Date: 2009-04-13 13:07:48 -0400 (Mon, 13 Apr 2009) $ by $Author: mole $ in $Rev: 601 $.
//==============================================================================

package mod.datagen.code.php;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.TableVars;
import druid.data.Trigger;
import druid.data.datatypes.TypeInfo;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import factory.code.AbstractLang;
import javax.swing.JComponent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import mod.datagen.code.php.panels.OptionPanel;

import org.dlib.tools.Util;

//==============================================================================

/* 
 * Php
 * Generates PHP code based on Entity/Attribute (Table/Field) definitions in a Druid Model.
 * Can generate code with a range of different capabilities, from a single class per table
 * containing constants describing the table structure to a CRUD capable Model class and an
 * accompanying View class for each table along with framework and example code to produce a
 * database driven web site from a Druid model.  Options more complex than constants depend on
 * the implementation of objects in PHP 5 and won't work with PHP 4.
 */
public class Php extends AbstractLang implements ModuleOptions
{

	protected Settings sett;  // The selected settings for the PHP code generation module on the the Druid UI

	private OptionPanel optPanel = new OptionPanel();
	
	// regex pattern used to clean field size to handle decimals as well as
	// integers.  For example, change '10,2' to '10'.
	private static String SIZE_REPLACEMENT_PATTERN = ",.*"; 
	
	// A list of fields for each table is provided as a list with separators, separator and
	// pattern allow the terminal separator to be stripped off allowing the
	// field list to be used to construct insert into (fieldlist)... 
	private static String FIELD_END_SEPARATOR = ", ";
    private static String FIELD_END_SEPARATOR_PATTERN = ", $";
    
    // Names of optional generated database level files and directories
    private static final String DRUID_INTERFACES_PHP = "druid_interfaces.php";
    private static final String DRUID_CLASSES_PHP = "druid_classes.php";
    private static final String DRUID_HANDLER_PHP = "druid_handler.php";
    private static final String DRUID_TEST_DIR = "test";  // path from outputDir to test files
    private static final String DRUID_TEST_TO_CLASSES_PATH = ".."; // path from DRUID_TEST_DIR to outputDir
    private static final String DRUID_ALL_TESTS_PHP = DRUID_TEST_DIR + "/all_tests.php";
    private static final String DRUID_TEST_CLASSES_PHP = DRUID_TEST_DIR + "/druid_test_classes.php";
    private static final String DRUID_TEST_CLASSES_PHP_RAW = "druid_test_classes.php";
    
    private static final int FORM_INPUT_SIZE_MAXIMUM = 51;  // should be user provided
    
    // how to display and recognize null field values inside the PHP code 
    public static final String NULL_DISPLAY = "[NULL]";
    public static final String NULL_PATTERN = "\\[NULL\\]";
    
    // variables to hold information used in multiple methods 
    private StringBuffer relatedTables;  // Holds parts of several genera ted PHP methods in model and view classes
                                         // that are used to link out to tables through foreign key relationships
    private int numberOfPrimaryKeys;     // Count of the number of primary keys for a table
	private String primaryKey;  // Will hold the name of the primary key field (or list of primary key fields) for a table.
	private ArrayList<FieldNode> primaryKeys;    // Fields involved in the primary key for a table.
	private boolean primaryKeyIsAutoAssigned;  // true if PK is SERIAL or AUTO_INCREMENT or otherwise auto-incremented
	
	// names of generated PHP class state variables - these may get renamed to prevent conflicts with fieldnames.
	private String dirtyvar = "dirty";
	protected String loadedvar = "loaded";
	private String errorvar = "error";
	// names of of generated PHP class constants - these may get renamed to prevent conflicts with fieldnames.
	private String fieldlistvar = "FIELDLIST";
	private String pklistvar = "PKFIELDLIST";
	private String pkcountvar = "NUMBER_OF_PRIMARY_KEYS";
	
	private String fieldlist;  // Will hold comma delimited list of fields in a table (see: FIELD_END_SEPARATOR).
	
	//---------------------------------------------------------------------------
	//---
	//--- Module interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "php"; }
	public String getVersion()  { return "1.1";  }
	public String getAuthor()   { return "Andrea Carboni, Paul J. Morris"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return "Generates PHP code with a class (using PHP5's object syntax) for each table.  " +
				"Can generate a range of different functionality in PHP, including example " +
				"files for a database driven website.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE)	return this;
			else				return null;
	}

	private boolean createDirs(String outputDir)
	{
		final String dirs[] = { DRUID_TEST_DIR };
		String separator = "";
		if (!outputDir.endsWith(System.getProperty("file.separator"))) { separator = System.getProperty("file.separator") ; } 

		boolean returnValue = true;
		for(int i=0; i<dirs.length; i++) {
			File f = new File(outputDir + separator + dirs[i]);
			try {
			   if (!f.mkdirs())  { 
				   returnValue=false; 
			   } 
			} catch (Exception e) {
				returnValue = false;
			}
		}
		return returnValue;
	}

	/*
	 * generate()
	 * Generates PHP code from a Druid model, main entry point for class Php.
	 * @see factory.code.AbstractLang#generate(druid.interfaces.Logger, druid.data.DatabaseNode)
	 */
	public void generate(Logger l, DatabaseNode dbNode) {
		//Overview: 
		//Override parent AbstractLang's generate to create Database level files
		//then, when done, call the parent's generate method to iterate through 
		//the tables and generate Table level class files.
		
		this.setup(dbNode);
		
		// Generate files for which there is one copy per database
		//
		// See Php.writeDatabaseLevelFiles() below for the one file per database code.
		this.writeDatabaseLevelFiles(l, dbNode);
			
		// super is AbstractLang, it's generate method calls getClassCodeInt() on each table
		// and writes the return value to a file, generating one class file per table.
		// getClassCodeInt() is overridden in Php below, it invokes methods that
		// generate the code for the one file per table output.
		//
		// See Php.getClassCodeInt() below for the class per table code.
		super.generate(l, dbNode);
	}
	
	//---------------------------------------------------------------------------

	public JComponent getPanel() { return optPanel; }

	//---------------------------------------------------------------------------

	public void refresh(AbstractNode node)
	{
		optPanel.refresh(new Settings(node.modsConfig, this));
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "Php"; }
	public boolean isDirectoryBased() { return true;  }
	public boolean hasLargePanel()    { return true;  }

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
	}

	//---------------------------------------------------------------------------
	//---
	//--- Ancillary supporting function, could move into a druid.core class
	//---
	//---------------------------------------------------------------------------
	
	/* Tests whether a field will have an automatically incrementing value 
	 * set when a new record is added.  This is the auto_increment field attribute
	 * in MySQL, a field of type serial in Postgresql, a field with a trigger 
	 * linking it to a sequence, a field with a default value obtained from
	 * a sequence, or a field that automatically applies a time stamp.   An auto-
	 * incrementing field might be a surrogate numeric primary key.
	 * @param f The field to check for an auto-incrementing sequence.
	 * @returns true if any property suggesting auto-incrementing was found, 
	 * otherwise returns false.   
	 */
	public boolean isAutoIncrement(FieldNode f) {
		boolean returnvalue = false;
		TypeInfo ti = DataTypeLib.getTypeInfo(f);
		
    	DatabaseNode dbNode = f.getDatabase();
    	Vector<String> vAttribAutoIncrement = new Vector<String>();
    	Vector<String> vAttribDefault = new Vector<String>();
    	AttribList attribs = dbNode.fieldAttribs;
    	//--- retrieve attribs that are auto_increment or default
    	for(int i=0; i< attribs.size(); i++)
    	{
    		AttribSet as      = attribs.get(i);
    		String    sqlName = as.getString("sqlName").toLowerCase();
    		String    type    = as.getString("type");

    		if (sqlName.equals("auto_increment") && type.equals(FieldAttribs.TYPE_BOOL))
    			vAttribAutoIncrement.addElement("" + as.getInt("id"));
    		if (sqlName.equals("default"))
    			vAttribDefault.addElement("" + as.getInt("id"));
    	}
		
		// Is this an integer field   (tinyint probably doesn't make sense as an autoincrementing primary key)
	    String basicType = ti.basicType.toUpperCase();
	    if (basicType.equals("TINYINT") || basicType.equals("TINYINT UNSIGNED") ||
	    		basicType.equals("SMALLINT") || basicType.equals("TINYINT UNSIGNED") ||
	    		basicType.equals("MEDIUMINT") || basicType.equals("TINYINT UNSIGNED") ||
	    		basicType.equals("INT") || basicType.equals("INT UNSIGNED") ||
	    		basicType.equals("INTEGER") || basicType.equals("INTEGER UNSIGNED") ||
	    		basicType.equals("BIGINT") || basicType.equals("BIGINT UNSIGNED")) 
	     {
	    	// Is the field attribute auto_increment set for this field?

	    	//--- scan auto_increment attribs to see if field has one set
	    	for(int i=0; i<vAttribAutoIncrement.size(); i++)
	    	{
	    		String attribId = (String) vAttribAutoIncrement.elementAt(i);

	    		if (f.fieldAttribs.getBool(attribId)) { 
	    			returnvalue =  true;
	    	    	System.out.println("auto_increment" + " [" + returnvalue + "]"  );	 
	    		}
	    	}
	    	
	    	// Is there a trigger with a sequence
	    	TableNode t = f.getParentTable();
	    	try { 
	    	   Trigger triggers = t.triggers;
	    	   String code = triggers.attrSet.getString("code").toUpperCase();
	    	   if (code.contains("SEQ") && code.contains("NEXTVAL") && code.contains(f.attrSet.getString("name").toUpperCase())) { 
	    	        // If trigger contains "SEQ", "NEXTVAL" and fieldname guess that it is setting the next value of a sequence on this field.
	    	        // We cant't tell for sure without understanding the trigger syntax.
	    		    returnvalue =  true;
	    	   }
	    	} catch (NullPointerException e) { 
	    		// expected if table has no triggers
	    	}
	    	
	    	// Is there a default value with a sequence
	    	//postgresql NEXTVAL(seq)
	    	//standard NEXT VALUE FOR seq
	    	//oracle seq.NEXTVAL
	    	for(int i=0; i<vAttribDefault.size(); i++)
	    	{
	    		String attribId = (String) vAttribDefault.elementAt(i);
	    		String defaultVal = f.fieldAttribs.getString(attribId).toUpperCase().trim();
	    		if (defaultVal.startsWith("NEXTVAL") || defaultVal.startsWith("NEXT VALUE FOR") || defaultVal.endsWith(".NEXTVAL")) {
	    			returnvalue =  true;
	    	    	System.out.println("NEXTVAL" + " [" + returnvalue + "]"  );	 
	    		}
	    	}
	    }

	    // Is this field's data type SERIAL
	    if (ti.basicType.toUpperCase().equals("SERIAL") || ti.getSqlType().toUpperCase().equals("SERIAL")) { 
	    	returnvalue = true;
	    	System.out.println("SERIAL" + " [" + returnvalue + "]"  );	 	    	
	    }
		
		// Is this a timestamp field  (including timestamp with timezone and timestamp with local timezone)
	    if (ti.basicType.toUpperCase().startsWith("TIMESTAMP") || ti.getSqlType().toUpperCase().startsWith("TIMESTAMP")) { 
	    	returnvalue = true;
	    	System.out.println("TIMESTAMP" + " [" + returnvalue + "]"  );	    	
	    }
		
	    // Is this a date/time field
	    if (ti.basicType.toUpperCase().equals("DATETIME") || ti.basicType.toUpperCase().equals("DATE")|| ti.basicType.toUpperCase().equals("TIME")) {

	    	//  with a default such as now()
	    	for(int i=0; i<vAttribDefault.size(); i++)
	    	{
	    		String attribId = (String) vAttribDefault.elementAt(i);
	    		String defaultVal = f.fieldAttribs.getString(attribId).toUpperCase().trim();
	    		if (defaultVal.startsWith("CURRENT_TIMESTAMP") || defaultVal.startsWith("NOW()"))
	    			returnvalue =  true;
	    	}
	    	//  with a trigger invoking functions like now()

	    	TableNode t = f.getParentTable();
	    	try {
				Trigger triggers = t.triggers;
				String code = triggers.attrSet.getString("code").toUpperCase();
				if ((code.contains("NOW()") || code.contains("CURRDATE")
						|| code.contains("CURRTIME")
						|| code.contains("CURRENT_DATE") 
						|| code.contains("CURRENT_TIME"))
						&& code.contains(f.attrSet.getString("name").toUpperCase())) 
				{
					// If trigger contains "SEQ", "NEXTVAL" and fieldname guess
					// that it is setting the next value of a sequence on this
					// field.
					// We cant't tell for sure without understanding the trigger
					// syntax.
					returnvalue = true;
				}
			} catch (NullPointerException e) {
				// expected if table has no triggers
			}
	    }
    	
    	System.out.println("Testing " + f.attrSet.getString("name") + " [" + returnvalue + "]"  );
		
		return returnvalue;
	}
	
	//---------------------------------------------------------------------------
	//---
	//--- PHP Classes Generation
	//---
	//---------------------------------------------------------------------------

    /*
     * getClassCodeInt()
     * Entry point for code generated per table.
     * @see factory.code.AbstractLang#getClassCodeInt(druid.interfaces.Logger logger, druid.data.TableNode node)
     */
	protected String getClassCodeInt(Logger logger, TableNode node)
	{
		String name = node.attrSet.getString("name");
		StringBuffer tableVars = new StringBuffer();
		StringBuffer lengthConstants = new StringBuffer();
		StringBuffer nameConstants  = new StringBuffer();		
		StringBuffer variables = new StringBuffer();
		StringBuffer persist  = new StringBuffer();
		StringBuffer view  = new StringBuffer();

		relatedTables = new StringBuffer();  // make sure that related tables is empty before starting
		
        // Find out some details about the table that will be repeatedly used
		// and store these in variables with class scope.
		primaryKey = "";  // Will hold the name of the primary key field (or list of primary key fields) .
		primaryKeys = getPrimaryKeyFieldNodes(node);
		primaryKeyIsAutoAssigned = false; // default value
		numberOfPrimaryKeys = primaryKeys.size();
		for (int i=0; i<numberOfPrimaryKeys; i++) { 
			if (i==0) {
		        primaryKey = primaryKeys.get(i).attrSet.getString("name");
			} else {
		        primaryKey = primaryKey + ", " + primaryKeys.get(i).attrSet.getString("name");
			}    
		} 
		if (numberOfPrimaryKeys==1) { 
			if (isAutoIncrement(primaryKeys.get(0))) { 
				primaryKeyIsAutoAssigned = true;
			}
		}
		
		// Logic for which code generation methods are called is encapsulated in this method and writeDatabaseLevelFiles.
		//0 TableVar constants are always generated
		//1 sett.isGenConsts()  // field length constants (used by 3,4,5,6)
		//2 sett.isGenNames()   // field name constants   (used by 3,5,5,6)
		//3 sett.isGenVaria()   // variables + get and set(used by 4,5,6)(interface model - druid_interfaces)
		//4 sett.isGenPersi()   // persistence framework  (used by 5,6)(interfaces summarymodel, loadablemodel, savablemodel)
		//5 sett.isGenMVC()     // view class  (tablenameView, interface viewer) (used by 6)
		   //sett.isGenDojo()    // add ajaxian editing form to view class (embed)
		//6 sett.isGenTests()   // unit tests (test folder and files) 
		//7 sett.isGenFrame()   // druid_handler, druid_classes framework files 
		//8 sett.isGenExample() // example index.php, class_lib, ajax_handler (independent)
		

	    // Original behavior of Druid 3.9 - always do tablevars, allow optional length constants and name constants.
		// "table vars" from Var tab of Table
		tableVars.append(getTableVarConstants(node));
		
	    if (sett.isGenConsts() || sett.isGenVaria() || sett.isGenPersi() || sett.isGenMVC() || sett.isGenTests()) {
			// generate length constants
			lengthConstants.append(getLengthConstants(node));
			
		}
		if (sett.isGenNames() || sett.isGenVaria() || sett.isGenPersi() || sett.isGenMVC() || sett.isGenTests()) { 
			// generate field name constants
			nameConstants.append(getNameConstants(node));
		}		
		
		// New behaviors and options added after Druid 3.10
		if (sett.isGenVaria() || sett.isGenPersi() || sett.isGenMVC() || sett.isGenTests()) {
		    // set class level variables used in both getVariables() and getPersist()
			// prepare to add state variables
			dirtyvar = "dirty";
			loadedvar = "loaded";
			errorvar = "error";
			fieldlistvar = "FIELDLIST";
			pklistvar = "PKFIELDLIST";
			pkcountvar = "NUMBER_OF_PRIMARY_KEYS";
			// Make sure state variables names aren't in use 
			// that is, not shared with variables created from field names.
			dirtyvar = fixGlobalName( node, dirtyvar);
			loadedvar = fixGlobalName( node, loadedvar);
			errorvar = fixGlobalName( node, errorvar);
			fieldlistvar = fixGlobalName( node, fieldlistvar);
			pklistvar = fixGlobalName( node, pklistvar);	
			pkcountvar = fixGlobalName( node, pkcountvar);
			fieldlist = " ";  // Will hold comma delimited list of fields (see: FIELD_END_SEPARATOR).
			// generate variables + get/set methods
			variables.append(getVariables(node));
		}		
		if (sett.isGenPersi() || sett.isGenMVC() || sett.isGenTests()) { 
			// persistence framework
			persist.append(getPersist(node));
		}		
		if (sett.isGenMVC() || sett.isGenTests()) { 
			// view
			view.append(getViewClass(node));
		}	
		if (sett.isGenTests()) { // unit tests, done in generate with writeDatabaseLevelFiles 	
		}
		if (sett.isGenFrame()) { // framework files, done in generate with writeDatabaseLevelFiles 	
		}
		if (sett.isGenTests()) { // unit tests, done in generate with writeDatabaseLevelFiles 			
		}
		
		//consts.append(getConsts(node));
		//vars.append(getVars(node.tableVars, dbNode));
		

		if (tableVars.length()==0 && lengthConstants.length()==0 && nameConstants.length()==0 && variables.length()==0 && persist.length()==0 && persist.length()==0  && view.length()==0)
			return "";

		return getHeader(name, node) + tableVars + lengthConstants + nameConstants +  variables + persist + view + getFooter();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------
	
	protected ArrayList<FieldNode> getPrimaryKeyFieldNodes(TableNode node) { 
		ArrayList<FieldNode> returnvalue = new ArrayList<FieldNode>() ;
		int keys = 0;
		for (int i = 0; i < node.getChildCount(); i++) {
			FieldNode f = (FieldNode) node.getChild(i);
			if (DataLib.isPrimaryKey(f)) { 
				returnvalue.add(keys, f);
				keys ++;
			}
		}	
		return returnvalue;
	}
	
    /*  getLengthConstants
     *  Generate length constants. 
	 *  @param node TableNode for which to get lengths for fields.
	 *  @returns string containing PHP constants holding field lengths in form 
	 *    const{fieldname}_SIZE = {length} // {fieldtype}
	 */
	private String getLengthConstants(TableNode node) {
		StringBuffer w = new StringBuffer();  // Holds output.
		w.append("   // These constants hold the sizes the fields in this table in the database.").append(LF);
		for( int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode f = (FieldNode)node.getChild(i);
			TypeInfo ti = DataTypeLib.getTypeInfo(f);

			//--- put field size (if the case)
			if (ti != null && ti.size != null)
			{
				String size = ti.size.replaceAll(SIZE_REPLACEMENT_PATTERN, "");   // handle pattern for decimals: e.g. '10,2'
				try
				{
					Integer.parseInt(size);

					String name = f.attrSet.getString("name").toUpperCase() + "_SIZE";
					w.append("   const ");
					w.append(Util.pad(name, 20)).append(" = ").append(size).append("; //" + ti.size).append(LF);
				}
				catch(NumberFormatException e)
				{
					//--- we arrive here if the size is something like '10,3'
					//--- in this case we don't generate the entry
				}
			} else {
				if (ti != null) { 
					// lookup an appropriate maximum length for the datatype
					String size = this.getFieldSize(f);
					String name = f.attrSet.getString("name").toUpperCase() + "_SIZE";
					String unsigned = "";
					if (DataLib.isUnsigned(f)) { unsigned = " UNSIGNED"; } 
					w.append("   const ");
					w.append(Util.pad(name, 20) + " = " + size +  "; //" + ti.basicType + unsigned).append(LF);
				}
			}
		}
		return w.toString();
	}
	
	//---------------------------------------------------------------------------
	/*
	 * getTableVaarConstants
	 * @param node the TableNode for which to obtain a list of druid "table variables"
	 *     from the Vars tab of a table definition.
	 * @returns a string containing a list of PHP constants holding table vars in 
	 *     the table provided in node using the form:
	 *     // {description} 
	 *     const {name} = {value};    
	 *     or for table vars of type string
	 *     const {name} = '{value}';
	 */
	private String getTableVarConstants(TableNode node)
	{
		TableVars tv = node.tableVars;
		DatabaseNode dbNode = node.getDatabase();
		StringBuffer w = new StringBuffer();
		if (tv.size()>0) {
			w.append("    // These constants hold the values of 'vars' for the table ").append(LF);
			w.append("    // as defined in the Vars tab of a table in Druid. ").append(LF);
			w.append(getSmallSeparator());
			for (int i = 0; i < tv.size(); i++)
			{
				AttribSet as = tv.get(i);

				String name  = as.getString("name");
				String type  = as.getString("type");
				String value = as.getString("value");
				String descr = as.getString("descr");

				if (!descr.equals(""))
				{
					w.append(LF);
					w.append("   //--- " + descr + LF);
				}

				String line = "   const ";

				if (type.equals(TableVars.STRING))
					if (!value.equals(""))
						value = fillBounds(value, "'");

				if (value.equals(""))
					w.append(Util.pad(line + name, 47) + ";" + LF);
				else
					w.append(Util.pad(line + name, 47) + " = " + dbNode.substVars(value) + ";" + LF);
			}
		}		
		
		return w.toString();
	}
	/*
	 * get nameConstants
	 * @param node the TableNode for which to obtain a list of field names
	 * @returns a string containing a list of PHP constants holding field names in 
	 *     the table provided in node using the form:
	 *     // {description} 
	 *     const {FIELDNAME} = '{fieldname}'; 
	 */
	private String getNameConstants(TableNode node)	{

		StringBuffer w = new StringBuffer();
	
		w.append("    // These constants hold the field names of the table in the database. ").append(LF);
		for (int i = 0; i < node.getChildCount(); i++)
		{
			FieldNode f = (FieldNode) node.getChild(i);
			String name = f.attrSet.getString("name");

			//--- put comment (if any)
			String comment = f.attrSet.getString("comment").trim();

			if (!comment.equals(""))
				w.append("   // ").append(comment).append(LF);

			//--- put field name as constant
			w.append("   const ");
			w.append(Util.pad(name.toUpperCase(), 17)).append(" = '").append(name).append("';").append(LF);
		}
		
		return w.toString();
	}

	
	private String getVariables(TableNode node) {
		StringBuffer w = new StringBuffer();  // Holds output.
		
		    w.append(getSmallSeparator());
			
			// generate supporting discovery code
			w.append("   // interface tableSchema implementation").append(LF);
			w.append("   // schemaPK returns array of primary key field names").append(LF);
			w.append("   public function schemaPK() {" ).append(LF);
			w.append("       return $this->primaryKeyArray;" ).append(LF);
			w.append("   } " ).append(LF);
			w.append("   // schemaHaveDistinct returns array of field names for which selectDistinct{fieldname} methods are available.").append(LF);
			w.append("   public function schemaHaveDistinct() {").append(LF);
			w.append("       return $this->selectDistinctFieldsArray;" ).append(LF);
			w.append("   } " ).append(LF);
			w.append("   // schemaFields returns array of all field names").append(LF);
			w.append("   public function schemaFields() { ").append(LF);
			w.append("       return $this->allFieldsArray;" ).append(LF);
			w.append("   } " ).append(LF);

			// Generate commented out example of how to get and sanitize values from $_GET 
			// for each variable. 
		    w.append("/*  Example sanitized retrieval of variable matching object variables from $_GET ").append(LF);
		    w.append("/*  Customize these to limit each variable to narrowest possible set of known good values. ").append(LF).append(LF);
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
                String sqlType = DataTypeLib.getSqlType(f);
                String null_pattern = NULL_PATTERN;
                // check if nulls are allowed in number fields
                // if() { null_pattern = ''; }
                String size = getFieldSize(f);
			    if (sqlType.equals("INTEGER")) {
			    	w.append("  $" + name + " = substr(preg_replace('/[^0-9\\-"+null_pattern+"]/','',$_GET['"+ name + "']), 0, " + size +  ");").append(LF);
			    } else if (sqlType.matches("^DECIMAL\\([0-9,]*\\)$")) {
			    	w.append("  $" + name + " = substr(preg_replace('/[^0-9\\-\\."+null_pattern+"]/','',$_GET['"+ name + "']), 0, " + size + ");").append(LF);
			    } else { 
			    	// allways allow NULL_PATTERN in non-numeric fields, as they can contain empty strings, which are handled the same way.
			    	w.append("  $" + name + " = substr(preg_replace('/[^A-Za-z0-9\\.\\.\\ "+NULL_PATTERN+"]/','',$_GET['"+ name + "']), 0, " + size + ");").append(LF);			    	
			    }
			}
			w.append("*/").append(LF);

		    w.append(getSmallSeparator());
			
			
			String pklist = " ";  // Will hold comma delimited list of primary key fields (see: FIELD_END__SEPARATOR).  
			String nodeTableName = getClassCodeName(node);   // the name of the current table with prefix/suffix
			// add variables for each field    
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				String comment = f.attrSet.getString("comment");  // sql comment, limited to a single line
                String sqlType = DataTypeLib.getSqlType(f);       // field type
				//--- put field name in variable, include comment with field type and sql comment from druid
                if (DataLib.isPrimaryKey(f)) { 
				    w.append("   private $"+name+"; // PK "+sqlType+" "+comment).append(LF);
				    pklist = pklist.concat(name).concat(FIELD_END_SEPARATOR);
                } else {
                	w.append("   private $"+name+"; // "+sqlType+" "+comment).append(LF);
			    }	
				// add field name to list of fields
				fieldlist = fieldlist.concat(name).concat(FIELD_END_SEPARATOR);
			}

			// It is now ok to write state variables
			w.append("   private $" + dirtyvar + ";").append(LF);
			w.append("   private $" + loadedvar + ";").append(LF);
			w.append("   private $" + errorvar + ";").append(LF);
			w.append("   const " + fieldlistvar + " = '" + fieldlist + "';").append(LF);
			w.append("   const " + pklistvar + " = '" + pklist + "';").append(LF);
			w.append("   const " + pkcountvar + " = " + numberOfPrimaryKeys + ";").append(LF);
			if (numberOfPrimaryKeys == 0) { 
				w.append("   // Warning: The table for this object has no primary key.").append(LF);
			}
			StringBuffer tempFieldList = new StringBuffer();
			for (int j=1;j<=primaryKeys.size();j++) {
				if (j>1&&j<=primaryKeys.size()) { tempFieldList.append(", "); }
				tempFieldList.append(j + " => '" + primaryKeys.get(j-1) + "' ");
			}
			w.append("   private $primaryKeyArray = array( " + tempFieldList + " ) ;").append(LF);
			
			tempFieldList = new StringBuffer();
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				if (i>0) { tempFieldList.append(", "); }
				tempFieldList.append(i + " => '" + name + "' ");
			}
			w.append("   private $allFieldsArray = array( " + tempFieldList + " ) ;").append(LF);
			
			tempFieldList = new StringBuffer();
			int indexCount = 0;
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				// generate of list of fields with index using same test as used later to generate selectDistinct methods
				if (DataLib.isIndexed(f) && (numberOfPrimaryKeys > 1 || !DataLib.isPrimaryKey(f))) { 
				   indexCount++;
				   String name = f.attrSet.getString("name");
				   if (indexCount>1) { tempFieldList.append(", "); }
				   tempFieldList.append(i + " => '" + name + "' ");
				}
			}
			w.append("   private $selectDistinctFieldsArray = array( " + tempFieldList + " ) ;").append(LF);
			
			w.append(getSmallSeparator());

			// add a constructor 
			w.append("   // constructor ").append(LF);
			w.append("   function ").append(nodeTableName).append("(){").append(LF);
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				if (DataLib.isPrimaryKey(f)) {
		   	        w.append("       $this->").append(name).append(" = NULL;").append(LF);
				} else { 
   				    //--- field name variable assigned to empty string 
		   	        w.append("       $this->").append(name).append(" = '';").append(LF);
				}
			}
			w.append("       $this->").append(dirtyvar).append(" = false;").append(LF);
			w.append("       $this->").append(loadedvar).append(" = false;").append(LF);
			w.append("       $this->").append(errorvar).append(" = '';").append(LF);			
			w.append("   }").append(LF).append(LF);
			
			// utility functions to quote strings for db insertion
			w.append("   private function l_addslashes($value) {").append(LF);
			w.append("      $retval = $value;").append(LF);
			w.append("      if (!get_magic_quotes_gpc()) {").append(LF);
			w.append("          $retval = addslashes($value);").append(LF);
			w.append("      }").append(LF);
			w.append("      return $retval;").append(LF);
			w.append("   }").append(LF);
			w.append("   private function l_stripslashes($value) {").append(LF);
			w.append("      $retval = $value;").append(LF);
			w.append("      if (!get_magic_quotes_gpc()) {").append(LF);
			w.append("          $retval = stripslashes($value);").append(LF);
			w.append("      }").append(LF);
			w.append("      return $retval;").append(LF);
			w.append("   }").append(LF);
			
			// add is and get methods for state and error
			w.append("   public function isDirty() {").append(LF);
			w.append("       return $this->").append(dirtyvar).append(";").append(LF);
			w.append("   }").append(LF);
			w.append("   public function isLoaded() {").append(LF);
			w.append("       return $this->").append(loadedvar).append(";").append(LF);
			w.append("   }").append(LF);
			// use non standard name, as getError might be duplicated 
			// if a field named Error exists in the table.
			w.append("   public function errorMessage() {").append(LF);
			w.append("       return $this->").append(errorvar).append(";").append(LF);
			w.append("   }").append(LF);			

			w.append(getSmallSeparator());			
			
			// generalized get+set function for key+value pairs
			StringBuffer ifbit_set = new StringBuffer();
			StringBuffer ifbit_get = new StringBuffer();
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				ifbit_set.append("             if ($fieldname=='" + name + "') { $returnvalue = $this->set" + name +"($value); } ").append(LF);
				ifbit_get.append("             if ($fieldname=='" + name + "') { $returnvalue = $this->get" + name +"(); } ").append(LF);
			}
			w.append("   public function keyValueSet($fieldname,$value) {").append(LF);
			w.append("       $returnvalue = false;").append(LF);
			w.append("       if ($this->hasField($fieldname)) { ").append(LF);
			w.append("          try {").append(LF);
			w.append(ifbit_set);
			w.append("             $returnvalue = true;").append(LF);
			w.append("          }").append(LF);
			w.append("          catch (exception $e) { ;").append(LF);
			w.append("              $returnvalue = false;").append(LF);
			w.append("              throw new Exception('Field Set Error'.$e->getMessage()); ").append(LF);
			w.append("          }").append(LF);
			w.append("       } else { ").append(LF);
			w.append("          throw new Exception('No Such field'); ").append(LF);
			w.append("       }  ").append(LF);
			w.append("       return $returnvalue;").append(LF);
			w.append("   }").append(LF);
			w.append("   public function keyGet($fieldname) {").append(LF);
			w.append("       $returnvalue = null;").append(LF);
			w.append("       if ($this->hasField($fieldname)) { ").append(LF);
			w.append("          try {").append(LF);
			w.append(ifbit_get);
			w.append("          }").append(LF);
			w.append("          catch (exception $e) { ;").append(LF);
			w.append("              $returnvalue = null;").append(LF);
			w.append("          }").append(LF);
			w.append("       }").append(LF);
			w.append("       return $returnvalue;").append(LF);
			w.append("   }").append(LF);
			
			 
			 
			// --------------- get+set methods for each field
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
	 		    //w.append("/*").append(f.attrSet.toString()).append("*/").append(LF);
	 		    w.append("/*").append(f).append("*/").append(LF);
                //w.append("/*").append(DataLib.getDefaultValue(f)).append("*/").append(LF);
                //w.append("/*").append(DataTypeLib.getSqlType(f)).append("*/").append(LF);    
                // simple get method, just returns value.
				w.append("   public function get").append(name).append("() {").append(LF);
				w.append("       if ($this->"+name+"==null) { ").append(LF);
				w.append("          return null;").append(LF);
				w.append("       } else { ;").append(LF);
				w.append("          return trim($this->l_stripslashes($this->"+name+"));").append(LF);
				w.append("       }").append(LF);
				w.append("   }").append(LF);
				//
				// get method tests for valid input
				w.append("   public function set").append(name).append("($").append(name).append(") {").append(LF);
				String size = getFieldSize(f);
                String sqlType = DataTypeLib.getSqlType(f);     
				if (size.length()>0) { 
					String sizeName = nodeTableName + "::" + name.toUpperCase() + "_SIZE";
                    if (sqlType!=null && (sqlType.matches("^DECIMAL\\([0-9,]*\\)$") || sqlType.equals("INTEGER"))) {
  				  	  w.append("       if (strlen(preg_replace('/[^0-9]/','',$").append(name).append(")) > ").append(sizeName).append(") { ").append(LF);
					  w.append("           throw new Exception('Value has too many digits for the field length.');").append(LF);                        
					  w.append("       } ").append(LF);                    	
                    } else { 
				  	  w.append("       if (strlen($").append(name).append(") > ").append(sizeName).append(") { ").append(LF);
					  w.append("           throw new Exception('Value exceeds field length.');").append(LF);                        
					  w.append("       } ").append(LF);
                    } 
				}    
                         
                if (sqlType != null) {
                	String orBlank = " && trim(strval($"+ name+"))!='' ";
                	if (DataLib.isNotNull(f)) { 
                		orBlank = "";
                	}
                    if (sqlType.equals("INTEGER")) {
                         w.append("       $" + name + " = trim($"+name+");").append(LF); 
                         w.append("       if (!ctype_digit(strval($" + name +"))"+ orBlank+ ") {").append(LF); 
                         w.append("             throw new Exception(\"Value must be an integer\");").append(LF);
                         w.append("       }").append(LF);
                    }
                    if (sqlType.matches("^DECIMAL\\([0-9,]*\\)$")) {
                        w.append("       $" + name + " = trim($"+name+");").append(LF); 
                        w.append("       if (!is_numeric($").append(name).append(")"+orBlank+") {").append(LF); 
                        w.append("             throw new Exception(\"Value must be a number\");").append(LF);
                        w.append("       }").append(LF);
                   }                    
                }
				w.append("       $this->").append(name).append(" = $this->l_addslashes($").append(name).append(");").append(LF);
				w.append("       $this->").append(dirtyvar).append(" = true;").append(LF);
				w.append("   }").append(LF);
				
			}
			
			// generic method to get primary key value
			if (numberOfPrimaryKeys==1) { 
				w.append("   public function PK() { // get value of primary key ").append(LF);
				w.append("        $returnvalue = '';").append(LF);
				w.append("        $returnvalue .= $this->get"+primaryKey+"();").append(LF);
				w.append("        return $returnvalue;").append(LF);
				w.append("   }").append(LF);
			} 
			if (numberOfPrimaryKeys==0) { 
				w.append("   public function PK() { // get value of primary key ").append(LF);
				w.append("        $returnvalue = null;").append(LF);
				w.append("        // this table has no primary key defined").append(LF);
				w.append("        return $returnvalue;").append(LF);
				w.append("   }").append(LF);
			} 
			if (numberOfPrimaryKeys>1) {
				w.append("   // call PK() to get displayable values of PK fields, call PKArray to get fieldnames and values for manipulation. ").append(LF);
				w.append("   public function PK() { // get value of primary keys as comma separated list of values ").append(LF);
				w.append("        $returnvalue = '';").append(LF);
				w.append("        $comma = '';").append(LF);
				for (int i=0; i<numberOfPrimaryKeys; i++) { 
				    w.append("        $returnvalue .= $comma . $this->get"+primaryKeys.get(i).attrSet.getString("name")+"();").append(LF);
				    w.append("        $comma = ', ';").append(LF);
				}
				w.append("        return $returnvalue;").append(LF);
				w.append("   }").append(LF);
			} 			

			w.append("   public function PKArray() { // get name and value of primary key fields ").append(LF);
			w.append("        $returnvalue = array();").append(LF);
			for (int i=0; i<numberOfPrimaryKeys; i++) { 
			    w.append("        $returnvalue['"+primaryKeys.get(i).attrSet.getString("name")+"'] = $this->get"+primaryKeys.get(i).attrSet.getString("name")+"();").append(LF);    
			}
			w.append("        return $returnvalue;").append(LF);
			w.append("   }").append(LF);

			
			w.append("   public function NumberOfPrimaryKeyFields() { // returns the number of primary key fields defined for this table ").append(LF);
		    if (numberOfPrimaryKeys!=1) { 
			   w.append("        // Warning: PHP code generated by druid doesn't yet full support cases like this where other than one primary key is defined.").append(LF);
		    }
			w.append("        return "+ numberOfPrimaryKeys + ";").append(LF);
			w.append("   }").append(LF);

			return w.toString();
		}
	    @SuppressWarnings("unchecked")
		private String getPersist(TableNode node) { 
			StringBuffer w = new StringBuffer();
			
			// Generate CRUD methods (load, save, delete, etc) with database specific code
			Php_crud_generator specificMethods = null;
			try {
				System.out.println("sett.getNameDbType(): " + sett.getNameDbType());
				System.out.println(Settings.TYPE_DB_MYSQLI);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) { 
                    // TODO: Finish cleaing up mysql decorator.
                    // until then, change to mysqli
					// specificMethods = new Php_mysql_decorator(node, this);
                    sett.setNameDbType(Settings.TYPE_DB_MYSQLI);
				}
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
					specificMethods = new Php_mysqli_decorator(node, this);
					w.append(((Php_mysqli_decorator)specificMethods).getMySQLiTypesConstants());
				}				
				if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) { 
					specificMethods = new Php_oracle_decorator(node, this);
				}
				if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) { 
					specificMethods = new Php_dbo_decorator(node, this);
				}
				if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) { 
					specificMethods = new Php_postgres_decorator(node, this);
				}
			} catch (WrongDbTypeException e) {
				// End up here if the wrong class is invoked, or the class doesn't check for the right db type.
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Selected DB Type not provided by selected class.");
			}
			if (specificMethods==null) {
				System.out.println("Error: no db type name in sett. " + sett.getNameDbType());
			} else { 
				// basic create/retrieve/update/delete methods
			    w.append(specificMethods.getLoadMethod());  // retrieve
				w.append(getSmallSeparator());
			    w.append(specificMethods.getSaveMethod());  // create + update 
				w.append(getSmallSeparator());
			    w.append(specificMethods.getDeleteMethod()); // delete
				w.append(getSmallSeparator());
				// additional useful methods for table as a whole
			    w.append(specificMethods.getCountMethod());
				w.append(getSmallSeparator());
			    w.append(specificMethods.getLoadArrayKeyValueSearchMethod());
				w.append(getSmallSeparator());
				// related tables
				w.append(this.getLoadRelatedMethods(node));
				// Methods producing JSON output to support Dojo/Dijit pick list controls
				if (sett.isGenDojo()) {
			        w.append(specificMethods.getKeySelectAllConcatJSONMethod());
			        w.append(getSmallSeparator());
			        w.append(specificMethods.getkeySelectDistinctJSONMethod());
			        w.append(getSmallSeparator());
				}
			    // additional useful methods for finding data in indexed fields  
			    w.append(getSmallSeparator());
			    w.append("   // Each field with an index has a load array method generated for it.").append(LF);
			    w.append(specificMethods.getLoadArrayByMethods());
				w.append(getSmallSeparator());
				w.append("   // Each fulltext index has a load array method generated for it.").append(LF);
				w.append(specificMethods.getLoadArrayByFullTextMethods());
				w.append(getSmallSeparator());
				w.append("   // Each field with an index has a select distinct method generated for it.").append(LF);
				w.append(specificMethods.getSelectDistinctMethods());
			}
						

			w.append(getSmallSeparator());			
			
			// --------------------------- hasField() -----------------------------------------------
			
			// -------------- has field method
			w.append("   public function hasField($fieldname) {").append(LF);
			w.append("       $returnvalue = false;").append(LF);
      
	        w.append("       if (trim($fieldname)!='' && trim($fieldname)!=',') {").append(LF);
	        w.append("            if (strpos(self::").append(fieldlistvar).append(",\" $fieldname, \")!==false) { ").append(LF);
	        w.append("               $returnvalue = true;").append(LF);
	        w.append("            }").append(LF);
	        w.append("       }").append(LF);	        
	        w.append("       return $returnvalue;").append(LF);
	        w.append("    }");  // end function hasField
	  
	        
			w.append(getSmallSeparator());		
			
		
		return w.toString();
	}
	
	    /**
	     * Generate code to link the PHP object representing the table to related objects (tables)
	     * following primary key - foreign key relationships.  
	     * 
	     * @return StringBuffer containing PHP methods to link the object to related objects. 
	     */
	    public StringBuffer getLoadRelatedMethods(TableNode node) {
	        // -------------------------  Link to related tables --------------------------------
	        StringBuffer w = new StringBuffer();
	        String nodeTableName = getClassCodeName(node); 
	        // TODO: Not finished.
	        
	        w.append("// TODO: *************** link to related tables ").append(LF);
	        
	        // TODO: add methods to link out to related tables (fk in this table, and this table as fk in other table)
	        //
	        relatedTables = new StringBuffer();  // Make sure that relatedTables is initalized as empty.
	        // links to tables with this table's pk as a fk
	        Vector <TableNode>v = (Vector<TableNode>)DataLib.getReferences(node, false);  
	        Iterator <TableNode>iter   = v.iterator();
	        DatabaseNode db = node.getDatabase();
			while(iter.hasNext()) { 
				TableNode table = (TableNode)iter.next();
				String fTable = table.attrSet.getString("name");
				String fkField = "";
				for (int i = 0; i < table.getChildCount(); i++) {
					FieldNode f = (FieldNode) table.getChild(i);
					if (f.isFkey()) {				   
						Integer fkt = f.attrSet.getInt("refTable");
						TableNode fkTable = db.getTableByID(fkt);
						if (fkTable.attrSet.getString("name").equals(nodeTableName)) {
							fkField = f.attrSet.getString("name");
			    			// related tables will be used later if sett.isGenMVC() is true, indentation here reflects 
			    			// the getDetailsView() method that it generates.  See writeDatabaseLevelFiles();
							relatedTables.append("           $returnvalue .= \"<li>" + fTable + "</li>\";").append(LF);    				
							relatedTables.append("           $t_" + fTable + " = new " + fTable + "();").append(LF);  
					        relatedTables.append("           $res = $t_"+fTable+"->loadArrayKeyValueSearch(array(\"" + fkField + "\" => $model->get" + primaryKey + "()));").append(LF);
							relatedTables.append("           $t_" + fTable + "View = new " + fTable + "View();").append(LF);
							relatedTables.append("           foreach($res as $r) {").append(LF);
							relatedTables.append("               $t_" + fTable + "View->setModel($r);").append(LF);
							if (numberOfPrimaryKeys==1) { 
							    relatedTables.append("               $r->load($r->PK());").append(LF);
							} else { 
								relatedTables.append("               $r->load($r->PKArray());").append(LF);
							}
							relatedTables.append("               $returnvalue .= $t_" + fTable + "View->getDetailsView(false);").append(LF); 
					        relatedTables.append("           }").append(LF);
						}
					}
				}
				w.append("  public function loadLinkedFrom"+fTable+"() { ").append(LF);
				w.append("      // ForeignKey in: " + fTable).append(LF);
				w.append("      $t = new " + fTable + "();").append(LF);
				w.append("  } ").append(LF);
			}
	        
			
	        w.append(getSmallSeparator());
			

			// links to tables with a fk in this table
	        boolean hasFK = false;
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				if (f.isFkey()) {
					hasFK = true;
				}
			}
			if (hasFK) { 
				w.append("  public function loadLinkedTo() { ").append(LF);
				w.append("     $returnvalue = array(); ").append(LF);
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);	
					if (f.isFkey()) { 
						String name = f.attrSet.getString("name");
						w.append("       // fk: " + name).append(LF);
						//String fk = DataTypeLib.getTypeDef(f);
						//Integer fkf = f.attrSet.getInt("refField");				   
						Integer fkt = f.attrSet.getInt("refTable");
						TableNode fkTable = db.getTableByID(fkt);
						//FieldNode fkField = fkTable.getFieldByID(fkf);
						w.append("      $t = new " + fkTable.attrSet.getString("name") + "();").append(LF);
						w.append("      $t->load(get" + name + "());" ).append(LF);
						w.append("      $returnvalue[" + name + "] = $t;" ).append(LF);
						relatedTables.append("           $returnvalue .= \"<li>" + fkTable + "</li>\";").append(LF);
						relatedTables.append("           $t_" + fkTable + " = new " + getClassCodeName(fkTable) + "();").append(LF);
						relatedTables.append("           $t_" + fkTable + "View = new " + getClassCodeName(fkTable) + "View();").append(LF);
						relatedTables.append("           $t_" + fkTable + "View->setModel($t_" + fkTable + ");").append(LF);
				        relatedTables.append("           if ($model->get"+ name +"() != '') { ").append(LF);
				        relatedTables.append("               $t_" + fkTable + "->load($model->get"+ name +"());").append(LF);
				        relatedTables.append("               $returnvalue .= $t_" + fkTable + "View->getDetailsView(false);").append(LF);
				        relatedTables.append("           }").append(LF);	
					}
				}
				w.append("     return $returnvalue;").append(LF);
				w.append("  } ").append(LF);
			}    
			return w;
		}	    
	      
	    
	    private String getViewClass(TableNode node) {

	    	StringBuffer w = new StringBuffer();

	    	String className = getClassCodeName(node);
	    	w.append("}").append(LF).append(LF).append(LF);  // close the previous class
	    	w.append("// Write your own views by extending this class.").append(LF);
	    	w.append("// Place your extended views in a separate file and you").append(LF);
	    	w.append("// can use Druid to regenerate the " + node.attrSet.getString("name") + ".php file to reflect changes").append(LF);
	    	w.append("// in the underlying database without overwriting your custom views of the data.").append(LF);
	    	w.append("// ").append(LF);
	    	w.append("class " + className + "View implements viewer").append(LF);
	    	w.append("{").append(LF); 
	    	w.append("   var $model = null;").append(LF);
	    	w.append("   public function setModel($aModel) { ").append(LF);
	    	w.append("       $this->model = $aModel;").append(LF);
	    	w.append("   }").append(LF);
	    	// example show record as a unordered list view
	    	// includeRelated is used to prevent infinite loops
	    	w.append("   // @param $includeRelated default true shows rows from other tables through foreign key relationships.").append(LF);
	    	w.append("   // @param $editLinkURL default '' allows adding a link to show this record in an editing form.").append(LF);
	    	w.append("   public function getDetailsView($includeRelated=true, $editLinkURL='') {").append(LF);
	    	w.append("       $returnvalue = '<ul>';").append(LF);
	    	w.append("       $editLinkURL=trim($editLinkURL);").append(LF);
	    	w.append("       $model = $this->model;").append(LF);
	    	w.append("       $primarykeys = $model->schemaPK();").append(LF);
	    	w.append("       if ($editLinkURL!='') { ").append(LF);
	    	w.append("          if (!preg_match('/\\&$/',$editLinkURL)) { $editLinkURL .= '&'; } ").append(LF) ;
	    	w.append("          $nullpk = false; ").append(LF);
	    	w.append("          foreach ($primarykeys as $primarykey) { ").append(LF);
	    	w.append("              // Add fieldname=value pairs for primary key(s) to editLinkURL. ").append(LF);
	    	w.append("              $editLinkURL .= urlencode($primarykey) . '=' . urlencode($model->keyGet($primarykey));").append(LF);
	    	w.append("              if ($model->keyGet($primarykey)=='') { $nullpk = true; } ").append(LF); 
	    	w.append("          }").append(LF);
	    	w.append("          if (!$nullpk) { $returnvalue .= \"<li>" + className + " <a href='$editLinkURL'>Edit</a></li>\\n\";  } ").append(LF) ;
	    	w.append("       }").append(LF);
	    	for (int i = 0; i < node.getChildCount(); i++) {
	    		FieldNode f = (FieldNode) node.getChild(i);
	    		String name = f.attrSet.getString("name");
	    		w.append("       $returnvalue .= \"<li>\"." + className + "::").append(name.toUpperCase()).append(".\": \".$model->get").append(name).append("().\"</li>\\n\";").append(LF) ;
	    	}			
	    	if (!relatedTables.equals("")) {
	    		w.append("       if ($includeRelated) { ").append(LF);	
	    		w.append("           // note that $includeRelated is provided as false in calls out to").append(LF);
	    		w.append("           // related tables to prevent infinite loops between related objects.").append(LF);
	    		w.append(relatedTables).append(LF);
	    		w.append("        }").append(LF);
	    	}
	    	w.append("       $returnvalue .= '</ul>';").append(LF);
	    	w.append("       return  $returnvalue;").append(LF);			
	    	w.append("   }").append(LF); 
	    	// example show record as a json object view
	    	w.append("   public function getJSON() {").append(LF);
	    	w.append("       $returnvalue = '{ ';").append(LF);
	    	w.append("       $model = $this->model;").append(LF);
	    	String delimiter = ",";
	    	for (int i = 0; i < node.getChildCount(); i++) {
	    		if (i == node.getChildCount()-1) { delimiter = " }"; }  
	    		FieldNode f = (FieldNode) node.getChild(i);
	    		String name = f.attrSet.getString("name");
	    		w.append("       $returnvalue .= '\"'." + className + "::" + name.toUpperCase() + ".\': \"'.$model->get"+ name + "().'\"" + delimiter + "';").append(LF);
	    	}			
	    	w.append("       $returnvalue .= '</ul>';").append(LF);
	    	w.append("       return  $returnvalue;").append(LF);			
	    	w.append("   }").append(LF);	
	    	// example show record as a table row
	    	w.append("   public function getTableRowView() {").append(LF);
	    	w.append("       $returnvalue = '<tr>';").append(LF);
	    	w.append("       $model = $this->model;").append(LF);
	    	for (int i = 0; i < node.getChildCount(); i++) {
	    		FieldNode f = (FieldNode) node.getChild(i);
	    		String name = f.attrSet.getString("name");
	    		w.append("       $returnvalue .= \"<td>\".$model->get" +  name + "().\"</td>\\n\";").append(LF) ;
	    	}			
	    	w.append("       $returnvalue .= '</tr>';").append(LF);
	    	w.append("       return  $returnvalue;").append(LF);			
	    	w.append("   }").append(LF);
	    	// example show table row headers.
	    	w.append("   public function getHeaderRow() {").append(LF);
	    	w.append("       $returnvalue = '<tr>';").append(LF);
	    	for (int i = 0; i < node.getChildCount(); i++) {
	    		FieldNode f = (FieldNode) node.getChild(i);
	    		String name = f.attrSet.getString("name");
	    		w.append("       $returnvalue .= \"<th>\"." + className + "::"+name.toUpperCase()+".\"</th>\\n\";").append(LF) ;
	    	}			
	    	w.append("       $returnvalue .= '</tr>';").append(LF);
	    	w.append("       return  $returnvalue;").append(LF);			
	    	w.append("   }").append(LF);
	    	// editFormDojoView()
	    	// example ajaxian editing form using the dojotoolkit
	    	String script;
	    	String form;
	    	String button;
	    	if (sett.isGenDojo()) { 
	    		script = "       $id = trim($model->PK());" + LF +  			
	    		"       $feedback = \"status$id\";" + LF +  
	    		"       $formname = \"editform$id\";" + LF +  
	    		"       $script = '" + LF +  
	    		"  var saveprocessor'.$id.' = {" + LF +  
	    		"        form: dojo.byId(\"'.$formname.'\")," + LF +  
	    		"        url: \"ajax_handler.php\"," + LF +  
	    		"        handleAs: \"text\"," + LF +  
	    		"        load: function(data){" + LF +  
	    		"                dojo.byId(\"'.$feedback.'\").innerHTML = data;" + LF +  
	    		"        }," + LF +  
	    		"        error: function(data){" + LF +  
	    		"                dojo.byId(\"'.$feedback.'\").innerHTML = \"Error: \" + data;" + LF +  
	    		"                console.debug(\"Error: \", data);" + LF +  
	    		"        }," + LF +  
	    		"        timeout: 8000" + LF +  
	    		"  };" + LF +  
	    		"  function save'.$id.'() {" + LF +  
	    		"     dojo.byId(\"'.$feedback.'\").innerHTML = \"Saving...\";" + LF +  
	    		"     dojo.xhrGet(saveprocessor'.$id.');" + LF +  
	    		"  };" + LF +  
	    		"       ';" + LF;
	    		form = "       $returnvalue .= \"<form id='$formname' name='$formname' dojoType='dijit.form.Form'>\";";
	    		button = "       $returnvalue .= \"<li><input name=save id=save type=button value='Save' onclick='$script  save$id();'><div id='$feedback'></div></li>\";";


	    		w.append("   public function getEditFormDojoView($includeRelated=true) {").append(LF);
	    		// TODO: determine if this is a surrogate numeric primary key, treat PK element on form accordingly, and display form as edit or add.
	    		w.append("       $model = $this->model;").append(LF);
	    		if (numberOfPrimaryKeys==1) { 
	    			w.append("       if ($model->PK()=='') { $addform=true; } else { $addform=false; } ").append(LF);
	    		}
	    		w.append("       $returnvalue = '';").append(LF);
	    		w.append(script);
	    		w.append(form).append(LF);
	    		w.append("       $returnvalue .= '<input type=hidden name=druid_action id=druid_action value=save>';").append(LF);
	    		w.append("       $returnvalue .= '<input type=hidden name=druid_table id=druid_action value=\"" + className + "\">';").append(LF);
	    		w.append("       $returnvalue .= \"<div id='div_$formnane' >\";").append(LF);
	    		for (int i = 0; i < node.getChildCount(); i++) {
	    			FieldNode f = (FieldNode) node.getChild(i);
	    			String name = f.attrSet.getString("name");
	    			String fname = className + "::" + name.toUpperCase();
	    			String sqlType = DataTypeLib.getSqlType(f);  
	    			String regex = "";
	    			String errorMessage = "";
	    			String dojoType = " dojoType='dijit.form.ValidationTextBox' ";
	    			if (sqlType != null) { 
	    				if (sqlType.equals("INTEGER")) { 
	    					regex = " regExp='^[0-9-]*' ";
	    					errorMessage = " invalidMessage='The value entered is not valid.  It must be an integer.'";
	    				}
	    				if (sqlType.matches("^DECIMAL\\([0-9,]*\\)$")) {
	    					regex = " regExp='^[0-9\\.-]*' ";
	    					errorMessage = " invalidMessage='The value entered is not valid.  It must be a number.' ";
	    				}
	    			}	
	    			String size;
	    			// TODO: Assumes single key is surrogate numeric primary key.
	    			if (numberOfPrimaryKeys==1 && DataLib.isPrimaryKey(f)) {
	    				w.append("       if($addform) { ").append(LF);
	    				w.append("          $returnvalue .= \"Add a new " + name +  "\"; ").append(LF);
	    				w.append("       } else { ").append(LF);
	    				w.append("  ");  // indent field line
	    			}
	    			String maxlength = " maxlength='\"."+className + "::" + name.toUpperCase() + "_SIZE .\"' ";
	    			if (Integer.parseInt(this.getFieldSize(f)) > FORM_INPUT_SIZE_MAXIMUM) { 
	    				size = " style=' width:" + FORM_INPUT_SIZE_MAXIMUM + "em; border:1px solid grey; ' ";
	    				dojoType = " dojoType='dijit.form.Textarea' ";
	    				w.append("       $returnvalue .= \"<div><label for=\"." + fname + ".\">" + name.toUpperCase() + "</label><textarea " + dojoType + size +  " name=\"." + fname + ".\" id=\"." + fname + ".\" >\".$model->get"+ name +"().\"</textarea></div>\\n\";").append(LF) ;

	    			} else { 
	    				size = " style=' width:\"."+className + "::" + name.toUpperCase() + "_SIZE .\"em;  ' ";
	    				w.append("       $returnvalue .= \"<div><label for=\"." + fname + ".\">" + name.toUpperCase() + "</label><input " + dojoType + regex + errorMessage +  " name=\"." + fname + ".\" id=\"." + fname + ".\" value='\".$model->get"+ name +"().\"' " + size + maxlength + "></div>\\n\";").append(LF) ;
	    			}    
	    			if (numberOfPrimaryKeys==1 && DataLib.isPrimaryKey(f)) {
	    				w.append("       }  ").append(LF);
	    			}
	    		}	
	    		//TODO: foreign key relations as filtering selects
	    		if (!relatedTables.equals("")) {
	    			w.append("       if ($includeRelated) { ").append(LF);	
	    			w.append("           // note that $includeRelated is provided as false in calls out to").append(LF);
	    			w.append("           // related tables to prevent infinite loops between related objects.").append(LF);
	    			//w.append(relatedTables).append(LF);
	    			w.append("        }").append(LF);
	    		}
	    		w.append(button).append(LF);
	    		w.append("       $returnvalue .= '</div>';").append(LF);
	    		w.append("       $returnvalue .= '</form>';").append(LF);
	    		if (numberOfPrimaryKeys==1) {
	    			w.append("       if(!$addform) { ").append(LF);	
	    			w.append("          // show delete button if editing an existing record ").append(LF);			
	    		}
	    		// delete button
	    		script = "       $id = trim($model->PK());" + LF +  			
	    		"       $feedback = \"deletestatus$id\";" + LF +  
	    		"       $formname = \"deleteform$id\";" + LF +  
	    		"       $script = '" + LF +  
	    		"  var deleteprocessor'.$id.' = {" + LF +  
	    		"        form: dojo.byId(\"'.$formname.'\")," + LF +  
	    		"        url: \"ajax_handler.php\"," + LF +  
	    		"        handleAs: \"text\"," + LF +  
	    		"        load: function(data){" + LF +  
	    		"                dojo.byId(\"'.$feedback.'\").innerHTML = data;" + LF +  
	    		"        }," + LF +  
	    		"        error: function(data){" + LF +  
	    		"                dojo.byId(\"'.$feedback.'\").innerHTML = \"Error: \" + data;" + LF +  
	    		"                console.debug(\"Error: \", data);" + LF +  
	    		"        }," + LF +  
	    		"        timeout: 8000" + LF +  
	    		"  };" + LF +  
	    		"  function deleterecord'.$id.'() {" + LF +  
	    		"     dojo.byId(\"'.$feedback.'\").innerHTML = \"Deleting...\";" + LF +  
	    		"     dojo.xhrGet(deleteprocessor'.$id.');" + LF +  
	    		"  };" + LF +  
	    		"       ';" + LF;
	    		w.append(script);
	    		w.append("       $returnvalue .= \"<form id='$formname' name='$formname' dojoType='dijit.form.Form'>\";").append(LF);
	    		w.append("       $returnvalue .= '<input type=hidden name=druid_action id=druid_action value=delete>';").append(LF);
	    		w.append("       $returnvalue .= '<input type=hidden name=druid_table id=druid_table value=\"" + className + "\">';").append(LF);
	    		w.append("       $returnvalue .= '<input type=hidden name="+primaryKey+" id=druid_table value=\"'.$id.'\">';").append(LF);
	    		w.append("       $returnvalue .= '<ul>';").append(LF);
	    		w.append("       $returnvalue .= \"<li><input name=delete id=save type=button value='Delete' onclick='$script  deleterecord$id();'><div id='$feedback'></div></li>\";").append(LF);
	    		w.append("       $returnvalue .= '</ul>';").append(LF);
	    		w.append("       $returnvalue .= '</form>';").append(LF);
	    		if (numberOfPrimaryKeys==1) {
	    			w.append("       } ").append(LF);					
	    		}
	    		w.append("       return  $returnvalue;").append(LF);			
	    		w.append("   }").append(LF); 
	    	} 
	    	script = "";
	    	form = "       $returnvalue = '<form method=get action=druid_handler.php>';";
	    	button = "       $returnvalue .= '<li><input type=submit value=\"Save\"></li>';";

	    	w.append("   public function getEditFormView($includeRelated=true) {").append(LF);
	    	w.append("       $model = $this->model;").append(LF);
	    	w.append(script);
	    	w.append(form).append(LF);
	    	w.append("       $returnvalue .= '<input type=hidden name=druid_action id=druid_action value=save>';").append(LF);
	    	w.append("       $returnvalue .= '<input type=hidden name=druid_table id=druid_action value=\"" + className + "\">';").append(LF);
	    	w.append("       $returnvalue .= '<ul>';").append(LF);
	    	for (int i = 0; i < node.getChildCount(); i++) {
	    		FieldNode f = (FieldNode) node.getChild(i);
	    		String name = f.attrSet.getString("name");
	    		String fname = className + "::" + name.toUpperCase();
	    		String size;
	    		if (Integer.parseInt(this.getFieldSize(f)) > FORM_INPUT_SIZE_MAXIMUM) { 
	    			size = " size='" + FORM_INPUT_SIZE_MAXIMUM + "' ";					
	    		} else { 
	    			size = " size='\"."+className + "::" + name.toUpperCase() + "_SIZE .\"' ";
	    		} 
	    		String maxlength = " maxlength='\"."+className + "::" + name.toUpperCase() + "_SIZE .\"' ";
	    		w.append("       $returnvalue .= \"<li>" + name.toUpperCase() + "<input type=text name=\"." + fname + ".\" id=\"." + fname + ".\" value='\".$model->get"+ name +"().\"' " + size + maxlength + "></li>\\n\";").append(LF) ;
	    	}			
	    	if (!relatedTables.equals("")) {
	    		w.append("       if ($includeRelated) { ").append(LF);	
	    		w.append("           // note that $includeRelated is provided as false in calls out to").append(LF);
	    		w.append("           // related tables to prevent infinite loops between related objects.").append(LF);
	    		//w.append(relatedTables).append(LF);
	    		w.append("        }").append(LF);
	    	}
	    	w.append(button).append(LF);
	    	w.append("       $returnvalue .= '</ul>';").append(LF);
	    	w.append("       $returnvalue .= '</form>';").append(LF);			
	    	w.append("       return  $returnvalue;").append(LF);			
	    	w.append("   }").append(LF); 

	    	// Note: close the last function, but not the last class (added in footer method).
	    	return w.toString();
	    }
	
	@SuppressWarnings("unchecked")
	protected void writeDatabaseLevelFiles(Logger l, DatabaseNode dbNode) { 	
		String outputDir = dbNode.modsConfig.getValue(this, "output");
		
		String separator = "";
		if (!outputDir.endsWith(System.getProperty("file.separator"))) { separator = System.getProperty("file.separator") ; } 

		// sett.isGenVaria() || sett.isGenPersi() || sett.isGenMVC() 
		       // (interface model)
		// sett.isGenPersi() || sett.isGenPersi()   
		       // (interfaces summarymodel, loadablemodel, savablemodel)
		// sett.isGenMVC() 
		       // (interface viewer) 
		
		l.log(Logger.INFO, "Creating interfaces file...");
		StringBuffer interfaces = new StringBuffer("<?php " + LF +
				"// " + DRUID_INTERFACES_PHP  + LF + 
				"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
				"//" + LF + LF);
		if (sett.isGenVaria() || sett.isGenPersi() || sett.isGenMVC()) {  
			// (interface model)		
			interfaces.append(LF + 
					"interface model {" + LF +
					"   public function keyValueSet($fieldname,$value); // set the value of a field to specified value for the current instance of model." + LF +
					"   public function keyGet($fieldname); // gets the value of the specified field in the current instance of model." + LF +
					"}"+ LF );
		}
		if (sett.isGenPersi() || sett.isGenMVC()) {  
			// (interfaces summarymodel, loadablemodel, savablemodel)
			interfaces.append(LF + 		
					"interface loadableModel {" + LF +
					"   public function load($pk);  // load data for the record specified by the primary key value $pk into the current instance of model." + LF +
					"   public function isLoaded(); // returns true if model instance contains data loaded from record in table. " + LF +
					"   public function isDirty();  // returns true if model data has changed since instantiation or load. " + LF +
					"   public function loadArrayKeyValueSearch($searchTermArray); // returns an array of models matching the fieldname=value criteria in the searchTermArray"+ LF +		
					"   public function NumberOfPrimaryKeyFields(); // returns the number of fields in the primary key of this table" + LF + 
					"   public function PKArray(); // return primary key(s) for current record as fieldname=>value array" + LF + 
					"}"+ LF +
					"interface saveableModel {" + LF +
					"   public function save();    // save changes to current record or add new record to underlying database." + LF +
					"   public function delete();  // delete current record from underlying database." + LF + 
					"}"+ LF +
					"interface summaryModel { " + LF +
					"   public function count();  // returns total number of records in table" + LF + 
					"   public function keySelectDistinct($fieldname,$startline,$link,$endline,$includecount=false,$orderbycount=false);  // " + LF + 
					"                           // return distinct values for $fieldname, if it has an index (if it is in schemaHaveDistinct())." + LF +
					"                           // generic wrapper for selectDistinct{fieldname}() methods." + LF +
					"}" + LF +    
					"interface tableSchema { " + LF +
					"   public function hasField($fieldname);  // returns true if the model includes a field with a name exactly matching $fieldname."+ LF +
					"   public function schemaPK();  // returns array of primary key field names" + LF +
					"   public function schemaHaveDistinct();  // returns array of field names for which selectDistinct{fieldname} methods are available." + LF + 
					"   public function schemaFields();  // returns array of all field names" + LF + 
					"" + LF + 
					"}" + LF +   		
					"" + LF);
		}
		if (sett.isGenMVC()) {  
			// (interfaces summarymodel, loadablemodel, savablemodel)
			interfaces.append(LF + 	 
					"interface viewer {" + LF +
					"   public function setModel($aModel);  // specify which instance of model provides the data to be shown in this view."+LF +
					"   public function getDetailsView($includeRelated=true, $editLinkURL=''); // display the fields and values of the instance of the model (as a html list in default implementation)." + LF +
					"}"+ LF + LF);
		}
		interfaces.append(LF + 	"?>");
		writeFileEvenIfExists(outputDir + separator + DRUID_INTERFACES_PHP, interfaces.toString(), l);
		
		
		if(sett.isGenFrame()) { 
		l.log(Logger.INFO, "Creating ajax hander file...");
		String handler = "<?php" +  LF + 
		"// " + DRUID_HANDLER_PHP +  LF +  
		"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
		"//" +  LF + 
		" " +  LF +
		"include_once('druid_classes.php');" +  LF + 
		" " +  LF +
		"$response = '';" +  LF + 
		" " +  LF + 
		"$debug = false;" +  LF + 
		"$action = '';" + LF +
		"$table = '';" + LF + 
		"@$action = substr(preg_replace('/[^a-z]/','',$_GET['druid_action']),0,20);" +  LF + 
		"@$table = substr(preg_replace('/[^a-z0-9A-Z\\_]/','',$_GET['druid_table']),0,50);" +  LF +
		"" +  LF + 
		"switch ($action) {" +  LF + 
		"   case 'save':" +  LF +
		"      $failed = false;"+  LF +
		"      $errors = '';"+  LF + 
		"      $schema = new database_schema();" +  LF + 
		"      if ($schema->hasTable($table)) { " +  LF + 
		"          $t = $schema->getClass($table);" +  LF +
	    "          foreach ($_GET as $key => $value ) { " + LF +
	    "              if ($t->hasField($key)) { " + LF +
	    "                 try {" + LF + 
	    "                     $ok = $t->keyValueSet($key,$value);" + LF +
	    "                 } catch (Exception $e) { " + LF +
	    "                     $failed = true;" + LF +
	    "                     $errors .= \" Field:$key [\". $e->getMessage(). '] ';" + LF +
	    "                 }" + LF + 
	    "              }" + LF +
	    "           }" + LF +
	    "           $ok = $t->save();" + LF +
	    "           if (!$ok || $failed===true) { " + LF + 
	    "               $response .= '<strong class=error>Error:</strong> '.$errors.' ';" + LF +
	    "               $response .= $t->errorMessage();" + LF +
	    "           } else { " + LF +	
	    "               $response .= '<strong class=ok>Saved</strong>';" + LF +
	    "           }  " + LF +	
		"      }" +  LF + 
		" " +  LF + 
		"      break;" +  LF + 
		"   case 'delete':" +  LF +
		"      $schema = new database_schema();" +  LF + 
		"      if ($schema->hasTable($table)) { " +  LF + 
		"          $t = $schema->getClass($table);" +  LF +
		"          $error = '';" + LF + 
	    "          foreach ($_GET as $key => $value ) { " + LF +
	    "              if ($t->hasField($key)) { " + LF +
	    "                 try { " + LF + 
	    "                     $ok = $t->keyValueSet($key,$value);" + LF +
	    "                 } catch (Exception $e) { " + LF + 
	    "                     $error .= $key . ': '. $e->getMessage() . ' ';" + LF + 
	    "                 }" + LF + 	    
	    "              }" + LF +
	    "           }" + LF +
	    "           $ok = $t->delete();" + LF +
	    "           if (!$ok) { " + LF + 
	    "               $response .= '<strong class=error>Error: Delete Failed</strong> '.$error.' ';" + LF +
	    "               $response .= $t->errorMessage();" + LF +
	    "           } else { " + LF +	
	    "               $response .= '<strong class=ok>Deleted</strong>';" + LF +
	    "           }  " + LF +	
		"      } else { " +  LF + 
	    "          $response .= '<strong class=error>Error: No Such Table</strong>';" + LF +
		"      }  " +  LF +		
		" " +  LF + 
		"      break;" +  LF + 
		"   case 'savesingle':" +  LF + 
		"      // test call: " + DRUID_HANDLER_PHP + "?action=savesingle&table=COLLECTION_LOCALITIES&field=COUNTRY&key=29856&value=Uganda" +  LF + 
		"      $ok = false;" +  LF + 
		"      $table = ''; " +  LF + 
		"      $key = '';" +  LF + 
		"      $field = '';" +  LF + 
		"      $value = '';" +  LF + 
		"      $uniqueid = '';" +  LF + 
		"      $controltype = '';" +  LF + 
		"      @$table = substr(preg_replace('/[^A-Za-z0-9_\\.]/','',$_GET['table']),0,40); // name of the table to update" +  LF + 
		"      @$key = substr(preg_replace('/[^0-9\\,A-Za-z0-9\\.\\;]/','',$_GET['key']),0,15);      // value of the  primary key in the table indicating the row to update" +  LF +
		"      // or a semicolon separated list of fieldname comma value pairs in cases of multiple field primary keys. " + LF +
		"      // Note: this will fail in cases where either semicolon or comma is a valid character in a primary key field." + LF +
		"      // Note: the list of allowed values for the content of a primary key may be too restricted. " + LF + 
		"      @$field= substr(preg_replace('/[^A-Za-z0-9_]/','',$_GET['field']),0,40);  // field to update in table" +  LF + 
		"      @$value = substr(preg_replace('/[^A-Z0-9a-zèéöòóàáùúü&\\>\\<\\.\\,\\?\\ \\_\\(\\)\\[\\]\\-\\/\\:\\'\\+\\=]/','',$_GET['value$uniqueid']),0,4000);  // new value for field in row with primary key = value" +  LF + 
		"      @$controltype = substr(preg_replace('/[^A-Za-z]/','',$_GET['controltype']),0,15);  // control type - to identify checkbox data that doesn't match values to send to db." +  LF + 
		"" +  LF + 
		"      if ($debug) { echo '[$table][$key][$field][$value][$uniqueid]'; } " +  LF + 
		"      // check that table is on allowed list" +  LF + 
		"      $schema = new database_schema(); " + LF +
		"      $error = '';" + LF +
		"      if ($schema->hasTable($table)) { " +  LF + 
		"         // we know table is an allowed target" +  LF + 
		"         if ($key!='') { " +  LF + 
		"            $t = $schema->getClass($table);" + LF + 
		"            if (strpos($key,';')>0) { " + LF +
		"                // multiple fieldname,value pairs " + LF + 
		"                $kv = explode($key,';');" + LF +
		"                $kvpairs = array();" + LF +
		"                for ($x=0;$x<length($kv); $x++) { " + LF +
		"                    $kvparts = explode($kv[$x]); " + LF +
		"                    $kvpairs[$kvparts[0]] = $kvparts[1];" + LF +
		"                }" + LF +
		"                $t->loadPKArray($kvparts);" + LF + 
		"            } else if (strpos($key,'.')>0) { " + LF +
		"                // single fieldname,value pair " + LF + 
		"                $kv = explode($key,',',1);" + LF +
		"                $t->load($kv[1]);" + LF + 
		"            } else { " + LF + 
		"                // we know that key is a single field" +  LF +
		"                $t->load($key);" + LF + 
		"            }" + LF + 
		"            $hasfield = false;" +  LF + 
		"            $keyfield = '';" +  LF + 
		"            try { " + LF + 
		"               $t->keyValueSet($field,$value);" + LF +
		"               $t->save();" + LF +
		"               $ok = true;" +  LF + 
		"            } catch (Exception $e) { "  + LF +
		"               $ok = false;" +  LF +
		"               $error = $e->getMessage();" +  LF + 
		"            }"  + LF + 
		"         }" +  LF + 
		"      }" +  LF + 
		"      if ($ok) { " +  LF + 
		"         $response = '<strong class=ok>Saved</strong>';   " +  LF + 
		"      } else {" +  LF + 
		"         $response = '<strong class=error>Failed</strong> '.$error;   " +  LF + 
		"      }" +  LF + 
		"      break;" +  LF + 
	    "   case 'returnfkjson': " +  LF + 
		"      // test call: druid_handler.php?action=returnfkjson&table=COLLECTION_LOCALITIES" +  LF +
		"      // use with dojo FilteringSelect" + LF +
		"      //<div dojoType='dojo.data.ItemFileReadStore' jsId='LocalityStore' url='ajax_handler.php?druid_action=returnfkjson&table=Locality'></div>" + LF +
		"      //Change Locality<input type=text name=tempLocality id=tempLocality dojoType='dijit.form.FilteringSelect' store='LocalityStore' onChange=\" dojo.byId('Locality').value=arguments[0]; \" searchAttr='fields' >" + LF + 
        "      //<input type=hidden name=Locality id=Locality >;" + LF + 
		"      $ok = false;" +  LF + 
		"      $table = ''; " +  LF + 
		"      $key = '';" +  LF + 
		"      $value = '';" +  LF + 
		"      @$table = substr(preg_replace('/[^A-Za-z0-9_\\.]/','',$_GET['table']),0,40); // name of the table to update" +  LF + 
		"      if ($debug) { echo \"[$table]\"; }" +  LF +  
		"      // check that table is on allowed list" +  LF + 
		"      $schema = new database_schema(); " +  LF + 
		"      $error = '';" +  LF + 
		"      if ($schema->hasTable($table)) {" +  LF +  
		"         // we know table is an allowed target" +  LF + 
		"         $t = $schema->getClass($table);" +  LF + 
		"         try { " +  LF + 
		"             $values = $t->keySelectAllConcatJSON('ASC');" +  LF +
		"             $ok = true;" +  LF + 
		"         } catch (Exception $e) { " +  LF + 
		"             $ok = false;" +  LF + 
		"         }" +  LF + 
		"      }" +  LF +
		"      //header(\"Content-type application/json\");" + LF + 
		"      header(\"Content-type text/json-comment-filtered\");" + LF + 
		"      if ($ok) {" +  LF +  
		"         // identifier (id) needs to be provided in values, will be used as the selected value"  +LF + 
		"         // specified searchAttr als needs to be provided in values, will be used as the displayed value"  +LF + 
		"         $response = '{ \"identifier\":\"id\", \"items\": [ '.$values.' ] }';" +  LF +    
		"      } else {" +  LF + 
		"         $response = '{ }';" +  LF +    
		"      }" +  LF + 
		"      break;	" +  LF + 
	    "   case 'returndistinctjson': " +  LF + 
		"      // test call: druid_handler.php?action=returndistinctjson&table=COLLECTION_LOCALITIES&field=COUNTRY" +  LF + 
		"      // use with dojo dijit.form.ComboBox to generate a pick list" + LF + 
		"      $ok = false;" +  LF + 
		"      $table = ''; " +  LF + 
		"      $key = '';" +  LF + 
		"      $field = '';" +  LF + 
		"      $value = '';" +  LF + 
		"      $uniqueid = '';" +  LF + 
		"      $controltype = '';" +  LF + 
		"      @$table = substr(preg_replace('/[^A-Za-z0-9_\\.]/','',$_GET['table']),0,40); // name of the table to update" +  LF + 
		"      @$field= substr(preg_replace('/[^A-Za-z0-9_]/','',$_GET['field']),0,40);  // field to update in table" +  LF + 
		"      " +  LF + 
		"      if ($debug) { echo \"[$table][$field]\"; }" +  LF +  
		"      // check that table is on allowed list" +  LF + 
		"      $schema = new database_schema(); " +  LF + 
		"      $error = '';" +  LF + 
		"      if ($schema->hasTable($table)) {" +  LF +  
		"         // we know table is an allowed target" +  LF + 
		"            $t = $schema->getClass($table);" +  LF + 
		"            if ($t->hasField($field)) { " +  LF + 
		"            try { " +  LF + 
		"               $values = $t->keySelectDistinctJSON($field);" +  LF +
		"               $ok = true;" +  LF + 
		"            } catch (Exception $e) { " +  LF + 
		"               $ok = false;" +  LF + 
		"            }" +  LF + 
		"         }" +  LF + 
		"      }" +  LF +
		"      //header(\"Content-type application/json\");" + LF + 
		"      header(\"Content-type text/json-comment-filtered\");" + LF + 
		"      if ($ok) {" +  LF +  
		"         $response = '{ \"identifier\":\"'.$field.'\", \"items\": [ '.$values.' ] }';" +  LF +    
		"      } else {" +  LF + 
		"         $response = '{ }';" +  LF +    
		"      }" +  LF + 
		"      break;	" +  LF + 	
	    "   case 'returndistinctjsonlimited': " +  LF + 
		"      // test call: druid_handler.php?action=returndistinctjsonlimited&table=COLLECTION_LOCALITIES&field=COUNTRY" +  LF + 
		"      // use with dojo dijit.form.ComboBox to generate a pick list" + LF + 
		"      $ok = false;" +  LF + 
		"      $table = ''; " +  LF + 
		"      $key = '';" +  LF + 
		"      $field = '';" +  LF + 
		"      $value = '';" +  LF + 
		"      $uniqueid = '';" +  LF + 
		"      $controltype = '';" +  LF + 
		"      @$table = substr(preg_replace('/[^A-Za-z0-9_\\.]/','',$_GET['table']),0,40); // name of the table to update" +  LF + 
		"      @$field= substr(preg_replace('/[^A-Za-z0-9_]/','',$_GET['field']),0,40);  // field to update in table" +  LF + 
		"      @$limit= substr(preg_replace('/[^A-Za-z0-9_]/','',$_GET['name']),0,40);  // query limit condition" +  LF + 
		"      " +  LF + 
		"      if ($debug) { echo \"[$table][$field][$name]\"; }" +  LF +  
		"      // check that table is on allowed list" +  LF + 
		"      $schema = new database_schema(); " +  LF + 
		"      $error = '';" +  LF + 
		"      if ($schema->hasTable($table)) {" +  LF +  
		"         // we know table is an allowed target" +  LF + 
		"            $t = $schema->getClass($table);" +  LF + 
		"            if ($t->hasField($field)) { " +  LF + 
		"            try { " +  LF + 
		"               $values = $t->keySelectDistinctJSONLimit($field,$limit);" +  LF +
		"               $ok = true;" +  LF + 
		"            } catch (Exception $e) { " +  LF + 
		"               $ok = false;" +  LF + 
		"            }" +  LF + 
		"         }" +  LF + 
		"      }" +  LF +
		"      //header(\"Content-type application/json\");" + LF + 
		"      header(\"Content-type text/json-comment-filtered\");" + LF + 
		"      if ($ok) {" +  LF +  
		"         $response = '{ \"identifier\":\"name\", \"label\":\"name\" \"items\": [ '.$values.' ] }';" +  LF +    
		"      } else {" +  LF + 
		"         $response = '{ }';" +  LF +    
		"      }" +  LF + 
		"      break;	" +  LF + 			
		"}" +  LF + 
		"" +  LF + 
		"echo $response;" +  LF + 
		" " +  LF + 
		"?>";
		
		writeFileEvenIfExists(outputDir + separator + DRUID_HANDLER_PHP, handler, l);
		}
		
		// used to generate unit tests and database level class files 
		Vector <TableNode>tables = (Vector<TableNode>)dbNode.getObjects(TableNode.class);
		String tablename;
		
		if (sett.isGenTests()) {  
			l.log(Logger.INFO, "Generating unit testing files..."); 

			// Create test/ subdirectory if it doesn't exist.
			createDirs(outputDir);

			// Updating to simpletest 1.1
			String all_tests = "<?php " + LF +
			"// " + DRUID_ALL_TESTS_PHP + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
			"//" + LF + 
			"// Run unit tests on "  + dbNode.attrSet.getString("name") + " class files " + LF +  
			"// using the simpletest unit testing framework for PHP." + LF  +LF +  
			"//	Load simpletest files *********** " + LF +
			"//	The simpletest files need to be on PHP's include path. " + LF + 
			"require_once(\"autorun.php\");" + LF +
			"//	run test cases ****************** " + LF +  LF + 
			"$test = new TestSuite('All tests on "+ dbNode.attrSet.getString("name") + " Code');" + LF + 
			"$test->addFile('" + DRUID_TEST_CLASSES_PHP_RAW + "');" + LF +
			"if (file_exists('test_classes.php')) { " + LF +
			"    $test->addFile('test_classes.php');" + LF +
			"}" + LF +
			"//$test->addFile('your_test_classes.php');" + LF + 
			"if (@$_SERVER['argc']==0) { " + LF + 
			"   $test->run(new HtmlReporter());" + LF + 
			"} else { " + LF + 
			"   $test->run(new TextReporter());" + LF + 
			"}" + LF  + LF + 
			"?>" + LF;
			writeFileIfNotExists(outputDir + separator + DRUID_ALL_TESTS_PHP, all_tests, l);
		

			// for each table
			// test constructor state:  isLoaded() = false, isDirty()=false, errorMessage()=''
			StringBuffer tests = new StringBuffer();
			StringBuffer hasTableTests = new StringBuffer();
			StringBuffer getClassTests = new StringBuffer();
			tests.append("// test table level classes").append(LF);
			for (int i = 0; i < tables.size(); i++) {
				TableNode node = tables.elementAt(i);
				tablename = getClassCodeName(node);
				// tests to be added to test_of_database_schema below.
				hasTableTests.append("     $this->assertTrue($d->hasTable('"+tablename+"'));").append(LF);
				getClassTests.append("     $t = new "+tablename+";").append(LF);
				getClassTests.append("     $this->assertNotNull($d->getClass('"+tablename+"'));").append(LF);
				getClassTests.append("     $this->assertTrue($d->getClass('"+tablename+"')==$t);").append(LF);
				// create test classes for each table, with tests of constructor and set methods for each field
				tests.append("class testOf_"+tablename+ " extends UnitTestCase { ").append(LF);
				tests.append("    function testConstructor() { ").append(LF);
				tests.append("       $t = new  " + tablename + "();").append(LF);
				tests.append("       $this->assertFalse($t->isLoaded());").append(LF);
				tests.append("       $this->assertFalse($t->isDirty());").append(LF);
				tests.append("       $this->assertIdentical($t->errorMessage(),'');").append(LF);
				ArrayList<FieldNode> primaryKeys = getPrimaryKeyFieldNodes(node);
				// each primary key in newly constructed instance should be null
				for (int k=0; k<primaryKeys.size(); k++) { 
				   tests.append("       $this->assertNull($t->get"+primaryKeys.get(k).attrSet.getString("name")+"());").append(LF);	
				}
				// test load() throwing exception.
				tests.append("    } ").append(LF);
				
				tests.append("    function testKeys() { ").append(LF);
				tests.append("       $t = new  " + tablename + "();").append(LF);
				tests.append("       $pk = $t->PKArray();").append(LF);
				tests.append("       $this->assertIdentical(count($pk),"+primaryKeys.size()+");").append(LF);
				for (int k=0; k<primaryKeys.size(); k++) { 
				      tests.append("       $t->set"+primaryKeys.get(k).attrSet.getString("name")+"('"+k+"');").append(LF);
				}
				tests.append("       $pk = $t->PKArray();").append(LF);
				for (int k=0; k<primaryKeys.size(); k++) { 
				     tests.append("       $this->assertIdentical($pk['"+primaryKeys.get(k).attrSet.getString("name")+"'], '"+k+"');").append(LF);
				}
				tests.append("    } ").append(LF); 
				
				for (int j = 0; j < node.getChildCount(); j++) {
					FieldNode f = (FieldNode) node.getChild(j);
					String sqlType = DataTypeLib.getSqlType(f);  
					String setStringTypeTest = "         $this->assertTrue($t->isDirty());";
					if (sqlType != null) { 
						if (sqlType.equals("INTEGER") || sqlType.matches("^DECIMAL\\([0-9,]*\\)$")) {   
							setStringTypeTest = "          $this->fail('Failed to throw exception on storing a character to an integer field.');";
						}
						if (sqlType.equals("VARCHAR")) {
							setStringTypeTest = "          $this->assertTrue($t->isDirty());";
						}
					}
					String fieldname = f.attrSet.getString("name");
					tests.append("    function testDirty"+fieldname+"() { ").append(LF);
					tests.append("       // FieldType: " + sqlType ).append(LF);
					tests.append("       $t = new  " + tablename + "();").append(LF);
					tests.append("       $this->assertFalse($t->isDirty());").append(LF);
					tests.append("       try { ").append(LF);
					tests.append("          $t->set"+fieldname+"('1');").append(LF);
					tests.append("          $this->assertTrue($t->isDirty());").append(LF);
					tests.append("       } catch (Exception $e) { ").append(LF);
					tests.append("          $this->assertFalse($t->isDirty());").append(LF);
					tests.append("       } ").append(LF);
					String testString = "A";
					tests.append("       $t = new  " + tablename + "();").append(LF);
					tests.append("       try { ").append(LF);
					tests.append("          $t->set"+fieldname+"('"+testString+"');").append(LF);
					tests.append(setStringTypeTest).append(LF);
					tests.append("       } catch (Exception $e) { ").append(LF);
					tests.append("          $this->assertFalse($t->isDirty());").append(LF);
					tests.append("       } ").append(LF);
					TypeInfo ti = DataTypeLib.getTypeInfo(f);
					int size = 0;
					int maxSizeToTest = 2048;
					if (ti != null) 	{
						try {
							size = Integer.parseInt(getFieldSize(f));
							if (size <= maxSizeToTest) {				
								testString = "9";
								for (int k=0;k<size+2;k++) { testString = testString.concat("9");  }
							}
						}
						catch(NumberFormatException e)
						{
							size = 0;  // we won't test oversize values
						}
					}
					if (size>0 && size<maxSizeToTest)  {
						tests.append("       $t = new  " + tablename + "();").append(LF);
						tests.append("       try { ").append(LF);
						tests.append("          $t->set"+fieldname+"('"+testString+"');").append(LF);
						tests.append("          $this->fail('Failed to throw exception on overlength set.');").append(LF);
						tests.append("       } catch (Exception $e) { ").append(LF);
						tests.append("          $this->assertFalse($t->isDirty());").append(LF);
						tests.append("       } ").append(LF);
					}
					tests.append("    } ").append(LF);
				}

				tests.append("} ").append(LF);

			}
			if (sett.isGenFrame()) { 
				// if they are being generated, test the database level classes in druid_classes.php  
				tests.append("// test database level classes").append(LF);
				tests.append("").append(LF);
				tests.append("class testOf_database_schema extends UnitTestCase {").append(LF);
				tests.append("  function testHasTable() { ").append(LF);
				tests.append("     $d = new database_schema();").append(LF);
				tests.append(hasTableTests).append(LF);
				tests.append("     $this->assertFalse($d->hasTable(\"SELECT\"),\"SELECT is not expected to be a valid table name.\" );").append(LF); // probably not a valid table name).append(LF);
				tests.append("  }").append(LF);
				tests.append("  function testGetClass() { ").append(LF);
				tests.append("     $d = new database_schema();").append(LF);
				tests.append(getClassTests).append(LF);
				tests.append("     $this->assertFalse($d->hasTable(\"SELECT\"),\"SELECT is not expected to be a valid table name.\" );").append(LF); // probably not a valid table name).append(LF);
				tests.append("  }").append(LF);
				tests.append("}").append(LF);
				tests.append("").append(LF);
			} 
			// assemble and write unit tests file
			String unit_tests = "<?php " + LF +
			"// " + DRUID_TEST_CLASSES_PHP + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
			"//" + LF + 
			"// Run unit tests on "  + dbNode.attrSet.getString("name") + " class files " + LF +  
			"// using the simpletest unit testing framework for PHP." + LF  +LF +  
			"//	Load class files *********** " + LF +
			"require_once(\"../"+DRUID_CLASSES_PHP+"\");" + LF +
			"//	Test cases ****************** " + LF +  LF +  
			tests + LF + 
			"?>" + LF;
			writeFileEvenIfExists(outputDir + separator + DRUID_TEST_CLASSES_PHP, unit_tests, l);

		}

		if (sett.isGenFrame()) { 
			l.log(Logger.INFO, "Generating main files...");
		
			// **** DRUID_CLASSES_PHP
			l.log(Logger.INFO, "Creating database level classes file...");
			String tablenames = "   const TABLELIST = \"";
			StringBuffer ifModelBit = new StringBuffer();  // holds tests to load model class
			StringBuffer ifViewBit = new StringBuffer();  // holds tests to load model class
			StringBuffer includes = new StringBuffer();
			String comma = " ";
			for (int i = 0; i < tables.size(); i++) {
				TableNode node = tables.elementAt(i);
				tablename = getClassCodeName(node);
				tablenames = tablenames + comma + tablename;
				ifModelBit.append("            if($tablename=='" + tablename + "') { $returnvalue = new " + tablename + "(); }" ).append(LF);
				ifViewBit.append("            if($tablename=='" + tablename + "') { $returnvalue = new " + tablename + "View(); }" ).append(LF);
				includes.append("include_once('" + tablename + ".php'); ").append(LF);
				comma = ", ";
			}
			tablenames = tablenames + comma;
			tablenames = tablenames + "\";" + LF;
			String classes = "<?php " + LF +
			"// " + DRUID_CLASSES_PHP + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
			"//" + LF + LF +
			includes + 		
			"" + LF +
			"class database_schema {" + LF +
			tablenames + 
			"   public function hasTable($tablename) { "+ LF +
			"       $returnvalue = false;" + LF +
			"       if (strpos(self::TABLELIST,\" $tablename, \")!==false) { " + LF +
			"           $returnvalue = true; " + LF + 
			"       } " + LF + 
			"       return $returnvalue;" + LF +
			"   }" + LF +
			"   public function getClass($tablename) { "+ LF +
			"       $returnvalue = false;" + LF +
			"       if ($this->hasTable($tablename)) { " + LF +
			ifModelBit +  
			"       } " + LF + 
			"       return $returnvalue;" + LF +
			"   }" + LF +			
			"   public function getViewClass($tablename) { "+ LF +
			"       $returnvalue = false;" + LF +
			"       if ($this->hasTable($tablename)) { " + LF +
			ifViewBit +  
			"       } " + LF + 
			"       return $returnvalue;" + LF +
			"   }" + LF +
			"}"+ LF +
			"?>";	
			writeFileEvenIfExists(outputDir + separator + DRUID_CLASSES_PHP, classes, l);

		}
		
		if (sett.isGenExample()) { 
			//TODO: index.php, ajax_handler.php, class_lib.php (page+user), test/test_classes.php  BugId: 25
			//TODO: all done except example integration of user class with index.php and ajax_handler.php
			

			l.log(Logger.INFO, "Generating example website files...");

			// ********** index.php
			// example main page
			StringBuffer tableLinks = new StringBuffer();
			for (int i = 0; i < tables.size(); i++) {
				TableNode node = tables.elementAt(i);
				tablename = getClassCodeName(node);
				tableLinks.append("<li>"+tablename+" <a href='index.php?display=table&table="+tablename+"'>[Browse]</a> ").append(LF);
				tableLinks.append("<a href='index.php?display=addform&table="+tablename+"'>[Add Record]</a></li> ").append(LF);
			}
			String valueFilter = "$value = substr(preg_replace('/[^a-zA-Z_\\ 0-9]/','',$_GET[$field_name]),0,255);  ";
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
				 // using prepared statements, so we can loosen up the filter.
				 valueFilter =  "$value = substr($_GET[$field_name],0,255);  " ;
			} 
			String index = "<?php " + LF +
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF +
			"// This is an example illustrating a few of the ways in which you can use PHP classes " + LF + 
			"// generated by Druid to produce a database driven website.  See the documentation  " + LF +
			"// in $DRUIDHOME/docs/manuals/php_code_generation for more details." + LF +
			"" + LF + 
			"session_start();" + LF + 
			"" + LF + 
			"include_once('druid_classes.php'); " + LF +
			"include_once('class_lib.php'); " + LF +		
			"// *******  " + LF + 
			"// *******  You must provide connections.php or a replacement means of" + LF +
			"// *******  having a database connection in scope before calling methods on generated php tables." + LF +
			"// *******  You must also do this in the file ajax_handler.php" + LF + 
			"// *******  " + LF + 
			"// *******  Warning: You must limit the rights of the user in the database for this" + LF +
			"// *******  connection with appropriate (e.g. select only on this schema only from this host only) privileges." + LF + 
			"// *******  " + LF + 
			"@include_once('connections.php'); // contains declaration of make_database_connection()" + LF +
			"if (!function_exists('make_database_connection')) { echo 'Error: Database connection function not defined.'; } " + LF +
			"@$connection = make_database_connection(); " + LF +
			"if (!$connection) { echo 'Error: No database connection.'; } " + LF +
			"" + LF + 
			"$table_name='';" + LF +
			"$field_name='';" + LF + 
			"$value='';" + LF + 
			"" + LF + 
			"$page = new Page();" + LF + 
			"echo $page->getHeader();" + LF + 
			"" + LF + 
			"$display = substr(preg_replace('/[^a-z]/','',$_GET['display']),0,20);" + LF +
			"" + LF  + 
			"// This switch block includes break statements and only produces output for particlar" + LF +
			"// values of display.  Another switch block follows this that produces a cascading output for" + LF + 
			"// other values of display." + LF +
			"switch ($display) { " + LF  +
			"    case 'logout':" + LF +
			"        if (isset($_COOKIE[session_name()])) { " + LF + 
			"            setcookie(session_name(), '', time()-42000, '/');" + LF + 
			"        }" + LF + 
			"        session_destroy();" + LF + 
			"        break;" + LF + 
			"    case 'addform':" + LF + 
			"" + LF + 
			"        // You must sanitize any data that could be provided by a user." + LF + 
			"        // The patern below may not include all valid characters for your data" + LF +
			"        // And the length limit below may be too short for your data" + LF + 
			"        $table_name = substr(preg_replace('/[^a-zA-Z_]/','',$_GET['table']),0,50);  " + LF +
			"" + LF + 
			"        echo \"<h2>Add new record to $table_name</h2>\";" + LF + 
			"        $db = new database_schema();  // database_schema has discovery methods allowing you load a class representing a table when given a string " + LF +
			"        if ($db->hasTable($table_name)) { ;" + LF +
			"            $table_class = $db->getClass($table_name);  // table_class is a model class for the table_name table." + LF +
			"            $table_view = $db->getViewClass($table_name); // table_view is a view class for the table_name table." + LF +
			"            $table_view->setModel($table_class);" + LF + 
            "            echo $table_view->getEditFormDojoView();" + LF +		
			"        } ;" + LF +		
			"        break;" + LF + 
			"    case 'edit':" + LF + 
			"        // You must sanitize any data that could be provided by a user." + LF + 
			"        // The patern below may not include all valid characters for your data" + LF +
			"        // And the length limit below may be too short for your data" + LF + 
			"        $table_name = substr(preg_replace('/[^a-zA-Z_]/','',$_GET['table']),0,50);  " + LF +
			"" + LF + 
			"        echo \"<h2>Add new record to $table_name</h2>\";" + LF + 
			"        $db = new database_schema();  // database_schema has discovery methods allowing you load a class representing a table when given a string " + LF +
			"        if ($db->hasTable($table_name)) { ;" + LF +
			"            $table_class = $db->getClass($table_name);  // table_class is a model class for the table_name table." + LF +
			"            $primarykeys = $table_class->schemaPK();" + LF +
			"            foreach ($primarykeys as $primarykey) { "+ LF +
			"                $pkarray[$primarykey]= substr(preg_replace('/[^a-zA-Z0-9_]/','',$_GET[$primarykey]),0,50);  " + LF +
			"            }" + LF +
			"            $table_class->load($pkarray);"+ LF +
			"            $table_view = $db->getViewClass($table_name); // table_view is a view class for the table_name table." + LF +
			"            $table_view->setModel($table_class);" + LF + 
            "            echo $table_view->getEditFormDojoView();" + LF +		
			"        }" + LF +  
			"        break;" + LF +		
			"}" + LF +
			"// This switch block doesn't include break statements, so search displays search results, table, and default table list" + LF +
			"// table displays table and default table list, and default for all pages is table list." + LF + 
			"switch ($display) {" + LF +
			"   case 'search':" + LF + 
			"       // You must sanitize any data that could be provided by a user." + LF + 
			"       // The paterns below may not include all valid characters for your data" + LF +
			"       // And the length limits below may be too short for your data" + LF + 
			"       $table_name = substr(preg_replace('/[^a-zA-Z_]/','',$_GET['table']),0,50);  " + LF +
			"       $field_name = substr(preg_replace('/[^a-zA-Z_0-9]/','',$_GET['field']),0,50);  " + LF +
            "       " + valueFilter + LF +
			"" + LF + 
			"       echo \"<h2>Search for [$table_name].[$field_name]=[$value]</h2>\";" + LF + 
			"       $db = new database_schema();  // database_schema has discovery methods allowing you load a class representing a table when given a string " + LF +
			"       if ($db->hasTable($table_name)) { ;" + LF +
			"          // two thirds of a model-view-controler are generated from the database by Druid in PHP" + LF + 
			"          $table_class = $db->getClass($table_name);  // table_class is a model class for the table_name table." + LF +
			"          $table_view = $db->getViewClass($table_name); // table_view is a view class for the table_name table." + LF +
			"          // You need to supply your own controler, or procedural code (like this file) to control these classes." + LF +
			"          "+LF +
			"          if ($table_class->hasField($field_name)) { "+LF +
			"              // loadArrayKeyValueSearch() takes an array of fieldnames and search values as a parameter." + LF + 
            "              $searchTermArray = array($field_name => $value); " + LF +
            "              // loadArrayKeyValueSearch() queries the database and returns an array of model objects representing rows in the target table." + LF + 
			"              $results = $table_class->loadArrayKeyValueSearch($searchTermArray);  " + LF +
			"              if (count($results)==0) { echo 'No matching values'; } " + LF + 	
			"              foreach($results as $row) { " + LF + 
			"                  $table_view->setModel($row);   // Tell the view which row to display. " + LF +  
			"                  echo $table_view->getDetailsView(true, 'index.php?display=edit&table='.$table_name.'&');  // Tell the view how to display the row. " + LF + 
			"                  // Extend the {tablename}View classes to customize how records are displayed. " + LF + 
            "              }" + LF +
            "          }" + LF +		
			"       } ;" + LF +		
			"   case 'table':" + LF +
			"       $table_name = substr(preg_replace('/[^a-zA-Z_]/','',$_GET['table']),0,50);  // May not include all valid table name characters." + LF +
			"       $db = new database_schema();" + LF +
			"       if ($db->hasTable($table_name)) { ;" + LF +
			"          echo '<h2>Distinct values in  '.$table_name.'</h2>';" + LF + 
			"          $table_class = $db->getClass($table_name);" + LF +
			"          // Each field in the table with an index has a selectDistinct method that runs a " + LF + 
			"          // select count(*), fieldname from tablename group by fieldname query." + LF +
			"          // These are directly accesible with selectDistinct{fieldname} methods, " + LF + 
			"          //  and as shown here, with a generic keySelectDistinct($fieldname...) method." + LF + 
			"          $fieldarray = $table_class->schemaHaveDistinct();  // schemaHaveDistinct is a discovery method to find fields with selectDistinct methods.  "+LF +
			"          if ($fieldarray=='') { "+LF +
            "              echo 'No values available.';" + LF +
            "          } else { " + LF +
			"              foreach($fieldarray as $key => $field) { " + LF +
			"                  echo '<h3>Distinct values for '.$field.'</h3>';" + LF +  
			"                  $beginwith = '';" + LF +
			"                  $link = 'index.php?display=search&table='.$table_name.'&field='.$field;  // &{fieldname}={value} is appended by keySelectDistinct." + LF + 			
			"                  $endwith = '<BR>';" + LF + 
			"                  $includecount = true;" + LF + 
			"                  $orderbycsount = false;" + LF + 
			"                  echo $table_class->keySelectDistinct($field,$beginwith,$link,$endwith,$includecount,$orderbycount);" + LF +
            "              }" + LF +
            "          }" + LF +		
			"       } ;" + LF + 
			"   default:" + LF +
			"     echo '<h2>Tables</h2>';" + LF + 
			"     echo \"<ul>"+ tableLinks + "</ul>\";" + LF + 
			"}" + LF + 
			"" + LF + 
			"echo $page->getFooter();" + LF + 
			"?>";
			writeFileIfNotExists(outputDir + separator + "index.php", index,l);

			// ajax_handler.php
			// wrapper for druid_handler.php
			String ajax_handler_msg = "";
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
			      ajax_handler_msg =  "// *******  $connection must be a mysqli object." + LF +
			                       "// *******  For example, make_database_connection() does the same as:" + LF + 
			                       "// *******  $connection = new mysqli('localhost', 'user', 'password', 'database');" + LF;	
			}
			String ajax_handler = "<?php " + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF +
			"// *******  " + LF + 
			"// *******  Customize this file to provide the desired behavior for your application." + LF + 
			"// *******  " + LF +
			" " +  LF + 
			"session_start();" +  LF + 
			" " +  LF + 
			"include_once('class_lib.php');  // contains declaration of User() class " + LF + 
			"// check authentication " + LF +
			"// *******  " + LF + 
			"// *******  Warning: You must provide appropriate limits on users for your" + LF +
			"// *******  situation.  This code is a general" + LF + 
			"// *******  " + LF + 
			"$authenticated=false;" + LF + 
			"if (isset($_SESSION['user_ticket'])) { " + LF +
			"   $u = new User();" + LF + 
			"   if ($u->validateTicket($_SESSION['user_ticket'],$_SERVER['REMOTE_ADDR'])) { " + LF + 
			"       $authenticated = true;" + LF +
			"   }" + LF +
			"}" + LF +
			"" + LF +
			"// *******  " + LF + 
			"// *******  You must provide connections.php or a replacement means of" + LF +
			"// *******  having a database connection in scope before including druid_handler." + LF + 
			"// *******  " + LF + 
			"// *******  Warning: You must limit the rights of the user for this" + LF +
			"// *******  connection with appropriate (e.g. select only) privileges." + LF + 
			"// *******  " + LF + 
			"include_once('connections.php'); // contains declaration of make_database_connection()" + LF +
			"if (!function_exists('make_database_connection')) { echo 'Error: Database connection function not defined.'; } " + LF +
			"@$connection = make_database_connection(); " + LF +    
			"if ($connection) {" + LF + 
			"    include_once('druid_handler.php');" + LF + 
			"} else {" + LF + 
			"    echo 'Error: Unable to connect to database.';" + LF + 
			"}" + LF + 
			ajax_handler_msg +  LF +
			"?>";
			writeFileIfNotExists(outputDir + separator + "ajax_handler.php", ajax_handler,l);

			// class_lib.php			
			// defines User and Page classes
			StringBuffer class_lib = new StringBuffer("<?php" + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF + 
			"// This is an example framework you can customize for your own application." + LF +
			"class User { " + LF + 
			"   private $email;          // email address provided by user" + LF + 
			"   private $password;       // password provided by user" + LF + 
			"   private $authenticated;  // has user been authenticated against database" + LF +
			"   private $user_token;     // hash for session (ip, email, session_secret) put in session " + LF +
			"   // values retrieved from database" + LF + 
			"   private $active;         // 1 for active user, 0 for deactivated users" + LF + 
			"   private $full_name;" + LF + 
			"   private $about;   " + LF + 
			"   private $date_created;" + LF + 
			"   private $date_last_updated;" + LF + 
			"   private $date_last_login;" + LF + 
			"   private $failures_since_last_login;" + LF +
			"   private $session_secret;  // used in hash for session, stored in db, not sent to user " + LF +
			LF + 
			"   function User($email='', $password='') {" + LF + 
			"      $this->setEmail($email);" + LF + 
			"      $this->setPassword($password);" + LF + 
			"      $this->authenticated = false;" + LF +
			"      $this->user_token = '';" + LF +  
			"      $this->about = '';" + LF + 
			"   } " + LF +
			LF + 
			"   private function l_addslashes($value) {" + LF +
			"      $retval = $value;" + LF +
			"      if (!get_magic_quotes_gpc()) {" + LF +
			"          $retval = addslashes($value);" + LF +
			"      }" + LF +
			"      return $retval;" + LF +
			"   }" + LF +
			"   private function l_stripslashes($value) {" + LF +
			"      $retval = $value;" + LF +
			"      if (!get_magic_quotes_gpc()) {" + LF +
			"          $retval = stripslashes($value);" + LF +
			"      }" + LF +
			"      return $retval;" + LF +
			"   }" + LF +
			LF + 			
			"   function setEmail ($email) {" + LF +
			"       $returnvalue = false;" + LF + 
			"       $len = strlen($email);" + LF + 
			"       $match = preg_match(\"/[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/\",$email); " + LF +  
			"	    if (($match || $len==0) && $len<256) { " + LF + 				  		
			"          $this->email = $this->l_addslashes($email);" + LF +
			"          $returnvalue = true;" + LF + 
		    "       } else {"  + LF + 
			"          $this->email = '';" + LF +
		    "       } "  + LF + 
			"       return $returnvalue;" + LF + 
			"   }" + LF +
			LF + 
			"   function setPassword ($password) {" + LF +
			"       $returnvalue = false;" + LF + 
			"       $len = strlen($password);" + LF + 
			"       $match = preg_match(\"/[::alpha::0-9!#$%*+\\/=?^_{|}~\\-@.]*/\",$password); " + LF +  
			"	    if ($match && $len<256 && $len >=8) { " +	
			"            $this->password = $this->l_addslashes($password);" + LF +
			"            $returnvalue = true;" + LF + 
		    "       } else {"  + LF + 
			"          $this->password = '';" + LF +
		    "       } "  + LF + 
			"       return $returnvalue;" + LF + 			
			"   }" + LF +
			LF + 
			"   function getAuthenticationState() {" + LF +
			"      return $this->authenticated;" + LF +
			"   }" + LF +
			LF +
			"  function setTicket($ip) { " + LF +
			"       $this->session_secret = hash('sha256', date('r') . rand() . rand());" + LF + 
			"       $this->user_token = hash('sha256',$ip . $this->email . $this->session_secret); " + LF + 
			"       // store session_secret to db for this user" + LF + 
			LF +
			"       return $this->user_token;" + LF +
			"  }" + LF + 
			"  function validateTicket($ticket, $ip) { " + LF +
			"       $returnvalue = false;" + LF + 
			"       // lookup session_secret for this user" +
			"       " + LF +
			"       if (hash('sha256',$ip . $this->email . $this->session_secret)==$ticket) { $returnvalue = true; }" + LF +
			"       return $returnvalue;" + LF +
			"  }" + LF + 
			"  function getLoginForm() {" + LF +
			"       return '';" + LF +
			"  }" + LF + 
			LF + 
			"   function logout() {" + LF +
		    "      if($this->email != '' && $this->password !='') {" + LF +
			"           $sql = \"UPDATE User set session_secret = NULL WHERE email = '\".$this->email.\"' \"; " + LF +
		    "           $result = mysql_query($sql);" + LF +
		    "           if ($result===false) { $this->error = mysql_error(); }" + LF +
			"      }" + LF +
			"      $_SESSION = array();" + LF + 
			"      session_destroy();" + LF + 
			"   }" + LF +
			LF + 
			"  function authenticate() {" + LF +
			"     $this->authenticated = false;" + LF +
		    "     if($this->email != '' && $this->password !='') {" + LF);
		    if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
		    class_lib.append("        $sql  = 'SELECT user_id, email, password, active, full_name, about, date_created, date_last_updated, date_last_login, failures_since_last_login, session_secret '; " + LF + 
		    "        $sql .= \"FROM User WHERE email = '\".$this->email.\"' and password = '\".$this->password.\"' and active = 1 \";" + LF +
		    "        $result = mysql_query($sql);" + LF +
		    "        if ($result===false) { $this->error = mysql_error(); }" + LF +
		    "        if (mysql_num_rows($result)==1) {" + LF +
		    "            $row = mysql_fetch_object($result);" + LF +
		    "            if ($row) {" + LF +
		    "                 $this->email = $row->email;" + LF +
		    "                 $this->password = $row->password;" + LF +
		    "                 $this->active = $row->active;" + LF +
		    "                 $this->full_name = $row->full_name;" + LF +
		    "                 $this->about = $row->about;" + LF +
		    "                 $this->date_created = $row->date_created;" + LF +
		    "                 $this->date_last_updated = $row->date_last_updated;" + LF +
		    "                 $this->date_last_login = $row->date_last_login;" + LF +
		    "                 $this->failures_since_last_login = $row->failures_since_last_login;" + LF +
		    "                 $this->session_secret = $row->session_secret;" + LF +
		    "                 " + LF +
		    "                 $returnvalue = true;" + LF +
		    "            }" + LF +
		    "         }" + LF +
		    "         mysql_free_result($result);" + LF);
		    }
		    class_lib.append("     }" + LF +
		    "     return $returnvalue;" + LF +
			"  }" + LF +
			"  function getUserHtml() {" + LF +
			"       $returnvalue = '';" + LF +
			"         if ($this->authenticated===true) {" + LF +
			"             $returnvalue  = '<h2>'.$this->full_name.'</h2>\\n';" + LF +
			"             if ($this->about != '') { $returnvalue .= '<p>'.$this->about.'</p>\\n'; }" + LF +
			"             $returnvalue .= '<ul>\\n';" + LF +
			"             $returnvalue .= '<li>Last login: '.$this->date_last_login.'</li>\\n';" + LF +
			"             $returnvalue .= '<li>Failed logins since last login '.$this->failures_since_last_login.' </li>\\n';" + LF +
			"             $returnvalue .= '<li><a href=\"annotation.php?display_mode=logout\">Logout</a></li>\\n';" + LF +
			"             $returnvalue .= '</ul>\\n';" + LF +
			"         }" + LF +
			"         return $returnvalue;" + LF +
			"  }" + LF + 
			"  function getFullname() {" + LF +
			"        $returnvalue = '';" + LF +
			"        if ($this->authenticated===true) {" + LF +
			"             $returnvalue = $this->full_name;" + LF +
		    "        }" + LF +
		    "       return $returnvalue;" + LF +
			"  }" + LF + 			
			"} // end class User" + LF +
		    "" + LF);
		    // generate a Page class to render page header and footer, may or may not include Dojo.
			String dojo = "";
			if (sett.isGenDojo()) { 
				// include call to load dojo.js
				//dojo = "<?php" +
				//"    include_once('class_lib.php');" + LF +
				//"    $page = new Page();" + LF + 
				//"    echo $page->getDojoPageHead();" + LF + 
				//"?>";
				dojo = "\".\n$this->getDojoPageHead()\n.\"";
				//dojo = "<script type=\"text/javascript\" src=\""+sett.getPathToDojo()+"\"  djConfig=\"isDebug: true, parseOnLoad: true\"></script>";
			}
			String bodyClass = "";
			if (sett.isGenDojo()) { 
				bodyClass = " class='tundra' "; // dojo theme to apply to page, css is loaded in $page->getDojoPageHead
			}
			class_lib.append(LF + 
		    "class Page { " + LF +
		    "   // customize the header and footer methods for your own site style and navigation " + LF +
		    "" + LF +
		    "    private $title;" + LF +
		    "" + LF +
		    "    public function setTitle($pageTitle) { " + LF +
            "        $title = 'Example autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + "';" + LF +
            "        $this->title.=$pageTitle;" + LF +
            "    }" + LF +
            "" + LF +
		    "   public function getHeader() { " + LF +
			"   return \"<html>" + LF +
			"<head>" + LF + 
			"<title>$this->title</title>" + LF + 
			" " +  dojo + LF + 
			"</head>" + LF +
			"<body" + bodyClass + ">" + LF +
			"<H1>$this->title</H1>\";" + LF + 
			"   }" + LF + LF + 
		    "   public function getFooter() { " + LF +
			"   return \"<div style=' text-align: center; '>" + LF +
			"<a href='index.html'>Example Home</a> &nbsp; <a href='index.html?display=logout'>Logout</a>" +  LF + 
			"</div>" + LF +
			"</body>" + LF +
			"</html>\";" + LF + 
			"   }" + LF			
		    );
		    if (sett.isGenDojo()) { 
		    	// Provide path to dojo, extract root holding dojo/dojo.js or dojo/dojo.xd.js.
		    	String fullPathToDojo = sett.getPathToDojo();
		    	String dojoRoot = "";
		    	if (fullPathToDojo.endsWith("dojo/dojo.js"))  {
		    		dojoRoot = fullPathToDojo.replaceFirst("dojo\\/dojo\\.js", "");
		    	}
		    	if (fullPathToDojo.endsWith("dojo/dojo.xd.js"))  {
		    		dojoRoot = fullPathToDojo.replaceFirst("dojo\\/dojo\\.xd\\.js", "");
		    	}
		    	class_lib.append(LF +  
		    			"   public function getDojoPageHead() {" + LF +
		    			"       return \"      <style type='text/css'>" + LF +
		    			"       @import '"+ dojoRoot + "dijit/themes/tundra/tundra.css';" + LF +
		    			"       @import '" + dojoRoot + "dojo/resources/dojo.css';" + LF +
		    			"     </style>" + LF +
		    			"     <script type='text/javascript' src='"+ fullPathToDojo+"'" + LF +
		    			"        djConfig='parseOnLoad: true'></script>" + LF +
		    			"     <script type='text/javascript'>" + LF +
		    			"       dojo.require('dojo.data.ItemFileReadStore');" + LF +
		    			"       dojo.require('dojox.data.QueryReadStore');" + LF +
		    			"       dojo.require('dijit.form.TextBox');" + LF +
		    			"       dojo.require('dijit.form.Form');" + LF +
		    			"       dojo.require('dijit.form.NumberTextBox');" + LF +
		    			"       dojo.require('dijit.form.SimpleTextarea');" + LF +
		    			"       dojo.require('dijit.form.Textarea');" + LF +
		    			"       dojo.require('dijit.form.ComboBox');" + LF +
		    			"       dojo.require('dijit.form.FilteringSelect');" + LF +
		    			"       dojo.require('dijit.form.CheckBox');" + LF +
		    			"       dojo.require('dijit.form.Button');" + LF +
		    			"       dojo.require('dijit.layout.ContentPane');" + LF +
		    			"       dojo.require('dijit.Tooltip');" + LF +
		    			"       dojo.require('dojo.parser');" + LF +
		    			"     </script>\";" + LF +
		    			"   }" + LF);
		    } 
			class_lib.append(LF+ 
		    "}" + LF + 
		    "" + LF +		    
			"?>");
			writeFileIfNotExists(outputDir + separator + "class_lib.php", class_lib.toString(),l);			

			// tests/test_classes.php
			// unit tests for classes in class_lib.php
			String test_classes = "<?php" + LF + 
			"// Autogenerated by Druid from " + dbNode.attrSet.getString("name")  + " " + sBuild + LF +
			"// sets up simpleTest for php unit tests on classes" + LF + 
			"// run unit tests with: php all_tests.php" + LF + 
			LF + 
			"// load simpletest framework and files containing classes to test" + LF + 
			"require_once('simpletest/unit_tester.php');" + LF + 
			"require_once('" + DRUID_TEST_TO_CLASSES_PATH + "/class_lib.php');" + LF + 
			LF + 
			"// define test cases" + LF + 
			LF + 
			"/* class testOfUser" + LF + 
			"* Unit tests for class User." + LF + 
			"*/" + LF + 
			"class testOfUser extends UnitTestCase {" + LF + 
			LF + 
			"  function testConstructor() {" + LF + 
			"     $user = new User('','');" + LF + 
			"     $this->assertNotNull($user);" + LF + 
			"     $this->assertFalse($user->getAuthenticationState(),'User authentication state must be false for newly constructed user object.');" + LF + 
			"     $this->assertIdentical($user->getUserHtml(),'');" + LF + 
			"     $this->assertIdentical($user->getFullname(),'');" + LF + 
			"  }" + LF + 
			LF + 
			"  function testAuthentication() {" + LF + 
			"     $invaliduser = new User('','');" + LF + 
			"     $this->assertFalse($invaliduser->authenticate(),'Invalid user authenticated.');" + LF + 
			"     $this->assertIdentical($invaliduser->getUserHtml(),'');" + LF + 
			"     $this->assertIdentical($invaliduser->getFullname(),'');" + LF + 
			"  }" + LF +
			LF +
			"  function testTicket() {" + LF +
			"      $user = new User('','');" + LF +
			"      $ip = '127.0.0.1';" + LF +
			"      $ticket = $user->setTicket($ip);" + LF +
			"      $this->assertTrue($user->validateTicket($ticket,$ip));" + LF +
			"      $this->assertFalse($user->validateTicket('invalidticket',$ip));" + LF +
			"      $this->assertFalse($user->validateTicket($ticket,'invalidip'));" + LF +
			"  }" + LF +
			" " + LF +
			LF + 
			"}" + LF + 
			"?>" + LF;
			writeFileIfNotExists(outputDir + separator + DRUID_TEST_DIR + separator + "test_classes.php", test_classes,l);
		}
	}	



	//---------------------------------------------------------------------------
	//---
	//--- Abstract methods implementation
	//---
	//---------------------------------------------------------------------------

	protected String getMessage() { return "Php Classes"; }

	//---------------------------------------------------------------------------

	protected String  getExtension()    { return "php"; }
	protected boolean isClassOriented() { return true;  }

	//---------------------------------------------------------------------------

	private String getHeader(String name, TableNode node)
	{
		String w = "<?php"+ LF;

		w += "//" + getSeparator() + LF;
		w += "//===   " + name + ".php                         " + LF;
        w += "//===   Autogenerated by Druid from " + node.getDatabase().attrSet.getString("name")  + " " + sBuild + LF;
		w += "//" + getSeparator() + LF;
		w += LF;
		w += "include_once(\"" + DRUID_INTERFACES_PHP + "\");" + LF;
		w += LF;

		//--- put comment (if any)
		String comment = node.attrSet.getString("comment").trim();

		if (!comment.equals(""))
			w += "// "+comment + LF + LF;

		w += "class " + getClassCodeName(node);

		if (!sett.getExtends().equals(""))
			w += " extends " + sett.getExtends();
		
		// pick correct set of interfaces for sett values
		String interfaces = "";
		String comma = "";
		// if (sett.isGenVaria()) { etc.... 
		if (sett.isGenVaria())  { 
			interfaces = " model ";
			comma = ",";
		}
		if (sett.isGenPersi()) { 
			interfaces = interfaces + comma + " loadableModel, saveableModel, tableSchema ";
		}
		if (!interfaces.equals("")) { 
	       w += " implements " + interfaces;
		}   

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
		w += "//"+ getSeparator() + LF;
		// Druid 3.11 comments out the next line.
		w += "?>";

		return w;
	}

	//---------------------------------------------------------------------------

	protected String getClassCodeName(TableNode node)
	{
		String className = node.attrSet.getString("name");

		return sett.getNamePrefix() + className + sett.getNameSuffix();
	}
	
	protected String getClassTableName(TableNode node) 
	{
		return node.attrSet.getString("name");
	}
	

	//---------------------------------------------------------------------------
	//---
	//--- Private utility methods
	//---
	//---------------------------------------------------------------------------

	private String fillBounds(String text, String boundary)
	{
		if (!text.startsWith(boundary)) text = boundary + text;
		if (!text.endsWith(boundary))   text += boundary;

		return text;
	}

	//---------------------------------------------------------------------------

	protected String getSmallSeparator()
	{
		return LF + "   //" + Util.replicate("-", 75) + LF + LF;
	}
	
	
	private String fixGlobalName(TableNode node, String globalNameToCheck) {
		boolean ok = false;
		while (!ok) {
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				if (f.attrSet.getString("name").equals(globalNameToCheck)) {
					globalNameToCheck = globalNameToCheck.concat("_").concat(Integer.toString(i));
				}
			}
			boolean found = false;
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				if (f.attrSet.getString("name").equals(globalNameToCheck)) { found = true; }
			}				
			if (!found) { ok = true; } 
		}
		return globalNameToCheck;
	}

	/**
	 * Determine the character to use to describe the type of this field for mysqli_stmt_bind_param().
	 * 
	 * @param f the FieldNode to check
	 * @return "s", "d", "i" or "b" for this field suitable for $types in mysqli_stmt_bind_param()
	 */
	public String getMySQLIType(FieldNode f) { 
		String returnvalue = "s";   // Default to "s" for String
		TypeInfo ti = DataTypeLib.getTypeInfo(f);
		if (ti != null) { 
			if (ti.basicType.toUpperCase().equals("CHAR")) { returnvalue = "s"; }
			if (ti.basicType.toUpperCase().equals("VARCHAR")) { returnvalue = "s"; }
			if (ti.basicType.toUpperCase().equals("BIT")) { returnvalue = "i"; }

			if (ti.basicType.toUpperCase().equals("BOOL")) { returnvalue = "s"; }   // Allows for aliases true and false as well as values 0 and 1.
			if (ti.basicType.toUpperCase().equals("BOOLEAN")) { returnvalue = "s"; } // Allows for aliases true and false as well as values 0 and 1 

			if (ti.basicType.toUpperCase().equals("TINYINT")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("TINYINT UNSIGNED")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("SMALLINT")) { returnvalue = "i"; }  
			if (ti.basicType.toUpperCase().equals("SMALLINT UNSIGNED")) { returnvalue = "i"; }  
			if (ti.basicType.toUpperCase().equals("MEDIUMINT")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("MEDIUMINT UNSIGNED")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("INT")) { returnvalue = "i"; }  
			if (ti.basicType.toUpperCase().equals("INT UNSIGNED")) { returnvalue = "i";   }  
			if (ti.basicType.toUpperCase().equals("INTEGER")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("INTEGER UNSIGNED")) { returnvalue = "i";  }											
			if (ti.basicType.toUpperCase().equals("BIGINT")) { returnvalue = "i"; } 
			if (ti.basicType.toUpperCase().equals("SERIAL")) { returnvalue = "i"; } 

			if (ti.basicType.toUpperCase().equals("FLOAT")) { returnvalue = "d"; }  // could be fixed length at least in MySQL FLOAT[(M,D)], allow for - . E+38  
			if (ti.basicType.toUpperCase().equals("DOUBLE")) { returnvalue = "d"; }  // could be fixed length at least in MySQL DOUBLE[(M,D)], allow for - . E+308
			if (ti.basicType.toUpperCase().equals("DOUBLE PRECISION")) { returnvalue = "d"; }  // set reasonable, but not nescisiarly correct defautl   
			if (ti.basicType.toUpperCase().equals("DECIMAL")) { returnvalue = "d"; }  // could be fixed length at least in MySQL MySQL DOUBLE[(M,D)], default M is 10.
			if (ti.basicType.toUpperCase().equals("DECI")) { returnvalue = "d"; }  // could be fixed length at least in MySQL MySQL DECI[(M,D)], default M is 10.
			if (ti.basicType.toUpperCase().equals("FIXED")) { returnvalue = "d"; }  // could be fixed length at least in MySQL MySQL FIXED[(M,D)], default M is 10.
			if (ti.basicType.toUpperCase().equals("DATETIME")) { returnvalue = "s"; }  // without timezone 
			if (ti.basicType.toUpperCase().equals("TIMESTAMP")) { returnvalue = "s"; } // without timezone
			if (ti.basicType.toUpperCase().equals("TIMESTAMP WITH TIMEZONE")) { returnvalue = "s"; } // without timezone
			if (ti.basicType.toUpperCase().equals("TIMESTAMP WITH LOCAL TIMEZONE")) { returnvalue = "s"; } // without timezone				
			if (ti.basicType.toUpperCase().equals("DATE")) { returnvalue = "s"; } 
			if (ti.basicType.toUpperCase().equals("TIME")) { returnvalue = "s"; }
			if (ti.basicType.toUpperCase().equals("TINYBLOB")) { returnvalue = "b"; } 
			if (ti.basicType.toUpperCase().equals("TINYTEXT")) { returnvalue = "b"; }
			if (ti.basicType.toUpperCase().equals("LONGVARCHAR")) { returnvalue = "s"; }						
			if (ti.basicType.toUpperCase().equals("BLOB")) { returnvalue = "b"; } 
			if (ti.basicType.toUpperCase().equals("TEXT")) { returnvalue = "b"; }
			if (ti.basicType.toUpperCase().equals("MEDIUMBLOB")) { returnvalue = "b"; } 
			if (ti.basicType.toUpperCase().equals("MEDIUMTEXT")) { returnvalue = "b"; }		
			if (ti.basicType.toUpperCase().equals("LONGBLOB")) { returnvalue = "b"; } 
			if (ti.basicType.toUpperCase().equals("LONGTEXT")) { returnvalue = "b"; }
			// oracle LONGRAW = 2GB
			if (ti.basicType.toUpperCase().equals("ROWID")) { returnvalue = "s"; }  // oracle
			if (ti.basicType.toUpperCase().equals("UROWID")) { returnvalue = "s"; }  // oracle
			if (ti.basicType.toUpperCase().equals("HTTPURITYPE")) { returnvalue = "s"; }  // oracle
			if (ti.basicType.toUpperCase().equals("BINARY_FLOAT")) { returnvalue = "d"; }  // oracle
			if (ti.basicType.toUpperCase().equals("BINARY_DOUBLE")) { returnvalue = "d"; } // oracle
			if (ti.basicType.toUpperCase().equals("NUMBER")) { returnvalue = "d"; }  // oracle

		}	
		return returnvalue;
	}
	
	/* getFieldSize gets the size of a field.
	 * @param f the FieldNode for which the size is to be returned.
	 * @returns For variable length data types, extracts the value from Size or an empty string if no size is specified
	 *   for fixed length data types, returns a value based on a match of the basic type name with appropriate sizes for 
	 *   commonly used MySQL, Oracle, and Postgresql types this list is not complete and a default value of 20 is returned
	 *   if the name of the basic type doesn't match
	 *   for fields that don't have a data type specified and empty string is returned.    
	 *   Limit for decimals may be two short, NUMBER(5,2) returns 5, not allowing for valid '-5.33 E+32'.
	 */
	private String getFieldSize(FieldNode f) { 
		String returnvalue = "";   // Default to an empty string if the field doesn't have a field type assigned.
		TypeInfo ti = DataTypeLib.getTypeInfo(f);
        boolean unsigned = false;
		
		//TODO: Handle oracle TIMESTAMP (fractional_sections_precision) and its other variable length variants 
        //TIMESTAMP () WITH TIME ZONE and TIMESTAMP () WITH LOCAL TIMEZONE. 
		if (ti != null && ti.size != null)
		{
			// Variable length fields have a size, use it if it is specified
			String size = ti.size.replaceAll(SIZE_REPLACEMENT_PATTERN, "");   // handle pattern for decimals: e.g. '10,2'
			try
			{
				Integer.parseInt(size);

				returnvalue = size;
			}
			catch(NumberFormatException e)
			{
				// shouldn't end up here, with decimal handling pattern.
				//--- in this case we don't generate the entry
			}
		} else {
			if (ti != null) { 
				// Fixed length fields 
				// lookup an appropriate maximum length for the datatype, includes room for negative sign in signed integers.
				// Bug 1981159 notes that unsigned should be used for part of type name rather than attribute, particularly for MySQL
				// for which invalid sql is generated if an unsigned field attribute isn't directly after primary key in field attributes.
				// 
				// Handle an unsigned field attribute as both "INT UNSIGNED" and as "INT" with an unsigned field property.
				if (DataLib.isUnsigned(f)) {
					unsigned = true;
				}			
				String size = "20";
				if (ti.basicType.toUpperCase().equals("BIT")) { size = "1"; } 
				if (ti.basicType.toUpperCase().equals("BOOL")) { size = "5"; }   // Allows for aliases true and false as well as values 0 and 1.
				if (ti.basicType.toUpperCase().equals("BOOLEAN")) { size = "5"; } // Allows for aliases true and false as well as values 0 and 1 
				                                                                  // the standard also allow alias UNKNOWN for NULL, apparently not implemented by anyone.
				if (ti.basicType.toUpperCase().equals("TINYINT")) { size = "4"; } // allows for -128 to 127 or 0 to 255
				if (ti.basicType.toUpperCase().equals("TINYINT UNSIGNED") || (ti.basicType.toUpperCase().equals("TINYINT") && unsigned)) {
					size = "3"; // 0 to 255  
				} 
				if (ti.basicType.toUpperCase().equals("SMALLINT")) { size = "6"; }  // allows for -32768 to 32767  
				if (ti.basicType.toUpperCase().equals("SMALLINT UNSIGNED") ||  (ti.basicType.toUpperCase().equals("SMALLINT") && unsigned)) { 
					size = "5"; // allows for 0 to 65535 
				}  
				if (ti.basicType.toUpperCase().equals("MEDIUMINT")) { size = "8"; } 
				if (ti.basicType.toUpperCase().equals("MEDIUMINT UNSIGNED") ||  (ti.basicType.toUpperCase().equals("MEDIUMINT") && unsigned)) { 
					size = "8"; 
				} 
				// set reasonable, but not nescisarily correct defaults for integers
			    if (ti.basicType.toUpperCase().equals("INT")) { size = "11"; }  // allows for -2147483648 to 2147483647
			    if (ti.basicType.toUpperCase().equals("INT UNSIGNED") 
			    		||  (ti.basicType.toUpperCase().equals("INT") && unsigned)) { 
			    	size = "10"; // allows for 0 to 4294967295   
			    }  
			    if (ti.basicType.toUpperCase().equals("INTEGER")) { size = "11"; } 
			    if (ti.basicType.toUpperCase().equals("INTEGER UNSIGNED") 
			    		|| (ti.basicType.toUpperCase().equals("INT") && unsigned)) { 
			    	size = "10"; 
			    }
				if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
				    if (ti.basicType.toUpperCase().equals("INT") || ti.basicType.toUpperCase().equals("INTEGER")) { 
				    	size = "12"; 
				    }  
				    if (ti.basicType.toUpperCase().equals("INT UNSIGNED") 
				    		|| ti.basicType.toUpperCase().equals("INTEGER UNSIGNED") 
				    		|| (ti.basicType.toUpperCase().equals("INTEGER") && unsigned) 
				    		|| (ti.basicType.toUpperCase().equals("INT") && unsigned)) { 
				    	size = "12"; 
				    }   
				}	 			
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL) || sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				    if (ti.basicType.toUpperCase().equals("INT") || ti.basicType.toUpperCase().equals("INTEGER")) { 
				    	size = "11"; // allows for -2147483648 to 2147483647			    	
				    }  
				    if (ti.basicType.toUpperCase().equals("INT UNSIGNED") 
				    		|| ti.basicType.toUpperCase().equals("INTEGER UNSIGNED") 
				    		|| (ti.basicType.toUpperCase().equals("INTEGER") && unsigned) 
				    		|| (ti.basicType.toUpperCase().equals("INT") && unsigned)) { 
				    	size = "10"; // allows for 0 to 4294967295
				    }   
				}								
				if (ti.basicType.toUpperCase().equals("BIGINT")) { size = "20"; } 
				if (ti.basicType.toUpperCase().equals("SERIAL")) { size = "20"; } 
				if (ti.basicType.toUpperCase().equals("FLOAT")) { size = "16"; }  // could be fixed length at least in MySQL FLOAT[(M,D)], allow for - . E+38  
				if (ti.basicType.toUpperCase().equals("DOUBLE")) { size = "24"; }  // could be fixed length at least in MySQL DOUBLE[(M,D)], allow for - . E+308
				if (ti.basicType.toUpperCase().equals("DOUBLE PRECISION")) { size = "24"; }  // set reasonable, but not nescisiarly correct defautl   
				if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
				    if (ti.basicType.toUpperCase().equals("DOUBLE PRECISION")) { size = "16"; }  //oracle DOUBLE PRECISON = MYSQL FLOAT, only 16 characters
				}
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL) || sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			        if (ti.basicType.toUpperCase().equals("DOUBLE PRECISION")) { size = "24"; }  // could be fixed length at least in MySQL DOUBLE[(M,D)], allow for - . E+308
				}
				if (ti.basicType.toUpperCase().equals("DECIMAL")) { size = "12"; }  // could be fixed length at least in MySQL MySQL DOUBLE[(M,D)], default M is 10.
				if (ti.basicType.toUpperCase().equals("DECI")) { size = "12"; }  // could be fixed length at least in MySQL MySQL DECI[(M,D)], default M is 10.
				if (ti.basicType.toUpperCase().equals("FIXED")) { size = "12"; }  // could be fixed length at least in MySQL MySQL FIXED[(M,D)], default M is 10.
				if (ti.basicType.toUpperCase().equals("DATETIME")) { size = "21"; }  // without timezone 
				if (ti.basicType.toUpperCase().equals("TIMESTAMP")) { size = "21"; } // without timezone
				if (ti.basicType.toUpperCase().equals("TIMESTAMP WITH TIMEZONE")) { size = "27"; } // without timezone
				if (ti.basicType.toUpperCase().equals("TIMESTAMP WITH LOCAL TIMEZONE")) { size = "27"; } // without timezone				
				if (ti.basicType.toUpperCase().equals("DATE")) { size = "12"; } 
				if (ti.basicType.toUpperCase().equals("TIME")) { size = "10"; }
				if (ti.basicType.toUpperCase().equals("TINYBLOB")) { size = "256"; } 
				if (ti.basicType.toUpperCase().equals("TINYTEXT")) { size = "256"; }
				if (ti.basicType.toUpperCase().equals("LONGVARCHAR")) { size = "255"; }						
				if (ti.basicType.toUpperCase().equals("BLOB")) { size = "65536"; } 
				if (ti.basicType.toUpperCase().equals("TEXT")) { size = "65536"; }
				if (ti.basicType.toUpperCase().equals("MEDIUMBLOB")) { size = "16777216"; } 
				if (ti.basicType.toUpperCase().equals("MEDIUMTEXT")) { size = "16777216"; }		
				if (ti.basicType.toUpperCase().equals("LONGBLOB")) { size = "4294967296"; } 
				if (ti.basicType.toUpperCase().equals("LONGTEXT")) { size = "4294967296"; }
				// oracle LONGRAW = 2GB
				if (ti.basicType.toUpperCase().equals("ROWID")) { size = "4000"; }  // oracle
				if (ti.basicType.toUpperCase().equals("UROWID")) { size = "4000"; }  // oracle
				if (ti.basicType.toUpperCase().equals("HTTPURITYPE")) { size = "255"; }  // oracle, no clear limit, browsers vary, 
				                         //2083 in IE, >100000 in Firefox, >190000 in opera, 255 in classical recomendation, 32KB in IE8 for data: URI.
				                         //Apache limits to 8192 bytes per field in a request, can throw error at 4000
				                         //Adding 2083 as reasonable alternative to default 20. 
				if (ti.basicType.toUpperCase().equals("BINARY_FLOAT")) { size = "16"; }  // oracle, documentation appears to have limits transposed.
				if (ti.basicType.toUpperCase().equals("BINARY_DOUBLE")) { size = "24"; } // oracle, documentation appears to have limits transposed.
				if (ti.basicType.toUpperCase().equals("NUMBER")) { size = "45"; }  // oracle, apparent default if size isn't specified is NUMBER(38)
				returnvalue = size;
			}
		}	
		return returnvalue;
	}

	/*
	 * @param filename The filename with path of the file to be written
	 * @param content The text to be written into the file
	 * @param l The Logger for logging error messages.
	 */
	private boolean writeFileEvenIfExists(String filename, String content, Logger l) {
		boolean returnvalue = false;
		String ifExistMessage = "<?php " + LF + "// *******  Warning: This file will be overwritten by druid if PHP code is regenerated. *******" + LF + "?>" + LF ;
		Writer file = null;
		BufferedWriter bfile = null;
		try {
			if (content.length() > 0) { 
				file = new FileWriter(filename);
				bfile = new BufferedWriter(file);
				bfile.write(ifExistMessage + content + LF);
				returnvalue = true;
			}
		} catch(IOException e) {
			l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
		} finally {
			try {
				if (bfile != null) bfile.close();
				if (file != null) file.close();
			} catch (IOException e) { 
				l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());	
			}
		}
		return returnvalue;
	}
	
	/*
	 * @param filename The filename with path of the file to be written
	 * @param content The text to be written into the file
	 * @param l The Logger for logging error messages.
	 */	
	private boolean writeFileIfNotExists(String filename, String content, Logger l) {
		boolean returnvalue = false;
		String ifNotExistMessage = "<?php " + LF + "// *******  This file won't be overwritten by druid if PHP code is regenerated." + LF + "// *******  You may customize this file for your own purposes."+ LF + "?>" + LF ;
        Writer file = null;
        BufferedWriter bfile = null;
		File fileTest = new File(filename);
		if (!fileTest.exists())  {
			try { 
				file = new FileWriter(filename);
				bfile = new BufferedWriter(file);
				bfile.write(ifNotExistMessage + content + LF);
			} catch(IOException e) {
				l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
			} finally {
				try {
					if (bfile != null) bfile.close();
					if (file != null) file.close();
					returnvalue = true;
				} catch (IOException e) {
					l.log(Logger.ALERT, "(?) Exception occured --> " + e.getMessage());
				}
			}
		}
		return returnvalue;
	}
}

//==============================================================================
