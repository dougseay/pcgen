/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.kit;

import org.junit.Test;

import pcgen.core.Language;
import pcgen.core.kit.KitLangBonus;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class LangBonusTokenTest extends AbstractKitTokenTestCase<KitLangBonus>
{

	static LangBonusToken token = new LangBonusToken();
	static CDOMSubLineLoader<KitLangBonus> loader = new CDOMSubLineLoader<KitLangBonus>(
			"SKILL", KitLangBonus.class);

	@Override
	public Class<KitLangBonus> getCDOMClass()
	{
		return KitLangBonus.class;
	}

	@Override
	public CDOMSubLineLoader<KitLangBonus> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitLangBonus> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertConstructionError();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Language.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Language.class, "Fireball");
		runRoundRobin("Fireball");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Language.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Language.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Language.class, "English");
		secondaryContext.ref.constructCDOMObject(Language.class, "English");
		runRoundRobin("Fireball" + getJoinCharacter() + "English");
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1" + getJoinCharacter()));
	}

	private char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		assertFalse(parse(getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
	}

}
