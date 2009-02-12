/*
 * SettingsHandler.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jan 10, 2009, 7:04:57 PM
 */
package pcgen.core;

import java.io.File;
import pcgen.gui.facade.GameModeFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class SettingsHandler
{

	public static File getPcgenSystemDir()
	{
		return new File("build/classes").getAbsoluteFile();
	}

	public static File getPcgenCustomDir()
	{
		return null;
	}

	public static String getSelectedGenerators(String generators)
	{
		return null;
	}

}
