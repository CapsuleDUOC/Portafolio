//==============================================================================
//===
//===   UrlCreator
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman.urlcreator;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.treeview.TreeView;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.gui.treeview.TreeViewSelEvent;
import org.dlib.gui.treeview.TreeViewSelListener;
import org.dlib.xml.XmlDocument;
import org.dlib.xml.XmlElement;
import org.dlib.xml.reader.XmlReader;

import druid.core.config.Config;
import druid.dialogs.BasicConfigDialog;
import druid.util.gui.ImageFactory;
import druid.util.gui.renderers.SimpleTreeViewRenderer;

//==============================================================================

public class UrlCreator extends BasicConfigDialog implements TreeViewSelListener
{
	private TreeView      treeView;
	private ControlsPanel ctrlPanel;
	private DriversPanel  drvPanel;

	private XmlElement elData;

	private static final String DATA_FILE = "/jdbc/connections.xml";

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public UrlCreator(Frame frame)
	{
		super(frame);

		setTitle("URL creator");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String run()
	{
		if (elData == null)
		{
			try
			{
				XmlDocument xmlDoc = new XmlReader().read(Config.dir.data + DATA_FILE);

				elData = xmlDoc.getRootElement();

				treeView.setRootNode(buildTree());
				treeView.getRootNode().expand(true,3);
				ctrlPanel.setUrl(null);
				drvPanel.setDrivers(null);

				showDialog();
			}
			catch(Exception e)
			{
				System.out.println("Error loading file --> " +e);
				e.printStackTrace();
			}
		}
		else
		{
			clearCancelled();
			setVisible(true);
		}

		return isCancelled() ? null : ctrlPanel.getUrl();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected JComponent getCentralPanel()
	{
		treeView = new TreeView();
		treeView.addSelectionListener(this);
		treeView.setCellRenderer(new SimpleTreeViewRenderer(ImageFactory.GEAR));

		//--- setup work panel

		JPanel workPanel = new JPanel();

		ctrlPanel = new ControlsPanel();
		drvPanel  = new DriversPanel();

		FlexLayout flexL = new FlexLayout(1,2);
		flexL.setColProp(0, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);
		workPanel.setLayout(flexL);

		workPanel.add("0,0,x,x", ctrlPanel);
		workPanel.add("0,1,x,x", drvPanel);

		//--- setup layout

		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, workPanel);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		p.setContinuousLayout(true);

		Dimension d = new Dimension(100,50);

		treeView.setMinimumSize(d);
		workPanel.setMinimumSize(d);

		return p;
	}

	//---------------------------------------------------------------------------
	//---
	//--- TreeViewSelListener
	//---
	//---------------------------------------------------------------------------

	public void nodeSelected(TreeViewSelEvent e)
	{
		TreeViewNode node = e.getSelectedNode();

		if (node == null)
		{
			ctrlPanel.setUrl(null);
			drvPanel.setDrivers(null);
		}

		else
		{
			drvPanel.setDrivers((UrlInfo) node.getUserData());

			if (!node.isLeaf())
				ctrlPanel.setUrl(null);
			else
			{
				ctrlPanel.setUrl((UrlInfo) node.getUserData());
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private TreeViewNode buildTree()
	{
		TreeViewNode rootNode = new TreeViewNode();

		List dbList = elData.getChildren();

		for(int i=0; i<dbList.size(); i++)
		{
			XmlElement elDb = (XmlElement) dbList.get(i);

			TreeViewNode node = new TreeViewNode(elDb.getAttributeValue("name"));

			List urlList = elDb.getChildren("url");

			if (urlList.size() == 1)
			{
				UrlInfo ui = new UrlInfo((XmlElement) urlList.get(0), elDb);
				node.setUserData(ui);
			}
			else
			{
				UrlInfo ui = new UrlInfo(null, elDb);
				node.setUserData(ui);

				for(int j=0; j<urlList.size(); j++)
				{
					XmlElement elUrl = (XmlElement) urlList.get(j);

					ui = new UrlInfo(elUrl, elDb);

					TreeViewNode child = new TreeViewNode(elUrl.getAttributeValue("name"), ui);

					node.addChild(child);
				}
			}

			rootNode.addChild(node);
		}

		return rootNode;
	}
}

//==============================================================================
