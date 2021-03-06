/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.qualifier.ability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.reference.CategorizedCreator;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractPCQualifierToken;

public class PCToken extends AbstractPCQualifierToken<Ability>
{
	private Category<Ability> category;

	@Override
	public boolean initialize(LoadContext context,
			SelectionCreator<Ability> sc, String condition, String value,
			boolean negate)
	{
		ReferenceManufacturer<Ability> rm = (ReferenceManufacturer<Ability>) sc;
		CategorizedCreator<Ability> fac = (CategorizedCreator<Ability>) rm.getFactory();
		category = fac.getCategory();
		return super.initialize(context, sc, condition, value, negate);
	}

	@Override
	protected Collection<Ability> getPossessed(PlayerCharacter pc)
	{
		Set<Ability> nl = pc.getAbilityList(category, Nature.NORMAL);
		Set<Ability> vl = pc.getAbilityList(category, Nature.VIRTUAL);
		Set<Ability> al = pc.getAbilityList(category, Nature.AUTOMATIC);
		HashSet<Ability> hs = new HashSet<Ability>();
		hs.addAll(nl);
		hs.addAll(vl);
		hs.addAll(al);
		return hs;
	}

	@Override
	public Class<? super Ability> getReferenceClass()
	{
		return Ability.class;
	}

}
