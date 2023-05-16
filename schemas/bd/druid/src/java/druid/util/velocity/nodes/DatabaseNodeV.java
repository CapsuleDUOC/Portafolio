//==============================================================================
//===
//===   DatabaseNodeV
//===
//===   Copyright (C) by Misko Hevery.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import java.util.Collection;

import org.dlib.tools.HtmlLib;

import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.util.velocity.sets.AbstractSetV;

//==============================================================================

public class DatabaseNodeV extends FolderNodeV
{
	private String build;
	private String version;

	//---------------------------------------------------------------------------

	public DatabaseNodeV(AbstractNode node)
	{
		super(node);

		DatabaseNode dbNode = (DatabaseNode) node;

		build    = dbNode.getProjectNode().attrSet.getInt("build") +"";
		version  = "?.?";

		int size = dbNode.revisions.size();

		if (size > 0)
			version = dbNode.revisions.get(size -1).getString("version");
	}

	//---------------------------------------------------------------------------

	public String getPreSql()      { return as.getString("preSql");  }
	public String getPostSql()     { return as.getString("postSql"); }
	public String getHtmlPreSql()  { return HtmlLib.encode(getPreSql());  }
	public String getHtmlPostSql() { return HtmlLib.encode(getPostSql()); }
	public String getVersion()     { return version; }
	public String getBuild()       { return build;   }

	//---------------------------------------------------------------------------

	public Collection getRevisions()
	{
		return AbstractSetV.convertSet("RevisionV", getDb().revisions);
	}

	//---------------------------------------------------------------------------

	public Collection getFieldAttribs()
	{
		return AbstractSetV.convertSet("FieldAttribsV", getDb().fieldAttribs);
	}

	//---------------------------------------------------------------------------

	public AbstractNodeV getDataTypes()
	{
		return convertNode(getDb().dataTypes);
	}

	//---------------------------------------------------------------------------

	public Collection getErViews()
	{
		return convertCollection(getDb().erViews);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private DatabaseNode getDb() { return (DatabaseNode) node; }
}

//==============================================================================
