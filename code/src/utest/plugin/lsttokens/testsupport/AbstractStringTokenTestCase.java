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
package plugin.lsttokens.testsupport;

import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public abstract class AbstractStringTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	protected abstract boolean isClearLegal();

	@Test
	public void testInputClear() throws PersistenceLayerException
	{
		try
		{
			assertEquals(isClearLegal(), getToken().parse(primaryContext,
				primaryProf, ".CLEAR"));
		}
		catch (IllegalArgumentException e)
		{
			if (isClearLegal())
			{
				throw e;
			}
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Niederösterreich"));
		assertEquals("Niederösterreich", primaryProf.get(getStringKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		assertEquals("Finger Lakes", primaryProf.get(getStringKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		assertEquals("Rheinhessen", primaryProf.get(getStringKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		assertEquals("Languedoc-Roussillon", primaryProf.get(getStringKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		assertEquals("Yarra Valley", primaryProf.get(getStringKey()));
	}

	public abstract StringKey getStringKey();

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		if (isClearLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, ".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
		assertTrue(getToken().parse(primaryContext, primaryProf, "Start"));
		assertTrue(getToken().parse(primaryContext, primaryProf, "Mod"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "Mod", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf, ".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}
}
