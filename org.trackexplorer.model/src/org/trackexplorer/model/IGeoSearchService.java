package org.trackexplorer.model;

import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * An interface to represent the functionality
 * to locate tracks which pass through the given bounds.
 * 
 * It shall only be checked if one of the track points
 * is inside the given bounds. If only the edge
 * between two track points intersects the bounds, the
 * track can be ignored.
 */
public interface IGeoSearchService {
	/**
	 * Searches for a set of tracks which pass through
	 * the given bounds.
	 * 
	 * @param bounds The bounds to check 
	 * @param trackSerive All tracks provided by this service are to be included in the search 
	 * @param progressCallback A feedback to call whenever a track has been checked
	 * @param stopFeedback A function to indicate whether the search has to stop. It shall be evaluated periodically.
	 * @return A set of tracks which pass through the given bounds.
	 */
	Set<TrackMetaInfo> search(final Bounds bounds,
			final ITrackService trackSerive,
			final Consumer<Integer> progressCallback,
			final BooleanSupplier stopFeedback);
}
