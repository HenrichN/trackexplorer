package org.trackexplorer.parts.overview;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.trackexplorer.parts.overview.geosearch.GeoSearch;
import org.trackexplorer.parts.overview.permanenttracks.PermanentTracks;
import org.trackexplorer.parts.overview.tracklist.TrackList;

/**
 * This class serves as the main part of the application.
 * 
 * It bundles the functionality for showing the available tracks,
 * selecting tracks for permanent display and to carry out a
 * geographical search.
 */
public class TrackOverviewPart {
	/**
	 * Needed when creating instances of a class with the help
	 * of a {@code ContextInjectionFactory}.
	 */
	@Inject
	private IEclipseContext context;

	/**
	 * Provides the controls and functionality for a geographical search.
	 */
	private GeoSearch geoSearch;

	/**
	 * Provides the controls and functionality for showing the available tracks.
	 */
	private TrackList trackList;
	
	/**
	 * Provides the controls and functionality for showing the tracks selected for permanent display.
	 */
	private PermanentTracks permanentTracks;
								
	/**
	 * Main function which creates the classes providing the actual controls and functionality.
	 */
	@PostConstruct
	public void createControls(Composite parent, EMenuService menuService) {
		parent.setLayout(new GridLayout(1, false));
		
		this.geoSearch = ContextInjectionFactory.make(GeoSearch.class, context);
		this.geoSearch.createGeoSearchControls(parent);
		
		this.trackList = ContextInjectionFactory.make(TrackList.class, context); 
		this.trackList.createTrackListControls(parent, menuService);
		
		this.permanentTracks = ContextInjectionFactory.make(PermanentTracks.class, context); 
		this.permanentTracks.createPermanentTracksControl(parent, menuService);
	}		
}
