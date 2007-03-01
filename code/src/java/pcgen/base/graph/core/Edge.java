/*
 * Copyright (c) Thomas Parker, 2004, 2005, 2006.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Aug 31, 2004
 * 
 * Current Ver: $Revision: 1650 $ Last Editor: $Author: thpr $ Last Edited:
 * $Date: 2006-11-12 20:40:28 -0500 (Sun, 12 Nov 2006) $
 * 
 */
package pcgen.base.graph.core;

import java.util.List;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An Edge is an edge which connects to one or more Nodes in a Graph.
 */
public interface Edge<N>
{

	/**
	 * Returns the Node at the given index in the Edge. An Edge in general makes
	 * no guarantee about the order in which a Node will appear in the Edge.
	 * 
	 * @param index
	 *            The index of the Node to be returned
	 * @return The Node at the given index in this Edge
	 */
	public N getNodeAt(int index);

	/**
	 * Returns a List of the Nodes which are adjacent (connected) to the Edge.
	 * 
	 * @return The List of Nodes which are adjacent to the Edge
	 */
	public List<N> getAdjacentNodes();

	/**
	 * Returns the number of adjacent nodes to this Edge.
	 * 
	 * @return The number of adjacent nodes to this Edge.
	 */
	public int getAdjacentNodeCount();

	/**
	 * Returns true if the given Node is adjacent to this Edge. Returns false if
	 * the given Node is not adjacent to this Edge or if the given Node is null.
	 * 
	 * @param gn
	 *            The Node to be tested
	 * @return true if the given Node is adjacent to this Edge; false otherwise.
	 */
	public boolean isAdjacentNode(N gn);
}