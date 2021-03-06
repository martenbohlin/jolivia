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
package org.dyndns.jkiddo.service.dmap;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dyndns.jkiddo.UnknownClientTypeException;
import org.dyndns.jkiddo.service.daap.server.IMusicLibrary;
import org.dyndns.jkiddo.service.dacp.client.ITouchRemoteResource;
import org.dyndns.jkiddo.service.dacp.server.ITouchAbleServerResource;
import org.dyndns.jkiddo.service.dpap.server.IImageLibrary;

import com.google.common.base.Strings;

@Path("/")
@Singleton
public class DMAPInterface implements ITouchAbleServerResource, ITouchRemoteResource, IMusicLibrary, IImageLibrary
{
	@Context
	private HttpServletRequest httpServletRequest;

	// @Context
	// private HttpServletResponse httpServletResponse;

	// @Context
	// private UriInfo uriInfo;

	private final ITouchAbleServerResource remoteControlResource;
	private final ITouchRemoteResource pairingResource;
	private final IMusicLibrary musicLibraryResource;
	private final IImageLibrary imageLibraryResource;

	private static final String CONTROLLER_HEADER_NAME = "Viewer-Only-Client";
	private static final String CONTROLLER_USER_AGENT = "Remote";
	private static final String DAAP_HEADER_NAME = "Client-DAAP-Version";
	private static final String DPAP_HEADER_NAME = "Client-DPAP-Version";

	private static boolean isDaapRequest(HttpServletRequest httpServletRequest)
	{
		if(Strings.isNullOrEmpty(httpServletRequest.getHeader(DAAP_HEADER_NAME)))
			return false;
		return true;
	}

	private static boolean isDpapRequest(HttpServletRequest httpServletRequest)
	{
		if(Strings.isNullOrEmpty(httpServletRequest.getHeader(DPAP_HEADER_NAME)))
			return false;
		return true;
	}

	private static boolean isRemoteControlRequest(HttpServletRequest httpServletRequest)
	{
		if(Strings.isNullOrEmpty(httpServletRequest.getHeader(CONTROLLER_HEADER_NAME)))
			return false;
		if(!httpServletRequest.getHeader(HttpHeaders.USER_AGENT).startsWith(CONTROLLER_USER_AGENT))
			return false;
		return true;
	}

	@Inject
	public DMAPInterface(ITouchAbleServerResource remoteControlResource, ITouchRemoteResource pairingResource, IMusicLibrary musicLibraryResource, IImageLibrary imageLibraryResource)
	{
		this.remoteControlResource = remoteControlResource;
		this.pairingResource = pairingResource;
		this.musicLibraryResource = musicLibraryResource;
		this.imageLibraryResource = imageLibraryResource;
	}

	@Path("favicon.ico")
	@GET
	public Response favicon()
	{
		return Response.noContent().build();
	}

	@Override
	@Path("server-info")
	@GET
	public Response serverInfo(@QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isRemoteControlRequest(httpServletRequest))
			return remoteControlResource.serverInfo(hsgid);
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.serverInfo(hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.serverInfo(hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("login")
	@GET
	public Response login(@QueryParam("pairing-guid") String guid, @QueryParam("hasFP") int value, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isRemoteControlRequest(httpServletRequest))
			return remoteControlResource.login(guid, value, hsgid);
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.login(guid, value, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.login(guid, value, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("update")
	@GET
	public Response update(@QueryParam("session-id") long sessionId, @QueryParam("revision-number") long revisionNumber, @QueryParam("delta") long delta, @QueryParam("daap-no-disconnect") int daapNoDisconnect, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isRemoteControlRequest(httpServletRequest))
			// return remoteControlResource.update(httpServletRequest, httpServletResponse, info, sessionId, revisionNumber, delta, daapNoDisconnect);
			return musicLibraryResource.update(sessionId, revisionNumber, delta, daapNoDisconnect, hsgid);
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.update(sessionId, revisionNumber, delta, daapNoDisconnect, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.update(sessionId, revisionNumber, delta, daapNoDisconnect, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("databases")
	@GET
	public Response databases(@QueryParam("session-id") long sessionId, @QueryParam("revision-number") long revisionNumber, @QueryParam("delta") long delta, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.databases(sessionId, revisionNumber, delta, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.databases(sessionId, revisionNumber, delta, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("databases/{databaseId}/items")
	@GET
	public Response items(@PathParam("databaseId") long databaseId, @QueryParam("session-id") long sessionId, @QueryParam("revision-number") long revisionNumber, @QueryParam("delta") long delta, @QueryParam("type") String type, @QueryParam("meta") String meta, @QueryParam("query") String query, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.items(databaseId, sessionId, revisionNumber, delta, type, meta, query, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.items(databaseId, sessionId, revisionNumber, delta, type, meta, query, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("databases/{databaseId}/containers")
	@GET
	public Response containers(@PathParam("databaseId") long databaseId, @QueryParam("session-id") long sessionId, @QueryParam("revision-number") long revisionNumber, @QueryParam("delta") long delta, @QueryParam("meta") String meta, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.containers(databaseId, sessionId, revisionNumber, delta, meta, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.containers(databaseId, sessionId, revisionNumber, delta, meta, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("databases/{databaseId}/containers/{containerId}/items")
	@GET
	public Response containerItems(@PathParam("containerId") long containerId, @PathParam("databaseId") long databaseId, @QueryParam("session-id") long sessionId, @QueryParam("revision-number") long revisionNumber, @QueryParam("delta") long delta, @QueryParam("meta") String meta, @QueryParam("type") String type, @QueryParam("group-type") String group_type, @QueryParam("sort") String sort, @QueryParam("include-sort-headers") String include_sort_headers, @QueryParam("query") String query, @QueryParam("index") String index, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.containerItems(containerId, databaseId, sessionId, revisionNumber, delta, meta, type, group_type, sort, include_sort_headers, query, index, hsgid);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.containerItems(containerId, databaseId, sessionId, revisionNumber, delta, meta, type, group_type, sort, include_sort_headers, query, index, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("content-codes")
	@GET
	public Response contentCodes() throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.contentCodes();
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.contentCodes();
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("databases/{databaseId}/items/{itemId}.{format}")
	@GET
	public Response item(@PathParam("databaseId") long databaseId, @PathParam("itemId") long itemId, @PathParam("format") String format, @HeaderParam("Range") String rangeHeader) throws IOException
	{
		return musicLibraryResource.item(databaseId, itemId, format, rangeHeader);
	}

	@Override
	@GET
	@Path("pair")
	public Response pair(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse, @QueryParam("pairingcode") String pairingcode, @QueryParam("servicename") String servicename) throws IOException
	{
		return pairingResource.pair(httpServletRequest, httpServletResponse, pairingcode, servicename);
	}

	@Override
	@GET
	@Path("logout")
	public Response logout(@QueryParam("session-id") long sessionId)
	{
		if(isRemoteControlRequest(httpServletRequest))
			return remoteControlResource.logout(sessionId);
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.logout(sessionId);
		if(isDpapRequest(httpServletRequest))
			return imageLibraryResource.logout(sessionId);
		throw new UnknownClientTypeException();
	}

	@Override
	@GET
	@Path("ctrl-int/1/pause")
	public Response pause(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.pause(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/stop")
	public Response stop(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.stop(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playpause")
	public Response playpause(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.playpause(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/nextitem")
	public Response nextitem(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.nextitem(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/previtem")
	public Response previtem(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.previtem(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playlist")
	public Response playlist(@QueryParam("session-id") long session_id)
	{
		return remoteControlResource.playlist(session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playstatusupdate")
	public Response playstatusupdate(@QueryParam("revision-number") long revisionNumber, @QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid) throws IOException
	{
		return remoteControlResource.playstatusupdate(revisionNumber, session_id, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int/1/getspeakers")
	public Response getspeakers(@QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid) throws IOException
	{
		return remoteControlResource.getspeakers(session_id, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int/1/setspeakers")
	public Response setspeakers(@QueryParam("speaker-id") String speaker_id, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.setspeakers(speaker_id, session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playspec")
	public Response playspec(@QueryParam("container-item-spec") String container_item_spec, @QueryParam("item-spec") String item_spec, @QueryParam("container-spec") String container_spec, @QueryParam("dacp.shufflestate") String dacp_shufflestate, @QueryParam("database-spec") String database_spec, @QueryParam("playlist-spec") String playlist_spec, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.playspec(container_item_spec, item_spec, container_spec, dacp_shufflestate, database_spec, playlist_spec, session_id);
	}

	@Override
	@Path("databases/{databaseId}/groups")
	@GET
	public Response groups(@PathParam("databaseId") long databaseId, @QueryParam("meta") String meta, @QueryParam("type") String type, @QueryParam("group-type") String groupType, @QueryParam("sort") String sort, @QueryParam("include-sort-headers") long includeSortHeaders, @QueryParam("query") String query, @QueryParam("session-id") long sessionId, @QueryParam("hsgid") String hsgid) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.groups(databaseId, meta, type, groupType, sort, includeSortHeaders, query, sessionId, hsgid);
		throw new UnknownClientTypeException();
	}

	@Override
	@GET
	@Path("ctrl-int/1/setproperty")
	public Response setproperty(@Context UriInfo uriInfo, @QueryParam("dmcp.volume") String dmcpVolume, @QueryParam("dacp.playingtime") String dacpPlayingtime, @QueryParam("dacp.shufflestate") String dacpShufflestate, @QueryParam("dacp.repeatstate") String dacpRepeatstate, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.setproperty(uriInfo, dmcpVolume, dacpPlayingtime, dacpShufflestate, dacpRepeatstate, session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/getproperty")
	public Response getproperty(@Context UriInfo uriInfo, @QueryParam("properties") String properties, @QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid) throws IOException
	{
		return remoteControlResource.getproperty(uriInfo, properties, session_id, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int/1/cue")
	public Response cue(@QueryParam("commmand") String command, @QueryParam("query") String query, @QueryParam("index") String index, @QueryParam("sort") String sort, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.cue(command, query, index, sort, session_id);
	}

	@Override
	@GET
	@Path("ctrl-int/1/nowplayingartwork")
	public Response nowplayingartwork(@QueryParam("mw") String mw, @QueryParam("mh") String mh, @QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid)
	{
		return remoteControlResource.nowplayingartwork(mw, mh, session_id, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int/1/set-genius-seed")
	public Response editGenius(@QueryParam("database-spec") String database_spec, @QueryParam("item-spec") String item_spec, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.editGenius(database_spec, item_spec, session_id);
	}

	@Override
	@Path("databases/{databaseId}/edit")
	@GET
	public Response editPlaylist(@PathParam("databaseId") long databaseId, @QueryParam("session-id") long sessionId, @QueryParam("action") String action, @QueryParam("edit-params") String edit_params) throws IOException
	{
		return remoteControlResource.editPlaylist(databaseId, sessionId, action, edit_params);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playqueue-contents")
	public Response playQueueContents(@QueryParam("span") int span, @QueryParam("session-id") long session_id)
	{
		return remoteControlResource.playQueueContents(span, session_id);
	}
	@Override
	@GET
	@Path("fp-setup")
	public Response fpSetup(@QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid)
	{
		return remoteControlResource.fpSetup(session_id, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int")
	public Response ctrlInt(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse, @QueryParam("hsgid") String hsgid) throws IOException
	{
		return remoteControlResource.ctrlInt(httpServletRequest, httpServletResponse, hsgid);
	}

	@Override
	@GET
	@Path("ctrl-int/1/playqueue-edit")
	public Response playQueueEdit(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse, @QueryParam("commmand") String command, @QueryParam("query") String query, @QueryParam("queuefilter") String index, @QueryParam("sort") String sort, @QueryParam("session-id") long session_id) throws Exception
	{
		return remoteControlResource.playQueueEdit(httpServletRequest, httpServletResponse, command, query, index, sort, session_id);
	}

	@Override
	// @Path("databases/{databaseId}/groups/{groupdId}/extra_data/artwork")
	@Path("databases/{databaseId}/items/{groupdId}/extra_data/artwork")
	@GET
	public Response artwork(@PathParam("databaseId") long databaseId, @PathParam("groupId") long groupId, @QueryParam("session-id") long sessionId, @QueryParam("mw") String mw, @QueryParam("mh") String mh, @QueryParam("group-type") String group_type, @QueryParam("daapSecInfo") String daapSecInfo) throws IOException
	{
		if(isDaapRequest(httpServletRequest))
			return musicLibraryResource.artwork(databaseId, groupId, sessionId, mw, mh, group_type, daapSecInfo);
		throw new UnknownClientTypeException();
	}

	@Override
	@Path("this_request_is_simply_to_send_a_close_connection_header")
	@GET
	public Response closeConnection() throws IOException
	{
		return imageLibraryResource.closeConnection();
	}

	@Override
	@GET
	@Path("home-share-verify")
	public Response homeShareVerify(@QueryParam("session-id") long session_id, @QueryParam("hsgid") String hsgid, @QueryParam("hspid") String hspid)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
