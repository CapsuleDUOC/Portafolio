//==============================================================================
//===
//===   DiffUtil
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.treeview.sqldiff.dialog.result.panel;

import org.dlib.gui.TTabbedPane;

//==============================================================================

public class DiffUtil
{
	public static void showTab(TTabbedPane tp)
	{
		int currTab = tp.getSelectedIndex();

		if (currTab == -1 || !tp.isEnabledAt(currTab))
			for(int i=0; i<tp.getTabCount(); i++)
				if (tp.isEnabledAt(i))
				{
					tp.setSelectedIndex(i);
					return;
				}
	}
}

//==============================================================================
