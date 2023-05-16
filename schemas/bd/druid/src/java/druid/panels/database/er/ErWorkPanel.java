//==============================================================================
//===
//===   ErWorkPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.panels.database.er;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import org.dlib.gui.GuiUtil;
import org.dlib.gui.MenuFactory;
import org.dlib.gui.MultiPanel;
import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.HtmlLib;

import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataModel;
import druid.core.DataTracker;
import druid.core.config.Config;
import druid.core.er.ErLink;
import druid.core.er.ErScrEntity;
import druid.core.er.ErScrView;
import druid.data.DatabaseNode;
import druid.data.FieldNode;
import druid.data.TableNode;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.data.er.Legend;
import druid.data.er.LegendColor;
import druid.dialogs.er.entityimport.EntityImportDialog;
import druid.dialogs.er.entityprop.EntityPropDialog;
import druid.util.gui.Dialogs;

//==============================================================================

public class ErWorkPanel extends MultiPanel implements DataModel
{
	private JScrollPane scrollPane = new JScrollPane();
	private ErViewPanel viewPanel  = new ErViewPanel();

	private ErView    erView;
	private ErScrView erScrView;

	//---------------------------------------------------------------------------

	public ErWorkPanel()
	{
		add("blank", new JPanel());
		add("panel", scrollPane);

		scrollPane.setViewportView(viewPanel);
	}

	//---------------------------------------------------------------------------

	public void setCurrentNode(TreeViewNode node)
	{
		erView = null;

		if (node == null || !(node instanceof ErView) || node.isRoot())
			show("blank");
		else
		{
			erView = (ErView) node;

			GuiUtil.setWaitCursor(this, true);
			erScrView = new ErScrView(erView, getGraphics());
			GuiUtil.setWaitCursor(this, false);

			viewPanel.setErScrView(erScrView);
			show("panel");
		}
	}

	public void saveDataToNode(TreeViewNode node) {}
}

//==============================================================================

class ErViewPanel extends JPanel implements 	MouseListener, ActionListener,
															MouseMotionListener, Scrollable
{
	private ErScrView   erScrView;
	private JPopupMenu  popup = new JPopupMenu();

	private int iSelX;
	private int iSelY;
	private int iObjX;
	private int iObjY;

	protected Object erSelObject;

	private boolean bDragging;
	private boolean bRecalcSize;

	private Cursor  crsMove = new Cursor(Cursor.MOVE_CURSOR);
	private Cursor  crsDef  = new Cursor(Cursor.DEFAULT_CURSOR);

	private EntityImportDialog entImpDlg;
	private EntityPropDialog   entPropDial;

	//---------------------------------------------------------------------------

	private Action delAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("ataio");
			if (erSelObject != null)
				pop_delete();
		}
	};

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ErViewPanel()
	{
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setDismissDelay(8000);

		addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), delAction);
	}

	//---------------------------------------------------------------------------

	private void addKeyBinding(KeyStroke ks, Action a)
	{
		getInputMap() .put(ks, a);
		getActionMap().put( a, a);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setErScrView(ErScrView view)
	{
		JViewport jvp = (JViewport)getParent();

		if (erScrView != null)
			erScrView.setCurrOffset(jvp.getViewPosition());

		erScrView   = view;
		bDragging   = false;
		erSelObject = null;

		Point p = erScrView.getCurrOffset();

		if (p != null)
			jvp.setViewPosition(p);

		recalcViewSize();
	}

	//---------------------------------------------------------------------------

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		//--- paint all entities

		erScrView.drawView(g);

		//--- draw focus if one object is selected

		if (erSelObject != null)
		{
			g.setColor(Color.blue);

			Rectangle r = ((ErScrEntity) erSelObject).getBounds();

			g.drawRect(r.x -4, r.y -4, r.width +7, r.height +7);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   MouseListener
	//---
	//---------------------------------------------------------------------------

	public void mousePressed(MouseEvent e)
	{
		requestFocusInWindow();

		iSelX = e.getX();
		iSelY = e.getY();

		erSelObject = erScrView.getObjectFromPoint(iSelX, iSelY);

		repaint();

		if (e.isMetaDown())
		{

			buildPopupMenu();
			popup.show(this, iSelX, iSelY);
		}
		else
		{
			if (erSelObject == null)
			{
				bDragging   = true;
				bRecalcSize = false;
				setCursor(crsMove);
			}
			else //--- left click on an object
			{
				if (erSelObject instanceof ErScrEntity)
				{
					ErScrEntity erScrEnt = (ErScrEntity) erSelObject;

					Rectangle r = erScrEnt.getBounds();

					if (erScrEnt.isMouseOnName(iSelX -r.x, iSelY -r.y)) //--- drag entity
					{
						bDragging   = true;
						bRecalcSize = false;
						setCursor(crsMove);

						iObjX = r.x;
						iObjY = r.y;
					}
					else //--- click on something else
					{
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	public void mouseReleased(MouseEvent e)
	{
		if (bDragging)
		{
			bDragging = false;
			setCursor(crsDef);

			if (bRecalcSize)
				recalcViewSize();
		}
	}

	//---------------------------------------------------------------------------

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e)  {}

	//---------------------------------------------------------------------------

	private void buildPopupMenu()
	{
		popup.removeAll();

		if (erSelObject == null)
		{
			popup.add(MenuFactory.createItem("pop_new",   "Add Entity", this));
		}
		else
		{
			popup.add(MenuFactory.createItem("pop_refs",  "Add referencing tables", this));
			popup.add(MenuFactory.createItem("pop_fkeys", "Add referenced tables",  this));
			popup.add(MenuFactory.createItem("pop_prop",  "Properties...",          this));
			popup.addSeparator();
			
			//--- add details menu
			
			JMenu details = MenuFactory.createMenu("Details");
			popup.add(details);
			
			details.add(MenuFactory.createItem("pop_det."+ErEntity.DEFAULT,      "Defalt (use view details)",           this));
			details.add(MenuFactory.createItem("pop_det."+ErEntity.ONLY_NAME,    "Only entity name",                    this));
			details.add(MenuFactory.createItem("pop_det."+ErEntity.NAME_AND_PKS, "Name and primary keys",               this));
			details.add(MenuFactory.createItem("pop_det."+ErEntity.NAME_PKS_FKS, "Name, primary keys and foreign keys", this));
			details.add(MenuFactory.createItem("pop_det."+ErEntity.ALL_FIELDS,   "All fields",                          this));
			details.add(MenuFactory.createItem("pop_det."+ErEntity.COMPLETE,     "Complete (include datatypes)",        this));
			
			//--- add color menu
			
			JMenu color = MenuFactory.createMenu("Color");
			popup.add(color);

			Legend legend = erScrView.getErView().legend;
			
			for (int i=0; i<legend.getChildCount(); i++)
			{
				LegendColor lg = (LegendColor) legend.getChildAt(i);
				
				color.add(MenuFactory.createItem("pop_col."+ lg.getId(), lg.getName(), this));
			}
			
			//--- add other stuff
			
			popup.addSeparator();
			popup.add(MenuFactory.createItem("pop_del",   "Delete Entity", this));
		}
	}

	//---------------------------------------------------------------------------
	//---
	//---   MouseMotionListener
	//---
	//---------------------------------------------------------------------------

	public void mouseDragged(MouseEvent e)
	{
		if (!bDragging) return;

		if (erSelObject == null) //--- nothing was selected
		{
			if (!e.isControlDown())
			{
				//--- scroll viewport

				JViewport jvp = (JViewport) getParent();

				Point p = jvp.getViewPosition();

				p.x += (iSelX - e.getX());
				p.y += (iSelY - e.getY());

				Dimension pd  = jvp.getViewSize();
				Dimension jvd = jvp.getSize();

				p.x = Math.min(p.x, pd.width - jvd.width);
				p.y = Math.min(p.y, pd.height - jvd.height);
				p.x = Math.max(p.x, 0);
				p.y = Math.max(p.y, 0);

				jvp.setViewPosition(p);
			}
			else
			{
				//--- scroll all entities

				int dx = e.getX() - iSelX;
				int dy = e.getY() - iSelY;

				erScrView.translate(dx, dy);

				iSelX = e.getX();
				iSelY = e.getY();

				repaint();
				bRecalcSize = true;
				DataTracker.setDataChanged();
			}
		}
		else
		{
			if (erSelObject instanceof ErScrEntity)
			{
				ErScrEntity erScrEnt = (ErScrEntity) erSelObject;

				Rectangle r = erScrEnt.getBounds();

				int dx = e.getX() - iSelX;
				int dy = e.getY() - iSelY;

				r.x = iObjX + dx;
				r.y = iObjY + dy;

				if (e.isControlDown())
				{
					//--- snap to grid

					int snap = Config.erView.snapSize;

					r.x = (r.x / snap) * snap;
					r.y = (r.y / snap) * snap;
				}

				r.x = Math.max(r.x, 0);
				r.y = Math.max(r.y, 0);

				//--- store new location

				AttribSet as = erScrEnt.getErEntity().attrSet;

				as.setInt("locX", r.x);
				as.setInt("locY", r.y);
			}

			repaint();
			bRecalcSize = true;
			DataTracker.setDataChanged();
		}
	}

	//---------------------------------------------------------------------------

	public void mouseMoved(MouseEvent e) {}

	//---------------------------------------------------------------------------
	//---
	//---   ActionListener
	//---
	//---------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("pop_new"))   pop_new();
		if (cmd.equals("pop_refs"))  pop_addRefs();
		if (cmd.equals("pop_fkeys")) pop_addFKeys();
		if (cmd.equals("pop_prop"))  pop_properties();
		if (cmd.equals("pop_del"))   pop_delete();
		
		if (cmd.startsWith("pop_det."))
			pop_setDetails(cmd.substring(8));
		
		if (cmd.startsWith("pop_col."))
			pop_setColor(cmd.substring(8));
	}

	//---------------------------------------------------------------------------

	private void pop_new()
	{
		if (entImpDlg == null)
			entImpDlg = new EntityImportDialog(GuiUtil.getFrame(this));

		entImpDlg.run(erScrView.getErView().getDatabase(), true);

		if (entImpDlg.isCancelled())
			return;

		String name = entImpDlg.getEntName();

		if (name.equals(""))
			name = "-UnNamed-";

		ErEntity erEnt = new ErEntity(name);

		erScrView.getErView().addChild(erEnt, false);

		erEnt.setDefLegendColor();
		erEnt.attrSet.setInt("locX", iSelX);
		erEnt.attrSet.setInt("locY", iSelY);

		//--- add selected tables

		List<Integer> v = entImpDlg.getTableIds();

		for(int id : v)
			erEnt.addTable(id);

		//--- add object to gui struct

		erScrView.addObject(erEnt);
		erScrView.rebuildLinks();

		recalcViewSize();
	}

	//---------------------------------------------------------------------------

	private void pop_addRefs()
	{
		ErScrEntity erScrEnt = (ErScrEntity) erSelObject;
		ErEntity    ent      = erScrEnt.getErEntity();

		Set<TableNode> linkedTables = new HashSet<TableNode>();
		
		for(int i=0; i<ent.getTableNum(); i++)
		{
			TableNode node = ent.getTableNodeAt(i);

			linkedTables.addAll(DataLib.getReferences(node, false));
		}

		addTables(erScrEnt, linkedTables);
	}
	
	//---------------------------------------------------------------------------
	
	private void pop_addFKeys()
	{
		ErScrEntity erScrEnt = (ErScrEntity) erSelObject;
		ErEntity    ent      = erScrEnt.getErEntity();

		Set<TableNode> fkeys = new HashSet<TableNode>();
		
		for(int i=0; i<ent.getTableNum(); i++)
		{
			TableNode node = ent.getTableNodeAt(i);

			for (int j=0; j<node.getChildCount(); j++)
			{
				FieldNode field = (FieldNode) node.getChildAt(j);
				
				if (field.isFkey())
					fkeys.add(field.getReferencedTable());
			}			
		}
		
		addTables(erScrEnt, fkeys);
	}
	
	//---------------------------------------------------------------------------
	
	private void addTables(ErScrEntity erScrEnt, Set<TableNode> tables)
	{
		ErEntity ent = erScrEnt.getErEntity();
		
		//--- remove tables already used inside er-view
		
		ErView erView = ent.getErView();
		
		for (Iterator<TableNode> iter = tables.iterator(); iter.hasNext();)
		{
			TableNode n = iter.next();
			
			for (int i=0; i<erView.getChildCount(); i++)
			{
				ErEntity aux = (ErEntity) erView.getChild(i);
				
				for(int j=0; j<aux.getTableNum(); j++)
					if (n == aux.getTableNodeAt(j))
						iter.remove();
			}
		}
		
		//--- add remaining tables
		
		if (tables.isEmpty())
			Dialogs.showInfo(this, "No tables found.");
		else
		{
			Rectangle bound = erScrEnt.getBounds();
			
			int pos    = 0;
			int cx     = bound.x + bound.width /2;
			int cy     = bound.y + bound.height/2;
			int radius = bound.width *2;
			int step   = 360 / tables.size();

			for (TableNode n : tables)
			{
				int    id   = n.attrSet.getInt("id");
				String name = n.attrSet.getString("name");
				
				ErEntity erEnt = new ErEntity(name);

				erScrView.getErView().addChild(erEnt, false);

				erEnt.setDefLegendColor();

				//--- add selected tables

				erEnt.addTable(id);

				//--- add object to gui struct

				ErScrEntity ese = erScrView.addObject(erEnt);

				//--- calculate a good screen location
			
				int x = cx + (int) (radius * Math.cos(pos*Math.PI/180)) - ese.getBounds().width /2;
				int y = cy + (int) (radius * Math.sin(pos*Math.PI/180)) - ese.getBounds().height/2;
				
				x = Math.max(0, x);
				y = Math.max(0, y);
				
				erEnt.attrSet.setInt("locX", x);
				erEnt.attrSet.setInt("locY", y);

				ese.getBounds().x = x;
				ese.getBounds().y = y;
				
				pos += step;
				
			}
			
			erScrView.rebuildLinks();
			recalcViewSize();
		}
	}
	
	//---------------------------------------------------------------------------
	
	private void pop_properties()
	{
		if (entPropDial == null)
			entPropDial = new EntityPropDialog(GuiUtil.getFrame(this));

		ErScrEntity erScrEnt = (ErScrEntity) erSelObject;

		entPropDial.run(erScrEnt.getErEntity());

		erScrEnt.rebuild();
		erScrView.rebuildLinks();

		recalcViewSize();
	}

	//---------------------------------------------------------------------------

	protected void pop_delete()
	{
		if (!Dialogs.confirm(this, "Delete entity confirmation", "Are you sure you want to delete the selected entity?"))
			return;
		
		ErEntity erEnt = ((ErScrEntity) erSelObject).getErEntity();
		TreeViewNode parent = (TreeViewNode) erEnt.getParent();
		parent.removeChild(erEnt);

		erScrView.removeObject(erSelObject);
		erScrView.rebuildLinks();
		erSelObject = null;

		recalcViewSize();
	}

	//---------------------------------------------------------------------------
	
	private void pop_setDetails(String details)
	{
		ErScrEntity erScrEnt = (ErScrEntity) erSelObject;
		ErEntity    erEnt    = erScrEnt.getErEntity();

		erEnt.attrSet.setString("details", details);
		erScrEnt.rebuild();
		recalcViewSize();
	}
	
	//---------------------------------------------------------------------------

	private void pop_setColor(String colorId)
	{
		ErScrEntity erScrEnt = (ErScrEntity) erSelObject;
		ErEntity    erEnt    = erScrEnt.getErEntity();

		erEnt.attrSet.setInt("colorId", new Integer(colorId));
		erScrEnt.rebuild();
		recalcViewSize();
		
	}
	
	//---------------------------------------------------------------------------
	//---
	//---   Private methods
	//---
	//---------------------------------------------------------------------------

	private void recalcViewSize()
	{
		setPreferredSize(erScrView.getErSize());
		revalidate();
		repaint();
	}

	//---------------------------------------------------------------------------
	//---
	//---   Tooltip handler
	//---
	//---------------------------------------------------------------------------

	public String getToolTipText(MouseEvent e)
	{
		ErScrEntity ent = (ErScrEntity) erScrView.getObjectFromPoint(e.getX(), e.getY());

		if (ent != null)
			return buildEntityToolTip(ent.getErEntity());

		ErLink erLink = erScrView.getLinkFromPoint(e.getX(), e.getY(), 6);

		if (erLink != null)
			return buildLinkToolTip(erLink);

		return null;
	}

	//---------------------------------------------------------------------------

	private String buildEntityToolTip(ErEntity ent)
	{
		StringBuffer sb = new StringBuffer("<HTML>");

		sb.append("<B>Used tables:</B>");
		sb.append("<UL>");

		Set<TableNode> linkedTables = new HashSet<TableNode>();
		
		for(int i=0; i<ent.getTableNum(); i++)
		{
			TableNode node = ent.getTableNodeAt(i);

			sb.append("<LI>" +HtmlLib.encode(node.attrSet.getString("name"))+ "</LI>");
			
			linkedTables.addAll(DataLib.getReferences(node, false));
		}

		sb.append("</UL>");

		if (!linkedTables.isEmpty())
		{
			sb.append("<B>Tables that references this entity:</B>");
			sb.append("<UL>");
			
			for (TableNode n : linkedTables)
				sb.append("<LI>" +HtmlLib.encode(n.attrSet.getString("name"))+ "</LI>");
			
			sb.append("</UL>");
		}
		
		return sb.toString();
	}
	
	//---------------------------------------------------------------------------
	
	private String buildLinkToolTip(ErLink link)
	{
		ErScrEntity scrEnt1 = link.getStartEntity();
		ErScrEntity scrEnt2 = link.getEndEntity();

		ErEntity ent1 = scrEnt1.getErEntity();
		ErEntity ent2 = scrEnt2.getErEntity();

		String name1 = HtmlLib.encode(ent1.attrSet.getString("name"));
		String name2 = HtmlLib.encode(ent2.attrSet.getString("name"));

		StringBuffer sb = new StringBuffer("<HTML>");

		//--- first part

		sb.append("<B>Exported keys from '"+name1+"' to '"+name2+"':</B>");
		sb.append("<UL>");
		calcRefs(ent1, ent2, sb);
		sb.append("</UL>");

		//--- second part

		sb.append("<B>Exported keys from '"+name2+"' to '"+name1+"':</B>");
		sb.append("<UL>");
		calcRefs(ent2, ent1, sb);
		sb.append("</UL>");

		return sb.toString();
	}

	//---------------------------------------------------------------------------

	private void calcRefs(ErEntity ent1, ErEntity ent2, StringBuffer sb)
	{
		DatabaseNode dbNode = null;

		for(int i=0; i<ent1.getTableNum(); i++)
		{
			TableNode table = ent1.getTableNodeAt(i);

			for(int j=0; j<table.getChildCount(); j++)
			{
				FieldNode field = (FieldNode) table.getChild(j);

				if (field.isFkey())
				{
					int refTable = field.attrSet.getInt("refTable");
					int refField = field.attrSet.getInt("refField");

					if (ent2.existsTable(refTable))
					{
						sb.append("<LI>");
						sb.append(HtmlLib.encode(table.attrSet.getString("name")));
						sb.append(".");
						sb.append(HtmlLib.encode(field.attrSet.getString("name")));
						sb.append(" --> ");

						if (dbNode == null)
							dbNode = table.getDatabase();

						TableNode refTableNode = dbNode.getTableByID(refTable);

						if (refTableNode == null)
							sb.append(HtmlLib.encode("<DELETED>"));
						else
						{
							sb.append(HtmlLib.encode(refTableNode.attrSet.getString("name")));
							sb.append(".");

							FieldNode refFieldNode = refTableNode.getFieldByID(refField);

							if (refFieldNode == null)
								sb.append(HtmlLib.encode("<DELETED>"));
							else
								sb.append(HtmlLib.encode(refFieldNode.attrSet.getString("name")));
						}

						sb.append("</LI>");
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Scrollable interface
	//---
	//---------------------------------------------------------------------------

	public boolean getScrollableTracksViewportHeight()
	{
		return (getParent().getHeight() > getPreferredSize().height);
	}

	//---------------------------------------------------------------------------

	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	//---------------------------------------------------------------------------

	public boolean getScrollableTracksViewportWidth()
	{
		return (getParent().getWidth() > getPreferredSize().width);
	}

	//---------------------------------------------------------------------------

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return Config.erView.scrollSize;
	}

	//---------------------------------------------------------------------------

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return (orientation == SwingConstants.HORIZONTAL) 	? visibleRect.width
																			: visibleRect.height;
	}
}

//==============================================================================
