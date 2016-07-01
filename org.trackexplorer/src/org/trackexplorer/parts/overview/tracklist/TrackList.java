package org.trackexplorer.parts.overview.tracklist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.handlers.AddTrackPermanentlyHandler;
import org.trackexplorer.handlers.RemovePermanentTrackHandler;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.TrackMetaInfo;
import org.trackexplorer.parts.overview.geosearch.GeoSearchViewerFilter;
import org.trackexplorer.parts.overview.tracklist.filehierarchy.GPXFileHierarchy;
import org.trackexplorer.parts.overview.tracklist.filehierarchy.GPXFileHierarchyContentProvider;
import org.trackexplorer.parts.overview.tracklist.filehierarchy.Tree;

/**
 * This class provides the controls and functionality for listing
 * the available tracks.
 * 
 * The tracks can be filter via a text search.
 * If a geographical search is carried out (see {@link GeoSearch}),
 * the track list is filtered according to the results.
 * 
 * Furthermore it offers the functionality to select a track for
 * permanent display.
 * 
 * This class is also responsible for informing the {@code ITrackService}
 * in case new locations are added via preferences.
 */
public class TrackList {	
	/**
	 * Provides the tracks which are available.
	 */
	@Inject
	private ITrackService trackService;

	/**
	 * Provides the current selected track. 
	 */
	@Inject
	private ESelectionService selectionService;
	
	/**
	 * When the user does a right-click on a track of the
	 * {@link treeViewerTracks},
	 * this class is used to invoke the corresponding handler
	 * {@link AddTrackPermanentlyHandler} / {@link RemovePermanentTrackHandler}.
	 */
	@Inject
	private EHandlerService handlerService;
	
	/**
	 * The {@link handlerService} needs a command to identify the correct handler.
	 * This class helps in generating the correct command. 
	 */
	@Inject
	private ECommandService commandService;
	
	/**
	 * The {@link handlerService} needs the eclipse context.
	 */
	@Inject
	private IEclipseContext context;
	
	/***
	 * Main UI control to display the available tracks.
	 */
	private TreeViewer treeViewerTracks;

	/**
	 * When the user has executed a geographical search, this
	 * class helps in displaying only the resulting tracks in
	 * the {@link treeViewerTracks}.
	 */
	private GeoSearchViewerFilter geoSearchViewerFilter = null;

	/**
	 * If a user wants to search for a track by name,
	 * this is the UI element which captures the search string.
	 */
	private Text textSearchString;
	
	/**
	 * Used to keep track of the content {@link textSearchString}
	 */
	private String searchString = "";
	
	/**
	 * Button which resets the content of {@link textSearchString}.
	 * This is some kind of "forward" declaration, as the reference
	 * to the button is needed before it is defined.
	 */
	private Button btnReset;

	/**
	 * Create the controls for displaying and searching the available tracks.
	 * 
	 * The controls consist of a Tableviewer, displaying the name of the track
	 * and a text area and a button for searching the tracks by name.
	 */
	public void createTrackListControls(Composite parent, EMenuService menuService) {
		Composite compositeTextSearch = new Composite(parent, SWT.NONE);
		RowLayout rl_compositeTextSearch = new RowLayout(SWT.HORIZONTAL);
		rl_compositeTextSearch.justify = true;
		rl_compositeTextSearch.fill = true;
		rl_compositeTextSearch.center = true;
		rl_compositeTextSearch.marginTop = 0;
		rl_compositeTextSearch.marginLeft = 0;		
		rl_compositeTextSearch.marginBottom = 0;
		compositeTextSearch.setLayout(rl_compositeTextSearch);
				
		// Create text search controls
		textSearchString = new Text(compositeTextSearch, SWT.BORDER);
		textSearchString.setLayoutData(new RowData(298, SWT.DEFAULT));
		textSearchString.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {				
				Text source = (Text) e.getSource();
				
				searchString = source.getText();
				
				if(btnReset.isEnabled()) {
					if(source.getText().isEmpty()) {
						btnReset.setEnabled(false);
					}
				}
				else {
					if(!source.getText().isEmpty()) {
						btnReset.setEnabled(true);
					}
				}
				
				// Trigger refresh
				treeViewerTracks.refresh();		
				
			}			
		});
		
		btnReset = new Button(compositeTextSearch, SWT.FLAT);
		btnReset.setEnabled(false);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textSearchString.setText("");
			}
		});
		btnReset.setText("Reset");
		
		 // Create table viewer
		treeViewerTracks = new TreeViewer(parent, SWT.BORDER);
		treeViewerTracks.setContentProvider(new GPXFileHierarchyContentProvider());
		treeViewerTracks.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Tree.Node<?> node = (Tree.Node<?>) element;
				return ((TrackMetaInfo) node.getData()).getName();
			}			
		});
		
		// Update current selection
		treeViewerTracks.addSelectionChangedListener(new ISelectionChangedListener() {			
			@SuppressWarnings("restriction")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(!selection.isEmpty()) {
					// Check if it is a folder or a file
					Tree.Node<?> node = (Tree.Node<?>) selection.getFirstElement();
					if(! node.hasChildren()) {						
						selectionService.setSelection(node.getData());
						ParameterizedCommand cmd = commandService.createCommand("org.trackexplorer.command.showtrack",null);
						if(cmd != null) {
							handlerService.executeHandler(cmd, context);
						}
					}
					else {
						selectionService.setSelection(null);
					}
				}												
			}
		});
		
		// Only show tracks whose name contain the string entered by the user in {@link textSearchString}.
		// Please note: The empty string matches all tracks.
		treeViewerTracks.addFilter(new ViewerFilter() {			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				Tree.Node<?> node = (Tree.Node<?>) element;
				// Only show folders if they have children
				if(node.hasChildren()) {
					return true;

					// The commented code does not work reliably.
					// Sometimes the same folder is shown twice.
					// Stackoverflow mentions it as the correct answer:
					//
					//					StructuredViewer structuredViewer = (StructuredViewer) viewer;
					//					ITreeContentProvider provider = (ITreeContentProvider) structuredViewer.getContentProvider();
					//					// Test if any of the children would be shown
					//					for (Object child: provider.getChildren(element)){
					//						if (select(viewer, element, child)) {							
					//							return true;
					//						}
					//					}				
					//					return false;
				}
				else {
					TrackMetaInfo trackInfo = (TrackMetaInfo) node.getData();
					return trackInfo.getName().toLowerCase().contains(searchString.toLowerCase());	
				}
			}
		});
		
		// Sort tracks by name
		treeViewerTracks.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				Tree.Node<?> node1 = (Tree.Node<?>) e1;
				Tree.Node<?> node2 = (Tree.Node<?>) e2;
				
				TrackMetaInfo info1 = (TrackMetaInfo) node1.getData();
				TrackMetaInfo info2 = (TrackMetaInfo) node2.getData();								

				return info1.getName().compareToIgnoreCase(info2.getName());
			}			
		});
		
		treeViewerTracks.setInput(GPXFileHierarchy.Create(trackService.getAvailabeTracks()));
		treeViewerTracks.expandAll();
				
		org.eclipse.swt.widgets.Tree treeAllTracks = treeViewerTracks.getTree();
		GridData gd_treeAllTracks = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeAllTracks.heightHint = 120;
		gd_treeAllTracks.minimumWidth = 300;
		treeAllTracks.setLayoutData(gd_treeAllTracks);
		
		// Register popup menus
		menuService.registerContextMenu(treeViewerTracks.getControl(), "org.trackexplorer.trackoverview.popupmenu.tracklist");
	}

	/**
	 * Removes the filter {@link geoSearchViewerFilter} from {@link treeViewerTracks} if present.
	 */
	private void clearGeoSearchFilterFromTreeViewer() {
		if(this.geoSearchViewerFilter != null) {
			this.treeViewerTracks.removeFilter(geoSearchViewerFilter);
		}
	}

	/**
	 * Invoked whenever the user edits the location of tracks via the preference page.
	 */
	@Inject
	private void onLocationChange(@Preference(nodePath="org.trackexplorer",value="locations") String locations) {
		if(locations != null) {
			List<String> locs = Arrays.asList(locations.split(File.pathSeparator));
			trackService.setLocations(locs);
		}
		else {
			trackService.setLocations(new ArrayList<String>());
		}
				
		if(treeViewerTracks != null && (!treeViewerTracks.getTree().isDisposed())) {
			treeViewerTracks.setInput(GPXFileHierarchy.Create(trackService.getAvailabeTracks()));
			treeViewerTracks.expandAll();
		}
	}

	/**
	 * Invoked whenever the geographical search has finished.
	 * Result is used to filter the track list.
	 */
	@Inject
	@Optional
	private void subscribeGeoSearchArea(final @UIEventTopic(TrackExplorerEventConstants.GEO_SEARCH_RESULTS) Set<TrackMetaInfo> searchResult) {
		if(searchResult != null) {
			if(this.geoSearchViewerFilter != null) {
				this.treeViewerTracks.removeFilter(this.geoSearchViewerFilter);
			}
			this.geoSearchViewerFilter = new GeoSearchViewerFilter(searchResult);											
			this.treeViewerTracks.addFilter(this.geoSearchViewerFilter);
		}
	}

	/**
	 * Invoked whenever the user resets the geo search functionality.
	 * Deletes the last search result.  
	 */
	@Inject
	@Optional
	private void subscribeResetGeoSearch(@UIEventTopic(TrackExplorerEventConstants.RESET_GEO_SEARCH) final Object data) {
		clearGeoSearchFilterFromTreeViewer();
	}
	
	/**
	 * Invoked whenever the user disables the geo search functionality.
	 * Deletes the last search result.
	 */
	@Inject
	@Optional
	private void subscribeDisableGeoSearch(@UIEventTopic(TrackExplorerEventConstants.DISABLE_GEO_SEARCH) final Object data) {
		clearGeoSearchFilterFromTreeViewer();
	}

}
