//==============================================================================
//===
//===   GeneralPanel
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package mod.dbms.mysql.sql.panels.table;

import druid.util.gui.guardians.TCheckBoxGuardian;
import druid.util.gui.guardians.TComboBoxGuardian;
import druid.util.gui.guardians.TTextFieldGuardian;
import mod.dbms.mysql.sql.TableSettings;
import org.dlib.gui.FlexLayout;
import org.dlib.gui.TLabel;
import org.dlib.gui.TPanel;

//==============================================================================

public class GeneralPanel extends TPanel
{
	private TComboBoxGuardian  tcbEngine  = new TComboBoxGuardian ("Engine");
	private TTextFieldGuardian ttfCharset = new TTextFieldGuardian("Charset");
	private TCheckBoxGuardian  tcbAutoInc = new TCheckBoxGuardian ("AutoInc", "Auto increment primary key");

	//---------------------------------------------------------------------------

	public GeneralPanel()
	{
		super("General");

		FlexLayout flexL = new FlexLayout(2, 4);
		flexL.setColProp(1, FlexLayout.EXPAND);
		setLayout(flexL);

		add("0,0", new TLabel("Engine"));
		add("0,1", new TLabel("Charset"));

		add("1,0,x",     tcbEngine);
		add("1,1,x",     ttfCharset);
		add("0,3,x,c,2", tcbAutoInc);
		
		//--- setup combobox

		tcbEngine.addItem(TableSettings.TYPE_INNODB, "InnoDB");
		tcbEngine.addItem(TableSettings.TYPE_MYISAM, "MyISAM");
		tcbEngine.addItem(TableSettings.TYPE_MERGE, "MERGE (collection of MyISAM tables, specify UNION in PostSQL)");
		tcbEngine.addItem(TableSettings.TYPE_MEMORY, "MEMORY (stored in memory only)");
		tcbEngine.addItem(TableSettings.TYPE_BDB, "BDB (Berkley DB)");
		tcbEngine.addItem(TableSettings.TYPE_FEDERATED, "Federated (remote storage, include CONNECTION in PostSQL");
		tcbEngine.addItem(TableSettings.TYPE_ARCHIVE, "Archive (without indices)");
		tcbEngine.addItem(TableSettings.TYPE_CSV, "CSV (comma separated values file)");
		tcbEngine.addItem(TableSettings.TYPE_BLACKHOLE, "BLackhole (doesn't store data)");		
	}

	//---------------------------------------------------------------------------

	public void refresh(TableSettings ts)
	{
		tcbEngine .refresh(ts);
		ttfCharset.refresh(ts);
		tcbAutoInc.refresh(ts);
	}
}

//==============================================================================
