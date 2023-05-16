#**
 This is Velocity template to generate docbook documentation
 @author Bruno Vernay
 @version 1.1  15-10-2003

    Todo :
        - Allow folder organisation

    Changes :
        - 11-10-2003 - Bruno : Construct folders'id with full folder path : "FolderPath"

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
    <title>${lang.database}</title>

    #foreach( $Folder in $Folders)
        #if ( !$Folder.getParent().getIsFolder() )
            #set ( $FolderPath = "folder" )
            #parse("folders.vm")
        #end
    #end

  </chapter>## Folders
  #else  ##if ( !$Folders.isEmpty() )
  <chapter>## ERROR  --------------------------------------------------
    <title>${lang.database}</title>
    <para>ERROR : You asked for a folder structured documentation but you didn't created them !?!</para>
  </chapter>## Folders
  #end  ##if ( !$Folders.isEmpty() )


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
