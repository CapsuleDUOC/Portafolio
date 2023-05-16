//==============================================================================
//===
//===   AbstractNodeV
//===
//===   Copyright (C) by Misko Hevery & Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.velocity.nodes;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dlib.tools.HtmlLib;
import org.dlib.tools.IOLib;
import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlCodec;
import org.dlib.xml.XmlElement;
import org.dlib.xml.XmlUtil;

import druid.core.AttribSet;
import druid.core.DocManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.ViewNode;

//==============================================================================

public class AbstractNodeV
{
	protected AbstractNode node;
	protected AttribSet    as;

	private String descr;
	private String xmlDescr;
	private String htmlDescr;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	protected AbstractNodeV(AbstractNode node)
	{
		this.node=node;

		as = node.attrSet;

		descr     = DocManager.toText(node.xmlDoc);
		xmlDescr  = XmlCodec.encode(descr);
		htmlDescr = DocManager.toHtml(remapImages(node.xmlDoc));
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public static AbstractNodeV convertNode(AbstractNode node)
	{
		AbstractNodeV abstractNode = convertNode(node, "druid.util.velocity.nodes");

		if (abstractNode != null)
			return abstractNode;

		abstractNode = convertNode(node, "druid.util.velocity.nodes.datatypes");

		if (abstractNode != null)
			return abstractNode;

		return convertNode(node, "druid.util.velocity.nodes.er");
	}

	//---------------------------------------------------------------------------

	public boolean equals(Object obj)
	{
		if (obj != null)
		{
			AbstractNodeV other = (AbstractNodeV) obj;

			return other.node == this.node;
		}

		return false;
	}

	//---------------------------------------------------------------------------

	public static String getNodeType(AbstractNode node)
	{
		String className = node.getClass().getName();

		int index = className.lastIndexOf('.');
		className = className.substring(index + 1).toLowerCase();

		String ending = "node";

		if (className.endsWith(ending))
			className = className.substring(0, className.length() - ending.length());

		return className;
	}

	//---------------------------------------------------------------------------

	public boolean getIsDatabase()  { return (node instanceof DatabaseNode);  }
	public boolean getIsFolder()    { return (node instanceof FolderNode);    }
	public boolean getIsTable()     { return (node instanceof TableNode);     }
	public boolean getIsField()     { return (node instanceof FieldNode);     }
	public boolean getIsView()      { return (node instanceof ViewNode);      }
	public boolean getIsProcedure() { return (node instanceof ProcedureNode); }
	public boolean getIsFunction()  { return (node instanceof FunctionNode);  }
	public boolean getIsSequence()  { return (node instanceof SequenceNode);  }
	public boolean getIsNotes()     { return (node instanceof NotesNode);     }

	public Collection    getChildren() { return convertCollection(node.children());          }
	public AbstractNodeV getParent()   { return convertNode((AbstractNode)node.getParent()); }

	//---------------------------------------------------------------------------

	public String getName()      { return as.getString("name");          }
	public String getDescr()     { return descr;                         }
	public String getXmlName()   { return XmlCodec.encode(getName());    }
	public String getXmlDescr()  { return xmlDescr;                      }
	public String getHtmlName()  { return HtmlLib.encode(getName());     }
	public String getHtmlDescr() { return htmlDescr;                     }
	public int    getDepth()     { return node.getLevel();               }

	//---------------------------------------------------------------------------

    public String getNCNamePart(){ return XmlUtil.makeNCNameChar(getName());}

   	public String getFile()
    {
        String fileName = IOLib.getFNString(getName());
        if (node instanceof FolderNode)
        {
            AbstractNode folderNode = (AbstractNode) node;
            while ((folderNode = (AbstractNode) folderNode.getParent()) instanceof FolderNode)
                fileName = IOLib.getFNString(folderNode.attrSet.getString("name"))+ '_' + fileName;
        }
        return fileName;
    }

	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append(getName());
		buf.append("{\n");
		buf.append(as.toString());

		if (node instanceof FieldNode)
			buf.append(((FieldNode)node).fieldAttribs.toString());

		buf.append("}\n");

		return buf.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static AbstractNodeV convertNode(AbstractNode node, String prefix)
	{
//		if (node==null) return null;

		String className = node.getClass().getName();
		int index = className.lastIndexOf('.');

		className = prefix +"."+ className.substring(index + 1) +"V";

		Class clazz;

		try
		{
			clazz = Class.forName(className);
		}
		catch(ClassNotFoundException e)
		{
			return null;
		}

		try
		{
			Constructor   constructor  = clazz.getConstructor(new Class[]{ AbstractNode.class});
			AbstractNodeV abstractNode = (AbstractNodeV)constructor.newInstance(new Object[]{ node });

			return abstractNode;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}

	//---------------------------------------------------------------------------

	private XmlElement remapImages(XmlElement docs)
	{
		XmlElement newDocs = docs.duplicate();

		List list = newDocs.preorderEnum();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement el = (XmlElement) list.get(i);

			if (el.getName().equals(DocManager.IMAGE))
			{
				XmlAttribute attr = el.getAttribute(DocManager.ATTR_IMAGE_SRC);

				String file = new File(attr.getValue()).getName();

				attr.setValue("../custom/images/" + file);
			}
		}

		return newDocs;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Reserved methods
	//---
	//---------------------------------------------------------------------------

	protected static Collection convertCollection(Collection nodes)
	{
		Vector myNodes = new Vector();

		Iterator iter = nodes.iterator();

		 while(iter.hasNext())
			myNodes.add(convertNode((AbstractNode) iter.next()));

		return myNodes;
	}

	//---------------------------------------------------------------------------

	protected Collection convertCollection(Enumeration nodes)
	{
		Vector myNodes = new Vector();

		while(nodes.hasMoreElements())
			myNodes.add(convertNode((AbstractNode) nodes.nextElement()));

		return myNodes;
	}

	//---------------------------------------------------------------------------

	protected Collection convertCollection(AbstractNode parent)
	{
		Vector myNodes = new Vector();

		for(int i=0; i<parent.getChildCount(); i++)
			myNodes.add(convertNode((AbstractNode) parent.getChild(i)));

		return myNodes;
	}
}

//==============================================================================
