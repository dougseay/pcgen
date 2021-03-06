/*
 * CompanionFacadeDelegate.java
 * Copyright 2012 (C) Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Mar 18, 2012, 11:49:23 PM
 */
package pcgen.gui2.facade;

import java.io.File;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;

/**
 * The <code>CompanionFacadeDelegate</code> is a <code>CompanionFacade</code>
 * implementation that delegates to another CompanionFacade.
 * All internal reference facades are themselves delegates to the underlying
 * CompanionFacade.
 * This class is used to help aid implementation of the
 * <code>CompanionSupportFacadeImpl</code>
 * @see pcgen.gui2.facade.CompanionSupportFacadeImpl
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompanionFacadeDelegate implements CompanionFacade
{

	private CompanionFacade delegate;
	private DelegateReferenceFacade<String> nameDelegate;
	private DelegateReferenceFacade<File> fileDelegate;
	private DelegateReferenceFacade<RaceFacade> raceDelegate;

	public CompanionFacadeDelegate()
	{
		this.nameDelegate = new DelegateReferenceFacade<String>();
		this.fileDelegate = new DelegateReferenceFacade<File>();
		this.raceDelegate = new DelegateReferenceFacade<RaceFacade>();
	}

	public void setCompanionFacade(CompanionFacade companionFacade)
	{
		delegate = companionFacade;
		nameDelegate.setDelegate(companionFacade.getNameRef());
		fileDelegate.setDelegate(companionFacade.getFileRef());
		raceDelegate.setDelegate(companionFacade.getRaceRef());
	}

	/**
	 * @return The CompanionFacade backing this CompanionFacadeDelegate 
	 */
	CompanionFacade getDelegate()
	{
		return delegate;
	}
	
	@Override
	public ReferenceFacade<String> getNameRef()
	{
		return nameDelegate;
	}

	@Override
	public ReferenceFacade<File> getFileRef()
	{
		return fileDelegate;
	}

	@Override
	public ReferenceFacade<RaceFacade> getRaceRef()
	{
		return raceDelegate;
	}

	@Override
	public String getCompanionType()
	{
		if (delegate == null)
		{
			return null;
		}
		return delegate.getCompanionType();
	}

	private static class DelegateReferenceFacade<T> extends DefaultReferenceFacade<T>
		implements ReferenceListener<T>
	{

		private ReferenceFacade<T> delegate;

		public void setDelegate(ReferenceFacade<T> newDelegate)
		{
			if (delegate != null)
			{
				delegate.removeReferenceListener(this);
			}
			delegate = newDelegate;
			if (delegate != null)
			{
				delegate.addReferenceListener(this);
				setReference(delegate.getReference());
			}
			else
			{
				setReference(null);
			}
		}

		@Override
		public void referenceChanged(ReferenceEvent<T> e)
		{
			setReference(e.getNewReference());
		}

	}
}
