/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Campaign;
import pcgen.core.PCSpell;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class SpellsLst extends AbstractToken implements GlobalLstToken
{
	@Override
	public String getTokenName()
	{
		return "SPELLS";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!(obj instanceof Campaign))
		{
			obj.getSpellSupport().addSpells(anInt, createSpellsList(value));
			return true;
		}
		return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 * 
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 * 
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private List<PCSpell> createSpellsList(final String sourceLine)
	{
		List<PCSpell> spellList = new ArrayList<PCSpell>();
		StringTokenizer tok = new StringTokenizer(sourceLine, "|");
		boolean isPre = false;

		if (tok.countTokens() > 1)
		{
			String spellBook = tok.nextToken();
			String casterLevel = null;
			String times = "1";
			List<String> preParseSpellList = new ArrayList<String>();
			List<Prerequisite> preList = new ArrayList<Prerequisite>();
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				if (token.startsWith("CASTERLEVEL="))
				{
					if (isPre)
					{
						Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
							+ getTokenName() + ": " + sourceLine);
						Logging.addParseMessage(Logging.LST_ERROR,
							"  PRExxx must be at the END of the Token");
						isPre = false;
					}
					casterLevel = token.substring(12);
				}
				else if (token.startsWith("TIMES="))
				{
					if (isPre)
					{
						Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
							+ getTokenName() + ": " + sourceLine);
						Logging.addParseMessage(Logging.LST_ERROR,
							"  PRExxx must be at the END of the Token");
						isPre = false;
					}
					times = token.substring(6);
				}
				else if (token.startsWith("PRE") || token.startsWith("!PRE"))
				{
					isPre = true;
					try
					{
						PreParserFactory factory =
								PreParserFactory.getInstance();
						preList.add(factory.parse(token));
					}
					catch (PersistenceLayerException ple)
					{
						Logging.addParseMessage(Logging.LST_ERROR, ple
							.getMessage());
					}
				}
				else
				{
					if (isPre)
					{
						Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
							+ getTokenName() + ": " + sourceLine);
						Logging.addParseMessage(Logging.LST_ERROR,
							"  PRExxx must be at the END of the Token");
						isPre = false;
					}
					preParseSpellList.add(token);
				}
			}
			for (int i = 0; i < preParseSpellList.size(); i++)
			{
				StringTokenizer spellTok =
						new StringTokenizer(preParseSpellList.get(i), ",");
				String name = spellTok.nextToken();
				String dcFormula = null;
				if (spellTok.hasMoreTokens())
				{
					dcFormula = spellTok.nextToken();
				}
				PCSpell spell = new PCSpell();
				spell.setName(name);
				spell.setKeyName(spell.getKeyName());
				spell.setSpellbook(spellBook);
				spell.setCasterLevelFormula(casterLevel);
				spell.setTimesPerDay(times);
				spell.setDcFormula(dcFormula);
				for (Prerequisite prereq : preList)
				{
					spell.addPreReq(prereq);
				}
				spellList.add(spell);
			}
		}
		else
		{
			Logging
				.errorPrint("SPELLS: line minimally requires SPELLS:<spellbook name>|<spell name>");
		}
		return spellList;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// if (!(obj instanceof Campaign)) {
		return createSpellsList(context, obj, value);
		// }
		// return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 * 
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 * 
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private boolean createSpellsList(LoadContext context, CDOMObject obj,
		String sourceLine)
	{
		if (isEmpty(sourceLine) || hasIllegalSeparator('|', sourceLine))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(sourceLine, Constants.PIPE);
		String spellBook = tok.nextToken();
		// Formula casterLevel = null;
		String casterLevel = null;
		String times = "1"; // FormulaFactory.getFormulaFor("1");

		if (!tok.hasMoreTokens())
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ ": minimally requires a Spell Name");
			return false;
		}
		String token = tok.nextToken();

		if (token.startsWith("TIMES="))
		{
			// times = FormulaFactory.getFormulaFor(token.substring(6));
			times = token.substring(6);
			if (times.length() == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Error in Times in "
					+ getTokenName() + ": argument was empty");
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ ": minimally requires a Spell Name (after TIMES=)");
				return false;
			}
			token = tok.nextToken();
		}
		if (token.startsWith("CASTERLEVEL="))
		{
			// casterLevel =
			// FormulaFactory.getFormulaFor(token.substring(12));
			casterLevel = token.substring(12);
			if (casterLevel.length() == 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
					"Error in Caster Level in " + getTokenName()
						+ ": argument was empty");
				return false;
			}
			if (!tok.hasMoreTokens())
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ ": minimally requires a Spell Name (after CASTERLEVEL=)");
				return false;
			}
			token = tok.nextToken();
		}

		if (token.charAt(0) == ',')
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " Spell arguments may not start with , : " + token);
			return false;
		}
		if (token.charAt(token.length() - 1) == ',')
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " Spell arguments may not end with , : " + token);
			return false;
		}
		if (token.indexOf(",,") != -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " Spell arguments uses double separator ,, : " + token);
			return false;
		}

		DoubleKeyMap<CDOMReference<Spell>, AssociationKey<String>, String> dkm =
				new DoubleKeyMap<CDOMReference<Spell>, AssociationKey<String>, String>();
		while (true)
		{
			int commaLoc = token.indexOf(',');
			String name = commaLoc == -1 ? token : token.substring(0, commaLoc);
			CDOMReference<Spell> spell =
					context.ref.getCDOMReference(Spell.class, name);
			dkm.put(spell, AssociationKey.CASTER_LEVEL, casterLevel);
			dkm.put(spell, AssociationKey.TIMES_PER_DAY, times);
			dkm.put(spell, AssociationKey.SPELLBOOK, spellBook);
			if (commaLoc != -1)
			{
				dkm.put(spell, AssociationKey.DC_FORMULA, token
					.substring(commaLoc + 1));
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, obj, dkm, null);
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
					"   (Did you put spells after the "
						+ "PRExxx tags in SPELLS:?)");
				return false;
			}
			prereqs.add(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		finish(context, obj, dkm, prereqs);
		return true;
	}

	public void finish(LoadContext context, CDOMObject obj,
		DoubleKeyMap<CDOMReference<Spell>, AssociationKey<String>, String> dkm,
		List<Prerequisite> prereqs)
	{
		for (CDOMReference<Spell> spell : dkm.getKeySet())
		{
			AssociatedPrereqObject edge =
					context.getGraphContext().grant(getTokenName(), obj, spell);
			for (AssociationKey<String> ak : dkm.getSecondaryKeySet(spell))
			{
				edge.setAssociation(ak, dkm.get(spell, ak));
			}
			if (prereqs != null)
			{
				for (Prerequisite prereq : prereqs)
				{
					edge.addPrerequisite(prereq);
				}
			}
		}
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<Spell> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, Spell.class);
		if (changes == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				changes.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		DoubleKeyMapToList<Set<Prerequisite>, Map<AssociationKey<String>, String>, Thingy> m =
				new DoubleKeyMapToList<Set<Prerequisite>, Map<AssociationKey<String>, String>, Thingy>();
		for (LSTWriteable lw : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(lw))
			{
				Map<AssociationKey<String>, String> am =
						new HashMap<AssociationKey<String>, String>();
				String dc = null;
				for (AssociationKey ak : assoc.getAssociationKeys())
				{
					if (AssociationKey.SOURCE_URI.equals(ak)
						|| AssociationKey.FILE_LOCATION.equals(ak))
					{
						// Do nothing
					}
					else if (AssociationKey.DC_FORMULA.equals(ak))
					{
						dc = assoc.getAssociation(AssociationKey.DC_FORMULA);
					}
					else
					{
						am.put(ak, (String) assoc.getAssociation(ak));
					}
				}
				m.addToListFor(new HashSet<Prerequisite>(assoc
					.getPrerequisiteList()), am, new Thingy(lw, dc));
			}
		}

		Set<String> set = new TreeSet<String>();
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			for (Map<AssociationKey<String>, String> am : m
				.getSecondaryKeySet(prereqs))
			{
				StringBuilder sb = new StringBuilder();
				sb.append(am.get(AssociationKey.SPELLBOOK));
				String times = am.get(AssociationKey.TIMES_PER_DAY);
				if (!"1".equals(times))
				{
					sb.append(Constants.PIPE).append("TIMES=").append(times);
				}
				String casterLvl = am.get(AssociationKey.CASTER_LEVEL);
				if (casterLvl != null)
				{
					sb.append(Constants.PIPE).append("CASTERLEVEL=").append(
						casterLvl);
				}
				List<Thingy> thingyList = m.getListFor(prereqs, am);
				Set<String> spellSet = new TreeSet<String>();
				for (Thingy t : thingyList)
				{
					String spellString = t.spell.getLSTformat();
					if (t.dc != null)
					{
						spellString += Constants.COMMA + t.dc;
					}
					spellSet.add(spellString);
				}
				sb.append(Constants.PIPE);
				sb.append(StringUtil.join(spellSet, Constants.PIPE));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				set.add(sb.toString());
			}
		}
		return set.toArray(new String[set.size()]);
	}

	private static class Thingy
	{
		public final LSTWriteable spell;
		public final String dc;

		public Thingy(LSTWriteable sp, String d)
		{
			spell = sp;
			dc = d;
		}
	}
}
