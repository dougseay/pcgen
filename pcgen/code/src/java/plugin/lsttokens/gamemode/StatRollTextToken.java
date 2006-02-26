/*
 * StatRollTextToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 17, 2005, 10:50 AM
 *
 * Current Ver: $Revision: 1.3 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006/02/16 01:03:16 $
 *
 */
package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

import java.util.StringTokenizer;


/**
 * <code>StatRollTextToken</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class StatRollTextToken implements GameModeLstToken {

	public String getTokenName() {
		return "STATROLLTEXT";
	}

	//
	// STATROLLTEXT:<stat_val>,<display_text>
	//
	public boolean parse(GameMode gameMode, String value) {
		final StringTokenizer tok = new StringTokenizer(value, "\t");
		if (tok.countTokens() == 2) {
			try {
				final int statValue = Integer.parseInt(tok.nextToken());
				gameMode.addStatDisplayText(statValue, tok.nextToken());
				return true;
			}
			catch (NumberFormatException exc) {
				// returns false
			}
		}
		return false;
	}
}
