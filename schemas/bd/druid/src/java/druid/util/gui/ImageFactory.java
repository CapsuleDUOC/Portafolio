//==============================================================================
//===
//===   ImageFactory
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

//==============================================================================

public class ImageFactory
{
	public static ImageIcon NULL;

	public static ImageIcon ICON;

	public static ImageIcon NEW;
	public static ImageIcon CUT;
	public static ImageIcon COPY;
	public static ImageIcon PASTE;
	public static ImageIcon UP;
	public static ImageIcon DOWN;
	public static ImageIcon LEFT;
	public static ImageIcon RIGHT;
	public static ImageIcon MERGE;
	public static ImageIcon PRINT;
	public static ImageIcon BLACK_WHITE;

	public static ImageIcon DATABASE;
	public static ImageIcon OFOLDER;
	public static ImageIcon CFOLDER;
	public static ImageIcon TABLE;
	public static ImageIcon GHOST_TABLE;
	public static ImageIcon VIEW;
	public static ImageIcon PROCEDURE;
	public static ImageIcon FUNCTION;
	public static ImageIcon FIELD;
	public static ImageIcon SYNONYM;
	public static ImageIcon SEQUENCE;
	public static ImageIcon UDT;
	public static ImageIcon OTHER;

	public static ImageIcon ALIASDT;
	public static ImageIcon BASICDT;

	public static ImageIcon DELETE;
	public static ImageIcon SET;
	public static ImageIcon REDSET;
	public static ImageIcon UNKNOWN;

	public static ImageIcon REFRESH;
	public static ImageIcon UPDATE;
	public static ImageIcon COMMIT;
	public static ImageIcon ROLLBACK;

	public static ImageIcon LENS;
	public static ImageIcon BOMB;
	public static ImageIcon COLUMN;
	public static ImageIcon COL_FIT;
	public static ImageIcon COL_AUTOSIZE;
	public static ImageIcon FLOPPY;
	public static ImageIcon EXPORT;
	public static ImageIcon IMPORT;
	public static ImageIcon WIZARD;
	public static ImageIcon DROP_DOWN;
	public static ImageIcon USER;

	public static ImageIcon TRIGGER;
	public static ImageIcon GEAR;
	public static ImageIcon MODULE;
	public static ImageIcon POPUP;
	public static ImageIcon RULE;

	public static ImageIcon BOOK_OPEN;
	public static ImageIcon BOOK_CLOSED;
	public static ImageIcon PAGE;

	public static ImageIcon BOX_NONE;
	public static ImageIcon BOX_NW;
	public static ImageIcon BOX_NE;
	public static ImageIcon BOX_SW;
	public static ImageIcon BOX_SE;

	public static ImageIcon ER_VIEW;
	public static ImageIcon ER_ENTITY;

	public static ImageIcon CHKBOX_SET;
	public static ImageIcon CHKBOX_UNSET;
	public static ImageIcon CHKBOX_UNKNOWN;

	public static ImageIcon FONT_FAMIL;
	public static ImageIcon FONT_MONOSPACED;

	public static ImageIcon STYLE_PLAIN;
	public static ImageIcon STYLE_BOLD;
	public static ImageIcon STYLE_ITALIC;
	public static ImageIcon STYLE_BOLDITAL;
	public static ImageIcon STYLE_UNDERLINE;

	public static ImageIcon PARAGRAPH;
	public static ImageIcon PAR_LEFT;
	public static ImageIcon PAR_CENTER;
	public static ImageIcon PAR_RIGHT;

	public static ImageIcon IMAGE;
	public static ImageIcon STRUCTURE;

	public static ImageIcon LIST_ORDERED;
	public static ImageIcon LIST_UNORDERED;
	public static ImageIcon LIST_ITEM;

	public static ImageIcon INFO;
	public static ImageIcon ALERT;
	public static ImageIcon DANGER;
	public static ImageIcon INFO2;
	public static ImageIcon ALERT2;
	public static ImageIcon DANGER2;

	public static ImageIcon FIRST_AID;

	//---------------------------------------------------------------------------
	//--- cursors

	public static Cursor NODROP;

	//---------------------------------------------------------------------------
	//---
	//--- Init method
	//---
	//---------------------------------------------------------------------------

	public static void init(String path)
	{
		NULL          = new ImageIcon(path + "/null.gif");

		ICON          = new ImageIcon(path + "/icon.gif");

		NEW           = new ImageIcon(path + "/new.gif");
		CUT           = new ImageIcon(path + "/cut.gif");
		COPY          = new ImageIcon(path + "/copy.gif");
		PASTE         = new ImageIcon(path + "/paste.gif");
		UP            = new ImageIcon(path + "/arrow-up.png");
		DOWN          = new ImageIcon(path + "/arrow-down.png");
		LEFT          = new ImageIcon(path + "/arrow-left.png");
		RIGHT         = new ImageIcon(path + "/arrow-right.png");
		MERGE         = new ImageIcon(path + "/merge.png");
		PRINT         = new ImageIcon(path + "/fileprint.png");
		BLACK_WHITE   = new ImageIcon(path + "/black-white.png");

		DATABASE      = new ImageIcon(path + "/database.png");
		OFOLDER       = new ImageIcon(path + "/opened-folder.png");
		CFOLDER       = new ImageIcon(path + "/folder.png");
		TABLE         = new ImageIcon(path + "/table.png");
		GHOST_TABLE   = new ImageIcon(path + "/ghost-table.png");
		VIEW          = new ImageIcon(path + "/view.png");
		PROCEDURE     = new ImageIcon(path + "/procedure.png");
		FUNCTION      = new ImageIcon(path + "/function.gif");
		FIELD         = new ImageIcon(path + "/field.gif");
		SYNONYM       = new ImageIcon(path + "/synonym.png");
		SEQUENCE      = new ImageIcon(path + "/sequence.png");
		UDT           = new ImageIcon(path + "/udt.gif");
		OTHER         = new ImageIcon(path + "/other.png");
		ALIASDT       = new ImageIcon(path + "/aliasdt.gif");
		BASICDT       = new ImageIcon(path + "/basicdt.gif");

		DELETE        = new ImageIcon(path + "/delete.gif");
		REFRESH       = new ImageIcon(path + "/refresh.gif");
		UPDATE        = new ImageIcon(path + "/update.gif");
		COMMIT        = new ImageIcon(path + "/commit.png");
		ROLLBACK      = new ImageIcon(path + "/rollback.png");

		SET           = new ImageIcon(path + "/set.gif");
		REDSET        = new ImageIcon(path + "/red-set.gif");
		UNKNOWN       = new ImageIcon(path + "/unknown.gif");

		LENS          = new ImageIcon(path + "/lens.gif");
		BOMB          = new ImageIcon(path + "/bomb.gif");
		COLUMN        = new ImageIcon(path + "/column.gif");
		COL_FIT       = new ImageIcon(path + "/column-fit.gif");
		COL_AUTOSIZE  = new ImageIcon(path + "/column-autosize.gif");
		FLOPPY        = new ImageIcon(path + "/floppy.gif");
		EXPORT        = new ImageIcon(path + "/export.gif");
		IMPORT        = new ImageIcon(path + "/import.gif");
		WIZARD        = new ImageIcon(path + "/wizard.png");
		DROP_DOWN     = new ImageIcon(path + "/drop-down.png");
		USER          = new ImageIcon(path + "/user.png");

		TRIGGER       = new ImageIcon(path + "/trigger.gif");
		GEAR          = new ImageIcon(path + "/gear.gif");
		MODULE        = new ImageIcon(path + "/module.gif");
		POPUP         = new ImageIcon(path + "/popup.gif");
		RULE          = new ImageIcon(path + "/rule.gif");

		BOOK_OPEN       = new ImageIcon(path + "/book-open.gif");
		BOOK_CLOSED     = new ImageIcon(path + "/book-closed.gif");
		PAGE            = new ImageIcon(path + "/page.gif");

		BOX_NONE        = new ImageIcon(path + "/box-none.gif");
		BOX_NW          = new ImageIcon(path + "/box-nw.gif");
		BOX_NE          = new ImageIcon(path + "/box-ne.gif");
		BOX_SW          = new ImageIcon(path + "/box-sw.gif");
		BOX_SE          = new ImageIcon(path + "/box-se.gif");

		ER_VIEW         = new ImageIcon(path + "/er-view.gif");
		ER_ENTITY       = new ImageIcon(path + "/er-entity.gif");

		CHKBOX_SET      = new ImageIcon(path + "/checkbox-set.gif");
		CHKBOX_UNSET    = new ImageIcon(path + "/checkbox-unset.gif");
		CHKBOX_UNKNOWN  = new ImageIcon(path + "/checkbox-unknown.gif");

		FONT_FAMIL      = new ImageIcon(path + "/font-family.gif");
		FONT_MONOSPACED = new ImageIcon(path + "/font-monospaced.png");

		STYLE_PLAIN     = new ImageIcon(path + "/style-plain.gif");
		STYLE_BOLD      = new ImageIcon(path + "/style-bold.gif");
		STYLE_ITALIC    = new ImageIcon(path + "/style-italic.gif");
		STYLE_BOLDITAL  = new ImageIcon(path + "/style-bolditalic.gif");
		STYLE_UNDERLINE = new ImageIcon(path + "/style-underline.png");

		PARAGRAPH       = new ImageIcon(path + "/paragraph.png");
		PAR_LEFT        = new ImageIcon(path + "/par-left.png");
		PAR_CENTER      = new ImageIcon(path + "/par-center.png");
		PAR_RIGHT       = new ImageIcon(path + "/par-right.png");

		IMAGE           = new ImageIcon(path + "/image.png");
		STRUCTURE       = new ImageIcon(path + "/structure.png");

		LIST_ORDERED    = new ImageIcon(path + "/list-ordered.png");
		LIST_UNORDERED  = new ImageIcon(path + "/list-unordered.png");
		LIST_ITEM       = new ImageIcon(path + "/list-item.png");

		INFO            = new ImageIcon(path + "/info.png");
		ALERT           = new ImageIcon(path + "/alert.png");
		DANGER          = new ImageIcon(path + "/danger.png");
		INFO2           = new ImageIcon(path + "/info2.png");
		ALERT2          = new ImageIcon(path + "/alert2.png");
		DANGER2         = new ImageIcon(path + "/danger2.png");

		FIRST_AID       = new ImageIcon(path + "/first-aid.png");
		
		//--- cursors

		NODROP = loadCursor(path + "/no-drop.gif");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private static Cursor loadCursor(String name)
	{
		return loadCursor(name, new Point(0, 0));
	}

	//---------------------------------------------------------------------------

	private static Cursor loadCursor(String name, Point p)
	{
		ImageIcon i = new ImageIcon(name);
		return Toolkit.getDefaultToolkit().createCustomCursor(i.getImage(), p , "");
	}
}

//==============================================================================
