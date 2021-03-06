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
package org.dyndns.jkiddo.dmap.chunks.audio.extension;

import org.dyndns.jkiddo.dmp.chunks.UByteChunk;

public class GeniusSeed extends UByteChunk
{
	public GeniusSeed()
	{
		this(0);
	}

	public GeniusSeed(int value)
	{
		super("aeGs", "com.apple.itunes.can-be-genius-seed", value);
	}
}
