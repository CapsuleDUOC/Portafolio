//==============================================================================
//===
//===   OjbParams
//===
//===   Copyright (C) by Antonio Gallardo & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================
package mod.datagen.generic.ojb;

import druid.data.DatabaseNode;
import druid.interfaces.Logger;

/**
 *  General parameters for OJB generation
 *
 * @author <a href="mailto:antonio@apache.org">Antonio Gallardo</a>
 * @version CVS $Id: OjbParams.java,v 1.7 2003/11/14 16:30:41 antoniog Exp $
*/
public class OjbParams
{
	// Variable Output Directory
	public String dirName;

	//	Variables DBConnection
	public String jcdAlias;
	public boolean defaultConnection;
	public String dbPlat;
	public String jdbcLevel;
	public String sequenceManager;
	public String dbAlias;
	public String dbUser;
	public String dbPassword;
	
	// Variables OJB
	public String ojbPackage;
	public String ojbClassSuffix;
	public boolean collectionDescriptor;
	public boolean referenceDescriptor;
	public boolean useDateConvertor;
	
	public Logger logger;
	public DatabaseNode dbNode;

	public OjbParams(Logger l, DatabaseNode node, Settings s, Ojb ojb)
	{
		logger = l;
		dbNode = node;
		dirName = node.modsConfig.getValue(ojb, "output");
		jcdAlias = s.getJcdAlias();
		defaultConnection = s.isDefaultConnection();
		dbPlat = s.getDbPlat();
		jdbcLevel = s.getJdbcLevel();
		sequenceManager = s.getSequenceManager();
		dbAlias = buildDbAlias(node, s);
		dbUser = s.getDbUser();
		dbPassword = s.getDbPassword();
		ojbPackage = s.getOjbPackage();
		ojbClassSuffix = s.getOjbClassSuffix();
		collectionDescriptor = s.isCollectionDescriptor();
		referenceDescriptor = s.isReferenceDescriptor();
		useDateConvertor = s.isDateConvertor();
	}
	
	private String buildDbAlias(DatabaseNode node, Settings s)
	{
		String str = "";
		if (s.getDbHost().length() > 0) {
			str = "//" + s.getDbHost();
			if (s.getDbPort().length() > 0)
				str += ":"+s.getDbPort();
			str += "/";
		}
		str += node.attrSet.getString("name").toLowerCase();
		return str;
	}		
}
