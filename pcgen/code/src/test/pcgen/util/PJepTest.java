/*
 * PJepTest.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 12-Jul-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.util;

import gmgen.pluginmgr.PluginLoader;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.nfunk.jep.SymbolTable;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

import java.util.Iterator;

/**
 * Tests {@link PJEP}.
 */
public class PJepTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>PJepTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PJepTest()
	{
		super();
	}

    @Override
	public void setUp() throws Exception
	{
		super.setUp();
		final PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
	}

	/**
	 * Constructs a new <code>PJepTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PJepTest(final String name)
	{
		super(name);
	}

	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PJepTest.class);
	}

	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PJepTest.class);
	}

	public void testMin()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("min(5,8,1)");
		final double value = jep.getValue();

		assertEquals("min", 1.0, value, 0.001);
	}

	public void testMax1()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("max(5,8,1)");
		final double value = jep.getValue();

		assertEquals("max", 8.0, value, 0.001);
	}

	public void testMax2()
	{
		final PJEP jep = new PJEP();

		jep
			.parseExpression("max(max(var(\"BL=Wizard\")+var(\"CL=Wizard\"),var(\"BL=Sorcerer\")+var(\"CL=Sorcerer\")),var(\"BL=Cleric\")+var(\"CL=Cleric\"))");

		assertFalse(jep.hasError());
	}

	public void testFloor1()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("floor(5.7)");
		final double value = jep.getValue();

		assertEquals("floor", 5.0, value, 0.001);
	}

	public void testFloor2()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("floor(-5.7)");
		final double value = jep.getValue();

		assertEquals("floor", -6.0, value, 0.001);
	}

	public void testCeil1()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("ceil(5.7)");
		final double value = jep.getValue();

		assertEquals("ceil", 6.0, value, 0.001);
	}

	public void testCeil2()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("ceil(-5.7)");
		final double value = jep.getValue();

		assertEquals("ceil", -5.0, value, 0.001);
	}

	public void testIf()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (5>8, 7, 9)");
		final double value = jep.getValue();

		assertEquals("if", 9.0, value, 0.001);
	}

	public void testIf2()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (5<8, 7, 9)");
		final double value = jep.getValue();

		assertEquals("if", 7.0, value, 0.001);
	}

	public void testIf3()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (5>8, min(5,8,1), max(17,18,29) )");
		final double value = jep.getValue();

		assertEquals("if", 29.0, value, 0.001);
	}

	public void testIf4()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (2 && 2, min(5,8,1), max(17,18,29) )");
		final double value = jep.getValue();

		assertEquals("if", 1.0, value, 0.001);
	}

	public void testIf5()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (1, 5, -5)");
		final double value = jep.getValue();

		assertEquals("if", 5, value, 0.001);
	}

	public void testIf6()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (-1, 5, -5)");
		final double value = jep.getValue();

		assertEquals("if", 5, value, 0.001);
	}

	public void testIf7()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if (0, 5, -5)");
		final double value = jep.getValue();

		assertEquals("if", -5, value, 0.001);
	}

	public void testIf8()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("IF(MONKLVL<=4,-2,0)");
		assertFalse(jep.hasError());

		jep.addVariable("MONKLVL", 3);
		assertEquals(-2, jep.getValue(), 0.1);

	}

	public void testIf9()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("IF(MONKLVL<=4,-2,IF(MONKLVL<=8,-1,0))");
		assertFalse(jep.hasError());

		SymbolTable symTab = jep.getSymbolTable();
		for (Iterator iter = symTab.keySet().iterator(); iter.hasNext();)
		{
			String key = (String) iter.next();
			Double value = (Double) symTab.getValue(key);
			System.out.println(key + " => " + value);
		}
		jep.addVariable("MONKLVL", 5);
		assertEquals(-1, jep.getValue(), 0.1);
	}

	public void testIf10()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("IF((MONKLVL<=4),-2,(IF((MONKLVL<=8),-1,0)))");
		assertFalse(jep.hasError());

		jep.addVariable("MONKLVL", 8);
		assertEquals(-1, jep.getValue(), 0.1);
	}

	public void testIf11()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if(MonkLvl<=4,-2,if(MonkLvl<=8,-1,0))");
		assertFalse(jep.hasError());

		jep.addVariable("MonkLvl", 11);
		assertEquals(0, jep.getValue(), 0.1);
	}

	public void testIf12()
	{
		final PJEP jep = new PJEP();

		jep.parseExpression("if(0==0,-2,5)");
		assertFalse(jep.hasError());

		assertEquals(-2, jep.getValue(), 0.1);

		jep.parseExpression("IF(0==0,-2,5)");
		assertFalse(jep.hasError());

		assertEquals(-2, jep.getValue(), 0.1);
	}

	public void testJepIf()
	{
		final PlayerCharacter character = new PlayerCharacter();
		Float val;
		val = character.getVariableValue("var(\"UseAlternateDamage\")", "");
		assertEquals("Undefined variable should return 0", 0.0, val
			.doubleValue(), 0.1);

		Race giantRace = TestHelper.makeRace("Ogre");
		giantRace.put(VariableKey.getConstant("UseAlternateDamage"),
				FormulaFactory.getFormulaFor(2));
		character.setRace(giantRace);

		val = character.getVariableValue("var(\"UseAlternateDamage\")", "");
		assertEquals("Variable defined to be 2.", 2.0, val.doubleValue(), 0.1);

		val = character.getVariableValue("2==2", "");
		assertEquals("Equality test of 2==2 should be true.", 1.0, val
			.doubleValue(), 0.1);

		val = character.getVariableValue("3-1==2", "");
		assertEquals("Equality test of 3-1==2 should be true.", 1.0, val
			.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")>1", "");
		assertEquals("Variable defined to be 2 should be more than 1", 1.0, val
			.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")<3", "");
		assertEquals(
			"Variable defined to be 2 should be more than 1 be less than 3",
			1.0, val.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")==1", "");
		assertEquals("Variable defined to be 2 should not be equal to 1", 0.0,
			val.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")>=2", "");
		assertEquals("Variable defined to be 2 should be >= 2", 1.0, val
			.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")<=2", "");
		assertEquals("Variable defined to be 2 should be <= 2", 1.0, val
			.doubleValue(), 0.1);

		val = character.getVariableValue("var(\"UseAlternateDamage\")==2", "");
		assertEquals("Variable defined to be 2 should be == 2", 1.0, val
			.doubleValue(), 0.1);

		val =
				character.getVariableValue(
					"IF(var(\"UseAlternateDamage\")==2,-2,5)", "");
		assertEquals("Test should have returned -2", -2, val.doubleValue(), 0.1);
	}
}
