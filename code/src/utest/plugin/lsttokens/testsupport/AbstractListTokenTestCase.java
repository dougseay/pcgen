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

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public abstract class AbstractListTokenTestCase<T extends PObject, TC extends PObject>
		extends AbstractTokenTestCase<T>
{

	public abstract Class<TC> getTargetClass();

	public abstract boolean isTypeLegal();

	public abstract boolean isAllLegal();

	public abstract char getJoinCharacter();

	@Test
	public void testArchitecture()
	{
		/*
		 * This case is not handled well by this generic tester, and thus should
		 * be prohibited in this level of automation... - Tom Parker 2/24/2007
		 */
		assertFalse(isTypeLegal() && getJoinCharacter() == '.');
	}

	@Test
	public void testInvalidInputEmptyString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		if (getJoinCharacter() != ',')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		if (getJoinCharacter() != '|')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1|TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		if (getJoinCharacter() != '.')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1.TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf, "TYPE="));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TYPE=One."));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TYPE=One..Two"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TYPE=.One"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputAll() throws PersistenceLayerException
	{
		if (!isAllLegal())
		{
			try
			{
				boolean parse =
						getToken().parse(primaryContext, primaryProf, "ALL");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate());
				}
			}
			catch (IllegalArgumentException e)
			{
				// This is okay too
			}
			assertTrue(primaryGraph.isEmpty());
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(getToken().parse(primaryContext, primaryProf, "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter()));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getJoinCharacter() + "TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP2" + getJoinCharacter() + getJoinCharacter() + "TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP1" + getJoinCharacter() + "TYPE=TestType"
					+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP1" + getJoinCharacter() + "TYPE.TestType.OtherTestType"
					+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf, "TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
			+ getJoinCharacter() + "TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
				+ getJoinCharacter() + "TYPE=OtherTestType"
				+ getJoinCharacter() + "TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"ALL" + getJoinCharacter() + "TestWP1"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "ALL"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"ALL" + getJoinCharacter() + "TYPE=TestType"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TYPE=TestType" + getJoinCharacter() + "ALL"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getTargetClass(), one);
	}

	public abstract boolean isClearLegal();

	public abstract boolean isClearDotLegal();

	@Test
	public void testInputInvalidClear() throws PersistenceLayerException
	{
		if (isClearLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + ".CLEAR")
				&& primaryContext.ref.validate());
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInputInvalidClearDot() throws PersistenceLayerException
	{
		if (isClearDotLegal())
		{
			// DoNotConstruct TestWP1
			assertTrue(getToken().parse(primaryContext, primaryProf,
				".CLEAR.TestWP1"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInputInvalidAddsAfterClearDotNoSideEffect()
		throws PersistenceLayerException
	{
		if (isClearDotLegal() && isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
					+ getJoinCharacter() + "ALL"));
			assertEquals("Bad Clear had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

	@Test
	public void testInputInvalidAddsTypeNoSideEffect()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP3" + getJoinCharacter() + "TYPE="));
			assertEquals("Bad Add had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		construct(primaryContext, "TestWP4");
		construct(secondaryContext, "TestWP4");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(getToken().parse(secondaryContext, secondaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertEquals("Test setup failed", primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP3" + getJoinCharacter() + getJoinCharacter() + "TestWP4"));
		assertEquals("Bad Add had Side Effects", primaryGraph, secondaryGraph);
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
		throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP3" + getJoinCharacter() + "ALL"));
			assertEquals("Bad Add had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

	@Test
	public void testInputInvalidAddsAfterClearNoSideEffect()
		throws PersistenceLayerException
	{
		if (isClearLegal() && isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				".CLEAR" + getJoinCharacter() + "TestWP3" + getJoinCharacter()
					+ "ALL"));
			assertEquals("Bad Clear had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

	@Test
	public void testInputInvalidTypeClearDotNoSideEffect()
		throws PersistenceLayerException
	{
		if (isClearDotLegal() && isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				"TestWP3" + getJoinCharacter() + ".CLEAR.TestWP1"
					+ getJoinCharacter() + ".CLEAR.TYPE="));
			assertEquals("Bad Clear had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

}
