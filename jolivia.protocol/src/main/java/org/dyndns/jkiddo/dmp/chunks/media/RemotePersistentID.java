/*******************************************************************************
 * Copyright (c) 2013 Jens Kristian Villadsen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jens Kristian Villadsen - Lead developer, owner and creator
 ******************************************************************************/
package org.dyndns.jkiddo.dmp.chunks.media;

import org.dyndns.jkiddo.dmp.chunks.ULongChunk;

public class RemotePersistentID extends ULongChunk
{
	public RemotePersistentID()
	{
		this(0);
	}
	public RemotePersistentID(long value)
	{
		super("mrpr", "dmap.remotepersistentid", value);
	}
}
