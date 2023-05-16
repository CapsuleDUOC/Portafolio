//==============================================================================
//===
//===   ControlsPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.dialogs.accountman.urlcreator;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;
import org.dlib.gui.TTextField;
import org.dlib.xml.XmlElement;

//==============================================================================

class ControlsPanel extends TPanel
{
	private static final int SIZE = 5;

	private TLabel     lblData[] = new TLabel    [SIZE];
	private TTextField txtData[] = new TTextField[SIZE];
	private TLabel     lblReq [] = new TLabel    [SIZE];

	private XmlElement elCurrent;
	private List       urlTokens;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public ControlsPanel()
	{
		super("Parameters");

		FlexLayout flexL = new FlexLayout(3,SIZE);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		for(int i=0; i<SIZE; i++)
		{
			lblData[i] = new TLabel();
			txtData[i] = new TTextField();
			lblReq [i] = new TLabel();

			lblReq[i].setForeground(Color.RED);
			lblReq[i].setText("(REQ)");

			add("0,"+i,      lblData[i]);
			add("1,"+i+",x", txtData[i]);
			add("2,"+i,      lblReq[i]);
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setUrl(UrlInfo ui)
	{
		setVisible(ui != null);

		if (ui == null) return;

		elCurrent = ui.elUrl;

		//--- hide all controls

		for(int i=0; i<SIZE; i++)
		{
			lblData[i].setVisible(false);
			txtData[i].setVisible(false);
			lblReq [i].setVisible(false);
		}

		//--- check if we have to display something

		if (elCurrent != null)
		{
			urlTokens = parseTemplate(elCurrent.getValue().trim());

			int control = 0;

			for(int i=0; i<urlTokens.size(); i++)
			{
				Object obj = urlTokens.get(i);

				if (obj instanceof Parameter)
				{
					Parameter param = (Parameter) obj;

					lblData[control].setVisible(true);
					txtData[control].setVisible(true);
					lblData[control].setText(param.name);
					txtData[control].setText(param.value);

					if (param.required)
						lblReq[control].setVisible(true);

					param.setControl(txtData[control++]);
				}
			}
		}
	}

	//---------------------------------------------------------------------------

	public String getUrl()
	{
		StringBuffer sb = new StringBuffer();

		for(int i=0; i<urlTokens.size(); i++)
			sb.append(urlTokens.get(i));

		return sb.toString();
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private List parseTemplate(String template)
	{
		Vector v = new Vector();

		while(!template.equals(""))
		{
			int posPG = template.indexOf("{");
			int posPQ = template.indexOf("[");

			//--- no parenthesis found

			if (posPG == -1 && posPQ == -1)
			{
				v.add(template);
				template = "";
			}

			//--- found [

			else if (posPG == -1)
				template = parseQuadra(template, posPQ, v);

			//--- found {

			else if (posPQ == -1)
				template = parseGraffa(template, posPG, v);

			//--- found both { and [

			else
			{
				if (posPG < posPQ)
					template = parseGraffa(template, posPG, v);
				else
					template = parseQuadra(template, posPQ, v);
			}
		}

		return v;
	}

	//---------------------------------------------------------------------------

	private String parseGraffa(String template, int posPG, Vector v)
	{
		if (posPG > 0)
		{
			v.add(template.substring(0, posPG));
			template = template.substring(posPG);
		}

		int endPG = template.indexOf("}");

		v.add(new Parameter(template.substring(0, endPG+1)));

		return template.substring(endPG +1);
	}

	//---------------------------------------------------------------------------

	private String parseQuadra(String template, int posPQ, Vector v)
	{
		if (posPQ > 0)
		{
			v.add(template.substring(0, posPQ));
			template = template.substring(posPQ);
		}

		int endPQ = template.indexOf("]");

		v.add(new Parameter(template.substring(1, endPQ), false));

		return template.substring(endPQ +1);
	}
}

//==============================================================================

class Parameter
{
	public String prefix = "";
	public String name;
	public String value  = "";
	public String suffix = "";

	public boolean required = true;

	private TTextField txtField;

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Parameter(String s)
	{
		this(s, true);
	}

	//---------------------------------------------------------------------------

	public Parameter(String s, boolean required)
	{
		int posPG = s.indexOf("{");
		int endPG = s.indexOf("}");

		if (posPG > 0)
			prefix = s.substring(0, posPG);

		if (endPG != s.length()-1)
			suffix = s.substring(endPG+1);

		s = s.substring(posPG +1, endPG);

		int posEQ = s.indexOf("=");

		if (posEQ == -1)
			name = s;
		else
		{
			name = s.substring(0, posEQ);
			value= s.substring(posEQ +1);
		}

		this.required = required;
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setControl(TTextField tf)
	{
		txtField = tf;
	}

	//---------------------------------------------------------------------------

	public String toString()
	{
		String param = txtField.getText().trim();

		if (required)
			return param;
		else
		{
			if (param.equals(""))
				return "";
			else
				return prefix+param+suffix;
		}
	}

	//---------------------------------------------------------------------------

	public String getDebugString()
	{
		return "[NAME:"+name+", VAL:|"+value+"|, REQ:"+required+", PRE:|"+prefix+"|, SUF:|"+suffix+"|]";
	}
}

//==============================================================================
