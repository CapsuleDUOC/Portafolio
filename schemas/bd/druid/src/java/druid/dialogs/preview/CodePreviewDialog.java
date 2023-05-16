//==============================================================================
//===
//===   CodePreviewDialog
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Enumeration;

import javax.swing.JComponent;

import org.dlib.gui.CustomLook;
import org.dlib.gui.TDialog;
import org.dlib.gui.TTabbedPane;
import org.dlib.gui.TTextArea;
import org.jedit.JavaTokenMarker;

import druid.core.modules.ModuleManager;
import druid.data.AbstractNode;
import druid.data.DatabaseNode;
import druid.data.TableNode;
import druid.interfaces.CodeGenModule;
import druid.interfaces.Logger;
import druid.util.gui.BasicTextArea;

//==============================================================================

public class CodePreviewDialog extends TDialog implements Logger
{
	private StringBuffer sbLog;
	private TTabbedPane  jtPane = new TTabbedPane();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public CodePreviewDialog(Frame frame)
	{
		super(frame, "Code Preview", true);

		jtPane.setPreferredSize(new Dimension(750,550));

		getContentPane().add(jtPane, BorderLayout.CENTER);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void run(AbstractNode node)
	{
		buildCode(node);
		showDialog();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Logger interface
	//---
	//---------------------------------------------------------------------------

	public void log(int type, String msg)
	{
		sbLog.append(msg + "\n");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void buildCode(AbstractNode node)
	{
		jtPane.removeAll();

		DatabaseNode dbNode = node.getDatabase();

		Enumeration e = ModuleManager.getModules(CodeGenModule.class);

		for(; e.hasMoreElements();)
		{
			CodeGenModule codeMod = (CodeGenModule) e.nextElement();

			if (dbNode.modsUsage.contains(codeMod))
			{
				sbLog = new StringBuffer();

				JComponent c = buildComponent(codeMod.getClassCode(this, (TableNode) node));

				if (sbLog.length() != 0)
					c = buildErrorComponent(sbLog.toString());

				jtPane.addTab(codeMod.getFormat(), c);
			}
		}
	}

	//---------------------------------------------------------------------------

	private JComponent buildComponent(String text)
	{
		BasicTextArea txaCode = new BasicTextArea();

		txaCode.setTokenMarker(new JavaTokenMarker());
		txaCode.setEditable(false);
		txaCode.setText(text);
		txaCode.scrollTo(0,0);

		return txaCode;
	}

	//---------------------------------------------------------------------------

	private JComponent buildErrorComponent(String text)
	{
		TTextArea txaNone = new TTextArea();

		txaNone.setEditable(false);
		txaNone.setText(text);
		txaNone.setFont(CustomLook.monospacedFont);

		return txaNone;
	}
	
    /* (non-Javadoc)
     * @see druid.interfaces.Logger#logHeader(java.lang.String)
     */
    public void logHeader(String message) {
        // TODO Auto-generated method stub
    }
}

//==============================================================================
