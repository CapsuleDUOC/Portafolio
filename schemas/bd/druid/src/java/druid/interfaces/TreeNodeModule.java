//==============================================================================
//===
//===   TreeNodeModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import java.awt.Frame;

import org.dlib.gui.treeview.TreeViewNode;

//==============================================================================

public interface TreeNodeModule extends BasicModule
{
	//--- environment constants

	public static final int PROJECT       =   1;
	public static final int DATATYPE      =   2;
	public static final int JDBC_STRUCT   =   4;
	public static final int JDBC_SQLNAVIG =   8;
	public static final int ER            =  16;
	public static final int ER_LEGEND     =  32;
	public static final int TRIGGER       =  64;
	public static final int TABLERULE     = 128;
	public static final int JDBC_TRIGGER  = 256;

	//--- constraints support
	public static final int CONSTRAINT      =  512;
	public static final int JDBC_CONSTRAINT = 1024;

	/** Called when the node has been selected by the user from the popup-menu.
	  * The frame can be used to open dialogs. Node is never null.
	  * If the user right clicks on no nodes, the root node is passed
	  */

	public void nodeSelected(Frame f, TreeViewNode node);

	/** Called when the user presses the right mouse-button on a node.
	  * Indicates if the module must be added to the popupmenu that is
	  * being built. The module should check the instance of node and
	  * return true if can handle that node. Node is never null.
	  * If the user right clicks on no nodes, the root node is passed
	  */

	public boolean isNodeAccepted(TreeViewNode node);

	/** Called after isNodeAccepted, if isNodeAccepted returned true. The module
	  * should check the node and its state to decide if the popup element
	  * must be enabled or not. Node is never null.
	  * If the user right clicks on no nodes, the root node is passed
	  */

	public boolean isNodeEnabled(TreeViewNode node);

	/** Text to display in the popupmenu
	  */

	public String getPopupText();

	/** Indicates where the module should be used. The module should return
	  * an OR between one or more values of "environment constants".
	  */

	public int getEnvironment();
}

//==============================================================================
