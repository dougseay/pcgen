package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with MENUTOOLTIP Token
 */
public class MenutooltipToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "MENUTOOLTIP";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setModeToolTip(value.replace('|', '\n'));
		return true;
	}
}
