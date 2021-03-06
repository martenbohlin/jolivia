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
package org.dyndns.jkiddo.dmcp.chunks.media;

import org.dyndns.jkiddo.dmap.chunks.audio.SongAlbumId;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.FullscreenStatus;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.NowPlaying;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.NowPlayingAlbum;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.NowPlayingArtist;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.NowPlayingGenre;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.NowPlayingName;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.PlayStatus;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.ProgressRemain;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.ProgressTotal;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.RepeatStatus;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.ShuffleStatus;
import org.dyndns.jkiddo.dmcp.chunks.media.audio.VisualizerStatus;
import org.dyndns.jkiddo.dmcp.chunks.media.extension.GeniusSelectable;
import org.dyndns.jkiddo.dmp.chunks.Chunk;
import org.dyndns.jkiddo.dmp.chunks.ContainerChunk;

public class PlayingStatus extends ContainerChunk
{
	public PlayingStatus()
	{
		super("cmst", "dmcp.playstatus");
	}

	public <T extends Chunk> T getSpecificChunk(Class<T> clazz)
	{
		return getSingleChunk(clazz);
	}

	public PlayStatus getPlayStatus()
	{
		return getSingleChunk(PlayStatus.class);
	}

	public ShuffleStatus getShuffleStatus()
	{
		return getSingleChunk(ShuffleStatus.class);
	}

	public RepeatStatus getRepeatStatus()
	{
		return getSingleChunk(RepeatStatus.class);
	}

	public VisualizerStatus getVisualizerStatus()
	{
		return getSingleChunk(VisualizerStatus.class);
	}

	public FullscreenStatus getFullscreenStatus()
	{
		return getSingleChunk(FullscreenStatus.class);
	}

	public GeniusSelectable getGeniusSelectable()
	{
		return getSingleChunk(GeniusSelectable.class);
	}

	public StatusRevision getStatusRevision()
	{
		return getSingleChunk(StatusRevision.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public NowPlaying getNowPlaying()
	{
		return getSingleChunk(NowPlaying.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public NowPlayingName getTrackName()
	{
		return getSingleChunk(NowPlayingName.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public NowPlayingArtist getTrackArtist()
	{
		return getSingleChunk(NowPlayingArtist.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public NowPlayingAlbum getTrackAlbum()
	{
		return getSingleChunk(NowPlayingAlbum.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public NowPlayingGenre getTrackGenre()
	{
		return getSingleChunk(NowPlayingGenre.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public SongAlbumId getSongAlbumId()
	{
		return getSingleChunk(SongAlbumId.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public ProgressRemain getProgressRemain()
	{
		return getSingleChunk(ProgressRemain.class);
	}

	/**
	 * Is only populated if something is being played.
	 * 
	 * @return
	 */
	public ProgressTotal getProgressTotal()
	{
		return getSingleChunk(ProgressTotal.class);
	}

	// com.apple.itunes.unknown-st(cmst; container)
	// dmap.status(mstt; uint)=200
	// com.apple.itunes.unknown-sr(cmsr; uint)=76
	// com.apple.itunes.unknown-ps(caps; ubyte)=4
	// com.apple.itunes.unknown-sh(cash; ubyte)=1
	// com.apple.itunes.unknown-rp(carp; ubyte)=0
	// com.apple.itunes.unknown-fs(cafs; ubyte)=0
	// com.apple.itunes.unknown-vs(cavs; ubyte)=0
	// com.apple.itunes.unknown-vc(cavc; ubyte)=1
	// com.apple.itunes.unknown-as(caas; uint)=2
	// com.apple.itunes.unknown-ar(caar; uint)=6
	// com.apple.itunes.unknown-fe(cafe; ubyte)=0
	// com.apple.itunes.unknown-ve(cave; ubyte)=1
	// com.apple.itunes.unknown-np('canp')
	// com.apple.itunes.unknown-nn(cann; string)=N'oubliez Jamais
	// com.apple.itunes.unknown-na(cana; string)=Joe Cocker
	// com.apple.itunes.unknown-nl(canl; string)=Greatest Hits
	// com.apple.itunes.unknown-ng(cang; string)=Rock
	// daap.songalbumid(asai; ulong)=4713229614869101231
	// com.apple.itunes.unknown-mk(cmmk; uint)=1
	// com.apple.itunes.can-be-genius-seed(aeGs; ubyte)=1
	// com.apple.itunes.genius-selectable(ceGS; ubyte)=1
	// com.apple.itunes.unknown-sa(casa; uint)=0
	// com.apple.itunes.unknown-nt(cant; uint)=94255
	// com.apple.itunes.unknown-st(cast; uint)=280293
	// com.apple.itunes.unknown-su(casu; ubyte)=1
	// com.apple.itunes.unknown-Qu(ceQu; ubyte)=0
}
