/*
 * PreSpellTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision: 8541 $
 *
 * Last Editor: $Author: thpr $
 *
 * Last Edited: $Date: 2008-12-04 21:18:26 -0500 (Thu, 04 Dec 2008) $
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;

public class PreSpellDescriptorTest extends AbstractCharacterTestCase
{

	public static void main(final String[] args)
	{
		TestRunner.run(PreSpellDescriptorTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSpellDescriptorTest.class);
	}

	Spell burning = null;
	Spell fireball = null;
	Spell lightning = null;
	Spell heal = null;
	Spell cure = null;
	private PCClass wiz;
	private PCClass cle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void additionalSetUp() throws Exception
	{
		LoadContext context = Globals.getContext();
		wiz = context.ref.constructCDOMObject(PCClass.class, "Wizard");
		context.unconditionallyProcess(wiz, "SPELLTYPE", "ARCANE");
		context.unconditionallyProcess(wiz, "KNOWNSPELLS", "LEVEL=1|LEVEL=2");
		context.unconditionallyProcess(wiz.getOriginalClassLevel(1), "CAST", "1,1");
		context.unconditionallyProcess(wiz.getOriginalClassLevel(2), "CAST", "2,2,1");
		cle = context.ref.constructCDOMObject(PCClass.class, "Cleric");
		context.unconditionallyProcess(cle, "SPELLTYPE", "DIVINE");
		context.unconditionallyProcess(cle, "KNOWNSPELLS", "LEVEL=1|LEVEL=2");
		context.unconditionallyProcess(cle.getOriginalClassLevel(1), "CAST", "1,1");
		context.unconditionallyProcess(cle.getOriginalClassLevel(2), "CAST", "1,1,1");

		fireball = new Spell();
		fireball.setName("Fireball");
		context.ref.importObject(fireball);
		Globals.addToSpellMap(fireball.getKeyName(), fireball);
		context.unconditionallyProcess(fireball, "CLASSES", "Wizard=2");
		context.unconditionallyProcess(fireball, "DESCRIPTOR", "Fire");

		lightning = new Spell();
		lightning.setName("Lightning Bolt");
		context.ref.importObject(lightning);
		Globals.addToSpellMap(lightning.getKeyName(), lightning);
		context.unconditionallyProcess(lightning, "CLASSES", "Wizard=2");
		context.unconditionallyProcess(lightning, "DESCRIPTOR", "Useful");

		burning = new Spell();
		burning.setName("Burning Hands");
		context.ref.importObject(burning);
		Globals.addToSpellMap(burning.getKeyName(), burning);
		context.unconditionallyProcess(burning, "CLASSES", "Wizard=1");
		context.unconditionallyProcess(burning, "DESCRIPTOR", "Fire");

		heal = new Spell();
		heal.setName("Heal");
		context.ref.importObject(heal);
		Globals.addToSpellMap(heal.getKeyName(), heal);
		context.unconditionallyProcess(heal, "CLASSES", "Cleric=2");
		context.unconditionallyProcess(heal, "DESCRIPTOR", "Useful");

		cure = new Spell();
		cure.setName("Cure Light Wounds");
		context.ref.importObject(cure);
		Globals.addToSpellMap(cure.getKeyName(), cure);
		context.unconditionallyProcess(cure, "CLASSES", "Cleric=1");
		context.unconditionallyProcess(cure, "DESCRIPTOR", "Useful");

		context.ref.buildDerivedObjects();
		assertTrue(context.ref.resolveReferences(null));
	}

	public void testSimpleDescriptor() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("SpellDescriptor");
		prereq.setKey("Fire");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");

		final PlayerCharacter character = getCharacter();
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	public void testTwoClassDescriptor() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory
				.parse("PRESPELLDESCRIPTOR:3,Fire=2,Useful=2");

		assertFalse(PrereqHandler.passes(prereq, character, null));
		character.incrementClassLevel(1, wiz);
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		character.incrementClassLevel(1, cle);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		character.incrementClassLevel(1, cle);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	public void testNotSimpleDescriptor() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("SpellDescriptor");
		prereq.setKey("Fire");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("2");

		final PlayerCharacter character = getCharacter();
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	public void testNotTwoClassDescriptor() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory
				.parse("!PRESPELLDESCRIPTOR:3,Fire=2,Useful=2");

		assertTrue(PrereqHandler.passes(prereq, character, null));
		character.incrementClassLevel(1, wiz);
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		character.incrementClassLevel(1, wiz);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		character.incrementClassLevel(1, cle);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		character.incrementClassLevel(1, cle);
		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

}
