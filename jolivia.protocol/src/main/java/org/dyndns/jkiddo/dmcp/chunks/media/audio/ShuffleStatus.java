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
package org.dyndns.jkiddo.dmcp.chunks.media.audio;

import org.dyndns.jkiddo.dmp.chunks.UByteChunk;

public class ShuffleStatus extends UByteChunk
{
	public ShuffleStatus()
	{
		this(0);
	}

	public ShuffleStatus(int value)
	{
		//# shuffle status: 0=off, 1=on
		super("cash", "dacp.shufflestate", value);
	}

}
