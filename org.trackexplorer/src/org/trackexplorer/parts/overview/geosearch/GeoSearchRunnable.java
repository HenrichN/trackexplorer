package org.trackexplorer.parts.overview.geosearch;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.trackexplorer.model.Bounds;
import org.trackexplorer.model.IGeoSearchService;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * The geo search shall use a {@code ProgressMonitorDialog} to report the progress.
 * The {code ProgressMonitorDialog} expects an {@code IRunnableWithProgress}.
 * 
 * This class basically wraps the call to the {@code IGeoSearchService} into
 * an {@code IRunnableWithProgress}.
 */
public class GeoSearchRunnable implements IRunnableWithProgress{
	private Bounds bounds;
	private IGeoSearchService searchService;
	private ITrackService trackService;
	private Consumer<Set<TrackMetaInfo>> processResults;
	

	/**
	 * @param bounds The bounds which shall be passed to the {@code searchService}.
	 * @param searchService The geo search service which shall be used.
	 * @param trackService The service to provide the tracks to the {@code searchService}.
	 * @param processResults A callback which shall be called with the results of the geo search. Null is
	 * passed if no results where found.
	 */
	public GeoSearchRunnable(Bounds bounds,
			IGeoSearchService searchService,
			ITrackService trackService,
			Consumer<Set<TrackMetaInfo>> processResults) {
		this.bounds = bounds;
		this.searchService = searchService;
		this.trackService = trackService;
		this.processResults = processResults;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask("Searching", this.trackService.getAvailabeTracks().size());
		Set<TrackMetaInfo> searchResult = this.searchService.search(bounds,
				this.trackService,
				amount -> monitor.worked(amount),
				() -> monitor.isCanceled());
		
		monitor.done();
		// If the user has canceled the operation, return null
		if(monitor.isCanceled()) {
			this.processResults.accept(null);
		}
		else {
			this.processResults.accept(searchResult);
		}
	}

}
