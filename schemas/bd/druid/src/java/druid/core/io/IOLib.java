//==============================================================================
//===
//===   IOLib
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.core.io;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.dlib.xml.XmlAttribute;
import org.dlib.xml.XmlElement;

import druid.core.AttribList;
import druid.core.AttribSet;
import druid.core.DruidException;
import druid.data.AbstractNode;
import druid.data.ModulesConfig;

//==============================================================================

class IOLib implements ElementNames
{
	//---------------------------------------------------------------------------
	//---
	//--- Loader
	//---
	//---------------------------------------------------------------------------

	public static void xmlToAttribSet(XmlElement elAttribs, AttribSet as)
	{
		if (elAttribs == null) return;

		java.util.List list = elAttribs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement elAttr = (XmlElement) list.get(i);

			String name  = elAttr.getAttributeValue(ATTR_NAME);
			String value = elAttr.getAttributeValue(ATTR_VALUE);
			String type  = elAttr.getAttributeValue(ATTR_TYPE);

			//--- if value == null we are loading the new format
			//--- (those with values moved from attrib to element level)

			if (value == null)
				value = elAttr.getValue();

			//--- if type is given, we are loading an attribset saved by a modConfig
			//--- method. In this case we must force the attrib into the set

			if (type != null)
			{
				if (type.equals(ATTR_TYPE_INT))    as.addAttrib(name, 0);
				if (type.equals(ATTR_TYPE_BOOL))   as.addAttrib(name, false);
				if (type.equals(ATTR_TYPE_STRING)) as.addAttrib(name, "");
			}

			if (as.contains(name))
			{
				if (as.isAttribAString(name))
					as.setString(name, value);

				else if (as.isAttribAnInt(name))
					as.setInt(name, Integer.parseInt(value));

				else if (as.isAttribABoolean(name))
					as.setBool(name, Boolean.valueOf(value).booleanValue());

				else
					throw new DruidException(DruidException.INC_STR, "Unknown type of attrib", name);
			}
			else
				System.out.println("Skipped unknown attribute --> " + name);
		}
	}

	//---------------------------------------------------------------------------

	public static void xmlToAttribList(XmlElement elList, AttribList al)
	{
		if (elList == null) return;

		java.util.List listHeader = elList.getChild(HEADER).getChildren();
		java.util.List listRows   = elList.getChildren(ROW);

		//--- pre-process : if we find the "type" attrib then the this attriblist
		//--- was saved by a modConfig method. In this case we must force the
		//--- attrib into the list

		for(int i=0; i<listHeader.size();i++)
		{
			XmlElement elName = (XmlElement) listHeader.get(i);

			String name = elName.getAttributeValue(ATTR_NAME);
			String type = elName.getAttributeValue(ATTR_TYPE);

			//--- if name == null we are loading the new format

			if (name == null)
				name = elName.getValue();

			if (type != null)
			{
				if (type.equals(ATTR_TYPE_INT))    al.addAttrib(name, 0);
				if (type.equals(ATTR_TYPE_BOOL))   al.addAttrib(name, false);
				if (type.equals(ATTR_TYPE_STRING)) al.addAttrib(name, "");
			}
		}

		for(int i=0; i<listRows.size(); i++)
		{
			XmlElement elRow = (XmlElement) listRows.get(i);

			java.util.List listElem = elRow.getChildren();

			AttribSet as = al.append();

			for(int j=0; j<listHeader.size(); j++)
			{
				XmlElement elName  = (XmlElement) listHeader.get(j);
				XmlElement elValue = (XmlElement) listElem.get(j);

				String name  = elName.getAttributeValue(ATTR_NAME);

				if (name == null)
					name = elName.getValue();

				String value = elValue.getAttributeValue(ATTR_VALUE);

				//--- if value == null we are loading the new format

				if (value == null)
					value = elValue.getValue();

				if (as.contains(name))
				{
					if (as.isAttribAString(name))
						as.setString(name, value);

					else if (as.isAttribAnInt(name))
						as.setInt(name, Integer.parseInt(value));

					else if (as.isAttribABoolean(name))
						as.setBool(name, Boolean.valueOf(value).booleanValue());

					else
						throw new DruidException(DruidException.INC_STR, "Unknown type of attrib", name);
				}
				else
					System.out.println("Skipped unknown attribute --> " + name);
			}
		}
	}

	//---------------------------------------------------------------------------

	public static void xmlToTree(XmlElement elRoot, AbstractNode rootNode, String name, Class c)
	{
		if (elRoot != null)
		{
			java.util.List list = elRoot.getChildren(name);

			for(int i=0; i<list.size(); i++)
			{
				XmlElement elChild   = (XmlElement) list.get(i);
				XmlElement elAttribs = elChild.getChild(ATTRIBS);

				AbstractNode node = createInstance(c);

				xmlToAttribSet(elAttribs, node.attrSet);
				xmlToDocs(elChild.getChild(DOCS), node);
				xmlToModsConfig(elChild.getChild(MODSCONFIG), node.modsConfig);

				node.setText(node.attrSet.getString("name"));
				rootNode.addChild(node);
			}
		}
	}

	//---------------------------------------------------------------------------

	public static void xmlToDocs(XmlElement elDocs, AbstractNode node)
	{
		if (elDocs == null) return;

		node.xmlDoc = elDocs;
	}

	//---------------------------------------------------------------------------

	private static AbstractNode createInstance(Class c)
	{
		try
		{
			return (AbstractNode) c.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Saver
	//---
	//---------------------------------------------------------------------------

	public static XmlElement attribsToXml(String name, AttribList al)
	{
		XmlElement el = new XmlElement(name);

		el.addChild(attribsToXml(al));

		return el;
	}

	//---------------------------------------------------------------------------

	public static XmlElement attribsToXml(String name, AttribSet as)
	{
		XmlElement el = new XmlElement(name);

		el.addChild(attribsToXml(as));

		return el;
	}

	//---------------------------------------------------------------------------

	public static XmlElement attribsToXml(AttribSet as)
	{
		return attribsToXml(as, false);
	}

	//---------------------------------------------------------------------------

	private static XmlElement attribsToXml(AttribSet as, boolean forceType)
	{
		XmlElement elAttribs = new XmlElement(ATTRIBS);

		for(Enumeration e=as.attribs(); e.hasMoreElements();)
		{
			String name  = (String) e.nextElement();
			Object value = as.getData(name);

			XmlElement elAttr = new XmlElement(ATTRIB, value.toString());

			elAttr.setAttribute(new XmlAttribute(ATTR_NAME,  name));
//			elAttr.setAttribute(new XmlAttribute(ATTR_VALUE, value.toString()));

			if (forceType)
				elAttr.setAttribute(new XmlAttribute(ATTR_TYPE, getAttribType(as, name)));

			elAttribs.addChild(elAttr);
		}

		return elAttribs;
	}

	//---------------------------------------------------------------------------

	public static XmlElement attribsToXml(AttribList al)
	{
		return attribsToXml(al, false);
	}

	//---------------------------------------------------------------------------

	/** The forceType parameter is only used by modsConfig methods to retrieve saved
	  * data because these methods have no means to know the data type
	  */

	private static XmlElement attribsToXml(AttribList al, boolean forceType)
	{
		XmlElement elList = new XmlElement(LIST);

		//--- create header

		XmlElement elHeader = new XmlElement(HEADER);
		elList.addChild(elHeader);

		for(int i=0; i<al.attribSize(); i++)
		{
			String name = al.getAttrib(i);

			XmlElement elElem = new XmlElement(HEADER_ELEM, name);

//			elElem.setAttribute(new XmlAttribute(ATTR_NAME, name));

			if (forceType)
				elElem.setAttribute(new XmlAttribute(ATTR_TYPE, getAttribType(al, name)));

			elHeader.addChild(elElem);
		}

		//------------------------------------------------------------------------
		//--- create body

		for(int i=0; i<al.size(); i++)
		{
			XmlElement elRow = new XmlElement(ROW);

			AttribSet as = al.get(i);

			for(int j=0; j<al.attribSize(); j++)
			{
				String name  = al.getAttrib(j);
				Object value = as.getData(name);

				XmlElement elElem = new XmlElement(ROW_ELEM, value.toString());

//				elElem.setAttribute(new XmlAttribute("value", value.toString()));

				elRow.addChild(elElem);
			}

			elList.addChild(elRow);
		}

		return elList;
	}

	//---------------------------------------------------------------------------

	private static String getAttribType(AttribSet as, String name)
	{
		if (as.isAttribAnInt(name))    return ATTR_TYPE_INT;
		if (as.isAttribAString(name))  return ATTR_TYPE_STRING;
		if (as.isAttribABoolean(name)) return ATTR_TYPE_BOOL;

		throw new DruidException(DruidException.INC_STR, "Unknown type of attrib", name);
	}

	//---------------------------------------------------------------------------

	private static String getAttribType(AttribList al, String name)
	{
		if (al.isAttribAnInt(name))    return ATTR_TYPE_INT;
		if (al.isAttribAString(name))  return ATTR_TYPE_STRING;
		if (al.isAttribABoolean(name)) return ATTR_TYPE_BOOL;

		throw new DruidException(DruidException.INC_STR, "Unknown type of attrib", name);
	}

	//---------------------------------------------------------------------------

	public static XmlElement treeToXml(AbstractNode node, String parent, String name)
	{
		XmlElement elRoot = new XmlElement(parent);

		for(int i=0; i<node.getChildCount(); i++)
		{
			AbstractNode child   = (AbstractNode) node.getChild(i);
			XmlElement   elChild = new XmlElement(name);

			elChild.addChild(attribsToXml(child.attrSet));
			elChild.addChild(modsConfigToXml(MODSCONFIG, child.modsConfig));
			docsToXml(child, elChild);

			elRoot.addChild(elChild);
		}

		return elRoot;
	}

	//---------------------------------------------------------------------------

	public static void docsToXml(AbstractNode node, XmlElement el)
	{
		if (!node.xmlDoc.isLeaf())
		{
			node.xmlDoc.setName(DOCS);
			el.addChild(node.xmlDoc);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- ModulesConfig loader
	//---
	//---------------------------------------------------------------------------

	public static void xmlToModsConfig(XmlElement elAttribs, ModulesConfig mc)
	{
		if (elAttribs == null) return;

		List groups = elAttribs.getChildren();

		//--- scan groups

		for(int i=0; i<groups.size(); i++)
		{
			XmlElement elGrp = (XmlElement) groups.get(i);
			String     group = elGrp.getName();
			Hashtable  htGrp = (Hashtable)  mc.htGroups.get(group);

			if (htGrp == null)
			{
				htGrp = new Hashtable();
				mc.htGroups.put(group, htGrp);
			}

			List mods = elGrp.getChildren();

			//--- scan modules

			for(int j=0; j<mods.size(); j++)
			{
				XmlElement elMod = (XmlElement) mods.get(j);
				String     modId = elMod.getName();
				Hashtable  htMod = (Hashtable)  htGrp.get(modId);

				if (htMod == null)
				{
					htMod = new Hashtable();
					htGrp.put(modId, htMod);
				}

				List attribs = elMod.getChildren();

				//--- scan attribs

				for(int k=0; k<attribs.size(); k++)
				{
					XmlElement elAttr = (XmlElement) attribs.get(k);

					elementToAttrib(elAttr, htMod);
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	private static void elementToAttrib(XmlElement elAttr, Hashtable htMod)
	{
		String name  = elAttr.getName();
		String value = elAttr.getAttributeValue(ATTR_VALUE);

		if (value != null)
			htMod.put(name, value);

		//--- we have an AttribSet, AttribList or xml

		else
		{
			String type = elAttr.getAttributeValue(ATTR_TYPE);

			if (type == null)
				htMod.put(name, elAttr.getValue());

			else
			{
				XmlElement elValue = (XmlElement) elAttr.getChildren().get(0);

				if (type.equals(ATTR_TYPE_SET))
				{
					AttribSet as = new AttribSet();
					xmlToAttribSet(elValue, as);
					htMod.put(name, as);
				}

				else if (type.equals(ATTR_TYPE_LIST))
				{
					AttribList al = new AttribList();
					xmlToAttribList(elValue, al);
					htMod.put(name, al);
				}

				else if (type.equals(ATTR_TYPE_XML))
				{
					htMod.put(name, elValue);
				}

				else
					System.out.println("Skipping unknown config elem type --> "+ type);
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- ModulesConfig saver
	//---
	//---------------------------------------------------------------------------

	public static XmlElement modsConfigToXml(String name, ModulesConfig mc)
	{
		XmlElement elRoot = new XmlElement(name);

		for(Enumeration e1=mc.htGroups.keys(); e1.hasMoreElements();)
		{
			String    group = (String)    e1.nextElement();
			Hashtable htGrp = (Hashtable) mc.htGroups.get(group);

			XmlElement elGrp = new XmlElement(group);

			for(Enumeration e2=htGrp.keys(); e2.hasMoreElements();)
			{
				String    modId = (String)    e2.nextElement();
				Hashtable htMod = (Hashtable) htGrp.get(modId);

				XmlElement elMod = new XmlElement(modId);

				for(Enumeration e3=htMod.keys(); e3.hasMoreElements();)
				{
					String attrib = (String) e3.nextElement();
					Object value  = htMod.get(attrib);

					XmlElement elAttr = attribToElement(attrib, value);

					if (elAttr != null)
						elMod.addChild(elAttr);
				}

				if (!elMod.isLeaf())
					elGrp.addChild(elMod);
			}

			if (!elGrp.isLeaf())
				elRoot.addChild(elGrp);
		}

		return elRoot;
	}

	//---------------------------------------------------------------------------

	private static XmlElement attribToElement(String attrib, Object value)
	{
		XmlElement elAttr = new XmlElement(attrib);

		if (value instanceof String)
			elAttr.setValue(value.toString());

		else if (value instanceof AttribSet)
		{
			AttribSet as = (AttribSet) value;

			if (as.isEmpty())
				elAttr = null;
			else
			{
				elAttr.setAttribute(new XmlAttribute(ATTR_TYPE, ATTR_TYPE_SET));
				elAttr.addChild(attribsToXml(as, true));
			}
		}

		else if (value instanceof AttribList)
		{
			AttribList al = (AttribList) value;

			if (al.size() == 0)
				elAttr = null;
			else
			{
				elAttr.setAttribute(new XmlAttribute(ATTR_TYPE, ATTR_TYPE_LIST));
				elAttr.addChild(attribsToXml(al, true));
			}
		}

		else if (value instanceof XmlElement)
		{
			elAttr.setAttribute(new XmlAttribute(ATTR_TYPE, ATTR_TYPE_XML));
			elAttr.addChild((XmlElement) value);
		}

		else
			throw new DruidException(DruidException.INC_STR, "Unknown instance of object", value);

		return elAttr;
	}
}

//==============================================================================
