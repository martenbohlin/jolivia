/*
    TunesRemote+ - http://code.google.com/p/tunesremote-plus/
    
    Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/
    Copyright (C) 2010 TunesRemote+, http://code.google.com/p/tunesremote-plus/
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    The Initial Developer of the Original Code is Jeffrey Sharkey.
    Portions created by Jeffrey Sharkey are
    Copyright (C) 2008. Jeffrey Sharkey, http://jsharkey.org/
    All Rights Reserved.
 */

package org.dyndns.jkiddo.daap.client;

import org.dyndns.jkiddo.protocol.dmap.Database;
import org.dyndns.jkiddo.protocol.dmap.Playlist;
import org.dyndns.jkiddo.protocol.dmap.chunks.Chunk;
import org.dyndns.jkiddo.protocol.dmap.chunks.daap.BasePlaylist;
import org.dyndns.jkiddo.protocol.dmap.chunks.daap.DatabasePlaylists;
import org.dyndns.jkiddo.protocol.dmap.chunks.daap.ServerDatabases;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.ContentCodesResponse;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.DatabaseShareType;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.ItemCount;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.ItemId;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.ItemName;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.ListingItem;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.LoginResponse;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.PersistentId;
import org.dyndns.jkiddo.protocol.dmap.chunks.dmap.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class Session
{
	public final static Logger logger = LoggerFactory.getLogger(Session.class);

	private final String host;
	private long revision = 1;
	private final int port, sessionId;
	private final Database database, radioDatabase;

	private final Library library;

	private final RemoteControl remoteControl;

	public final long getRevision()
	{
		return revision;
	}

	public final int getSessionId()
	{
		return sessionId;
	}

	final Database getDatabase()
	{
		return database;
	}

	final Database getRadioDatabase()
	{
		return radioDatabase;
	}
	
	public final Library getLibrary()
	{
		return library;
	}

	public final RemoteControl getRemoteControl()
	{
		return remoteControl;
	}

	public Session(String host, int port, String pairingGuid) throws Exception
	{
		// start a session with the itunes server
		this.host = host;
		this.port = port;

		// http://192.168.254.128:3689/login?pairing-guid=0x0000000000000001
		logger.debug(String.format("trying login for host=%s and guid=%s", host, pairingGuid));
		LoginResponse loginResponse = RequestHelper.requestParsed(String.format("%s/login?pairing-guid=0x%s", this.getRequestBase(), pairingGuid));

		sessionId = loginResponse.getSessionId().getValue();

		// Update revision at once. As the initial call, this does not block but simply updates the revision.
		getUpdateBlocking();
		library = new Library(this);
		remoteControl = new RemoteControl(this);

		// http://192.168.254.128:3689/databases?session-id=1301749047
		ServerDatabases serverDatabases = RequestHelper.requestParsed(String.format("%s/databases?session-id=%s", this.getRequestBase(), this.sessionId));

		// Local database
		{
			// For now, the LocalDatabase is sufficient
			ListingItem localDatabase = serverDatabases.getListing().getSingleListingItem(new Predicate<ListingItem>() {
				@Override
				public boolean apply(ListingItem input)
				{
					return DatabaseShareType.LOCAL == input.getSpecificChunk(DatabaseShareType.class).getValue();
				}
			});

			String databaseName = localDatabase.getSpecificChunk(ItemName.class).getValue();
			int itemId = localDatabase.getSpecificChunk(ItemId.class).getValue();
			long persistentId = localDatabase.getSpecificChunk(PersistentId.class).getUnsignedValue().longValue();

			// fetch playlists to find the overall magic "Music" playlist
			DatabasePlaylists allPlaylists = RequestHelper.requestParsed(String.format("%s/databases/%d/containers?session-id=%s&meta=dmap.itemname,dmap.itemcount,dmap.itemid,dmap.persistentid,daap.baseplaylist,com.apple.itunes.special-playlist,com.apple.itunes.smart-playlist,com.apple.itunes.saved-genius,dmap.parentcontainerid,dmap.editcommandssupported", this.getRequestBase(), itemId, this.sessionId));

			// For now, the BasePlayList is sufficient
			ListingItem item = allPlaylists.getListing().getSingleListingItemContainingClass(BasePlaylist.class);

			Playlist playlist = new Playlist(item.getSpecificChunk(ItemName.class).getValue(), item.getSpecificChunk(PersistentId.class).getUnsignedValue().longValue(), item.getSpecificChunk(ItemId.class).getUnsignedValue(), item.getSpecificChunk(ItemCount.class).getUnsignedValue());
			database = new Database(databaseName, itemId, persistentId, playlist);
		}

		// Radio database
		{
			ListingItem database = serverDatabases.getListing().getSingleListingItem(new Predicate<ListingItem>() {
				@Override
				public boolean apply(ListingItem input)
				{
					return DatabaseShareType.RADIO == input.getSpecificChunk(DatabaseShareType.class).getValue();
				}
			});

			String databaseName = database.getSpecificChunk(ItemName.class).getValue();
			int itemId = database.getSpecificChunk(ItemId.class).getValue();
			long persistentId = database.getSpecificChunk(PersistentId.class).getUnsignedValue().longValue();
			radioDatabase = new Database(databaseName, itemId, persistentId);

			DatabasePlaylists allPlaylists = RequestHelper.requestParsed(String.format("%s/databases/%d/containers?session-id=%s&meta=dmap.itemname,dmap.itemcount,dmap.itemid,dmap.persistentid,daap.baseplaylist,com.apple.itunes.special-playlist,com.apple.itunes.smart-playlist,com.apple.itunes.saved-genius,dmap.parentcontainerid,dmap.editcommandssupported", this.getRequestBase(), itemId, this.sessionId));

			Iterable<ListingItem> items = allPlaylists.getListing().getListingItems();
			for(ListingItem item : items)
			{
				Playlist playlist = new Playlist(item.getSpecificChunk(ItemName.class).getValue(), 0, item.getSpecificChunk(ItemId.class).getUnsignedValue(), item.getSpecificChunk(ItemCount.class).getUnsignedValue());
				logger.debug(String.format("found radio genre=%s", playlist.getName()));
				radioDatabase.addPlaylist(null, playlist);

			}
		}
	}

	public String getRequestBase()
	{
		return String.format("http://%s:%d", host, port);
	}

	/**
	 * Logout method disconnects the session on the server. This is being a good DACP citizen that was not happening in previous versions.
	 * 
	 * @throws Exception
	 */
	public void logout() throws Exception
	{
		RequestHelper.dispatch(String.format("%s/logout?session-id=%s", this.getRequestBase(), this.sessionId));
	}

	// Query the media server about the content codes it handles
	// print to stderr as a csv file
	public void listContentCodes() throws Exception
	{
		ContentCodesResponse contentcodes = RequestHelper.requestParsed(String.format("%s/content-codes?session-id=%s", this.getRequestBase(), this.sessionId));
		for(Chunk contentCode : contentcodes)
		{
			logger.info(contentCode.getContentCodeString());
		}
	}

	/**
	 * What is currently known is that pausing a playing number does not release it, but eg. changing to next song does.
	 * 
	 * @return
	 * @throws Exception
	 */
	public UpdateResponse getUpdateBlocking() throws Exception
	{
		// try fetching next revision update using socket keepalive
		// approach
		// using the next revision-number will make itunes keepalive
		// until something happens
		// GET /update?revision-number=1&daap-no-disconnect=1&session-id=1250589827

		UpdateResponse state = RequestHelper.requestParsed(String.format("%s/update?revision-number=%d&daap-no-disconnect=1&session-id=%s", this.getRequestBase(), revision, sessionId), true);
		revision = state.getServerRevision().getUnsignedValue();
		return state;
	}
}
