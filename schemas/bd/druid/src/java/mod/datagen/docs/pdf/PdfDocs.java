//==============================================================================
//===
//===   PdfDocs
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.datagen.docs.pdf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import mod.datagen.docs.pdf.panels.OptionPanel;

import org.dlib.gui.treeview.TreeViewNode;
import org.dlib.tools.TVector;
import org.dlib.tools.Util;

import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.codec.PngImage;

import druid.core.AttribListIterator;
import druid.core.AttribSet;
import druid.core.DataLib;
import druid.core.DataTypeLib;
import druid.core.DruidException;
import druid.core.config.Config;
import druid.core.er.ErScrEntity;
import druid.core.er.ErScrView;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.FieldAttribs;
import druid.data.FieldNode;
import druid.data.FolderNode;
import druid.data.FunctionNode;
import druid.data.NotesNode;
import druid.data.ProcedureNode;
import druid.data.Revisions;
import druid.data.SequenceNode;
import druid.data.TableNode;
import druid.data.TableRule;
import druid.data.Trigger;
import druid.data.ViewNode;
import druid.data.datatypes.ConstAlias;
import druid.data.datatypes.ConstDataType;
import druid.data.datatypes.VarAlias;
import druid.data.datatypes.VarDataType;
import druid.data.er.ErEntity;
import druid.data.er.ErView;
import druid.interfaces.DocsGenModule;
import druid.interfaces.Logger;
import druid.interfaces.ModuleOptions;
import druid.util.DruidUtil;
import druid.util.Language;
import druid.util.decoder.OnClauseDecoder;
import druid.util.decoder.TableVarTypeDecoder;
import druid.util.decoder.TriggerActivationDecoder;
import druid.util.decoder.TriggerForEachDecoder;

//==============================================================================

public class PdfDocs implements DocsGenModule, ModuleOptions
{
	private OptionPanel optPanel = new OptionPanel();

	private OnClauseDecoder          onClause    = new OnClauseDecoder();
	private TableVarTypeDecoder      tabVarType  = new TableVarTypeDecoder();
	private TriggerActivationDecoder trigActiv   = new TriggerActivationDecoder();
	private TriggerForEachDecoder    trigForEach = new TriggerForEachDecoder();

	private Hashtable htLangStrings;
	private static final String pdfExtension= ".pdf";

	//---------------------------------------------------------------------------
	//---
	//--- DocsGenModule interface
	//---
	//---------------------------------------------------------------------------

	public String getId()       { return "pdf"; }
	public String getVersion()  { return "1.0"; }
	public String getAuthor()   { return "Andrea Carboni"; }

	//---------------------------------------------------------------------------

	public String getDescription()
	{
		return	"Generates docs in PDF format using iText.";
	}

	//---------------------------------------------------------------------------

	public ModuleOptions getModuleOptions(int env)
	{
		if (env == DATABASE)
			return this;

		return null;
	}

	//---------------------------------------------------------------------------

	public JComponent getPanel() { return optPanel; }

	//---------------------------------------------------------------------------

	public void refresh(AbstractNode node)
	{
		optPanel.refresh(new Settings(node.modsConfig,this));
	}

	//---------------------------------------------------------------------------

	public String  getFormat()        { return "PDF Format"; }
	public boolean isDirectoryBased() { return false;        }
	public boolean hasLargePanel()    { return true;         }

	//---------------------------------------------------------------------------
	//---
	//--- Html Doc Generation
	//---
	//---------------------------------------------------------------------------

	public void generate(Logger l, DatabaseNode dbNode)
	{
		String outFile   = DruidUtil.toAbsolutePath(dbNode.modsConfig.getValue(this, "output"));

		if (!outFile.endsWith(pdfExtension))
			outFile += pdfExtension;

		l.logHeader("PDF Documentation");

		String dbName = dbNode.attrSet.getString("name");

		Settings settings = new Settings(dbNode.modsConfig, this);

		PdfParams params = new PdfParams(l, dbNode, settings);

		htLangStrings = Language.loadLanguage(null, settings.getLanguage());

		if (htLangStrings == null)
		{
			l.log(Logger.ALERT, "Cannot load language strings");
			return;
		}

		Document doc = params.createDocument();

		try
		{
			PdfWriter.getInstance(doc, new FileOutputStream(outFile));

			doc.addTitle(dbName);
			doc.addSubject(local("docsForDatabase") +" "+ dbName);
			doc.addCreator("Druid, the database manager");

			HeaderFooter header = new HeaderFooter(new Phrase(local("docsForDatabase") +" "+ dbName), false);
			doc.setHeader(header);

			HeaderFooter footer = new HeaderFooter(new Phrase(local("page") +" : "), true);
			footer.setAlignment(HeaderFooter.ALIGN_CENTER);
			doc.setFooter(footer);

			doc.open();

			buildDoc(params, doc);
			doc.close();

			l.log(Logger.INFO, "");
			l.log(Logger.INFO, "Done.");
		}
		catch(Exception e)
		{
			StringWriter s = new StringWriter();
			PrintWriter  p = new PrintWriter(s);

			e.printStackTrace(p);

			l.log(Logger.ALERT, "Raised exception --> " + e.getMessage());
			l.log(Logger.ALERT, "Type : " + s);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create entire document
	//---
	//---------------------------------------------------------------------------

	private void buildDoc(PdfParams params, Document doc) throws Exception
	{
		params.logger.log(Logger.INFO, "");
		params.logger.log(Logger.INFO, "Generating main stuff...");

		buildCover(params, doc);

		doc.add(buildGeneral(params));
		doc.add(buildDatatypes(params));

		params.logger.log(Logger.INFO, "Generating E/R views...");
		buildErViews(params, doc);

		params.logger.log(Logger.INFO, "Generating objects...");
		buildTree(params, doc, params.dbNode);
	}

	//---------------------------------------------------------------------------

	private void buildTree(PdfParams params, Document doc, AbstractNode node) throws Exception
	{
		doc.add(createFolder(params, node));

		for(int i=0; i<node.getChildCount(); i++)
		{
			AbstractNode child = (AbstractNode) node.getChild(i);

			if (child instanceof FolderNode)
				buildTree(params, doc, child);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Build document cover
	//---
	//---------------------------------------------------------------------------

	private void buildCover(PdfParams params, Document doc) throws Exception
	{
		String dbName   = params.dbNode.attrSet.getString("name");
		int    prjBuild = params.dbNode.getProjectNode().attrSet.getInt("build");

		Paragraph title = new Paragraph(dbName, params.fontCover);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		title.setLeading(150);
		doc.add(title);

		Paragraph build = new Paragraph(local("build") +" "+ prjBuild, params.fontDefault);
		build.setAlignment(Paragraph.ALIGN_CENTER);
		build.setLeading(50);
		doc.add(build);

		Paragraph date = new Paragraph(local("date") +" "+ Util.getCurrentDate(), params.fontDefault);
		date.setAlignment(Paragraph.ALIGN_CENTER);
		doc.add(date);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Build general page
	//---
	//---------------------------------------------------------------------------

	private Chapter buildGeneral(PdfParams params) throws Exception
	{
		Chapter chapter = PdfUtil.createChapter(params, local("overview"));

		addIfCase(chapter, createRevisions(params));
		addIfCase(chapter, createDbOther(params));

		return chapter;
	}

	//---------------------------------------------------------------------------

	private Table createRevisions(PdfParams params) throws Exception
	{
		String[] names  = { "version", "date",       "descr"};
		String[] labels = { "Version", "Date", "Description"};
		int[]    widths = {        15,     25,           60 };

		labels[0] = local("version");
		labels[1] = local("date");
		labels[2] = local("description");

		Revisions revs = params.dbNode.revisions;

		if (revs.size() == 0) return null;

		Iterator i = new AttribListIterator(revs);

		return PdfUtil.buildTable(params, local("revisions"), names, labels, widths, i);
	}

	//---------------------------------------------------------------------------

	private Table createDbOther(PdfParams params) throws Exception
	{
		AttribSet as = params.dbNode.attrSet;

		String  preSql  = as.getString("preSql").trim();
		String  postSql = as.getString("postSql").trim();

		int colWidths[] = { 30, 70 };

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle(params, local("other"), 2));

		if (!preSql.equals(""))
		{
			t.addCell(PdfUtil.createTableCell(params, local("preSql")));
			t.addCell(PdfUtil.createTableCellCode(params, preSql));
		}

		if (!postSql.equals(""))
		{
			t.addCell(PdfUtil.createTableCell(params, local("postSql")));
			t.addCell(PdfUtil.createTableCellCode(params, postSql));
		}

		return t;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Build datatypes
	//---
	//---------------------------------------------------------------------------

	private Chapter buildDatatypes(PdfParams params) throws Exception
	{
		String aliasSpace = "      ";

		Chapter chapter = PdfUtil.createChapter(params, local("datatypes"));

		//------------------------------------------------------------------------
		//--- constant size

		int constWidths[] = { 30, 50, 20};

		Table table = PdfUtil.createTable(params, constWidths);

		//--- setup header

		table.addCell(PdfUtil.createTableTitle (params, local("constantTypes"), constWidths.length));
		table.addCell(PdfUtil.createTableHeader(params, local("name")));
		table.addCell(PdfUtil.createTableHeader(params, local("domain")));
		table.addCell(PdfUtil.createTableHeader(params, local("dataDEquiv")));
		table.endHeaders();

		TreeViewNode cdt = params.dbNode.dataTypes.getChild(0);

		for(int i=0; i<cdt.getChildCount(); i++)
		{
			ConstDataType type = (ConstDataType) cdt.getChild(i);

			String name    = type.attrSet.getString("name");
			String domain  = DataTypeLib.getTypeCheckString(local("field"), type);
			String ddEquiv = type.attrSet.getString("ddEquiv");

			if (domain == null) domain = "";

			table.addCell(PdfUtil.createTableCell(params, name, true));
			table.addCell(PdfUtil.createTableCell(params, domain));
			table.addCell(PdfUtil.createTableCell(params, ddEquiv));

			for(int j=0; j<type.getChildCount(); j++)
			{
				ConstAlias alias = (ConstAlias) type.getChild(j);

				name    = alias.attrSet.getString("name");
				domain  = DataTypeLib.getTypeCheckString(local("field"), alias);
				ddEquiv = alias.attrSet.getString("ddEquiv");

				if (domain == null) domain = "";

				table.addCell(PdfUtil.createTableCell(params, aliasSpace+name));
				table.addCell(PdfUtil.createTableCell(params, domain));
				table.addCell(PdfUtil.createTableCell(params, ddEquiv));
			}
		}

		chapter.add(table);

		//------------------------------------------------------------------------
		//--- variable size

		int[] varWidths = { 25, 10, 50, 15 };

		table = PdfUtil.createTable(params, varWidths);

		//--- setup header

		table.addCell(PdfUtil.createTableTitle (params, local("variableTypes"), varWidths.length));
		table.addCell(PdfUtil.createTableHeader(params, local("name")));
		table.addCell(PdfUtil.createTableHeader(params, local("size")));
		table.addCell(PdfUtil.createTableHeader(params, local("domain")));
		table.addCell(PdfUtil.createTableHeader(params, local("dataDEquiv")));
		table.endHeaders();

		TreeViewNode vdt = params.dbNode.dataTypes.getChild(1);

		for(int i=0; i<vdt.getChildCount(); i++)
		{
			VarDataType type = (VarDataType) vdt.getChild(i);

			String name = type.attrSet.getString("name");

			table.addCell(PdfUtil.createTableCell(params, name, true));
			table.addCell(PdfUtil.createTableCell(params, local("notAvailable")));
			table.addCell(PdfUtil.createTableCell(params, local("notAvailable")));
			table.addCell(PdfUtil.createTableCell(params, local("notAvailable")));

			for(int j=0; j<type.getChildCount(); j++)
			{
				VarAlias alias = (VarAlias) type.getChild(j);

				       name    = alias.attrSet.getString("name");
				String size    = alias.attrSet.getString("size");
				String domain  = DataTypeLib.getTypeCheckString(local("field"), alias);
				String ddEquiv = alias.attrSet.getString("ddEquiv");

				if (domain == null) domain = "";

				table.addCell(PdfUtil.createTableCell(params, aliasSpace+name));
				table.addCell(PdfUtil.createTableCell(params, size));
				table.addCell(PdfUtil.createTableCell(params, domain));
				table.addCell(PdfUtil.createTableCell(params, ddEquiv));
			}
		}

		chapter.add(table);

		return chapter;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create ER/Views
	//---
	//---------------------------------------------------------------------------

	private void buildErViews(PdfParams params, Document doc) throws Exception
	{
		TreeViewNode erv = params.dbNode.erViews;

		if (erv.getChildCount() == 0) return;

		Chapter chapter = PdfUtil.createChapter(params, local("erViews"));

		int percWidth = (int) (params.thumbSize * 100 / params.getSpaceWidth()) +5;

		int constWidths[] = { percWidth, 100 -percWidth };

		Table t = PdfUtil.createTable(params, constWidths);

		t.addCell(PdfUtil.createTableHeader(params, local("erView")));
		t.addCell(PdfUtil.createTableHeader(params, local("description")));

		Graphics aux = new BufferedImage(10,10, BufferedImage.TYPE_3BYTE_BGR).createGraphics();

		Vector vChapters = new Vector();

		for(int i=0; i<erv.getChildCount(); i++)
		{
			ErView erView = (ErView) erv.getChild(i);

			ErScrView erScrView = new ErScrView(erView, aux);

			BufferedImage erImage = null;

			if (erView.getChildCount() > 0)
			{
				erImage = erScrView.createErImage();
				java.awt.Image img = erImage.getScaledInstance(params.thumbSize, -1, BufferedImage.SCALE_SMOOTH);
				t.addCell(PdfUtil.createTableCell(convertImage(img)));
			}
			else
			{
				t.addCell(PdfUtil.createTableCell(params, ""));
			}

			t.addCell(PdfUtil.createTableCell(params, erView.xmlDoc));

			Chapter c = PdfUtil.createChapter(params, erView.attrSet.getString("name"));

			vChapters.add(c);

			if (erImage != null)
			{
				Image ii = convertImage(erImage);
				ii.setInterpolation(true);

				//--- vertical scaling is less pixels than actual height to let the chapter
				//--- name and image fit the page

				ii.scaleToFit(params.getSpaceWidth(), params.getSpaceHeight() - params.getSpaceTop());
				c.add(ii);
			}

			for(int j=0; j<erView.getChildCount(); j++)
			{
				ErEntity    erEnt    = (ErEntity) erView.getChild(j);
				ErScrEntity erScrEnt = erScrView.getObjectFromEntity(erEnt);

				Image entImage = convertImage(erScrEnt.getImage());

				entImage.setInterpolation(true);
				entImage.scalePercent(params.entScalingPerc);

				Section section = PdfUtil.createSection(params, erEnt.attrSet.getString("name"), j+1, c);

				PdfUtil.add(params, section, erEnt.xmlDoc);
				section.add(entImage);

				//--- "used tables" paragraph

				Paragraph parag = new Paragraph(local("usedTables"), params.fontTableCellBold);
				parag.setLeading(30);

				section.add(parag);

				for(int k=0; k<erEnt.getTableNum(); k++)
				{
					AttribSet as = erEnt.getTableNodeAt(k).attrSet;

					Paragraph paragr = new Paragraph(as.getString("name"), params.fontDefault);

					paragr.setIndentationLeft(20);

					section.add(paragr);
				}
			}
		}

		chapter.add(t);

		doc.add(chapter);

		for(Iterator i=vChapters.iterator(); i.hasNext();)
			doc.add((Chapter) i.next());
	}

	//---------------------------------------------------------------------------

	private Image convertImage(java.awt.Image img) throws Exception
	{
		int width  = img.getWidth(null);
		int height = img.getHeight(null);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g = bimg.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.drawImage(img, 0, 0, null);
		g.dispose();

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		boolean result = ImageIO.write(bimg, "png", os);

		if (!result)
			throw new DruidException(DruidException.OPE_ABO, "Error saving image to buffer");

		return PngImage.getImage(os.toByteArray());
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create one chapter for each folder
	//---
	//---------------------------------------------------------------------------

	private Chapter createFolder(PdfParams params, AbstractNode node) throws Exception
	{
		Chapter chapter = PdfUtil.createChapter(params, node.attrSet.getString("name"));

		PdfUtil.add(params, chapter, node.xmlDoc);

		//------------------------------------------------------------------------
		//--- create subfolder table

		boolean existSubFolders = false;

		for(int i=0; i<node.getChildCount(); i++)
		{
			AbstractNode child = (AbstractNode) node.getChild(i);

			if (child instanceof FolderNode)
				existSubFolders = true;
		}

		if (existSubFolders)
		{
			int colWidths[] = { 30, 70 };

			Table t = PdfUtil.createTable(params, colWidths);

			t.addCell(PdfUtil.createTableTitle (params, local("subFolders"), colWidths.length));
			t.addCell(PdfUtil.createTableHeader(params, local("name")));
			t.addCell(PdfUtil.createTableHeader(params, local("description")));

			for(int i=0; i<node.getChildCount(); i++)
			{
				AbstractNode child = (AbstractNode) node.getChild(i);

				if (child instanceof FolderNode)
				{
					t.addCell(PdfUtil.createTableCell(params, child.attrSet.getString("name")));
					t.addCell(PdfUtil.createTableCell(params, child.xmlDoc));
				}
			}

			chapter.add(t);
		}

		//------------------------------------------------------------------------
		//--- create entries for objects

		int section = 1;

		for(int i=0; i<node.getChildCount(); i++)
		{
			AbstractNode child = (AbstractNode) node.getChild(i);

			if (child instanceof TableNode)
				createTable(params, child, chapter, section++);

			else if (child instanceof ViewNode)
				createView(params, child, chapter, section++);

			else if (child instanceof ProcedureNode)
				createProcedure(params, child, chapter, section++);

			else if (child instanceof FunctionNode)
				createFunction(params, child, chapter, section++);

			else if (child instanceof SequenceNode)
				createSequence(params, child, chapter, section++);

			else if (child instanceof NotesNode)
				createNote(params, child, chapter, section++);
		}

		return chapter;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create tables
	//---
	//---------------------------------------------------------------------------

	private void createTable(PdfParams params, AbstractNode node, Chapter chapter,
									 int sectNum) throws Exception
	{
		TableNode table = (TableNode) node;

		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		PdfUtil.add(params, section, node.xmlDoc);

		createSeeAlso(params, table, section);

		addIfCase(section, createFieldSummary(params, table));
		addIfCase(section, createSqlSummary  (params, table));
		addIfCase(section, createForeignKeys (params, table));
		addIfCase(section, createTableVars   (params, table));
		addIfCase(section, createSqlCommands (params, table));

		createTriggers  (params, table, section, 5);
		createTableRules(params, table, section, 6);
	}

	//---------------------------------------------------------------------------

	private void addIfCase(Section section, Table table)
	{
		if (table != null)
			section.add(table);
	}

	//---------------------------------------------------------------------------

	private void createSeeAlso(PdfParams params, TableNode table, Section section) throws Exception
	{
		Vector vRefs = DataLib.getReferences(table, true);

		if (vRefs.size() != 0)
		{
			Paragraph parag = new Paragraph(local("seeAlso"), params.fontTableCellBold);
			parag.setLeading(30);

			section.add(parag);

			for(int i=0; i<vRefs.size(); i++)
			{
				TableNode t = (TableNode) vRefs.get(i);

				Paragraph paragr = new Paragraph(t.attrSet.getString("name"), params.fontDefault);

				paragr.setIndentationLeft(20);

				section.add(paragr);
			}
		}
	}

	//---------------------------------------------------------------------------

	private Table createFieldSummary(PdfParams params, TableNode table) throws Exception
	{
		int colWidths[] = { 25, 25, 50};

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle (params, local("fieldSummary"), 3));
		t.addCell(PdfUtil.createTableHeader(params, local("name")));
		t.addCell(PdfUtil.createTableHeader(params, local("type")));
		t.addCell(PdfUtil.createTableHeader(params, local("description")));

		for(int i=0; i<table.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) table.getChild(i);

			int style = Font.NORMAL;

			if (DataLib.isPrimaryKey(field)) style |= Font.BOLD;
			if (field.isFkey()) 					style |= Font.ITALIC;

			Font font = params.createDefaultFont(style);

			t.addCell(PdfUtil.createTableCell(field.attrSet.getString("name"), font));
			t.addCell(PdfUtil.createTableCell(params, DataTypeLib.getTypeDef(field)));
			t.addCell(PdfUtil.createTableCell(params, field.xmlDoc));
		}

		return t;
	}

	//---------------------------------------------------------------------------

	private Table createSqlSummary(PdfParams params, TableNode table) throws Exception
	{
		FieldAttribs fattr = table.getDatabase().fieldAttribs;

		int colWidths[] = new int[fattr.size() +2];

		colWidths[0] = 30;
		colWidths[1] = 25;

		for(int i=2; i<colWidths.length; i++)
			colWidths[i] = 9;

		//--- expand columns if attribs are of string type

		for(int i=0; i<fattr.size(); i++)
		{
			AttribSet as = fattr.get(i);

			if (as.getString("type").equals(FieldAttribs.TYPE_STRING))
				colWidths[2+i] = 20;
		}

		//--- table construction

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle (params, local("sqlSummary"), colWidths.length));
		t.addCell(PdfUtil.createTableHeader(params, local("name")));
		t.addCell(PdfUtil.createTableHeader(params, local("type")));

		for(int i=0; i<fattr.size(); i++)
			t.addCell(PdfUtil.createTableHeader(params, fattr.get(i).getString("name")));

		t.endHeaders();

		for(int i=0; i<table.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) table.getChild(i);

			int style = Font.NORMAL;

			if (DataLib.isPrimaryKey(field)) style |= Font.BOLD;
			if (field.isFkey()) 					style |= Font.ITALIC;

			Font font = params.createDefaultFont(style);

			t.addCell(PdfUtil.createTableCell(field.attrSet.getString("name"), font));
			t.addCell(PdfUtil.createTableCell(params, DataTypeLib.getSqlType(field)));

			for(int j=0; j<fattr.size(); j++)
			{
				int id = fattr.get(j).getInt("id");

				Object obj   = field.fieldAttribs.getData(id +"");
				String value = obj.toString();

				if (obj instanceof Boolean)
					value = ((Boolean) obj).booleanValue() ? "x" : "-";

				Cell cell = PdfUtil.createTableCell(params, value);

				cell.setHorizontalAlignment(Cell.ALIGN_CENTER);

				t.addCell(cell);
			}
		}
		return t;
	}

	//---------------------------------------------------------------------------

	private Table createForeignKeys(PdfParams params, TableNode table) throws Exception
	{
		int colWidths[] = { 25, 25, 25, 25};

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle (params, local("foreignKeys"), 4));
		t.addCell(PdfUtil.createTableHeader(params, local("name")));
		t.addCell(PdfUtil.createTableHeader(params, local("import")));
		t.addCell(PdfUtil.createTableHeader(params, local("onUpdate")));
		t.addCell(PdfUtil.createTableHeader(params, local("onDelete")));

		boolean existData = false;

		for(int i=0; i<table.getChildCount(); i++)
		{
			FieldNode field = (FieldNode) table.getChild(i);
			AttribSet as    = field.attrSet;

			if (field.isFkey())
			{
				t.addCell(PdfUtil.createTableCell(params, as.getString("name")));
				t.addCell(PdfUtil.createTableCell(params, DataTypeLib.getTypeDef(field)));
				t.addCell(PdfUtil.createTableCell(params, onClause.decode(as.getString("onUpdate"))));
				t.addCell(PdfUtil.createTableCell(params, onClause.decode(as.getString("onDelete"))));
				existData = true;
			}
		}

		return (existData ? t : null);
	}

	//---------------------------------------------------------------------------

	private Table createTableVars(PdfParams params, TableNode table) throws Exception
	{
		int colWidths[] = { 20, 20, 20, 40};

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle (params, local("variables"), 4));
		t.addCell(PdfUtil.createTableHeader(params, local("name")));
		t.addCell(PdfUtil.createTableHeader(params, local("type")));
		t.addCell(PdfUtil.createTableHeader(params, local("value")));
		t.addCell(PdfUtil.createTableHeader(params, local("description")));

		boolean existData = false;

		for(int i=0; i<table.tableVars.size(); i++)
		{
			AttribSet as = table.tableVars.get(i);

			t.addCell(PdfUtil.createTableCell(params, as.getString("name")));
			t.addCell(PdfUtil.createTableCell(params, tabVarType.decode(as.getString("type"))));
			t.addCell(PdfUtil.createTableCell(params, as.getString("value")));
			t.addCell(PdfUtil.createTableCell(params, as.getString("descr")));
			existData = true;
		}

		return (existData ? t : null);
	}

	//---------------------------------------------------------------------------

	private Table createSqlCommands(PdfParams params, TableNode table) throws Exception
	{
		String sqlCmd = table.attrSet.getString("sqlCommands").trim();

		if (sqlCmd.equals(""))
			return null;

		Table t = PdfUtil.createTable(params, new int[] { 100 });

		t.addCell(PdfUtil.createTableTitle(params, local("sqlCommands"), 1));
		t.addCell(PdfUtil.createTableCellCode(params, sqlCmd));

		return t;
	}

	//---------------------------------------------------------------------------

	private void createTriggers(PdfParams params, TableNode table, Section section, int sectNum) throws Exception
	{
		if (table.triggers.getChildCount() == 0) return;

		Section subSect = PdfUtil.createSubSection(params, local("triggers"), sectNum, section);

		for(int i=0; i<table.triggers.getChildCount(); i++)
		{
			Trigger   tr = (Trigger) table.triggers.getChild(i);
			AttribSet as = tr.attrSet;

			int colWidths[] = { 100 };

			Table t = PdfUtil.createTable(params, colWidths);

			t.addCell(PdfUtil.createTableTitle(params, as.getString("name"), 1));
			t.addCell(PdfUtil.createTableCell(params, tr.xmlDoc));

			//--- write trigger code

			TVector v = new TVector();

			v.setSeparator(" OR ");

			if (as.getBool("onInsert")) v.add("INSERT");
			if (as.getBool("onUpdate")) v.add("UPDATE");
			if (as.getBool("onDelete")) v.add("DELETE");

			String code = trigActiv.decode(as.getString("activation")) +" "+ v +"\n";

			code += "FOR EACH " + trigForEach.decode(as.getString("forEach")) +"\n";

			if (!as.getString("when").equals(""))
				code += "WHEN("+as.getString("when")+")\n";

			code += "\n" + as.getString("code");

			t.addCell(PdfUtil.createTableCellCode(params, code));

			subSect.add(t);
		}
	}

	//---------------------------------------------------------------------------

	private void createTableRules(PdfParams params, TableNode table, Section section, int sectNum) throws Exception
	{
		if (table.rules.getChildCount() == 0) return;

		Section subSect = PdfUtil.createSubSection(params, local("rules"), sectNum, section);

		for(int i=0; i<table.rules.getChildCount(); i++)
		{
			TableRule tr = (TableRule) table.rules.getChild(i);
			AttribSet as = tr.attrSet;

			int colWidths[] = { 100 };

			Table t = PdfUtil.createTable(params, colWidths);

			t.addCell(PdfUtil.createTableTitle   (params, as.getString("name"), 1));
			t.addCell(PdfUtil.createTableCell    (params, tr.xmlDoc));
			t.addCell(PdfUtil.createTableCellCode(params, as.getString("rule")));

			subSect.add(t);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create other objects : views, procedures, functions, sequences and notes
	//---
	//---------------------------------------------------------------------------

	private void createView(PdfParams params, AbstractNode node, Chapter chapter,
									int sectNum) throws Exception
	{
		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		PdfUtil.add(params, section, node.xmlDoc);
		section.add(createSqlTable(params, node.attrSet.getString("sqlCode")));
	}

	//---------------------------------------------------------------------------

	private void createProcedure(PdfParams params, AbstractNode node, Chapter chapter,
										  int sectNum) throws Exception
	{
		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		PdfUtil.add(params, section, node.xmlDoc);
		section.add(createSqlTable(params, node.attrSet.getString("sqlCode")));
	}

	//---------------------------------------------------------------------------

	private void createFunction(PdfParams params, AbstractNode node, Chapter chapter,
										 int sectNum) throws Exception
	{
		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		PdfUtil.add(params, section, node.xmlDoc);
		section.add(createSqlTable(params, node.attrSet.getString("sqlCode")));
	}

	//---------------------------------------------------------------------------

	private void createSequence(PdfParams params, AbstractNode node, Chapter chapter,
										 int sectNum) throws Exception
	{
		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		PdfUtil.add(params, section, node.xmlDoc);

		AttribSet as = node.attrSet;

		int colWidths[] = {50,50};

		Table t = PdfUtil.createTable(params, colWidths);

		t.addCell(PdfUtil.createTableTitle(params, local("attribs"), 2));

		t.addCell(PdfUtil.createTableCell (params, local("increment")));
		t.addCell(PdfUtil.createTableCell (params, as.getString("increment")));
		t.addCell(PdfUtil.createTableCell (params, local("minValue")));
		t.addCell(PdfUtil.createTableCell (params, as.getString("minValue")));
		t.addCell(PdfUtil.createTableCell (params, local("maxValue")));
		t.addCell(PdfUtil.createTableCell (params, as.getString("maxValue")));
		t.addCell(PdfUtil.createTableCell (params, local("start")));
		t.addCell(PdfUtil.createTableCell (params, as.getString("start")));
		t.addCell(PdfUtil.createTableCell (params, local("cache")));
		t.addCell(PdfUtil.createTableCell (params, as.getString("cache")));
		t.addCell(PdfUtil.createTableCell (params, local("cycle")));
		t.addCell(PdfUtil.createTableCell (params, as.getBool("cycle") ? "x" : "-"));
		t.addCell(PdfUtil.createTableCell (params, local("order")));
		t.addCell(PdfUtil.createTableCell (params, as.getBool("order") ? "x" : "-"));

		section.add(t);
	}

	//---------------------------------------------------------------------------

	private void createNote(PdfParams params, AbstractNode node, Chapter chapter,
									int sectNum) throws Exception
	{
		String type = node.attrSet.getString("type");
		String file = "??";

		if (type.equals(NotesNode.INFO))
			file = "info.png";

		else if (type.equals(NotesNode.ALERT))
			file = "alert.png";

		else if (type.equals(NotesNode.DANGER))
			file = "danger.png";

		Image image = Image.getInstance(Config.dir.images + "/large/"+file );

		//--- create section

		Section section = PdfUtil.createSection(params, node.attrSet.getString("name"), sectNum, chapter);

		int colWidths[] = { 15, 85 };

		Table t = PdfUtil.createTable(params, colWidths);

		t.setBorderWidth(0);
		t.setBorder(Table.NO_BORDER);
		t.addCell(PdfUtil.createTableCell(image));
		t.addCell(PdfUtil.createTableCell(params, node.xmlDoc));

		section.add(t);
	}

	//---------------------------------------------------------------------------

	private Table createSqlTable(PdfParams params, String sqlCode) throws Exception
	{
		Table t = PdfUtil.createTable(params, new int[] { 100 });

		t.addCell(PdfUtil.createTableTitle(params, local("sqlCode"), 1));
		t.addCell(PdfUtil.createTableCellCode(params, sqlCode));

		return t;
	}

	//---------------------------------------------------------------------------
	//---
	//--- General private methods
	//---
	//---------------------------------------------------------------------------

	private String local(String text)
	{
		String s = (String) htLangStrings.get(text);

		if (s == null)
			throw new IllegalArgumentException("Message to localize not found --> " + text);

		return s;
	}
}

//==============================================================================
