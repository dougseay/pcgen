/*
 * Copyright 2012 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.core.bonus;

import java.util.List;
import java.util.Set;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.InfoFacade;
import pcgen.core.spell.Spell;
import pcgen.gui2.facade.TempBonusHelper;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

public class TempBonusTest extends AbstractCharacterTestCase
{

	public void testPCTemporaryBonus() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();
		BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		Spell spell = context.ref.constructNowIfNecessary(Spell.class, "PCTempBonusItem");
		spell.addToListFor(ListKey.BONUS_PC, bonus);
		assertFalse(TempBonusHelper.hasAnyPCTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasPCTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasNonPCTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasCharacterTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasEquipmentTempBonus(spell, character));
		try
		{
			assertTrue(TempBonusHelper.getEquipmentApplyString(spell, character).isEmpty());
		}
		catch (NullPointerException e)
		{
			//This is appropriate too
		}
	}

	
	public void testANYPCTemporaryBonus() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();
		BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		Spell spell = context.ref.constructNowIfNecessary(Spell.class, "PCTempBonusItem");
		spell.addToListFor(ListKey.BONUS_ANYPC, bonus);
		assertTrue(TempBonusHelper.hasAnyPCTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasPCTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasNonPCTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasCharacterTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasEquipmentTempBonus(spell, character));
		try
		{
			assertTrue(TempBonusHelper.getEquipmentApplyString(spell, character).isEmpty());
		}
		catch (NullPointerException e)
		{
			//This is appropriate too
		}
	}

	
	public void testEquipmentTemporaryBonus() throws PersistenceLayerException
	{
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();
		BonusObj bonus = Bonus.newBonus(context, "WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement");
		EquipBonus tb = new EquipBonus(bonus, "MARTIAL;SIMPLE;EXOTIC");
		Spell spell = context.ref.constructNowIfNecessary(Spell.class, "PCTempBonusItem");
		spell.addToListFor(ListKey.BONUS_EQUIP, tb);
		assertFalse(TempBonusHelper.hasAnyPCTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasPCTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasNonPCTempBonus(spell, character));
		assertFalse(TempBonusHelper.hasCharacterTempBonus(spell, character));
		assertTrue(TempBonusHelper.hasEquipmentTempBonus(spell, character));
		Set<String> eaStringSet = TempBonusHelper.getEquipmentApplyString(spell, character);
		assertFalse(eaStringSet.isEmpty());
		assertEquals(1, eaStringSet.size());
		assertEquals("MARTIAL;SIMPLE;EXOTIC", eaStringSet.iterator().next());
		Equipment dagger = context.ref.constructNowIfNecessary(Equipment.class, "Dagger");
		dagger.addToListFor(ListKey.TYPE, Type.WEAPON);
		dagger.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
		character.addEquipment(dagger);
		List<InfoFacade> eList = TempBonusHelper.getListOfApplicableEquipment(spell, character);
		assertEquals(1, eList.size());
		assertEquals("Dagger", eList.iterator().next().getKeyName());
	}

}
