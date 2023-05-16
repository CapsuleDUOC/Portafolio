//==============================================================================
//===
//===   Php_mysql
//===
//===   Copyright (C) 2009 President and Fellows of Harvard College
//===   Author: Paul J. Morris  
//===   This file may be distributed under the terms of the GPL license.
//===   File last changed on $Date:$ by $Author:$ in $Rev:$.
//==============================================================================
package mod.datagen.code.php;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.datatypes.TypeInfo;

/**Generates MySQL specific PHP code for CRUD capable model classes based on Druid Table definitions.
 * @author mole
 *
 */
public class Php_mysql_decorator extends Php implements Php_crud_generator {
	private TableNode node;
	private Php parent;
	private String primaryKey;
	private ArrayList<FieldNode> primaryKeys;
	private boolean primaryKeyIsAutoAssigned;
	private int numberOfPrimaryKeys;
	private String nodeTableName;
	
	public Php_mysql_decorator(TableNode node, Php parent) throws WrongDbTypeException {
		if (parent.sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)==false) {
			throw new WrongDbTypeException("Trying to create code for wrong database type.  Creating code for  " + Settings.TYPE_DB_MYSQL + " when the selected type is " + sett.getNameDbType()); 
		}
		
	    this.node = node;
	    this.parent = parent;
	    nodeTableName = parent.getClassCodeName(node); 
        // Find out some details about the table that will be repeatedly used
		primaryKey = "";  // Will hold the name of the primary key field (or list of primary key fields) .
		primaryKeys = parent.getPrimaryKeyFieldNodes(node);
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
			if (parent.isAutoIncrement(primaryKeys.get(0))) { 
				primaryKeyIsAutoAssigned = true;
			}
		}
	    
		
	}

	/* (non-Javadoc)
	 * @see mod.datagen.code.php.Php_crud_generator#getLoadMethod()
	 */
	public StringBuffer getLoadMethod() { 
		StringBuffer w = new StringBuffer();  // holds result 
		String and = "";
		StringBuffer prepared = new StringBuffer();   // hold prepared statement and key/value pairs in $values
		StringBuffer preparedmk = new StringBuffer(); // hold key/value pairs for multiple field primary keys.
		StringBuffer wheremk = new StringBuffer();     // holds where key=value pairs for where clause for multiple field primary keys
		
		if (numberOfPrimaryKeys==0) {
			// Remove code from load function in cases where table doesn't have a defined primary key (but leave as class implements interface).
			if (primaryKey.equals("")) { w.append("// load, save and delete functions requires a primary key, none is defined for this table. ").append(LF).append(LF); }
			w.append("   public function load($pk) {").append(LF);
			w.append("        $returnvalue = false;").append(LF);
			w.append("        // no primary key defined for this table, can't be sure of deleting a single row.").append(LF);
			w.append("        return $returnvalue;").append(LF);
			w.append("   }").append(LF);
		} else {
			w.append("   // Function load() can take either the value of the primary key which uniquely identifies a particular row").append(LF);
			w.append("   // or an array of array('primarykeyfieldname'=>'value') in the case of a single field primary key").append(LF);
			w.append("   // or an array of fieldname value pairs in the case of multiple field primary key.").append(LF);
			if (numberOfPrimaryKeys>1) { 
				w.append("   // This table has a multiple field primary key, so load() must be given an array.").append(LF);				
			}
			w.append("   public function load($pk) {").append(LF);
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
				w.append("        global $connection;").append(LF);
			}
			w.append("        $returnvalue = false;").append(LF);
			w.append("        try {").append(LF);
			if (numberOfPrimaryKeys>1) { 
				w.append("             if (is_array($pk)) { ").append(LF);
				for (int i=0; i<numberOfPrimaryKeys; i ++) { 
					w.append("             $this->set" + primaryKeys.get(i) + "($pk[" + primaryKeys.get(i) + "]);").append(LF);
				}
				w.append("             } else { ;").append(LF);
				w.append("                 throw new Exception('Load given a single value, not an array, for a multiple field primary key.');").append(LF);	
				w.append("             };").append(LF);
			} else { 
				w.append("             if (is_array($pk)) { ").append(LF);
				w.append("                 $this->set" + primaryKey + "($pk["+ primaryKey +"]);").append(LF);
				w.append("             } else { ;").append(LF);
				w.append("                 $this->set" + primaryKey + "($pk);").append(LF);	
				w.append("             };").append(LF);
			}
			w.append("        } ").append(LF);
			w.append("        catch (Exception $e) { ").append(LF);
			w.append("             throw new Exception($e->getMessage());").append(LF);
			w.append("        }").append(LF);
			//
			// check if this is a new record
			if (numberOfPrimaryKeys==1) { 
				w.append("        if($this->").append(primaryKey).append(" != NULL) {").append(LF);
			} else { 
				w.append("        if(");
				and = "";
				for (int i=0; i<primaryKeys.size(); i++) { 
					w.append(and + primaryKeys.get(i).attrSet.getString("name") + " != NULL ");
					and = " && ";
				}
				w.append(") {").append(LF);
			}
			w.append("           $sql = 'SELECT ");
			prepared.append("           $preparesql = 'SELECT ");
			String key = "";
			and = "";
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				w.append(name);
				prepared.append(name);
				if (i< node.getChildCount() - 1 ) { w.append(", ");  prepared.append(", "); }
				if (DataLib.isPrimaryKey(f)) { 
					if (numberOfPrimaryKeys==1) { 
						key = name;
					} else { 
						if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
							key = key + name + " = ? ";
						}  else { 
							key = key + name + " = :pk" + i + " ";
						}
						preparedmk.append("           $values = array('pk"+ i +"' => $this->"+ name +");");
						wheremk.append(and + name + " = \"'. $this->" + name + ".'\" ");
						and = " and ";
					}
				}
			}
			if (numberOfPrimaryKeys==1) {
				w.append(" FROM " + nodeTableName + " WHERE "+ key + " = '.$this->"+ key+" ;").append(LF);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
					prepared.append(" FROM " + nodeTableName + " WHERE " + key + " = ? ';").append(LF);
				} else { 
					prepared.append(" FROM " + nodeTableName + " WHERE " + key + " = :pk ';").append(LF);
					prepared.append("           $values = array('pk' => $this->"+ key +");");
				} 
			} else { 
				w.append(" FROM " + nodeTableName + " WHERE " + wheremk + "' ;").append(LF);
				prepared.append(" FROM " + nodeTableName + " WHERE " + key + " ';").append(LF);	   
				prepared.append(preparedmk);
			}
			w.append(LF);
			// add database specific retrieval code
			// MySQL
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
				w.append("           $result = mysql_query($sql);").append(LF);
				w.append("           if ($result===false) { $this->error = mysql_error(); }").append(LF);
				w.append("           if (mysql_num_rows($result)==1) {").append(LF);
				w.append("                $row = mysql_fetch_object($result);").append(LF);
				w.append("                if ($row) {").append(LF); 	        	
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					if(!DataLib.isPrimaryKey(f)) { 
						String name = f.attrSet.getString("name");	        	
						w.append("                     $this->").append(name).append(" = $row->").append(name).append(";").append(LF) ;
					}
				}
				w.append("                    $returnvalue = true;").append(LF);  // end row				
				w.append("                }").append(LF);  // end row
				w.append("            }").append(LF);  // end result	
				w.append("            mysql_free_result($result);").append(LF); 				
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				w.append(prepared).append(LF);
				w.append("           if ($statement = $connection->prepare($preparesql)) { ").append(LF);
				String typeList = "";
				StringBuffer fieldList = new StringBuffer();
				String comma = "";
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					if(DataLib.isPrimaryKey(f)) { 
						String name = f.attrSet.getString("name");
						fieldList.append(comma).append("$this->").append(name);
						// Identify i, d, s, or b field type for field.					
						typeList = typeList + getMySQLIType(f);
						comma = ", ";
					}
				} 
				w.append("              $statement->bind_param(\""+ typeList +"\", "+ fieldList +");").append(LF);
				w.append("              $statement->execute();").append(LF);
				w.append("              $statement->bind_result(");
				comma = "";
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					//if(!DataLib.isPrimaryKey(f)) { 
					String name = f.attrSet.getString("name");	        	
					w.append(comma).append("$this->").append(name);
					comma = ", ";
					//}
				}
				w.append(");").append(LF);
				w.append("              $statement->fetch();").append(LF);
				w.append("              $statement->close();").append(LF);
				w.append("           }").append(LF);
			}
			//Oracle
			if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
				w.append("           $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
				w.append("           ociexecute($statement,OCI_DEFAULT);").append(LF);
				w.append("           $error = OCIError($statement);").append(LF);
				w.append("           if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
				w.append("            	 $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
				w.append("           	 $this->error = 'Query Error: ' . $sqltext;").append(LF);
				w.append("           }").append(LF);
				w.append("           while (ocifetch($statement)) {").append(LF);	        	
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					int fieldno = i+1;   // ociresults fields start at 1.
					if(!DataLib.isPrimaryKey(f)) { 
						// retrieve all except the primary key, which we used to get the row 
						String name = f.attrSet.getString("name");	        	
						w.append("                  $this->").append(name).append(" = ociresult($statement,").append(fieldno).append(");").append(LF) ;
					}
				}
				w.append("                $returnvalue = true;").append(LF); 			
				w.append("            }").append(LF);  // end ocifetch
				w.append("            oci_free_statement($statement);").append(LF);
			}	    
			//Postgresql
			if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
				w.append("           $result = pg_query($sql);").append(LF);
				w.append("           if (pg_num_rows($result)==1) {").append(LF);
				w.append("                $row = pg_fetch_object($result);").append(LF);
				w.append("                if ($row) {").append(LF); 	        	
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					if(!DataLib.isPrimaryKey(f)) { 
						String name = f.attrSet.getString("name");	        	
						w.append("                      $this->").append(name).append(" = $row->").append(name).append(";").append(LF) ;
					}
				}
				w.append("                    $returnvalue = true;").append(LF);  // end row				
				w.append("                }").append(LF);  // end row
				w.append("            }").append(LF);  // end result
				w.append("            pg_free_result($result);").append(LF);  			
			}	        
			if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
				w.append(prepared).append(LF);  // defines $preparedsql and $parameterarray with :pk named parameter 
				w.append("           $statement = $dbh->prepare($preparesql);").append(LF);
				w.append("           $executed = $dbh->execute($paramenterarray);").append(LF);
				w.append("           if ($executed) {").append(LF);
				w.append("                $row = $statement->fetch(PDO::FETCH_OBJ);").append(LF);
				w.append("                if ($row) {").append(LF); 	        	
				for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					if(!DataLib.isPrimaryKey(f)) { 
						String name = f.attrSet.getString("name");	        	
						w.append("                      $this->").append(name).append(" = $row->").append(name).append(";").append(LF) ;
					}
				}
				w.append("                    $returnvalue = true;").append(LF);  // end row				
				w.append("                }").append(LF);  // end row
				w.append("            }").append(LF);  // end result
				w.append("            pg_free_result($result);").append(LF);  			
			}	        
			w.append(LF);
			w.append("            $this->").append(loadedvar).append(" = true;").append(LF); 
			w.append("            $this->dirty = false;").append(LF);
			w.append("        } else { ").append(LF);
			w.append("        }").append(LF); // end new record
			w.append("        return $returnvalue;").append(LF);
			w.append("    }");  // end function load
		}
		
		return w;
	}

	@Override
	public StringBuffer getCountMethod() {
		StringBuffer w = new StringBuffer();  // holds result;
		
		w.append("   public function count() {").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
			w.append("        global $connection;").append(LF);
		}
        w.append("        $returnvalue = false;").append(LF);
        w.append("        $sql = 'SELECT count(*)  FROM " + nodeTableName + "';").append(LF);
        // add database specific retrieval code
        // MySQL
        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
            w.append("        $result = mysql_query($sql);").append(LF);
            w.append("        if ($result===false) { $this->error = mysql_error(); }").append(LF);
            w.append("        if (mysql_num_rows($result)==1) {").append(LF);
            w.append("             $row = mysql_fetch_row($result);").append(LF);
            w.append("             if ($row) {").append(LF);                
            w.append("                 $returnvalue = $row[0];").append(LF);  // end row                
            w.append("             }").append(LF);  // end row
            w.append("         }").append(LF);  // end result   
            w.append("         mysql_free_result($result);").append(LF);                
        }
        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
            w.append("        if ($result = $connection->query($sql)) { ").append(LF);
            w.append("           if ($result->num_rows()==1) {").append(LF);
            w.append("             $row = $result->fetch_row();").append(LF);
            w.append("             if ($row) {").append(LF);                
            w.append("                $returnvalue = $row[0];").append(LF);  // end row                
            w.append("             }").append(LF);  // end row
            w.append("           }").append(LF);  // end row
            w.append("        } else { ").append(LF);  
            w.append("           $this->error = mysqli_error($connection); ").append(LF);
            w.append("        }").append(LF);  // end result
            w.append("        mysqli_free_result($result);").append(LF);      
        }            
        //Oracle
        if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
            w.append("        $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
            w.append("        ociexecute($statement,OCI_DEFAULT);").append(LF);
            w.append("        $error = OCIError($statement);").append(LF);
            w.append("        if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
            w.append("            $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
            w.append("            $this->error = 'Query Error: ' . $sqltext;").append(LF);
            w.append("        }").append(LF);
            w.append("        while (ocifetch($statement)) {").append(LF);              
            w.append("           $returnvalue = ociresult($statement,1);").append(LF);          
            w.append("        }").append(LF);  // end ocifetch
            w.append("        oci_free_statement($statement);").append(LF);
        }       
        //Postgresql
        if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
            w.append("        $result = pg_query($sql);").append(LF);
            w.append("        if (pg_num_rows($result)==1) {").append(LF);
            w.append("             $row = pg_fetch_row($result);").append(LF);
            w.append("             if ($row) {").append(LF);                
            w.append("                 $returnvalue = $row[0];").append(LF);  // end row                
            w.append("             }").append(LF);  // end row
            w.append("        }").append(LF);  // end result
            w.append("        pg_free_result($result);").append(LF);            
        }           
        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
            w.append("        // TODO: PDO: code generation").append(LF);	
        }
        
        w.append(LF);
        
        w.append("        $this->").append(loadedvar).append(" = true;").append(LF); 
        w.append("        return $returnvalue;").append(LF);
        w.append("    }");  // end function count  
        
        return w;
	}

	@Override
	public StringBuffer getDeleteMethod() {
		StringBuffer w = new StringBuffer();  // holds result;
		StringBuffer prepared = new StringBuffer();   // hold prepared statement and key/value pairs in $values
		StringBuffer preparedmk = new StringBuffer(); // hold key/value pairs for multiple field primary keys.
		StringBuffer wheremk = new StringBuffer();     // holds where key=value pairs for where clause for multiple field primary keys
		String comma = "";
		String and = "";
		if (numberOfPrimaryKeys==0) {
			w.append("   public function delete() {").append(LF);
			w.append("        $returnvalue = false;").append(LF);
			w.append("        // no primary key defined for this table, can't be sure of deleting a single row.").append(LF);
			w.append("        return $returnvalue;").append(LF);
			w.append("   }").append(LF);
		} else {
			w.append("   public function delete() {").append(LF);
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
				w.append("        global $connection;").append(LF);
			}
			w.append("        $returnvalue = false;").append(LF);
			// check if this is an existing record
			if (numberOfPrimaryKeys==1) { 
				w.append("        if($this->"+primaryKey+" != NULL) {").append(LF);
			} else { 
				w.append("        if(");
				and = "";
				for (int i=0; i<primaryKeys.size(); i++) { 
					w.append(and + primaryKeys.get(i).attrSet.getString("name") + " != NULL ");
					and = " && ";
				}
				w.append(") {").append(LF);
			}
			prepared.setLength(0);
			w.append("           $sql = 'SELECT ");
			prepared.append("           $preparedsql = 'SELECT ");
			and = "";
			preparedmk.setLength(0); // empty out the StringBuffer to hold the prepared query bits  
			wheremk.setLength(0);  // empty out the StringBuffer to hold the where clause
			
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				String name = f.attrSet.getString("name");
				w.append(comma + name);
				comma = ", ";
				if (DataLib.isPrimaryKey(f)) {
					wheremk.append(and + name + " = \"'.$this->" + name + ".'\" ");
					and = " and ";
					preparedmk.append(and + name + " = ? ");
				}
			}
			w.append(" FROM " + nodeTableName + " WHERE "+wheremk+" ' ;").append(LF);
			prepared.append(" FROM " + nodeTableName + " WHERE "+preparedmk+" ' ;").append(LF);
			w.append(LF);
			// add database specific deletion code
			// MySQL
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
				w.append("           $result = mysql_query($sql);").append(LF);
				w.append("           if ($result===false) { $this->error = mysql_error(); }").append(LF);
				w.append("           if (mysql_num_rows($result)==1) {").append(LF);
				w.append("                $sql = 'DELETE FROM " + nodeTableName + " WHERE "+ wheremk +" ';").append(LF);
				w.append("                if (mysql_query($sql)) { ").append(LF);	        	
				w.append("                    $returnvalue = true;").append(LF);  // end row
				w.append("                } else {").append(LF);
				w.append("                    $this->error = mysql_error();").append(LF);
				w.append("                }").append(LF);  // end row
				w.append("            }").append(LF);  // end result	
				w.append("            mysql_free_result($result);").append(LF); 				
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				// MySQL improved (using prepared statements)
				w.append(prepared);
                w.append("        if ($statement = $connection->prepare($preparedsql)) { ").append(LF);
		        String typeList = "";
		        StringBuffer fieldList = new StringBuffer();
		        comma = "";
		        for (int i = 0; i < node.getChildCount(); i++) {
					FieldNode f = (FieldNode) node.getChild(i);
					String name = f.attrSet.getString("name");
					fieldList.append(comma).append("$this->").append(name);
                    // Identify i, d, s, or b field type for field.					
					typeList = typeList + getMySQLIType(f);
					comma = ", ";
		        } 
		        w.append("           $statement->bind_param(\""+ typeList +"\", "+ fieldList +");").append(LF);
                w.append("           $statement->execute();").append(LF);
                w.append("           $statement->store_result();").append(LF);
                w.append("           if ($statement->num_rows()==1) {").append(LF);
				w.append("                $sql = 'DELETE FROM " + nodeTableName + " WHERE "+ preparedmk +" ';").append(LF);
                w.append("                if ($stmt_delete = $connection->prepare($sql)) { ").append(LF);
		        w.append("                   $stmt_delete->bind_param(\""+ typeList +"\", "+ fieldList +");").append(LF);
				w.append("                   if ($stmnt_delete->execute()) { ").append(LF);	        	
				w.append("                       $returnvalue = true;").append(LF);  // end row
				w.append("                   } else {").append(LF);
                w.append("                       $this->error = mysqli_error($connection); ").append(LF);
				w.append("                   }").append(LF);  // end row
				w.append("                   $stmt_delete->close();").append(LF);
                w.append("                }").append(LF);  // end row
                w.append("           } else { ").append(LF);  
                w.append("               $this->error = mysqli_error($connection); ").append(LF);
                w.append("           }").append(LF);  // end result
                w.append("           $tatement->close();").append(LF);
                w.append("        } else { ").append(LF);  
                w.append("            $this->error = mysqli_error($connection); ").append(LF);
                w.append("        }").append(LF);  // end if prepare
			}
			//Oracle
			if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
				w.append("           $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
				w.append("           ociexecute($statement,OCI_DEFAULT);").append(LF);
				w.append("           $error = OCIError($statement);").append(LF);
				w.append("           if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
				w.append("            	 $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
				w.append("           	 $this->error = 'Query Error: ' . $sqltext;").append(LF);
				w.append("           } else { ").append(LF);
				w.append("              $rows = 0;").append(LF);	
				w.append("              while (ocifetch($statement)) {").append(LF);	
				w.append("                 $rows++;").append(LF);	
				w.append("              }").append(LF);	
				w.append("              if ($rows==1) {").append(LF);
				w.append("                oci_free_statement($statement);").append(LF);
				w.append("                $sql = 'DELETE FROM " + nodeTableName + " WHERE "+wheremk+" ';").append(LF);
				w.append("                $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
				w.append("                ociexecute($statement,OCI_DEFAULT);").append(LF);
				w.append("                $error = OCIError($statement);").append(LF);
				w.append("                if (!$error) { ").append(LF);	        	
				w.append("                    $returnvalue = true;").append(LF);  // end row
				w.append("                } else {").append(LF);
				w.append("                    $this->error = 'Delete Failed: SQL Error';").append(LF);
				w.append("                }").append(LF);  // end row     
				w.append("                $returnvalue = true;").append(LF);
				w.append("              } else { ").append(LF);
				w.append("                $this->error = 'Delete Failed: Trying to delete more than one row.';").append(LF);
				w.append("              }").append(LF);
				w.append("           }").append(LF);  // end ocifetch
				w.append("           oci_free_statement($statement);").append(LF);
			}	    
			//Postgresql
			if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
				w.append("           $result = pg_query($sql);").append(LF);
				w.append("           if ($result!==false) {").append(LF);
				w.append("              if (pg_num_rows($result)==1) {").append(LF);
				w.append("                $sql = 'DELETE FROM " + nodeTableName + " WHERE "+ wheremk +" ';").append(LF);
				w.append("                if (pg_query($sql)) { ").append(LF);	        	
				w.append("                    $returnvalue = true;").append(LF);  // end row
				w.append("                } else {").append(LF);
				w.append("                    $this->error = pg_last_error();").append(LF);
				w.append("                }").append(LF);  // end row
				w.append("              }").append(LF);  // end row   
				w.append("            }").append(LF); 
				w.append("            pg_free_result($result);").append(LF);  			
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
				w.append("           //TODO: PBO: support ").append(LF);
			}
			w.append(LF);
			w.append("            $this->"+ loadedvar + " = true;").append(LF);
			w.append("            // record was deleted, so set PK to null").append(LF);
			for (int i=0; i<primaryKeys.size(); i++) { 
				w.append("            $this->"+ primaryKeys.get(i).attrSet.getString("name")  + " = NULL; ").append(LF);
			}
			w.append("        } else { ").append(LF);
			w.append("           throw new Exception('Unable to identify which record to delete, primary key is not set');").append(LF);
			w.append("        }").append(LF); // end new record
			w.append("        return $returnvalue;").append(LF);
			w.append("    }");  // end function delete			
	 
		}
        return w;
	}

	@Override
	public StringBuffer getLoadArrayKeyValueSearchMethod() {
		StringBuffer w = new StringBuffer(); // holds result 
		String comma = "";
		
		w.append(getSmallSeparator());
		w.append("   public function loadArrayKeyValueSearch($searchTermArray) {").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			w.append("       // ******* Note: $connection must be a mysqli object.").append(LF);
			w.append("       global $connection;").append(LF);
			w.append("       $returnvalue = array();").append(LF);
			w.append("       $and = '';").append(LF);
			w.append("       $wherebit = 'WHERE ';").append(LF);
			w.append("       foreach($searchTermArray as $fieldname => $searchTerm) {").append(LF);
			w.append("           if ($this->hasField($fieldname)) { ").append(LF);
			w.append("               $operator = '='; ").append(LF);
			w.append("               // change to a like search if a wildcard character is present").append(LF);
			w.append("               if (!(strpos($searchTerm,'%')===false)) { $operator = 'like'; }").append(LF);
			w.append("               if (!(strpos($searchTerm,'_')===false)) { $operator = 'like'; }").append(LF);
			w.append("               if ($searchTerm=='"+NULL_DISPLAY+"') { ").append(LF);
			w.append("                   $wherebit .= \"$and ($fieldname is null or $fieldname='') \"; ").append(LF);
			w.append("               } else { ").append(LF);
			//TODO: Type recognition
			w.append("                   $wherebit .= \"$and $fieldname $operator ? \";").append(LF);
			w.append("                   $types = $types . 's';").append(LF);
			//TODO: Fieldname recognition
			//w.append("                   $names = $names + $fieldname;").append(LF);
			w.append("               } ").append(LF);			
			w.append("               $and = ' and ';").append(LF);
			w.append("           }").append(LF);
			w.append("       }").append(LF);
		} else {
			w.append("       $returnvalue = array();").append(LF);
			w.append("       $and = '';").append(LF);
			w.append("       $wherebit = 'WHERE ';").append(LF);
			w.append("       foreach($searchTermArray as $fieldname => $searchTerm) {").append(LF);
			w.append("           if ($this->hasField($fieldname)) { ").append(LF);
			w.append("               $operator = '='; ").append(LF);
			w.append("               // change to a like search if a wildcard character is present").append(LF);
			w.append("               if (!(strpos($searchTerm,'%')===false)) { $operator = 'like'; }").append(LF);
			w.append("               if (!(strpos($searchTerm,'_')===false)) { $operator = 'like'; }").append(LF);
			w.append("               if ($searchTerm=='"+NULL_DISPLAY+"') { ").append(LF);
			w.append("                   $wherebit .= \"$and ($fieldname is null or $fieldname='') \"; ").append(LF);
			w.append("               } else { ").append(LF);
			w.append("                   $wherebit .= \"$and $fieldname $operator '$searchTerm' \";").append(LF);
			w.append("               } ").append(LF);			
			w.append("               $and = ' and ';").append(LF);
			w.append("           }").append(LF);
			w.append("       }").append(LF);
		}
		w.append("       $sql = \"SELECT "+ primaryKey +" FROM " + nodeTableName + " $wherebit\";").append(LF);
		w.append("       if ($wherebit=='') { ").append(LF);
		w.append("             $this->error = 'Error: No search terms provided';").append(LF);
		w.append("       } else {").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
			w.append("          $result = mysql_query($sql);").append(LF);
			w.append("          if ($result===false) { $this->error = mysql_error(); }").append(LF);
			w.append("          while ($row = mysql_fetch_row($result)) {").append(LF);
			w.append("              if ($row) {").append(LF);
			w.append("                  $id = $row[0];").append(LF);
			w.append("                  $obj = new " + nodeTableName + "();").append(LF);
			w.append("                  $obj->load($id);").append(LF);
			w.append("                  $returnvalue[] = $obj;").append(LF);
			w.append("              }").append(LF);
			w.append("          }").append(LF);
			w.append("          mysql_free_result($result);").append(LF);
		}
        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
            w.append("          $statement = $connection->prepare($sql);").append(LF);
            w.append("          $vars = Array();").append(LF);
            w.append("          $vars[] = $types;").append(LF);
            w.append("          $i = 0;").append(LF);
            // See: http://bugs.php.net/bug.php?id=43568 for binding of variables by reference
            w.append("          foreach ($searchTermArray as $value) { ").append(LF);
            w.append("               $varname = 'bind'.$i;  // create a variable name").append(LF);
            w.append("               $$varname = $value;    // using that variable name store the value ").append(LF);
            w.append("               $vars[] = &$$varname;  // add a reference to the variable to the array").append(LF);
            w.append("               $i++;").append(LF);
            w.append("           }").append(LF);
            w.append("           //$vars[] contains $types followed by references to variables holding each value in $searchTermArray.").append(LF);
            w.append("          call_user_func_array(array($statement,'bind_param'),$vars);").append(LF);
            w.append("          //$statement->bind_param($types,$names);").append(LF); 
			w.append("          $statement->execute();").append(LF);				
			if (numberOfPrimaryKeys==1 ) { 
				w.append("          $statement->bind_result($id);").append(LF);
				w.append("          $ids = array();").append(LF);
				w.append("          while ($statement->fetch()) {").append(LF);
				w.append("              $ids[] = $id;").append(LF);
				w.append("          } // double loop to allow all data to be retrieved before preparing a new statement. ").append(LF);
				w.append("          $statement->close();").append(LF);
				w.append("          for ($i=0;$i<count($ids);$i++) {").append(LF);
				w.append("              $obj = new " + nodeTableName + "();").append(LF);
				w.append("              $obj->load($ids[$i]);").append(LF);
				w.append("              $returnvalue[] = $obj;").append(LF);
				w.append("              $result=true;").append(LF);
				w.append("          }").append(LF);
			} else {
				String keys = "";
				String keysarray = "array(";
				comma = "";
				for (int keyn=0; keyn<numberOfPrimaryKeys; keyn++) { 
					keys = keys + comma + " $id"+ keyn + " ";
					keysarray = keysarray + " " + keyn + " => $id" + keyn;
					comma = ",";
				}
				w.append("          $statement->bind_result("+keys+");").append(LF);
				w.append("          while ($statement->fetch()) {").append(LF);
				w.append("              $obj = new " + nodeTableName + "();").append(LF);
				w.append("              $obj->load("+keysarray+");").append(LF);
				w.append("              $returnvalue[] = $obj;").append(LF);	
				w.append("              $result=true;").append(LF);
				w.append("          }").append(LF);
				w.append("          $statement->close();").append(LF);
			}
			w.append("          if ($result===false) { $this->error = mysqli_error($connection); }").append(LF);
        }
        if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
            w.append("           $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
            w.append("           ociexecute($statement,OCI_DEFAULT);").append(LF);
            w.append("           $error = OCIError($statement);").append(LF);
            w.append("           if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
            w.append("               $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
            w.append("               $this->error = 'Query Error: ' . $sqltext;").append(LF);
            w.append("           }").append(LF);
            w.append("           while (ocifetch($statement)) {").append(LF);              
            w.append("              $id = ociresult($statement,1);").append(LF);
			w.append("              $obj = new " + nodeTableName + "();").append(LF);
			w.append("              $obj->load($id);").append(LF);
			w.append("              $returnvalue[] = $obj;").append(LF);
            w.append("           }").append(LF);  // end ocifetch
            w.append("           oci_free_statement($statement);").append(LF);
        }
        if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
			w.append("          $result = pg_query($sql);").append(LF);
			w.append("          if ($result===false) { $this->error = pg_last_error(); }").append(LF);
			w.append("          while ($row = pg_fetch_row($result)) {").append(LF);
			w.append("              if ($row) {").append(LF);
			w.append("                  $id = $row[0];").append(LF);
			w.append("                  $obj = new " + nodeTableName + "();").append(LF);
			w.append("                  $obj->load($id);").append(LF);
			w.append("                  $returnvalue[] = $obj;").append(LF);
			w.append("              }").append(LF);
			w.append("          }").append(LF);
			w.append("          pg_free_result($result);").append(LF);
        }
        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
            w.append("           // TODO: PDO: code generation").append(LF);	
        }
        w.append("       }").append(LF);
		w.append("       return $returnvalue;").append(LF);
		w.append("   }	").append(LF);
		
		return w;
	}

	@Override
	public StringBuffer getSaveMethod() {
		StringBuffer w = new StringBuffer();  // holds result;
		StringBuffer prepared = new StringBuffer();   // hold prepared statement and key/value pairs in $values
		StringBuffer preparedmk = new StringBuffer(); // hold key/value pairs for multiple field primary keys.
		StringBuffer wheremk = new StringBuffer();     // holds where key=value pairs for where clause for multiple field primary keys
		String comma = "";
		String and = "";
		if (numberOfPrimaryKeys==0) {
			w.append("   public function save() {").append(LF);
			w.append("        $returnvalue = false;").append(LF);
			w.append("        // no primary key defined for this table, can't be sure of updating a single row.").append(LF);
			w.append("        return $returnvalue;").append(LF);
			w.append("   }").append(LF);
		} else {
			// ----------------------  save method ----------------------------
			// Does insert if class doesn't have the primary key set, otherwise does update
			// expects primary key to be a surrogate numeric primary key (serial/autoincrement).
			// 
			w.append("// Function save() will either save the current record or insert a new record.").append(LF);
			wheremk.setLength(0);
			prepared.setLength(0);
			preparedmk.setLength(0);
			String typeList = "";
			String fieldThisList = "";  // to hold list of: $this->fieldname,  
			StringBuffer fieldNameList = new StringBuffer();  // to hold list of : fieldname,
			if (numberOfPrimaryKeys>1) {
				w.append("   // Inserts new record if the value of $doinsert is set to true.").append(LF);				
				w.append("   // Otherwise updates the record identified by the primary key values in this instance.").append(LF);
				w.append("   public function save($doinsert=false) {").append(LF);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
					w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
					w.append("        global $connection;").append(LF);
				}
				w.append("        $returnvalue = false;").append(LF);
				// is this an insert or update?
				w.append("        if (");
				and = "";
				for (int i=0; i<primaryKeys.size(); i++) { 
					w.append(and + "$this->"+ primaryKeys.get(i).attrSet.getString("name") + "!= NULL ");
					and = " && ";
				}
				w.append(") {").append(LF); 			    
			} else { 
				w.append("   // Inserts new record if the primary key field in this table is null ").append(LF);
				w.append("   // for this instance of this object.").append(LF);
				w.append("   // Otherwise updates the record identified by the primary key value.").append(LF);
				w.append("   public function save() {").append(LF);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
					w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
					w.append("        global $connection;").append(LF);
				}
				w.append("        $returnvalue = false;").append(LF);
				w.append("        // Test to see if this is an insert or update.").append(LF);
				w.append("        if ($this->").append(primaryKey).append("!= NULL) {").append(LF); 			    
			}
			// update
			w.append("            $sql  = 'UPDATE  " + nodeTableName + " SET ';").append(LF);
			//prepared.append("            $sql  = 'UPDATE  " + nodeTableName + " SET ';").append(LF);
			w.append("            $isInsert = false;").append(LF);
			String fieldlist_truncated = "";  // list of fields excluding pk
			comma = "";
			String sqlType = "";	        
			String joiner = " WHERE ";  // build where clause, starting WHERE with AND separating key value pairs.
			StringBuffer whereClause = new StringBuffer();
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				if(!DataLib.isPrimaryKey(f)) { 
					// Field is Not primary key
					String name = f.attrSet.getString("name");
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
						prepared.append("            $sql .=  \"" + comma + name + " = ? \";").append(LF) ;
					} else {
					    w.append("            $sql .=  \"" + comma + name + " = '$this->"+ name + "' \";").append(LF) ;   
					}
				    fieldThisList = fieldThisList + comma + "$this->"+ name + " ";
				    fieldlist_truncated = fieldlist_truncated + comma + "$this->"+ name + " ";
				    fieldNameList.append(comma + " " + name + " ");
				    typeList = typeList + getMySQLIType(f);
				} else {
					// Field is primary key
					// find type of PK field(s)
					sqlType = DataTypeLib.getSqlType(f);
					String name = f.attrSet.getString("name");
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
						whereClause.append("            $sql .= \" "+ joiner +name+" = ? \";").append(LF);
						fieldThisList = fieldThisList + comma + "$this->"+ name + " ";
						if (numberOfPrimaryKeys==1 && (primaryKeyIsAutoAssigned==false || sett.isDetectAutoPK()==false)) {			
						    typeList = typeList + getMySQLIType(f);
						    fieldlist_truncated = fieldlist_truncated + comma + "$this->"+ name + " ";
						    fieldNameList.append(comma + " " + name + " ");
						} else {
							// don't append to fieldlist_truncated or typelist or fieldnamelist for insert
						}
					} else {
						if (sqlType != null && sqlType.equals("INTEGER")) {
							whereClause.append("            $sql .= \" "+ joiner +name+" = \".$this->"+name+".\" \";").append(LF);
						} else {
							whereClause.append("            $sql .= \" " + joiner +name+" = '\".$this->"+name+".\"' \";").append(LF);
						}    
					}
					joiner = " and "; // for any primary keys other than the first.
				}
				comma = ", ";
			}   
//			String joiner = " WHERE ";  // build where clause, starting WHERE with AND separating key value pairs.
//			for (int i = 0; i < node.getChildCount(); i++) {
//				FieldNode f = (FieldNode) node.getChild(i);
//				if(DataLib.isPrimaryKey(f)) {
//					// find type of PK field(s)
//					sqlType = DataTypeLib.getSqlType(f);
//					String name = f.attrSet.getString("name");
//					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
//						prepared.append("            $sql .= \" "+ joiner +name+" = ? \";").append(LF);
//						fieldList = fieldList + comma + "$this->"+ name + " ";
//						typeList = typeList + getMySQLIType(f);
//					} else { 
//						if (sqlType != null && sqlType.equals("INTEGER")) {
//							w.append("            $sql .= \" "+ joiner +name+" = \".$this->"+name+".\" \";").append(LF);
//						} else {
//							w.append("            $sql .= \" " + joiner +name+" = '\".$this->"+name+".\"' \";").append(LF);
//						}       
//					}
//					joiner = " and "; // for any primary keys other than the first.
//				}
//			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				w.append(prepared).append(LF);
			}
			w.append(whereClause);
			w.append("        } else {").append(LF); 
			// insert
			w.append("            $sql  = 'INSERT INTO " + nodeTableName + " ';").append(LF);
			//prepared.append("            $sql  = 'INSERT INTO " + nodeTableName + " ';").append(LF);
			prepared.setLength(0);
			w.append("            $isInsert = true;").append(LF);
			// remove auto increment primary key from field list
			// TODO: BUGID: 16
			if (numberOfPrimaryKeys==1 && (primaryKeyIsAutoAssigned==false || sett.isDetectAutoPK()==false)) {			
				w.append("if ($this->PK==NULL) { throw new Exception('Can\\'t insert record with null primary key for this table'); }").append(LF);
			} else { 
				fieldThisList = fieldlist_truncated;
				fieldThisList = fieldThisList.replaceFirst("^,", "");  // If field order has produced a leading comma, remove it.
				fieldNameList = new StringBuffer(fieldNameList.toString().replaceFirst("^,", ""));
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				prepared.append("            $sql .= '(" + fieldNameList + ") VALUES (';").append(LF);
			} else {
			    w.append("            $sql .= '(" + fieldlist_truncated + ") VALUES (';").append(LF);
			}
			comma = "";
			typeList = "";
			for (int i = 0; i < node.getChildCount(); i++) {
				FieldNode f = (FieldNode) node.getChild(i);
				// TODO: BUGID: 16 
				// Handle sequences/auto increment - this insert clause assumes that a single PK is auto increment
				// and multiple field primary keys include no auto increment fields.
				// this also assumes that default value and serials/auto increments on other fields are not used.
				if(DataLib.isPrimaryKey(f)==false || sett.isDetectAutoPK()==false || isAutoIncrement(f)==false){ 
					String name = f.attrSet.getString("name");	        	
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) { 
						prepared.append("            $sql .=  \" " + comma  + " ? \";").append(LF) ;
						//fieldList = fieldList + comma + "$this->" + name;
						typeList = typeList + getMySQLIType(f);
					} else { 
						w.append("            $sql .=  \" " + comma  + "'$this->").append(name).append("' \";").append(LF) ;	
					}
					comma = ", ";
				}  
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				prepared.append("            $sql .= ')';").append(LF);
			} else { 
			    w.append("            $sql .= ')';").append(LF);
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
				w.append(prepared).append(LF);
				// bind param needs to happen here
			}
			w.append("        }").append(LF); 
			// add database specific code
			// MySQL
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
				w.append("        $result = mysql_query($sql);").append(LF);
				w.append("        $affectedrows = mysql_affected_rows();").append(LF);
				w.append("        $this->error='';").append(LF); 
				w.append("        if ($result===false) { $this->error = mysql_error(); }").append(LF);
				w.append("        if ($affectedrows==0) { $this->error .= ' No records were changed'; }").append(LF);
				w.append("        if ($affectedrows>1) { $this->error .= ' Warning: More than one record was changed'; }").append(LF);
				//TODO: distinguish between auto increment PK, single field PK, and multiple field PK. 
				if (numberOfPrimaryKeys==1 ) { 
					w.append("        if ($isInsert) { ").append(LF); 
					w.append("            if (mysql_insert_id()) {").append(LF);
					w.append("                $this->"+primaryKey+" = mysql_insert_id();").append(LF);
					w.append("                $returnvalue = true;").append(LF); 
					w.append("            }").append(LF);  
					w.append("        } else {").append(LF);
					w.append("           if ($this->error=='') { ").append(LF); 
					w.append("                $returnvalue = true;").append(LF); 
					w.append("           };").append(LF); 
					w.append("        }").append(LF);
				} else {
					w.append("        if ($affectedrows==1) { $returnvalue = true; }").append(LF);
				}
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
                w.append("        if ($statement = $connection->prepare($sql)) { ").append(LF);
				w.append("           $statement->bind_param(\""+ typeList +"\", "+ fieldThisList +");").append(LF);
                w.append("           $statement->execute();").append(LF);
                w.append("           if ($statement->num_rows()!=1) {").append(LF);  
                w.append("               $this->error = $statement->error; ").append(LF);
                w.append("           }").append(LF);  // end result
                w.append("           $statement->close();").append(LF);
                w.append("        } else { ").append(LF);  
                w.append("            $this->error = mysqli_error($connection); ").append(LF);
                w.append("        }").append(LF);  // end if prepare
				w.append("        if ($this->error=='') { ").append(LF); 
				w.append("            $returnvalue = true;").append(LF); 
				w.append("        };").append(LF); 
			}
			//Oracle
			if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
				w.append("        if ($isInsert) { $sql .= ' RETURNING " +  primaryKey + " into :pk '; } ").append(LF);
				w.append("        $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
				w.append("        ociexecute($statement,OCI_DEFAULT);").append(LF);
				w.append("        $error = OCIError($statement);").append(LF);
				w.append("        if ($error) { ").append(LF);
				w.append("           if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
				w.append("              $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
				w.append("              $this->error = 'Query Error: ' . $sqltext;").append(LF);
				w.append("           }").append(LF);	
				// TODO: Check for affected rows / value of key
				w.append("        } else { ").append(LF);	 
				w.append("           if ($isInsert) { ").append(LF);
				w.append("                $row = oci_fetch_object($statement);").append(LF);
				w.append("                $pk = $row->pk;").append(LF);
				w.append("           }").append(LF);
				w.append("           $affected_rows = oci_num_rows($statement);").append(LF);
				w.append("           if ($affectedrows==0) { $this->error .= ' No records were changed'; }").append(LF);
				w.append("           if ($affectedrows>1) { $this->error .= ' Warning: More than one record was changed'; }").append(LF);	
				w.append("           if ($this->error=='') { ").append(LF); 
				w.append("                $returnvalue = true;").append(LF); 
				w.append("           };").append(LF); 		
				w.append("        }").append(LF); 
				w.append("        oci_free_statement($statement);").append(LF);
			}	    
			//Postgresql
			if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
				w.append("        $result = pg_query($sql);").append(LF);
				w.append("        $affectedrows = pg_affected_rows($result);").append(LF);
				w.append("        $this->error='';").append(LF); 
				w.append("        if ($result===false) { $this->error = mysql_error(); }").append(LF);
				w.append("        if ($affectedrows==0) { $this->error .= ' No records were changed'; }").append(LF);
				w.append("        if ($affectedrows>1) { $this->error .= ' Warning: More than one record was changed'; }").append(LF);
				if (numberOfPrimaryKeys==1 ) { 
					w.append("        if ($isInsert) { ").append(LF); 
					w.append("            $sql = 'select LASTVAL()';").append(LF);  // lastval without knowing sequencename requires postgresql 8.1+
					w.append("            $result = pg_query($sql);").append(LF);
					w.append("            if (pg_num_rows($result)==1) {").append(LF);
					w.append("                $row = pg_fetch_array($result);").append(LF);
					w.append("                if ($row) {").append(LF);
					w.append("                   $this->"+primaryKey+" = $row[0];").append(LF);
					w.append("                   $returnvalue = true;").append(LF);
					w.append("                }").append(LF);
					w.append("            }").append(LF);  
					w.append("        } else {").append(LF);
					w.append("           if ($this->error=='') { ").append(LF); 
					w.append("                $returnvalue = true;").append(LF); 
					w.append("           };").append(LF); 
					w.append("        }").append(LF); 
				} else { 
					w.append("        if ($affectedrows==1) { $returnvalue = true; }").append(LF);
				}
			}
			if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
				w.append("        // TODO: PDO: code generation").append(LF);	
			}
			w.append(LF);
			w.append("        $this->").append(loadedvar).append(" = true;").append(LF); 
			w.append("        return $returnvalue;").append(LF);
			w.append("    }");  // end function save
		}
		return w;
	}

	@Override
	public StringBuffer getKeySelectAllConcatJSONMethod() {
		StringBuffer w = new StringBuffer();  // holds result 
		
		w.append("   // Returns an array of primary key values (id) and concatenated values of all other fields (fields)").append(LF);
		w.append("   // wrapped as $values in: '{ \"identifier\":\"id\", \"items\": [ '.$values.' ] }';").append(LF);
		w.append("   // when druid_handler.php is called with druid_action=returnfkjson").append(LF);
		w.append("   // Used with dojo dijit.form.FilteringSelect to submit surrogate numeric key values from a text picklist ").append(LF);
		w.append("   public function keySelectAllConcatJSON($orderby='ASC') {").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
			w.append("        global $connection;").append(LF);
		}
		w.append("       $returnvalue = '';").append(LF);
		String pk = "";
		String pkcomma = "";
		String fieldscomma = "";
		String orderfields = "";
		String ordercomma = "";
		StringBuffer fields = new StringBuffer();
		for (int i = 0; i < node.getChildCount(); i++) {
			FieldNode f = (FieldNode) node.getChild(i);
			String name = f.attrSet.getString("name");
			if (DataLib.isPrimaryKey(f)) {
				pk = pk + pkcomma + name;
				pkcomma = ", ";
			} else { 
				if (DataLib.isIndexed(f)) { 
					 orderfields = orderfields + ordercomma + name;  
					 ordercomma = ", "; 
			    }  
				TypeInfo ti = DataTypeLib.getTypeInfo(f);
				if (ti.basicType.equals("MEDIUMTEXT") || ti.basicType.equals("LONGTEXT") || ti.basicType.endsWith("BLOB") ) {
					// omit large blobs						
				} else { 
				   fields.append(fieldscomma+"IFNULL("+name+",'')");
				   if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) { 
				       fieldscomma = ", ' ', ";
				   } else { 
					   fieldscomma = " || ' ' || ";
				   }
				}
			}
		}	
		// handle cases of no indexes other than pk, or no fields other than pk.
		if (orderfields.equals(""))  { orderfields = pk; }
		if (fields.equals(""))  { fields.append(pk); }
		w.append("       $order = '';").append(LF);
		w.append("       if ($orderby=='ASC') { $order = 'ASC'; } else { $order = 'DESC'; } ").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL))  { 
		   w.append("       $sql = \"SELECT " + pk + ", concat(" + fields + ") FROM " + nodeTableName + " order by " + orderfields +" $order \";").append(LF);
		} else { 
		   w.append("       $sql = \"SELECT " + pk + ", " + fields + " FROM " + nodeTableName + " order by " + orderfields +" $order \";").append(LF);
		}
		w.append("       $comma = '';").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {			        
			w.append("       $result = mysql_query($sql);").append(LF);
			w.append("       if ($result===false) { $this->error = mysql_error(); }").append(LF);
			w.append("       while ($row = mysql_fetch_row($result)) {").append(LF); 
			w.append("           if ($row) {").append(LF);
			w.append("               $pkval = trim($row[0]);").append(LF);
			w.append("               $fval = trim($row[1]);").append(LF);
			w.append("               if ($pkval!='') { ").append(LF);
			w.append("                   $pkval = str_replace('\"','&quot;',$pkval);").append(LF);
			w.append("                   $fval = str_replace('\"','&quot;',$fval);").append(LF);
			w.append("                   $returnvalue .= $comma . ' { \"id\":\"'.$pkval.'\", \"fields\": \"'.$fval.'\" } ';").append(LF);
			w.append("                   $comma = ', ';").append(LF);
			w.append("               }").append(LF);
			w.append("          }").append(LF);
			w.append("       }").append(LF);
			w.append("       mysql_free_result($result);").append(LF);
		}
        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			w.append("       if ($result = $connection->query($sql)) { ").append(LF);
			w.append("          while ($row = $result->fetch_row()) {").append(LF); 
			w.append("             if ($row) {").append(LF);
			w.append("                $pkval = trim($row[0]);").append(LF);
			w.append("                $fval = trim($row[1]);").append(LF);
			w.append("                if ($pkval!='') { ").append(LF);
			w.append("                    $pkval = str_replace('\"','&quot;',$pkval);").append(LF);
			w.append("                    $fval = str_replace('\"','&quot;',$fval);").append(LF);
			w.append("                    $returnvalue .= $comma . ' { \"id\":\"'.$pkval.'\", \"fields\": \"'.$fval.'\" } ';").append(LF);
			w.append("                    $comma = ', ';").append(LF);
			w.append("                }").append(LF);
			w.append("             }").append(LF);
			w.append("          }").append(LF);
			w.append("       } else { ").append(LF);
			w.append("          $this->error = mysqli_error(); ").append(LF);
			w.append("       }").append(LF);
			w.append("       $result->close();").append(LF);
        }
		if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {			        
			w.append("        $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
			w.append("        ociexecute($statement,OCI_DEFAULT);").append(LF);
			w.append("        $error = OCIError($statement);").append(LF);
			w.append("        if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
			w.append("            $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
			w.append("            $this->error = 'Query Error: ' . $sqltext;").append(LF);
			w.append("        }").append(LF);
			w.append("        while (ocifetch($statement)) {").append(LF);	        				
			w.append("            $key = trim(ociresult($statement,1));").append(LF);
			w.append("            $val = trim(ociresult($statement,2));").append(LF);
			w.append("            if ($key!='') {").append(LF);				
			w.append("                $key = str_replace('\"','&quot;',$key);").append(LF);
			w.append("                $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                     $returnvalue .= $comma . ' { \"id\":\"'.$key.'\", \"fields\": \"'.$val.'\" } ';").append(LF);
			w.append("                $comma = ', ';").append(LF);
			w.append("            }").append(LF);
			w.append("        }").append(LF);
			w.append("        oci_free_statement($statement);").append(LF);;
		}
		if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {			    
			w.append("        $result = pg_query($sql);").append(LF);
			w.append("        if ($result===false) { $this->error = pg_last_error(); }").append(LF);
			w.append("        while ($row = pg_fetch_row($result)) {").append(LF); 
			w.append("             if ($row) {").append(LF);
			w.append("                 $key = $row[0];").append(LF);
			w.append("                 $val = $row[1];").append(LF);
			w.append("                 if ($key!='') {").append(LF);
			w.append("                     $key = str_replace('\"','&quot;',$key);").append(LF);
			w.append("                     $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                     $returnvalue .= $comma . ' { \"id\":\"'.$key.'\", \"fields\": \"'.$val.'\" } ';").append(LF);
			w.append("                     $comma = ', ';").append(LF);
			w.append("                 }").append(LF);
			w.append("            }").append(LF);
			w.append("        }").append(LF);
			w.append("        pg_free_result($result);").append(LF);
		}
        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
        	// TODO: PDO: code generation
            w.append("        // TODO: PDO: code generation").append(LF);	
        }
		w.append("       return $returnvalue;").append(LF);
		w.append("   }");
        
        return w;
	}

	@Override
	public StringBuffer getLoadArrayByFullTextMethods() {
		StringBuffer w = new StringBuffer();
		String comma = "";
       
		AttribList idxAL = node.getDatabase().fieldAttribs;
		boolean hasFullText = false;
		Vector <AttribSet> fullTextIndexes = new Vector<AttribSet>();
		// Determine which indexes, if any are marked as full text indexes.
		for (int j=0; j<idxAL.size();j++) { 
			AttribSet as = idxAL.get(j);
			if (as.getString("scope").equals(FieldAttribs.SCOPE_FTINDEX)) {
				fullTextIndexes.add(as);				
                hasFullText = true;
			}
		}
		if (hasFullText && parent.sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) { 
			for (int i=0; i<fullTextIndexes.size(); i++) {
				// For each full text index
				StringBuffer fieldsInIndex = new StringBuffer();
				comma = "";
				for (int j=0; j<node.getChildCount(); j++) {
					// Find which fields in this table are in this index.
					FieldNode f = (FieldNode) node.getChild(j);
					if (f.fieldAttribs.getBool("" + fullTextIndexes.get(i).getInt("id") )) {
						fieldsInIndex.append(comma + f.attrSet.getString("name"));
						comma = ", ";
					}
				}
				if (fieldsInIndex.length()>0) { 
					// If any fields in this table are in this index create a search function.
					w.append("   public function loadArrayByFullText" + fullTextIndexes.get(i).getString("name") + "($searchterm) { ").append(LF);
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
						w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
						w.append("        global $connection;").append(LF);
					}
					w.append("       $returnvalue = array();").append(LF);				    
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
						w.append("       $sql = \"SELECT " + primaryKey+ "  FROM " + nodeTableName + " WHERE MATCH ("+fieldsInIndex+") AGAINST ('$searchterm')\";").append(LF);
						w.append("       $result = mysql_query($sql);").append(LF);
						w.append("       if ($result===false) { $this->error = mysql_error(); }").append(LF);
						w.append("       while ($row = mysql_fetch_row($result)) {").append(LF);
						w.append("           if ($row) {").append(LF);
						w.append("               $id = $row[0];").append(LF);
						w.append("               $obj = new " + nodeTableName + "();").append(LF);
						w.append("               $obj->load($id);").append(LF);
						w.append("               $returnvalue[] = $obj;").append(LF);
						w.append("           }").append(LF);
						w.append("       }").append(LF);
						w.append("       mysql_free_result($result);").append(LF);
					}
					//TODO: FullTextIndex search for non-mysql databases
					if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
						//TODO: mysqli support
						w.append("        // TODO: mysqli code generation").append(LF);	
						w.append("       $sql = \"SELECT " + primaryKey+ "  FROM " + nodeTableName + "   WHERE MATCH ("+fieldsInIndex+") AGAINST ('$searchterm')\";").append(LF);
					}
					if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
						w.append("        $sql = \"SELECT " + primaryKey+ "  FROM " + nodeTableName + "  WHERE \";").append(LF); 
						w.append("        $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
						w.append("        ociexecute($statement,OCI_DEFAULT);").append(LF);
						w.append("        $error = OCIError($statement);").append(LF);
						w.append("        if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
						w.append("            $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
						w.append("            $this->error = 'Query Error: ' . $sqltext;").append(LF);
						w.append("        }").append(LF);
						w.append("        while (ocifetch($statement)) {").append(LF);              
						w.append("           $id = ociresult($statement,1);").append(LF);
						w.append("           $obj = new " + nodeTableName + "();").append(LF);
						w.append("           $obj->load($id);").append(LF);
						w.append("           $returnvalue[] = $obj;").append(LF);
						w.append("        }").append(LF);  // end ocifetch
						w.append("        oci_free_statement($statement);").append(LF);
					}
					if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
						w.append("       $result = pg_query($sql);").append(LF);
						w.append("       if ($result===false) { $this->error = pg_last_error(); }").append(LF);
						w.append("       while ($row = pg_fetch_row($result)) {").append(LF);
						w.append("           if ($row) {").append(LF);
						w.append("               $id = $row[0];").append(LF);
						w.append("               $obj = new " + nodeTableName + "();").append(LF);
						w.append("               $obj->load($id);").append(LF);
						w.append("               $returnvalue[] = $obj;").append(LF);
						w.append("           }").append(LF);
						w.append("       }").append(LF);
						w.append("       pg_free_result($result);").append(LF);
					}
					if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
						w.append("        // TODO: PDO: code generation").append(LF);	
					}						
					w.append("       return $returnvalue;").append(LF);
					w.append("   }").append(LF);
				}
			} 
		}
		return w;
	}

	@Override
	public StringBuffer getLoadArrayByMethods() {
		StringBuffer w = new StringBuffer(); // holds result
		
		// Get list of primary keys 
		String key = "";
		String comma = "";
		for (int i = 0; i < node.getChildCount(); i++) {
			FieldNode f = (FieldNode) node.getChild(i);
			String name = f.attrSet.getString("name");
			if (DataLib.isPrimaryKey(f)) { 
				if (numberOfPrimaryKeys==1) { 
					key = name;
				} else { 
					key = key + comma +  name + " = :pk" + i + " ";
					comma = ", ";
					// TODO: Support multiple field primary keys.
				}
			}
		}
		
		// -------------------- load array by methods for each field with an index
		// Indexes are listed in the database fieldAttributes list.
		// Unlike DataLib.isPrimaryKey(node), there doesn't seem to be a method for
		// checking to see if a field is indexed  [added one].
		// AttribList idxAL = node.getDatabase().fieldAttribs;
		// w.append("/*" + node.getDatabase().fieldAttribs.toString() + "*/).append(LF);
		for (int i = 0; i < node.getChildCount(); i++) {
			FieldNode f = (FieldNode) node.getChild(i);
		    String fieldName = f.attrSet.getString("name");
		    if (DataLib.isIndexed(f)) { 
		    	// the current field is an index , create a load by method
				w.append("   public function loadArrayBy").append(fieldName).append("($searchTerm) {").append(LF);
				if (parent.sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
					w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
					w.append("        global $connection;").append(LF);
				}
				w.append("        $returnvalue = array();").append(LF);
				w.append("        $operator = \"=\";").append(LF);
				w.append("        // change to a like search if a wildcard character is present").append(LF);
				w.append("        if (!(strpos($searchTerm,\"%\")===false)) { $operator = \"like\"; }").append(LF);
				w.append("        if (!(strpos($searchTerm,\"_\")===false)) { $operator = \"like\"; }").append(LF);
		        w.append("        $sql = \"SELECT " + key + " FROM " + nodeTableName + " WHERE " + fieldName  + " $operator '$searchTerm'\";").append(LF);
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
		        	w.append("        $preparedsql = \"SELECT " + key + " FROM " + nodeTableName + " WHERE " + fieldName  + " $operator ? \";").append(LF);
		        }
	            // add database specific retrieval code
		        // MySQL
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {
		        	w.append("        $result = mysql_query($sql);").append(LF);
		        	w.append("        if ($result===false) { $this->error = mysql_error(); }").append(LF);
		        	w.append("        while ($row = mysql_fetch_row($result)) {").append(LF);
		        	w.append("             if ($row) {").append(LF);
		        	w.append("                  $id = $row[0];").append(LF) ;
		        	w.append("                  $obj = new "+ nodeTableName + "();").append(LF);
		        	w.append("                  $obj->load($id);").append(LF) ;
		        	w.append("                  $returnvalue[] = $obj;").append(LF) ;		
					w.append("             }").append(LF);  // end row
					w.append("        }").append(LF);  // end result	
					w.append("        mysql_free_result($result);").append(LF); 				
		        }
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			        w.append("        if ($statement = $connection->prepare($preparedsql)) { ").append(LF);
			        w.append("            $statement->bind_param(\"s\", $searchTerm);").append(LF);
			        w.append("            $statement->execute();").append(LF);
			        w.append("            $statement->bind_result($id);").append(LF);
			        w.append("            while ($statement->fetch()) { ;").append(LF);
		        	w.append("                $obj = new "+ nodeTableName + "();").append(LF);
		        	w.append("                $obj->load($id);").append(LF) ;
		        	w.append("                $returnvalue[] = $obj;").append(LF) ;		
					w.append("            }").append(LF);  // end row
			        w.append("            $statement->close();").append(LF);		
					w.append("        }").append(LF);  // end result					                	   	
		        }
		        //Oracle
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {
		        	w.append("           $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
		        	w.append("           ociexecute($statement,OCI_DEFAULT);").append(LF);
		        	w.append("           $error = OCIError($statement);").append(LF);
		        	w.append("           if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
		        	w.append("            	 $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
		        	w.append("           	 $this->error = 'Query Error: ' . $sqltext;").append(LF);
		        	w.append("           }").append(LF); 
		        	w.append("           while (ocifetch($statement)) {").append(LF);	        	
		        	w.append("                  $id = ociresult($statement,1);").append(LF) ;
		        	w.append("                  $obj = new "+ nodeTableName + "();").append(LF);
		        	w.append("                  $obj->load($id);").append(LF) ;
		        	w.append("                  $returnvalue[] = $obj;").append(LF) ;			
					w.append("            }").append(LF);  // end ocifetch
					w.append("            oci_free_statement($statement);").append(LF);
		        }	    
		        //Postgresql
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {
		        	w.append("           $result = pg_query($sql);").append(LF);
		        	w.append("           while ($row = pg_fetch_row($result)) { ").append(LF);
		        	w.append("                if ($row) {").append(LF); 	        	
		        	w.append("                   $id = $row[0];").append(LF) ;
		        	w.append("                   $obj = new "+ nodeTableName + "();").append(LF);
		        	w.append("                   $obj->load($id);").append(LF) ;
		        	w.append("                   $returnvalue[] = $obj;").append(LF) ;			
					w.append("                }").append(LF);  // end row
					w.append("            }").append(LF);  // end result
					w.append("            pg_free_result($result);").append(LF);  			
		        }
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
		            w.append("        // TODO: PDO: code generation").append(LF);	
		        }
		        
		    	w.append("        return $returnvalue;").append(LF);
		    	w.append("   }").append(LF);
		    }
		}
        return w;
	}

	@Override
	public StringBuffer getSelectDistinctMethods() {
		StringBuffer w = new StringBuffer();  // holds result;
		
        // --------------------- select distinct methods for each field with an index
        StringBuffer selectDistinctCases = new StringBuffer();
		for (int i = 0; i < node.getChildCount(); i++) {
			FieldNode f = (FieldNode) node.getChild(i);
			// Create a select distinct method for each indexed field, except a single field primary key.
			if (DataLib.isIndexed(f) && (numberOfPrimaryKeys > 1 || !DataLib.isPrimaryKey(f))) { 
				String name = f.attrSet.getString("name");
				selectDistinctCases.append("          case '").append(name).append("':").append(LF);
				selectDistinctCases.append("             $returnvalue = $this->selectDistinct").append(name).append("($startline,$link,$endline,$includecount,$orderbycount);").append(LF);
				selectDistinctCases.append("             break;").append(LF);
				w.append("   public function selectDistinct").append(name).append("($startline,$link,$endline,$includecount=false,$orderbycount=false) {").append(LF);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
					w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
					w.append("        global $connection;").append(LF);
				}
				w.append("        $returnvalue = '';").append(LF);
				w.append("        $order = ' "+name+" ';").append(LF);
				w.append("        if ($orderbycount) { $order = ' druid_ct DESC '; } ").append(LF);
				w.append("        $sql = \"SELECT count(*) as druid_ct, " + name + " FROM " + nodeTableName + " group by " + name + " order by $order \";").append(LF);
				if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {			        
					w.append("        $result = mysql_query($sql);").append(LF);
					w.append("        if ($result===false) { $this->error = mysql_error(); }").append(LF);
					w.append("        while ($row = mysql_fetch_row($result)) {").append(LF); 
					w.append("             if ($row) {").append(LF);
					w.append("                 $count = $row[0];").append(LF);
					w.append("                 $val = $row[1];").append(LF);
					w.append("                 $escaped = urlencode($row[1]);").append(LF);						
					w.append("                 if ($val=='') {").append(LF);
					w.append("                    $val = '"+NULL_DISPLAY+"'; ").append(LF);						
					w.append("                    $escaped = urlencode('"+NULL_DISPLAY+"');").append(LF);
					w.append("                 }").append(LF);	
					w.append("                 if ($link=='') { ").append(LF);
					w.append("                     $returnvalue .= \"$startline$val&nbsp;($count)$endline\";").append(LF);
					w.append("                 } else { ").append(LF);
					w.append("                     $returnvalue .= \"$startline<a href='$link&").append(name).append("=$escaped'>$val</a>&nbsp;($count)$endline\";").append(LF);
					w.append("                 }").append(LF);
					w.append("            }").append(LF);
					w.append("        }").append(LF);
					w.append("        mysql_free_result($result);").append(LF);
				}
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			        w.append("        if ($result = $connection->query($sql)) { ").append(LF);
					w.append("           while ($row = $result->fetch_row()) {").append(LF); 
					w.append("              if ($row) {").append(LF);
					w.append("                  $count = $row[0];").append(LF);
					w.append("                  $val = $row[1];").append(LF);
					w.append("                  $escaped = urlencode($row[1]);").append(LF);						
					w.append("                  if ($val=='') {").append(LF);
					w.append("                     $val = '"+NULL_DISPLAY+"'; ").append(LF);						
					w.append("                     $escaped = urlencode('"+NULL_DISPLAY+"');").append(LF);
					w.append("                  }").append(LF);	
					w.append("                  if ($link=='') { ").append(LF);
					w.append("                     $returnvalue .= \"$startline$val&nbsp;($count)$endline\";").append(LF);
					w.append("                  } else { ").append(LF);
					w.append("                     $returnvalue .= \"$startline<a href='$link&").append(name).append("=$escaped'>$val</a>&nbsp;($count)$endline\";").append(LF);
					w.append("                  }").append(LF);
					w.append("              }").append(LF);
					w.append("           }").append(LF);
			        w.append("           $result->close();").append(LF);
		        	w.append("        }").append(LF);
		        }
				if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {			        
					w.append("        $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
					w.append("        ociexecute($statement,OCI_DEFAULT);").append(LF);
					w.append("        $error = OCIError($statement);").append(LF);
					w.append("        if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
					w.append("            $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
					w.append("            $this->error = 'Query Error: ' . $sqltext;").append(LF);
					w.append("        }").append(LF);
					w.append("        while (ocifetch($statement)) {").append(LF);	        				
					w.append("            $count = ociresult($statement,1);").append(LF); 
					w.append("            $val = ociresult($statement,2);").append(LF);
					w.append("            $escaped = urlencode($row[1]);").append(LF);						
					w.append("            if ($val=='') {").append(LF);
					w.append("                $val = '"+NULL_DISPLAY+"'; ").append(LF);						
					w.append("                $escaped = urlencode('"+NULL_DISPLAY+"');").append(LF);
					w.append("            }").append(LF);	
					w.append("            if ($link=='') { ").append(LF);
					w.append("                $returnvalue .= \"$startline$val&nbsp;($count)$endline\";").append(LF);
					w.append("            } else { ").append(LF);
					w.append("                $returnvalue .= \"$startline<a href='$link&").append(name).append("=$escaped'>$val</a>&nbsp;($count)$endline\";").append(LF);
					w.append("                 }").append(LF);
					w.append("        }").append(LF);
					w.append("        oci_free_statement($statement);").append(LF);;
				}
				if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {			    
					w.append("        $result = pg_query($sql);").append(LF);
					w.append("        if ($result===false) { $this->error = pg_last_error(); }").append(LF);
					w.append("        while ($row = pg_fetch_row($result)) {").append(LF); 
					w.append("             if ($row) {").append(LF);
					w.append("                 $count = $row[0];").append(LF);
					w.append("                 $val = $row[1];").append(LF);
					w.append("                 $escaped = urlencode($row[1]);").append(LF);						
					w.append("                 if ($val=='') {").append(LF);
					w.append("                    $val = '"+NULL_DISPLAY+"'; ").append(LF);						
					w.append("                    $escaped = urlencode('"+NULL_DISPLAY+"');").append(LF);
					w.append("                 }").append(LF);						
					w.append("                 if ($link=='') { ").append(LF);
					w.append("                     $returnvalue .= \"$startline$val&nbsp;($count)$endline\";").append(LF);
					w.append("                 } else { ").append(LF);
					w.append("                     $returnvalue .= \"$startline<a href='$link&").append(name).append("=$escaped'>$val</a>&nbsp;($count)$endline\";").append(LF);
					w.append("                 }").append(LF);
					w.append("            }").append(LF);
					w.append("        }").append(LF);
					w.append("        pg_free_result($result);").append(LF);
				}
		        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
		            w.append("        // TODO: PDO: code generation").append(LF);	
		        }
				w.append("        return $returnvalue;").append(LF);
				w.append("    }").append(LF);
			}
        }     
        w.append(LF);  // end select distinct methods
        
        // generalized select distinct method taking fieldname as parameter
        w.append("   public function keySelectDistinct($fieldname,$startline,$link,$endline,$includecount=false,$orderbycount=false) {").append(LF);
        w.append("       $returnvalue = '';").append(LF);
        w.append("       switch ($fieldname) { ").append(LF);
        w.append(selectDistinctCases);
        w.append("       }").append(LF);
        w.append("       return $returnvalue;").append(LF);
        w.append("    }").append(LF);
        
        return w;
	}

	@Override
	public StringBuffer getkeySelectDistinctJSONMethod() {
		StringBuffer w = new StringBuffer(); 
		w.append("   public function keySelectDistinctJSON($field,$orderby='ASC') {").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
			w.append("        // ******* Note: $connection must be a mysqli object.").append(LF);
			w.append("        global $connection;").append(LF);
		}
		w.append("       $returnvalue = '';").append(LF);
		w.append("       if ($this->hasField($field)) { ").append(LF);
		w.append("          $order = '';").append(LF);
		w.append("          if ($orderby=='ASC') { $order = 'ASC'; } else { $order = 'DESC'; } ").append(LF);
		w.append("          $sql = \"SELECT DISTINCT $field FROM " + nodeTableName + " order by $field $order \";").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
		    w.append("          $preparemysql = \"SELECT DISTINCT ? FROM " + nodeTableName + " order by ? $order \";").append(LF);
		}
		w.append("          $comma = '';").append(LF);
		if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQL)) {			        
			w.append("          $result = mysql_query($sql);").append(LF);
			w.append("          if ($result===false) { $this->error = mysql_error(); }").append(LF);
			w.append("          while ($row = mysql_fetch_row($result)) {").append(LF); 
			w.append("             if ($row) {").append(LF);
			w.append("                 $val = trim($row[0]);").append(LF);
			w.append("                 if ($val!='') { ").append(LF);
			w.append("                     $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                     $returnvalue .= $comma . ' { \"'.$field.'\":\"'.$val.'\" } ';").append(LF);
			w.append("                     $comma = ', ';").append(LF);
			w.append("                 }").append(LF);
			w.append("             }").append(LF);
			w.append("          }").append(LF);
			w.append("          mysql_free_result($result);").append(LF);
		}
        if (sett.getNameDbType().equals(Settings.TYPE_DB_MYSQLI)) {
	        w.append("           if ($statement = $connection->prepare($preparemysql)) { ").append(LF);
	        w.append("              $stmt->bind_param(\"ss\", $field, $field);").append(LF);
	        w.append("              $stmt->execute();").append(LF);
	        w.append("              $statement->bind_result($val);").append(LF);
	        w.append("              $stmt->fetch();").append(LF);
	        w.append("              $val = trim($val);");
			w.append("              if ($val!='') { ").append(LF);
			w.append("                  $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                  $returnvalue .= $comma . ' { \"'.$field.'\":\"'.$val.'\" } ';").append(LF);
			w.append("                  $comma = ', ';").append(LF);
			w.append("              }").append(LF);		        
	        w.append("              $stmt->close();").append(LF);
        	w.append("           }").append(LF);
        }
		if (sett.getNameDbType().equals(Settings.TYPE_DB_ORACLE)) {			        
			w.append("          $statement = oci_parse($GLOBALS['conn'],$sql);").append(LF);
			w.append("          ociexecute($statement,OCI_DEFAULT);").append(LF);
			w.append("          $error = OCIError($statement);").append(LF);
			w.append("          if ($error['offset']) {").append(LF);  // highlight the location of the error in the query string
			w.append("              $sqltext = substr($error['sqltext'],0,$error['offset']) . '*' .substr ($error['sqltext'],$error['offset']);").append(LF);
			w.append("              $this->error = 'Query Error: ' . $sqltext;").append(LF);
			w.append("          }").append(LF);
			w.append("          while (ocifetch($statement)) {").append(LF);	        				
			w.append("              $val = trim(ociresult($statement,1));").append(LF);
			w.append("              if ($val!='') {").append(LF);
			w.append("                  $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                  $returnvalue .= $comma . ' { \"'.$field.'\":\"'.$val.'\" } ';").append(LF);
			w.append("                  $comma = ', ';").append(LF);
			w.append("              }").append(LF);
			w.append("          }").append(LF);
			w.append("          oci_free_statement($statement);").append(LF);;
		}
		if (sett.getNameDbType().equals(Settings.TYPE_DB_POSTGRES)) {			    
			w.append("          $result = pg_query($sql);").append(LF);
			w.append("          if ($result===false) { $this->error = pg_last_error(); }").append(LF);
			w.append("          while ($row = pg_fetch_row($result)) {").append(LF); 
			w.append("               if ($row) {").append(LF);
			w.append("                   $val = $row[0];").append(LF);
			w.append("                   if ($val!='') {").append(LF);
			w.append("                       $val = str_replace('\"','&quot;',$val);").append(LF);
			w.append("                       $returnvalue .= $comma . ' { \"'.$field.'\":\"'.$val.'\" } ';").append(LF);
			w.append("                       $comma = ', ';").append(LF);
			w.append("                   }").append(LF);
			w.append("              }").append(LF);
			w.append("          }").append(LF);
			w.append("          pg_free_result($result);").append(LF);
		}
        if (sett.getNameDbType().equals(Settings.TYPE_DB_PDO)) {
            w.append("        // TODO: PDO: code generation").append(LF);	
        }
		w.append("       }").append(LF);
		w.append("       return $returnvalue;").append(LF);
		w.append("   }");
		
		return w;
	}

	@Override
	public StringBuffer getkeySelectDistinctJSONFilteredMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
