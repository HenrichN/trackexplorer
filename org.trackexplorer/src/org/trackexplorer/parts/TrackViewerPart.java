package org.trackexplorer.parts;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.model.Bounds;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.LatLng;
import org.trackexplorer.model.DrawableTrackMetaInfo;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * This class is responsible for displaying the tracks on a map.
 * 
 * A browser widget is used to display a local website containing 
 * Google Maps. The website uses the Google Maps API for displaying
 * the tracks.
 *
 * Furthermore the website handles a rectangle which indicates the
 * area for the geo search.
 */
public class TrackViewerPart {
	
	/**
	 * A callback function which can be invoked from
	 * JavaScript.
	 * 
	 * It basically serves as a bridge to pass
	 * data from JavaScript to this program.
	 * 
	 * It will send the message {@code GEO_SEARCH_AREA}
	 * on the bus.
	 * 
	 * The data which is transfered is the bounding box of
	 * the rectangle indicating the search area for a geo search.
	 * See {@link TrackViewerPart.subscribeRequestGeoSearchArea}.
	 */	
	private class BrowserBoundsCallback extends BrowserFunction {
		BrowserBoundsCallback (Browser browser) {
			super (browser, "BoundsCallback");
		}
				
		public Object function (Object[] arguments) {
			double latSoutWest = ((Double) arguments[0]).doubleValue();
			double lngSouthWest = ((Double) arguments[1]).doubleValue();
			
			double latNorthEast = ((Double) arguments[2]).doubleValue();
			double lngNorthEast = ((Double) arguments[3]).doubleValue();
			
			broker.post(TrackExplorerEventConstants.GEO_SEARCH_AREA,
					new Bounds(
							new LatLng(latSoutWest, lngSouthWest),
							new LatLng(latNorthEast, lngNorthEast)));

			return null;
		}
     }

	/**
	 * Used for sending events on the event bus.
	 */
	@Inject
	private IEventBroker broker;	

	/**
	 * Provides access to the available tracks.
	 */
	@Inject
	private ITrackService trackService;

	/**
	 * UI element which contains the browser used to display Google Maps.
	 */
	private Browser browser;
	
	/**
	 * Callback function which can be invoked from JavaScript. 
	 */
	private BrowserBoundsCallback browserCallback;
	
	/**
	 * Define HTML error page which should be displayed in case
	 * index.html cannot be loaded.
	 */
	final private String htmlErrorPage = "<html><body><h1>Error loading index.html</body></html>";
	
	public TrackViewerPart() {
				 
	}
	
	/**
	 * Set up the browser.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		browser = new Browser(parent, SWT.NONE);
		URL url = null;
		File file = null;

		try {
			url = new URL("platform:/plugin/org.trackexplorer/pages/index.html");
			URL resolvedFileURL = FileLocator.toFileURL(url);
			URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			file = new File(resolvedURI);
		} catch (URISyntaxException e) {		
			browser.setText(htmlErrorPage);
		} catch (IOException e) {
			browser.setText(htmlErrorPage);
		}
		
		if(file != null) {			
			browser.setUrl(file.getAbsolutePath());
		}
		else {
			browser.setText(htmlErrorPage);
		}
		
		// Register browser callback
		this.browserCallback = new BrowserBoundsCallback(browser);		
	}
	
	/**
	 * Invoked whenever a track shall be displayed. Track points are transformed into a JavaScript compatible format
	 * and passed to a JavaScript function for display.
	 * Furthermore the Google Map is centered around the track.
	 */
	@Inject
	@Optional
	private void subscribeShowTrack(final @UIEventTopic(TrackExplorerEventConstants.SHOW_TRACK) DrawableTrackMetaInfo trackInfo) {
		if(trackInfo != null) {
			List<LatLng> points = trackService.getTrackPoints(trackInfo.getId());	
			String waypoints = points.stream()
					.map(LatLng::toString)
					.collect(Collectors.joining(","));
			browser.execute("updatePath(" +
					"'" + trackInfo.getColor() + "'" + "," + 
					"[" + waypoints + "]);");
			
			Bounds bounds = new Bounds(points);
			String s = "fitBounds([" + bounds.getSouthWest().toString() + "," + bounds.getNorthEast().toString() + "]);";
			browser.execute(s);			
		}
	}
	
	/**
	 * Invoked whenever a track shall be displayed permanently.
	 * 
	 * Track points are transformed into a JavaScript compatible format
	 * and passed to a JavaScript function for display.
	 * Furthermore the Google Map is centered around the track.
	 */
	@Inject
	@Optional
	private void subscribeAddTrackPermanently(final @UIEventTopic(TrackExplorerEventConstants.PERMANENT_TRACK_ADDED) DrawableTrackMetaInfo trackInfo) {
		if(trackInfo != null) {
			List<LatLng> points = trackService.getTrackPoints(trackInfo.getId());
			String waypoints = points.stream()
					.map(LatLng::toString)
					.collect(Collectors.joining(","));
						
			browser.execute("addPermanentTrack(" +
					"'" + trackInfo.getId() + "'," +
					"'" + trackInfo.getColor() + "'" + "," + 
					"[" + waypoints + "]);");			
			Bounds bounds = new Bounds(points);
			String s = "fitBounds([" + bounds.getSouthWest().toString() + "," + bounds.getNorthEast().toString() + "]);";
			browser.execute(s);			
		}
	}
	
	/**
	 * Invoked whenever a permanent track is removed.
	 */
	@Inject
	@Optional
	private void subscribeRemovePermanentTrack(final @UIEventTopic(TrackExplorerEventConstants.PERMANENT_TRACK_REMOVED) TrackMetaInfo trackInfo) {
		if(trackInfo != null) {			
			browser.execute("removePermanentTrack(" +
					"'" + trackInfo.getId() + "');");			
		}
	}
	
	/**
	 * Invoked whenever the user enables the geo search functionality.
	 * It calls a JavaScript function which displays a rectangular area on the map.
	 * This rectangle indicates the area which shall be used in a geo search.
	 */
	@Inject
	@Optional
	private void subscribeEnableGeoSearch(@UIEventTopic(TrackExplorerEventConstants.ENABLE_GEO_SEARCH) final Object data) {
		// data should be null, only there to comply with syntax
		browser.execute("showGeoSearchRectangle();");		
	}

	/**
	 * Invoked whenever the user disables the geo search functionality.
	 * The "geo search rectangle" is removed from the map. 
	 */
	@Inject
	@Optional
	private void subscribeDisableGeoSearch(@UIEventTopic(TrackExplorerEventConstants.DISABLE_GEO_SEARCH) final Object data) {
		// data should be null, only there to comply with syntax
		browser.execute("hideGeoSearchRectangle();");
	}

	/**
	 * Invoked whenever the user resets the geo search functionality.
	 * This causes the "geo search rectangle" to be centered around the current
	 * position on the map. 
	 */
	@Inject
	@Optional
	private void subscribeResetGeoSearch(@UIEventTopic(TrackExplorerEventConstants.RESET_GEO_SEARCH) final Object data) {
		// data should be null, only there to comply with syntax
		browser.execute("resetGeoSearchRectangle();");
	}
	
	/**
	 * Invoked whenever the user executes a geographical search by pressing the button "Search"
	 * of the class {@link TrackOverviewPart} causing the event {@code REQUEST_GEO_SEARCH_AREA}
	 * to be sent on the bus.
	 */
	@Inject
	@Optional
	private void subscribeRequestGeoSearchArea(@UIEventTopic(TrackExplorerEventConstants.REQUEST_GEO_SEARCH_AREA) final Object data) {
		// data should be null, only there to comply with syntax
		
		// This function will execute a JavaScript function which in turn will trigger
		// the Java custom function {@link BrowserBoundsCallback}.
		this.browser.execute("getBounds();");
	}	
}
