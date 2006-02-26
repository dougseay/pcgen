/**
 * CompareEqualBoolean.java
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
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
 * Current Ver: $Revision: 1.5 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/10/11 14:02:00 $
 */

package pcgen.util.testchecker;

import pcgen.util.TestChecker;

/**
 * Compares booleans
 */
public class CompareEqualBoolean extends TestChecker
{
	private boolean bool;

	/**
	 * Constructor
	 * @param bool
	 */
	public CompareEqualBoolean( boolean bool ) {
		this.bool = bool;
	}

	public boolean check( Object obj ) {
		return obj.equals(new Boolean(this.bool));
	}

	public StringBuffer scribe( StringBuffer buf ) {
		buf.append("a ");
		buf.append(this.bool);
		buf.append(" value");
		return buf;
	}

}
