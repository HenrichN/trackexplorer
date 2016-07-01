package org.trackexplorer.parts.overview.geosearch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.trackexplorer.model.TrackMetaInfo;
import org.trackexplorer.parts.overview.tracklist.filehierarchy.Tree;

/**
 * Filter to determine whether a track
 * was found during the geo search and shall
 * be displayed.
 */
public class GeoSearchViewerFilter extends ViewerFilter {
	private Set<TrackMetaInfo> resultSet;
	
	public GeoSearchViewerFilter(final Set<TrackMetaInfo> resultSet) {
		if(resultSet == null) {
			this.resultSet = new HashSet<>();
		}
		else {
			this.resultSet = resultSet;
		}
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement,
			Object element) {
		Tree.Node<?> node = (Tree.Node<?>) element;
		// Only show folders if they have children
		if(node.hasChildren()) {
			return true;
		}
		else {
			TrackMetaInfo trackInfo = (TrackMetaInfo) node.getData();
			return resultSet.contains(trackInfo);	
		}
	}
}