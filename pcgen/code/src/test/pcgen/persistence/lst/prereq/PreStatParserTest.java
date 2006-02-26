/*
 * PreStatLstParserTest.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.9 $
 * Last Editor: $Author: byngl $
 * Last Edited: $Date: 2005/11/05 17:50:30 $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.prereq.Prerequisite;

/**
 * @author wardc
 *
 */
public class PreStatParserTest extends TestCase
{
	public static void main(String[] args)
	{
		TestRunner.run(PreStatParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreStatParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testDex9() throws Exception
	{
		PreStatParser producer = new PreStatParser();

		Prerequisite prereq = producer.parse("STAT", "1,DEX=9", false, false);

//		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"
//				+ "<prereq kind=\"stat\" key=\"DEX\" operator=\"gteq\" operand=\"9\" >\n" + "</prereq>\n" + "</prereq>\n",
//				prereq.toString());
		assertEquals("<prereq kind=\"stat\" key=\"DEX\" operator=\"gteq\" operand=\"9\" >\n" + "</prereq>\n",
				prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testDex9a() throws Exception
	{
		PreParser parser = new PreParser();
		Prerequisite prereq = parser.parse("PRESTAT:1,DEX=9");
System.out.println(prereq);
		assertEquals("<prereq kind=\"stat\" key=\"DEX\" operator=\"gteq\" operand=\"9\" >\n" + "</prereq>\n",
				prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testDex9Str13() throws Exception
	{
		PreStatParser producer = new PreStatParser();

		Prerequisite prereq = producer.parse("STAT", "2,DEX=9,STR=13", false, false);

		assertEquals("<prereq operator=\"gteq\" operand=\"2\" >\n"
		    + "<prereq kind=\"stat\" key=\"DEX\" operator=\"gteq\" operand=\"9\" >\n" + "</prereq>\n"
		    + "<prereq kind=\"stat\" key=\"STR\" operator=\"gteq\" operand=\"13\" >\n" + "</prereq>\n" + "</prereq>\n",
		    prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testDexEqual9() throws Exception
	{
		PreStatParser producer = new PreStatParser();

		Prerequisite prereq = producer.parse("STATEQ", "1,DEX=9", false, false);

//		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"
//		    + "<prereq kind=\"stat\" key=\"DEX\" operator=\"eq\" operand=\"9\" >\n" + "</prereq>\n" + "</prereq>\n",
//		    prereq.toString());
		assertEquals("<prereq kind=\"stat\" key=\"DEX\" operator=\"eq\" operand=\"9\" >\n" + "</prereq>\n",
		    prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testEmpty() throws Exception
	{
		PreStatParser producer = new PreStatParser();

		Prerequisite prereq = producer.parse("STAT", "1", false, false);

		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n" + "</prereq>\n", prereq.toString());
	}
}
