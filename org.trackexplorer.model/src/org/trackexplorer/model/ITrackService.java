package org.trackexplorer.model;

import java.util.List;

/**
 * A service which shall provide access to the available tracks.
 * 
 * The service shall be configured via {@link setLocations}.
 * As soon as the locations are set, the service shall return data gathered
 * from these locations.
 * 
 * The implementation if free to choose when the actual content of the files is loaded.
 * 
 * The content of the tracks may be cached locally, there is no need to check for
 * changes of a track on disk.
 */
public interface ITrackService {
	/**
	 * Sets the locations where the service shall search for available tracks.
	 * 
	 * @param locations List of strings containing path information.
	 */
	public void setLocations(List<String> locations);	
	
	/**
	 * Creates a list of the available tracks.
	 *
	 * @return List of the tracks available.
	 */
	public List<TrackMetaInfo> getAvailabeTracks();
	
	/**
	 * Returns a list of the track points of the given track.
	 * The id attribute of a {@link TrackMetaInfo} shall be used
	 * to identify the track.  
	 *
	 * @param id The id stored in {@link TrackMetaInfo}
	 * @return List of all points of the track.
	 */
	public List<LatLng> getTrackPoints(String id);
	
	/**
	 * Computes the total distance of a track in meters.
	 *
	 * @param id The id stored in {@link TrackMetaInfo}
	 * @return Total distance of track in meters.
	 */
	public double getTotalDistanceInMeter(String id);
	
	/**
	 * Computes the total distance of a track in kilometers.
	 * 
	 * @param id The id stored in {@link TrackMetaInfo}
	 * @return Total distance of a track in kilometers.
	 */
	public double getTotalDistanceInKilometer(String id);
	
	/**
	 * Computes the total elevation of a track.
	 * If no elevation information is available, this function shall
	 * return 0.
	 * 
	 * @param id The id stored in {@link TrackMetaInfo}
	 * @return Total elevation of a track
	 */
	public double getTotalElevation(String id);
}
