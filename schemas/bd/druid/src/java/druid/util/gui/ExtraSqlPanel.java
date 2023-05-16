//==============================================================================
//===
//===   ExtraSqlPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.util.gui;

import javax.swing.JPanel;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;

import druid.core.AttribSet;

//==============================================================================

public class ExtraSqlPanel extends JPanel
{
	private boolean bUsePost;
	private String  sAttrib;

	private SqlTextArea txaPre  = new SqlTextArea();
	private SqlTextArea txaPost = new SqlTextArea();

	//---------------------------------------------------------------------------

	public ExtraSqlPanel(boolean usePost)
	{
		this("preSql", "Pre Sql", usePost);
	}

	//---------------------------------------------------------------------------

	public ExtraSqlPanel(String attrib, String label, boolean usePost)
	{
		bUsePost = usePost;
		sAttrib  = attrib;

		FlexLayout flexL = new FlexLayout(2, (usePost ? 2:1), 4, 4);
		setLayout(flexL);

		flexL.setColProp(1, FlexLayout.EXPAND);
		flexL.setRowProp(0, FlexLayout.EXPAND);

		if (usePost)
			flexL.setRowProp(1, FlexLayout.EXPAND);

		add("0,0,l,t", new TLabel(label));
		add("1,0,x,x", txaPre);

		if (usePost)
		{
			add("0,1,l,t", new TLabel("Post Sql"));
			add("1,1,x,x", txaPost);
		}

		//--- setup sentinel

		ChangeSentinel sent = ChangeSentinel.getInstance();

		txaPre.getDocument().addDocumentListener(sent);
		txaPost.getDocument().addDocumentListener(sent);
	}

	//---------------------------------------------------------------------------

	public void refresh(AttribSet as)
	{
		txaPre.setText(as.getString(sAttrib));

		if (bUsePost)
			txaPost.setText(as.getString("postSql"));
	}

	//---------------------------------------------------------------------------

	public void saveDataToNode(AttribSet as)
	{
		as.setString(sAttrib, txaPre.getText());

		if (bUsePost)
			as.setString("postSql", txaPost.getText());
	}
}

//==============================================================================
