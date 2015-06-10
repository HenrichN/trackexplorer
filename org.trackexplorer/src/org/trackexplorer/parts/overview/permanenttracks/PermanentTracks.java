package org.trackexplorer.parts.overview.permanenttracks;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.handlers.AddTrackPermanentlyHandler;
import org.trackexplorer.handlers.RemovePermanentTrackHandler;
import org.trackexplorer.model.DrawableTrackMetaInfo;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * This class provides the controls and functionality for listing
 * the tracks selected for permanent display.
 * 
 * It also maintains a list of the tracks
 * which are displayed permanently.
 */
public class PermanentTracks {	
	/**
	 * Provides the tracks which are available.
	 */
	@Inject
	private ITrackService trackService;
	
	/**
	 * When the user does a right-click on a track of the
	 * {@link tableViewerPermanentTracks},
	 * this class is used to invoke the handler
	 * {@link RemovePermanentTrackHandler}.
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
	 * Provides the current selected track. 
	 */
	@Inject
	private ESelectionService selectionService;

	/**
	 * The {@link handlerService} needs the eclipse context.
	 */
	@Inject
	private IEclipseContext context;

	/**
	 * Main UI control to display the list of tracks which
	 * are shown permanently.
	 */
	private TableViewer tableViewerPermanentTracks;

	/**
	 * Colors for TableViewers etc. need to be managed as system resources.
	 * This class helps in doing this. 
	 */
	private ColorResourceManager colorResourceManager;
	
	/**
	 * Keeps track of the tracks which are selected for permanent display. 
	 */
	private List<TrackMetaInfo> permanentTracks;	

	public PermanentTracks() {
		this.permanentTracks = new LinkedList<>();
	}
	/**
	 * Creates the controls associated with listing permanent tracks.
	 * 
	 * The controls consist of Tableviewer with two columns displaying the name
	 * and length (km) of a track.
	 */
	public void createPermanentTracksControl(Composite parent, EMenuService menuService) {
		// Create color resource manager
		this.colorResourceManager = new ColorResourceManager(parent);
		
		 // Create table viewer
		tableViewerPermanentTracks = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);		
		Table tablePermanentTracks = tableViewerPermanentTracks.getTable();
		tablePermanentTracks.setHeaderVisible(true);
		tablePermanentTracks.setLinesVisible(true);
		tablePermanentTracks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewerPermanentTracks.setContentProvider(ArrayContentProvider.getInstance());
		tableViewerPermanentTracks.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				TrackMetaInfo info1 = (TrackMetaInfo) e1;
				TrackMetaInfo info2 = (TrackMetaInfo) e2;
				return info1.getName().compareToIgnoreCase(info2.getName());
			}
		});		
		
		// Add column for display the name of the track
		TableViewerColumn viewerColumnPermanentName = new TableViewerColumn(tableViewerPermanentTracks, SWT.NONE);
		viewerColumnPermanentName.setLabelProvider(new ColumnLabelProvider() {				
			@Override
			public String getText(Object element) {
				return ((DrawableTrackMetaInfo) element).getName();
			}
			
			@Override
			public Color getBackground(Object element) {
				return colorResourceManager.getColor(
						((DrawableTrackMetaInfo) element).getColor());
				
			}
			
			@Override
			public Color getForeground(Object element) {
				return colorResourceManager.getColor("#ffffff");			
			}
		});
		
		TableColumn columnPermanentName = viewerColumnPermanentName.getColumn();
		columnPermanentName.setWidth(200);
		columnPermanentName.setText("Name");
		
		TableViewerColumn viewerColumnPermanentKm = new TableViewerColumn(tableViewerPermanentTracks, SWT.NONE);
		viewerColumnPermanentKm.setLabelProvider(new ColumnLabelProvider() {			
			@Override
			public String getText(Object element) {
				TrackMetaInfo trackInfo = (TrackMetaInfo) element;
				double totalDistance = trackService.getTotalDistanceInKilometer(trackInfo.getId());
				String distanceString = String.format("%.2f", totalDistance);
				return distanceString;
			}
		});
		
		// Add column for displaying the length (km) of a track
		TableColumn columnPermanentKm = viewerColumnPermanentKm.getColumn();
		columnPermanentKm.setWidth(50);
		columnPermanentKm.setText("km");
		
		TableViewerColumn viewerColumnPermanentHm = new TableViewerColumn(tableViewerPermanentTracks, SWT.NONE);
		viewerColumnPermanentHm.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TrackMetaInfo trackInfo = (TrackMetaInfo) element;
				double totalElevation = trackService.getTotalElevation(trackInfo.getId());
				String elevationString = String.format("%.2f", totalElevation);
				return elevationString;
			}
		});
				
		TableColumn columnPermanentHm = viewerColumnPermanentHm.getColumn();
		columnPermanentHm.setWidth(50);
		columnPermanentHm.setText("hm");
		
		// The permanent track list should not stay in focus when the user switches to another application.
		tablePermanentTracks.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {
				((Table)e.getSource()).deselectAll();						
			}

			@Override
			public void focusGained(FocusEvent e) {
				// Auto-generated method stub				
			}			
		});

		// Update the current selection
		tableViewerPermanentTracks.addSelectionChangedListener(event -> {			
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			selectionService.setSelection(selection.getFirstElement());					
		});		
		
		// Remove a track if the user presses delete
		tablePermanentTracks.addKeyListener(new KeyListener() {			
			@SuppressWarnings("restriction")
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					IStructuredSelection selection = (IStructuredSelection) tableViewerPermanentTracks.getSelection();
					if (!selection.isEmpty()) {
						ParameterizedCommand cmd = commandService.createCommand("org.trackexplorer.command.removepermanentrack",null);
						if(cmd != null) {
							handlerService.executeHandler(cmd, context);
						} 
					}
				}				
			}

			@Override
			public void keyPressed(KeyEvent e) {	
			}
		});
		tableViewerPermanentTracks.setInput(permanentTracks);
		
		// Register popup menus		
		menuService.registerContextMenu(tableViewerPermanentTracks.getControl(), "org.trackexplorer.trackoverview.popupmenu.permanenttracks");
	}

	/**
	 * Invoked whenever a track is added for permanent display.
	 */
	@Inject
	@Optional
	private void subscribeAddTrackPermanently(final @UIEventTopic(TrackExplorerEventConstants.PERMANENT_TRACK_ADDED) DrawableTrackMetaInfo trackInfo) {
		if(trackInfo != null) {
			if(! permanentTracks.contains(trackInfo)) {
				permanentTracks.add(trackInfo);
				tableViewerPermanentTracks.refresh();
			}
		}
	}
	
	/**
	 * Invoked whenever a permanent track is removed.
	 */
	@Inject
	@Optional
	private void subscribeRemovePermanentTrack(final @UIEventTopic(TrackExplorerEventConstants.PERMANENT_TRACK_REMOVED) TrackMetaInfo trackInfo) {
		if(trackInfo != null) {
			if(permanentTracks.contains(trackInfo)) {
				permanentTracks.remove(trackInfo);
				tableViewerPermanentTracks.refresh();
			}
		}
	}
}
