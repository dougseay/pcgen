/*
 * AbilityUtilities.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Aug 25, 2005
 *  Refactored from PlayerCharacter, created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.13 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 13:54:42 $
 *
 */
package pcgen.core;

import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.Logging;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * General utilities related to the Ability class.
 *
 * @author   Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version  $Revision: 1.13 $
 */
public class AbilityUtilities
{
	/**
	 * Find an ability that matches a given name in a list (using this is
	 * probably a really bad idea since it doesn't pay attention to category)
	 *
	 * @param   aFeatList  A list of Ability Objects
	 * @param   featName   The name of the Ability being looked for
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getFeatNamedInList(
	    final List   aFeatList,
	    final String featName)
	{
		return getFeatNamedInList(aFeatList, featName, -1);
	}


	/**
	 * Find an ability that matches a given name in a list (using this is
	 * probably a really bad idea since it doesn't pay attention to category).
	 * Also takes an integer representing a type, -1 always matches, otherwise
	 * an ability will only be returned if its type is the same as featType
	 *
	 * @param   aFeatList
	 * @param   featName
	 * @param   featType
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getFeatNamedInList(
	    final List   aFeatList,
	    final String featName,
	    final int    featType)
	{
		if (aFeatList.isEmpty())
		{
			return null;
		}

		for (Iterator e = aFeatList.iterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();

			if (aFeat.getName().equalsIgnoreCase(featName))
			{
				if ((featType == -1) || (aFeat.getFeatType() == featType))
				{
					return aFeat;
				}
			}
		}

		return null;
	}


	/**
	 * Get an Ability (that is the same basic ability as the argument, but may
	 * have had choices applied) from the list.
	 *
	 * @param   abilityList
	 * @param   argAbility
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getMatchingFeatInList(
	    final List    abilityList,
	    final Ability argAbility)
	{
		if (abilityList.isEmpty())
		{
			return null;
		}

		for (Iterator it = abilityList.iterator(); it.hasNext();)
		{
			final Ability anAbility = (Ability) it.next();

			if (anAbility.isSameBaseAbility(argAbility))
			{
				return anAbility;
			}
		}

		return null;
	}

	/**
	 * Extracts the choiceless form of a name, for example, with all choices removed
	 *
	 * @param   aName
	 *
	 * @return  the name with choices stripped
	 */
	public static String removeChoicesFromName(String aName)
	{
		final int anInt = aName.indexOf('(');

		return (anInt >= 0) ? aName.substring(0, anInt).trim() : aName;
	}


	/**
	 * This method attempts to get an Ability Object from the Global Store keyed
	 * by token. If this fails, it checks if token has info in parenthesis
	 * appended to it.  If it does, it strips this and attempts to get an
	 * Ability Keyed by the stripped token.  If this works, it passes back this
	 * Ability, if it does not work, it returns null.
	 *
	 * @param   cat    The category of Ability Object to retrieve
	 * @param   token  The name of the Ability Object
	 *
	 * @return  The ability in category "cat" called "token"
	 */
	public static Ability retrieveAbilityKeyed(String cat, final String token)
	{
		Ability ab = Globals.getAbilityKeyed(cat, token);

		if (ab != null)
		{
			return ab;
		}

		String stripped = removeChoicesFromName(token);
		ab = Globals.getAbilityKeyed(cat, stripped);

		if (ab != null)
		{
			return ab;
		}

		return null;
	}


	/**
	 * Add a virtual feat to the character and include it in the List.
	 *
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 * @param   aPC
	 * @param   levelInfo
	 *
	 * @return  the list with the new Ability added
	 */
	public static List addVirtualFeat(
	    Ability               anAbility,
	    final List            choices,
	    final List            addList,
	    final PlayerCharacter aPC,
	    final PCLevelInfo     levelInfo)
	{
		if (anAbility != null)
		{
			Ability newAbility = (Ability) anAbility.clone();
			newAbility.setFeatType(Ability.ABILITY_VIRTUAL);
			newAbility.clearPreReq();

			if (choices != null)
			{
				final Iterator it = choices.iterator();

				while (it.hasNext())
				{
					final String assoc = (String) it.next();

					if (!newAbility.containsAssociated(assoc))
					{
						newAbility.addAssociated(assoc);
					}
				}
			}

			if (newAbility.isMultiples())
			{
				addList.add(newAbility);

				if (levelInfo != null)
				{
					levelInfo.addObject(newAbility);
				}
			}
			else if (getMatchingFeatInList(addList, newAbility) == null)
			{
				addList.add(newAbility);

				if (levelInfo != null)
				{
					levelInfo.addObject(newAbility);
				}
			}
		}
		aPC.setDirty(true);

		return addList;
	}


	/**
	 * Add a virtual feat to the character by name and include it in the List.
	 * Any choices are extracted from the name and added appropriately
	 *
	 * @param   featName
	 * @param   addList
	 * @param   levelInfo
	 * @param   aPC
	 *
	 * @return  the list with the new Ability added
	 */
	public static List addVirtualFeat(
	    final String      featName,
	    final List        addList,
	    final PCLevelInfo levelInfo,
	    PlayerCharacter   aPC)
	{
		Ability anAbility = Globals.getAbilityNamed("FEAT", featName);
		List    choices   = null;

		if (!anAbility.getName().equalsIgnoreCase(featName))
		{
			final int i = featName.indexOf('(');
			final int j = featName.indexOf(')');

			if ((i >= 0) && (j >= 0))
			{
				choices = Arrays.asList(featName.substring(i + 1, j).split(","));
			}
		}

		return addVirtualFeat(anAbility, choices, addList, aPC, levelInfo);
	}


	/**
	 * Add a Feat to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   playerCharacterLevelInfo
	 * @param   featName                  the name of the Feat to add.
	 * @param   addIt {<code>false</code>} means the character must already have the
	 *                                    feat (which only makes sense if it
	 *                                    allows multiples); {<code>true</code>} means
	 *                                    to add the feat (the only way to add
	 *                                    new feats).
	 * @param   addAll {<code>false</code>} means allow sub-choices; {<code>true</code>} means
	 *                                    no sub-choices.
	 *
	 * @return  1 if adding the Ability but it wasn't there or 0 if the PC does
	 *          not currently have the Ability.
	 */
	public static int modFeat(
	    PlayerCharacter   aPC,
	    final PCLevelInfo playerCharacterLevelInfo,
	    String            featName,
	    final boolean     addIt,
	    boolean           addAll)
	{
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		int     retVal  = addIt ? 1 : 0;
		boolean added   = false;
		String  subName = "";

		// See if our choice is not auto or virtual
		Ability      anAbility = aPC.getRealFeatNamed(featName);
		final String oldName   = featName;

		// if a feat named featName doesn't exist, and featName
		// contains a (blah) descriptor, try removing it.
		if ((anAbility == null) && featName.endsWith(")"))
		{
			final int idx = featName.indexOf('(');

			// we want what is inside the outermost parenthesis
			subName   = featName.substring(idx + 1, featName.lastIndexOf(')'));
			featName  = featName.substring(0, idx).trim();
			anAbility = aPC.getRealFeatNamed(featName);

			if (addAll && (anAbility != null) && (subName.length() != 0))
			{
				addAll = false;
			}
		}

		// (anAbility == null) means we don't have this feat,
		// so we need to add it
		if (addIt && (anAbility == null))
		{
			// adding feat for first time
			anAbility = Globals.getAbilityNamed("FEAT", featName);

			if (anAbility == null)
			{
				anAbility = Globals.getAbilityNamed("FEAT", oldName);

				if (anAbility != null)
				{
					featName = oldName;
					subName  = "";
				}
			}

			if (anAbility != null)
			{
				anAbility = (Ability) anAbility.clone();
			}
			else
			{
				Logging.errorPrint("Feat not found: " + oldName);

				return retVal;
			}

			aPC.addFeat(anAbility, playerCharacterLevelInfo);
			anAbility.getTemplates(aPC.isImporting(), aPC);
		}

		/*
		 * could not find the Ability: addIt true means that no global Ability called
		 * featName exists, addIt false means that the PC does not have this ability
		 */
		if (anAbility == null)
		{
			return retVal;
		}

		return finaliseAbility(anAbility, subName, aPC, addIt, addAll, added, retVal);
	}


	/**
	 * Add an Ability to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   playerCharacterLevelInfo  LevelInfo object to adjust.
	 * @param   argAbility                The ability to process
	 * @param   choice                    For an isMultiples() Ability
	 * @param   addIt {<code>false</code>} means the character must already have the
	 *                                    feat (which only makes sense if it
	 *                                    allows multiples); {<code>true</code>} means
	 *                                    to add the feat (the only way to add
	 *                                    new feats).
	 * @param   addAll {<code>false</code>} means allow sub-choices; {<code>true</code>} means
	 *                                    no sub-choices.
	 * @return  1 if adding the Ability or 0 if removing it.
	 */

	public static int modAbility (
	    PlayerCharacter   aPC,
	    final PCLevelInfo playerCharacterLevelInfo,
	    Ability           argAbility,
	    String            choice,
	    final boolean     addIt,
	    boolean           addAll)
	{

		int     retVal  = addIt ? 1 : 0;
		boolean added   = false;

		if (argAbility == null)
		{
			Logging.errorPrint("Can't process null Ability");
			return retVal;
		}

		if (aPC.isNotImporting()) {aPC.getSpellList();}

		List realAbilities = aPC.getRealFeatsList();
		Ability pcAbility  = getMatchingFeatInList(realAbilities, argAbility);

		if (addAll && (pcAbility != null) && (choice.length() != 0))
		{
			addAll = false;
		}

		// (pcAbility == null) means we don't have this feat,
		// so we need to add it
		if (addIt && (pcAbility == null))
		{
			// adding feat for first time
			pcAbility = (Ability) argAbility.clone();

			aPC.addFeat(pcAbility, playerCharacterLevelInfo);
			pcAbility.getTemplates(aPC.isImporting(), aPC);
		}

		return finaliseAbility(pcAbility, choice, aPC, addIt, addAll, added, retVal);
	}

	/**
	 * Finishes off the processing necessary to add or remove an Ability to/from
	 * a PC.  modFeat or modAbility have identified the Ability (either one
	 * already owned by the PC, or a clone of the Globals copy.  They have added
	 * the Ability to the character, this method ensures that all necessary
	 * adjustments (choices to add etc.) are made.
	 *
	 * @param   anAbility
	 * @param   choice
	 * @param   aPC
	 * @param   addIt
	 * @param   addAll
	 * @param   added
	 * @param   retVal
	 *
	 * @return 1 if adding the Ability, or 0 if removing it.
	 */
	private static int finaliseAbility(
	    Ability         anAbility,
	    String          choice,
	    PlayerCharacter aPC,
	    final boolean   addIt,
	    boolean         addAll,
	    boolean         added,
	    int             retVal)
	{
		// how many sub-choices to make
		double j = (anAbility.getAssociatedCount() * anAbility.getCost(aPC)) + aPC.getFeats();

		// process ADD tags from the feat definition
		if (!addIt)
		{
			anAbility.modAdds(addIt, aPC);
		}

		boolean canSetFeats = true;

		if (addIt || anAbility.isMultiples())
		{
			if (!addAll)
			{
				if ("".equals(choice))
				{
					// Allow sub-choices
					canSetFeats = !anAbility.modChoices(aPC, addIt);
				}
				else
				{
					if (addIt && anAbility.isWeaponProficiency())
					{
						aPC.addWeaponProfToChosenFeats(choice);
						added = true;
					}
					else if (
					    addIt &&
					    (anAbility.isStacks() || !anAbility.containsAssociated(choice)))
					{
						anAbility.addAssociated(choice);
					}
					else if (!addIt && anAbility.containsAssociated(choice))
					{
						anAbility.removeAssociated(choice);
					}
				}
			}
			else
			{
				if (
				    (anAbility.getChoiceString().lastIndexOf('|') >= 0) &&
				    Globals.weaponTypesContains(
				        anAbility.getChoiceString().substring(0,
				            anAbility.getChoiceString().lastIndexOf('|'))))
				{
					final String aName =
						anAbility.getChoiceString().substring(0,
						    anAbility.getChoiceString().lastIndexOf('|'));
					aPC.addWeaponProfToChosenFeats(aName);
				}
			}
		}

		anAbility.modifyChoice(aPC);

		if (anAbility.isMultiples() && !addAll)
		{
			retVal = (anAbility.getAssociatedCount() > 0) ? 1 : 0;
		}

		// process ADD tags from the feat definition
		if (!added && addIt)
		{
			anAbility.modAdds(addIt, aPC);
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;

		if (retVal == 0)
		{
			removed = aPC.removeRealFeat(anAbility);
			aPC.removeNaturalWeapons(anAbility);

			for (int x = 0; x < anAbility.templatesAdded().size(); ++x)
			{
				aPC.removeTemplate(
				    aPC.getTemplateNamed((String) anAbility.templatesAdded().get(x)));
			}
			anAbility.subAddsForLevel(-9, aPC);
		}

		if (!addIt && !anAbility.isMultiples() && removed)
		{
			j += anAbility.getCost(aPC);
		}
		else if (addIt && !anAbility.isMultiples())
		{
			j -= anAbility.getCost(aPC);
		}
		else
		{
			int associatedListSize = anAbility.getAssociatedCount();

			for (Iterator e1 = aPC.getRealFeatsIterator(); e1.hasNext();)
			{
				final Ability myFeat = (Ability) e1.next();

				if (myFeat.getName().equalsIgnoreCase(anAbility.getName()))
				{
					associatedListSize = myFeat.getAssociatedCount();
				}
			}

			j -= (associatedListSize * anAbility.getCost(aPC));
		}

		if (!addAll && canSetFeats)
		{
			aPC.adjustFeats(j - aPC.getFeats());
		}

		aPC.setAutomaticFeatsStable(false);

		if (addIt && !aPC.isImporting())
		{
			anAbility.globalChecks(false, aPC);
			anAbility.checkRemovals(aPC);
		}

		return retVal;
	}
}
