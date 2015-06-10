package org.trackexplorer.service.track;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alternativevision.gpx.GPXParser;
import org.alternativevision.gpx.beans.GPX;
import org.alternativevision.gpx.beans.Route;
import org.alternativevision.gpx.beans.Track;
import org.alternativevision.gpx.beans.Waypoint;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.LatLng;
import org.trackexplorer.model.TrackMetaInfo;


/**
 * A simple, thread safe, implementation of the {@code ITrackService} interface.
 * 
 * The contents of the tracks are only loaded from disk when
 * they are asked for via {@code getTrackPoints}.
 * Once loaded, the track points are stored locally and are
 * not read from disk again.
 * 
 * The same holds true for track information like distance or
 * total elevation. Both values are only computed on demand
 * and are cached locally, too.
 */
public class TrackService implements ITrackService{
	/**
	 * A class to store track information which is
	 * computed when a GPX file is loaded.
	 * 
	 * This class should store information which might
	 * not be available in all GPX files like totalDistance
	 * or totalElevation and needs to calculated on demand.
	 */
	private class TrackInfo {		
		private double totalDistance;
		private double totalElevation;
		
		public TrackInfo() {
			this.totalDistance = 0;
			this.totalElevation = 0;
		}
		
		public TrackInfo(double totalLength, double totalElevation) {
			this.totalDistance = totalLength;
			this.totalElevation = totalElevation;
		}
		
		public double getTotalDistance() {
			return this.totalDistance;
		}
		
		public double getTotalElevation() {
			return this.totalElevation;
		}
	}
	
	/**
	 * Contains a map of GPX files found on disk along
	 * with some basic information like name and path.
	 * 
	 * The {@code id} of {@link TrackMetaInfo} is used as a key to uniquely
	 * identify each track.
	 */
	private Map<String, TrackMetaInfo> trackMetaInfoList;
	
	
	/**
	 * Serves as a cache such that the information stored in 
	 * {@link TrackInfo} needs to be computed only once.
	 * The {@code id} from {@link TrackMetaInfo}
	 * is used as a key.
	 */
	private ConcurrentMap<String, TrackInfo> trackInfoCache;
	
	/**
	 * Serves as a cache such that the track files need
	 * only to be loaded once. 
	 * The {@code id} from {@link TrackMetaInfo}
	 * is used as a key.
	 */	
	private ConcurrentMap<String, GPX> gpxFileCache;

	public TrackService() {
		init();
	}
	
	private void init() {
		trackMetaInfoList = new HashMap<>();
		gpxFileCache = new ConcurrentHashMap<>();
		trackInfoCache = new ConcurrentHashMap<>();
	}

	/**
	 * See {@link ITrackService}.
	 */
	@Override
	public void setLocations(final List<String> locations) {
		init();
		findAvailableTracks(locations);
	}

	/**
	 * See {@link ITrackService}.
	 */
	@Override
	public List<TrackMetaInfo> getAvailabeTracks() {
		return trackMetaInfoList.entrySet().stream()
			.map(pair -> pair.getValue().copy())
			.collect(Collectors.toList());	
	}

	/**
	 * See {@link ITrackService}.
	 * 
	 * Attention! Only the first route or track is used.
	 * No merging is done for several routes or tracks.
	 * See {@link getGPXWaypoints}.
	 * 
	 * If no track points are present, this function returns
	 * an empty list.
	 */
	@Override
	public List<LatLng> getTrackPoints(final String id) {
		GPX gpx = getCachedTrack(id);
		
		List<LatLng> result = new LinkedList<>();
		
		Optional<List<Waypoint>> waypoints = getGPXWaypoints(gpx);
		if(waypoints.isPresent()) {
			result = waypoints.get().stream()
					.map(p -> new LatLng(p.getLatitude(), p.getLongitude()))
					.collect(Collectors.toList());
		}
		
		return result;
	}

	/**
	 * See {@link ITrackService}.
	 */
	@Override
	public double getTotalDistanceInMeter(final String id) {
		TrackInfo trackInfo = this.getCachedTrackInfo(id);
		return trackInfo.getTotalDistance();
	}
	
	/**
	 * See {@link ITrackService}.
	 */
	@Override
	public double getTotalDistanceInKilometer(final String id) {		
		return getTotalDistanceInMeter(id) / 1000.0;
	}

	/**
	 * See {@link ITrackService}.
	 */
	@Override
	public double getTotalElevation(final String id) {
		TrackInfo trackInfo = this.getCachedTrackInfo(id);
		return trackInfo.getTotalElevation();
	}
	
	/**
	 * Searches for available tracks on disk and 
	 * gathers some meta information about them.
	 * 
	 * No tracks are actually loaded!
	 */
	private void findAvailableTracks(final List<String> locations) {
		trackMetaInfoList = locations.stream()
				.flatMap(path -> (this.findGPXFiles(path)).stream())
				.map(name -> { 
					return new TrackMetaInfo(UUID.randomUUID().toString().substring(0, 6),
							name.getFileName().toString(),
							name);	
				})
				.collect(Collectors.toMap(TrackMetaInfo::getId, Function.identity()));
	}

	/**
	 * Searches for tracks at the given location.
	 */
	private List<Path> findGPXFiles(final String searchPathRoot) {
		List<Path> results;
		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.gpx");
		try {
			results = Files.walk(Paths.get(searchPathRoot))
					.filter(path -> matcher.matches(path.getFileName()))
					.collect(Collectors.toList());
		} catch (IOException e) {		
			results = new ArrayList<>();
		}

		return results;		
	}
	
	/**
	 * Loads a track from disk.
	 * 
	 * The track is also stored in the cache {@link gpxFileCache}
	 */
	private GPX loadTrack(final TrackMetaInfo trackInfo) {
		GPXParser p = new GPXParser();
		FileInputStream in = null;
		GPX gpx = null;
		try {
			in = new FileInputStream(trackInfo.getPath().toString());
			gpx = p.parseGPX(in);
		} catch (Exception e) {
			System.out.println("COULD NOT LOAD TRACK!");
			e.printStackTrace();
			
			// Create dummy gpx file such that the rest of 
			// the functions in this class do not constantly have to 
			// check for null pointers
			gpx = new GPX();
		}
				
		// Store the file in the cache
		this.gpxFileCache.put(trackInfo.getId(), gpx);
		
		return gpx;
	}
	
	/**
	 * Computes the {@code TrackInfo} for the given track.
	 * 
	 * Attention! Only the first route or track is used.
	 * No merging is done for several routes or tracks.
	 */
	private TrackInfo computeTrackInfo(final String id) {
		GPX gpx = this.getCachedTrack(id);
		TrackInfo result = new TrackInfo();
		
		Optional<List<Waypoint>> waypoints = getGPXWaypoints(gpx);
		if(waypoints.isPresent()) {			
			double totalDistance = GPXHelperFunctions.ComputeTotalDistance(waypoints.get());
			double totalElevation = GPXHelperFunctions.ComputeTotalElevation(waypoints.get());		
			result = new TrackInfo(totalDistance, totalElevation);
		}
		
		return result;
	}
	
	/**
	 * A wrapper functions which lets us extract
	 * the waypoints from a GPX object regardless if
	 * they are stored in routes or tracks.
	 * 
	 * Attention: This function does not merge tracks or routes.
	 * It always returns the first track or route it finds!
	 */
	private Optional<List<Waypoint>> getGPXWaypoints(GPX gpx) {
		Optional<List<Waypoint>> result = Optional.empty();
		// Check if the file contains a route or track.
		// After that, try to get as far as possible without encountering null.
		if(gpx.getRoutes() != null) {
			if(gpx.getRoutes().iterator().hasNext()) {
				if(gpx.getRoutes().iterator().next().getRoutePoints() != null) {
					result = Optional.of(gpx.getRoutes().iterator().next().getRoutePoints());
				}				
			}
		}
		else if (gpx.getTracks() != null) {
			if(gpx.getTracks().iterator().hasNext()) {
				if(gpx.getTracks().iterator().next().getTrackPoints() != null) {
					result = Optional.of(gpx.getTracks().iterator().next().getTrackPoints());
				}
			}
		}

		return result;
	}

	/**
	 * Tries to get the track from the cache.
	 * 
	 * If the track is not in the cache,
	 * load it and store it in the cache.
	 */
	private GPX getCachedTrack(final String id) {
		synchronized(this.trackMetaInfoList.get(id)) {			
			if(! this.gpxFileCache.containsKey(id)) {
				GPX trackData = loadTrack(trackMetaInfoList.get(id));
				this.gpxFileCache.put(id, trackData);			
			}
			return this.gpxFileCache.get(id);
		}
	}
	
	/**
	 * Tries to get the {@code TrackInfo} from the cache.
	 * 
	 * If the data is not in the cache,
	 * compute it and store it in the cache.
	 */
	private TrackInfo getCachedTrackInfo(final String id) {		
		return this.trackInfoCache.
				computeIfAbsent(id, trackId -> computeTrackInfo(trackId));
		
	}
}
