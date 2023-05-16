//==============================================================================
//===
//===   PdfUtil
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf;

import java.awt.Color;
import java.net.URL;
import java.util.Iterator;

import org.dlib.xml.XmlElement;

import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.Table;

import druid.core.AttribSet;
import druid.core.DocManager;
import druid.core.DruidException;

//==============================================================================

public class PdfUtil
{
	//---------------------------------------------------------------------------
	//---
	//--- Chapters / sections methods
	//---
	//---------------------------------------------------------------------------

	public static Chapter createChapter(PdfParams params, String name)
	{
		Paragraph title = new Paragraph(name, params.fontChapters);

		return new Chapter(title, params.currChapter++);
	}

	//---------------------------------------------------------------------------

	public static Section createSection(PdfParams params, String name, int index, Chapter chapter)
	{
		Paragraph sTitle = new Paragraph(name, params.fontSections);

		return chapter.addSection(sTitle);
	}

	//---------------------------------------------------------------------------

	public static Section createSubSection(PdfParams params, String name, int index, Section section)
	{
		Paragraph sTitle = new Paragraph(name, params.fontSubSects);

		return section.addSection(sTitle);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table methods
	//---
	//---------------------------------------------------------------------------

	public static Table createTable(PdfParams params, int[] colWidths) throws Exception
	{
		Table table = new Table(colWidths.length);

		table.setPadding(params.tableCellPadding);
		table.setWidth(100);
		table.setOffset(20);
		table.setWidths(colWidths);

		return table;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableTitle(PdfParams params, String text, int colSpan) throws Exception
	{
		Cell cell = new Cell(new Phrase(text, params.fontTableTitle));

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(params.colTableTitle);
		cell.setColspan(colSpan);

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableHeader(PdfParams params, String text) throws Exception
	{
		Cell cell = new Cell(new Phrase(text, params.fontTableHeader));

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(params.colTableHeader);
		cell.setHeader(true);

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCell(PdfParams params, String text) throws Exception
	{
		return createTableCell(params, text, false);
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCell(PdfParams params, String text, boolean isBold) throws Exception
	{
		Font font = isBold ? params.fontTableCellBold : params.fontTableCell;

		Cell cell = new Cell(new Phrase(text, font));

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCell(String text, Font font) throws Exception
	{
		Cell cell = new Cell(new Phrase(text, font));

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCell(PdfParams params, XmlElement docs) throws Exception
	{
		Cell cell = new Cell();

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		//--- handle children

		java.util.List list = docs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.PARAG))
				cell.add(toPdfParagraph(params, child));

			if (child.getName().equals(DocManager.OLIST))
				cell.add(toPdfList(params, child, true));

			if (child.getName().equals(DocManager.ULIST))
				cell.add(toPdfList(params, child, false));
		}

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCell(Image image) throws Exception
	{
		Phrase phrase = new Phrase(new Chunk(image, 0, 0, true));

		phrase.setLeading(image.plainHeight() * 1.05f);

		Cell cell = new Cell(phrase);

		cell.setVerticalAlignment(Element.ALIGN_TOP);

		return cell;
	}

	//---------------------------------------------------------------------------

	public static Cell createTableCellCode(PdfParams params, String text) throws Exception
	{
		Cell cell = new Cell(new Phrase(text, params.fontCode));

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(params.colTableCellCode);

		return cell;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Table building methods
	//---
	//---------------------------------------------------------------------------

	public static Table buildTable(PdfParams params, String title, String[] names,
									String[] labels, int[] colWidths, Iterator iter) throws Exception
	{
		Table table = createTable(params, colWidths);

		//--- setup header

		table.addCell(createTableTitle (params, title, names.length));

		for(int i=0; i<labels.length; i++)
			table.addCell(createTableHeader(params, labels[i]));

		for(; iter.hasNext() ;)
		{
			AttribSet as = (AttribSet) iter.next();

			for(int i=0; i<names.length; i++)
				table.addCell(createTableCell(params, as.getString(names[i]), false));
		}

		return table;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Conversion : DOC -> PDF (iText)
	//---
	//---------------------------------------------------------------------------

	public static void add(PdfParams params, Section section, XmlElement docs) throws Exception
	{
		//--- handle children

		java.util.List list = docs.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.PARAG))
				section.add(toPdfParagraph(params, child));

			if (child.getName().equals(DocManager.OLIST))
				section.add(toPdfList(params, child, true));

			if (child.getName().equals(DocManager.ULIST))
				section.add(toPdfList(params, child, false));
		}
	}

	//---------------------------------------------------------------------------

	private static List toPdfList(PdfParams params, XmlElement e, boolean type) throws Exception
	{
		List list = new List(type, 20);

		//--- handle children

		java.util.List children = e.getChildren();

		for(int i=0; i<children.size(); i++)
		{
			XmlElement child = (XmlElement) children.get(i);

			if (child.getName().equals(DocManager.ITEM))
				list.add(toPdfListItem(params, child));
		}

		return list;
	}

	//---------------------------------------------------------------------------

	private static ListItem toPdfListItem(PdfParams params, XmlElement e) throws Exception
	{
		ListItem item = new ListItem();

		//--- handle children

		java.util.List children = e.getChildren();

		for(int i=0; i<children.size(); i++)
		{
			XmlElement child = (XmlElement) children.get(i);

			if (child.getName().equals(DocManager.PARAG))
				item.add(toPdfParagraph(params, child));

			else
				throw new DruidException(DruidException.INC_STR, "Unknown child type", child);
		}

		return item;
	}

	//---------------------------------------------------------------------------

	private static Paragraph toPdfParagraph(PdfParams params, XmlElement e) throws Exception
	{
		Paragraph parag  = new Paragraph();

		//--- handle align attrib

		String align = e.getAttributeValue(DocManager.ATTR_ALIGN);

		if (align == null)
			parag.setAlignment(Paragraph.ALIGN_JUSTIFIED);

		else if (align.equals(DocManager.ATTR_ALIGN_RIGHT))
			parag.setAlignment(Paragraph.ALIGN_RIGHT);

		else if (align.equals(DocManager.ATTR_ALIGN_CENTER))
			parag.setAlignment(Paragraph.ALIGN_CENTER);

		else
			parag.setAlignment(Paragraph.ALIGN_JUSTIFIED);

		//--- handle children (if any)

		java.util.List list = e.getChildren();

		for(int i=0; i<list.size(); i++)
		{
			XmlElement child = (XmlElement) list.get(i);

			if (child.getName().equals(DocManager.TEXT))
				parag.add(toPdfText(params, child));

			if (child.getName().equals(DocManager.IMAGE))
				parag.add(toPdfImage(child));
		}

		//--- scan all added chunks to fix the leading

		java.util.Iterator i = parag.getChunks().iterator();

		float maxLeading = 0;

		for(; i.hasNext() ;)
		{
			Chunk chk = (Chunk) i.next();
			Image img = chk.getImage();

			if (img != null)
				maxLeading = Math.max(maxLeading, img.plainHeight() * 1.05f);
			else
				maxLeading = Math.max(maxLeading, chk.font().leading(1.5f));
		}

		parag.setLeading(maxLeading);

		return parag;
	}

	//---------------------------------------------------------------------------

	private static Chunk toPdfText(PdfParams params, XmlElement e) throws NumberFormatException
	{
		String bold  = e.getAttributeValue(DocManager.ATTR_BOLD);
		String ital  = e.getAttributeValue(DocManager.ATTR_ITALIC);
		String under = e.getAttributeValue(DocManager.ATTR_UNDERLINE);
		String col   = e.getAttributeValue(DocManager.ATTR_COLOR);
		String font  = e.getAttributeValue(DocManager.ATTR_FONTNAME);
		String size  = e.getAttributeValue(DocManager.ATTR_FONTSIZE);

		String fontName  = params.defFontName;
		int    fontStyle = params.defFontStyle;
		float  fontSize  = params.defFontSize;
		Color  fontColor = Color.BLACK;

		if (font != null)  fontName   = FontFactory.COURIER;
		if (bold != null)  fontStyle |= Font.BOLD;
		if (ital != null)  fontStyle |= Font.ITALIC;
		if (under != null) fontStyle |= Font.UNDERLINE;

		if (col != null)
		{
			int red   = Integer.parseInt(col.substring(0, 2), 16);
			int green = Integer.parseInt(col.substring(2, 4), 16);
			int blue  = Integer.parseInt(col.substring(4),    16);

			fontColor = new Color(red, green, blue);
		}

		if (size != null)
			fontSize = params.sizeMapping[Integer.parseInt(size) -1];

		return new Chunk(e.getValue(), FontFactory.getFont(fontName, fontSize, fontStyle, fontColor));
	}

	//---------------------------------------------------------------------------

	private static Chunk toPdfImage(XmlElement e) throws Exception
	{
		String src = e.getAttributeValue(DocManager.ATTR_IMAGE_SRC);

		return new Chunk(Image.getInstance(new URL(src)), 0, 0, true);
	}
}

//==============================================================================
