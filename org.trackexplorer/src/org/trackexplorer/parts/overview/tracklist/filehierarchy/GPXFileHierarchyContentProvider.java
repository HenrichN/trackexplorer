package org.trackexplorer.parts.overview.tracklist.filehierarchy;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * A content provider for translating the tree hierarchy
 * created by {@link GPXFileHierarchy} to a format
 * which can be used by a JFace TreeViewer.
 */
public class GPXFileHierarchyContentProvider implements ITreeContentProvider {
	private static Object[] EMPTY_ARRAY = new Object[0];

	public GPXFileHierarchyContentProvider() {		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		assert(inputElement instanceof Tree<?>);
		
		@SuppressWarnings("unchecked")
		Tree<TrackMetaInfo> tree = (Tree<TrackMetaInfo>) inputElement;
		Tree.Node<TrackMetaInfo> rootNode = tree.getRoot();
		
		if(rootNode.hasChildren()) {
			return rootNode.getChildren().toArray();
		}
		else {
			return EMPTY_ARRAY;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		assert(parentElement instanceof Tree.Node<?>);
		
		@SuppressWarnings("unchecked")
		Tree.Node<TrackMetaInfo> node = (Tree.Node<TrackMetaInfo>) parentElement;
		if(node.hasChildren()) {
			return node.getChildren().toArray();
		}
		else {
			return EMPTY_ARRAY;
		}
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		assert(element instanceof Tree.Node<?>);
		
		@SuppressWarnings("unchecked")
		Tree.Node<TrackMetaInfo> node = (Tree.Node<TrackMetaInfo>) element;
		return node.hasChildren();
	}
}
