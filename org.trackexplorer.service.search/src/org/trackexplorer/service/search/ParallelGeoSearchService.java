package org.trackexplorer.service.search;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.trackexplorer.model.Bounds;
import org.trackexplorer.model.IGeoSearchService;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.LatLng;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * Provides an implementation of the {@link IGeoSearchService}.
 * 
 * The search is carried out in parallel.
 */
public class ParallelGeoSearchService implements IGeoSearchService{
	/*
	 * See {@link IGeoSearchService}
	 */
	@Override
	public Set<TrackMetaInfo> search(final Bounds bounds,
			final ITrackService trackService,
			final Consumer<Integer> progressCallback,
			final BooleanSupplier stopFeedback) {
		
		return trackService.getAvailabeTracks().parallelStream()
				.filter(trackMetaInfo -> {
					boolean result = false;
					if(!stopFeedback.getAsBoolean()) {
						result = match(bounds, trackService.getTrackPoints(trackMetaInfo.getId()));
						progressCallback.accept(1);
					}																
					return result;
				})																
				.collect(Collectors.toSet());
	}
	
	/**
	 * Checks whether any of the points of track is inside the given bounds.
	 */
	private boolean match(final Bounds bounds, final List<LatLng> trackPoints) {
		return trackPoints.stream().anyMatch(point -> bounds.isInside(point));
	}
}
