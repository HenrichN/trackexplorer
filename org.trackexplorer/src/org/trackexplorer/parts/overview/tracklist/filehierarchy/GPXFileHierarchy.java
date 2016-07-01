package org.trackexplorer.parts.overview.tracklist.filehierarchy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.trackexplorer.model.TrackMetaInfo;

/**
 * A helper class to bundle the functionality for creating a
 * tree based representation out of the available tracks.
 */
public class GPXFileHierarchy {
	/**
	 * Creates a tree based representation of the available tracks.
	 * Tracks are bundled according to the folders they are located in.
	 * The folders are extracted from the path of the tracks.
	 * For each folder a new node in the tree is created and the
	 * corresponding tracks are attached to that node.
	 * 	 
	 * @param tracks The tracks to convert into a tree based structure
	 * @return A tree based representation of the tracks
	 */
	public static Tree<TrackMetaInfo> Create(final List<TrackMetaInfo> tracks) {
				
		//Group tracks by parent folder
		Map<String, List<Tree.Node<TrackMetaInfo>>> tracksGroupedByFolder = tracks.stream().
				collect(Collectors.groupingBy(element -> element.getPath().getParent().getFileName().toString(),
						Collectors.mapping(Tree.Node<TrackMetaInfo>::new, Collectors.toList())));
		
		// Convert tracks to tree nodes 
		List<Tree.Node<TrackMetaInfo>> nodes = tracksGroupedByFolder.entrySet().stream().map( element -> {
			// Create parent
			Tree.Node<TrackMetaInfo> node = new Tree.Node<>(new TrackMetaInfo(null, element.getKey(), null));
			// Add children
			node.addChildren(element.getValue());
			return node;
		}).collect(Collectors.toList());
		
		
		// Create empty tree and attach root node
		Tree<TrackMetaInfo> tree = new Tree<TrackMetaInfo>(null);
		tree.getRoot().addChildren(nodes);
		
		return tree;		
	}
}
