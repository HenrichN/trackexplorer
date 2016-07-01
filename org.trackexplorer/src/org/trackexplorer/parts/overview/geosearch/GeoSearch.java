package org.trackexplorer.parts.overview.geosearch;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.model.Bounds;
import org.trackexplorer.model.IGeoSearchService;
import org.trackexplorer.model.ITrackService;

/**
 * This class provides the controls and functionality for a geographical search.
 * 
 * The geographical search can be enabled, disabled and reset.
 * Furthermore a geographical search can be carried out by using the
 * {@code IGeoSearchService}.
 */
public class GeoSearch {
	/**
	 * Used for sending events on the event bus.
	 */
	@Inject
	private IEventBroker broker;

	/**
	 * Provides the tracks which are available.
	 */
	@Inject
	private ITrackService trackService;
	
	/**
	 * Carries out the geographical search.
	 */
	@Inject
	private IGeoSearchService searchService;
	
	/**
	 * The geographical search is executed asynchronously.
	 * This class is used to sync the UI, see {@link subscribeGeoSearchArea}.
	 */
	@Inject
	private UISynchronize sync;

	public GeoSearch() {
		
	}
	
	/**
	 * Create the controls used for doing a geographical search.
	 * 
	 * The controls consist of three buttons:
	 * One button enables the search after which the user can adjust the search area.
	 * The other button executes the search while
	 * the third button disables the search functionality.
	 */
	public void createGeoSearchControls(Composite parent) {
		Composite compositeGeoSearch = new Composite(parent, SWT.NONE);
		RowLayout rl_compositeGeoSearch = new RowLayout(SWT.HORIZONTAL);
		rl_compositeGeoSearch.justify = true;
		rl_compositeGeoSearch.fill = true;
		rl_compositeGeoSearch.center = true;
		rl_compositeGeoSearch.marginTop = 0;
		rl_compositeGeoSearch.marginLeft = 0;
		rl_compositeGeoSearch.marginBottom = 0;
		compositeGeoSearch.setLayout(rl_compositeGeoSearch);
		
		// Create button for enabling the geo search
		Button btnGeoSearchToggle = new Button(compositeGeoSearch, SWT.FLAT | SWT.TOGGLE);
		btnGeoSearchToggle.setText("Geo search");
		
		// Create button for executing the search
		Button btnSearchGo = new Button(compositeGeoSearch, SWT.FLAT);
		btnSearchGo.setEnabled(false);
		btnSearchGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				broker.post(TrackExplorerEventConstants.REQUEST_GEO_SEARCH_AREA, null);
			}
		});
		btnSearchGo.setText("Go");
		
		// Create button for disabling the search functinality
		Button btnSearchReset = new Button(compositeGeoSearch, SWT.FLAT);
		btnSearchReset.setEnabled(false);
		btnSearchReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				broker.post(TrackExplorerEventConstants.RESET_GEO_SEARCH, null);				
			}
		});
		btnSearchReset.setText("Reset");

		// Set functionality for enabling the geo search
		btnGeoSearchToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isToggled = ((Button) e.getSource()).getSelection();
				if(isToggled) {
					broker.post(TrackExplorerEventConstants.ENABLE_GEO_SEARCH, null);
					btnSearchGo.setEnabled(true);
					btnSearchReset.setEnabled(true);
				}
				else {
					broker.post(TrackExplorerEventConstants.DISABLE_GEO_SEARCH, null);
					
					btnSearchGo.setEnabled(false);
					btnSearchReset.setEnabled(false);
				}
			}
		});
	}
	
	/**
	 * This function is called whenever the user has requested a geographical search and
	 * the {@code TrackViewerPart} sends the corresponding bounding box on the event bus. 
	 */
	@Inject
	@Optional
	private void subscribeGeoSearchArea(final @UIEventTopic(TrackExplorerEventConstants.GEO_SEARCH_AREA) Bounds bounds,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		if(bounds != null) {
			try {
				new ProgressMonitorDialog(shell).run(true, true,
						new GeoSearchRunnable(bounds,
								searchService,
								trackService,
								(result) -> {
									this.sync.asyncExec(() -> {
										// Note: If the user has canceled the operation,
										// null will be returned as a result
										if(result != null) {
											broker.post(TrackExplorerEventConstants.GEO_SEARCH_RESULTS, result);
										}
									});
								}));
			} catch (InvocationTargetException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			} catch (InterruptedException e) {
				// This should never happen, but we have to deal with the exception
				MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
			}
		}
	}	

}
