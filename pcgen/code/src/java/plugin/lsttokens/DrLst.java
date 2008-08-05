/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.DamageReduction;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */

public class DrLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{
	@Override
	public String getTokenName()
	{
		return "DR";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (".CLEAR".equals(value))
		{
			context.getObjectContext()
					.removeList(obj, ListKey.DAMAGE_REDUCTION);
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, "|");
		DamageReduction dr;
		try
		{
			String[] values = tok.nextToken().split("/");
			if (values.length != 2)
			{
				Logging.errorPrint(getTokenName()
						+ " failed to build DamageReduction with value "
						+ value);
				Logging
						.errorPrint("  ...expected a String with one / as a separator");
				return false;
			}
			if (values[0].length() == 0)
			{
				Logging.errorPrint("Amount of Reduction in " + getTokenName()
						+ " cannot be empty");
				return false;
			}
			if (values[1].length() == 0)
			{
				Logging.errorPrint("Damage Type in " + getTokenName()
						+ " cannot be empty");
				return false;
			}
			dr = new DamageReduction(values[0], values[1]);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint(getTokenName()
					+ " failed to build DamageReduction with value " + value
					+ " ... " + iae.getLocalizedMessage());
			return false;
		}

		if (tok.hasMoreTokens())
		{
			String currentToken = tok.nextToken();
			Prerequisite prereq = getPrerequisite(currentToken);
			if (prereq == null)
			{
				return false;
			}
			dr.addPrerequisite(prereq);
		}
		context.getObjectContext().addToList(obj, ListKey.DAMAGE_REDUCTION, dr);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<DamageReduction> changes = context.getObjectContext()
				.getListChanges(obj, ListKey.DAMAGE_REDUCTION);
		Collection<DamageReduction> added = changes.getAdded();
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (added == null || added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		Set<String> set = new TreeSet<String>();
		if (added != null)
		{
			for (DamageReduction lw : added)
			{
				set.add(lw.getLSTformat());
			}
		}
		list.addAll(set);
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
