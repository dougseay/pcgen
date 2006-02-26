/*
 * GameModeLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 22, 2002, 4:33 PM
 *
 * Current Ver: $Revision: 1.76 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 10:37:39 $
 *
 */
package pcgen.persistence.lst;

import java.io.File;
import java.util.Map;

import pcgen.core.GameMode;
import pcgen.util.Logging;

/**
 * <code>GameModeLoader</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.76 $
 */
public final class GameModeLoader
{
	/**
	 * Private constructor added to inhibit instance creation for this utility class.
	 */
	private GameModeLoader()
	{
	    // Empty Constructor
	}

	/**
	 * Parse the MISC game information line in the game mode file
	 * @param gameMode
	 * @param aLine
	 * @param aFile
	 * @param lineNum
	 */
	public static void parseMiscGameInfoLine(GameMode gameMode, String aLine, File aFile, int lineNum)
	{
		if (gameMode == null)
		{
			return;
		}

		final int idxColon = aLine.indexOf(':');
		if (idxColon < 0)
		{
			return;
		}

		final String key = aLine.substring(0, idxColon);
		final String value = aLine.substring(idxColon + 1);
		Map tokenMap = TokenStore.inst().getTokenMap(GameModeLstToken.class);
		GameModeLstToken token = (GameModeLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, gameMode.getName(), aFile.getName(), value);
			if (!token.parse(gameMode, value))
			{
				Logging.errorPrint("Error parsing misc. game info " + gameMode.getName() + '/' + aFile.getName() + ':' + Integer.toString(lineNum) + " \"" + aLine + "\"");
			}
		}
		else
		{
			Logging.errorPrint("Illegal misc. game info " + gameMode.getName() + '/' + aFile.getName() + ':' + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
	}
}
