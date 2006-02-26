/*
 * PreEquipTest.java
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
 * Current Ver: $Revision: 1.7 $
 * 
 * Last Editor: $Author: karianna $
 * 
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;


public class PreEquipTest extends TestCase {
	public static void main(String[] args)
	{
		TestRunner.run(PreEquipTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipTest.class);
	}

	public void test1() throws Exception
	{
		PreEquipParser parser = new PreEquipParser();
		// "|!PREEQUIP:1,TYPE=Armor.Medium,TYPE=Armor.Heavy";

		Prerequisite prereq = parser.parse("EQUIP", "1,TYPE=Armor.Medium,TYPE=Armor.Heavy", true, false);

		assertEquals("<prereq operator=\"lt\" operand=\"1\" >\n" + 
				"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Medium\" operator=\"gteq\" operand=\"1\" >\n" + 
				"</prereq>\n" + 
				"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Heavy\" operator=\"gteq\" operand=\"1\" >\n" + 
				"</prereq>\n" + 
				"</prereq>\n", prereq.toString());
	}

}
