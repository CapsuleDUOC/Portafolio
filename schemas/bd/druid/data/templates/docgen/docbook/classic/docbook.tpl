#**
 This is Velocity template to generate docbook documentation
 @author Bruno Vernay
 @version 1.1  15-10-2003

    Todo :
        - Allow folder organisation

    Changes :
        - 11-10-2003 - Bruno : Construct folders'id with full folder path : "FolderPath"
        - 26-04-2004 - Andrea: Added fix by Gian Luigi Gragnani
		  
*### This is to avoid spaces in the beginning of the file.
##
## Global variables :
##
#set ( $fattribs = $db.fieldAttribs )
##
##-------------------------------------------------------------------------------
#macro( buildType $var )
	#if( $var.isTypeBool )
		Bool
	#elseif ( $var.isTypeString )
		String
	#elseif ( $var.isTypeInt )
		Integer
	#elseif ( $var.isTypeLong )
		Long
	#elseif ( $var.isTypeChar )
		Char
	#elseif ( $var.isTypeFloat )
		Float
	#elseif ( $var.isTypeDouble )
		Double
	#else
		????
	#end
#end
##===============================================================================
##
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE book PUBLIC "-//OASIS//DTD DocBook XML V4.2//EN"
   "http://www.oasis-open.org/docbook/xml/4.2/docbookx.dtd">
<?xml-stylesheet href="stylesheet.css" type="text/css"?>
<book>
  <bookinfo>
    <!-- ${lang.generatedOn} ${Sys.date} -->
	<title>${lang.docsForDatabase} : ${db.name}</title>
    #set ( $revisions = $db.revisions )
	#if ( !$revisions.isEmpty() )
     <revhistory>
      #foreach( $revision in $revisions)
        <revision>
            <revnumber>${revision.version}</revnumber>
            <date>${revision.date}</date>
            <revremark><![CDATA[${revision.descr}]]></revremark>
        </revision>
      #end
     </revhistory>
    #end
  </bookinfo>


  <preface>
      <title>Introduction</title>
      <para>${db.xmlDescr}</para>

      <itemizedlist>
          <listitem><para>Druid : <ulink url="http://druid.sourceforge.net/">http://druid.sourceforge.net/</ulink></para></listitem>
          <listitem><para>${lang.date.toUpperCase()} : ${Sys.date}</para></listitem>
          <listitem><para>${lang.version.toUpperCase()} : ${db.version}</para></listitem>
          <listitem><para>${lang.build.toUpperCase()} : ${db.build}</para></listitem>
      </itemizedlist>

		#if( (${db.preSql} != "") || (${db.postSql} != ""))
			<section>
				<title>${lang.preSql}, ${lang.postSql}</title>
                #if(${db.preSql} != "")
                    <para>${lang.preSql} : <programlisting><![CDATA[${db.PreSql}]]></programlisting></para>
                #end
                #if(${db.postSql} != "")
                    <para>${lang.postSql} : <programlisting><![CDATA[${db.postSql}]]></programlisting></para>
                #end
            </section>
        #end
  </preface>

  #set ( $Folders = $db.getAllFolders() )
  #if ( !$Folders.isEmpty() )
  <chapter>## Folders  --------------------------------------------------
    <title>${lang.folder}</title>

    #foreach( $Folder in $Folders)
        #if ( !$Folder.getParent().getIsFolder() )
            #set ( $FolderPath = "folder" )
            #parse("folders.vm")
        #end
    #end

  </chapter>## Folders
  #end  ##if ( !$Folders.isEmpty() )


  <chapter>## Tables  ---------------------------------------------------
    <title>${lang.table}</title>
    #foreach($table in ${db.getAllTables()})
    <section id="table_${table.getNCNamePart()}">
      <title>${table.Name}</title>
      #if (${table.descr}!="")
          <para>${table.xmlDescr}</para>
      #end
      <para>
        #set ( $referringTables = ${table.getReferringTables()} )
        #if  ( !$referringTables.isEmpty() )
            <emphasis>${lang.seeAlso} : </emphasis>
            #foreach($refTable in $referringTables)
              <link linkend="table_${refTable.getNCNamePart()}">${refTable.Name}</link> ;
            #end
            .
        #end
      </para>
      <table>
        <title>${lang.fieldSummary}</title>
        <tgroup cols="3">
          <thead>
            <row>
              <entry>${lang.name}</entry>
              <entry>${lang.type}</entry>
              <entry>${lang.description}</entry>
            </row>
          </thead>
          <tbody>
          #foreach($field in ${table.getFields()})
            <row>
              <entry>
                <emphasis>
                  <link linkend="field_${table.getNCNamePart()}.${field.getNCNamePart()}">${field.Name}</link>
                </emphasis>
              </entry>
              <entry>
              #if(${field.getIsPrimaryKey()})
                  PK,
              #end
              #if(${field.getIsForeignKey()})
                  FK,
              #end
              ##<link linkend="datatype_${field.getBasicType()}">${field.getSqlType()}</link>
              ${field.getSqlType()}
                  #if ( ${field.getSqlType()} != ${field.getType()})
                    #if( $field.isForeignKey )
                        #set ($ft = ${field.getForeignTable()})
                        #set ($ff = ${field.getForeignField()})
                        : <link linkend="table_${ft.getNCNamePart()}">${ft.getName()}</link>(
                        <link linkend="field_${ft.getNCNamePart()}.${ff.getNCNamePart()}">${ff.getName()}</link>)
                    #else
                        : <link linkend="datatype_${field.getType()}">${field.getType()}</link>
                    #end
                  #end
              .
              </entry>
              <entry>${field.xmlDescr}</entry>
            </row>
          #end
          </tbody>
        </tgroup>
      </table>
      <section>
        <title>${lang.field}</title>
        #foreach($field in ${table.getFields()})
         <section id="field_${table.getNCNamePart()}.${field.getNCNamePart()}">
          <title>${field.Name}</title>
          <para>${field.xmlDescr}</para>
          <itemizedlist>
            <listitem>
              <para>
                <emphasis>SQL Type : </emphasis>
                ##<link linkend="datatype_${field.getBasicType()}">${field.getSqlType()}</link> ;
                ${field.getSqlType()} ;
                #if( $field.isForeignKey )
                    #set ($ft = ${field.getForeignTable()})
                    #set ($ff = ${field.getForeignField()})
                    <emphasis>Reference : </emphasis>
                        <link linkend="table_${ft.getNCNamePart()}">${ft.getName()}</link>(
                        <link linkend="field_${ft.getNCNamePart()}.${ff.getNCNamePart()}">${ff.getName()}</link>).
                #else
                    <emphasis>Local Type : </emphasis><link linkend="datatype_${field.getType()}">${field.getType()}</link>.
                #end
              </para>
            </listitem>
            <listitem>
              <para>
                Attributs :
                <itemizedlist>
                #foreach( $attr in $fattribs )
                    #set( $val = $field.getFieldAttribVal( $attr ) )
                    #set( $name = ${attr.descr} )
                    #if($name == "")
                        #set( $name = ${attr.sqlName} )
                    #end
                    #if($name == "")
                        #set( $name = ${attr.Name} )
                    #end
                    <listitem>
                        <para>
                          #if (${val.value} == "true")
                            <emphasis>${name}</emphasis> : True.
                          #elseif (${val.value} == "false")
                            ${name} : False.
                          #elseif (${val.value} == "")
                            ${name} : <emphasis>Empty</emphasis>.
                          #else
                            <emphasis>${name}</emphasis> : ${val.value}.
                          #end
                      </para>
                    </listitem>
                #end
                </itemizedlist>
              </para>
            </listitem>
            #if( $field.isForeignKey )
            <listitem>
              <para>Actions
               <itemizedlist>
                <listitem>
                 <para><emphasis>On update : </emphasis><programlisting><![CDATA[${field.onUpdateAction}]]></programlisting></para>
                </listitem>
                <listitem>
                 <para><emphasis>On delete : </emphasis><programlisting><![CDATA[${field.onDeleteAction}]]></programlisting></para>
                </listitem>
               </itemizedlist>
              </para>
            </listitem>
            #end
          </itemizedlist>
         </section>
        #end  ##foreach($field in ${table.getFields()})
      </section>
      #set ( $vars = $table.vars )
	  #if ( !$vars.isEmpty() )
      <section>
        <title>${lang.tableVars}</title>
          <table>
            <title>${lang.tableVars}</title>
            <tgroup cols="4">
              <thead>
                <row>
                  <entry>${lang.name}</entry>
                  <entry>${lang.type}</entry>
                  <entry>${lang.value}</entry>
                  <entry>${lang.description}</entry>
                </row>
              </thead>
              <tbody>
              #foreach( $var in $vars )
                <row>
                  <entry>${var.name}</entry>
                  <entry>#buildType( $var )</entry>
                  <entry>${var.value}</entry>
                  <entry>${var.xmlDescr}</entry>
                </row>
              #end
              </tbody>
            </tgroup>
          </table>
      </section>
      #end ##if ( !$vars.isEmpty() )


    #set ( $sqlCommands = $table.sqlCommands )
	#if ( $sqlCommands != "" )
    <section>
      <title>${lang.sqlCommands}</title>
      <para>
         <programlisting><![CDATA[${sqlCommands}]]></programlisting>
      </para>
    </section>
    #end ##if ( $sqlCommands != ""  )



    #set ( $triggers = $table.triggers )
	#if ( !$triggers.isEmpty() )
    <section>
      <title>${lang.triggers}</title>
      <para>
       <itemizedlist>
       #foreach( $trigger in $triggers )
        <listitem>
         <para><emphasis>${trigger.Name} : </emphasis>
         ${trigger.xmlDescr}
         <programlisting><![CDATA[${trigger.activString} ${trigger.activEvent}
FOR EACH ${trigger.forEachString}
#if(${trigger.when} != "")WHEN(${trigger.When})
#end

${trigger.Code}]]></programlisting></para>
        </listitem>
       #end
       </itemizedlist>
      </para>
    </section>
    #end ##if ( !$triggers.isEmpty() )

    #set ( $rules = $table.rules )
	#if ( !$rules.isEmpty() )
    <section>
      <title>${lang.rules}</title>
      <para>
       <itemizedlist>
       #foreach( $rule in $rules )
        <listitem>
         <para><emphasis>${rule.Name} : </emphasis>
         ${rule.xmlDescr}
         <programlisting><![CDATA[${rule.Rule}]]></programlisting></para>
        </listitem>
       #end
       </itemizedlist>
      </para>
    </section>
    #end ##if ( !$rules.isEmpty() )

    </section>
    #end
  </chapter>## Tables

  #set ( $views = $db.getAllViews() )
  #if ( !$views.isEmpty() )
  <chapter>## View  -----------------------------------------------------
    <title>${lang.view}</title>
    #foreach( $view in $views)
    <section id="view_${view.getNCNamePart()}">
        <title>${view.Name}</title>
        #if (${view.Descr} != "")
            <para>${view.xmlDescr}</para>
        #end
        <programlisting><![CDATA[${view.Code}]]></programlisting>
    </section>
    #end ##foreach( $view in $views)
  </chapter>## View
  #end  ##if ( !$views.isEmpty() )

  #set ( $functions = $db.getAllFunctions() )
  #if ( !$functions.isEmpty() )
  <chapter>## functions  ------------------------------------------------
    <title>${lang.function}</title>
    #foreach( $function in $functions)
    <section id="function_${function.getNCNamePart()}">
        <title>${function.Name}</title>
        #if (${function.Descr} != "")
            <para>${function.xmlDescr}</para>
        #end
        <programlisting><![CDATA[${function.Code}]]></programlisting>
    </section>
    #end ##foreach( $function in $functions)
  </chapter>## function
  #end  ##if ( !$functions.isEmpty() )

  #set ( $Procedures = $db.getAllProcedures() )
  #if ( !$Procedures.isEmpty() )
  <chapter>## Procedures  -----------------------------------------------
    <title>${lang.procedure}</title>
    #foreach( $Procedure in $Procedures)
    <section id="procedure_${Procedure.getNCNamePart()}">
        <title>${Procedure.Name}</title>
        #if (${Procedure.Descr} != "")
            <para>${Procedure.xmlDescr}</para>
        #end
        <programlisting><![CDATA[${Procedure.Code}]]></programlisting>
    </section>
    #end ##foreach( $Procedure in $Procedures)
  </chapter>## Procedures
  #end  ##if ( !$Procedures.isEmpty() )

  #set ( $Sequences = $db.getAllSequences() )
  #if ( !$Sequences.isEmpty() )
  <chapter>## Sequences  -----------------------------------------------
    <title>${lang.sequence}</title>
    #foreach( $Sequence in $Sequences)
    <section id="sequence_${Sequence.getNCNamePart()}">
        <title>${Sequence.Name}</title>
        #if (${Sequence.Descr} != "")
            <para>${Sequence.xmlDescr}</para>
        #end
        <table>
          <title>${lang.attribs}</title>
          <tgroup cols="2">
            <thead>
              <row>
                <entry>${lang.increment}</entry>
                <entry>${lang.minValue}</entry>
                <entry>${lang.maxValue}</entry>
                <entry>${lang.start}</entry>
                <entry>${lang.cache}</entry>
                <entry>${lang.cycle}</entry>
                <entry>${lang.order}</entry>
              </row>
            </thead>
            <tbody>
              <row>
                <entry>${Sequence.increment}</entry>
                <entry>${Sequence.minValue}</entry>
                <entry>${Sequence.maxValue}</entry>
                <entry>${Sequence.start}</entry>
                <entry>${Sequence.cache}</entry>
                <entry>${Sequence.isCycleSet}</entry>
                <entry>${Sequence.isOrderSet}</entry>
              </row>
            </tbody>
          </tgroup>
        </table>
    </section>
    #end ##foreach( $Sequence in $Sequences)
  </chapter>## Sequences
  #end  ##if ( !$Sequences.isEmpty() )

  #set ( $Notes = $db.getAllNotes() )
  #if ( !$Notes.isEmpty() )
  <chapter>## Notes  ----------------------------------------------------
    <title>${lang.notes}</title>
    #foreach( $Note in $Notes)
    <section id="note_${Note.getNCNamePart()}">
        #if( $Note.isInfo )
            #set( $image = "Info : " )
        #elseif( $Note.isAlert )
            #set( $image = "Alert : " )
        #elseif( $Note.isDanger )
            #set( $image = "Danger : " )
        #else
            #set( $image = "? : " )
        #end
        <title>$image ${Note.Name}</title>
        #if (${Note.Descr} != "")
            <para>${Note.xmlDescr}</para>
        #end
    </section>
    #end ##foreach( $Note in $Notes)
  </chapter>## Notes
  #end  ##if ( !$Notes.isEmpty() )


  <chapter>## Data Types  ----------------------------------------------
    <title>${lang.datatypes}</title>
    <table>
      <title>${lang.constantTypes}</title>
      <tgroup cols="5">
        <thead>
          <row>
            <entry>${lang.datatypes_basic}</entry>
            <entry>${lang.datatypes_alias}</entry>
            <entry>${lang.name}</entry>
            <entry>${lang.domain}</entry>
            <entry>${lang.dataDEquiv}</entry>
          </row>
        </thead>
        <tbody>
        #foreach( $cdt in $db.dataTypes.constFolder.children)
            <row id="datatype_${cdt.Name}">
              <entry>${lang.datatypes_basic}</entry>
              <entry> </entry>
              <entry>${cdt.Name}</entry>
              <entry>${cdt.htmlDomain}</entry>
              <entry>${cdt.ddEquiv}</entry>
            </row>
            #foreach( $ca in $cdt.children )
              <row id="datatype_${ca.Name}">
                <entry> </entry>
                <entry>${lang.datatypes_alias}</entry>
                <entry>${ca.Name}</entry>
                <entry>${ca.htmlDomain}</entry>
                <entry>${ca.ddEquiv}</entry>
              </row>
            #end
        #end
        </tbody>
      </tgroup>
    </table>
    <table>
      <title>${lang.variableTypes}</title>
      <tgroup cols="6">
        <thead>
          <row>
            <entry>${lang.datatypes_basic}</entry>
            <entry>${lang.datatypes_alias}</entry>
            <entry>${lang.name}</entry>
            <entry>${lang.size}</entry>
            <entry>${lang.domain}</entry>
            <entry>${lang.dataDEquiv}</entry>
          </row>
        </thead>
        <tbody>
        #foreach( $vdt in $db.dataTypes.varFolder.children)
            <row>
              <entry>${lang.datatypes_basic}</entry>
              <entry> </entry>
              <entry>${vdt.Name}</entry>
              <entry>n/a</entry>
              <entry>n/a</entry>
              <entry>n/a</entry>
            </row>
            #foreach( $va in $vdt.children )
              <row id="datatype_${va.Name}">
                <entry> </entry>
                <entry>${lang.datatypes_alias}</entry>
                <entry>${va.Name}</entry>
                <entry>${va.size}</entry>
                <entry>${va.htmlDomain}</entry>
                <entry>${va.ddEquiv}</entry>
              </row>
            #end
        #end
        </tbody>
      </tgroup>
    </table>
  </chapter>## Data Types
</book>
