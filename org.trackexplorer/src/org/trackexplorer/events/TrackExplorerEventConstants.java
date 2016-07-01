package org.trackexplorer.events;

/**
 * Events which are sent on the event bus.
 * 
 * +-------------------------+-----------------------------+-----------------+
 * | EVENT                   | SENDER                      | RECEIVER        |
 * |-------------------------+-----------------------------+-----------------|
 * | PERMANENT_TRACK_ADDED   | AddTrackPermanentlyHandler  | TrackViewerPart |
 * |                         |                             | PermanentTracks |
 * |-------------------------+-----------------------------+-----------------|
 * | PERMANENT_TRACK_REMOVED | RemovePermanentTrackHandler | TrackViewerPart |
 * |                         |                             | PermanentTracks |
 * |-------------------------+-----------------------------+-----------------|
 * | SHOW_TRACK              | ShowTrackHandler            | TrackViewerPart |
 * |                         |                             | TrackInfoPart   |
 * |-------------------------+-----------------------------+-----------------|
 * | ENABLE_GEO_SEARCH       | GeoSearch                   | TrackViewerPart |
 * |-------------------------+-----------------------------+-----------------|
 * | DISABLE_GEO_SEARCH      | GeoSearch                   | TrackList       |
 * |                         |                             | TrackViewerPart |
 * |-------------------------+-----------------------------+-----------------|
 * | RESET_GEO_SEARCH        | GeoSearch                   | TrackList       |
 * |                         |                             | TrackViewerPart |
 * |-------------------------+-----------------------------+-----------------|
 * | REQUEST_GEO_SEARCH_AREA | GeoSearch                   | TrackViewerPart |
 * |-------------------------+-----------------------------+-----------------|
 * | GEO_SEARCH_RESULTS      | GeoSearch                   | TrackList       |
 * |-------------------------+-----------------------------+-----------------|
 * | GEO_SEARCH_AREA         | TrackViewerPart             | GeoSearch       |
 * +-------------------------+-----------------------------+-----------------+
 */
public interface TrackExplorerEventConstants {
	final String PERMANENT_TRACK_ADDED		= "PERMANENT_TRACK_ADDED";
	final String PERMANENT_TRACK_REMOVED	= "PERMANENT_TRACK_REMOVED";
	final String SHOW_TRACK					= "SHOW_TRACK";
	final String ENABLE_GEO_SEARCH			= "ENABLE_GEO_SEARCH";
	final String DISABLE_GEO_SEARCH			= "DISABLE_GEO_SEARCH";
	final String RESET_GEO_SEARCH			= "RESET_GEO_SEARCH";
	final String REQUEST_GEO_SEARCH_AREA	= "REQUEST_GEO_SEARCH_AREA";
	final String GEO_SEARCH_AREA			= "GEO_SEARCH_AREA";
	final String GEO_SEARCH_RESULTS			= "GEO_SEARCH_RESULTS";
}