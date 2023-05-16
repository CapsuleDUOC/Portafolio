//==============================================================================
//===
//===   SqlGenModule
//===
//===   Copyright (C) by Andrea Carboni.
//===   This file may be distributed under the terms of the GPL license.
//==============================================================================

package druid.interfaces;

import druid.data.AbstractNode;
import druid.data.TableNode;
import java.util.List;

//==============================================================================

public interface SqlGenModule extends DataGenModule
{
	public String generate(AbstractNode node);
	public String generateDrop(AbstractNode node);
	public String generateExtra(AbstractNode node);
	public List   generateIndexes(TableNode node);
	public List   generateComments(TableNode node);
	public String generateReferences(TableNode node);

	public String comment(String message);
	public String check(AbstractNode node);
	public String getCodeSeparator();
}

//==============================================================================
